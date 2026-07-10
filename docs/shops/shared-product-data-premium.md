# 🔗Shared Product Data - Premium

{% hint style="warning" %}
The **shared product data** feature is not currently introduced as a core function of UltimateShop and is still in the early testing phase. It may be officially released in future versions or potentially removed if it contains irreparable critical issues. \
Shared product data is now available at **4.4.2 or later** version.
{% endhint %}

## Overview

`shared-use-times` now does two things:

* it makes multiple products share the same use-times data. Shared data includes:
  * player buy times
  * player sell times
  * player buy cooldown/reset time
  * player sell cooldown/reset time
  * global buy times
  * global sell times
  * persisted storage and cross-server sync data
* it makes shared-group use-times settings load from:

```yaml
plugins/UltimateShop/shared_use_times/<group>.yml
```

If multiple products in the same open GUI use the same shared group, clicking one product will refresh all product buttons in that GUI that use the same shared use-times cache.

## Basic Usage

Set the same `shared-use-times` value on all products that should share one group:

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

Products in the same shared group share the same value to those options too:

**Buy times**

* `buy-times-reset-mode`
* `buy-times-reset-time`
* `buy-times-reset-time-format`
* `buy-times-reset-value`
* `buy-times-max-value`

Legacy aliases still supported:

* `buy-limits-reset-mode`
* `buy-limits-reset-time`

**Sell times**

* `sell-times-reset-mode`
* `sell-times-reset-time`
* `sell-times-reset-time-format`
* `sell-times-reset-value`
* `sell-times-max-value`

You can use same shared group share the same value to those options:

* `buy-limits`
* `sell-limits`

You need put the shared group settings into `shared_use_times/<group>.yml`, and keep only `shared-use-times` on the products.

For an example of shared use times setting config file inside `shared_use_times` folder:

```yaml
buy-limits:
  global: '{sell-times-server}'
buy-times-reset-mode: COOLDOWN_TIMED
buy-times-reset-time: 00:00:00
```
