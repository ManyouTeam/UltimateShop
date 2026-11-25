# 💰物品出售菜单

物品出售菜单让玩家将背包物品放入界面、实时预览出售总价，并在点击确认按钮后完成出售；如果未点击确认就关闭界面，放入的物品会全数返还。若希望玩家「关闭窗口即自动出售」，请使用[全部出售菜单](sell-all-menu.md)。

文件存放在 `menus/item_sell_menus/`（放入该目录即自动识别类型），可通过 `/shop menu item-sell` 或 `open_menu` 动作打开内置菜单。放在 `common_menus` 中的旧版文件也可在根级设置 `menu-type: item-sell`（别名 `item_sell`）。

<a id="example"></a>

## 创建物品出售菜单

将以下配置保存为 `menus/item_sell_menus/my-sell.yml`，重载后执行 `/shop menu my-sell` 即可打开：

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

布局中 `I` 为可放入待售物品的槽位，`C` 为确认出售按钮；未定义按钮的 `0` 槽位虽然显示为空，但不允许放入物品。

## 配置参考

| 配置键 | 用途 |
| --- | --- |
| `input-item` | 主物品投放槽位标记（默认 `'I'`） |
| `input-items` | 额外投放槽位标记（会与 `input-item` 合并） |
| `confirm-item` | 确认按钮槽位标记（默认 `'C'`，可占用多个槽位） |
| `confirm-button.display-item` | 确认按钮的图标、名称与描述 |
| `max-sell-amount` | 单次点击确认最多合计出售的交易单位数（`-1` 为不限制） |

该菜单支持常规标题、布局、普通按钮、打开条件与生命周期动作，多字符 ID 遵循[布局与按钮](layout-and-buttons.md)规则。注意此处确认按钮配置键为 `confirm-button`，与批量购买菜单的 `confirm-items` 不同。

## 价格预览与确认机制

确认按钮的 Lore 支持以下实时预览占位符：

| 占位符 | 含义 |
| --- | --- |
| `{price}` | 应用出售倍率与按实际物品计算的修正后，最终可获得的收益 |
| `{original-price}` | 应用倍率与修正前的原始收益 |
| `{multiplier}` | 合并计算后的有效出售倍率 |
| `{item-amount}` | 当前可出售的物品数量 |
| `{unsellable-amount}` | 当前不可出售的物品数量 |

玩家放入或取出物品时预览数值会实时更新；点击确认后，系统会执行完整的商品出售流程（包含条件校验、限额、事件、动作与交易日志），未售出的物品会留在槽位并在关闭界面时返还（界面自身的装饰图标不会被误当成待售物品）。

注意，`max-sell-amount` 仅限制本菜单单次确认的合计数量，不会覆盖商品自身的库存、限额或其它出售条件。

## 将需要检查实际物品的商品转到此菜单

对于开启了 `price-modifier: true` 的商品，插件需要读取玩家实际交出的物品来计算修正价格，因此必须在 `config.yml` 中指定将这类出售转发到物品出售菜单：

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

启用后，这类商品的 `sell` 与 `sell-all` 操作会自动打开指定的物品出售菜单（目标菜单 ID 必须存在）。在 Bedrock 表单或原生对话框中，玩家会先看到商品信息并选择买/卖，选择出售后同样会打开实际物品栏界面完成投放与确认。

## 呈现方式支持

为了支持玩家实际放入待售物品，物品出售菜单在**所有客户端**下都仅使用原版物品栏界面，不提供表单、对话框、Ore 或 ArcMenu 版本，也不继承图形 `menu.ui`（全局 `menu.force-ui` 同样会保留此物品栏界面）。

[价格修正器](../shops/price-modifier.md) · [出售倍率](../shops/sell-multiplier-premium.md) · [物品栏 UI](inventory-ui.md)
