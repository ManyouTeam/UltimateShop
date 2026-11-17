# 🌐多服务器同步 - 付费版

* Velocity 向后兼容 BungeeCord 的插件消息功能，因此 Velocity 用户应当也能使用此功能。
* 将 Spigot 服务器 `spigot.yml` 中的 `bungeecord` 设为 `true`。
* 将代理服务器 `config.yml` 中的 `ip_forward` 设为 `true`。
* 将 **UltimateShop** 的 `config.yml` 中的 `database.enabled` 设为 `true`，并配置数据库连接。
* 将 **UltimateShop** 的 `config.yml` 中的 `bungeecord-sync.enabled` 设为 `true`。
* 与多数插件一样，UltimateShop 不会验证 BungeeCord 下不同服务器之间的数据准确性和同步状态。如果出现同步问题，可尝试增大 `config.yml` 中的 `cache.load-delay`。对于同步要求极高的大型代理网络，建议选择其他插件。
