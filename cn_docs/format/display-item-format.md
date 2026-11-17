# 🖼️展示物品格式

展示物品格式可用于菜单按钮，以及商店商品配置中的 `display-item` 选项。

## 单个展示物品

如果只有一个展示物品、也不需要按条件切换，直接在这一段写[物品格式](itemformat-tm/)即可。

```yaml
  B:
    display-item:
      material: BREAD
      name: '&cSuper Bread'
```

## 多个展示物品&#x20;

如果想给按钮或商品配置多个展示物品，并借助条件系统决定实际显示哪一个，按下面的新格式来写即可：

```yaml
  1:
    display-item:
      default:
        material: GREEN_WOOL
        amount: 1
        name: '&eDay 1'
        lore:
          - '&fToday Reward:'
          - '&7  - 10 Gems'
          - ''
          - '&#FFFACDClick to claim!'
      claimed:
        material: RED_WOOL
        amount: 1
        name: '&eDay 1'
        lore:
          - '&fIncluded Reward:'
          - '&7  - 10 Gems'
          - ''
          - '&#ff3300Already claimed!'
    display-item-conditions:
      claimed: # Condition ID
        1: # Means first condition
          type: placeholder
          placeholder: '%ultimateshop_streak_A_{buy-times-player}%'
          rule: '>'
          value: '0'
        2: # You can add more condition with Condition Format
          type: world
          world: 'testCondition'
      default:
        1:
          type: placeholder
          placeholder: '%ultimateshop_streak_A_{buy-times-player}%'
          rule: '='
          value: '0'z
```
