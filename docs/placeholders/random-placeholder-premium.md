# 🎲Random Placeholder - Premium

We added `{random}` built-in placeholder in premium version. You can use random placeholder at the options that support the use of placeholders to randomly select the value. For example, random price amount, random reset time, random stock, etc.

## Config

All random placeholder configs are stored in `random_placeholders` folder. The file name is it's ID, for example: `rotate.yml` means it's ID is `rotate`. An example of it's config is like below:

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

* per-player-element: If set to `false`, all players will use the same value output by random placeholder. For example, player 1 will get **A**, and player 2 will get the same value, even if the player reaches 100 million, the value will be the same. If set to `true`, the values output by each player will be different from each other, and conditions can be set for elements. If an element does not meet the conditions, the player will never be able to extract it.

{% hint style="info" %}
This option is <mark style="color:red;">**NOT**</mark> recommended to be changed after enabling this random placeholder, as it may cause the plugin to detect errors (such as a per player random placeholder appearing in the global database) and output a prompt in the console that cannot be blocked.\
The random placeholder of per player cannot be applied to global server scenarios, and vice versa. Random placeholders without per player enabled cannot be applied to per player scenarios. For example, when using the `resetrandomplaceholder` command, the per player's random placeholder must enter the player name in the command parameters, while the per player's random placeholder cannot enter the player name in the command parameters, otherwise the plugin will prompt an error.
{% endhint %}

* element-sort: If set to `false`, the result of the random placeholder will be out of order. For example, if you set `elements` to `A, B, C, D, E` and the randomly generated result is `B, D`, the result of the random placeholder may be `B, D`, or `D, B`. However, if set to `true`, the result will be output strictly in the order of elements, and the output result can only be `B, D`. **(Added in 3.12.2)**
* reset-mode/reset-time: Please view below to know.
* element-amount: The amount of the element will picked in this placeholder. **(Added in 3.1.0)**
* elements: The random element what placeholder will picked.&#x20;

There are two ways to express `elements`.&#x20;

The first is to write all elements in the form of a list, where the rate of each element is equal and without any conditions. **Support use \~ symbol means pick random number, for example, 5\~100 means pick one random number from 5 to 100.**

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

The second is to use the form of subsections, where each element has options for `rate` and `conditions` to fill in. The `conditions` option is not required and only support when `per-player-element` option being set to true. **(Added in 3.12.0)**

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

## Use Placeholder

Use `{random_<ID>;;<Number>}` placeholder to display it's value, like `{random_daily;;2}` will query `daily` random placeholder's **second** element picked. For more info, please view [Placeholders](built-in-placeholders.md) page. For example of this placeholder usage, please view [Daily Shop](../shops/example-daily-shop-rotating-shop.md) page.

Use `{random_times_<ID>}` placeholder to display the reset time of the placeholder, like `{random_times_daily}`.

## Reset Placeholder

You can reset placeholder by setting `reset-mode` and `reset-time` option or use `/shop resetrandomplaceholder` command.&#x20;

### Reset Mode

Supports below reset mode:

* **NEVER**
* **ONCE**: Each time the placeholer is used, it will reset and is not able to use in the price, as the price seen by the player opening the shop and the actual transaction result are calculated twice, so you cannot achieve price synchronization which means price player seen in shop will not same as the price player actually cost.
* **TIMER**: It will reset after the time you specify, for example, after 5 hours.
* **TIMED**: It will be reset at the corresponding time, such as 8:15 pm.
* **CUSTOM**: Directly enter the reset time in reset time, and the plugin will not perform any calculations. Recommend obtain reset time through the Placeholder API results. You need set time format at `reset-time-format`  option to helps us know how does your PlaceholderAPI results be like.&#x20;

We will first generate reset time after random placeholder be used once. The reset time will not automatically adjust based on configuration updates. If you set the reset time incorrectly, you will need to delete the corresponding data.

Do <mark style="color:red;">**NOT**</mark> use `COOLDOWN_TIMER/COOLDOWN_TIMED/COOLDOWN_CUSTOM` reset mode here, they will not work in random placeholder, and since random placeholder data is always saved in server, so random placeholder's `TIMER/TIMED/CUSTOM` effect is same as product config's `CUSTOM_TIMER/CUSTOM_TIMED/COOLDOWN_CUSTOM` reset mode.

### Reset Time

Different reset modes require different values to be filled in here. Supports placeholders, <mark style="color:red;">**the placeholder used here must be on the server side, which means that all players receive the same value.**</mark>

#### NEVER

Don't need anything here.

#### TIMER

You can enter 3 to 5 numbers here, separated by a `:` symbol between each number. For example: `15:00:00`.

Each number from **right** to **left** represents:

* Seconds
* Minutes
* Hours
* Days
* Months&#x20;

In this example, represents 15 hours later. Which means: **if now time is 2023-09-04 12:00:00. Will reset after 15 hours, which means 2023-09-05 03:00:00.**

#### TIMED

The composition of TIMED and TIMER is almost identical, but the first three digits from the right-hand side represent the time of day. Let's also take 15:00:00 as an example:

If now time is 2023-09-04 12:00:00, will reset at 2023-09-04 15:00:00.

This is the result obtained with days set to 0. If you set it to 1, we will add another day, and that's it.

If you want to do a daily shop, **days** should be set to 0, and if you want to do a weekly shop, **days** should be set to 6. Because you need to reset the number of times on the last day, not on the second day after the last day, right?

This type of reset mode also supports set multi reset time, each reset time use `;;` to splite, we will pick up the earliest reset time. For example:&#x20;

```yaml
reset-mode: 'TIMED'
reset-time: '20:00:00;;19:00:00'
```

In this example, this product or random placeholder will reset reset at 19:00 and 20:00 every day.

#### CUSTOM

You only need to enter a Placeholder API placeholder here, and the result of the placeholder must include the complete year, month, day, hour, minute, and second. You also need to enter their format in the reset time format option, because different types of placeholders return different time formats, making it difficult for plugins to achieve uniformity.&#x20;

### Cron Reset (Weekly Reset/Monthly Reset)&#x20;

You can use Cron format in reset time.&#x20;

* Set reset mode to `CUSTOM`.
* Use `{cron_"<Cron Expression>"}` built-in placeholder in reset time. Don't miss out the `"` symbol. The `<Cron Expression>`  should use **Quartz** format.

For example:

```yaml
reset-mode: 'CUSTOM'
reset-time: '{cron_"0 0 0 ? * 5}'
```

You can obtain the Cron expression you want by asking ChatGPT or use [this tool](https://freeformatter.com/cron-expression-generator-quartz.html). For example, the Cron expression in this example means to reset at 0:00 every Thursday. We do not provide any help related to how to write Cron expression.

{% hint style="info" %}
You **MUST** make sure that time format of the result of Cron placeholder (set it in `config.yml` file) and the time format you set here is same. By default, they are same.
{% endhint %}

## Testing

You can know about the reset time of the random placeholder by using the command `/shop getplaceholdvalue {random times_<placeholdID>}`. And by using `/shop getplaceplaceervalue {random_<placeholdID>}` to know the current value of the random placeholder, you can compare whether the value before reset is the same after reset by this way.

## Example: Random Price

### Create new random placeholder

In this example, we create a new random placeholder config called `price.yml` at `random_placeholder` folder.

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

### Set dynamic value in your product configs

Use `{random_price}` placeholder at any product's price config. Like I add this placeholder in this product:

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
