# 🛒全部出售菜单

全部出售菜单是一个全局物品栏界面：玩家把要卖的物品放进去后，**关闭界面即自动结算出售**，无需额外点击确认按钮。如果你需要「先预览总价再手动确认」的流程，请使用[物品出售菜单](item-sell-menus.md)。

<a id="open-the-menu"></a>

## 打开界面

玩家可通过 `/shop sellall` 命令（需要 `ultimateshop.sellall` 权限）打开，也可以在任意菜单按钮中配置 `sell_all_menu` 动作：

```yaml
actions:
  1:
    type: sell_all_menu
```

即使从 Bedrock 表单、原生对话框、Ore 或 ArcMenu 中触发该动作，打开的依然是实际物品栏界面。

## 配置界面

全部出售菜单直接在 `config.yml` 的 `menu.sell-all` 下配置，不需要在 `menus/` 目录创建独立文件：

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

| `menu.sell-all` 选项 | 用途 |
| --- | --- |
| `size` | 物品栏槽位数：`9`、`18`、`27`、`36`、`45`、`54` |
| `title` | 界面标题，`{lang}` 会自动读取当前语言文件 |
| `black-slots` | 禁止放入物品的槽位编号列表（从 `0` 开始，如 `[0, 1, 2]`） |

该界面没有独立的菜单 ID，也不支持普通 `layout`、`buttons`、图形样式或 `force-ui` 切换。

## 排除特定商品

在 `config.yml` 中可以通过 `商店ID;;商品ID` 的格式将特定商品排除在全部出售之外：

```yaml
sell:
  sell-all:
    ignore-items:
      - 'farming;;A'
```

价格与出售条件的基础规则见[商品配置](../shops/products.md)。

## 关闭时结算逻辑

玩家关闭窗口后，插件会自动扫描所有符合条件的商店商品，对放入的物品执行正常的批量出售流程（商品条件、限额、排除列表与出售倍率均照常生效）。此外，全局 `sell.max-amount` 控制出售引擎单次处理的上限，与物品栏容量以及物品出售菜单的 `max-sell-amount` 相互独立。

无法出售或超出限额的剩余物品会返还到玩家背包（背包已满时掉落在地面）；空窗口关闭不会触发任何奖励。如果某商品开启了需要检查实际物品的价格修正器，则会交由配置的物品出售流程处理。

[物品出售菜单](item-sell-menus.md) · [物品栏 UI](inventory-ui.md)
