# 🗯️自定义占位符



{% hint style="info" %}
该功能在**4.5.26**版本中添加，目前仍在**BETA**测试中。
{% endhint %}

{% hint style="info" %}
免费版最多可创建 **5** 个自定义占位符，付费版没有数量限制。
{% endhint %}

自定义占位符的主要目的是记录所需信息，例如销售相同材料的数量或收入。只需在适当的地方使用命令修改占位符值即可达到预期效果。

## 配置

所有自定义占位符配置都存储在`placeholders/custom_placeholders` 文件夹中。文件名是ID，例如：`example.yml`表示它是ID是`example`。其配置示例如下：

```yaml
type: number
default-value: "1"
per-player-value: true
```

* 类型：支持值：`default`或`number`。如果只想记录数值数据，则设置为`number`；否则，设置为`default`。
* default-value：如果没有为此占位符设置值，将使用此处设置的默认值。
* per-player-value：如果设置为`true`，则每个玩家都有独立的占位符值；否则，所有玩家共享相同的占位符值。

## 使用占位符

使用`{custom_<ID>}` 占位符显示其值。欲了解更多信息，请查看[占位符](built-in-placeholders.md)页面。例如：`{custom_example}`

## 设置占位符值

需要通过命令设置自定义占位符的值。

* /shop setcustomplaceholder \<ID> \<value> \[playerName] \
  如果`per-player-result`设置为`true`，则`playerName`为必填参数；否则，不应输入playerName参数。
* /shop addcustomplaceholder \<ID> \<value> \[playerName]\
  仅自定义工号类型占位符。它将被添加到占位符的当前值中。例如，如果当前值为 1，此处输入的值参数为 5，则最终的占位符值将替换为 6。
