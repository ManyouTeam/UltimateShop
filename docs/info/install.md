# ⚙️Install

## Install

* Put the `.jar` file into your server's `plugins` folder.
* Stop your server and then restart it. <mark style="color:red;">Cannot load plugins in any other way while the server is starting</mark>.
* When updating plugins, please be sure to remove old versions.
* Previously, you used the free version, but now to upgrade to the paid version, you only need to install the paid version on the server and remove the free version. The configuration file of the plugin does not require any changes.
* When downgrading from **a new game version** to **an old version** on the server where the plugin is located, it is important to remove the `items` folder from the configuration file.
* When using the [Localized Item Name](../features/localized-item-name-premium.md) feature, it is important to note that when switching server game versions, the previously generated localized files need to be deleted.
* **UltimateShop** is just a shop plugin and does not provide custom economy functionality. If you need a custom economy as your server economy, please find a suitable economy plugin yourself. **Vault** is not an economy plugin, it is just a dependency plugin for many economy plugins. <mark style="color:red;">After installing Vault on the server, it is also necessary to install the economy plugins that support it</mark>.
* Default configs are just helping you understand the config framework, you need modify them that meet your needs.
