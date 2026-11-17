# 🪄出售棒 - 付费版

{% hint style="info" %}
无法计算出售棒的累计收益，因为 UltimateShop 支持单件商品包含多种价格类型，并非简单地将一个物品堆叠对应到一个 `double` 类型的 **Vault** 金额。如果只有单一金额，直接相加即可；但本插件还支持自定义虚拟价格等，因此很难计算总收入。

出售棒支持动态价格。例如基础出售价格为 10，每出售一次下降 1，则出售多个商品时依次计算为 10 + 9 + 8 + 7 + 6 + 5……。
{% endhint %}

所有出售棒配置保存在 `sell_sticks` 目录中，文件名即为 ID。例如 `A.yml` 的 ID 是 `A`。配置示例：

```yaml
display-item:
  material: STICK
  name: '&dSell Stick &7(5 times)'
  lore:
    - '&fRight click a chest to use this item!'
    - ''
    - '&cLeft usages: {times}'

usage-times: 5

multiplier: 1.2

actions:
  1:
    type: sound
    sound: 'block.note_block.pling'

conditions: []
```

* `display-item`：出售棒的展示物品，使用[物品格式](../format/itemformat-tm/)。
* `useage-times`：最大使用次数。未设置或小于 0 时，表示无限次使用。
* `multiplier`：出售倍率。
* `actions`：使用后执行的动作，使用[动作格式](../format/action-format.md)。
* `conditions`：玩家使用时必须满足的条件，使用[条件格式](../format/condition-format.md)。

配置完成后重载插件，通过 `/shop givesellstick` 获取出售棒，详见[这里](../info/commands-and-permissions.md#shop-givesellstick-less-than-itemid-greater-than-less-than-playerid-greater-than-amount-premium-vers)。

UltimateShop 支持阻止玩家在保护区或其他玩家领地内使用出售棒，OP 不受此检查限制。支持的保护插件见[这里](../info/compatibility.md#directly-supported-protection-plugins-list-premium)。权限 `ultimateshop.bypass.protection` 可跳过出售棒的保护检查。
