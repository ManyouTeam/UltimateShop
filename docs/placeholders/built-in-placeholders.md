# 🔧Built-in Placeholders

{% hint style="info" %}
By default, **placeholder displayed in GUI** will only refreshed when player **open the menu or click the button**. If you want to auto update placeholder value, you need enable `menu.shop.update` option in `config.yml` file. Remeber: this can even cost <mark style="color:red;">**x20**</mark> plugin performance than normal when open the GUI. This is <mark style="color:red;">**NOT**</mark> a good choice to enable this.
{% endhint %}

{% hint style="info" %}
If your shop ID includes `_` symbol, you can replace them to `-` symbol to avoid plugin may not parse them correctly.
{% endhint %}

## Built-in Placeholders List

<table><thead><tr><th width="147">Placeholder</th><th>Display Info</th><th>Where can use</th></tr></thead><tbody><tr><td>{shop}</td><td>Display Shop ID (filename).</td><td>Message File<br>Actions</td></tr><tr><td>{shop-name}</td><td>Display Shop Display Name.</td><td>Shop Menu<br>Actions</td></tr><tr><td>{shop-menu}</td><td>Display Shop Menu ID.</td><td>Actions</td></tr><tr><td>{product}</td><td>Display Product ID.</td><td>Message File</td></tr><tr><td>{amount}</td><td>Display shop or sell amount.</td><td>Message File<br>Actions<br>Price <code>placeholder</code> option</td></tr><tr><td>{status}</td><td>Show whether now price is greatter or less than base price. Only use for dynamic price.</td><td>Price <code>placeholder</code> option</td></tr><tr><td>{item}</td><td>Display Purchased Items Name.</td><td>Message File</td></tr><tr><td>{menu}</td><td>Display Menu ID.</td><td>Message File</td></tr><tr><td>{price}</td><td>Display Buy/Sell price.</td><td>Message File</td></tr><tr><td>{limit}</td><td>Display Buy/Sell limits.</td><td>Message File</td></tr><tr><td>{times}</td><td>Display Buy/Sell times.</td><td>Message File</td></tr><tr><td>{refresh}</td><td>Display Product Reset Refresh Time </td><td>Message File</td></tr><tr><td>{next}</td><td>Display Countdown Time for Product Reset Refresh Time</td><td>Message File<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{buy-price}</td><td>Display Buy Price.</td><td>Display Item Add Lore<br>PlaceholderAPI Support</td></tr><tr><td>{sell-price}</td><td>Display Sell Price</td><td>Display Item Add Lore<br>PlaceholderAPI Support</td></tr><tr><td>{buy-times-player}</td><td>Display player's buy times.</td><td>Display Item Add Lore<br>PlaceholderAPI Support<br><code>amount</code> option</td></tr><tr><td>{buy-total-player}</td><td>Display player total buy times. This data will not be clear by auto reset feature. </td><td><p>Display Item Add Lore</p><p>PlaceholderAPI Support<br><code>amount</code> option</p><p><mark style="color:red;"><strong>PREMIUM (3.8.3+)</strong></mark></p></td></tr><tr><td>{buy-limit-player}</td><td>Display player's buy limits.</td><td>Display Item Add Lore<br>PlaceholderAPI Support</td></tr><tr><td>{buy-refresh-player}</td><td>Display player's product reset refresh time.</td><td>Display Item Add Lore<br>PlaceholderAPI Support</td></tr><tr><td>{buy-next-player}</td><td>Display countdown time for product reset refresh time.</td><td>Display Item Add Lore<br>PlaceholderAPI Support<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{sell-xxx}</td><td>See above.<br>xxx is same as buy, like {sell-limit-playe}</td><td>See above.</td></tr><tr><td>{xxx-server}</td><td>See above.<br>xxx is same as player, like {buy-limit-server}</td><td>See above.</td></tr><tr><td>{last-buy-player}</td><td>Display the time interval between the last purchase of this item by a single player, in seconds. If the player has not purchased this item or the buy time has been reset, it will return <code>0</code>.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{last-buy-reset-player}</td><td>Display the time interval between the last buy reset or first buy time after reset (depends on the reset mode you selected, for more info, please view <a href="../shops/product-config-buy-sell-times-reset.md">this page</a>) of this item by a single player, in seconds. If the player has not purchased this item or the buy time has been reset, it will return last buy time.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM (3.8.3+)</strong></mark></td></tr><tr><td>{last-sell-player}</td><td>Display the time interval between the last sell of this item by a single player, in seconds. If the player has not sold this item or the sell time has been reset, it will return <code>0</code>. </td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{last-sell-reset-player}</td><td>Display the time interval between the last sell reset or first sell time after reset (depends on the reset mode you selected, for more info, please view <a href="../shops/product-config-buy-sell-times-reset.md">this page</a>) of this item by a single player, in seconds. If the player has not sold this item or the sell time has been reset, it will return last sell time.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM (3.8.3+)</strong></mark></td></tr><tr><td>{last-buy-server}</td><td>Display the time interval between the last purchase of this item by global server, in seconds. If no one has not purchased this item before or the buy time has been reset, it will return <code>0</code>.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{last-buy-reset-server}</td><td>Display the time interval between the last buy reset or first buy time after reset (depends on the reset mode you selected, for more info, please view <a href="../shops/product-config-buy-sell-times-reset.md">this page</a>) of this item by global server, in seconds. If the player has not purchased this item or the buy time has been reset, it will return last buy time.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM (3.8.3+)</strong></mark></td></tr><tr><td>{last-sell-server}</td><td>Display the time interval between the last sell of this item by global server, in seconds. If no one sold this item before or the sell time has been reset, it will return <code>0</code>.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{last-sell-reset-server}</td><td>Display the time interval between the last sell reset or first sell time after reset (depends on the reset mode you selected, for more info, please view <a href="../shops/product-config-buy-sell-times-reset.md">this page</a>) of this item by global server, in seconds. If the player has not sold this item or the sell time has been reset, it will return last sell time.</td><td>PlaceholderAPI Support<br><code>amount</code> option<br><mark style="color:red;"><strong>PREMIUM (3.8.3+)</strong></mark></td></tr><tr><td>{buy-click}</td><td>View Buy Price Status</td><td>Display Item Add Lore</td></tr><tr><td>{sell-click}</td><td>View Sell Price Status</td><td>Display Item Add Lore</td></tr><tr><td>{item-name}</td><td>Display product display name</td><td>Display Item Add Lore<br>PlaceholderAPI Support</td></tr><tr><td>{random_&#x3C;ID>}</td><td>Query random placeholder's first picked element.<br>For more info about random placeholder, please view <a href="random-placeholder-premium.md">Randoms Placeholders</a> page.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{random_&#x3C;ID>;;&#x3C;Number>}</td><td>Quert random placeholder specife number of picked element, if this number of picked element does not exist, we will quert the last picked element. You can set max amount of picked element by <code>element-amount</code> option in random placeholder.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{random-times_&#x3C;ID>}</td><td>View random placeholder refresh time.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{random-next_&#x3C;ID>}</td><td>View countdown time for random placeholder refresh time. If the refresh time is not generated, we will return a string set in <code>config.yml</code>.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{conditional_&#x3C;ID>}</td><td>Use conditional placeholder.<br>For more info, please view <a href="conditional-placeholder-premium.md">Conditional Placeholders</a> page.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{custom_&#x3C;ID>}</td><td>Use custom placeholder.<br>For more info, please view <a href="custom-placeholder-premium.md">Custom Placeholders</a> page.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{compare_<br>&#x3C;number1>_&#x3C;number2}</td><td>Compare 2 numbers. Result format can be changed in <code>config.yml</code> file.</td><td>Anywhere in plugin<br><mark style="color:red;"><strong>PREMIUM</strong></mark></td></tr><tr><td>{cron_"&#x3C;cronStr>"}</td><td>Print Cron format result.<br>Don't miss out the <code>"</code> symbol! Should use Quartz format.</td><td>Anywhere in plugin</td></tr><tr><td>{math_&#x3C;mathStr>}</td><td>Calculate the math string you put. Like <code>{math_10+50}</code> will print 60.<br>Require you enable <code>math.enabled</code> option in config.yml file.<br>You can set result scale at <code>placeholder.math.scale</code> option.</td><td>Anywhere in plugin</td></tr></tbody></table>

{% hint style="info" %}
Use built-in placeholder in `amount` option requires you change this option in `config.yml` file from

```yaml
placeholder:
  data:
    can-used-in-amount: false
```

to

```yaml
placeholder:
  data:
    can-used-in-amount: true
```
{% endhint %}

## PlaceholderAPI Support

All built-in placeholders above that has PlaceholderAPI Support tag can be used in PlaceholderAPI expansion:

Use `%ultimateshop_<shopID>_<productID>_<builtInPlaceholder>%` to display built-in placeholder outsite of the plugin!

For example:

`%ultimateshop_example_A_{buy-limit-player}%`

**Start from 2.5.6 version, you don't need put {} symbol into builtInPlaceholder arg, new format example, if you want to use this placeholderapi in our plugin, you have to use this new format becuase we will auto parse built-in placeholder into the value, this new format can avoid this:**

`%ultimateshop_example_A_buy-limit-player%`

For random, random times, random next, compare and conditional placeholder, you don't need specife the shop and the product, just put the placeholder after `ultimateshop`. For example:

`%ultimateshop_{random-times_rotate}%`

This don't support remove `{}` symbol.

## New Line Symbol

Use `;;` symbol if you want to start a new line, this is very useful for some people want to display price in multi lines.

```yaml
placeholder:
  price:
    split-symbol-any: ';;' # <--- Changed this in config.yml
    split-symbol-all: ';;' # <--- Changed this in config.yml
    unknown: "Unknown"
```

