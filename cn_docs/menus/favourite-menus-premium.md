# ❤️收藏菜单 - 付费版

收藏菜单保存玩家选择的商品与顺序。收藏列表归属于对应的收藏菜单 ID，添加商品的动作必须指向相同 ID。商品仍使用来源商店的价格与交易规则。

文件保存在 `menus/favourite_menus/`。使用 `/shop menu favourite` 或 `open_menu` 打开内置菜单。目录决定菜单类型；通用目录中的文件可以设置 `menu-type: favourite`（别名 `favorite`），或使用旧版 `menu.favourite-gui.menu` 列表。

<a id="add-products"></a>

## 添加商品

内置 `add-favourite` 操作绑定 `SWAP_OFFHAND`（F 键），自定义动作指向内置 `favourite` 列表：

```yaml
type: add_favourite
menu: favourite
shop: '{shop}'
item: '{item}'
```

在具有商品上下文的动作列表中使用此配置。自定义列表如 `my-favourites`，需要把添加动作的 `menu` 改为该 ID。商品必须允许收藏。原生和图形界面的信息/额外操作控件可展示同一个已配置操作，但仅设置标签不会创建动作绑定。

## 物品栏示例

保存为 `menus/favourite_menus/my-favourites.yml`：

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

重载，向 `my-favourites` 添加商品，再执行 `/shop menu my-favourites`。此示例正常选择时使用物品栏，全局强制 UI 仍有效。

## 收藏配置

| 配置键 | 用途 |
| --- | --- |
| `result-item`、`result-items` | 合并的结果标记，均未提供时默认 `6` |
| `edit-mode-item.<id>.normal-display-item` | 正常模式下编辑开关的外观 |
| `edit-mode-item.<id>.editing-display-item` | 编辑模式下开关的外观 |
| `edit-mode-item.<id>.display-item` | 缺少对应外观时的后备值 |
| `edit-mode-item.<id>.slot` | 开关 ID 未出现在布局时的后备槽位，从零编号 |
| `empty-item.display-item` | 未填充结果槽位的占位物品 |
| `result-lore` | 正常操作提示，`{shop}`、`{product}` 表示来源 |
| `editing-result-lore` | 编辑操作提示 |
| 专用控件 `menu-visibility` | 各呈现方式的可见性 |
| 专用控件 `ui` | ArcMenu 位置与外观 |

只使用一个最终生效的编辑开关。根级结果标记决定最大显示数量，按保存顺序填充；图形分页不会单独增加容量。

编辑开关文本支持 `{editing}`、`{edit-mode}`、`{favourite-amount}`、`{favorite-amount}`。空槽位文本支持数量别名与 `{slot-index}`，后者为从 1 开始的结果位置。

## 正常与编辑模式

正常模式使用商品点击操作。编辑开关只切换模式，不执行交易。

| 编辑模式下的物品栏输入 | 操作 |
| --- | --- |
| 左键 | 向前移动一位 |
| 右键 | 向后移动一位 |
| 丢弃 / Ctrl+丢弃 | 从当前列表移除 |

移除收藏不会删除玩家物品或商店库存。关闭编辑后恢复商品操作。列表与顺序在不同呈现方式之间共享。

## 其他呈现方式

| 呈现方式 | 正常模式 | 编辑模式 |
| --- | --- | --- |
| Bedrock 表单 | 商品信息表单 | 打开商品后显示前移/后移/移除/返回 |
| 原生对话框 | 商品信息与操作 | 专用编辑控件，省略空结果槽位 |
| ArcMenu | 商品卡片、操作与详情 | 卡片提供前移/后移/移除，支持左/右/中键编辑，中键移除 |

表单可以显示已配置的空槽位按钮。ArcMenu 的编辑开关和导航需要 `ui.placement`，结果卡片需要 `ui.products.placement`。可以关闭空槽位的图形定位。

表单编辑文本使用 `bedrock.favourite-edit`，后备为全局 `menu.bedrock.favourite-edit`；对话框使用 `dialog.favourite-edit` 与 `menu.dialog.favourite-edit`。其中包含 `title`、`content`、`buttons.forward/backward/remove/back`，标题与内容支持 `{item-name}`。

ArcMenu 卡片编辑标签默认采用表单标签，可用 `ui.text.favourite-edit.forward/backward/remove` 覆盖。可通过[呈现条件](display-item-add-lore.md#presentation-conditions)为不同界面设置不同编辑描述。

收藏不渲染 Ore 图形。`dialog.layout: ore` 会使用原生 `multi-action`，不会为此类型生成 Ore 资源。

[UI 模式](ui-overview.md) · [自定义点击事件](../features/custom-click-event-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
