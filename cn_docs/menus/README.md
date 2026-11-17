# 📚菜单系统

菜单用于定义玩家打开的界面。菜单**类型**决定内容与控件，**呈现方式**决定显示为物品栏、表单、对话框、Ore 还是 ArcMenu。商品、价格和交易条件仍配置在商店文件中。

本章节所有路径均相对于 `plugins/UltimateShop/`。菜单和商店 ID 都是不含 `.yml` 的文件名。请保持 ID 唯一，避免命令和动作打开错误界面。

## 创建第一个菜单

将以下完整示例保存为 `menus/common_menus/hello.yml`：

```yaml
title: '&8Hello'
size: 9
layout:
  - '0000X0000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

执行 `/shop reload`，再执行 `/shop menu hello`。会打开一个单行物品栏菜单，第五个槽位是关闭按钮。此示例使用正常的界面开关；若启用全局 `menu.force-ui`，则以强制设置为准。

布局中每个字符代表一个槽位。`X` 引用 `buttons.X`，未定义的 `0` 留空。多字符 ID、点击动作和可见性见[布局与按钮](layout-and-buttons.md)。

## 选择菜单类型

| 类型 | 配置位置 | 玩家操作流程 |
| --- | --- | --- |
| [通用菜单](general-menus.md) | `menus/common_menus/<id>.yml` | 导航和自定义动作 |
| [商店菜单](shop-menus.md) | `shops/<id>.yml` 与 `menus/shop_menu_templates/<template>.yml` | 浏览并交易商品 |
| [批量购买菜单](buy-more-menus.md) | `menus/buy_more_menus/<id>.yml` | 为已选商品选择数量 |
| [搜索菜单 - 付费版](search-menus-premium.md) | `menus/search_menus/<id>.yml` | 按物品或名称搜索商品 |
| [收藏菜单 - 付费版](favourite-menus-premium.md) | `menus/favourite_menus/<id>.yml` | 收藏商品并调整顺序 |
| [物品出售菜单](item-sell-menus.md) | `menus/item_sell_menus/<id>.yml` | 放入物品、预览价值并确认出售 |
| [全部出售菜单](sell-all-menu.md) | `config.yml` → `menu.sell-all` | 放入物品，关闭时出售 |

通用、商店、搜索、收藏和物品出售菜单均可通过 `/shop menu <id>` 打开。批量购买菜单必须先选中商品；全部出售菜单使用 `/shop sellall` 或对应菜单动作打开。

## 选择呈现方式

物品栏界面使用根级 `size` 和 `layout`。付费版还提供 Bedrock 表单、Java 原生对话框、图形化 Ore 和 ArcMenu。可用性取决于菜单类型、服务器平台和已安装的对接插件。支持范围与选择顺序见 [UI 模式](ui-overview.md)。

图形样式位于 `ui/`。通过全局 `menu.ui.style` 或菜单 `ui.style` 选择共享样式，通过 `ui.layout` 和条目 `ui.placement` 设置坐标。`bedrock` 样式只是图形预设，与 Bedrock 表单是独立概念。

## 继续配置

- [配置与继承](menu-configuration.md)：文件、模板、样式与覆盖优先级。
- [布局与按钮](layout-and-buttons.md)：槽位 ID、动作、条件与可见性。
- [图形 UI 布局 - 付费版](../menus-getting-started/ui-layout.md)：区域、卡片、分页与主题。
- [条件显示 - 付费版](conditional-display-premium.md)：按玩家选择条目及后备选项。
- [展示物品的附加描述](display-item-add-lore.md)：价格、限额和各呈现方式的操作提示。
- [配置兼容性](migration-guide.md)：当前配置键与迁移示例。

通用格式：[展示物品](../format/display-item-format.md)、[动作](../format/action-format.md)、[条件](../format/condition-format.md)。
