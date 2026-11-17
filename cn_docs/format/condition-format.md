# ⚖️条件格式

条件格式由若干选项组成。

{% hint style="info" %}
示例中的 `conditions` 只表示「**条件格式**从这里开始」。具体该用什么选项名，请查看对应功能的页面说明，例如 `buy-conditions`。
{% endhint %}

## 常规选项

#### 生效次数（Apply Times）

只有当玩家对该商品的购买/出售次数落在指定区间时，才会检查这个条件。&#x20;

* start-apply：从第几次开始生效。**可选，默认 0。**
* end-apply：到第几次为止生效。**可选，默认无限。**
* apply：指定在哪些次数生效，格式 `[1,2,3,4]`。**可选，默认取 start-apply 的值。**

```yaml
    conditions:
      1:
        apply: [1,2,3,4,5]
        start-apply: 1
        end-apply: 5
```

#### 点击方式（Click Type）

只有当玩家以指定的点击方式操作按钮时，才检查这个条件。&#x20;

```yaml
    conditions:
      1:
        click-type: LEFT
```

#### 动作（Actions） <mark style="color:red;">- 付费版</mark>

{% hint style="info" %}
如果想改商品买卖条件不满足时的提示，应该在商品配置里用 `fail-actions`。
{% endhint %}

```yaml
    conditions:
      1:
        not-meet-actions:
          1: 
            type: message
            message: 'Condition not meet'
        meet-actions:
          1: 
            type: message
            message: 'Condition meet'
```

也可以给整个条件组设置动作，写法如下：

```yaml
conditions:
  not-meet-actions:
      1: 
        type: message
        message: 'You must use this apply item in world!'
  1:
    type: world
    world: 'test'
```

## 可用占位符

* {world}
* {amount}

### 物品层级 `buy-conditions` 和 `sell-conditions`

在物品本身使用 `buy-conditions` 或 `sell-conditions` 时，`{amount}` 表示玩家这次想买/卖几份。

玩家买 5 份，`{amount}` 就是 `5`。

### 单项的 `apply-conditions` 与旧版 `conditions`

在单项上使用 `apply-conditions` 时，`{amount}` 恒为 `1`。

旧版 `conditions` 也是如此，因为它在代码里就是 `apply-conditions` 的老形式。

为什么？

* `apply-conditions` 是在插件挑定最终单项之前检查的
* 这个阶段真正的计算结果还没出来

所以在这一层：

* `{amount}` 不代表最终价格
* `{amount}` 也不代表最终的商品数量
* `{amount}` 只是 `1`

所以 `apply-conditions` 是用来挑分支的，不是用来校验算出来的数量的。

### 单项的 `require-conditions`

在单项上使用 `require-conditions` 时，`{amount}` 表示该单项最终算出的数量。



这正是它与 `apply-conditions` 最关键的区别。

* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}
* {item} - 商品 ID
* {item-name} - 商品显示名称
* {shop} - 商店 ID
* {shop-name} - 商店显示名称
* {shop-menu} - 商店菜单 ID

## 世界

玩家必须处于指定世界。

<pre class="language-yaml"><code class="lang-yaml"><strong>  conditions:
</strong>    1:
      type: world
      world: lobby
</code></pre>

## 生物群系

玩家必须处于指定生物群系中。

```yaml
  conditions:
    1:
      type: biome
      biome: oraxen
```

## 权限

玩家必须拥有该权限。

**注意：OP 默认拥有所有权限，所以想测试这个条件，得先把自己 deop。**

```yaml
  conditions:
    1:
      type: permission
      permission: 'group.vip'
```

## 占位符

玩家必须满足该占位符的条件。

rule 可取值：

* \>=
* <=
* \>
* <
* \==（字符串）
* \=（数字）
* !=（数字或字符串）
* !\*=（数字或字符串）不包含。
* \*=（字符串）包含。例如 str \*= string 为 true，而 example \*= ple 为 false。

```yaml
  conditions:
    1:
      type: placeholder
      placeholder: '%player_health%'
      rule: '<='
      value: 5
```

## 菜单

玩家当前打开的是指定菜单。

```yaml
  conditions:
    1:
      type: menu
      menu: 'example-shop-menu'
```

## 菜单类型

玩家当前打开的是指定类型的菜单。

支持的菜单类型：

* COMMON
* SHOP
* MORE
* SEARCH
* FAVOURITE

```yaml
  conditions:
    1:
      type: menu_type
      menu-type: 'favourite'
```

## 任意满足（any）<mark style="color:red;">- 付费版</mark>

```yaml
  conditions:
    1:
      type: any
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
        2:
          type: placeholder
          placeholder: '%player_points%'
          rule: '>='
          value: 400
```

## 取反（not）<mark style="color:red;">- 付费版</mark>

```yaml
  conditions:
    1:
      type: not
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
```
