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

## 用不同界面呈现同一商店 - 付费版

- 物品栏使用根级 `size` 和 `layout`。
- 表单与原生对话框将选中的条目转换为列表或动作控件。
- Ore 与 ArcMenu 使用所选样式的 `ui.layout`、`ui.products.placement`，以及各按钮的 `ui.placement`。

图形商店中，非动态商品收集可以纳入未出现在根布局中的商品，并使用默认商品位置。动态布局只保留已选商品，除非其他商品显式设置了图形位置。导航控件始终需要自身位置配置。通过 `menu-visibility` 和显示条件控制条目是否符合显示要求。

[UI 模式](ui-overview.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md) · [批量购买](buy-more-menus.md)
