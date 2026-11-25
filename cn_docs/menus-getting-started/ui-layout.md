# 📐图形 UI 布局 - 付费版

Ore 与 ArcMenu 共用同一套图形样式、逻辑画布、命名区域和条目定位体系。这些配置仅影响图形渲染器；原版物品栏仍使用根级 `layout`，Bedrock 表单和原生对话框则使用各自的控件。

通用菜单和商店菜单支持 Ore 与 ArcMenu 两种渲染器，搜索菜单和收藏菜单支持 ArcMenu，而批量购买与物品出售界面不使用此画布，详见 [UI 模式](../menus/ui-overview.md#supported-menu-types)。

## 从共享样式开始

在 `config.yml` 中配置全局 `menu.ui.style`，或在单个菜单根级指定 `ui.style`。内置样式 `classic` 采用左侧导航栏，`bedrock` 采用顶部导航栏，两者均预设了 `sidebar`、`content` 与 `footer` 三个命名区域。样式文件存放在 `plugins/UltimateShop/ui/`，直接编写 UI 字段（无需外层 `ui:`）。

下面是一个使用继承画布的完整通用菜单示例：

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

使用前请确保目标商店已创建；若使用 Ore，需开启全局对话框、重载插件并[生成/分发资源包](../menus/ore-menus-premium.md#generate-the-resource-pack)；若使用 ArcMenu，需安装兼容插件并开启全局与菜单开关。注意，根级物品栏 `layout` 不会决定图形界面中的坐标。

商店菜单中的商品卡片通常只需继承统一区域设置：

```yaml
ui:
  products:
    placement:
      area: content
```

## 配置作用范围

| 作用范围 | 配置位置 |
| --- | --- |
| 共享样式文件 | `ui/<style>.yml`：`layout`、`products`、`theme`、`text`、`renderers` |
| 全局图形默认值 | `config.yml` → `menu.ui` |
| 菜单级覆盖 | 菜单根级 `ui`；商店还可使用 `settings.menu-settings.ui` |
| 单个商品操作与价格 | 商品根级 `ui.actions`、`ui.price` |
| 单个条目位置 | 商品或按钮根级 `ui.placement` |
| 普通条目外观 | 条目 `ui.appearance`、`ui.font-size`、`ui.text-align` |

正常模式下，菜单显式填写的字段优先于所选样式的默认值；切换样式会覆盖全局默认值并递归补齐缺失字段。强制样式则会覆盖本地默认值，详见[菜单配置与继承](../menus/menu-configuration.md)。

商品的图形操作与价格按「商品 → 所属商店菜单 → 全局默认值」的顺序读取，卡片外观设置则对整个菜单生效。Bedrock 表单的价格行使用 `bedrock.price-extra-line`，与图形价格相互独立。

## 画布与区域

以下菜单片段定义了一个完整的 classic 风格画布：

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

由于配置段采用递归合并，单独修改 `header.title` 不会移除继承来的其他页眉；若不需要某个页眉，请显式设置 `enabled: false`。

| 相对于 `ui.layout` 的路径 | 用途 |
| --- | --- |
| `canvas.width`、`canvas.height` | 逻辑画布尺寸：宽 `144–900`，高 `144–540` |
| `header.<id>.enabled` / `text` | 命名页眉的开关与文本 |
| `header.<id>.x` / `y` / `width` / `height` | 页眉位置与尺寸，默认高度 `27` |
| `header.<id>.align` | 对齐方式：`left`、`center` 或 `right` |
| `header.<id>.font-size`、`bold` | ArcMenu 文字缩放与粗体 |
| `areas.<id>.type` | 区域类型：`grid`、`stack` 或 `free` |
| `areas.<id>.x` / `y` / `width` / `height` | 区域在画布中的位置与边界 |
| `areas.<id>.columns` / `rows` | 网格列数 `1–36`、行数 `1–60` |
| `areas.<id>.cell-height` | 网格单元格高度（宽度由区域宽度、列数与间距自动计算） |
| `areas.<id>.direction` | 堆叠方向：默认 `vertical`，可选 `horizontal` |
| `areas.<id>.item-width` / `item-height` | 堆叠条目宽高，默认 `108` / `36` |
| `areas.<id>.gap`、`gap-x` / `gap-y` | 条目间距（`gap` 为横纵间距后备值） |
| `areas.<id>.background-enabled` / `background-padding` | ArcMenu 区域背景与内边距 |
| `pagination.enabled` | 是否启用图形分页控件 |
| `pagination.previous` / `indicator` / `next` | 分页按钮与页码指示器，各自支持 `enabled`、`x`、`y`、`width`、`height` |

页眉文本支持 `{title}`、`{content}`、语言变量与 PlaceholderAPI。其中 `{content}` 在 Ore 下读取 `dialog.content`，在 ArcMenu 下读取 `arcmenu.content`，未设置时回退到菜单根级 `content`。

## 区域行为与分页

* **网格（Grid）**：优先放置指定了显式行列坐标的条目，其余条目按顺序填充。例如 `3 × 2` 区域每页可容纳 6 个条目，超出后自动分页；行/列均从 `0` 开始编号，行索引超出单页行数时会落入后续页面，列索引不可超出总列数。
* **堆叠（Stack）**：按 `placement.order` 纵向或横向连续排列，不产生分页，所有条目必须能放入区域内。例如内置 `bedrock` 样式的横向 `sidebar` 可容纳 5 个导航按钮，且网格翻页时保持常驻。
* **自由（Free）**：按区域内部偏移坐标定位，必须同时提供 `placement.x` 与 `placement.y`。默认条目尺寸为 `108 × 27`，放大后仍需位于区域边界内。

{% hint style="warning" %}
**几何校验规则**：画布高度、所有元素的 Y 坐标/高度以及纵向间距都必须是 **9 的倍数**；元素边界不得超出父区域；矩形最小宽度 `24`、最小高度 `9`；非自由区域单元格最小高度 `27`；所有 `appearance: card` 最小高度 `99`。可见条目之间、条目与页眉或启用的分页控件之间均不得重叠。
{% endhint %}

对于搜索菜单与收藏菜单，系统会先按根级结果槽位数量截取来源条目，再进行图形排版；扩大网格尺寸或增加图形页数不会突破来源容量上限。

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

上例中，目标 `search` 菜单需已存在（付费版功能），`close` 按钮则相对于 `footer` 原点定位；两个按钮即使不写在根级 `layout` 中也能在图形界面显示。

| 条目配置键 | 用途 |
| --- | --- |
| `ui.placement.enabled` | 存在位置配置时默认 `true`；设为 `false` 可关闭图形定位 |
| `ui.placement.area` | 所属命名区域 ID |
| `ui.placement.order` | 排序优先级，默认沿用来源槽位顺序 |
| `ui.placement.row` / `column` | 显式网格单元格（设置 `row` 后即启用显式单元格定位） |
| `ui.placement.x` / `y` | 自由区域内部偏移坐标 |
| `ui.placement.width` / `height` | 覆盖条目尺寸 |
| `ui.appearance` | `button` 或 `card`，商品默认使用 `card` |
| `ui.font-size` | ArcMenu 普通按钮文字缩放 |
| `ui.text-align` | 普通图形按钮文字对齐方式 |
| `menu-visibility.ore` / `arcmenu` | 条目根级的图形呈现可见性 |

普通条目需要各自配置位置；商品会继承 `ui.products.placement` 并合并自身的 `ui.placement` 覆盖。`appearance: card` 也可用于分类或导航按钮。

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
| `card.icon-size`、`padding` | 物品图标大小与卡片内边距 |
| `card.name-size` / `price-size` | ArcMenu 商品名称与价格文字缩放 |
| `card.button-font-size` / `button-height` | ArcMenu 操作按钮文字缩放与高度 |
| `card.buttons.inset-x` / `inset-bottom` / `gap` | ArcMenu 操作按钮边距与间距 |
| `card.lore-lines` | Ore 商品正文描述最大行数 |
| `card.description-lines` | Ore 分类说明最大行数（ArcMenu 会按可用空间自适应） |
| `price.enabled` | 是否显示紧凑价格行 |
| `price.formats.both` / `only-buy` / `only-sell` | 根据可用交易方向选择的价格文本格式 |
| `actions.primary` | 按优先顺序列出的主要操作 ID，最多显示两个可用操作 |
| `actions.extra` | `auto` 表示显示其余操作；也可指定列表，`[]` 表示不显示额外操作 |
| `actions.labels.<id>` | 操作按钮文本，支持 `{item-name}` 与常规占位符 |

操作 ID 来源于商品点击绑定与自定义 `menu.click-event-actions`（在 `primary` 中写出 `buy` 不会凭空生成缺失的点击绑定，且受价格、商品选项与仅买/仅卖限制过滤）。

商品可在根级覆盖 `ui.price` 与 `ui.actions`，而卡片外观设置对整个菜单统一生效。紧凑价格行表示单份交易价格（已套用共享的动态价格与倍率计算），原始 Lore 与自动附加描述仍会在详情/悬停面板中显示，不同呈现方式可用[描述呈现条件](../menus/display-item-add-lore.md#presentation-conditions)区分。

## 主题与文本

YAML 颜色值必须加引号。ArcMenu 同时支持 `#RRGGBB` 与 `#AARRGGBB`，Ore 生成的 RGB 纹理会忽略透明度。

| 相对于 `ui.theme` 的路径 | 用途 |
| --- | --- |
| `background-enabled`、`background` | ArcMenu 窗口背景 |
| `sidebar`、`card`、`border`、`accent`、`text` | 共享面板/卡片/边框/强调色与 ArcMenu 基础文字色 |
| `button.background` / `hover` / `border` / `text-align` | 普通图形按钮样式 |
| `buy.background` / `hover`、`sell.background` / `hover` | 买卖按钮样式 |
| `title.background-enabled` / `background` / `decoration-enabled` | 独立的 ArcMenu 标题栏背景与装饰 |
| `decorations.enabled` / `style` / `color` / `corner-color` | ArcMenu 边框与角饰外观 |
| `decorations.thickness` / `corner-thickness` / `corner-length` / `inset` | 装饰线条尺寸与内缩 |
| `decorations.dividers-enabled` | ArcMenu 页眉分隔装饰 |
| `bevel.enabled` / `shadow` / `top` / `left` / `right` / `bottom` | ArcMenu 控件斜边默认值 |
| `tooltip.border` / `background` | ArcMenu 悬停提示面板颜色 |
| `details.background`、`more-menu.background` | ArcMenu 详情面板与更多操作弹窗背景 |

ArcMenu 的 `buy`、`sell`、`quantity`、`pagination`、`cancel` 等专用主题段还支持各自的 `text` 与斜边颜色（斜边颜色会与控件基础颜色合成）。

标题背景可以独立于窗口背景启用：

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

装饰 `style` 支持 `frame`、`corners`、`both` 与 `none`（`none` 不绘制外框，但仍可保留页眉分隔装饰）。尺寸范围：线条粗细 `0.5–6`、角饰粗细 `0.5–8`、角长 `0–80`、内缩 `0–30`。

图形界面中的固定标签位于 `ui.text`（包含 `cancel`、`no-extra`、`amount-input`、`select-product`、`favourite-edit.forward/backward/remove`），支持引用语言文件进行本地化。

## 渲染器专用设置

Ore 的纹理模板位于 `ui.renderers.ore.templates`，ArcMenu 的交互、动画与提示框配置位于 `ui.renderers.arcmenu`（其中 `update-interval` 仅支持全局配置）。由于两者渲染机制不同，共享同一套几何布局并不代表字体大小与卡片正文显示效果完全一致。

[Ore UI](../menus/ore-menus-premium.md) · [ArcMenu UI](../menus/arcmenu-menus-premium.md) · [配置兼容性](../menus/migration-guide.md)
