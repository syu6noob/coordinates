package com.github.syu6noob.coordinates;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoordinatesTabCompleter implements TabCompleter {

    private final List<String> modes = Arrays.asList("actionbar", "scoreboard");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) return completions;

        if (args.length == 1) {
            // /coords <tab>
            List<String> options = Arrays.asList("show", "hide");
            for (String option : options) {
                if (option.startsWith(args[0].toLowerCase())) completions.add(option);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("show")) {
            // /coords show <tab>
            for (String mode : modes) {
                if (mode.startsWith(args[1].toLowerCase())) completions.add(mode);
            }
        }

        return completions;
    }
}
