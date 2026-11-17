# 🎲随机占位符 - 付费版

付费版新增了内置占位符 `{random}`。凡是可以使用占位符的选项，都能用随机占位符来随机取值，例如随机价格数量、随机重置时间、随机库存等。

## 配置

所有随机占位符配置都存放在 `placeholders/random_placeholders` 文件夹中，文件名就是它的 ID，例如 `rotate.yml` 的 ID 是 `rotate`。配置示例如下：

```yaml
reset-mode: TIMED
reset-time: '00:00:00'
per-player-element: true
element-sort: true
element-amount: 5
elements:
  A:
    rate: 1
    conditions:
      1:
        type: permission
        permission: 'test.permission'
  B:
    rate: 5
  C:
    rate: 2
  D:
    rate: 5
  E:
    rate: 2
  F:
    rate: 15
  G:
    rate: 7
```

* per-player-element：设为 `false` 时，所有玩家共用随机占位符输出的同一个值。例如玩家 1 抽到 **A**，玩家 2 抽到的也是同一个值，哪怕玩家人数多达一亿，结果依旧不变。设为 `true` 时，每名玩家输出的值互不相同，并且可以为元素设置条件；元素不满足条件时，该玩家永远抽不到它。

{% hint style="info" %}
启用该随机占位符后<mark style="color:red;">**不建议**</mark>再修改此选项，否则插件可能检测到错误（例如每玩家独立的随机占位符出现在全服数据库中），并在控制台输出无法屏蔽的提示。\
每玩家独立的随机占位符不能用于全服场景，反之亦然；未启用每玩家独立的随机占位符也不能用于每玩家场景。例如使用 `resetrandomplaceholder` 命令时，每玩家独立的随机占位符必须在命令参数中填写玩家名，而未启用每玩家独立的随机占位符则不能填写玩家名，否则插件会报错。
{% endhint %}

* element-sort：设为 `false` 时，随机占位符的结果不保证顺序。例如把 `elements` 设为 `A, B, C, D, E`，随机抽中了 `B, D`，最终结果可能是 `B, D`，也可能是 `D, B`。设为 `true` 时，结果会严格按照元素顺序输出，只可能是 `B, D`。**（3.12.2 版本加入）**
* reset-mode/reset-time：详见下文。
* element-amount：该占位符一次抽取的元素数量。**（3.1.0 版本加入）**
* elements：占位符将从中抽取的随机元素。&#x20;

`elements` 有两种写法。&#x20;

第一种是把所有元素写成列表，此时每个元素的权重相同，且不能设置条件。**支持用 `~` 符号表示随机取数，例如 `5~100` 表示在 5 到 100 之间随机取一个数。**

```yaml
elements:
  - 'A'
  - 'B'
  - 'C'
```

```yaml
elements:
# Random number from 5 to 100.
  - '5~100'
```

第二种是分段写法，每个元素都可以填写 `rate` 和 `conditions` 选项。`conditions` 不是必填项，且仅在 `per-player-element` 设为 `true` 时可用。**（3.12.0 版本加入）**

```yaml
per-player-element: true # <--- Only support use condition when this option to true
elements:
  A:
    rate: 1
    conditions:
      1:
        type: permission
        permission: 'test.permission'
  B:
    rate: 5
  C:
    rate: 2
```

## 使用占位符

用 `{random_<ID>;;<Number>}` 占位符显示它的值，例如 `{random_daily;;2}` 会查询 `daily` 随机占位符抽中的**第二个**元素。更多信息请见[占位符](built-in-placeholders.md)页面；该占位符的用法示例请见[每日商店](../shops/example-daily-shop-rotating-shop.md)页面。

用 `{random-times_<ID>}` 占位符显示该占位符的重置时间，例如 `{random-times_daily}`。

用 `{random-last-reset_<ID>}` 占位符显示其值上一次被重置的时间，例如 `{random-last-reset_daily}`。在首次重置发生之前，它显示 `Never`。

## 重置占位符

你可以通过设置 `reset-mode` 和 `reset-time` 选项来重置占位符，也可以使用 `/shop resetrandomplaceholder` 命令。&#x20;

### 重置模式

支持以下重置模式：

* **NEVER**
* **ONCE**：占位符每次被使用都会重置，因此不能用于价格。玩家打开商店时看到的价格和实际成交结果各计算一次，两次结果无法同步，也就是说玩家在商店里看到的价格与他实际支付的并不相同。
* **TIMER**：在你指定的时长之后重置，例如 5 小时后。
* **TIMED**：在指定的时刻重置，例如晚上 8:15。
* **CUSTOM**：直接在重置时间中填写重置时间，插件不做任何计算。建议通过 PlaceholderAPI 的结果获取重置时间，并且需要在 `time-format` 选项中设置时间格式，好让插件知道你的 PlaceholderAPI 结果长什么样。&#x20;

随机占位符被使用后，我们会先生成一次重置时间。CUSTOM 目标会在查询占位符或其刷新时间时重新计算，目标发生变化时还会更新对应的池化任务。

<mark style="color:red;">**不要**</mark>在这里使用 `COOLDOWN_TIMER`、`COOLDOWN_TIMED` 或 `COOLDOWN_CUSTOM` 重置模式，随机占位符不支持它们。

### 重置时间

不同的重置模式需要在这里填写不同的值。支持使用占位符，<mark style="color:red;">**但这里使用的占位符必须取自全服，也就是说所有玩家得到的结果相同。**</mark>

#### NEVER

此处不需要填写任何内容。

#### TIMER

这里可以输入 3 到 5 个数字，数字之间用 `:` 分隔，例如 `15:00:00`。

从**右**往**左**，每个数字依次代表：

* 秒
* 分
* 时
* 天
* 月&#x20;

上例表示 15 小时之后。也就是说：**如果当前时间是 2023-09-04 12:00:00，那么将在 15 小时后重置，即 2023-09-05 03:00:00。**

#### TIMED

TIMED 与 TIMER 的写法几乎相同，只是从右边数起的前三位数字代表一天中的时刻。同样以 `15:00:00` 为例：

如果当前时间是 2023-09-04 12:00:00，则会在 2023-09-04 15:00:00 重置。

这是天数设为 0 时的结果。如果设为 1，就会再往后加一天，仅此而已。

要做每日商店，**天数**应设为 0；要做每周商店，**天数**应设为 6。因为次数上限要在最后一天重置，而不是在最后一天的第二天，对吧？

该类重置模式还支持设置多个重置时间，多个时间之间用 `;;` 分隔，插件会取最早的那个。例如：&#x20;

```yaml
reset-mode: 'TIMED'
reset-time: '20:00:00;;19:00:00'
```

上例中，该随机占位符会在每天 19:00 和 20:00 重置。

#### CUSTOM

这里只需填入一个 PlaceholderAPI 占位符，其结果必须包含完整的年、月、日、时、分、秒。你还需要在 `time-format` 选项中填写它的格式。同一个 CUSTOM 目标只会执行一次，`lastResetTime` 会被持久化保存，因此重启后不会重复执行。

### Cron 重置（每周重置 / 每月重置）&#x20;

重置时间可以使用 Cron 格式。&#x20;

* 将重置模式设为 `CUSTOM`。
* 在重置时间中使用 `{cron_"<Cron Expression>"}` 内置占位符，不要漏掉 `"` 符号。`<Cron Expression>` 需使用 **Quartz** 格式。

例如：

```yaml
reset-mode: 'CUSTOM'
reset-time: '{cron_"0 0 0 ? * 5}'
time-format: 'yyyy-MM-dd HH:mm:ss'
```

你可以让 ChatGPT 帮你生成所需的 Cron 表达式，也可以使用[这个工具](https://freeformatter.com/cron-expression-generator-quartz.html)。上例中的 Cron 表达式表示每周四 0:00 重置。我们不提供任何关于如何编写 Cron 表达式的帮助。

{% hint style="info" %}
你**必须**确保 Cron 占位符结果的时间格式（在 `config.yml` 文件中设置）与这里设置的 `time-format` 一致。默认情况下二者本来就是一致的。
{% endhint %}

## 测试

使用命令 `/shop getplaceholdvalue {random times_<placeholdID>}` 可以查看随机占位符的重置时间；使用 `/shop getplaceplaceervalue {random_<placeholdID>}` 可以查看随机占位符的当前值，借此对比重置前后的值是否相同。

## 示例：随机价格

### 创建新的随机占位符

本例在 `random_placeholder` 文件夹下新建一个名为 `price.yml` 的随机占位符配置。

```yaml
reset-mode: TIMED
reset-time: '00:00:00'
element-sort: true
element-amount: 50 
elements:
# Random number from 5 to 100.
  - '5~100'
  - '4~40'
  - '53~530'
  - '32~140'
  - '55~140'
```

### 在商品配置中设置动态值

在任意商品的价格配置中使用 `{random_price}` 占位符。比如我在这个商品中加上它：

```yaml
items:
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: coal
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '{random_price;;1}' # <--- Changed line
        placeholder: '&6{amount} Coins'
        start-apply: 0
```
