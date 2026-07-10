# 🛒Products

Here is an example of 2 product configs:

```yaml
items:
  A:
    display-name: "Apple"
    price-mode: ANY
    product-mode: ALL
    products:
      1:
        material: APPLE
        amount: 1
      2: 
        material: BREAD
        amount: 5
        conditions:
          1:
            type: permission
            permission: 'group.vip'
        give-actions:
          1:
            type: message
            message: 'Wow, seems that you are a VIP player, so we bonud give you 5 breads!'
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 200
        placeholder: '{amount} Coins'
        start-apply: 0
      2:
        economy-plugin: PlayerPoints
        amount: 10
        placeholder: '{amount} Points'
        start-apply: 5
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 50
        placeholder: '{amount} Coins'
      2:
        economy-plugin: PlayerPoints
        amount: 1
        start-apply: 5
        placeholder: '{amount} Points'
        give-actions:
          1: 
            type: message: 
            message: 'Wow, seems that you have already sell 5 apples!'
    buy-actions:
      1:
        type: player_command
        command: 'say %player_name% purchased an Apple!'
      2:
        type: announcement
        message: '&7%player_name% purchased an Apple!'
  B:
    display-item:
      material: BREAD
      name: '&cSuper Bread'
    display-name: "Bread"
    add-lore:
      - '@a&ePurchase: {buy-price}'
      - '@b&eSell: {sell-price}'
      - '&eDrop to buy, right to sell'
    click-event:
      buy: 'DROP'
      sell: 'RIGHT'
    bedrock:
      hide: false
      icon: 'url;;https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/1.20/bread.png'
    buy-more: true
    buy-more-menu:
      menu: buy-more-2
      max-amount: 16
    price-mode: ANY
    product-mode: ALL
    products:
      1:
        material: BREAD
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 200
        placeholder: '{amount} Coins'
        start-apply: 0
      2:
        economy-plugin: PlayerPoints
        amount: 10
        placeholder: '{amount} Points'
        start-apply: 5
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 50
        placeholder: '{amount} Coins'
      2:
        economy-plugin: PlayerPoints
        amount: 1
        start-apply: 5
        placeholder: '{amount} Points'
    buy-limits:
      global: 100
      default: 10
      test-condition: 20
    buy-limits-conditions:
      test-condition:
        1:
          type: permission
          permission: 'test.permission'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '00:00:00'
  C:
    display-item: 
      material: DIAMOND
    as-sub-button: A
```

## Model

```
Shop file
└─ Product entry
   ├─ display item and menu behavior
   ├─ products       → what changes hands as the product
   ├─ buy-prices     → what the player pays when buying
   ├─ sell-prices    → what the player receives when selling
   ├─ buy-conditions/sell-conditions     → who may use or see it
   ├─ buy-actions/sell-actions           → what else happens
   └─ buy-limits/sell-limits             → how often it may be used
```

## General Options

{% hint style="info" %}
Click [here](shops.md) to see the detalied example of those general options.
{% endhint %}

* display-item: Product display item in shop menu, it can be different from the real item player will obtain after purchase. For virtual products, you must set `display-item` here, otherwise they can not be displayed in GUI. For real item products, you must enable `auto-set-first-product` option under `display-item` section in `config.yml` file to allow you remove this section, after enable, if `display-item` is not set, the first real item product will be used as display item. This section use [Item format](../format/itemformat-tm/). **Optional (if not set, will use first products)**
  * display-item.modify-lore: Whether we will modify display item lore to add info about price, limits and so on. You can set add lore info at `config.yml` file or `add-lore` option in product config, for more info about it, please view [Display Item Add Lore](../menus/display-item-add-lore.md) page. **Optional (default to true)**
* display-name: Set product display name in `{product}` placeholder and buy more menu display item. **Optional. (if not set, we will use the display item's name as product display name)**
* add-lore: Set special [display item add lore](../menus/display-item-add-lore.md) for this product, if not set, we will use default value set in `config.yml`. **Optional.**
* click-event: Set special click event for this product, if not set, we will use default value set in `config.yml`. Don't forgot also modify `add-lore` option to correspond to the modified click event. **Optional.**

```yaml
    add-lore:
      - '@a&ePurchase: {buy-price}'
      - '@b&eSell: {sell-price}'
      - '&eDrop to buy, right to sell' # Modified add lore to to correspond to the modified click event
    click-event:
      buy: 'DROP'
      sell: 'RIGHT'
```

* bedrock: View [this page](../menus/bedrock-menus-premium.md).
* buy-more: Set whether this product can open buy more menu. **Optional. (default to true)**
* buy-more-menu: Set up separate buy more menu settings for the product. **Optional. Require 2.2.10+ version. (if not set, will use default value set in `config.yml` file)**

```yaml
    buy-more: true
    sell-all: true
    hide-message: false
    buy-more-menu:
      menu: buy-more-2
      max-amount: 16
```

* sell-all: Set whether this product can use sell all feature. **Optional, default to true. (Added in 3.9.3)**
* shared-use-times: Please view [this page](shared-product-data-premium.md).
*   hide-message: Whether we hide the messages that will send after player buy or sell this product. **Optional, default to false. (Added in 4.2.11)**

    <div data-gb-custom-block data-tag="hint" data-style="warning" class="hint hint-warning"><p>By default, only hide success buy or sell message, for fail message like limit reached or not enough money, you have 2 methods to hide them:</p><ul><li>Try <strong>also</strong> set <code>placeholder.click.enabled</code> option value to <code>true</code> in your <code>config.yml</code> file to hide them. This will display fail status at display item lore and will cost extra performance.</li><li>Try <strong>also</strong> set <code>force-display-fail-message</code> option value to <code>true</code> in your <code>config.yml</code> file. This way is recommended. (4.2.11+)</li></ul></div>
* buy-prices/sell-prices/products: Please view [this page](products-config-single-thing/).
* price-mode/product-mode: Support `ANY, ALL, CLASSIC_ANY, CLASSIC_ALL`.  Plase see [Mode](products.md#mode) to know more. **Required.**
* buy-actions: The action will run after buy this product, use [Action Forma](../format/action-format.md)[t](../format/action-format.md) here. **Optional.**&#x20;
* sell-actions: The action will run after sell this product,  use [Action Format](../format/action-format.md) here. **Optional.**
* fail-actions: The action will run if we fail to buy or sell this product,  use [Action Format](../format/action-format.md) here. **Optional.**&#x20;
* buy-conditions: The condition player need to meet to buy this product, use [Condition Format](../format/condition-format.md) here. **Optional**.
* sell-conditions: The condition player need to meet to sell this product, use [Condition Format](../format/condition-format.md) here. **Optional**.
* buy-limits: Set the maximum times of buy/sell times. **Optional. If not set, product can be purchased with unlimited times.**
  * buy-limits.global: Global limit. **Optional.**
  * buy-limits.default: If player don't meet any condition set below, they will use this limit. **Required if you have set buy-limits.**
* buy-limits.\<Condition ID>: Players who meet this condition will use this limit. Condition format can be found at [Conditions](../format/condition-format.md). For example:

```yaml
buy-limits:
  default: 10
  vip: 20
buy-limits-conditions:
  vip: 
    1:
      type: permission
      permission: 'test.permission'
```

* sell-limits: Same as buy-limits, but use for sell.

{% hint style="info" %}
For how to reset limits, please view [this page](product-config-buy-sell-times-reset.md).
{% endhint %}

## Single Thing Options

This section of the configuration includes the following options:

* buy-prices
* sell-prices
* products

The introduction of these options is on a separate page, please [click here](products-config-single-thing/) to view.

## Buy/Sell Times Reset Options

This section of the configuration includes the following options:

* buy-times-reset-mode
* buy-times-reset-time
* buy-times-reset-time-format
* buy-times-reset-value
* buy-times-max-value
* sell-times-reset-mode&#x20;
* sell-times-reset-time
* sell-times-reset-time-format
* sell-times-reset-value
* sell-times-max-value

The introduction of these options is on a separate page, please [click here](product-config-buy-sell-times-reset.md) to view.

## Dynamic Value

You can set placeholders (including PlaceholderAPI) and [Math Calculate Format](../format/math-calculate-format.md) in `buy-prices`, `sell-prices` section's `amount` option and `buy-limits`, `sell-limits` section's value in product configs.

By default, dynamic values are calculated in real-time and are not refreshed periodically. However, players will not see real-time values in the GUI. We will only refresh the dynamic values displayed in the GUI after the player opens the UI or clicks the item each time. For example, if you set a dynamic value in the purchase price and the dynamic value is changed when the player opens the GUI, although the player will not notice this change in the GUI display item, the plugin will calculate the price based on the updated dynamic value at the final purchase. This is a decision made to balance server performance and save costs. If you don't want this and want to dynamic value display refresh immediately, please [click here](../menus/general-menus.md#all-buttons-update).

Available built-in placeholder list as below, for more info about them, please view [Built-In Placeholders](../placeholders/built-in-placeholders.md) page.

* {buy-times-player}
* {buy-times-server}
* {buy-total-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {buy-total-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

{% hint style="info" %}
Difference between **times** placeholder and **total** placeholder:

* **times** placeholder will be reset to the value you set after each reset.
* **total** placeholder will keep data after reset, and will accumulate previous times. Use command to set buy/sell times or other way will still effect total placeholder. This placeholder will auto reset when reaching the upper limit of int type data.
{% endhint %}

* {sell-times-player}
* {sell-times-server}
* {sell-total-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {sell-total-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {last-buy-player} <mark style="color:red;">**- PREMIUM**</mark>

Display the time interval between the last purchase of this item by a single player, in seconds. If the player has not purchased this item or the buy time has been reset, it will return `0`.

* {last-buy-server} <mark style="color:red;">**- PREMIUM**</mark>

Display the time interval between the last purchase of this item by global server, in seconds. If no one has not purchased this item before or the buy time has been reset, it will return `0`.

* {last-sell-player} <mark style="color:red;">**- PREMIUM**</mark>

Display the time interval between the last sell of this item by a single player, in seconds. If the player has not sold this item or the sell time has been reset, it will return `0`.

* {last-sell-server} <mark style="color:red;">**- PREMIUM**</mark>\
  Display the time interval between the last sell of this item by global server, in seconds. If no one sold this item before or the sell time has been reset, it will return `0`.
* {last-buy-reset-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

Display the time interval between the last buy reset or first buy time after reset (depends on the reset mode you selected, for more info, please view [this page](https://ultimateshop.superiormc.cn/shops/product-config-buy-sell-times-reset)) of this item by a single player, in seconds. If the player has not purchased this item or the buy time has been reset, it will return last buy time.

* {last-buy-reset-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

Display the time interval between the last buy reset or first buy time after reset (depends on the reset mode you selected, for more info, please view [this page](https://ultimateshop.superiormc.cn/shops/product-config-buy-sell-times-reset)) of this item by global server, in seconds. If the player has not purchased this item or the buy time has been reset, it will return last buy time.

* {last-sell-reset-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

Display the time interval between the last sell reset or first sell time after reset (depends on the reset mode you selected, for more info, please view [this page](https://ultimateshop.superiormc.cn/shops/product-config-buy-sell-times-reset)) of this item by a single player, in seconds. If the player has not sold this item or the sell time has been reset, it will return last sell time.

* {last-sell-reset-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

Display the time interval between the last sell reset or first sell time after reset (depends on the reset mode you selected, for more info, please view [this page](https://ultimateshop.superiormc.cn/shops/product-config-buy-sell-times-reset)) of this item by global server, in seconds. If the player has not sold this item or the sell time has been reset, it will return last sell time.

Also in `buy-prices` and `sell-prices` section, you can set new 2 options:

* max-amount: Price max amount, useful for dynamic prices. **Optional.**
* min-amount: Price min amount, useful for dynamic prices. **Optional.**

When you use dynamic value in `amount` option, you can use `min-amount` and `max-amount` option to limit it's min value and max value. Useful for dynamic price.

Please carefully note that if you want to use our PlaceholderAPI extansion's placeholder, you have to use our new format, for example:

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # We use the new format without { and } symbol.
        placeholder: '{amount}$'
        start-apply: 0
```

Additionally, you need to set `menu.shop.click-update` to `true` if the related to product is also in the menu you opened. Otherwise this price won't auto update after you sell B product.

## Sub Buttons <mark style="color:red;">- Premium</mark>

Sometimes, you want to display same product in different menus, or you want to make 2 or more buttons for same product. Well, `as-sub-button` option can help you. Just set another product ID here, then this button will also be considered as the product you set here.

* display-item: Supports set different display item for sub buttons.
* as-sub-button: Type `Product ID` or `ShopID;;ProductID` here.

The example config of **Sub Buttons** can be found at [Shops](shops.md) page, please check out the `C` section under `items` in the head example.

{% hint style="warning" %}
If the corresponding product of the sub button is not displayed in the shop GUI, or if the player does not meet the conditions to open the corresponding shop menu, the item cannot be traded. If this is not desired, change the `settings.secret-shop-items` option in the shop configuration to `false`.
{% endhint %}

## Mode

### ALL example: coins and diamonds

```yaml
price-mode: ALL
buy-prices:
  coins:
    economy-plugin: Vault
    amount: 500
    placeholder: '&6$500'
  diamonds:
    material: DIAMOND
    amount: 8
    placeholder: '&b8 diamonds'
```

The player must provide both entries.

### ANY example: money or points

```yaml
price-mode: ANY
buy-prices:
  money:
    economy-plugin: Vault
    amount: 1000
    placeholder: '&6$1,000'
  points:
    economy-plugin: PlayerPoints
    amount: 50
    placeholder: '&e50 points'
```

Only one applicable alternative is used. Conditions can control which alternative is available.

### When not to use CLASSIC modes

Use `ALL` or `ANY` when entries rely on staged application such as use dynamic price, use `start-apply`, `end-apply`, or complex per-entry conditions. Use a `CLASSIC_` mode only when the configuration follows the simple immediate-application model.

<table><thead><tr><th width="118">Mode</th><th width="179">ANY</th><th>ALL</th><th>CLASSIC_ANY</th><th>CLASSIC_ALL</th></tr></thead><tbody><tr><td>Product Give</td><td>Give random products that meet conditions.</td><td>Give all products.</td><td>Same as ANY.</td><td>Same as ALL.</td></tr><tr><td>Product /Price Take</td><td>First product/price that we found player meet condition and have enough amount.</td><td>Players must have all products/prices that meet conditions to sell.</td><td>Same as ANY.</td><td>Same as ALL.</td></tr><tr><td>Price Give (means sell)</td><td>First prices meet the condition requirements.</td><td>All prices will be given.</td><td>Same as ANY.</td><td>Same as ALL.</td></tr><tr><td>Price Support</td><td>Support dynamic price &#x26; <code>apply</code> option.</td><td>Same as ALL.</td><td>Price must be same at  everytime.</td><td>Same as CLASSIC_ALL.</td></tr><tr><td>Support Sell All if you are using dynamic value in products <code>amount</code>  option.</td><td><strong>No</strong><br>Due to the dynamic nature of price values, plugins have no way of knowing the maximum number of times you can sell a product if you use dynamic product amount.</td><td><strong>No</strong><br>Due to the dynamic nature of price values, plugins have no way of knowing the maximum number of times you can sell a product if you use dynamic product amount.</td><td>Yes</td><td>Yes</td></tr><tr><td>Server  Performances</td><td>Maybe high when you have much buy/sell requests.</td><td>Same as ALL.</td><td>Low, just like other shop plugins doing!</td><td>Same as CLASSIC_ANY.</td></tr></tbody></table>
