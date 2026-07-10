---
hidden: true
---

# Hook into UltimateShop

## Economy Plugins

Add a new class that extends AbstractEconomyHook.

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

Register into our hook manager.

```java
HookManager.hookManager.registerNewEconomyHook("YourPluginName", new YourClassName())
```

## Item Plugins

Similar to economy plugins, no further elaboration is needed.
