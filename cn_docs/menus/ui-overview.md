# 🎨UI 模式

UltimateShop 的菜单可以通过原版物品栏槽位、原生表单/对话框控件或图形卡片来呈现。菜单**类型**决定了支持哪些呈现方式；切换呈现方式只会改变界面展示形式，不会影响商品定价、条件校验或已保存的交易数据。

## 可用呈现方式

| 呈现方式 | 客户端与运行要求 | 布局配置方式 |
| --- | --- | --- |
| [物品栏](inventory-ui.md) | Java 与 Bedrock 通用，标准箱子界面 | 根级 `size`、`layout`、`buttons` |
| [Bedrock 表单 - 付费版](bedrock-menus-premium.md) | Bedrock 客户端，已安装受支持的 Geyser / Floodgate | `bedrock.*`，条目自动转为表单列表 |
| [原生对话框 - 付费版](dialog-menus-premium.md) | Java 客户端，受支持的 Paper 1.21.9+ 服务端 | `dialog.layout: multi-action` 或 `item-action-list` |
| [Ore - 付费版](ore-menus-premium.md) | 受支持的 Paper 对话框环境 + 生成的客户端资源包 | `dialog.layout: ore` + 图形 `ui` |
| [ArcMenu - 付费版](arcmenu-menus-premium.md) | Java 客户端，已安装兼容的 ArcMenu 插件与客户端配置 | `arcmenu.enabled` + 图形 `ui` |

原生 `multi-action` 与 `item-action-list` 无需生成 Ore 资源包；ArcMenu 的客户端要求请以其自身对接方案为准。

## 正常选择顺序

在 `config.yml` 中配置全局默认开关：

```yaml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

在单个菜单文件中可以进一步细化或限制：

```yaml
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
```

* **Java 玩家**：按 **ArcMenu → Dialog / Ore → 物品栏** 的顺序尝试打开。如果 ArcMenu 当前不可用，会自动向下回退到已启用且受支持的对话框或物品栏；`dialog.layout` 决定对话框路径走原生布局还是 Ore。
* **Bedrock 玩家**：单独检查 Bedrock 表单开关。如需让 Java 与 Bedrock 玩家统一使用物品栏，可使用下方的 `force-ui` 强制设置。

默认情况下，菜单的 `dialog.enabled` 与 `bedrock.enabled` 为 `true`，`arcmenu.enabled` 为 `false`。注意，仅开启全局开关并不能保证运行环境或菜单类型一定兼容。

## 各菜单类型支持的呈现方式

| 菜单类型 | 物品栏 | Bedrock 表单 | 原生对话框 | Ore | ArcMenu |
| --- | --- | --- | --- | --- | --- |
| 通用菜单 / 商店菜单 | 支持 | 支持 | 支持 | 支持 | 支持 |
| 搜索菜单 / 收藏菜单 | 支持 | 支持 | 支持 | 回退为原生 `multi-action` | 支持 |
| 批量购买菜单 | 专用槽位控件 | 来源商店的数量表单 | 原生数量输入 | 无独立图形界面 | 来源界面的商品详情 |
| 物品出售 / 全部出售 | 支持 | 不支持 | 不支持 | 不支持 | 不支持 |
| 游戏内编辑器 | 支持 | 不支持 | 不支持 | 不支持 | 不支持 |

说明：
* 搜索与收藏菜单设置 `dialog.layout: ore` 时，会自动回退为原生 `multi-action`，且不会生成 Ore 资源。
* 批量购买菜单的物品栏布局不会直接转成表单或图形画布。
* 物品出售与全部出售菜单需要玩家实际放入背包物品，因此无论从哪种界面打开，始终使用原版物品栏。

<a id="force-one-presentation"></a>

## 强制统一呈现方式

在 `config.yml` 中配置 `menu.force-ui`，即可全局统一指定呈现方式：

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

该设置会覆盖全局与菜单自身的呈现开关，但仍需满足平台、服务端版本、对接插件与菜单类型本身的限制：

| 强制 `type` | Java 客户端 | Bedrock 客户端 |
| --- | --- | --- |
| `inventory` | 物品栏 | 物品栏 |
| `arcmenu` | 可用时使用 ArcMenu，否则回退物品栏 | 物品栏 |
| `dialog` | 可用时使用原生对话框（Ore 布局也会转为 `multi-action`） | 物品栏 |
| `item-action-list` | 受支持时使用原生 `item-action-list` | 物品栏 |
| `ore` | 通用/商店使用 Ore，搜索/收藏使用原生 `multi-action` | 物品栏 |
| `form` | 物品栏 | 可用时使用 Bedrock 表单 |

若目标方式不受支持，仍会按后备界面打开（例如物品出售与全部出售始终保留物品栏；强制 `dialog` 时，原使用 `item-action-list` 的菜单保持不变）。

将 `style` 设为 `bedrock` 等样式 ID 可同时强制覆盖图形样式，留空（`''`）则保留各菜单自己的样式选择，详见[菜单配置与继承](menu-configuration.md#强制呈现方式与样式)。若填写了无效的 `type`，插件会报错并恢复正常选择。

## 布局、文本与可见性配置速查

| 调整目标 | 配置位置 |
| --- | --- |
| 物品栏槽位位置 | 根级 `size`、`layout` |
| 原生对话框控件 | `dialog.*` |
| Bedrock 表单文本与行为 | `bedrock.*` |
| 图形样式选择 | 全局 `menu.ui.style` 或菜单 `ui.style` |
| Ore / ArcMenu 坐标 | `ui.layout` 与条目 `ui.placement` |
| 商品卡片默认值 | `ui.products.*` |
| 在特定呈现方式中隐藏条目 | 条目 `menu-visibility.<presentation>` |

菜单根级 `content` 作为共享说明文本的后备值，`dialog.content`、`bedrock.content` 与 `arcmenu.content` 可分别覆盖各自界面的说明文本。注意，图形 `ui` 配置段仅负责绘制与排版，不能通过 `ui.mode` 切换呈现方式。

## ArcMenu 与对话框的文本宽度校准

UltimateShop 会根据 `plugins/UltimateShop/glyph-widths.yml` 计算文本渲染宽度。如果你的服务器使用非英文等字符集，默认宽度可能会导致 ArcMenu 或对话框（含 Ore 布局）出现文字截断、意外换行或对齐偏移。

可在 `glyph-widths.yml` 的 `shared.width` 中按像素设置单个 Unicode 码位（如 `U+0041`）或闭区间（如 `U+0041-U+005A`）的宽度（同一段内靠后的规则会覆盖前面的规则）；Ore 还可通过 `ore.width` 单独覆盖共享宽度。修改保存后执行 `/shop reload` 并重新打开菜单即可查看效果。

[菜单系统](README.md) · [布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
