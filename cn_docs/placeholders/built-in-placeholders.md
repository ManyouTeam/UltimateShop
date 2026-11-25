# 🔧内置占位符

`{random-last-reset_<ID>}` 占位符用于显示某个随机占位符的值上一次被重置的时间。在首次重置之前，它会返回 `placeholder.refresh.never` 中配置的文本。

{% hint style="info" %}
默认情况下，**GUI 中显示的占位符**只会在玩家**打开菜单或点击按钮**时刷新。如果你希望占位符自动更新，需要在 `config.yml` 文件中启用 `menu.shop.update` 选项。请记住：启用后打开 GUI 时，插件性能开销最高可达正常情况的 <mark style="color:red;">**x20**</mark>。因此这<mark style="color:red;">**绝非**</mark>启用该功能的好选择。
{% endhint %}

{% hint style="info" %}
如果你的商店 ID 中包含 `_` 符号，可以将其替换为 `-` 符号，以免插件无法正确解析。
{% endhint %}

## 内置占位符列表

<table><thead><tr><th width="147">占位符</th><th>显示内容</th><th>使用位置</th></tr></thead><tbody><tr><td>{shop}</td><td>显示商店 ID（即文件名）。</td><td>消息文件<br>动作</td></tr><tr><td>{shop-name}</td><td>显示商店的显示名称。</td><td>商店菜单<br>动作</td></tr><tr><td>{shop-menu}</td><td>显示商店菜单 ID。</td><td>动作</td></tr><tr><td>{product}</td><td>显示商品 ID。</td><td>消息文件</td></tr><tr><td>{amount}</td><td>显示购买或出售的数量。</td><td>消息文件<br>动作<br>价格 <code>placeholder</code> 选项</td></tr><tr><td>{status}</td><td>显示当前价格高于还是低于基础价格，仅适用于动态价格。</td><td>价格 <code>placeholder</code> 选项</td></tr><tr><td>{item}</td><td>显示所购物品的名称。</td><td>消息文件</td></tr><tr><td>{menu}</td><td>显示菜单 ID。</td><td>消息文件</td></tr><tr><td>{price}</td><td>显示购买/出售价格。</td><td>消息文件</td></tr><tr><td>{limit}</td><td>显示购买/出售次数上限。</td><td>消息文件</td></tr><tr><td>{times}</td><td>显示购买/出售次数。</td><td>消息文件</td></tr><tr><td>{refresh}</td><td>显示商品的重置刷新时间。</td><td>消息文件</td></tr><tr><td>{next}</td><td>显示商品重置刷新时间的倒计时。</td><td>消息文件<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{buy-price}</td><td>显示购买价格。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持</td></tr><tr><td>{sell-price}</td><td>显示出售价格。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持</td></tr><tr><td>{buy-times-player}</td><td>显示该玩家的购买次数。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持<br><code>amount</code> 选项</td></tr><tr><td>{buy-total-player}</td><td>显示该玩家的累计购买次数。该数据不会被自动重置功能清除。</td><td><p>展示物品的物品描述</p><p>PlaceholderAPI 支持<br><code>amount</code> 选项</p><p><mark style="color:red;"><strong>付费版 (3.8.3+)</strong></mark></p></td></tr><tr><td>{buy-limit-player}</td><td>显示该玩家的购买次数上限。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持</td></tr><tr><td>{buy-refresh-player}</td><td>显示该玩家的商品重置刷新时间。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持</td></tr><tr><td>{buy-next-player}</td><td>显示该玩家商品重置刷新时间的倒计时。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{sell-xxx}</td><td>参见上文。<br>xxx 的写法与购买相同，例如 {sell-limit-player}</td><td>参见上文。</td></tr><tr><td>{xxx-server}</td><td>参见上文。<br>xxx 的写法与玩家相同，例如 {buy-limit-server}</td><td>参见上文。</td></tr><tr><td>{last-buy-player}</td><td>显示单个玩家上次购买该物品至今的时间间隔，单位为秒。如果该玩家从未购买过该物品，或购买时间已被重置，则返回 <code>0</code>。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{last-buy-reset-player}</td><td>显示单个玩家上次购买重置（或重置后的首次购买）至今的时间间隔，单位为秒，具体取决于你选择的重置模式，详见<a href="../shops/product-config-buy-sell-times-reset.md">此页</a>。如果该玩家从未购买过该物品，或购买时间已被重置，则返回上次购买时间。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版 (3.8.3+)</strong></mark></td></tr><tr><td>{last-sell-player}</td><td>显示单个玩家上次出售该物品至今的时间间隔，单位为秒。如果该玩家从未出售过该物品，或出售时间已被重置，则返回 <code>0</code>。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{last-sell-reset-player}</td><td>显示单个玩家上次出售重置（或重置后的首次出售）至今的时间间隔，单位为秒，具体取决于你选择的重置模式，详见<a href="../shops/product-config-buy-sell-times-reset.md">此页</a>。如果该玩家从未出售过该物品，或出售时间已被重置，则返回上次出售时间。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版 (3.8.3+)</strong></mark></td></tr><tr><td>{last-buy-server}</td><td>显示全服上次购买该物品至今的时间间隔，单位为秒。如果此前无人购买过该物品，或购买时间已被重置，则返回 <code>0</code>。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{last-buy-reset-server}</td><td>显示全服上次购买重置（或重置后的首次购买）至今的时间间隔，单位为秒，具体取决于你选择的重置模式，详见<a href="../shops/product-config-buy-sell-times-reset.md">此页</a>。如果此前无人购买过该物品，或购买时间已被重置，则返回上次购买时间。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版 (3.8.3+)</strong></mark></td></tr><tr><td>{last-sell-server}</td><td>显示全服上次出售该物品至今的时间间隔，单位为秒。如果此前无人出售过该物品，或出售时间已被重置，则返回 <code>0</code>。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{last-sell-reset-server}</td><td>显示全服上次出售重置（或重置后的首次出售）至今的时间间隔，单位为秒，具体取决于你选择的重置模式，详见<a href="../shops/product-config-buy-sell-times-reset.md">此页</a>。如果此前无人出售过该物品，或出售时间已被重置，则返回上次出售时间。</td><td>PlaceholderAPI 支持<br><code>amount</code> 选项<br><mark style="color:red;"><strong>付费版 (3.8.3+)</strong></mark></td></tr><tr><td>{buy-click}</td><td>查看购买价格状态。</td><td>展示物品的物品描述</td></tr><tr><td>{sell-click}</td><td>查看出售价格状态。</td><td>展示物品的物品描述</td></tr><tr><td>{item-name}</td><td>显示商品的显示名称。</td><td>展示物品的物品描述<br>PlaceholderAPI 支持</td></tr><tr><td>{random_&#x3C;ID>}</td><td>查询随机占位符首次抽取到的元素。<br>有关随机占位符的更多信息，请查看<a href="random-placeholder-premium.md">随机占位符</a>页面。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{random_&#x3C;ID>;;&#x3C;Number>}</td><td>查询随机占位符在指定序号处抽取到的元素（序号由 Number 参数指定）；如果该序号处没有元素，则返回最后一次抽取到的元素。你可以通过随机占位符的 <code>element-amount</code> 选项设置最多可抽取的元素数量。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{random-times_&#x3C;ID>}</td><td>查看随机占位符的刷新时间。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{random-next_&#x3C;ID>}</td><td>查看随机占位符刷新时间的倒计时。如果尚未生成刷新时间，则返回 <code>config.yml</code> 中设置的字符串。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{conditional_&#x3C;ID>}</td><td>使用条件占位符。<br>更多信息请查看<a href="conditional-placeholder-premium.md">条件占位符</a>页面。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{custom_&#x3C;ID>}</td><td>使用自定义占位符。<br>更多信息请查看<a href="custom-placeholder-premium.md">自定义占位符</a>页面。</td><td>插件内任意位置<br>免费版最多 5 个</td></tr><tr><td>{compare_<br>&#x3C;number1>_&#x3C;number2}</td><td>比较两个数字。结果格式可在 <code>config.yml</code> 文件中修改。</td><td>插件内任意位置<br><mark style="color:red;"><strong>付费版</strong></mark></td></tr><tr><td>{cron_"&#x3C;cronStr>"}</td><td>输出 Cron 表达式的结果。<br>不要遗漏 <code>"</code> 符号！应使用 Quartz 格式。</td><td>插件内任意位置</td></tr><tr><td>{math_&#x3C;mathStr>}</td><td>计算你输入的数学表达式，例如 <code>{math_10+50}</code> 会输出 60。<br>需要在 config.yml 文件中启用 <code>math.enabled</code> 选项。<br>可在 <code>placeholder.math.scale</code> 选项中设置结果的小数位数。</td><td>插件内任意位置</td></tr></tbody></table>

### 出售箱全息图占位符

出售箱全息图同样支持 `{id}`、`{owner}`、`{usage}`、`{multiplier}` 和 `{price}`。各占位符的取值与行为请参阅 [出售箱](../features/sell-chest-premium.md#hologram-placeholders) 页面。

{% hint style="info" %}
要在 `amount` 选项中使用内置占位符，需要将 `config.yml` 文件中的该选项从

```yaml
placeholder:
  data:
    can-used-in-amount: false
```

改为

```yaml
placeholder:
  data:
    can-used-in-amount: true
```
{% endhint %}

<a id="placeholderapi-support"></a>

## PlaceholderAPI 支持

上表中所有带有 PlaceholderAPI 支持标记的内置占位符，均可在 PlaceholderAPI 扩展中使用：

使用 `%ultimateshop_<shopID>_<productID>_<builtInPlaceholder>%`，即可在插件外部显示内置占位符！

例如：

`%ultimateshop_example_A_{buy-limit-player}%`

**从 2.5.6 版本开始，`builtInPlaceholder` 参数中不再需要写 `{}` 符号。以下是新格式示例：如果你想在插件内使用这个 PlaceholderAPI 变量，必须采用新格式，因为插件会自动将内置占位符解析为对应的值，而新格式可以避免这种解析：**

`%ultimateshop_example_A_buy-limit-player%`

对于随机、随机次数、随机上次重置、随机下次、比较和条件占位符，无需指定商店和商品，只需把占位符写在 `ultimateshop` 后面即可。例如：

`%ultimateshop_{random-times_rotate}%`

这些占位符不支持去掉 `{}` 符号。

## 换行符

如果需要换行，请使用 `;;` 符号；对于希望分多行显示价格的人来说非常实用。

```yaml
placeholder:
  price:
    split-symbol-any: ';;' # <--- Changed this in config.yml
    split-symbol-all: ';;' # <--- Changed this in config.yml
    unknown: "Unknown"
```
