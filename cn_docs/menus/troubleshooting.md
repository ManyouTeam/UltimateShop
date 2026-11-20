# 🩺故障排查

请根据具体症状排查，不要随意修改无关配置。下方路径均相对于 `plugins/UltimateShop/`。

## 菜单无法打开

1. 检查 ID 与位置：通用菜单为 `menus/common_menus/<id>.yml`，商店菜单为 `shops/<shop>.yml`。
2. 修改 YAML 后先执行 `/shop reload`，再通过 `/shop menu <id>` 重试。
3. 检查根级 `conditions` 和命令权限。商店使用**商店 ID** 打开，不一定是模板文件名。
4. 检查控制台是否报告缺少商品或无效菜单文件。

## Java 打开了物品栏，而不是对话框、Ore 或 ArcMenu <mark style="color:red;">- 付费版</mark>

| 目标呈现方式 | 首先检查 |
| --- | --- |
| 原生对话框 | 全局 `menu.dialog.enabled`、菜单 `dialog.enabled`；受支持的 Paper 对话框 API 和付费版 |
| Ore | 上述对话框开关，**加上**菜单 `dialog.layout: ore`；图形布局与已加载的 Ore 资源包 |
| ArcMenu | 全局 `menu.arcmenu.enabled`、菜单 `arcmenu.enabled`；兼容对接与客户端配置 |

Java 优先级为 ArcMenu → Dialog/Ore → 物品栏。ArcMenu 无法打开时，仍需启用 Dialog/Ore 才能作为下一选项。物品出售/全部出售始终使用实际物品栏。

**不要使用 `ui.mode.java`：它不是有效的选择配置键。**

## Bedrock 打开了物品栏，或表单图片缺失 <mark style="color:red;">- 付费版</mark>

* 检查全局 `menu.bedrock.enabled` 和菜单 `bedrock.enabled`。
* 确认 Floodgate 检测正常，后端可以发送表单。
* 显式图标使用 `display-item.bedrock.icon: 'path;;…'` 或 `url;;…`；`path;;` 是**客户端**纹理路径。
* 自动表单图标依赖材质纹理映射，Java 自定义模型数据不会自动提供 Bedrock 图片。
* 空展示名称或 `display-item.bedrock.hide: true` 会有意省略表单按钮。

## 按钮或商品缺失

| 界面 | 检查内容 |
| --- | --- |
| 所有界面 | 来源 ID、显示条件、`menu-visibility`、菜单类型是否支持该条目 |
| 物品栏 | 根级 `layout` 标记、`size` 和槽位数 |
| 表单 | 按钮名称、`display-item.bedrock.hide`、来源槽位顺序 |
| 原生对话框 | 来源 `layout` 和适用的 `menu-visibility.dialog` / `item-action-list` |
| Ore/ArcMenu | 命名 `ui.layout.areas`、按钮 `ui.placement`、商品 `ui.products.placement`、边界与分页 |

物品栏里有按钮、ArcMenu 里没有时，先查该按钮是否设置了 `ui.placement`，再查 `ui.placement.enabled` 和 `menu-visibility.arcmenu`。根级 `layout` 只分配物品栏槽位。商品可使用 `ui.products.placement` 的默认位置。 [配置示例](arcmenu-menus-premium.md#按钮在物品栏里有arcmenu-里没有)

## 图形元素重叠或消失 <mark style="color:red;">- 付费版</mark>

检查全局/菜单 `ui.layout` 的画布和命名区域、位置索引、网格容量、卡片大小与自由坐标边界。显式菜单 `ui` 及商店 `settings.menu-settings.ui` 可以覆盖全局 `menu.ui` 默认值。调整渲染器专用选项前，先查看[图形布局 - 付费版](../menus-getting-started/ui-layout.md)。

## 重载后 Ore 布局未更新 <mark style="color:red;">- 付费版</mark>

重载服务器 YAML 不会分发新的客户端资源。执行 `/shop dialogpack` 重新生成，分发 `pack/` 内容，并确认客户端接受了更新包。检查 `textures/` 中可选 PNG 路径，以及模板验证日志。

## 搜索、收藏或数量控件行为不同

这些菜单在表单/对话框中使用原生输入与详情，在 ArcMenu 中使用图形流程，不一定逐一复现物品栏输入槽位和状态标记。结果槽位容量、已配置动作和商品权限仍然有效。见[搜索 - 付费版](search-menus-premium.md)、[收藏 - 付费版](favourite-menus-premium.md)、[批量购买](buy-more-menus.md)。

## 频繁刷新或标题闪烁

检查 `menu.menu-update.circle-update`、`click-update`、`menu.title-update`。仅在物品展示确有需要时启用周期更新。ArcMenu 使用独立的 `ui.renderers.arcmenu.update-interval`。

[⚙️配置](menu-configuration.md) · [🧱布局与按钮](layout-and-buttons.md)
