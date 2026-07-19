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
