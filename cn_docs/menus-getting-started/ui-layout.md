# 📐图形 UI 布局 - 付费版

Ore 与 ArcMenu 共用图形样式、逻辑画布、命名区域和条目位置。这些设置只定位图形控件；物品栏使用根级 `layout`，原生表单/对话框使用各自控件。

通用/商店支持两种图形渲染器，搜索/收藏支持 ArcMenu。批量购买和实际物品出售界面不使用此画布，见 [UI 模式](../menus/ui-overview.md#supported-menu-types)。

## 从共享样式开始

在 `config.yml` 选择全局 `menu.ui.style`，或使用菜单根级 `ui.style`。内置 `classic` 使用左侧导航，`bedrock` 使用顶部导航，二者都提供 `sidebar`、`content`、`footer` 命名区域。样式文件位于 `plugins/UltimateShop/ui/`，直接包含 UI 字段，不需要外层 `ui`。

以下完整通用菜单使用继承的画布：

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
arcmenu:
  enabled: false
ui:
  style: classic
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
```

创建目标商店。Ore 需要启用全局对话框，重载并[生成/分发资源包](../menus/ore-menus-premium.md#generate-the-resource-pack)。ArcMenu 需要兼容对接，并启用全局/菜单开关。根级物品栏布局不会决定图形坐标。

商店商品卡片通常继承以下位置：

```yaml
ui:
  products:
    placement:
      area: content
```

## 配置作用范围

| 范围 | 配置 |
| --- | --- |
| 共享样式文件 | `ui/<style>.yml`：`layout`、`products`、`theme`、`text`、`renderers` |
| 全局图形默认值 | `config.yml` → `menu.ui` |
| 菜单覆盖 | 菜单根级 `ui`；商店还可使用 `settings.menu-settings.ui` |
| 单个商品操作/价格 | 商品根级 `ui.actions`、`ui.price` |
| 单个条目位置 | 商品/按钮根级 `ui.placement` |
| 普通条目外观 | 条目 `ui.appearance`、`ui.font-size`、`ui.text-align` |

正常选择时，显式菜单字段优先于所选默认值。不同的菜单样式会覆盖全局默认值，再递归补齐缺失字段。强制样式可替换本地默认值，见[配置与继承](../menus/menu-configuration.md)。

图形商品操作/价格依次读取：商品 → 所属商店菜单 → 全局默认值。卡片外观对整个菜单生效。原生表单价格行使用 `bedrock.price-extra-line`，与图形价格独立。

## 画布与区域

以下菜单片段提供完整的 classic 风格画布：

```yaml
ui:
  layout:
    canvas:
      width: 648
      height: 405
    header:
      title:
        enabled: true
        text: '{title}'
        x: 18
        y: 9
        width: 570
        height: 27
        align: left
        font-size: 1.3
      content:
        enabled: true
        text: '{content}'
        x: 18
        y: 36
        width: 570
        height: 27
        align: left
        font-size: 0.8
    areas:
      sidebar:
        type: stack
        direction: vertical
        x: 18
        y: 90
        width: 108
        height: 270
        item-height: 36
        gap: 9
      content:
        type: grid
        x: 144
        y: 90
        width: 486
        height: 270
        columns: 3
        rows: 2
        cell-height: 126
        gap-x: 9
        gap-y: 9
      footer:
        type: free
        x: 18
        y: 369
        width: 612
        height: 27
    pagination:
      enabled: true
      previous:
        x: 297
        y: 369
        width: 36
        height: 27
      indicator:
        x: 342
        y: 369
        width: 90
        height: 27
      next:
        x: 441
        y: 369
        width: 36
        height: 27
```

样式配置段递归合并。修改 `header.title` 不会移除继承的其他命名页眉；不需要的页眉应设置 `enabled: false`。

| 相对于 `ui.layout` 的路径 | 用途 |
| --- | --- |
| `canvas.width`、`canvas.height` | 逻辑尺寸：宽 `144–900`，高 `144–540` |
| `header.<id>.enabled/text` | 命名页眉的可见性与文本 |
| `header.<id>.x/y/width/height` | 页眉边界，默认高度 `27` |
| `header.<id>.align` | `left`、`center` 或 `right` |
| `header.<id>.font-size`、`bold` | ArcMenu 文字缩放与粗体 |
| `areas.<id>.type` | `grid`、`stack` 或 `free` |
| `areas.<id>.x/y/width/height` | 区域在画布中的边界 |
| `areas.<id>.columns/rows` | 网格列数 `1–36`、行数 `1–60` |
| `areas.<id>.cell-height` | 单元格高度，宽度由区域宽度、列数和间距计算 |
| `areas.<id>.direction` | 堆叠方向：默认 `vertical`，也可 `horizontal` |
| `areas.<id>.item-width` | 横向堆叠条目宽度，默认 `108` |
| `areas.<id>.item-height` | 堆叠条目高度，默认 `36` |
| `areas.<id>.gap` | 横纵间距的后备值 |
| `areas.<id>.gap-x/gap-y` | 单独覆盖横纵间距 |
| `areas.<id>.background-enabled/background-padding` | ArcMenu 区域背景与内边距 |
| `pagination.enabled` | 图形分页控件 |
| `pagination.previous/indicator/next` | 各自支持 `enabled`、`x`、`y`、`width`、`height` |

页眉支持 `{title}`、`{content}` 以及常规语言/PlaceholderAPI 文本。Ore 说明来自 `dialog.content`，ArcMenu 来自 `arcmenu.content`，再回退到菜单根级 `content`。

## 区域行为与分页

**网格（Grid）**按顺序填充单元格，优先预留显式行列位置。`3 × 2` 区域每页容纳六个条目，超出时生成后续页。行列从零编号，超过第一页行数的行索引表示后续页面，列索引必须在配置列数范围内。

**堆叠（Stack）**按 `placement.order` 纵向或横向排列，不会创建额外页面，所有条目必须放得下。内置 Bedrock 样式的横向 `sidebar` 容纳五个导航按钮，网格翻页时仍保持显示。

**自由（Free）**使用区域内部偏移，必须同时提供 `placement.x`、`placement.y`。默认条目尺寸为 `108 × 27`，扩大后仍须位于区域边界内。

画布高度、所有元素的 Y 坐标/高度及纵向间距都必须是 9 的倍数。边界不得超出父区域。矩形最小宽度 24、最小高度 9；非自由布局单元格最小高度 27；所有 `appearance: card` 最小高度 99。可见条目不得彼此重叠，也不得与页眉或启用的分页控件重叠。

搜索/收藏先按根级结果槽位数量限制来源条目，再进行图形排列。扩大网格或增加图形页面不会扩大来源容量。

## 条目位置

```yaml
buttons:
  search:
    display-item:
      material: HOPPER
      name: '&eSearch'
    actions:
      1:
        type: open_menu
        menu: search
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
      font-size: 1.0
      text-align: left
  close:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
    ui:
      placement:
        area: footer
        x: 504
        y: 0
        width: 108
        height: 27
```

目标搜索菜单必须存在，并需要付费版。关闭按钮相对于页脚原点定位。两个控件即使没有根级布局标记，也可以在图形界面显示。

| 条目配置键 | 用途 |
| --- | --- |
| `ui.placement.enabled` | 存在位置配置时默认 `true`，`false` 关闭图形定位 |
| `ui.placement.area` | 命名区域 ID |
| `ui.placement.order` | 排序优先级，默认来源槽位 |
| `ui.placement.row/column` | 显式网格单元格，设置行后启用显式单元格定位 |
| `ui.placement.x/y` | 自由区域偏移 |
| `ui.placement.width/height` | 覆盖条目尺寸 |
| `ui.appearance` | `button` 或 `card`，商品默认卡片 |
| `ui.font-size` | ArcMenu 普通按钮文字缩放 |
| `ui.text-align` | 普通图形按钮文字对齐 |
| `menu-visibility.ore/arcmenu` | 条目根级的呈现可见性 |

普通条目需要自身位置；商品继承 `ui.products.placement`，再合并自身覆盖。`appearance: card` 也可用于分类/导航卡片。空侧栏或动作本身都不会自动生成导航控件。

## 商品卡片、价格与操作

```yaml
ui:
  products:
    placement:
      area: content
    card:
      icon-size: 32
      padding: 9
      name-size: 1.0
      price-size: 0.85
      button-font-size: 1.0
      button-height: 27
      buttons:
        inset-x: 6
        inset-bottom: 6
        gap: 3
      lore-lines: 6
      description-lines: 2
    price:
      enabled: true
      formats:
        both: '&6Buy: {buy-price} | Sell: {sell-price}'
        only-buy: '&6Buy: {buy-price}'
        only-sell: '&6Sell: {sell-price}'
    actions:
      primary: [buy, sell]
      extra: auto
      labels:
        buy: 'Buy'
        sell: 'Sell'
```

| 相对于 `ui.products` 的路径 | 用途 |
| --- | --- |
| `card.icon-size`、`padding` | 物品图标大小与内容内边距 |
| `card.name-size/price-size` | ArcMenu 商品名称/价格文字缩放 |
| `card.button-font-size/button-height` | ArcMenu 操作按钮文字与高度 |
| `card.buttons.inset-x/inset-bottom/gap` | ArcMenu 操作按钮位置 |
| `card.lore-lines` | Ore 商品正文描述最大行数 |
| `card.description-lines` | Ore 分类说明最大行数，ArcMenu 按可用空间适配说明 |
| `price.enabled` | 紧凑商品价格行 |
| `price.formats.both/only-buy/only-sell` | 按商品可用交易方向选择格式 |
| `actions.primary` | 按优先顺序列出的 ID，最多显示两个可用主要操作 |
| `actions.extra` | `auto` 显示剩余操作，也可指定列表，`[]` 表示无额外操作 |
| `actions.labels.<id>` | 操作标签，支持 `{item-name}` 与常规文本占位符 |

操作 ID 来自商品点击绑定和自定义 `menu.click-event-actions`。列出 `buy` 不会自动创建缺失的绑定。价格、商品选项及自定义仅购买/仅出售限制会过滤可用操作。

商品可在根级覆盖 `ui.price`、`ui.actions`，卡片样式仍对整个菜单生效。紧凑价格表示一个交易单位，使用共享的动态价格/倍率计算。信息与详情使用各自适用数量。原始描述及适用的自动描述仍可在信息/提示框中显示；不同提示可用[描述呈现条件](../menus/display-item-add-lore.md#presentation-conditions)区分。

## 主题与文本

YAML 颜色必须加引号。ArcMenu 接受 `#RRGGBB`、`#AARRGGBB`，Ore 生成的 RGB 纹理会忽略透明度。

| 相对于 `ui.theme` 的路径 | 用途 |
| --- | --- |
| `background-enabled`、`background` | ArcMenu 窗口背景 |
| `sidebar`、`card`、`border`、`accent`、`text` | 共享面板/卡片/强调颜色及 ArcMenu 基础文字色 |
| `button.background/hover/border/text-align` | 普通图形控件 |
| `buy.background/hover`、`sell.background/hover` | 交易控件 |
| `title.background-enabled/background/decoration-enabled` | 独立的 ArcMenu 标题背景与装饰 |
| `decorations.enabled/style/color/corner-color` | ArcMenu 边框与角饰样式 |
| `decorations.thickness/corner-thickness/corner-length/inset` | 装饰尺寸 |
| `decorations.dividers-enabled` | ArcMenu 页眉强调装饰 |
| `bevel.enabled/shadow/top/left/right/bottom` | ArcMenu 控件斜边默认值 |
| `tooltip.border/background` | ArcMenu 悬停面板颜色 |
| `details.background`、`more-menu.background` | ArcMenu 详情与弹窗颜色 |

ArcMenu 的 `buy`、`sell`、`quantity`、`pagination`、`cancel` 等控件专用主题段可提供自身的 `text` 与斜边颜色。斜边颜色会与控件基础颜色合成。

标题背景可独立于窗口背景启用：

```yaml
ui:
  theme:
    background-enabled: false
    title:
      background-enabled: true
      background: '#EF0C1223'
      decoration-enabled: true
    decorations:
      enabled: true
      style: corners
      color: '#65E8EF'
      corner-color: '#65E8EF'
      thickness: 2
      corner-thickness: 2
      corner-length: 20
      inset: 10
      dividers-enabled: true
```

装饰 `style` 支持 `frame`、`corners`、`both`、`none`。`none` 不绘制外框，但仍允许页眉强调装饰。尺寸范围：厚度 `0.5–6`，角饰厚度 `0.5–8`，角长 `0–80`，内缩 `0–30`。标题背景与装饰遵循标题页眉的配置边界。

图形标签位于 `ui.text`：`cancel`、`no-extra`、`amount-input`、`select-product`、`favourite-edit.forward/backward/remove`，可用语言引用本地化。

## 渲染器专用设置

Ore 纹理模板位于 `ui.renderers.ore.templates`，ArcMenu 交互、动画、提示框位于 `ui.renderers.arcmenu`。ArcMenu 刷新 `update-interval` 仅支持全局配置。共享几何布局不保证两种渲染器的字体和卡片正文完全一致。

[Ore](../menus/ore-menus-premium.md) · [ArcMenu](../menus/arcmenu-menus-premium.md) · [配置兼容性](../menus/migration-guide.md)
