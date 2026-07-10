# 🗯️Custom Placeholder - Premium



{% hint style="info" %}
This feature was added in version **4.5.26**, and still in **BETA** test.
{% endhint %}

The main purpose of a custom placeholder is to record desired information, such as the quantity of same of material sold, or the earning. Simply modify the placeholder value using commands in the appropriate places to achieve the desired effect.

## Config

All custom placeholder configs are stored in `custom_placeholders` folder. The file name is it's ID, for example: `example.yml` means it's ID is `example`. An example of it's config is like below:

```yaml
type: number
default-value: "1"
per-player-value: true
```

* type: Support value: `default` or `number`. If you only want to record numeric data, then set this to `number`; otherwise, set it to `default`.
* default-value: If no value is set for this placeholder, the default value set here will be used.
* per-player-value: If set to `true`, each player has an independent placeholder value; otherwise, all players share the same placeholder value.

## Use Placeholder

Use `{custom_<ID>}` placeholder to display it's value. For more info, please view [Placeholders](built-in-placeholders.md) page. For example: `{custom_example}`

## Set Placeholder Value

You need to set the value of the custom placeholder by using commands.

* /shop setcustomplaceholder \<ID> \<value> \[playerName] \
  If `per-player-result` is set to `true`, then `playerName` will be a required parameter; otherwise, the playerName parameter should not be entered.
* /shop addcustomplaceholder \<ID> \<value> \[playerName]\
  Only work number type of custom placeholder. It will be added to the current value of the placeholder. For example, if the current value is 1 and the value parameter entered here is 5, then the final placeholder value will be replaced with 6.
