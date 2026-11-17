# ✏️游戏内编辑器 - 付费版

编辑器内置于 UltimateShop 付费版。拥有 `ultimateshop.editor` 权限后，使用 `/shop editor` 打开，无需安装额外附属插件。

## 启用编辑器

在 `plugins/UltimateShop/config.yml` 中设置 `editor.enabled: true`，重载插件后即可启用。每次使用前都应备份配置。编辑器直接修改 YAML 文件；编辑完成后，点击其 Reload Plugin（重载插件）按钮或执行 `/shop reload`，以重新构建当前生效的菜单。

## 打开文件

首页分别列出商店和各类菜单目录。命令中的文件 ID 不包含 `.yml`：

| 命令 | 相对于插件数据目录的路径 |
| --- | --- |
| `/shop editor shop <id>` | `shops/` |
| `/shop editor menu <id>` | `menus/common_menus/` |
| `/shop editor shop_menu_template <id>` | `menus/shop_menu_templates/` |
| `/shop editor buy_more_menu <id>` | `menus/buy_more_menus/` |
| `/shop editor favourite_menu <id>` | `menus/favourite_menus/` |
| `/shop editor search_menu <id>` | `menus/search_menus/` |
| `/shop editor item_sell_menu <id>` | `menus/item_sell_menus/` |

这七种范围都支持命令补全。编辑器用于打开已有文件；要新建菜单，请先将当前内置示例复制到相应目录，再用编辑器打开。旧版目录会在启动时迁移，详见[配置文件](../info/configuration-files.md)。

## 菜单与 UI 预设

菜单预设可编辑根级 `title`、`content`、`size`、物品栏 `layout`、条件、动作和按钮。打开 **UI** 可编辑当前界面设置：

* `mode.java`：`inherit`、`inventory`、`dialog`、`ore`、`arcmenu`。
* `mode.bedrock`：`inherit`、`inventory`、`form`。
* `layout.canvas`、`layout.header`、`layout.areas`、`layout.pagination`：Ore/ArcMenu 共享布局。
* `products`：商品默认位置、卡片样式、价格格式与操作列表。
* `renderers.dialog`：原生对话框布局、按钮宽度和列数。
* `renderers.form`、`renderers.ore`：对应界面的设置。
* `renderers.arcmenu`：右键动作、画面缩放、更新间隔、动画和提示框。

上述路径均相对于菜单的 `ui` 配置段。全局默认值位于 `config.yml` 的 `menu.ui` 下，需要在该文件中修改。商店的 `settings.menu-settings.ui` 使用相同的菜单 UI 预设。

ArcMenu 右键默认使用 `select-amount` 打开商品详情。选项中包含标准商品动作和已配置的自定义动作。ArcMenu 不渲染物品栏式批量购买槽位；修改 `amount-items` 或 `confirm-items` 只影响原有数量菜单，不影响 ArcMenu 详情面板。

搜索预设还可编辑输入/结果标识、动作按钮、状态按钮、空结果与结果描述。收藏预设可编辑结果标识、编辑模式按钮、空状态，以及正常/编辑状态描述。数量菜单和物品出售预设则提供各自专用控件。

## 商品与按钮 UI

在商品或普通按钮预设中打开 `ui`，可编辑 `visible-in`、`show-sprite`、`appearance`、位置、字号、文字对齐、价格和操作。菜单的 `ui.products` 预设只提供共享商品默认值。普通按钮动作仍位于按钮根级 `actions`；`ui.actions` 控制商品操作的显示，并不用于填写编号动作定义。

`visible-in` 接受字符串列表，例如 `inventory;;dialog;;ore;;arcmenu`；表单使用 `form`。空列表会在所有界面隐藏该条目；删除整个列表则恢复默认可见性规则。`dialog` 包含原生 `item-action-list` 布局。

## 保存与高级配置段

按各字段的点击提示操作：选项字段循环切换值，布尔字段开关切换，数字/字符串字段通过聊天输入。列表输入用 `;;` 分隔，支持右键删除的字段会显示提示。Raw Section（原始配置段）可以编辑预设之外的 YAML 结构，包括命名布局区域、页眉、价格格式和额外操作。`ui.products`、`ui.actions` 和名为 `content` 的区域会被当作 UI 设置，而不是交易物品或可执行动作。

菜单覆盖值优先于全局默认值；支持覆盖的位置中，商品/按钮设置优先。删除本地值并重载后会恢复继承。位置尺寸、优先级和布局示例见[统一 UI 与 ArcMenu](../menus/arcmenu-menus-premium.md)。
