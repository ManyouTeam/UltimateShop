# ⌨️命令与权限

## 常见问题&#x20;

### /shop 命令有权限要求吗？

答：`/shop` 会直接打开名为 `main` 的菜单，这是**自动打开**功能，可通过 `config.yml` 中的 `menu.auto-open.enabled` 关闭。如果只允许部分玩家使用，请为菜单设置 `conditions`，仅让满足条件的玩家打开。详见[菜单](../menus/general-menus.md)。

### 为什么打开每日商店示例菜单时提示条件不满足？

答：该菜单已配置条件，请查看 `menus/shop_menu_templates/daily-shop-menu.yml`。

## ultimateshop.bypassprice

此权限用于跳过价格检查，交易时无需支付任何费用。

## ultimateshop.bypass.protection

此权限用于在使用出售棒时跳过区域保护（防破坏）检查。&#x20;

## /shop menu \<menuID>/\<shopID>

打开通用菜单或商店菜单。&#x20;

需要权限：`ultimateshop.menu`。

由控制台执行时，需要在命令末尾增加 `<player>` 参数，例如 `/shop menu test Player1`。

可以在命令末尾添加 `-b`，跳过菜单打开条件检查。<mark style="color:red;">**（仅付费版）**</mark>

## /shop quickbuy \<shopID> \<productID> \[amount]

快速购买物品。

需要权限：`ultimateshop.quickbuy`。

由控制台执行时，需要在末尾增加 `<player>` 参数，例如 `/shop quickbuy test A Player1`。

## /shop quicksell \<shopID> \<productID> \[amount]

用法与 `quickbuy` 相同，将 `quickbuy` 替换为 `quicksell` 即可。

将 `amount` 参数替换为 `*`，插件会自动出售所有可出售的对应物品。<mark style="color:red;">**（仅付费版）**</mark>

{% hint style="warning" %}
如果商品没有显示在商店 GUI 中，或玩家不满足对应商店菜单的打开条件，则无法交易。若不希望受到这一限制，请将商店配置中的 `settings.secret-shop-items` 设为 `false`。
{% endhint %}

## /shop reload

重载插件。部分配置仍需重启服务器才能生效。

需要权限：`ultimateshop.reload`。

## /shop givesellstick \<itemID> \<playerID> \[amount] <mark style="color:red;">**（仅付费版）**</mark>

向指定玩家发放指定数量的[出售棒](../features/sell-stick-premium.md)。数量未设置时默认为 **1**。&#x20;

需要权限：`ultimateshop.givesellstick`。

## /shop givesellchest \<itemID> \<playerID> \[amount] <mark style="color:red;">**（仅付费版）**</mark>

向指定玩家发放指定数量的[出售箱](../features/sell-chest-premium.md)。数量未设置时默认为 **1**。

需要权限：`ultimateshop.givesellstick`。

## /shop setbuytimes/setselltimes \<shopID> \<productID> \<player>/global \[times]

将指定玩家对指定商品的购买次数设为给定数值。

需要权限：`ultimateshop.setbuytimes`。

省略 `times` 参数时，表示重置购买/出售次数。

将 `productID` 替换为 `*`，可选择指定商店中的所有商品。<mark style="color:red;">**（仅付费版）**</mark>

`setselltimes` 的用法与 `setbuytimes` 类似，用于出售次数。

{% hint style="info" %}
`global` 表示设置 `{buy-times-server}` 或 `{sell-times-server}` 对应的全服购买/出售次数，并不表示修改所有玩家各自的次数。&#x20;

UltimateShop **不支持**通过命令一次性修改所有玩家数据。如果服务器存在数十万份玩家数据，缺少充分优化时，这类操作会直接导致服务器崩溃。少数经济或物品插件可能提供此功能并将其作为卖点，但我们从未承诺提供，未来也不会加入，因为实现耗时且实际意义有限。可以使用**自动重置**功能实现类似效果，详见[此页面](../shops/product-config-buy-sell-times-reset.md)。
{% endhint %}

## /shop addbuytimes/addselltimes \<shopID> \<productID> \<player>/global \<times>

为指定玩家对指定商品的购买次数增加给定数值。

需要权限：`ultimateshop.addtbuytimes`。

将 `productID` 替换为 `*`，可选择指定商店中的所有商品。<mark style="color:red;">**（仅付费版）**</mark>

`addselltimes` 对应出售次数，用法类似。

## /shop sellall

打开全部出售菜单。

需要权限：`ultimateshop.sellall`。

## /shop saveitem \<saveItemID> \<saveItemMethod> <a href="#mc-saveitem-less-than-itemid-greater-than" id="mc-saveitem-less-than-itemid-greater-than"></a>

保存手持物品，详见[保存物品](../features/saved-item-item-manager.md)。

需要权限：`ultimateshop.saveitem`。

## /shop givesaveitem <itemID> [player] [amount]

发放 `plugins/UltimateShop/items/` 中保存的物品，ID 为不含 `.yml` 的文件名。游戏内执行 `/shop givesaveitem component-test` 会发给自己；`/shop givesaveitem component-test PlayerName 1` 会发给指定在线玩家。控制台必须指定玩家。

需要权限：`ultimateshop.givesaveitem`。关于 ItemFormat 文件的创建和重载，见[保存物品](../features/saved-item-item-manager.md)。

## /shop generateitemformat

将主手物品导出到 `plugins/UltimateShop/generated-item-format.yml`，每次执行都会覆盖该文件。在 Paper 1.21.5+ 和 UltimateShop 付费版中，`debuild-item-method: COMPONENT` 会将受支持的属性保存到 `component` 下。保存和重建流程见[测试 Paper 组件](../format/itemformat-tm/component-testing.md)。

需要权限：`ultimateshop.generateitemformat`。

## /shop getplaceholdervalue \<text> <mark style="color:red;">**（仅付费版）**</mark>

解析输入文本，获取其中占位符的值。

需要权限：`ultimateshop.getplaceholdervalue`。

## /shop resetrandomplaceholder \<placeholderID> \[player] <mark style="color:red;">**（仅付费版）**</mark>

重置随机占位符的值。

玩家独立的随机占位符必须在参数中指定玩家名称；非玩家独立的随机占位符则不能指定玩家名称，否则插件会报错。

需要权限：`ultimateshop.resetrandomplaceholder`。

## /shop setrandomplaceholder \<placeholderID> \[element] \[player] <mark style="color:red;">**（仅付费版）**</mark>

设置随机占位符的值。&#x20;

与 `resetrandomplaceholder` 不同，`setrandomplaceholder` 不会重置刷新时间，并且允许选择指定元素。

玩家独立的随机占位符必须指定玩家名称；非玩家独立的随机占位符不能指定，否则插件会报错。

~~可在末尾添加 `-b` 跳过元素存在性检查，以设置自定义元素。例如，占位符只有 `A,B,C` 三个元素，直接指定 **D** 会报错；添加 `-b` 后即可设为 **D**，但不推荐这样使用。~~（此参数已于 3.12.0 移除；现在该命令不再检查元素是否存在。）

需要权限：`ultimateshop.setrandomplaceholder`。

## /shop search

输出提示，帮助查找手持物品对应的商店商品。

需要权限：`ultimateshop.search`。

## /shop sellhand

仅出售主手中的整组物品。

需要权限：`ultimateshop.sellhand`。

## /shop sellallhand

出售物品栏内所有与手持物品匹配的物品。

需要权限：`ultimateshop.sellallhand`。

{% hint style="info" %}
`sellhand`：仅出售手持的这一组物品。
\
`sellallhand`：以手持物品作为匹配样本，出售物品栏中的所有匹配物品。
{% endhint %}

## /shop updategui \[player]

更新指定玩家的 GUI，仅更新按钮，不更新物品栏标题。

需要权限：`ultimateshop.updategui`。

## /shop updateguititle \[player] <mark style="color:red;">**（仅付费版）**</mark>

更新指定玩家的 GUI 标题，仅更新标题，不更新按钮。

服务器需启用标题更新功能，详见[此页面](../menus/general-menus.md#title-update-premium)。

需要权限：`ultimateshop.updateguititle`。

## /shop editor <mark style="color:red;">**（仅付费版）**</mark>

打开内置游戏内编辑器。在 `config.yml` 中设置 `editor.enabled: true` 并重载后启用。每次使用前都应备份插件配置，无需安装独立的编辑器附属插件。

使用 `/shop editor <scope> <id>` 可直接打开文件。支持的范围为 `shop`、`menu`、`shop_menu_template`、`buy_more_menu`、`favourite_menu`、`search_menu` 和 `item_sell_menu`。ID 不包含 `.yml`；专用菜单使用 `menus/` 下对应目录。UI 预设和保存方式见[游戏内编辑器](../features/in-game-editor-premium.md)。

需要权限：`ultimateshop.editor`。

## 自定义占位符命令 <mark style="color:red;">**（仅付费版）**</mark>

自定义占位符功能的命令列表见[此页面](../placeholders/custom-placeholder-premium.md)。
