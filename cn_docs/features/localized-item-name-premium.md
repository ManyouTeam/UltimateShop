# 🌍物品名称本地化 - 付费版

需要付费版和 Minecraft 1.16+。

需要在服务器端解析物品名称的功能会自动下载原版语言文件。将 `config-files.language` 设为服务器语言，并启用 `display-item.auto-translate-item-name`。Paper 会保留客户端解析的 `<lang:...>` 名称，使每位玩家看到各自 Minecraft 语言的名称；这种方式无需下载服务器语言文件。

```yaml
config-files:
  language: zh_CN
display-item:
  auto-translate-item-name: true
```

下载在后台进行。所需语言尚未准备好时，会使用原有的后备名称。缓存位于 `plugins/UltimateShop/minecraft-resources/<version>/languages/`。未知的原版语言代码会回退到英语，自定义物品名称始终优先。

Minecraft 客户端 JAR、语言资源、材质映射和自动 Ore 图标共用同一版本目录。不同功能以及重启后会复用已下载内容；升级服务器后会自动使用新的版本目录。Ore 使用客户端默认字体和 5.1.0 的宽度估算，不会将原版字体或语言宽度补偿表打包。

旧的 `minecraft-locate-file` 与 `minecraft-item-material-file` 下载设置已不再使用，也不需要设置 `enabled`、`generate-new-one` 或资源文件名。网络失败会被记录，并在冷却结束后的下一次请求时重试，不会阻塞游戏线程。
