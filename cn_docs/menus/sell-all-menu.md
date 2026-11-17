# 🛒全部出售菜单

这是一个全局物品栏界面。玩家放入物品后，**关闭窗口即出售**，没有独立确认按钮。若需要价格预览和确认流程，请使用[物品出售菜单](item-sell-menus.md)。

<a id="open-the-menu"></a>

## 打开界面

使用 `/shop sellall`，需要 `ultimateshop.sellall` 权限；也可以给菜单按钮添加以下动作：

```yaml
actions:
  1:
    type: sell_all_menu
```

从表单、对话框、Ore 或 ArcMenu 打开时，仍会创建实际物品栏。

## 配置界面

在 `config.yml` 中设置：

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

| `menu.sell-all` 下的配置键 | 用途 |
| --- | --- |
| `size` | 槽位数：`9`、`18`、`27`、`36`、`45`、`54` |
| `title` | 标题，`{lang}` 使用对应语言覆盖 |
| `black-slots` | 禁止放入物品的槽位，从零编号，例如 `[0, 1, 2]` |

此界面在 `menus/` 中没有独立文件，不使用普通布局/按钮配置，也没有独立菜单 ID。图形样式和强制呈现方式不会改变它。

## 排除商品

在 `config.yml` 中通过 `shop-id;;product-id` 排除商店商品：

```yaml
sell:
  sell-all:
    ignore-items:
      - 'farming;;A'
```

这会从全部出售流程中排除对应商品引用。价格与出售要求见[商品配置](../shops/products.md)。

## 关闭时出售

关闭菜单后，插件扫描符合要求的商品，并对放入物品执行正常出售。商品条件、限额、排除项和适用倍率仍然生效。全局 `sell.max-amount` 限制出售引擎的处理数量，与物品栏容量、物品出售菜单的 `max-sell-amount` 是独立设置。

未售出物品返还玩家，放不下时掉落。空菜单不会产生奖励。需要检查实际物品来修正价格的商品，会使用配置的物品出售流程。

[物品出售](item-sell-menus.md) · [物品栏 UI](inventory-ui.md)
