# 💹EconomyFormat™

EconomyFormat 是本插件提供的一种配置格式，用来对接服务器的经济系统。如果配置中的某个选项需要或支持 **EconomyFormat**，我们会在文档中标注出来。&#x20;

{% hint style="info" %}
请记住：**UltimateShop** 只是一个商店插件，本身不提供经济系统。如果你需要服务器使用某种经济，请自行选择合适的 economy 插件。**Vault** 并不是经济插件，它只是许多经济插件的依赖前置。<mark style="color:red;">装好 Vault 之后，还必须再安装一个支持 Vault 的经济插件</mark>。
{% endhint %}

## 挂钩经济插件

挂钩经济插件的写法如下：

* economy-plugin：该价格要挂钩到哪个经济插件。目前 **UltimateShop** 支持 `Vault, GamePoints, PlayerPoints, CoinsEngine, UltraEconomy, EcoBits, PEconomy, RedisEconomy, RoyaleEconomy, VotingPlugin`。**必填。**
* economy-type：若所用经济插件支持多货币，在这里填货币名称。**多货币插件必填，其余情况可省略。**
* amount：价格数值。**必填。**
* max-amount：价格上限，动态价格场景常用。**可选。**
* min-amount：价格下限，动态价格场景常用。**可选。**

**示例：**

```yaml
  1:
    economy-plugin: Vault
    # If you set Economy plugin to CoinsEngine, then:
    # economy-plugin: CoinsEngine
    # economy-type: Coin
    # Yeah, you need add economy-type option here because its a multi-currency plugin.
    # For this, players will always cost 5$.
    amount: 5
    placeholder: '$5'
```

## 原版经济

使用原版经济（经验）的写法如下。

* economy-type：可取 `exp`（经验点数）或 `levels`（经验等级）。**必填。**
* amount：价格数值。**必填。**
* max-amount：价格上限，动态价格场景常用。**可选。**
* min-amount：价格下限，动态价格场景常用。**可选。**

**示例：**

```yaml
  1:
    economy-type: levels
    amount: 5
    placeholder: '5 levels'
```
