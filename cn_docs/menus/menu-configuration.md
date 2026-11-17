# ⚙️菜单配置与继承

`config.yml` 配置共享行为，`ui/` 存放可复用图形样式，`menus/` 定义具体界面。商店文件选择菜单模板，并可针对自身覆盖模板设置。

## 文件位置

```text
plugins/UltimateShop/
├── config.yml
├── ui/
│   ├── classic.yml
│   └── bedrock.yml
├── menus/
│   ├── common_menus/
│   ├── shop_menu_templates/
│   ├── buy_more_menus/
│   ├── search_menus/
│   ├── favourite_menus/
│   └── item_sell_menus/
└── shops/
```

菜单和商店 ID 是不含 `.yml` 的文件名，自定义子目录不会改变这些 ID。样式 ID 则包含相对子目录，例如 `ui/custom/dark.yml` 的 ID 为 `custom/dark`。

## 全局设置与菜单设置

以下示例写在 `config.yml` 中：

```yaml
menu:
  force-ui:
    enabled: false
    type: inventory
    style: ''
  dialog:
    enabled: false
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
  ui:
    style: classic
```

菜单文件中的对应配置不需要外层 `menu`：

```yaml
title: '&8Categories'
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
ui:
  style: classic
```

正常选择界面时，全局和菜单自身的开关都必须允许该呈现方式。菜单 `dialog.enabled` 和 `bedrock.enabled` 默认是 `true`，`arcmenu.enabled` 默认是 `false`。启用 `menu.force-ui` 会覆盖这些开关，但平台与菜单类型限制仍然有效。详见 [UI 模式](ui-overview.md)。

受支持的原生标签和选项优先读取菜单 `dialog.*` / `bedrock.*`，其次读取全局 `menu.dialog.*` / `menu.bedrock.*`。部分对接及资源生成设置只支持全局配置，具体见各呈现方式的页面。界面说明使用菜单的 `dialog.content`、`bedrock.content` 或 `arcmenu.content`，未设置时回退到根级 `content`。

## 商店模板与覆盖

商店通过以下配置选择 `menus/shop_menu_templates/farming.yml`：

```yaml
settings:
  menu: farming
  menu-settings:
    title: '&aFarming Specials'
    dialog:
      enabled: true
      layout: multi-action
    ui:
      products:
        card:
          icon-size: 40
```

`settings.menu-settings` 应写在**商店文件**中。它会先合并到所选模板，然后再应用图形默认值。

| 覆盖值类型 | 结果 |
| --- | --- |
| `title`、`enabled` 等标量 | 替换模板中的值 |
| 嵌套配置段 | 与模板递归合并 |
| `layout` 或物品描述等列表 | 替换模板中的整个列表 |
| 未填写的配置键 | 保留模板值 |

例如，覆盖 `dialog.layout` 不会清除模板的 `dialog.columns`。若要替换物品栏布局，需要提供完整的全部行。未设置 `settings.menu` 的商店，可以直接在 `settings.menu-settings` 中定义界面。

## 共享图形样式

`ui/classic.yml` 和 `ui/bedrock.yml` 是内置图形默认样式。插件只会生成缺失的内置样式文件，不会覆盖已有文件。可以将预设复制为 `ui/my-style.yml`，自行维护。

样式文件**直接**包含 UI 字段，不需要外层 `ui`：

```yaml
theme:
  accent: '#6CC349'
products:
  card:
    icon-size: 40
```

通过 `ui.style: my-style` 选择此局部样式，其他字段使用全局图形默认值。若需要完整独立布局，请先复制整个内置预设再修改。

在 `config.yml` 中选择全局样式：

```yaml
menu:
  ui:
    style: classic
    theme:
      accent: '#65E8EF'
```

为单个菜单选择不同样式：

```yaml
ui:
  style: bedrock
  theme:
    accent: '#6CC349'
```

正常图形继承顺序如下：

1. 加载全局样式，再用显式配置的 `menu.ui` 值覆盖默认值。
2. 如果菜单选择了其他样式，将该样式覆盖到已解析的全局默认值上。
3. 使用这些默认值补齐菜单缺失的 `ui` 字段。菜单明确填写的字段，包括商店覆盖，具有优先权。

因此，不同的菜单样式可以替换全局强调色，而菜单自身的 `ui.theme.accent` 在正常选择时始终优先。嵌套配置段递归合并，列表整体替换。

样式为 Ore/ArcMenu 提供图形布局与外观。`ui.style: bedrock` 只改变图形样式，不会将 Java 界面转换为 Bedrock 表单。

## 强制呈现方式与样式

在 `config.yml` 中设置：

```yaml
menu:
  force-ui:
    enabled: true
    type: arcmenu
    style: bedrock
```

`type` 全局选择呈现方式。非空的 `style` 还会使用解析后的强制默认值，覆盖菜单自身相应图形字段；强制默认值未包含的本地字段仍会保留。条目位置和商品级覆盖仍需适配所选布局。

将 `style` 留为 `''`，即可只强制呈现方式，保留正常样式选择。`enabled: false` 时，强制呈现方式和强制样式均不生效。支持的类型与客户端行为见 [UI 模式](ui-overview.md#force-one-presentation)。

## 商品与按钮设置

| 用途 | 位置 |
| --- | --- |
| 普通按钮物品与动作 | 菜单 `buttons.<id>.display-item/actions` |
| 商品物品、价格与交易规则 | 商店 `items.<id>` |
| 各呈现方式的可见性 | 按钮/商品 `menu-visibility.<presentation>` |
| 图形商品位置 | 菜单 `ui.products.placement`，由商品 `ui.placement` 覆盖 |
| 普通图形按钮位置 | 按钮 `ui.placement` |
| 图形商品价格与操作 | 商品 `ui.price` / `ui.actions` → 所属商店菜单 `ui.products.*` → 全局 `menu.ui.products.*` |
| 图形卡片样式 | 菜单 `ui.products.card` |
| 表单图标与补充信息行 | 当前生效的 `display-item.bedrock.*` |
| 表单商品直接操作 | 商品 `bedrock.click-event` |

搜索/收藏中展示的商品仍保留来源商品的设置与交易规则。图形操作和价格解析时，菜单级默认值来自商品所属的商店菜单。

## 各菜单类型的适用范围

通用与商店菜单支持 Ore 和 ArcMenu 默认值。搜索与收藏菜单继承 ArcMenu 默认值，但不继承 Ore 模板和卡片描述默认值。物品出售与批量购买菜单不继承图形 `menu.ui` 设置。批量购买菜单读取自身 `dialog.enabled` 决定是否使用原生数量输入；表单/对话框的数量标签由来源商店提供。全部出售菜单只使用专用的 `menu.sell-all` 配置。

修改菜单、样式或选择器后，执行 `/shop reload`。修改 Ore 布局与纹理后，还需要重新生成并分发[资源包](ore-menus-premium.md#generate-the-resource-pack)。

[布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
