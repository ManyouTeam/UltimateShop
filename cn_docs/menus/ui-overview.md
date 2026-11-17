# 🎨UI 模式

菜单可以通过物品栏槽位、原生控件或图形卡片呈现。菜单类型决定支持哪些呈现方式；切换呈现方式不会改变商品价格、条件或已保存数据。

## 可用呈现方式

| 呈现方式 | 客户端与运行要求 | 布局设置 |
| --- | --- | --- |
| [物品栏](inventory-ui.md) | Java 或 Bedrock，标准菜单界面 | 根级 `size`、`layout`、`buttons` |
| [Bedrock 表单 - 付费版](bedrock-menus-premium.md) | Bedrock，已配置受支持的 Geyser/Floodgate | `bedrock.*`，条目转为列表 |
| [原生对话框 - 付费版](dialog-menus-premium.md) | Java，受支持的 Paper 1.21.9+ | `dialog.layout: multi-action` 或 `item-action-list` |
| [Ore - 付费版](ore-menus-premium.md) | 受支持的 Paper 对话框运行环境与生成的客户端资源包 | `dialog.layout: ore` 加图形 `ui` |
| [ArcMenu - 付费版](arcmenu-menus-premium.md) | Java，兼容的 ArcMenu 与客户端配置 | `arcmenu.enabled` 加图形 `ui` |

原生 `multi-action` 和 `item-action-list` 不需要 Ore 生成的资源包。ArcMenu 的客户端要求以其自身对接方案为准。

## 正常选择顺序

`config.yml` 中的全局开关启用对应界面：

```yaml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

各菜单文件可进一步限制可用选项：

```yaml
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
```

Java 玩家按 **ArcMenu → Dialog/Ore → 物品栏** 的顺序尝试打开。ArcMenu 无法打开时，仍可使用已启用且受支持的对话框。菜单 `dialog.layout` 决定使用原生布局还是 Ore。

检测为 Bedrock 的玩家会单独检查 Bedrock 表单。若要确保两种客户端都使用物品栏，请使用下方的强制物品栏设置。菜单 `dialog.enabled` 和 `bedrock.enabled` 默认是 `true`，`arcmenu.enabled` 默认是 `false`。仅开启全局开关，并不能保证运行环境或菜单类型兼容。

## 支持的菜单类型

| 菜单类型 | 物品栏 | Bedrock 表单 | 原生对话框 | Ore | ArcMenu |
| --- | --- | --- | --- | --- | --- |
| 通用 / 商店 | 支持 | 支持 | 支持 | 支持 | 支持 |
| 搜索 / 收藏 | 支持 | 支持 | 支持 | 回退到原生 multi-action | 支持 |
| 批量购买 | 专用槽位控件 | 来源商店的数量表单 | 原生数量输入 | 无独立图形界面 | 来源界面的商品详情 |
| 物品出售 / 全部出售 | 支持 | 不支持 | 不支持 | 不支持 | 不支持 |
| 游戏内编辑器 | 支持 | 不支持 | 不支持 | 不支持 | 不支持 |

搜索/收藏设置 `dialog.layout: ore` 时，会使用原生 `multi-action`，不会生成 Ore 资源。批量购买菜单的物品栏布局不会转换为表单或图形画布。物品出售与全部出售始终提供实际物品栏槽位，即使从其他呈现方式打开也是如此。

## 强制一种呈现方式

在 `config.yml` 中统一选择菜单呈现方式：

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

此设置覆盖全局和菜单自身的界面启用开关，但仍保留平台、版本、对接插件和菜单类型要求。

| 强制 `type` | Java | Bedrock |
| --- | --- | --- |
| `inventory` | 物品栏 | 物品栏 |
| `arcmenu` | 可用时 ArcMenu，否则物品栏 | 物品栏 |
| `dialog` | 可用时原生对话框 | 物品栏 |
| `item-action-list` | 受支持时使用原生 item-action-list | 物品栏 |
| `ore` | 通用/商店使用 Ore，搜索/收藏使用原生 multi-action | 物品栏 |
| `form` | 物品栏 | 可用时使用表单 |

不支持的方式仍会使用其可用的后备界面。物品出售和全部出售保持物品栏。强制对话框时，批量购买可使用原生数量输入；ArcMenu 数量控件仍位于商品详情中。强制 `dialog` 会把 Ore 布局替换成 `multi-action`，原本使用原生 `item-action-list` 的则保持原样。

将 `style` 设为 `bedrock` 等样式 ID，可同时强制图形默认值；留空则保留正常样式选择，见[配置与继承](menu-configuration.md#force-a-presentation-and-style)。无效的 `type` 会报告配置错误并恢复正常选择。修改后请重载。

## 配置布局、内容与可见性

| 修改内容 | 配置键 |
| --- | --- |
| 物品栏位置 | 根级 `size`、`layout` |
| 原生对话框控件 | `dialog.*` |
| Bedrock 表单文本与行为 | `bedrock.*` |
| 图形样式 | 全局 `menu.ui.style` 或菜单 `ui.style` |
| Ore/ArcMenu 坐标 | `ui.layout`、条目 `ui.placement` |
| 商品卡片默认值 | `ui.products.*` |
| 在某种呈现方式中隐藏条目 | 条目 `menu-visibility.<presentation>` |

根级 `content` 是共享说明的后备值；`dialog.content`、`bedrock.content`、`arcmenu.content` 分别覆盖各自界面说明。图形 `ui` 配置段控制绘制和位置，不能通过 `ui.mode` 选择呈现方式。

[菜单系统](README.md) · [布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
