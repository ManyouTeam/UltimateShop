# 📦物品栏 UI

物品栏界面在 Java 与 Bedrock 客户端上均以标准的箱子格子展示，直接读取菜单根级的 `size`、`layout` 与 `buttons`（图形 `ui.layout` 与 `ui.placement` 不会影响物品栏排版）。

## 切换到物品栏界面

如果只想让**单个菜单**使用物品栏，在菜单根级关闭其他呈现方式即可：

```yaml
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
```

如果想让**全服所有菜单**统一使用物品栏，可在 `config.yml` 中启用强制 UI：

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

`force-ui` 优先级高于菜单自身开关，关闭后即恢复正常选择。此外，当其他呈现方式不可用时，物品栏也是默认的后备界面。

## 配置行与条目

将以下示例保存为 `menus/common_menus/example.yml`，重载后执行 `/shop menu example` 即可打开：

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

`layout` 每行固定 9 个槽位，`size` 必须为 `9`、`18`、`27`、`36`、`45` 或 `54`。未定义的 ID 留空，`0` 也可定义为装饰填充按钮，多字符 ID 使用反引号包裹，详见[布局与按钮](layout-and-buttons.md)。

<a id="clicks-visibility-and-refresh"></a>

## 点击、可见性与刷新

普通按钮在点击条件通过后执行 `actions`，商店商品则执行配置的商品点击操作。若只想在物品栏中隐藏某个装饰条目，可设置 `menu-visibility.inventory: false`；付费版还支持通过 `dynamic-layout` 与 `display-conditions` 按玩家动态切换条目。

在 `config.yml` 中配置全局刷新与标题更新：

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

* `circle-update` 每秒定时刷新一次，`click-update` 在玩家点击后刷新（仅在占位符需要实时变动时建议开启定时刷新）。
* `title-update` 为付费版功能，需前置 **PacketEvents**。
* 物品栏点击冷却与重新打开冷却分别由 `menu.cooldown.click` 与 `menu.cooldown.reopen` 控制（单位为游戏刻 tick，默认 `-1` 为不限制）。

## 始终使用物品栏的界面

[物品出售菜单](item-sell-menus.md)需要玩家放入物品并手动确认，[全部出售菜单](sell-all-menu.md)会在关闭时结算放入的物品，两者在所有环境下都始终使用物品栏，不受全局呈现方式影响。[批量购买菜单](buy-more-menus.md)则有自己专用的数量按钮与预览槽位。

[UI 模式](ui-overview.md) · [展示物品的附加描述](display-item-add-lore.md)
