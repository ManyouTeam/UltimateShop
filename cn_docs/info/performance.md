# 🚀性能

UltimateShop 作为商店插件，通常不会给服务器带来过大的性能负担。出现性能问题时，常见原因是配置不合理或其他插件的影响，例如以下情况。

## 非必要时不要使用 ANY、ALL 价格/商品模式

与 `CLASSIC_ANY, CLASSIC_ALL` 不同，`ANY` 和 `ALL` 会为每次购买或出售单独计算。仅在设置动态价格或动态限制时使用这两种模式。

## MMOItems/MythicMobs 的物品生成开销

这两个插件生成物品比生成原版物品消耗更多服务器资源。如果物品是静态的，不包含动态属性或等级要求，可以尝试使用[保存物品](../features/saved-item-item-manager.md)功能代替实时生成。

{% hint style="info" %}
**以下**建议涉及的修改均在 `config.yml` 中完成。
{% endhint %}

## 关闭出售箱

如果不使用出售箱，可以关闭此功能以节省服务器资源。

```yaml
sell:
  sell-chest:
    enabled: false 
```

## 将 placeholder.click.enabled 设为 false

这可能有助于降低性能开销。

```yaml
  click:
    # If enabled, {buy-click} and {sell-stick} will display different value according to product status.
    # But, it will maybe make server lag if you are running big server and have many products in your shop.
    enabled: false
```

## 为菜单设置冷却

大型服务器推荐设置菜单冷却，防止玩家快速点击并反复打开商店菜单，避免因此造成卡顿。

```yaml
  # Recommend use when you are running big server, it will make player no longer quickly click
  # and reopen shop menu to make sure UltimateShop not lag your server.
  # In ticks.
  cooldown:
    click: 6 # Default value is -1, set value greater than 5 is recommended.
    reopen: 20 # Default value is -1, set value greater than 5 is recommended.
```

## 不要启用 GUI 自动更新

启用这些选项后，GUI 中的按钮能够自动更新，但服务器资源消耗会显著增加。

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

多数情况下，只需要在购买或出售次数重置后更新 GUI、展示最新数据。这种需求可以通过 `use-times.auto-reset-mode` 实现。

```yaml
  auto-reset-mode: true
```

如果追求极致性能，下面的选项也最好关闭。

```yaml
  auto-reset-mode: false
```

## 使用 BUKKIT 物品发放方式

此方式性能最好，但物品合并堆叠可能存在问题。此外，玩家物品栏已满时，多余物品只能掉落到地上，插件无法阻止玩家继续交易。

```yaml
give-item:
  # Support value: BUKKIT, SMART
  # SMART will cost more server performance but will follow the vanilla max stack to give player item, also support check full.
  give-method: BUKKIT
  # Only support SMART give method.
  check-full: false
```

## 使用 Bukkit 出售物品匹配方式

这种方式可以节省部分资源，但待售物品必须与商店物品完全相同，包括铁砧修复惩罚、附魔等细节。因此，带耐久度或可附魔的物品通常会因细微差异而无法出售。

```yaml
sell:
  # Support Value: Bukkit or ItemFormat.
  # For each product, you can add match-item section to make custom sell match method, for more info, please view Wiki.
  sell-method: Bukkit
```
