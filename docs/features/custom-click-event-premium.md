# 🎮Custom Click Event - Premium

* Start from version 2.5.1, you can set custom click event for products in shop GUI.
* Find those contents at `config.yml` file.

{% hint style="info" %}
We support override default click event for specifed product, for more info, please view [Products](../shops/products.md) page.
{% endhint %}

```yaml
  # Support value: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.htm
  # Support use ;; symbol to make multi click type.
  click-event:
    buy: 'SHIFT_LEFT'
    sell: 'RIGHT'
    buy-or-sell: 'LEFT'
    # If you want to disable select-amount feature, set this to NEVER.
    select-amount: 'SHIFT_RIGHT'
    sell-all: 'DROP'
    buy-one-stack: 'SWAP_OFFHAND'
  # Custom click actions for shop menu.
  # Premium version only.
  click-event-actions:
    buy-one-stack:
      display-name: 'Buy One Stack'
      buy-only: true
      1:
        type: buy
        shop: '{shop}'
        item: '{item}'
        amount: 64
    sell-one-stack:
      display-name: 'Sell One Stack'
      sell-only: true
      1:
        type: sell
        shop: '{shop}'
        item: '{item}'
        amount: 64
```

* Here we create a new custom click event called `buy-one-stack`, in this custom event, we will execute a action which can buy this product x64 amount.
* After reload the server, if you press **F** key on a product, we will execute the action you set in `click-event-actions` section, like here we will buy x64 this item.

{% hint style="info" %}
The auto add lore and click event in the product are not implemented in the same code, and the plugin cannot automatically generate a suitable auto add lore based on your click event. What I mean is: if you change the custom click event, for example, if you want to right-click to purchase a product instead of the default sell product, unfortunately, you need to manually change the content of the auto add lore to make the product description display correctly as 'Right-click to purchase product'.
{% endhint %}

## Options

Each action in `click-event-actions` support those options, like example above:

* display-name: The friendly name displayed.
* buy-only: This click event button will only display when this product can be purchased (means has buy price).
* sell-only: This click event button will only display when this product can be sold (means has sell price).

Those options only work in bedrock form UI.

## Showcase

<figure><img src="../.gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../.gitbook/assets/image (14).png" alt=""><figcaption></figcaption></figure>

In this example, the **Buy One Stack** button only display in product inclused buy price.

## Example: Only Buy More Menu

In this example, players can only purchase or sell products by opening the buy more menu and selecting the quantity, where the left button executes the buy more buy action and the right button executes the buy more sell action.

{% hint style="info" %}
Do not forgot also update your auto add lore configs to make product description correctly display the click event info.
{% endhint %}

```yaml
  # Support value: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html
  # Support use ;; symbol to make multi click type.
  click-event:
    buy: 'NEVER'
    sell: 'NEVER'
    buy-or-sell: 'NEVER'
    select-amount: 'NEVER'
    sell-all: 'NEVER'
    buy-more-buy: 'LEFT'
    buy-more-sell: 'RIGHT'
  # Custom click actions for shop menu.
  # Premium version only.
  click-event-actions:
    buy-more-buy:
      display-name: 'Buy'
      buy-only: true
      1:
        type: buy_more_menu
        shop: '{shop}'
        item: '{item}'
        buy-more-menu:
          menu: buy-more-buy
          max-amount: 128
    buy-more-sell:
      display-name: 'Buy'
      sell-only: true
      1:
        type: buy_more_menu
        shop: '{shop}'
        item: '{item}'
        buy-more-menu:
          menu: buy-more-sell
          max-amount: 128  
```

## Example: Java Defaults and Bedrock Left Click Only

Keep the existing `menu.click-event` mapping for Java players. Add one option under `menu.bedrock` in `config.yml`:

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    click-event: 'select-amount'
```

Java players keep all their existing click actions. Bedrock players can only use `LEFT` (tap the product) to open its Buy More menu. Right click, shift clicks, Q, and F have no product action for Bedrock players. Form and Dialog shop product buttons execute this action directly instead of opening the product info screen first. Quantity selection and confirmation controls inside Buy More menus keep their own behavior.

Products must have `buy-more: true` and a valid Buy More menu. Use `UUID` instead of `FLOODGATE` if that is your server's supported Bedrock detection method. The value can be a built-in action such as `select-amount`, or a custom event name defined under `menu.click-event-actions`. Omit this option or set it to an empty string to keep the existing Bedrock product info flow. Use `none` to disable Bedrock product clicks.

A product or sub button may override the global setting with the same single string:

```yaml
items:
  A:
    buy-more: true
    bedrock:
      click-event: 'select-amount'
```

Resolution order is: sub-button setting, target product setting, global `menu.bedrock.click-event`. Java players always use their existing product/global `click-event` mapping. These overrides apply to shop products and sub buttons, not Buy More confirmation buttons.

Update auto add lore using `@y` for Java hints and `@x` for a Bedrock hint such as `@x&eClick to select an amount`; see [Display Item Add Lore](../menus/display-item-add-lore.md).

### Custom Buy/Sell Actions Using Default Amounts

For custom actions, use `amount: '{amount}'` to use the click's default buy or sell count. This supports different defaults and sub-button overrides:

```yaml
menu:
  click-event:
    custom-buy: 'LEFT'
    custom-sell: 'RIGHT'
  bedrock:
    click-event: 'select-amount'
  click-event-actions:
    custom-buy:
      display-name: 'Buy'
      1:
        type: buy
        shop: '{shop}'
        item: '{item}'
        amount: '{amount}'
    custom-sell:
      display-name: 'Sell'
      1:
        type: sell
        shop: '{shop}'
        item: '{item}'
        amount: '{amount}'
```

With `default-buy-amount: 10` and `default-sell-amount: 5`, the Java custom buy action trades 10 times and the sell action trades 5 times. A sub button uses its corresponding override or inherits the target product's defaults. Explicit values such as `amount: 1` or `amount: 64` remain fixed. If `amount` is omitted, it retains the existing default of 1. Outside product clicks, `{amount}` keeps the action context's existing amount. Actions run once per click; the default amount does not repeat the action list.
