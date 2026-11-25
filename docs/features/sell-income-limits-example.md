# 🧪Example: Sell Income Limits

This example uses `vault-sell.yml` from [Easy Prices](easy-prices.md) so all products referencing it share the same per-player Vault income caps (default: 2,000 per 5 hours, 10,000 daily, 50,000 weekly).

{% hint style="info" %}
Custom placeholders are available in the free version (up to 5), so this template works in both the free and premium versions.
{% endhint %}

## Usage

Reference `vault-sell` in `sell-prices` on every product that should share the same Vault sell limits:

```yaml
sell-prices:
  1:
    shared-price: vault-sell
    amount: 25
```

## Default Caps and Reset Windows

| Period | Default cap | Counter | Reset |
| --- | --- | --- | --- |
| Every 5 hours | 2,000 | `{custom_sell_5h}` | 5 hours after the first recorded payout in the window |
| Daily | 10,000 | `{custom_sell_daily}` | Midnight (`00:00`) every day |
| Weekly | 50,000 | `{custom_sell_weekly}` | Midnight (`00:00`) every Monday |

## How It Works and Lore Display

* **Condition timing**: `type: placeholder` in `require-conditions` evaluates expressions after sell multipliers and rounding. When multiple price entries in the same sale reference the template, they are checked against their combined payout.
* **Income tracking**: `give-actions` uses `type: add_custom_placeholder` to add `{amount}` to each numeric custom placeholder, with `multi-once: true` so the full reward is recorded once per price entry.
* **Resets & countdowns**: Numeric custom placeholders support `TIMER`, `TIMED`, and `CUSTOM` reset modes. Use `{custom-times_sell_daily}` to show the next reset time.
* **Lore display**: The default language file uses `@r[vault-sell]` lines in `display-item.add-lore` to show used quota for all three periods below the sell price. See [Display Item Add Lore](../menus/display-item-add-lore.md).

## How Sell All works out how many times to sell

The default `vault-sell.yml` includes a `max-sell-amount` formula that checks the remaining room under all three caps and divides the tightest remaining allowance by the actual payout per trade. For example, if the tightest cap has 80 coins left and each trade pays 5 coins, Sell All allows at most 16 trades (or 10 if the player only has stock for 10).

* To change a cap, just edit that condition's `value`—the formula reads it automatically. Only update the formula if you change which counter or comparison rule is used.
* The default formula uses `min()`, which requires `math.enable-function` in `config.yml`. To write your own formula, see [`max-sell-amount` in the Single Entry reference](../shops/products-config-single-thing/README.md#max-sell-amount).

## Adjust or remove the limits

All three caps are enforced by `require-conditions` in `vault-sell.yml`. There is no separate toggle; edit that section directly:

```yaml
require-conditions:
  1:
    type: placeholder
    placeholder: '{custom_sell_5h} + {amount}'
    rule: '<='
    value: '2000'
```

Common adjustments:

* **Remove all limits**: delete the entire `require-conditions` section. The template keeps counting and displaying used quota without blocking sales.
* **Keep only some periods**: delete the entries you do not want (for example, keep only the 5-hour cap).
* **Raise the caps**: increase `value` (for example, `'999999999'`).
* **Bypass for one product**: add `require-conditions: false` on that product's price entry to clear the inherited conditions.

{% hint style="info" %}
Even after removing `require-conditions`, `give-actions` still increments `sell_5h`, `sell_daily`, and `sell_weekly`, so the displayed quota continues to grow without blocking sales. To hide the display as well, remove the `@r[vault-sell]` lines from `display-item.add-lore` in your language file.
{% endhint %}
