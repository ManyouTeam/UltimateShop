# 📂Shops

An example shop file is here:

```yaml
settings:
  menu: 'example-shop-menu'
  # Optional: inline menu config for this shop. You can use this without settings.menu.
  menu-settings:
    dynamic-layout: false
    layout:
      - '000000000'
      - '0ABCDEFG0'
      - '0HIJKLMN0'
      - '0OPQRSTU0'
      - '000000000'
      - 'a0003000b'
  buy-more: true
  shop-name: 'Food Shop'
  hide-message: false
  secret-shop-items: true
  custom-command:
    name: 'mineral'
    description: 'Custom Words'
  
general-configs:
  # This means all products in this shop will use this price mode and product mode.
  # unless they have set other value.
  price-mode: CLASSIC_ANY
  product-mode: CLASSIC_ANY
  # Support all product options, like prices, products, limits and so on!
  fail-actions:
    1:
      type: sound
      sound: block.note_block.bass
    
items:
  A: # Product A config
  #...
  B: # Product B config
  #...
  C: # Product C config
  #...

buttons:
  a:
    display-item:
      material: arrow
      name: '&cPrevious page'
      lore:
        - '&7Click to view previous page!'
    actions:
      1:
        type: shop_menu
        shop: 'crops'    
```

## Create the shop

Create a config file at `plugins/UltimateShop/shops/<shopName>.yml`:

{% code title="PUT_SHOP_ID_INTO_FILE_NAME.yml" %}
```yaml
settings:
  menu: starter-menu
  buy-more: true
  shop-name: Starter Shop
  hide-message: false

items:
  D:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: DIAMOND
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 100
        placeholder: '&6$100'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 60
        placeholder: '&6$60'
        start-apply: 0
```
{% endcode %}

## Settings

*   menu: Shop menu name, which means menu file name. It’s the central hub that connects your shop and menu. In this example, we set it to `example-shop-menu`.

    You should be find the menu file at the `menus` folder, it will called `example-shop-menu.yml`. For info about menus, please view [Menus](../menus/general-menus.md) page.&#x20;
* menu-settings: You can set up a separate menu configuration exclusively for this shop. This part will overwrite the corresponding menu configuration. You can remove `menu` option if this part include all menu options you want to.
* buy-more: Whether product in this shop can open buy more menu.&#x20;
* shop-name: Shop display name, which used in `{shop-name}` placeholder.&#x20;
* hide-message: Whether we hide the messages that will send after player buy or sell items in this shop.&#x20;

{% hint style="warning" %}
By default, only hide success buy or sell message, for fail message like limit reached or not enough money, you have 2 methods to hide them:

* Try **also** set `placeholder.click.enabled` option value to `true` in your `config.yml` file to hide them. This will display fail status at display item lore and will cost extra performance.
* Try **also** set `force-display-fail-message` option value to `true` in your `config.yml` file. This way is recommended. (4.2.11+)
{% endhint %}

* secret-shop-items: If enabled, if the player does not meet the open condition of the menu corresponding to the shop where the product is located or the product is not displayed in the menu, the corresponding product will be automatically hidden and cannot be traded. You can set secret rule at `config.yml` file.

```yaml
secret-shop-items:
  require-display-in-menu: true
  require-meet-menu-open-conditions: true
```

* custom-command: Custom Open Command Settings for this shop. If not set, this menu can only be opened by `/shop menu` command. <mark style="color:red;">**(PREMIUM)**</mark>

## General Configs

The product configuration options set here will apply to all products. For `buy-action`, `sell-actions` and `fail-actions`, we will auto merge the value set here and in product configs.

## Items

Items is products, products can not only being real items, but also virtual items, like 100 gems economy.

For more info, please view [Products](products.md) page.

## Buttons

Shops can add buttons which has custom actions when player clicks it.

For each button, we have those options:

* display-item: The display item of this button, should use [Display Item Format](https://ultimateshop.superiormc.cn/format/display-item-format).
* actions: The action will executed after we click this button. Use [Action Forma](https://ultimateshop.superiormc.cn/format/action-format)[t](https://ultimateshop.superiormc.cn/format/action-format) here.
* fail-actions: The action will executed if we don't meet the condition of this button. Use [Action Forma](https://ultimateshop.superiormc.cn/format/action-format)[t](https://ultimateshop.superiormc.cn/format/action-format) here.
* conditions: The condition of this button, if player don't meet this condition, then we will execute the `fail-action`. Use [Condition Format](https://ultimateshop.superiormc.cn/format/condition-format) here.
