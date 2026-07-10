# 🌱Example: Stock (like in life)

{% hint style="info" %}
This means real life stock, for set limit of buy/sell times, please view [Products](products.md) page.
{% endhint %}

UltimateShop does not store a separate warehouse inventory. Instead, use server-wide transaction counts: the number sold becomes the buy limit.

```
Player A sells 5 items
        ↓
server sell count becomes 5
        ↓
shop buy limit becomes 5
        ↓
Player B can buy at most those 5 items
```

## Set dynamic value for your product configs

The plugin itself does not store stock data, however, the logic of stock is essentially that player A sells an item to the shop, then the shop can sell the item sold by player A to player B. You only need to set a buy limit, and the value of the buy limit is the number of times the product is sold globally, that is, the placeholder: `{server-times-server}`. Read [dynamic prices](../dynamic-prices/dynamic-price.md) before read this page. Similar to Dynamic Prices, if you want to make stock system, do it in `buy-limits` option and put `{server-times-server}` placeholder in it, for example:

```yaml
  A:
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: echo_shard
        amount: 1
    buy-prices:
      1:
        economy-plugin: EcoBits
        economy-type: quest_points
        amount: 5
        placeholder: '&b{amount} Quest Points'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: EcoBits
        economy-type: quest_points
        amount: 5
        placeholder: '&b{amount} Quest Points'
        start-apply: 0
    buy-limits:
      global: '{sell-times-server}' 
    buy-times-reset-mode: 'NEVER'
    buy-times-reset-time: '00:00:00' 
```

We changed:

* `price-mode` option to `ANY` or `ALL`.
* `buy-limits` option to `{sell-times-server}` . For sell limits, you need write `{buy-times-server}` here. Replace the placeholder to `{buy-times-player}` and `{sell-times-player}` to make the stock be per player.
* `buy-limits-reset-mode` option to `'NEVER'`

In this way, we can ensure that players can only purchase items in the same quantity as the sell times (means restocking by other players sell it)

## Variations

* Replace server placeholders with player placeholders for a personal stock system.
* Use `{buy-times-server}` in a sell limit to model demand before supply.
* Add a fixed amount in a math expression to provide initial stock.
* Combine this with dynamic prices so scarce products become more expensive.

## Restock Manually

Restocking means changing transaction counts or the formula that determines the limit.

* This content of auto reset buy/sell times is introduced on [this page](product-config-buy-sell-times-reset.md).
* Use command `/shop setselltimes <shop> <product> global <newStockPlusNowSellTimes>`. If you want to restock 50 amount, and the now sell time is 75, then you need put 125 to `<newStockPlusNowSellTimes>`.
