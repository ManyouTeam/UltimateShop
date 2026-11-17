# 🔢数字格式

## 内置数字格式

可以使用插件内置格式，并在 `config.yml` 中设置：

```yaml
number-display:
  format:
    enabled: true
    decimal: "#,##0.00##########"
    integer: "#,##0"
  strip-trailing-zeros:
    enabled: true
```

## 使用其他 PlaceholderAPI 扩展

* 将 `number-format.format.enabled` 设为 `false`。
* 下载并安装 **PlaceholderAPI**。
* 执行 `/papi ecloud download Formatter` 安装 **Formatter** 扩展，详见[这里](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders#formatter)。
* 将价格配置的 `placeholder` 改为 `%formatter_number_format_{amount}%`。

例如：

```yaml
  C:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: potato
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 20000000
        placeholder: '&6%formatter_number_format_{amount}% Coins'
        start-apply: 0  
```

<figure><img src="../.gitbook/assets/image (8).png" alt=""><figcaption></figcaption></figure>

* 从 **2.3.2** 起，可通过 `config.yml` 中的以下选项让插件统一替换 `{amount}`，无需逐处手动修改。

```yaml
placeholder:
  auto-settings:
    # If enabled, we will try change {amount} in price placeholder option to the value you set here.
    change-amount-in-all-price-placeholder:
      enabled: true # <--- Change this to true
      replace-value: '%formatter_number_format_{amount}%' # <--- Change to same as here
```
