# 🔍自定义物品匹配方式

## 默认物品匹配方式

### 原版物品

默认支持两种出售匹配方式：

* `Bukkit`：玩家物品栏中的物品必须与商品物品完全相同。若玩家修改了物品，就无法再出售。例如附魔或用铁砧改名都会改变物品的 NBT。
* `ItemFormat`：可以设置需要忽略的原版物品数据。若将附魔和名称加入忽略列表，即使玩家新增了附魔或改过名称，仍然可以出售。

配置示例：

```yaml
sell:
  # Support Value: Bukkit or ItemFormat.
  # For each product, you can add match-item section to make custom sell match method, for more info, please view Wiki.
  sell-method: Bukkit
  # Only support ItemFormat sell method.
  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'
      - 'damage'
      - 'enchants'
      - 'tool.damage-per-block'
      - 'nbt.CustomNBTKey'
```

`item-format` 下的选项：

* `require-same-key`：要求商店物品具有玩家物品中的全部数据。例如，商店出售的是未附魔钻石剑，玩家的钻石剑带有锋利附魔。若设为 `true`，玩家无法出售，因为商店物品缺少对应附魔数据。
* `ignore-key`：匹配物品时忽略的 ItemFormat™ 配置键列表。例如，商店钻石剑带有锋利附魔，玩家的剑没有该附魔。若不忽略 `enchants`，就无法出售；加入后会跳过这一附魔差异，继续检查其他配置键。

{% hint style="info" %}
可使用 `/shop generateitemformat` 解析手持物品的 ItemFormat。忽略项也支持更细的路径，例如只忽略锋利附魔而保留其他附魔检查时，填写 `enchants.sharpness`，而不是整个 `enchants`。
{% endhint %}

### 第三方插件物品

[受支持插件](../info/compatibility.md)生成的物品会自动解析物品 ID，并与 ItemFormat 的 `hook-item` 比较。因此，即使外观或其他属性变化，仍可正常出售。

## 为每件商品自定义匹配规则 - 需要 MythicChanger

上述 **ItemFormat** 方式能够处理物品被修改的情况，但灵活性仍有限。此功能允许为每件商品设置自定义出售匹配规则。

在每个**商品或价格**的配置中**添加** `match-item` 配置段。物品满足该规则时，就会被视为可出售。

如果还需要在购买后发放物品，或在插件占位符和商店 GUI 中展示它，**仍须配置 ItemFormat**。

服务器必须安装 **MythicChanger**：

**免费版：** [点击下载](https://www.spigotmc.org/resources/mythicchanger-match-and-modify-all-your-items-without-trouble-1-14-1-21.98523/)

**付费版：** [点击下载](https://www.spigotmc.org/resources/mythicchanger-premium-match-and-modify-all-your-items-without-trouble-1-14-1-21.115913/)

`match-item` 的配置方式见 [MythicChanger Wiki](https://mythicchanger.superiormc.cn/)。注意：某些匹配规则需要的是<mark style="color:red;">**MythicChanger 付费版，而非 UltimateShop 付费版**</mark>！

下面的商品示例同时允许出售绿宝石和钻石，但商店只展示钻石。购买后也只会获得钻石，因为 `match-rule` 不影响购买流程。

```yaml
  A:
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      '1':
        material: diamond
        amount: 1
        match-item: # This is added match-item section.
          material: # This is the match rule key! Read MythicChanger wiki for list of them!
            - diamond
            - emerald
          has-name: false # This is the match rule key! Read MythicChanger wiki for list of them!
      '2':
        # more sub products here?
    buy-prices:
        # You can also use this in buy price!
```
