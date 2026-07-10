# 🔽Buy More Menus

{% hint style="info" %}
Buy more menu means select quantity you want, not means open a new menu that can purchase this item again.
{% endhint %}

## Buy More Menus Config

Buy more menus have those special options compare than common menu:

```yaml
amount-items:
  1:
    display-item:
      material: GREEN_WOOL
      name: '&a+1'
      lore:
        - '&7Click to add 1 amount.'
    add-amount: 1
  2:
    display-item:
      material: GREEN_WOOL
      name: '&a+10'
      lore:
        - '&7Click to add 10 amount.'
    add-amount: 10
  3:
    display-item:
      material: GREEN_WOOL
      name: '&a+32'
      lore:
        - '&7Click to add 32 amount.'
    add-amount: 32
  4:
    display-item:
      material: RED_WOOL
      name: '&c-1'
      lore:
        - '&7Click to remove 1 amount.'
    add-amount: -1
  5:
    display-item:
      material: RED_WOOL
      name: '&c-10'
      lore:
        - '&7Click to remove 10 amount.'
    add-amount: -10
  6:
    display-item:
      material: RED_WOOL
      name: '&c-32'
      lore:
        - '&7Click to remove 32 amount.'
    add-amount: -32

display-item: B

confirm-items:
  C:
    display-item:
      material: PAPER
      name: '&aConfirm'
      lore:
        - '&7Click to finish the trade!'
    modify-lore: true
  D:
    display-item:
      material: PAPER
      name: '&a&lCLICK TO BUY'
      lore:
        - '&aClick to confirm and purchase'
        - '&athe quantity you have selected.'
    # You can remove click-action option, then confirm button can do all things, like buy, sell, sell all.
    # If this option exists, then this confirm button can only do the thing.
    click-action: buy
  E:
    display-item:
      material: PAPER
      name: '&c&lCLICK TO SELL'
      lore:
        - '&cClick to confirm and sell'
        - '&cthe quantity you have selected.'
    click-action: sell
```

* amount-items: Select amount item config. `add-amount` can be replaced to `set-amount` option.
* display-item: Must be a single char, use this char in `layout` option to set where it will display in menu.
* confirm-items: Confirm buy or sell item config.
* confirm-items.??.click-action: Make this confirm button can only do specified thing, like buy, sell.
* confirm-items.??.modify-lore: Whether we will modify display item lore to add info about price, limits and so on. You can set add lore info at `config.yml` file. **Optional (default to true)**

## Set Buy More Menu

You have 2 way to set buy more menu for products. By default, all products use the buy more menu you set in `config.yml` file. There are 3 types of default buy more menus.&#x20;

* If this product has both buy price and sell price, we will use `default` one you set here.
* If this product don't has sell price, we will use `only-buy` one you set here.
* If this product don't has buy price, we will use `only-sell` one you set here.

```yaml
  buy-more-menu:
    not-open-when-invalid: true
    default:
      menu: buy-more
      max-amount: 64
    only-buy:
      menu: buy-more-buy
      max-amount: 64
    only-sell:
      menu: buy-more-sell
      max-amount: 64
```

You can also set `buy-more-menu` section in each product configs, in [Shops](../shops/shops.md) page, we have told you how to do that with an example.

If you want to disable buy more menu for specified shops or products, just use `buy-more` option in shop configs or product configs, this also claimed in [Shops](../shops/shops.md) page.

Other option:

* not-open-when-invalid: If there is only a purchase button in the buy more menu and the product is not purchasable (meaning there is no buy price), then we consider this buy more menu to be invalid and cannot be opened, and the same applies to sell. (Added in 3.12.1)

## FAQ: Can we separate buy more and sell more?

A: This feature added in version 3.12.1, and premium version only, for example of it, please view [here](../features/custom-click-event-premium.md#example-only-buy-more-menu).
