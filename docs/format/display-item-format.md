# 🖼️Display Item Format

You can use display item format in menu button and shop product's `display-item` option.

## Single

If your display item is only one and does not have any condition to display it, simply use [Item Format](itemformat-tm/) in this section!

```yaml
  B:
    display-item:
      material: BREAD
      name: '&cSuper Bread'
```

## Multi&#x20;

If you want set multi display item for this button or product and use condition system to make us choose one of it to display, simply use this new format as below:

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
