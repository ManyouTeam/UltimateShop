# 💴简易价格

在 `shared_prices/` 中为每个模板创建一个 YAML 文件。文件名是模板 ID。模板可以使用价格项支持的配置，包括 `apply-conditions`、`require-conditions`、`take-actions` 和 `give-actions`。

商品可以在买价或卖价中引用模板，并填写需要覆盖的字段。

```yaml
sell-prices:
  1:
    shared-price: vault
    amount: 25
```

价格项填写的字段会覆盖模板中的对应字段，其余字段继续继承。例如，上面的写法引用内置的 `vault.yml` 模板，并将卖价设为 25。空的 `{}` 会保留模板设置。若要在某个价格项禁用继承的条件或动作，可将 `require-conditions` 或 `give-actions` 设为 `false`。

要让多个商品共用 Vault 回收限额，请参见[示例:回收限额示例](sell-income-limits-example.md)。
