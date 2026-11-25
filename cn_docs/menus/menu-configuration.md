# ⚙️菜单配置与继承

UltimateShop 的菜单配置分为三层：`config.yml` 负责全局默认行为，`ui/` 存放可复用的图形样式，`menus/` 定义各类具体界面；商店文件则负责指定菜单模板，并可在自身配置中按需覆盖模板选项。

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

菜单 ID 和商店 ID 均为不含 `.yml` 的文件名，即使在目录下再建子文件夹也不会改变它们的 ID；而**图形样式 ID** 会包含相对子目录路径，例如 `ui/custom/dark.yml` 对应的样式 ID 为 `custom/dark`。

## 全局设置与菜单设置

在 `config.yml` 中配置全局默认开关与样式：

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

在具体的菜单文件中，对应配置直接写在根级（无需外层的 `menu:`）：

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

在正常选择呈现方式时，**全局开关和菜单自身开关必须同时允许**，该呈现方式才会生效。默认情况下，菜单的 `dialog.enabled` 和 `bedrock.enabled` 为 `true`，`arcmenu.enabled` 为 `false`。若启用了全局 `menu.force-ui`，则会忽略这些开关限制（但平台与菜单类型本身的限制依然有效），详见 [UI 模式](ui-overview.md)。

原生对话框与表单的标签及选项优先读取菜单内的 `dialog.*` / `bedrock.*`，未设置时再回退读取全局 `menu.dialog.*` / `menu.bedrock.*`（部分对接与资源包生成选项仅支持全局配置）。界面说明文本优先读取当前呈现方式对应的 `dialog.content`、`bedrock.content` 或 `arcmenu.content`，均未设置时回退到菜单根级的 `content`。

## 商店模板与覆盖

商店可以在 `settings` 中引用 `menus/shop_menu_templates/` 下的模板（例如 `farming.yml`），并通过 `menu-settings` 局部覆盖模板设置：

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

`settings.menu-settings` 写在**商店文件**中，它会先合并到所选模板上，然后再应用图形默认值：

| 覆盖值类型 | 合并结果 |
| --- | --- |
| `title`、`enabled` 等标量 | 直接替换模板中的原值 |
| 嵌套配置段（Map） | 与模板内容递归合并 |
| `layout`、Lore 等列表（List） | 整体替换模板中的整个列表 |
| 未填写的配置键 | 保留模板原有设置 |

例如，仅覆盖 `dialog.layout` 不会影响模板中的 `dialog.columns`；但如果要替换物品栏 `layout`，则必须写出完整的全部行。如果某个商店没有指定 `settings.menu`，也可以直接在 `settings.menu-settings` 中定义完整界面。

## 共享图形样式

`ui/classic.yml` 和 `ui/bedrock.yml` 是内置的默认图形样式（插件只会在文件缺失时生成，不会覆盖已有修改）。你可以将预设复制为 `ui/my-style.yml` 自行定制。

样式文件中**直接**编写 UI 字段，无需外层 `ui:`：

```yaml
theme:
  accent: '#6CC349'
products:
  card:
    icon-size: 40
```

如果样式文件只写了部分字段，引用 `ui.style: my-style` 时，未填写的字段会自动沿用全局图形默认值；若希望拥有一套完全独立的布局，建议先完整复制内置预设再修改。

在 `config.yml` 中设置全局样式，或在单个菜单中指定不同样式：

```yaml
# config.yml
menu:
  ui:
    style: classic
    theme:
      accent: '#65E8EF'
```

```yaml
# 单个菜单文件
ui:
  style: bedrock
  theme:
    accent: '#6CC349'
```

正常模式下的图形继承顺序如下：

1. 加载全局指定的样式文件，并用 `config.yml` 中显式配置的 `menu.ui` 覆盖默认值。
2. 如果当前菜单指定了不同的 `ui.style`，则将该样式文件覆盖到全局默认值之上。
3. 用上述结果补齐菜单缺失的 `ui` 字段；菜单自身（含商店 `menu-settings`）显式填写的字段始终具有最高优先级。

{% hint style="info" %}
图形样式仅服务于 Ore 与 ArcMenu 的布局和外观。`ui.style: bedrock` 只是切换到类基岩风格预设，并不会把 Java 端界面变成 Bedrock 表单。
{% endhint %}

## 强制呈现方式与样式

在 `config.yml` 中配置 `menu.force-ui`：

```yaml
menu:
  force-ui:
    enabled: true
    type: arcmenu
    style: bedrock
```

* `type` 会全局强制所有支持的菜单使用指定的呈现方式。
* 若 `style` 不为空，还会用该强制样式解析出的默认值覆盖菜单对应的图形字段（强制样式中未定义的本地字段仍会保留）。
* 如果只希望强制呈现方式、保留各菜单自己的样式选择，将 `style` 留空（`''`）即可；`enabled: false` 时强制设置不生效。详见 [UI 模式](ui-overview.md#force-one-presentation)。

## 商品与按钮设置位置速查

| 用途 | 配置位置 |
| --- | --- |
| 普通按钮物品与动作 | 菜单 `buttons.<id>.display-item` / `actions` |
| 商品物品、价格与交易规则 | 商店 `items.<id>` |
| 各呈现方式的可见性 | 按钮或商品根级 `menu-visibility.<presentation>` |
| 图形商品位置 | 菜单 `ui.products.placement`（可被商品 `ui.placement` 覆盖） |
| 普通图形按钮位置 | 按钮 `ui.placement` |
| 图形商品价格与操作 | 商品 `ui.price` / `ui.actions` → 所属商店菜单 `ui.products.*` → 全局 `menu.ui.products.*` |
| 图形卡片样式 | 菜单 `ui.products.card` |
| Bedrock 表单图标与补充行 | 当前生效的 `display-item.bedrock.*` |
| Bedrock 表单商品直接操作 | 商品 `bedrock.click-event` |

在搜索菜单或收藏菜单中展示的商品，仍保留来源商店的交易规则与设置；解析图形操作与价格时，菜单级默认值取自该商品所属的商店菜单。

## 各菜单类型的支持范围

不同菜单类型对图形样式和 UI 模式的支持程度不同：

* **通用菜单与商店菜单**：完整支持 Ore 与 ArcMenu 的默认样式和卡片配置。
* **搜索菜单与收藏菜单**：支持 ArcMenu 样式，但**不支持 Ore**（也不会继承 Ore 模板与卡片描述默认值）。
* **物品出售菜单与批量购买菜单**：不继承 `config.yml` 中的图形 `menu.ui` 设置。批量购买菜单仅通过自身的 `dialog.enabled` 决定是否弹出原生数字输入框，表单/对话框中的提示文字则来自来源商店。
* **全部出售菜单**：是一个独立的全局箱子界面，只读取 `config.yml` 中的 `menu.sell-all`。

修改菜单、样式或选择器后，执行 `/shop reload` 即可生效；如果修改了 Ore 布局或纹理，还需重新生成并分发[资源包](ore-menus-premium.md#generate-the-resource-pack)。

[布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
