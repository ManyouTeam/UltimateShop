# 📦物品栏 UI

物品栏界面在 Java 与 Bedrock 上显示箱子式槽位，读取菜单根级 `size`、`layout` 和按钮。图形 `ui.layout`、`ui.placement` 不影响物品栏位置。

## 选择物品栏界面

对于单个菜单，在根级关闭其他呈现方式：

```yaml
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
```

对于全部菜单，在 `config.yml` 中设置：

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

全局强制设置优先于菜单自身开关，关闭后恢复正常选择。请求的呈现方式不可用时，物品栏也是标准后备界面。

## 配置行与条目

保存为 `menus/common_menus/example.yml`：

```yaml
title: '&8Example'
size: 18
layout:
  - '000000000'
  - '0000X0000'
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

选择物品栏后，重载并执行 `/shop menu example`。每行对应九个槽位，支持的大小为 `9`、`18`、`27`、`36`、`45`、`54`。未定义的 ID 留空，`0` 可定义为装饰填充物，多字符 ID 使用反引号。见[布局与按钮](layout-and-buttons.md)。

## 点击、可见性与刷新

普通按钮在点击条件通过后执行 `actions`。商店商品使用配置的商品点击操作。`menu-visibility.inventory: false` 只在物品栏中隐藏条目；付费版还可通过动态布局与显示条件按玩家选择条目。

`config.yml` 中的全局刷新选项：

```yaml
menu:
  menu-update:
    circle-update: false
    click-update: false
  title-update:
    enabled: false
    circle-update: false
    click-update: true
    resend-items-pack: false
```

`circle-update` 每秒刷新，`click-update` 在点击后刷新。只有占位符变化需要定期更新时才启用周期刷新。标题更新为付费版功能，并需要 PacketEvents。物品栏点击和重新打开冷却分别为 `menu.cooldown.click`、`menu.cooldown.reopen`，单位是游戏刻，内置值为 `-1`。

## 需要实际物品栏的界面

[物品出售](item-sell-menus.md)接收实际物品并要求确认出售；[全部出售](sell-all-menu.md)会在关闭时出售放入的物品。二者不受全局呈现方式影响，始终使用物品栏。[批量购买](buy-more-menus.md)则使用自身数量控件和商品预览。

[UI 模式](ui-overview.md) · [展示物品的附加描述](display-item-add-lore.md)
