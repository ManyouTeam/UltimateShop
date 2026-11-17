# 🛏️Bedrock 表单 UI - 付费版

Bedrock 表单以列表展示菜单条目，并提供独立的商品信息和数量输入表单。它与 Java 界面共用商品、价格和交易规则，使用 Bedrock 原生控件，不使用图形 `ui.layout` 坐标。

## 启用表单

需要受支持的 Geyser/Floodgate 环境，并正确检测 Bedrock 玩家。在 `config.yml` 中设置：

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
```

在通用菜单中，例如 `menus/common_menus/categories.yml`：

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
bedrock:
  enabled: true
  content: '&7Choose a category'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
```

创建 `farming` 商店，重载后由 Bedrock 玩家执行 `/shop menu categories`。正常选择时，全局与菜单开关都必须开启。全局 `menu.force-ui` 可覆盖开关，但平台与对接要求仍有效。

## 条目列表与可见性

表单从根布局收集条目，忽略箱子界面的空槽间距。商店商品排在普通导航控件前面。按钮执行 `LEFT` 点击，商品通常打开其信息表单。

按钮/商品设置 `menu-visibility.form: false` 可隐藏装饰条目。当前展示物品的 `bedrock.hide: true`，或空的按钮名称，也会使表单按钮不显示。

## 图标与补充文本

在当前生效的 `display-item` 中填写：

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
      bedrock:
        icon: 'path;;textures/items/wheat.png'
        extra-line: '&7Seeds, crops and food'
        hide: false
```

| `display-item.bedrock` 配置键 | 含义 |
| --- | --- |
| `icon` | `path;;<客户端纹理路径>` 或 `url;;<公开图片 URL>` |
| `extra-line` | 按钮补充文本，商品支持价格占位符 |
| `hide` | 在表单中隐藏此展示变体 |

`path;;` 指向 Bedrock 客户端资源，`url;;` 指向客户端可以加载的图片。未显式设置图标时，自动图标会通过 `bedrock.auto-add-icon` 尝试使用原版材质纹理。Java 自定义模型数据不会自动提供 Bedrock 纹理。条件展示物品的图标选项应写在各自变体内。

对于商品，显式的 `display-item.bedrock.extra-line` 优先；`''` 表示有意留空。否则先读取菜单 `bedrock.price-extra-line`，再回退到全局 `menu.bedrock.price-extra-line`：

```yaml
bedrock:
  price-extra-line:
    default: '&7Buy: {buy-price} | Sell: {sell-price}'
    only-buy: '&7Buy: {buy-price}'
    only-sell: '&7Sell: {sell-price}'
```

此文本与图形 `ui.products.price` 独立。

## 商品直接操作

如需直接执行商品操作而非打开信息界面，在商店文件的商品根级添加：

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

读取顺序为商品 `bedrock.click-event` → 所属商店菜单 `bedrock.click-event` → 全局 `menu.bedrock.click-event`。只有商品的 `LEFT` 交互使用此快捷操作。可用绑定、交易条件以及需要检查实际物品的出售转向规则仍然有效。

## 表单文本参考

受支持选项先读取菜单 `bedrock.*`，再读取全局 `menu.bedrock.*`。玩家检测仅支持全局配置。

| 相对配置键 | 用途 |
| --- | --- |
| `enabled` | 允许表单 |
| `content` | 菜单说明，后备为菜单根级 `content` |
| `check-method` | 全局检测方式：`FLOODGATE` 或 `UUID` |
| `not-auto-close` | 受支持的信息表单交易后重新打开 |
| `auto-add-icon.enabled/format` | 原版图标生成，格式支持 `{version}`、`{path}` |
| `price-extra-line.default/only-buy/only-sell` | 商品价格补充文本 |
| `click-event` | 默认商品直接操作 |
| `info.title`、`info.buttons.buy/sell/buy-more/sell-all/back` | 商品信息文本 |
| `buy-or-sell.title`、`buy-or-sell.buttons.amount.name/tip` | 数量输入文本 |
| `search.input`、`search.buttons.search` | 搜索输入与按钮 |
| `favourite-edit.title/content/buttons.*` | 收藏编辑文本 |

`{lang}` 使用匹配的语言覆盖。表单商品动作来自可用点击操作。图形 `ui.products.actions` 与物品栏批量购买确认标签不会定义原生表单控件。

搜索和收藏使用专用表单流程。物品出售与全部出售保留实际物品栏。图形 `ui.style: bedrock` 是 Ore/ArcMenu 独立的外观预设。

[UI 模式](ui-overview.md) · [批量购买](buy-more-menus.md) · [搜索](search-menus-premium.md) · [收藏](favourite-menus-premium.md)
