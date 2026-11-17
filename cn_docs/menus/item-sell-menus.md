# 💰物品出售菜单

此菜单接收实际物品，预览出售价值，并等待玩家明确确认。未确认就关闭时会返还物品。如果希望关闭时直接出售，请使用[全部出售](sell-all-menu.md)。

文件保存在 `menus/item_sell_menus/`。使用 `/shop menu item-sell` 或 `open_menu` 打开内置菜单。目录自动确定类型；`common_menus` 中的文件也可设置根级 `menu-type: item-sell`（别名 `item_sell`）。

<a id="example"></a>

## 创建物品投放菜单

```yaml
title: 'Sell Items'
size: 54
max-sell-amount: -1
layout:
  - '000000000'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '000000000'
  - '0000C0X00'
input-item: 'I'
confirm-item: 'C'
confirm-button:
  display-item:
    material: EMERALD
    name: '&aConfirm Sale'
    lore:
      - '&7Price: &f{price}'
      - '&7Original price: &f{original-price}'
      - '&7Multiplier: &fx{multiplier}'
      - '&7Sellable items: &f{item-amount}'
      - '&7Unsellable items: &f{unsellable-amount}'
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

保存为 `menus/item_sell_menus/my-sell.yml`，重载后执行 `/shop menu my-sell`。`I` 为物品投放槽位，`C` 为确认按钮。未定义的 `0` 槽位虽然留空，但不接受待售物品。

## 配置参考

| 配置键 | 用途 |
| --- | --- |
| `input-item` | 主要输入标记，默认 `I` |
| `input-items` | 额外输入标记，与 `input-item` 合并 |
| `confirm-item` | 确认标记，默认 `C`，可出现在多个槽位 |
| `confirm-button.display-item` | 确认图标、名称与描述 |
| `max-sell-amount` | 每次确认时，全部输入合计出售的最大交易单位数；`-1` 为无限制 |

支持普通标题、布局、按钮、打开条件和生命周期动作。多字符标记遵循[常规布局规则](layout-and-buttons.md)。内置确认控件使用 `confirm-button`，不同于批量购买的 `confirm-items`。

## 价格预览与确认

| 确认按钮占位符 | 含义 |
| --- | --- |
| `{price}` | 应用出售倍率和按实际物品计算的修正后，最终获得的奖励 |
| `{original-price}` | 应用这些倍率前的奖励 |
| `{multiplier}` | 合并后的有效倍率 |
| `{item-amount}` | 可出售数量 |
| `{unsellable-amount}` | 当前不可出售数量 |

预览随放入物品更新。确认后执行正常商品出售流程，包括条件、限额、事件、动作和交易日志。未售出的物品会保留，并在关闭时返还。菜单图标不是玩家待售物品。

`max-sell-amount` 限制本菜单单次确认的合计数量，不会覆盖商品库存、条件或其他出售限制。

## 将需要检查实际物品的商品转到此菜单

使用 `price-modifier: true` 的商品需要通过物品出售菜单，让定价读取实际放入的物品。在 `config.yml` 中配置目标：

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

这类商品的 `sell` 和 `sell-all` 会转到指定菜单，目标 ID 必须存在。表单/对话框列表会先展示商品信息，让玩家选择购买或出售；转入出售后打开实际物品栏。修正器由商品定义，菜单负责输入与确认。

## 呈现方式支持

所有客户端的物品出售菜单均只使用物品栏，不包含表单、对话框、Ore 或 ArcMenu 布局，也不继承图形 `menu.ui`。全局强制 UI 会保留此实际物品投放界面。

[价格修正器](../shops/price-modifier.md) · [出售倍率](../shops/sell-multiplier-premium.md) · [物品栏 UI](inventory-ui.md)
