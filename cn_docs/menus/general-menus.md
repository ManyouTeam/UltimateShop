# 🔲通用菜单

通用菜单用于展示自定义按钮，适合制作主菜单、商店分类导航或信息面板。文件存放在 `menus/common_menus/`，可通过 `/shop menu <id>` 或 `open_menu` 动作打开。

## 创建导航菜单

将以下配置保存为 `menus/common_menus/main.yml`：

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '000A0X000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

使用前请先确保你已经创建了 `farming` 商店（否则点击按钮时找不到目标商店）。保存好文件后，在游戏或控制台执行 `/shop reload`，再输入 `/shop menu main` 就能打开这个菜单。

因为示例里把 `dialog`、`bedrock`、`arcmenu` 都设成了 `false`，所以默认会以普通的箱子物品栏界面打开（除非你在 `config.yml` 中开启了全局强制界面 `menu.force-ui`）。

{% hint style="info" %}
**跳转到商店 vs 跳转到其他菜单**：
* 想打开**商店**（买卖商品），请用 `type: shop_menu` 并填写 `shop: 商店ID`。
* 想打开**另一个通用菜单、搜索、收藏或物品出售菜单**，请用 `type: open_menu` 并填写 `menu: 菜单ID`。

注意：通用菜单只会显示你在 `buttons` 里手写的按钮，**不会**自动把商店里的商品填进来。
{% endhint %}

## 通用菜单配置键

| 配置键 | 用途 |
| --- | --- |
| `title` | 界面标题 |
| `content` | 界面说明文本（各呈现方式 `content` 的后备值） |
| `size`、`layout` | 物品栏大小与槽位布局 |
| `buttons` | 按钮展示物品、动作与条件 |
| `conditions` | 打开整个菜单前校验的条件 |
| `open-actions`、`close-actions` | 打开和关闭菜单时执行的动作 |
| `dynamic-layout` | 按玩家动态解析布局（付费版） |
| `dialog`、`bedrock`、`arcmenu` | 各呈现方式的开关与原生选项 |
| `ui` | Ore / ArcMenu 的样式、布局与渲染配置 |

注意区分三种条件作用域：按钮点击条件写在 `buttons.<id>.conditions`，按钮显示条件写在 `buttons.<id>.display-conditions`，而菜单根级 `conditions` 控制整个界面是否允许打开。详见[布局与按钮](layout-and-buttons.md)。

## 使用其他呈现方式 - 付费版

如需改用原生对话框，可将示例中的 `dialog` 段替换为：

```yaml
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
```

同时需在 `config.yml` 开启全局 `menu.dialog.enabled`。若使用 Ore，设 `dialog.layout: ore` 并配置图形位置与资源包；若使用 ArcMenu，需安装兼容插件并开启全局及菜单的 `arcmenu.enabled`；若使用 Bedrock 表单，则开启全局与菜单的 `bedrock.enabled`。

选择顺序、强制开关与平台支持见 [UI 模式](ui-overview.md)；图形区域与坐标见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 自定义命令 - 付费版

在菜单根级添加 `custom-command`，即可为该菜单注册一个快捷命令（请避免与现有命令重名）：

```yaml
custom-command:
  name: categories
  description: Open the category menu
```

<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

## 物品栏刷新与标题更新

在 `config.yml` 中，全局 `menu.menu-update.circle-update` 控制每秒自动刷新物品显示，`click-update` 控制点击后刷新。付费版还支持动态标题更新（`menu.title-update.enabled` / `circle-update` / `click-update`，需前置 PacketEvents）。完整配置见[物品栏 UI](inventory-ui.md#clicks-visibility-and-refresh)。

## 在通用目录中声明特殊菜单类型

虽然搜索、收藏与物品出售菜单建议放在各自专用目录下（插件会自动识别类型），但放在 `menus/common_menus/` 中的旧版文件也可以通过在根级添加 `menu-type: search`、`favourite`（别名 `favorite`）或 `item-sell`（别名 `item_sell`）来声明类型（搜索和收藏 ID 同样支持在全局旧版菜单列表中指定）。

无论文件放在哪个目录，其可用控件与 UI 支持范围均以最终识别到的菜单类型为准。

[商店菜单](shop-menus.md) · [菜单配置与继承](menu-configuration.md)
