# 🔲General Menus

All menu files are saved in `/menus/` folder.

## Create the menu

Create a config file at `plugins/UltimateShop/menus/<menuID>.yml`:

{% code title="PUT_MENU_ID_INTO_FILE_NAME.yml" %}
```yaml
title: '{shop-name}'
size: 18

layout:
  - '000000000'
  - '000D00X00'

buttons:
  0:
    display-item:
      material: BLACK_STAINED_GLASS_PANE
      name: ' '
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```
{% endcode %}

`D` is the product ID used under [shop](../shops/shops.md) config's `items` section. Menu layouts use one-character product IDs directly; advanced sub-buttons can represent the same product in additional slots or menus. For more info, please view [Layout Option](general-menus.md#layout-option).

## Types

There are 3 types of menus.

* Common Menus: Just like other menu plugins doing. You can use them open other shop menus.
* Shop Menus: Shop menus will display products in specified shop in it. The shop menu has all features of a common menu.&#x20;
  * Set `menu` option to set their corresponding shop menu in shop configs.
  * Or, use `menu-settings` directly in the shop configuration to set up a separate menu configuration exclusively for this shop.
* Buy More Menus: Can select amount of you will buy or sell. This type of menus have more settings, please view [Buy More Menus](buy-more-menus.md) page to know more. **Buy more menu can only open from shop menus with selecting a product, it can not be directly opened**.

## Configs

* title: Menu title, for shop menu type, support `{shop-name}` to display shop displayname which set in it's config.
* size: Menu size, only support one of the number: **9,18,27,36,45,54**. For detalied info, please view below.
* layout: Button layout, this is a list option. For more info, please view below. For detalied info, please view below.

{% hint style="warning" %}
The `layout` option and `size` option must correspond. If your layout is based on **4x9** format, then your size must be set to **36**, otherwise the plugin will encounter errors.
{% endhint %}

* dynamic-layout: If you are using dynamic value like placeholders in `layout` option, you need enable this option. This will cost extra performance. <mark style="color:red;">**(PREMIUM)**</mark>
* buttons: Button configs, button ID is being used in `layout` option to set where this button display in menu.
* conditions: Only players who meet the conditions can open this menu, use [Condition Format](../format/condition-format.md) here.
* open-actions: Do action when open this menu, use [Action Format](../format/action-format.md) here.
* close-actions: Do action when close this menu, use [Action Format](../format/action-format.md) here. <mark style="color:red;">**Please carefully note that when you have already opened a menu, if you open other menus through actions or other means, it will also trigger close actions of the opened menu**</mark>**.**&#x20;
* bedrock: Please view [Bedrock Menus](bedrock-menus-premium.md) page to know about it. <mark style="color:red;">**(PREMIUM)**</mark>
* dialog: Please view [Dialog Menus](dialog-menus-premium.md) page to know about it. <mark style="color:red;">**(PREMIUM)**</mark>
* custom-command: Custom Command settings for common menu, if you want to set custom command for shop menu, please add them at [Shops](../shops/shops.md) config. <mark style="color:red;">**(PREMIUM)**</mark>

Example:

```yaml
title: 'Shop'

size: 54

bedrock:
  enabled: true
  content: '&fWelcome to shop.'
  
dialog:
  enabled: true
  content: '<gray>Select a category.'
  button-width: 180
  columns: 2
  
custom-command:
  name: 'mineral'
  description: 'Custom Words'

conditions: 
  1:
    type: permission
    permission: 'test.required'
  
open-actions:
  1:
    type: sound
    sound: 'ui.button.click' 

close-actions:
  1:
    type: sound
    sound: 'ui.button.click' 

dynamic-layout: false

layout:
  - '000000000'
  - '000000000'
  - '0000A0000'
  - '000000000'
  - '000000000'
  - '000000000'

buttons:
  A:
    display-item:
      material: BREAD
      name: '&dFoods'
      lore:
        - '&7Click to open food shop!'
    actions:
      1:
        type: shop_menu
        menu: 'example'
```

For each button, we have those options:

* display-item: The display item of this button, should use [Display Item Format](../format/display-item-format.md).
* actions: The action will executed after we click this button. Use [Action Forma](../format/action-format.md)[t](../format/action-format.md) here.
* fail-actions: The action will executed if we don't meet the condition of this button. Use [Action Forma](../format/action-format.md)[t](../format/action-format.md) here.
* conditions: The condition of this button, if player don't meet this condition, then we will execute the `fail-action`. Use [Condition Format](../format/condition-format.md) here.

## Layout Option

The menu and shop feature in the plugin are separate, and their configurations are stored in the menus folder and shops folder respectively.

* To know more info about menu feature, please read the **Menus** chapter.
* To know more info about menu feature, please read the **Shops** chapter.
* To know more info about the effect of other folder, please read the [Configuration files](../info/configuration-files.md) page.

The following is a sample configuration file for a shop:

```yaml
# This is a shop config exist in /shops/ folder.
settings:
  menu: 'example-shop-menu'
  buy-more: true
  shop-name: 'Blocks Shop'
  hide-message: false

items:
  A:
    # ...
  B:
    # ...
  C:
    # ...
```

In there you’ll find the `settings.menu` option, which is crucial because it’s the central hub that connects your shop and menu. In this example, we set it to `example-shop-menu`.

You should be find the menu file at the `menus` folder, it will called `example-shop-menu.yml`.

<pre class="language-yaml"><code class="lang-yaml"># This is a menu config exist in /menus/ folder.
title: '{shop-name}'
size: 54

layout:
  - '000000000'
  - '0ABCDEFG0'
  - '0HIJKLMN0'
  - '0OPQRSTU0'
  - '000000000'
  - 'a0003000b'

buttons:
<strong>  # ...
</strong></code></pre>

Also, you can set up a separate menu configuration for this shop through the `menu-settings` option in shop configs, for example:

<pre class="language-yaml"><code class="lang-yaml"><strong># This is a shop config exist in /shops/ folder.
</strong><strong>settings:
</strong>  shop-name: 'Example Shop'
  # Override menu config for this shop.
  menu-settings:
    title: '{shop-name}'
    size: 18
    dynamic-layout: false
    layout:
      - '000000000'
      - '0ABCDEFG0'
    buttons:
      3:
        display-item:
          material: ARROW
          name: '{lang:back-button}'
</code></pre>

Among them, the `layout` option is crucial, as it determines where your products or buttons will be displayed. You will find that it consists of **6x9** characters, with each character corresponding to a slot in the Minecraft chest inventory. The characters entered in the corresponding position represent the items or buttons with the corresponding ID that we will display.

In this example:

* Actually, since there is no product or button with an ID of `0`, nothing will be displayed in the slot with a character of `0`.
* If the shop using this menu has products with IDs `A, B, C, D, etc`., the corresponding products will be displayed in the corresponding slots.
* The same goes for buttons. I forgot to tell you that you can set custom buttons not only in the menu configuration file, but also in the shop configuration file.
* If you change the value of the `size` option, don't forget to remove the extra lines in the `layout` option. For example, if you set the value of the size option to **36**, then the character composition of the layout option is **4x9**.

You can also use multiple characters to represent slots, in which case you need to use `` ` `` symbols to separate these multiple characters for the plugin to recognize them. For example:

```yaml
layout:
  - '000000000'
  - '0`b1``b2``b3``b4``b5``b6``b7`0'
  - '0HIJKLMN0'
  - '0OPQRSTU0'
  - '000000000'
  - 'a0003000b'
```

with this shop or button configs:

```yaml
items: # or buttons:
  b1: # Product ID
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products: 
      #...
    sell-prices:
      #...
  b2:
    #...
  b3:
    #...
  b4:
    #...
  b5:
    #...
  b6:
    #...
  b7:
    #...
```

You can also use conditional display in `layout` option, for more info, please view [Conditional Display](conditional-display-premium.md) page.

## Dynamic Layout <mark style="color:red;">- Premium</mark>

You can even also use PlaceholderAPI or built-in placeholder value in `layout` option, but you need enable `dynamic-layout` option in menu configs. Like:

```yaml
dynamic-layout: true

layout:
  - '000000000'
  # Using random placeholder here!
  - '0`{random_daily;;1}``{random_daily;;2}``b3``b4``b5``b6``b7`0'
  - '0HIJKLMN0'
  - '0OPQRSTU0'
  - '000000000'
  - 'a0003000b'
```

## Title Update <mark style="color:red;">- Premium, Require Paper</mark>

Require your server install both **packetevents and MythicChanger** plugin in your server, only support for Paper server users.

Set `menu.title-update.enabled` option value to `true`, then set  `menu.title-update.click-update` or `menu.title-update.circle-update` option value to `true` in `config.yml` file to active title update feature. After enable, after each click button in menu, the title will be updated, very useful for display placeholder in title and then auto update value fater each click or every second auto update.

```yaml
  # PREMIUM version only, if enabled, can update dynamic value used in GUI title.
  title-update:
    enabled: true # <--- Set it to true
    # Whether gui title will refresh every buttons every 1 second.
    # This will refresh placeholder that displayed in menu title.
    circle-update: false
    # Whether gui title will refresh every buttons when click any of them.
    # This will refresh placeholder that displayed in menu title.
    click-update: true # <--- Set it to true
    resend-items-pack: false
```

The Minecraft client itself does not support changing the title of a container after opening it, so you will see items flashing and quickly reappearing, which cannot be solved. You can try enable `menu.title-update.resend-items-pack` value to `true` in `config.yml` file. This will only slightly alleviate the situation.

## All Buttons Update

Set whether keep trying to refresh the button display in the GUI after the player opens it. Find those content at `config.yml` file. Do not recommend enable it as it will cost more performance.

If you just want us trying to refresh the product button when we reset buy or sell times of it, you can use `use-times.auto-reset-mode` option instead. That option can help you filter out all button displays that only refresh when reset occurs, thereby saving server performance.

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
```
