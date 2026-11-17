# 🔄菜单配置兼容性

更新菜单配置时，请使用下方当前目录与配置键。呈现方式选择和图形绘制分别配置，旧版图形选择器不会自动当作原生控件设置解析。

## 配置位置

| 配置 | 当前位置 |
| --- | --- |
| 通用菜单 | `menus/common_menus/` |
| 商店模板 | `menus/shop_menu_templates/` |
| 数量菜单 | `menus/buy_more_menus/` |
| 搜索 / 收藏 / 物品出售 | `menus/` 下对应专用子目录 |
| 共享图形样式 | `ui/<style>.yml` |
| 全局设置 | `config.yml` → `menu` |
| 商店专用覆盖 | 商店 `settings.menu-settings` |

旧版根级专用菜单目录和通用菜单位置，会在启动时迁入整理后的目录。升级前请备份，并保持 ID 唯一。当前专用目录会自动识别类型，已有通用目录文件可以使用 `menu-type` 声明。

## 原生呈现方式配置键

| 用途 | 当前配置键 |
| --- | --- |
| 允许对话框 | 全局 `menu.dialog.enabled` 和菜单 `dialog.enabled` |
| 选择原生对话框/Ore | 菜单 `dialog.layout: multi-action`、`item-action-list` 或 `ore` |
| 对话框说明与尺寸 | 菜单 `dialog.content`、`dialog.button-width`、`dialog.columns` |
| 对话框标签图标 | 条目 `dialog.show-sprite`，全局 `menu.dialog.auto-add-sprite.*` |
| Ore 图标/资源包行为 | 全局 `menu.dialog.ore.show-item-icon.*`、`menu.dialog.ore.pack-format` |
| 允许表单 | 全局 `menu.bedrock.enabled` 和菜单 `bedrock.enabled` |
| 表单文本与行为 | 菜单 `bedrock.*`，全局 `menu.bedrock.*` |
| 表单条目图标/文本/隐藏 | 当前 `display-item.bedrock.icon/extra-line/hide` |
| 表单商品直接操作 | 商品 `bedrock.click-event` |
| 允许 ArcMenu | 全局 `menu.arcmenu.enabled` 和菜单 `arcmenu.enabled` |
| 强制呈现方式/样式 | 全局 `menu.force-ui.enabled/type/style` |
| 条目可见性 | 条目 `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |

`ui.mode`、`ui.visible-in`、`ui.show-sprite`、`ui.renderers.form`、`ui.renderers.dialog` 不用于选择或配置原生呈现方式。请把设置迁移到上表受支持的路径。

## 图形配置键

| 用途 | 当前配置键 |
| --- | --- |
| 选择共享样式 | 全局 `menu.ui.style` 或菜单 `ui.style` |
| 画布、页眉、区域与分页 | `ui.layout.*` |
| 默认商品位置 | `ui.products.placement` |
| 卡片外观、简洁价格与操作 | `ui.products.card/price/actions` |
| 单个条目位置 | 条目 `ui.placement` |
| 按钮/卡片外观与字体 | 条目 `ui.appearance/font-size/text-align` |
| 颜色与图形标签 | `ui.theme`、`ui.text` |
| Ore 纹理模板 | `ui.renderers.ore.templates` |
| ArcMenu 交互/渲染 | `ui.renderers.arcmenu` |

样式文件直接包含这些字段，不需要外层 `ui`。已有内联 `menu.ui` 默认值仍受支持。适合复用的图形定义可移入样式，再通过 ID 选择。

## 示例：使用共享样式选择 Ore

将以下片段放在菜单根级：

```yaml
dialog:
  enabled: true
  layout: ore
bedrock:
  enabled: true
arcmenu:
  enabled: false
ui:
  style: classic
  products:
    placement:
      area: content
```

启用全局对话框，为普通图形按钮显式设置位置，重载后生成并分发 Ore 资源包。物品栏槽位不会转换成图形坐标，也不会根据旧动作自动推断分类侧栏或返回按钮。

选择 Ore 时，搜索/收藏使用原生对话框。批量购买不使用图形样式，物品出售与全部出售仍为实际物品栏。在向这些文件添加呈现字段前，请检查[支持的菜单类型](ui-overview.md#supported-menu-types)。

[配置与继承](menu-configuration.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
