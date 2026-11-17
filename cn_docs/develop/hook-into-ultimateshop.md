---
hidden: true
---

# 钩入UltimateShop

## 经济插件

添加一个扩展 AbstractEconomyHook 的新类。

```java
public class YourClassName extends AbstractEconomyHook {

    public YourClassName() {
        super("YourPluginName");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        // Your code here.
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        // Your code here.;
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        // Your code here.
    }
}
```

注册到我们的钩子管理器。

```java
HookManager.hookManager.registerNewEconomyHook("YourPluginName", new YourClassName())
```

## 物品插件

与经济插件类似，无需进一步阐述。
