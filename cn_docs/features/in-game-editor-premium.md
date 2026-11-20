# ✏️游戏内编辑器 - 付费版

用 `/shop editor` 打开编辑器。第一次使用时，在 `plugins/UltimateShop/config.yml` 里把 `editor.enabled` 改成 `true`，然后执行 `/shop reload`。使用者需要 `ultimateshop.editor` 权限。

## 打开要改的文件

想改商品，点 **编辑商店**，选中商店文件，再打开 `items` 并选择商品。想改菜单标题、槽位或按钮，就从首页选对应的菜单分类。

首页底部的 **配置文件** 可以打开 `config.yml`、共享 UI 样式、已保存的物品、语言文件、各类占位符、共享限购次数、出售棒和出售箱。

也可以用命令直接打开文件。例如，`/shop editor shop example` 会打开 `shops/example.yml`，`/shop editor config config` 会打开 `config.yml`。文件名省略 `.yml`，忘了分类名或文件名时可以按 Tab 补全。

| 分类 | 文件位置 |
| --- | --- |
| `shop` | `shops/` |
| `menu` | `menus/common_menus/` |
| `shop_menu_template` | `menus/shop_menu_templates/` |
| `buy_more_menu` | `menus/buy_more_menus/` |
| `favourite_menu` | `menus/favourite_menus/` |
| `search_menu` | `menus/search_menus/` |
| `item_sell_menu` | `menus/item_sell_menus/` |

要新建菜单，先把内置示例复制到对应目录，再打开新文件。其他目录及旧版配置的迁移方式见[配置文件](../info/configuration-files.md)。

## 修改字段

把光标放在字段上，物品说明会告诉你怎么操作。开关点一下就能切换。有固定选项的字段可以点击轮换，比如商品的价格模式和菜单使用的共享 UI 样式。想把默认购买数量直接设为 32，就把光标放在对应字段上，按丢弃键（默认是 **Q**），然后在聊天栏输入 `32`。

点开列表后可以添加、修改或删除其中一项。改限购重置时间时，先选重置模式。例如，想让次数在交易后 15 分钟重置，可用 `TIMER`，时间填 `00:15:00`。想设为每天 19 点，可用 `TIMED`，时间填 `19:00:00`。

常用字段都有单独的按钮。找不到某个设置时，点 **原始节点**，就可以按 YAML 文件里的层级继续查找或添加字段。

## 菜单外观和按钮去哪改

- **改物品栏的大小和槽位**时，打开菜单文件里的 `size` 和 `layout`。批量购买菜单的加减数量按钮在 `amount-items`，确认按钮在 `confirm-items`。
- **启用对话框、基岩版表单或 ArcMenu**时，在菜单文件里找 `dialog.enabled`、`bedrock.enabled` 或 `arcmenu.enabled`。对话框的布局在 `dialog.layout` 里选。Ore 和 ArcMenu 的布局、样式和颜色在 `ui` 里改。
- **设置商品或按钮在哪显示**时，打开它的 `menu-visibility`。用 `dialog.show-sprite` 开关对话框里的物品图标。想改它在 ArcMenu 里的位置、字号或对齐方式，就打开它自己的 `ui`。
- **改 ArcMenu 里右键商品的动作**时，打开菜单的 `ui.renderers.arcmenu.right-click-action`。默认动作 `select-amount` 会打开商品详情。

如果想让多个菜单用同一套外观，可以从 **配置文件** 打开共享 UI 样式。全局默认设置在 `config.yml` 的 `menu.ui` 下，也可以在单个菜单里覆盖。更多布局示例见[统一 UI 与 ArcMenu](../menus/arcmenu-menus-premium.md)。

## 让改动生效

每次成功修改后，编辑器都会把新值写进文件。全部改好后，点 **重载插件** 或执行 `/shop reload`，再打开菜单查看效果。

如果保存时提示文件已被修改，返回文件列表重新打开它，再把刚才的改动填进去。
