# 🔄菜单配置兼容性

升级或整理旧版菜单配置时，请对照下方的当前目录结构与配置键。需要注意：呈现方式选择与图形绘制是分开配置的，旧版图形选择器不会自动当作原生控件设置解析。

## 当前配置文件位置

| 配置类型 | 当前位置 |
| --- | --- |
| 通用菜单 | `menus/common_menus/` |
| 商店菜单模板 | `menus/shop_menu_templates/` |
| 批量购买菜单 | `menus/buy_more_menus/` |
| 搜索 / 收藏 / 物品出售菜单 | `menus/` 下各自对应的专用子目录 |
| 共享图形样式 | `ui/<style>.yml` |
| 全局默认设置 | `config.yml` → `menu` |
| 单个商店覆盖 | 商店文件 `settings.menu-settings` |

启动插件时，旧版根目录下的专用菜单与通用菜单会自动迁入整理后的子目录（升级前请先备份，并确保菜单 ID 唯一）。专用子目录会自动识别菜单类型，仍留在通用目录中的旧文件也可通过 `menu-type` 声明类型。

## 原生呈现方式配置键对照

| 用途 | 当前配置键 |
| --- | --- |
| 允许原生对话框 | 全局 `menu.dialog.enabled` 与菜单 `dialog.enabled` |
| 选择原生对话框 / Ore | 菜单 `dialog.layout: multi-action`、`item-action-list` 或 `ore` |
| 对话框说明与按钮尺寸 | 菜单 `dialog.content`、`dialog.button-width`、`dialog.columns` |
| 对话框 Sprite 图标 | 条目 `dialog.show-sprite`、全局 `menu.dialog.auto-add-sprite.*` |
| Ore 图标与资源包格式 | 全局 `menu.dialog.ore.show-item-icon.*`、`menu.dialog.ore.pack-format` |
| 允许 Bedrock 表单 | 全局 `menu.bedrock.enabled` 与菜单 `bedrock.enabled` |
| 表单文本与行为 | 菜单 `bedrock.*`、全局 `menu.bedrock.*` |
| 表单条目图标 / 补充行 / 隐藏 | `display-item.bedrock.icon` / `extra-line` / `hide` |
| 表单商品直接操作 | 商品 `bedrock.click-event` |
| 允许 ArcMenu | 全局 `menu.arcmenu.enabled` 与菜单 `arcmenu.enabled` |
| 强制呈现方式与样式 | 全局 `menu.force-ui.enabled` / `type` / `style` |
| 各呈现方式可见性 | 条目 `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |

注意：`ui.mode`、`ui.visible-in`、`ui.show-sprite`、`ui.renderers.form`、`ui.renderers.dialog` 均不用于选择或配置原生呈现方式，请迁移至上表对应的受支持路径。

## 图形 UI 配置键对照

| 用途 | 当前配置键 |
| --- | --- |
| 选择共享样式 | 全局 `menu.ui.style` 或菜单 `ui.style` |
| 画布、页眉、区域与分页 | `ui.layout.*` |
| 默认商品位置 | `ui.products.placement` |
| 卡片外观、紧凑价格与操作 | `ui.products.card` / `price` / `actions` |
| 单个条目位置 | 条目 `ui.placement` |
| 按钮/卡片外观与文字对齐 | 条目 `ui.appearance` / `font-size` / `text-align` |
| 配色与图形标签 | `ui.theme`、`ui.text` |
| Ore 纹理模板 | `ui.renderers.ore.templates` |
| ArcMenu 交互与渲染 | `ui.renderers.arcmenu` |

`ui/` 目录下的样式文件直接编写这些字段（无需外层 `ui:`）。可复用的图形定义建议抽离到共享样式中，再通过样式 ID 引用。

## 迁移示例：使用共享样式启用 Ore

在菜单根级添加以下配置：

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

同时确保全局 `menu.dialog.enabled` 已开启，为普通图形按钮显式配置 `ui.placement`，重载插件后执行 `/shop dialogpack` 生成并分发资源包（物品栏槽位不会自动转换成图形坐标，也不会根据旧动作自动推断侧边栏或返回按钮）。

另外，启用 Ore 时搜索与收藏菜单会自动回退为原生对话框；批量购买不使用图形样式；物品出售与全部出售始终保留实际物品栏界面，详见[各菜单类型支持的呈现方式](ui-overview.md#各菜单类型支持的呈现方式)。

[菜单配置与继承](menu-configuration.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
