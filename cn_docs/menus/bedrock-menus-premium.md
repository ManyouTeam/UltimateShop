# 🛏️Bedrock 表单 UI - 付费版

Bedrock 表单（Form UI）将菜单条目转换为基岩版原生的列表按钮，并提供独立的商品详情与数量输入表单。它与 Java 界面共用同一套商品、价格和交易规则，但不使用图形 `ui.layout` 坐标。

## 启用表单

使用前需具备受支持的 Geyser / Floodgate 环境以正确识别基岩版玩家。在 `config.yml` 中配置：

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
```

在通用菜单（例如 `menus/common_menus/categories.yml`）中启用：

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

使用前请确保已经创建了 `farming` 商店。重载插件后，用 Bedrock 客户端进入游戏并执行 `/shop menu categories` 即可看到表单界面。正常模式下需要全局与菜单自身的 `bedrock.enabled` 都为 `true`（如果在 `config.yml` 开启了全局 `menu.force-ui`，则会以强制设置为准，但仍需满足平台与对接插件要求）。

## 条目列表与可见性

表单会从根级 `layout` 收集有效条目，并自动忽略物品栏的空槽位；在商店菜单中，商品条目会排在普通导航按钮之前。点击普通按钮相当于物品栏的 `LEFT`（左键）点击，点击商品默认会打开商品信息表单。

如需在表单中隐藏背景玻璃板等装饰物，可设置 `menu-visibility.form: false`；如果当前生效的展示物品设置了 `bedrock.hide: true` 或按钮名称为空，同样不会在表单中显示。

## 图标与补充文本

在当前生效的 `display-item` 中配置 Bedrock 表单的专属图标与补充说明：

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

| `display-item.bedrock` 配置键 | 用途 |
| --- | --- |
| `icon` | 图标来源：`path;;<基岩版客户端纹理路径>` 或 `url;;<公网图片 URL>` |
| `extra-line` | 按钮标题下方的补充行（商品支持价格占位符） |
| `hide` | 在表单中隐藏该展示物品 |

未显式指定 `icon` 时，若开启了 `bedrock.auto-add-icon`，插件会尝试自动匹配原版材质纹理（注意 Java 端的 `custom-model-data` 不会自动提供 Bedrock 纹理）。如果是带条件切换的展示物品，需要将 `bedrock` 图标选项写在各自的展示变体内部。

对于商品，若显式配置了 `display-item.bedrock.extra-line` 则优先使用（设为 `''` 表示有意留空）；未配置时依次读取菜单 `bedrock.price-extra-line` 与全局 `menu.bedrock.price-extra-line`：

```yaml
bedrock:
  price-extra-line:
    default: '&7Buy: {buy-price} | Sell: {sell-price}'
    only-buy: '&7Buy: {buy-price}'
    only-sell: '&7Sell: {sell-price}'
```

此文本配置与图形界面中的 `ui.products.price` 相互独立。

## 商品直接操作

如果希望 Bedrock 玩家点击商品时直接购买/出售，而不是先进入详情表单，可在商店文件的商品根级配置 `bedrock.click-event`：

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

读取优先级为：商品 `bedrock.click-event` → 所属商店菜单 `bedrock.click-event` → 全局 `menu.bedrock.click-event`（仅影响表单中的 `LEFT` 点击，且仍受点击绑定、交易条件与「需要检查实际物品时转到物品出售菜单」规则限制）。

## 表单文本与选项参考

以下选项优先读取菜单 `bedrock.*`，未设置时回退到全局 `menu.bedrock.*`（`check-method` 仅支持全局配置）：

| 相对配置键 | 用途 |
| --- | --- |
| `enabled` | 是否允许 Bedrock 表单 |
| `content` | 界面说明文本（回退到菜单根级 `content`） |
| `check-method` | 全局玩家检测方式：`FLOODGATE` 或 `UUID` |
| `not-auto-close` | 在信息表单完成交易后是否重新打开表单 |
| `auto-add-icon.enabled` / `format` | 原版图标自动生成开关与路径格式（支持 `{version}`、`{path}`） |
| `price-extra-line.default` / `only-buy` / `only-sell` | 商品价格补充行文本 |
| `click-event` | 默认商品直接操作 |
| `info.title`、`info.buttons.buy/sell/buy-more/sell-all/back` | 商品信息表单标题与按钮文本 |
| `buy-or-sell.title`、`buy-or-sell.buttons.amount.name/tip` | 数量输入表单文本 |
| `search.input`、`search.buttons.search` | 搜索表单输入框与按钮文本 |
| `favourite-edit.title/content/buttons.*` | 收藏编辑表单文本 |

表单文本中的 `{lang}` 会自动读取当前语言文件；表单内的商品操作由商品实际可用的点击操作决定，图形 `ui.products.actions` 与物品栏批量购买确认标签不会影响原生表单控件。

此外，搜索与收藏菜单拥有专用的表单流程，物品出售与全部出售保留实际物品栏；图形设置 `ui.style: bedrock` 仅是 Ore / ArcMenu 的视觉预设，与 Bedrock 表单无关。

[UI 模式](ui-overview.md) · [批量购买菜单](buy-more-menus.md) · [搜索菜单](search-menus-premium.md) · [收藏菜单](favourite-menus-premium.md)
