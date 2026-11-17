# ♻️商品配置：购买/出售次数重置

## 重置

我们只会在以下情况下尝试重置：

* 玩家不在线时：购买/出售次数要等他再次进服才会重置。
* 玩家在线时：只在下面几种时机重置：
  * 玩家执行购买/出售操作之前
  * 玩家打开商店界面时
  * 自动重置（需在 `config.yml` 开启 `use-times.auto-reset-mode`，会额外消耗服务器性能）
* 重置的前提是商品已经生成过重置时间；压根没有重置时间，自然不会重置：&#x20;
  * 以 `COOLDOWN_` 开头的重置模式：需要先查看一次该商品（例如打开商店界面），才会生成新的重置时间。
  * **不**以 `COOLDOWN_` 开头的重置模式：需要先买入或卖出一次，才会生成新的重置时间。

这些设计是为了在重置数据时省下性能开销，我们不会改动这套行为。如果你觉得难以接受，换用其他商店插件会是更合适的选择。

## 选项类型

购买次数的相关选项：

* buy-times-reset-mode（3.3.0之前是buy-limits-reset-mode，但它们是相同的）
* buy-times-reset-time（3.3.0之前是buy-limits-reset-time，但它们是相同的）
* buy-times-reset-time-format
* buy-times-reset-value
* buy-times-max-value

出售次数的相关选项：

* sell-times-reset-mode（3.3.0之前是sell-limits-reset-mode，但它们是相同的）
* sell-times-reset-time（3.3.0之前是sell-limits-reset-time，但它们是相同的）
* sell-times-reset-time-format
* sell-times-reset-value
* sell-times-max-value

如果要给所有商品统一启用购买/出售次数重置，只需在 `config.yml` 中修改即可。

```yaml
use-times:
  default-reset-mode: 'NEVER'
  default-reset-time: '00:00:00'
  # This only works for CUSTOM type of reset mode.
  default-reset-time-format: 'yyyy-MM-dd HH:mm:ss'
  default-reset-value: 0
  # Set -1 to disable.
  default-max-value: -1
  # If set to true, product default buy / sell times will be set to reset value set in product configs or default value above.
  set-reset-value-by-default: true
  # If set to true, max value set in product configs or default value set above will only work for total placeholder.
  max-value-for-total-only: true
```

{% hint style="info" %}
`set-reset-value-by-default` 和 `max-value-for-total-only` 只存在于 `config.yml` 中。
{% endhint %}

不论在哪一层设置，这个功能都由 5 类选项构成：

* 重置模式
* 重置时间
* 重置时间格式（仅`CUSTOM`类型需要）
* 重置值
* 最大值

## 重置模式

支持这些模式：

* NEVER:&#x20;
* TIMER：经过你指定的时长后重置，例如 5 小时后。
* TIMED：在每天指定的时刻重置，比如 20:15。
* RANDOM\_PLACEHOLDER：与指定随机占位符的重置时间保持同步。 （3.3.0 起）<mark style="color:red;">**- 付费版**</mark>
* CUSTOM：直接在这里写出重置时间，插件不做任何换算。推荐通过 PlaceholderAPI 的结果取得时间；同时要在 `reset-time-format` 选项里声明该结果的时间格式，插件才知道你的占位符输出长什么样。 （3.3.0 起）<mark style="color:red;">**- 付费版**</mark>
* COOLDOWN\_TIMER （3.3.0 起）&#x20;
* COOLDOWN\_TIMED （3.3.0 起）&#x20;
* COOLDOWN\_CUSTOM （3.9.1 起） <mark style="color:red;">**- 付费版**</mark>

### COOLDOWN\_TIMED（COOLDOWN\_TIMER）与 TIMED（TIMER）的区别

`TIMED` 和 `TIMER` 是在每次买入或卖出之后才开始推算重置时间，而且一旦玩家达到上限就不再保存；`COOLDOWN_TIMED` 和 `COOLDOWN_TIMER` 则在最近一次重置、或首次查看该商品（例如打开商店界面）时，立刻算出下一次重置时间并存下来，在到点之前不会再重置。

正因如此，使用 `COOLDOWN_TIMED` 或 `COOLDOWN_TIMER` 时，重置时间不会因为服务器重启、改配置等原因自动调整。也就是说，如果你不小心把它设成一年后才刷新，改回配置也不会让已存下的时间跟着变；而 `TIMED` 或 `TIMER` 模式会自动跟上。

另外，`last-*-reset-*` 这类占位符在 `COOLDOWN_TIMED`/`COOLDOWN_TIMER` 下返回的是实际的重置时刻，在 `TIMED`/`TIMER` 下返回的则是上次重置后首次买入或卖出的时间。

## 重置时间

不同重置模式在这里要填的内容不同。支持占位符，<mark style="color:red;">**但这里用的占位符必须是服务端变量，也就是所有玩家拿到同一个值。**</mark>

使用不以 `COOLDOWN_` 开头的重置模式时，在 `reset time` 里填动态占位符要格外小心。

这些模式并不保存「下一次重置」的时间戳，它们的做法是：每次发生购买或出售时，拿当前时间与配置的 `reset time` 比一次。如果你的 `reset time` 占位符在交易发生_之前_就已经自动更新了，就可能出现界面上的重置时间已经变了、而实际的购买/出售次数还没清零的情况。

一句话总结：如果非要在 `reset time` 里用占位符，就改用 `COOLDOWN_` 开头的重置模式。

#### NEVER

这一项留空即可。

#### TIMER/COOLDOWN\_TIMER

这里填 3 到 5 个数字，用 `:` 分隔，例如 `15:00:00`。

从**右**到**左**的每个数字代表：

* 秒
* 分钟
* 小时
* 天 <mark style="color:red;">**- 付费版**</mark>
* 月份 <mark style="color:red;">**- 付费版**</mark>

上例表示 15 小时之后。也就是说：**若当前时间为 2023-09-04 12:00:00，就会在 15 小时后、即 2023-09-05 03:00:00 重置。**

#### TIMED/COOLDOWN\_TIMED

TIMED 与 TIMER 的写法几乎一样，区别只是从右数前三位表示一天中的时刻。同样以 `15:00:00` 为例：

若当前时间是 2023-09-04 12:00:00，则会在 2023-09-04 15:00:00 重置。

这是天数为 0 时的结果；若填 1，就整体再往后推一天，如此类推。

做每日商店时 **days** 填 0；做每周商店时填 6。因为你要在最后一天就把次数重置掉，而不是拖到第二天，对吧？

这类重置模式还支持一次写多个重置时间，用 `;;` 分隔，插件会取最早的那个。例如：<mark style="color:red;">（仅付费版）</mark>

```yaml
    sell-times-reset-mode: 'TIMED'
    sell-times-reset-time: '20:00:00;;19:00:00'
```

上例中，该商品（或该随机占位符）每天 19:00 和 20:00 各重置一次。

#### CUSTOM/COOLDOWN\_CUSTOM<mark style="color:red;">**- 付费版**</mark>

这里只需填一个 PlaceholderAPI 占位符，其输出必须包含完整的年月日时分秒。由于各类占位符返回的时间格式并不统一，你还必须在「重置时间格式」选项里说明它的格式。&#x20;

#### RANDOM\_PLACEHOLDER <mark style="color:red;">**- 付费版**</mark>

填入一个有效的随机占位符 ID。

## 重置值 <mark style="color:red;">**- 付费版**</mark>

重置值默认是 0，也可以改成别的。该选项支持占位符，配合随机占位符使用，还能让每次重置后的初始次数都不一样。

重置值只在每次重置时生效。如果玩家从没买过或卖过，这个值也就用不上，这时得先考虑设置默认值，详见下面的「默认值」一节。

<mark style="color:red;">此选项非常危险，必须谨慎设置</mark>。你<mark style="color:red;">**必须**</mark>确保 **buy-limits / sell-limits 的值始终大于重置值**。也就是说，重置之后玩家必须还能再买、再卖；否则这个商品就会变成永远买不了、也卖不了的状态，而且**除非**你删掉该商品的 buy/sell-limits，或用命令手动改次数，否则不可恢复。<mark style="color:red;">**切记**</mark>！

## 默认值 <mark style="color:red;">**- 付费版**</mark>

重置值只会在购买次数或出售次数被重置之后生效。如果你希望它同时充当初始默认值，可以在 `config.yml` 中开启 `use-times.set-reset-value-by-default`。想做「默认库存」这类效果时非常有用。

## 最大值 <mark style="color:red;">**- 付费版**</mark>

可以给购买次数和出售次数设一个上限，达到之后插件就不再继续累加。

请注意：

* 达到上限后玩家仍能继续买卖，只是次数不再增长。如果想让玩家干脆买不了、卖不了，应该改用[商品](products.md)配置里的 `buy-limits` 或 `sell-limits`，而不是这个功能。
* 因为次数到顶后不再累加，一旦这个上限高于 buy-limits/sell-limits 的限值，限购就形同虚设；其他未提及的功能也可能受影响。
* 可以打开 `config.yml` 里的 `use-times.max-value-for-total-only`：让 times 系占位符到顶后照常增长，而 total 系占位符到顶后停止增长，正好解决上面说的问题。两者的区别见[本页](../placeholders/built-in-placeholders.md)。

## 动态重置时间 <mark style="color:red;">**- 付费版**</mark>

下面的例子用随机占位符，让商品在 3、4、5 小时之后随机刷新，而不是固定时刻刷新。

在 `random_placeholder` 文件夹中新建一个随机占位符：

```yaml
reset-mode: ONCE
elements:
  - '03:00:00'
  - '04:00:00'
  - '05:00:00'
```

然后在任意商品配置的 `buy-times-reset-time` 中引用它。

```yaml
  B:
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: GOLD_INGOT
        amount: 1
    buy-prices:
      # 
    sell-prices:
      #
    buy-limits:
      default: '2'
    buy-times-reset-mode: 'COOLDOWN_TIMED'
    buy-times-reset-time: '{random_reset}' # <--- Used here, sell-times also works!
```

## 动态重置值 <mark style="color:red;">**- 付费版**</mark>

默认每次重置后玩家的购买/出售次数都会归零，但也可以改成别的值，甚至是随机值。

在 `placeholders/random_placeholders` 下新建一个名为 `resetvalue.yml` 的随机占位符配置：

```yaml
reset-mode: ONCE
elements:
  - '0~20' # A random number from 0 to 20
  - '40' # A fixed number
```

然后在任意商品配置的 `buy-times-reset-value` 中引用它。

```yaml
  B:
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: GOLD_INGOT
        amount: 1
    buy-prices:
      # 
    sell-prices:
      #
    buy-limits:
      default: '2'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '19:00:00;;20:00:00' # <--- TIMED mode supports multi reset time!
    buy-times-reset-value: '{random_resetvalue}' # <--- Used random placeholder
```

## Cron 重置（每周重置/每月重置）<mark style="color:red;">- 付费版</mark>

重置时间支持 Cron 表达式。&#x20;

* 把重置模式设为 `COOLDOWN_CUSTOM`。
* 在重置时间里使用内置占位符 `{cron_"<Cron 表达式>"}`，注意 `"` 不能漏。表达式需采用 **Quartz** 格式。

例如：

商品配置：

```yaml
    sell-times-reset-mode: 'COOLDOWN_CUSTOM'
    sell-times-reset-time: '{cron_"0 0 0 ? * 5"}'
    # sell-times-reset-time-format: 'yyyy-MM-dd HH:mm:ss' 
    # You do not need set a time format here, this just help you know there is a option that you can set custom time format.
```

Cron 表达式可以借助 ChatGPT 或[这个工具](https://freeformatter.com/cron-expression-generator-quartz.html)生成。上面这个表达式表示每周四 0:00 重置。如何编写 Cron 不在我们的支持范围内。

{% hint style="info" %}
你**必须**保证 Cron 占位符输出所用的时间格式（在 `config.yml` 中设置）与这里设置的一致。默认情况下二者本来就是相同的。
{% endhint %}

## 重置时间不正确？

* 商品必须先被买过或卖过（`TIMED`/`TIMER`）、或被查看过（`COOLDOWN_TIMED`/`COOLDOWN_TIMER`），下一次的重置时间才会被存下来。否则只能等交易完成后，按当前时间推算出一个可能的重置时间显示出来。
* 玩家的数据只会在其在线时重置。若玩家离线期间就到了重置时间，会等他再次进服时再处理；新的重置时间以当时的时间为准，而不是原本应到的时刻（那时他并不在线）。服务器数据没有这个问题（服务器一直在线），所以服务器与玩家的重置时间对不上很正常。（常见于 `COOLDOWN_TIMER` 模式）

## 重置时间已更新，但购买/出售次数没清零？

* 如果非要在 `reset time` 里用占位符，请改用 `COOLDOWN_` 开头的重置模式。
