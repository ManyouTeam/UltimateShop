# 🌐Multi Server Sync - Premium

* Due to the backward compatibility of Velocity with BungeCord's Plugin Message feature, Velocity users should also be able to use this feature.
* Change Spigot server's `spigot.yml` file's `bungeecord` option value to `true`.
* Change proxy server's `config.yml` file's `ip_forward` option value to `true`.
* Change **UltimateShop** plugin's `config.yml` file's `database.enabled` option value to `true`, then setup your database settings.
* Change **UltimateShop** plugin's `config.yml` file's `bungeecord-sync.enabled` option value to `true`.
* Like most plugins, UltimateShop does not verify the accuracy and synchronization of data across different servers in BungeCord. If your BungeCord is experiencing data synchronization issues, you can try increasing the `cache.load-delay` value in the `config.yml` file. If your server is a large proxy server with very high synchronization requirements, we recommend that you choose other plugins.
