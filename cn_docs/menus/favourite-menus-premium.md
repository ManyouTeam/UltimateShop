# ❤️收藏菜单 - 付费版

收藏菜单用于保存玩家个人收藏的商品及其排列顺序。每个收藏列表都绑定到对应的收藏菜单 ID，添加收藏的动作必须指向相同的菜单 ID；收藏商品的价格与交易规则仍沿用来源商店的设置。

文件存放在 `menus/favourite_menus/`（放入该目录即自动识别为收藏菜单），可通过 `/shop menu favourite` 或 `open_menu` 打开内置菜单。放在 `common_menus` 中的旧版文件可通过根级 `menu-type: favourite`（别名 `favorite`）或全局 `menu.favourite-gui.menu` 列表声明。

<a id="add-products"></a>

## 添加商品到收藏

内置 `add-favourite` 操作默认绑定 `SWAP_OFFHAND`（F 键），自定义动作会指向内置的 `favourite` 菜单：

```yaml
type: add_favourite
menu: favourite
shop: '{shop}'
item: '{item}'
```

将此动作放在具备商品上下文的动作列表中即可使用；如果你自建了名为 `my-favourites` 的收藏菜单，只需将 `menu` 改为 `my-favourites`（且商品本身需允许被收藏）。原生与图形界面的信息/额外操作控件会自动展示已配置的收藏动作，但仅设置按钮标签并不会凭空创建动作绑定。

## 物品栏示例

将以下配置保存为 `menus/favourite_menus/my-favourites.yml`：

```yaml
title: 'Favourites'
content: 'Your saved products.'
size: 27
layout:
  - '000000000'
  - '011111110'
  - '0000D0000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
result-item: '1'
edit-mode-item:
  D:
    normal-display-item:
      material: NAME_TAG
      name: '&eEdit favourites'
    editing-display-item:
      material: WRITABLE_BOOK
      name: '&aFinish editing'
empty-item:
  display-item:
    material: GRAY_STAINED_GLASS_PANE
    name: '&7Empty favourite slot'
result-lore:
  - '&7Shop: {shop}'
  - '&7Product: {product}'
editing-result-lore:
  - '&eLeft: move forward'
  - '&eRight: move backward'
  - '&cDrop: remove'
```

重载插件并向 `my-favourites` 添加几个商品后，执行 `/shop menu my-favourites` 即可打开（全局 `menu.force-ui` 优先级高于菜单开关）。

## 收藏配置键

| 配置键 | 用途 |
| --- | --- |
| `result-item`、`result-items` | 收藏商品展示槽位标记（合并生效，均未配置时默认 `'6'`） |
| `edit-mode-item.<id>.normal-display-item` | 正常模式下编辑开关的展示物品 |
| `edit-mode-item.<id>.editing-display-item` | 编辑模式下编辑开关的展示物品 |
| `edit-mode-item.<id>.display-item` | 缺少上述状态外观时的后备展示物品 |
| `edit-mode-item.<id>.slot` | 编辑开关 ID 未写入 `layout` 时的后备槽位（从 `0` 开始编号） |
| `empty-item.display-item` | 未放满的结果槽位显示的占位物品 |
| `result-lore` | 正常模式下的附加描述，`{shop}`、`{product}` 表示来源 |
| `editing-result-lore` | 编辑模式下的操作提示描述 |
| 专用控件 `menu-visibility` | 控制该控件在各呈现方式下的可见性 |
| 专用控件 `ui` | 控制在 ArcMenu 中的位置与外观 |

每个菜单只会生效一个编辑开关。根级 `result-item` 的数量决定了最大可展示的收藏数量（按保存顺序填充），图形分页不会额外扩大该上限。

编辑开关文本支持 `{editing}`、`{edit-mode}`、`{favourite-amount}`、`{favorite-amount}`；空槽位文本支持上述数量别名以及 `{slot-index}`（从 `1` 开始的结果槽位序号）。

## 正常模式与编辑模式

正常模式下点击收藏商品会直接执行商品交易操作；点击编辑开关则只切换模式，不触发买卖：

| 编辑模式下的物品栏点击 | 操作 |
| --- | --- |
| 左键 | 向前移动一位 |
| 右键 | 向后移动一位 |
| 丢弃 / Ctrl+丢弃 | 从当前收藏列表中移除 |

移除收藏仅从列表删除，不会影响玩家背包物品或商店库存；关闭编辑模式后恢复正常买卖操作。同一份收藏列表与排序在所有呈现方式之间实时共享。

## 其他呈现方式的行为

| 呈现方式 | 正常模式 | 编辑模式 |
| --- | --- | --- |
| Bedrock 表单 | 打开商品信息表单 | 点击商品后显示前移 / 后移 / 移除 / 返回按钮 |
| 原生对话框 | 商品信息与操作按钮 | 显示专用编辑控件，自动省略空结果槽位 |
| ArcMenu | 商品卡片、操作与详情 | 卡片提供前移 / 后移 / 移除，支持左 / 右 / 中键编辑（中键移除） |

Bedrock 表单支持显示已配置的空槽位按钮。在 ArcMenu 中，编辑开关与导航按钮需要配置 `ui.placement`，收藏商品卡片使用 `ui.products.placement`（可关闭空槽位的图形定位）。

表单编辑界面文本读取 `bedrock.favourite-edit`（回退全局 `menu.bedrock.favourite-edit`），对话框读取 `dialog.favourite-edit`（回退 `menu.dialog.favourite-edit`），其中包含 `title`、`content` 及 `buttons.forward/backward/remove/back`（标题与内容支持 `{item-name}`）。ArcMenu 卡片的编辑按钮文本默认复用表单标签，也可用 `ui.text.favourite-edit.forward/backward/remove` 覆盖；不同界面的编辑提示可用[描述呈现条件](display-item-add-lore.md#presentation-conditions)区分。

收藏菜单**不渲染 Ore 图形**（当 `dialog.layout: ore` 时自动回退为原生 `multi-action` 对话框，且不生成 Ore 资源）。

[UI 模式](ui-overview.md) · [自定义点击事件](../features/custom-click-event-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
