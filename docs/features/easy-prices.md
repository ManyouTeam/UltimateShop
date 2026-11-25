# 💴Easy Prices

Create one YAML file per template in `shared_prices/`. Use the filename without its extension as the template ID. Templates accept price entry fields, including `apply-conditions`, `require-conditions`, `take-actions`, and `give-actions`.

Reference a template from a buy or sell price entry.

```yaml
sell-prices:
  1:
    shared-price: vault
    amount: 25
```

A price entry overrides the fields it supplies and inherits the rest. The example above uses the built-in `vault.yml` template and sets the payout to 25. An empty `{}` keeps the template settings. Set an inherited section such as `require-conditions` or `give-actions` to `false` to disable it for that entry.

For a template that shares Vault payout limits across products, see [Example: Sell Income Limits](sell-income-limits-example.md).
