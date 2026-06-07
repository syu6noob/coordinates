package com.github.syu6noob.coordinates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Coordinates extends JavaPlugin {

    private final MiniMessage mm = MiniMessage.miniMessage();

    // プレイヤーごとの表示モード
    private final Map<UUID, String> modes = new HashMap<>();

    private File playerDataFile;
    private FileConfiguration playerData;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPlayerData();

        // 再起動後も状態を復元
        for (String key : playerData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                modes.put(uuid, playerData.getString(key, "hidden"));
            } catch (IllegalArgumentException ignored) {}
        }

        // 0.5秒ごとに更新
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAllPlayers();
            }
        }.runTaskTimer(this, 0L, 10L);

        getLogger().info("Coordinates plugin enabled");
    }

    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("Coordinates plugin disabled");
    }

    // ------------------------
    // コマンド処理
    // ------------------------
    private void sendHelp(Player player) {
        String msg = "<green>[Coordinates]</green>\n"
                + "<yellow>/coords show <actionbar | scoreboard></yellow>\n"
                + "  - show your current coordinates\n"
                + "<yellow>/coords hide</yellow>\n"
                + "  - hide coordinates";

        player.sendMessage(mm.deserialize(msg));
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender,
                             org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize("<green>[Coordinates]</green> This command can only be used by players."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "show" -> {
                if (args.length < 2) {
                    player.sendMessage(mm.deserialize("<green>[Coordinates]</green> Please specify a mode: <yellow>actionbar</yellow> or <yellow>scoreboard</yellow>."));
                    return true;
                }

                String mode = args[1].toLowerCase();
                if (!mode.equals("actionbar") && !mode.equals("scoreboard")) {
                    player.sendMessage(mm.deserialize("<green>[Coordinates]</green> Invalid mode. Choose: <yellow>actionbar</yellow> or <yellow>scoreboard</yellow>."));
                    return true;
                }

                modes.put(player.getUniqueId(), mode);
                player.sendMessage(mm.deserialize("<green>[Coordinates]</green> Coordinates and time will now be shown in <yellow>" + mode + "</yellow>."));
                savePlayerData();
            }
            case "hide" -> {
                modes.put(player.getUniqueId(), "hidden");
                player.sendMessage(mm.deserialize("<green>[Coordinates]</green> Coordinates and time display hidden."));
                savePlayerData();
            }
            default -> sendHelp(player);
        }
        return true;
    }

    // ------------------------
    // プレイヤー更新
    // ------------------------
    private void updateAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String mode = modes.getOrDefault(player.getUniqueId(), "hidden");
            if (mode.equals("hidden")) continue;

            String timeStr = formatWorldTime(player);

            switch (mode) {
                case "actionbar" -> {
                    String coordsMini = formatCoordsMini(player);
                    sendActionBar(player, coordsMini, timeStr);
                }
                case "scoreboard" -> {
                    String coords = formatCoords(player);
                    updateScoreboard(player, coords, timeStr);
                }
            }
        }
    }

    // ------------------------
    // 表示形式
    // ------------------------

    private String formatCoords(Player player) {
        return ChatColor.YELLOW + "X:" + ChatColor.WHITE + player.getLocation().getBlockX() + " "
                + ChatColor.YELLOW + "Y:" + ChatColor.WHITE + player.getLocation().getBlockY() + " "
                + ChatColor.YELLOW + "Z:" + ChatColor.WHITE + player.getLocation().getBlockZ();
    }

    private String formatCoordsMini(Player player) {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        return "<yellow>X:</yellow> <white>" + x + "</white> "
                + "<yellow>Y:</yellow> <white>" + y + "</white> "
                + "<yellow>Z:</yellow> <white>" + z + "</white>";
    }

    private String formatWorldTime(Player player) {
        long ticks = player.getWorld().getTime() % 24000;
        long hours = (ticks / 1000 + 6) % 24;
        long minutes = (ticks % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    // ActionBar
    private void sendActionBar(Player player, String coords, String time) {
        Component message = mm.deserialize("<green>[Coordinates]</green> " + coords + " | <yellow>Time:</yellow> <white>" + time + "</white>");
        player.sendActionBar(message);
    }

    // Scoreboard
    private void updateScoreboard(Player player, String coords, String time) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("coords", "dummy", ChatColor.GREEN + "[Coordinates]");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 1行ずつ表示
        String[] parts = coords.split(" ");
        obj.getScore(parts[0]).setScore(3); // X:
        obj.getScore(parts[1]).setScore(2); // Y:
        obj.getScore(parts[2]).setScore(1); // Z:
        obj.getScore(ChatColor.GOLD + "Time: " + ChatColor.WHITE + time).setScore(0);

        player.setScoreboard(board);
    }

    // ------------------------
    // プレイヤーデータ保存
    // ------------------------
    private void loadPlayerData() {
        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            saveResource("players.yml", false);
        }

        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    private void savePlayerData() {
        if (playerData == null || playerDataFile == null) return;
        for (Map.Entry<UUID, String> entry : modes.entrySet()) {
            playerData.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save player data: " + e.getMessage());
        }
    }
}
