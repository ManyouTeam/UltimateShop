# ⛓️条件占位符 - 付费版

我们在插件中添加了 `{conditional}` 内置占位符。

## 配置

所有条件占位符配置都放在 `placeholders/conditional_placeholders` 文件夹里，文件名就是它的 ID——例如 `buy.yml` 的 ID 就是 `buy`。配置示例如下：

```yaml
mode: MAX

value:
  default: 1
  vip: 1.5
  mvp: 2

conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
  mvp:
    1:
      type: permission
      permission: 'group.mvp'
```

* mode：条件占位符的模式。
  * DEFAULT：基础模式。从上往下依次检查玩家是否满足对应条件，一旦满足就立刻返回该值。
  * MAX：要求所有值都是数字，返回满足条件的值当中最大的那个。
  * MIN：与 MAX 类似，但返回其中最小的那个。
* conditions：每个值对应的条件，写法见[条件格式](../format/condition-format.md)。
* value：各条件对应的值。其中 `default` 是所有条件都不满足时返回的值。

## 使用占位符

用 `{conditional_<ID>}` 显示它的值，详见[占位符](built-in-placeholders.md)页面。例如 `{conditional_buy}`<br>

<a id="example-conditional-product"></a>

## 示例：按条件切换商品

* 新建一个条件占位符，内容如下：

```yaml
mode: DEFAULT

value:
  default: A # Product ID
  vip: B # Product ID
  mvp: C # Product ID

conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
  mvp:
    1:
      type: permission
      permission: 'group.mvp'
```

* 确保商店里有 ID 为 `A`、`B`、`C` 的商品。
* 打开该商店的菜单配置，找到 `layout` 选项：

```yaml
dynamic-layout: true

layout:
  - '000000000'
  - '000`{conditional_yourPlaceholderName}``{conditional_yourPlaceholderName}``{conditional_yourPlaceholderName}`000'
  - '000000000'
  - 'a0003000b'
```

把 `yourPlaceholderName` 换成你所用条件占位符的文件名。
