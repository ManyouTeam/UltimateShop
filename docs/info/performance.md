# 🚀Performance

UltimateShop is just a shop plugin that usually does not have too much impact on the server. It is usually a performance issue caused by your unreasonable settings or other plugins, such as:

## Do not use ANY, ALL price/product mode unless necessary.

Different from `CLASSIC_ANY, CLASSIC_ALL` mode, `ANY` and `ALL` mode, the calculation for each buy or sell is independent. Only use the 2 mode when you set dynamic price or limit.

## MMOItems/MythicMobs Item generation performance.

Both plugins are spend much server resource to generate item than vanilla items. If possible and your item is static item with no things like dynamic stat or level requirement, you can try use [Save Item](../features/saved-item-item-manager.md) instead.

{% hint style="info" %}
For suggestions **below**:\
The change you need made are all exist in `config.yml` file.
{% endhint %}

## Disable sell chest.

If you are not using sell chest feature, you can disable this feature to save serve performance.

```yaml
sell:
  sell-chest:
    enabled: false 
```

## Set placeholder.click.enabled option to false.

This maybe save server performance.

```yaml
  click:
    # If enabled, {buy-click} and {sell-stick} will display different value according to product status.
    # But, it will maybe make server lag if you are running big server and have many products in your shop.
    enabled: false
```

## Set cooldown for menu system.

Recommend use when you are running big server, it will make player no longer quickly click and reopen shop menu to make sure UltimateShop not lag your server.

```yaml
  # Recommend use when you are running big server, it will make player no longer quickly click
  # and reopen shop menu to make sure UltimateShop not lag your server.
  # In ticks.
  cooldown:
    click: 6 # Default value is -1, set value greater than 5 is recommended.
    reopen: 20 # Default value is -1, set value greater than 5 is recommended.
```

## Do not enable auto update GUI feature.

Enabling these options will significantly increase server performance consumption, but buttons within the GUI will be able to automatically update.

```yaml
  menu-update:
    # Whether menu will refresh every buttons every 1 second.
    # This will refresh placeholder that displayed in display item lore.
    # But maybe lead to server lag if you have much online players, and they are all opening shop GUI.
    circle-update: false
    # Whether menu will refresh every buttons when click any of them.
    # This will refresh placeholder that displayed in display item lore.
    # But maybe lead to server lag if you have much online players, and they are all opening shop GUI.
    click-update: false
   # PREMIUM version only, if enabled, can update dynamic value used in GUI title.
  title-update:
    # Whether gui title will refresh every buttons every 1 second.
    # This will refresh placeholder that displayed in menu title.
    circle-update: false
    # Whether gui title will refresh every buttons when click any of them.
    # This will refresh placeholder that displayed in menu title.
    click-update: true
    resend-items-pack: false
```

In most cases, you just want the GUI to update and display the latest data when we reset buy times or sell times, so you can achieve this idea through `use-times.auto-reset-mode` option.

```yaml
  auto-reset-mode: true
```

If you pursue ultimate plugin performance, it's best not to turn on this option either.

```yaml
  auto-reset-mode: false
```

## Use BUKKIT item give method.

This item give method has the best performance, but the cost is that there may be some issues with stacking items, and when the player's inventory is full, we can only throw the excess items on the ground instead of preventing the player from further trading.

```yaml
give-item:
  # Support value: BUKKIT, SMART
  # SMART will cost more server performance but will follow the vanilla max stack to give player item, also support check full.
  give-method: BUKKIT
  # Only support SMART give method.
  check-full: false
```

## Use Bukkit sell item match method.

This can save some server performance, but the drawback is that the items to be sold must be identical to the items in the shop, with no differences, even including anvil cost, enchants, etc. (Items with durability and enchantable items basically cannot be sold to the shop because of this)

```yaml
sell:
  # Support Value: Bukkit or ItemFormat.
  # For each product, you can add match-item section to make custom sell match method, for more info, please view Wiki.
  sell-method: Bukkit
```
