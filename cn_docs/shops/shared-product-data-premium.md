# 🔗共享商品数据 - 付费版

{% hint style="warning" %}
**共享商品数据**功能目前尚未作为UltimateShop的核心功能引入，仍处于早期测试阶段。它可能会在未来版本中正式发布，或者如果它包含无法修复的关键问题，则可能会被删除。 \
共享商品数据现已在 **4.4.2 或更高版本** 版本中提供。
{% endhint %}

## 概述

`shared-use-times` 现在做了两件事：

* 让多个商品共用同一份 use-times 数据，其中包含：
  * 玩家购买次数
  * 玩家出售次数
  * 玩家购买冷却/重置时间
  * 玩家卖出冷却/重置时间
  * 全服购买次数
  * 全服出售次数
  * 持久化存储与跨服同步数据
* 让该共享组的 use-times 设置改为从下面这个文件读取：

```yaml
plugins/UltimateShop/shared_use_times/<group>.yml
```

如果同一个已打开的界面里有多个商品属于同一共享组，点其中任意一个，该界面中所有共用这份 use-times 缓存的商品按钮都会一起刷新。

## 基本用法

给需要归入同一组的商品填上相同的 `shared-use-times` 值：

```yml
items:
  diamond_pack_a:
    shared-use-times: daily_bundle
    buy-prices: 
    # Buy Price Settings
    sell-prices:
    # Sell Price Settings

  diamond_pack_b:
    shared-use-times: daily_bundle
    buy-prices: 
    # Buy Price Settings
    sell-prices:
    # Sell Price Settings
```

同一共享组内的商品，下面这些选项也共用同一份值：

**购买次数**

* `buy-times-reset-mode`
* `buy-times-reset-time`
* `buy-times-reset-time-format`
* `buy-times-reset-value`
* `buy-times-max-value`

仍支持旧别名：

* `buy-limits-reset-mode`
* `buy-limits-reset-time`

**出售次数**

* `sell-times-reset-mode`
* `sell-times-reset-time`
* `sell-times-reset-time-format`
* `sell-times-reset-value`
* `sell-times-max-value`

下面这两项同样可以在同一共享组内共用：

* `buy-limits`
* `sell-limits`

共享组的设置要写进 `shared_use_times/<group>.yml`，商品配置里只留 `shared-use-times` 即可。

`shared_use_times` 文件夹内配置文件的示例：

```yaml
buy-limits:
  global: '{sell-times-server}'
buy-times-reset-mode: COOLDOWN_TIMED
buy-times-reset-time: 00:00:00
```
