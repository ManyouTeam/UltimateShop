# 📚菜单系统

菜单负责定义玩家在游戏中打开的界面。其中，**菜单类型**决定界面承载的内容与交互流程，**呈现方式**决定它以物品栏、Bedrock 表单、原生对话框、Ore 还是 ArcMenu 的形式展示；商品本身的价格、库存与交易条件仍保留在商店配置文件中。

{% hint style="info" %}
本章节提到的所有文件路径均相对于 `plugins/UltimateShop/`。菜单 ID 与商店 ID 均为不含 `.yml` 后缀的文件名，请确保各自目录内的 ID 唯一，避免命令或跳转动作打开错误的界面。
{% endhint %}

## 创建第一个菜单

将下面这段完整配置保存为 `menus/common_menus/hello.yml`：

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

保存后执行 `/shop reload`，再运行 `/shop menu hello`，即可打开一个单行物品栏菜单，中间（第 5 格）是一个关闭按钮。此示例关闭了其他呈现方式，仅保留物品栏界面；如果 `config.yml` 中启用了全局 `menu.force-ui`，则会以强制 UI 设置为准。

在 `layout` 中，每个字符代表一个格子：`X` 对应 `buttons.X`，未定义对应按钮的 `0` 则会留空。关于多字符 ID、点击动作、条件与可见性控制，请参见[布局与按钮](layout-and-buttons.md)。

## 选择菜单类型

| 类型 | 配置位置 | 玩家交互流程 |
| --- | --- | --- |
| [通用菜单](general-menus.md) | `menus/common_menus/<id>.yml` | 分类导航、信息展示与自定义动作 |
| [商店菜单](shop-menus.md) | `shops/<id>.yml` 与 `menus/shop_menu_templates/<template>.yml` | 浏览商店商品并进行买卖交易 |
| [批量购买菜单](buy-more-menus.md) | `menus/buy_more_menus/<id>.yml` | 为已选中的商品选择购买/出售数量 |
| [搜索菜单 - 付费版](search-menus-premium.md) | `menus/search_menus/<id>.yml` | 按物品或名称搜索全服商店商品 |
| [收藏菜单 - 付费版](favourite-menus-premium.md) | `menus/favourite_menus/<id>.yml` | 收藏常用商品并调整展示顺序 |
| [物品出售菜单](item-sell-menus.md) | `menus/item_sell_menus/<id>.yml` | 放入物品、预览回收总价并确认出售 |
| [全部出售菜单](sell-all-menu.md) | `config.yml` → `menu.sell-all` | 放入物品，关闭界面时一键出售 |

通用、商店、搜索、收藏和物品出售菜单均可直接通过 `/shop menu <id>` 打开；批量购买菜单需要先选中某件商品才能进入；全部出售菜单则通过 `/shop sellall` 或对应的菜单动作打开。

## 选择呈现方式

原版物品栏界面通过菜单根级的 `size` 和 `layout` 进行排版。付费版额外支持 **Bedrock 表单**、**Java 原生对话框**、**图形化 Ore** 以及 **ArcMenu**，具体可用性取决于菜单类型、服务端平台和已安装的联动插件（详见 [UI 模式](ui-overview.md)）。

Ore 与 ArcMenu 使用 `ui/` 目录下的图形样式文件：可通过全局 `menu.ui.style` 或单个菜单的 `ui.style` 指定样式，并通过 `ui.layout` 与条目的 `ui.placement` 控制坐标。

{% hint style="info" %}
`ui.style: bedrock` 只是 Ore / ArcMenu 的一套图形外观预设，与面向基岩版玩家的 Bedrock 表单（Form UI）是两个完全独立的概念。
{% endhint %}

## 进阶与相关阅读

* [菜单配置与继承](menu-configuration.md)：文件结构、商店模板、图形样式与覆盖优先级。
* [布局与按钮](layout-and-buttons.md)：槽位 ID、按钮动作、条件与呈现可见性。
* [图形 UI 布局 - 付费版](../menus-getting-started/ui-layout.md)：画布、区域、商品卡片、分页与主题。
* [条件显示 - 付费版](conditional-display-premium.md)：按玩家条件切换条目与后备方案。
* [展示物品的附加描述](display-item-add-lore.md)：价格、限额与不同呈现方式下的提示文本。
* [菜单配置兼容性](migration-guide.md)：当前配置键一览与旧版迁移说明。

通用配置格式参考：[展示物品格式](../format/display-item-format.md)、[动作格式](../format/action-format.md)、[条件格式](../format/condition-format.md)。
