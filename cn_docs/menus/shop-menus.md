# 🛍️商店菜单

商店文件保存商品、价格与交易规则，菜单模板保存布局、导航按钮和呈现设置。多个商店可以共用一个模板。

## 创建商店与模板

将模板保存为 `menus/shop_menu_templates/farming.yml`：

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

将商店保存为 `shops/farming.yml`：

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

安装 Vault 和兼容的经济插件后，执行 `/shop reload`，再执行 `/shop menu farming`。正常选择物品栏时，`A` 显示商店商品 `items.A`，`X` 显示模板的关闭按钮。全局 `menu.force-ui` 可以覆盖菜单自身的呈现开关。

`settings.menu: farming` 选择的是模板 ID；`/shop menu farming` 打开的是**商店** ID，两者可以不同。商店模板由商店使用，不能作为独立通用菜单打开。

## 为单个商店覆盖模板

在商店文件中填写：

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

覆盖内容会递归合并到模板，`layout` 等列表则整体替换。随后，全局图形默认值补齐缺失的 `ui` 字段。若 `settings.menu-settings` 已提供完整界面定义，也可以不使用模板。详见[配置与继承](menu-configuration.md)。

## 添加商品与导航

在商店 `items` 下添加商品，再在模板物品栏布局中引用商品 ID。普通导航按钮可以放在模板 `buttons` 或商店 `buttons` 中，不同来源应避免 ID 重复。

商品点击使用 `menu.click-event` 和商品自身覆盖设置，普通按钮使用动作列表。商品定价与奖励遵循[商品](../shops/products.md)规则，图形布局不会改变交易规则。

## 用同一个布局摆放不同商店的商品

在商店菜单模板里写 `shop-items`，指定哪些布局字符用来摆放商品。例如，下面四个 `P` 槽会从左到右放入商店 `items` 下的前四件商品。商品少于四件时，剩余的 `P` 槽留空。

```yaml
size: 9
shop-items: ['P']
layout:
  - '0PPPP000X'
```

如果要给不同顺序的商品指定固定位置，可以写 `shop-items: ['A', 'B', 'C']`。布局里的 `A` 对应第一件商品，`B` 对应第二件，`C` 对应第三件。商品顺序以商店文件中 `items` 的书写顺序为准。

用普通按钮或商品 ID 填其他槽位即可，例如这里的 `X` 可以是关闭按钮。商品的显示条件和可见性仍然生效。单个商店可以用 `settings.menu-settings.shop-items` 覆盖模板。游戏内编辑器里，这个设置叫 **商品槽位标记**。

## 给物品栏商店加翻页按钮

用一个 `shop-items` 标记重复摆放商品。上一页和下一页的按钮和 `amount-items` 一样，用布局字符作键，例如 `previous-page-item.l` 和 `next-page-item.r`。每页能放几件商品，就数 `layout` 里有几个单独的 `P` 槽。

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

按钮在首尾页默认隐藏。配置了 `hidden-item` 就显示替代物品，否则槽位留空。把对应隐藏选项设为 `false` 可以保留箭头，点到边界时页码保持原样。翻页只替换商品槽里的内容，普通按钮留在原位。Editor 里可以直接编辑这两个按钮。

## 用不同界面呈现同一商店 - 付费版

- 物品栏使用根级 `size` 和 `layout`。
- 表单与原生对话框将选中的条目转换为列表或动作控件。
- Ore 与 ArcMenu 使用所选样式的 `ui.layout`、`ui.products.placement`，以及各按钮的 `ui.placement`。

图形商店中，非动态商品收集可以纳入未出现在根布局中的商品，并使用默认商品位置。动态布局只保留已选商品，除非其他商品显式设置了图形位置。导航控件始终需要自身位置配置。通过 `menu-visibility` 和显示条件控制条目是否符合显示要求。

[UI 模式](ui-overview.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md) · [批量购买](buy-more-menus.md)
