# 🛍️商店菜单

商店菜单采用「数据与模板分离」的设计：商店文件负责商品、价格与交易规则，菜单模板负责界面布局、导航按钮和呈现设置。多个商店可以共用同一个菜单模板。

## 创建商店与模板

先将模板保存为 `menus/shop_menu_templates/farming.yml`：

```yaml
title: '{shop-name}'
size: 9
layout:
  - '0000A0X00'
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

再将商店文件保存为 `shops/farming.yml`：

```yaml
settings:
  shop-name: '&aFarming'
  menu: farming
items:
  A:
    display-item:
      material: WHEAT
      name: '&eWheat'
    products:
      1:
        material: WHEAT
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 5
```

安装 Vault 与经济插件后，执行 `/shop reload` 并运行 `/shop menu farming` 即可打开。在物品栏界面中，`A` 槽位显示商店商品 `items.A`，`X` 槽位显示模板的关闭按钮（若启用了全局 `menu.force-ui` 则以强制 UI 为准）。

{% hint style="info" %}
`settings.menu: farming` 指定的是**模板 ID**，`/shop menu farming` 打开的是**商店 ID**，两者可以不同名。商店模板仅供商店引用，不能当作独立通用菜单打开。
{% endhint %}

## 为单个商店覆盖模板

如果某个商店需要在共用模板的基础上微调标题、呈现方式或图形样式，可在商店文件的 `settings.menu-settings` 中覆盖：

```yaml
settings:
  shop-name: '&aFarming'
  menu: farming
  menu-settings:
    title: '&aFarming Specials'
    dialog:
      enabled: true
      layout: multi-action
    bedrock:
      enabled: true
      content: '&7Browse farming products'
    ui:
      style: bedrock
```

覆盖配置会递归合并到模板中（`layout` 等列表会整体替换），随后再由全局图形默认值补齐缺失的 `ui` 字段。如果商店不需要复用模板，也可以省略 `settings.menu`，直接在 `settings.menu-settings` 中编写完整界面。详见[菜单配置与继承](menu-configuration.md)。

## 添加商品与导航按钮

在商店 `items` 下定义商品后，即可在模板 `layout` 中引用商品 ID；普通导航按钮既可以写在模板 `buttons` 中供多个商店复用，也可以写在商店 `buttons` 中供当前商店独享（注意避免 ID 重复）。

商品被点击时，会执行 `menu.click-event` 与商品自身的买卖配置；普通按钮则执行各自的 `actions`。图形布局只会改变外观与坐标，不会影响[商品](../shops/products.md)的定价与交易逻辑。

## 用 `shop-items` 自动填充商品槽位

在商店菜单模板中配置 `shop-items`，可以指定哪些布局字符用作通用「商品槽位」，无需为每件商品单独改模板。例如下面的四个 `P` 槽会按商店 `items` 的书写顺序，从左到右依次填入前四件商品（不足四个时多余槽位留空）：

```yaml
size: 9
shop-items: ['P']
layout:
  - '0PPPP000X'
```

如果需要按固定位置分别对应第 1、2、3 件商品，也可以写成 `shop-items: ['A', 'B', 'C']`，布局中的 `A`、`B`、`C` 会依次对应商店中的前三个商品。

其余槽位照常填写普通按钮（如 `X`）或指定商品 ID 即可，商品的显示条件与可见性依然生效。单个商店可通过 `settings.menu-settings.shop-items` 覆盖模板设置（在游戏内编辑器中该选项名为**商品槽位标记**）。

## 给物品栏商店添加翻页按钮

使用同一个 `shop-items` 标记铺设商品区域后，即可配合 `previous-page-item.<字符>` 与 `next-page-item.<字符>` 添加翻页按钮（键名为布局中的按钮字符）。每页可展示的商品数量等于 `layout` 中该商品槽位标记的总数：

```yaml
size: 18
shop-items: ['P']
layout:
  - '0PPPPPPP0'
  - 'l0000000r'

previous-page-item:
  l:
    hide-on-first-page: true
    display-item:
      material: ARROW
      name: '&e上一页'
    hidden-item:
      material: GRAY_STAINED_GLASS_PANE
      name: ' '

next-page-item:
  r:
    hide-on-last-page: true
    display-item:
      material: ARROW
      name: '&e下一页'
```

默认情况下，首页会隐藏上一页按钮、末页会隐藏下一页按钮：若配置了 `hidden-item` 则显示替代物品，否则留空；将 `hide-on-first-page` / `hide-on-last-page` 设为 `false` 则始终保留翻页箭头（点击边界页时页码不变）。翻页时仅刷新商品槽位内容，普通按钮保持不变；两者也均可在游戏内编辑器中直接修改。

## 用不同界面呈现同一商店 - 付费版

同一个商店可以同时适配多种客户端界面，每种界面各自读取不同的排版配置：

* **箱子物品栏**：按菜单根级的 `size` 和 `layout` 格子排版。
* **Bedrock 表单与原生对话框**：把选中的商品和按钮自动排成列表或动作按钮。
* **Ore 与 ArcMenu（图形商店）**：按所选图形样式的 `ui.layout` 排版，商品卡片放到 `ui.products.placement` 指定的区域，普通按钮放到各自 `ui.placement` 指定的区域。

在 **Ore / ArcMenu 图形商店**中，插件如何决定「显示哪些商品」取决于是否开启了 `dynamic-layout`：

* **未开启 `dynamic-layout`（默认）**：只要商店 `items` 里的商品满足显示条件，就算你**没有**把它写进箱子 `layout` 格子里，它也会自动出现在图形商店的商品区中。
* **开启了 `dynamic-layout: true`**：插件只显示在 `layout` 中被动态规则选中的商品；没被 `layout` 选中的商品不会出现（除非你专门为该商品单独配置了 `ui.placement`）。
* **普通按钮（返回、关闭、跳转等）**：无论是否开启动态布局，都不会自动放进图形界面，必须为按钮单独配置 `ui.placement`。所有条目也都会受 `menu-visibility` 和 `display-conditions` 控制。

[UI 模式](ui-overview.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md) · [批量购买菜单](buy-more-menus.md)
