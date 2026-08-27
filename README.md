# Coordinates

[![Release](https://img.shields.io/github/v/release/syu6noob/coordinates?label=release)](https://github.com/syu6noob/coordinates/releases/latest)
[![Paper](https://img.shields.io/badge/Paper-1.21.8-blue)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)

Paper サーバーでプレイヤーの現在座標とワールド内時刻を表示する、シンプルな Minecraft プラグインです。

座標は Action Bar または Scoreboard（サイドバー）に表示でき、プレイヤーごとの表示設定はサーバー再起動後も維持されます。

## 主な機能

- X / Y / Z 座標とワールド内時刻をリアルタイム表示
- Action Bar と Scoreboard の2種類の表示モード
- プレイヤーごとの表示モードを保存
- コマンドのタブ補完に対応
- 権限プラグインなしでも全プレイヤーが利用可能

## 必要環境

- Paper 1.21.8
- Java 21

## インストール

1. **[Coordinates.jar をダウンロード](https://github.com/syu6noob/coordinates/releases/latest/download/Coordinates.jar)**
2. ダウンロードした `Coordinates.jar` を Paper サーバーの `plugins` フォルダーへ配置します。
3. サーバーを再起動します。

## コマンド

| コマンド | 説明 |
| --- | --- |
| `/coords show actionbar` | 座標と時刻を Action Bar に表示します。 |
| `/coords show scoreboard` | 座標と時刻を Scoreboard に表示します。 |
| `/coords hide` | 座標と時刻を非表示にします。 |

権限ノードは `coordinates.use` です。デフォルトですべてのプレイヤーに付与されます。

## ソースからビルド

Java 21 をインストールし、リポジトリのルートで次を実行します。

```shell
./gradlew build
```

Windows の場合:

```powershell
.\gradlew.bat build
```

ビルドされたファイルは `build/libs/Coordinates.jar` に出力されます。

## リリース

`build.gradle` の `version` を更新して `v1.0.0` 形式のタグを push すると、GitHub Actions が JAR をビルドし、同じバージョンの GitHub Release に `Coordinates.jar` を添付します。
