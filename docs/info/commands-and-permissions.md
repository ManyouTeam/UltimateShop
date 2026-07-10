# ⌨️Commands & Permissions

## FAQ:&#x20;

### Does the /shop command has permission?

A: Use `/shop` can directly open a menu called `main`. This is a feature called **Auto Open**, and you can disable it at `config.yml` file with `menu.auto-open.enabled` option. If you only want some players to be exposed to this command, you can set `conditions` for the menu so that only players who meet the specified conditions can open the menu. For more info, please view [Menus](../menus/general-menus.md) page.

### Why it say condition not meet when open daily example menu?

A: That menu already set a condition and you can find it at `menus/daily-shop-example.yml`.

## ultimateshop.bypassprice

This permission can help you bypass price check and won't cost any price.

## ultimateshop.bypass.protection

This permission can help you bypass protection (anti grief) check when using sell stick.&#x20;

## /shop menu \<menuID>/\<shopID>

Open common menu or shop menu.&#x20;

Require `ultimateshop.menu` permission.

For console, should extra add `<player>` arg at the end of command, like `/shop menu test Player1`.

Support add `-b` at the end of the command to bypass menu open condition check. <mark style="color:red;">**(Premium version only)**</mark>

## /shop quickbuy \<shopID> \<productID> \[amount]

Quick purchase item.

Require `ultimateshop.quickbuy` permission.

For console, should extra add `<player>` arg at the end of command, like `/shop quickbuy test A Player1`.

## /shop quicksell \<shopID> \<productID> \[amount]

Same as `quickbuy`, just replace `quickbuy` to `quicksell`.

`amount` arg can be replaced to `*` symbol, then plugin will auto sell all the items you can sell. <mark style="color:red;">**(Premium version only)**</mark>

{% hint style="warning" %}
If the corresponding product is not displayed in the shop GUI, or if the player does not meet the conditions to open the corresponding shop menu, the item cannot be traded. If this is not desired, change the `settings.secret-shop-items` option in the shop configuration to `false`.
{% endhint %}

## /shop reload

Reload the plugin, some configs need you restart server.

Require `ultimateshop.reload` permission.

## /shop givesellstick \<itemID> \<playerID> \[amount] <mark style="color:red;">**(Premium version only)**</mark>

Give specifeid player specified amount (if not set, default to **1**) [sell stick](../features/sell-stick-premium.md).&#x20;

Require `ultimateshop.givesellstick` permission.

## /shop givesellchest \<itemID> \<playerID> \[amount] <mark style="color:red;">**(Premium version only)**</mark>

Give specifeid player specified amount (if not set, default to **1**) [sell chest](../features/sell-chest-premium.md).

Require `ultimateshop.givesellstick` permission.

## /shop setbuytimes/setselltimes \<shopID> \<productID> \<player>/global \[times]

Set player's specified product buy times to specified value.

Require `ultimateshop.setbuytimes` permission.

If didn't set `times` arg, we will think you are trying to reset the buy/sell times.

`productID` arg can be replaced to `*` symbol, then plugin will auto pick up all product in specified shop. <mark style="color:red;">**(Premium version only)**</mark>

`setselltimes` is similar to setbuytimes here.

{% hint style="info" %}
The global arg means set buy/sell times for `{buy-times-server}` or `{sell-times-server}` placeholer, not means set buy/sell times for all players.&#x20;

It is **impossible** to set all player data at once through commands in UltimateShop. Because assuming your server has hundreds of thousands of player data, without excellent performance optimization code, the server will immediately crash. You may see very few economy plugins or item plugins providing this feature, but they are selling it as a selling point. We have not declared ourselves providing this feature on any occasion, and this feature will not be added in the future because it is very time-consuming and not very meaningful. You can achieve similar functions through the **auto reset** function, and relevant information can be viewed on [this page](../shops/product-config-buy-sell-times-reset.md).
{% endhint %}

## /shop addbuytimes/addselltimes \<shopID> \<productID> \<player>/global \<times>

Add specified value to player's specified product buy times.

Require `ultimateshop.addtbuytimes` permission.

`productID` arg can be replaced to `*` symbol, then plugin will auto pick up all product in specified shop. <mark style="color:red;">**(Premium version only)**</mark>

`addselltimes` is similar to setbuytimes here.

## /shop sellall

Open sellall menu.

Require `ultimateshop.sellall` permission.

## /shop saveitem \<saveItemID> \<saveItemMethod> <a href="#mc-saveitem-less-than-itemid-greater-than" id="mc-saveitem-less-than-itemid-greater-than"></a>

Save your hold items. For more info, please view [Save Item](../features/saved-item-item-manager.md) page.

Require `ultimateshop.saveitem` permission.

## /shop generateitemformat

Generate hold item into Item Format at `plugins/UltimateShop` folder.

Require `ultimateshop.generateitemformat` permission.

## /shop getplaceholdervalue \<text> <mark style="color:red;">**(Premium version only)**</mark>

Parse input text to get placeholder value in it.

Require `ultimateshop.getplaceholdervalue` permission.

## /shop resetrandomplaceholder \<placeholderID> \[player] <mark style="color:red;">**(Premium version only)**</mark>

Reset random placeholder value.

The per player's random placeholder must enter the player name in the command parameters, while the none-per player's random placeholder cannot enter the player name in the command parameters, otherwise the plugin will prompt an error.

Require `ultimateshop.resetrandomplaceholder` permission.

## /shop setrandomplaceholder \<placeholderID> \[element] \[player] <mark style="color:red;">**(Premium version only)**</mark>

Set random placeholder value.&#x20;

Different from `resetrandomplaceholder`, `setrandomplaceholder` command won't reset refresh time and allow users pick specifeid element.

The per player's random placeholder must enter the player name in the command parameters, while the per player's random placeholder cannot enter the player name in the command parameters, otherwise the plugin will prompt an error.

~~Support add `-b` at the end of the command to bypass element exist check, which means you can set the custom element you'd like. For example, my random placeholder only have `A,B,C` total 3 elements, if I type **D** as element here, plugin will print error message, if you add `-b` suffix, then the placeholder value will be set to **D** and plugin never print error message, but it is not recommended.~~ (Removed in 3.12.0, now this command no longer check element exist)

Require `ultimateshop.setrandomplaceholder` permission.

## /shop search

Print the message that helps you know the hold item target product in shop.

Require `ultimateshop.search` permission.

## /shop sellhand

Sell the entire stack in your main hand only.

Require `ultimateshop.sellhand` permission.

## /shop sellallhand

Sell all hand item.

Require `ultimateshop.sellallhand` permission.

{% hint style="info" %}
sellhand = sell only the held stack.\
sellallhand = use the held item as selector, then sell matching items from inventory.
{% endhint %}

## /shop updategui \[player]

Update GUI for specified player. Only buttons will be updated, inventory title will not update.

Require `ultimateshop.updategui` permission.

## /shop updateguititle \[player] <mark style="color:red;">**(Premium version only)**</mark>

Update GUI title for specifed player. Only gui title will be updated, buttons will not update.

Require server enable title update feature, for more info, please view [this page](../menus/general-menus.md#title-update-premium).

Require `ultimateshop.updateguititle` permission.

## /shop editor <mark style="color:red;">**(Premium version only)**</mark>

Open in-game editor.

Require `ultimateshop.editor` permission.

## /shop searchgui \[searchMenuID] <mark style="color:red;">**(Premium version only)**</mark>

Open search GUI.

Require `ultimateshop.searchgui` permission.

## Custom Placeholder Commands <mark style="color:red;">**(Premium version only)**</mark>

For command list of custom placeholder feature, please view [this page](../placeholders/custom-placeholder-premium.md).
