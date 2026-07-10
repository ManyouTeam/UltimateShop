# Develop Guide

{% hint style="info" %}
Please note that UltimateShop is not a traditional shop plugin. It can dynamically display products and prices (and even the each single price amount), unlike other shop plugins where one ItemStack corresponds to one price.
{% endhint %}

## Add as dependency <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

{% hint style="info" %}
As of March 20, 2026, the latest plugin version number is **4.3.4**. If this date is too far away, then you should check the latest plugin version number yourself, as the provided plugin version may be outdated or unavailable.
{% endhint %}

```xml
<repositories>
    <repository>
        <id>repo-lanink-cn</id>
        <url>https://repo.lanink.cn/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cn.superiormc.ultimateshop</groupId>
        <artifactId>plugin</artifactId>
        <version>[PLUGIN VERSION]</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

```graphql
repositories {
    maven {
        url "https://repo.lanink.cn/repository/maven-public/"
    }
}

dependencies {
    compileOnly group: 'cn.superiormc.ultimateshop', name: 'plugin', version: '[PLUGIN VERSION]'
}

```

```kts
repositories {
    maven("https://repo.lanink.cn/repository/maven-public/")
}

dependencies {
    compileOnly("cn.superiormc.ultimateshop:plugin:[PLUGIN VERSION]")
}
```

## UltimateShop Developer Integration Guide

This document is for developers who want to integrate with UltimateShop, not for regular server owners configuring shops.

The content here is based on the current `4.3.4` source code and focuses on the most common integration scenarios:

* Depending on UltimateShop from your own plugin
* Fetching shop items by shop ID / product ID
* Querying player buy/sell usage counts and previewing prices
* Listening to transaction events
* Triggering bulk sell
* Opening a shop or triggering quick buy/sell from your plugin

The code snippets below intentionally omit unrelated listener registration, class wrappers, and some basic imports, and only keep the parts directly related to UltimateShop integration.

### 1. Understand What UltimateShop Exposes

From the source code, the best external entry points are mainly these:

* `cn.superiormc.ultimateshop.api.ShopHelper`
* `cn.superiormc.ultimateshop.api.ItemPreTransactionEvent`
* `cn.superiormc.ultimateshop.api.ItemFinishTransactionEvent`

Besides that, there are also some `public` classes that are usable but are more internal implementation details:

* `cn.superiormc.ultimateshop.objects.ObjectShop`
* `cn.superiormc.ultimateshop.objects.buttons.ObjectItem`
* `cn.superiormc.ultimateshop.gui.inv.ShopGUI`
* `cn.superiormc.ultimateshop.methods.Product.BuyProductMethod`
* `cn.superiormc.ultimateshop.methods.Product.SellProductMethod`

The practical way to think about them is:

* Prefer the `api` package first
* Use the `objects` package for read-only item/shop queries
* Use `gui` / `methods` only when you need more direct control, knowing they are more likely to change across upgrades

### 2. Declare the Dependency in Your Plugin

#### `plugin.yml`

If your plugin integrates with UltimateShop only when it is present, add at least a `softdepend`:

```yaml
softdepend:
  - UltimateShop
```

If your plugin cannot work without UltimateShop at all, use `depend` instead.

### 3. Check at Runtime That UltimateShop Is Present

```java
Plugin plugin = Bukkit.getPluginManager().getPlugin("UltimateShop");
if (plugin == null || !plugin.isEnabled()) {
    return;
}
```

If your integration logic should only be enabled when UltimateShop exists, this check is best placed in `onEnable()`.

### 4. Most Common Entry Point: Fetch an Item by ID

The safest way is to fetch the target item directly by shop ID and product ID:

```java
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;

ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

String shopId = item.getShop();
String productId = item.getProduct();
String displayName = item.getDisplayName(player);
```

This is the recommended approach because it is deterministic.

Common read-only methods on `ObjectItem` include:

* `getShop()`: owning shop ID
* `getProduct()`: product ID
* `getDisplayName(Player)`: display name
* `getBuyPrice()` / `getSellPrice()`: price definitions
* `getReward()`: product reward definition
* `getPlayerBuyLimit(Player)` / `getPlayerSellLimit(Player)`: per-player limits
* `getServerBuyLimit(Player)` / `getServerSellLimit(Player)`: global limits

### 5. Query Current Player Usage Counts

If you want to show something like "how many more times this player can still buy this item" inside your own plugin, use `ShopHelper` directly:

```java
int buyTimes = ShopHelper.getBuyUseTimes(item, player);
int sellTimes = ShopHelper.getSellUseTimes(item, player);

int playerBuyLimit = item.getPlayerBuyLimit(player);
int playerSellLimit = item.getPlayerSellLimit(player);
```

A typical usage looks like this:

```java
int used = ShopHelper.getBuyUseTimes(item, player);
int limit = item.getPlayerBuyLimit(player);

if (limit != -1) {
    int remain = Math.max(0, limit - used);
    player.sendMessage("You can still buy " + remain + " more times");
}
```

Notes:

* `-1` means unlimited
* `ShopHelper` will create the use-times cache automatically if it does not exist yet

### 6. Preview Buy Cost

If you only want to display something like "how much this batch of items is worth" in your own GUI or message flow, use the preview methods in `ShopHelper`.

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
String buyPriceDisplay = ShopHelper.getBuyPricesDisplay(items, player, 3);
if (buyPriceDisplay != null) {
    player.sendMessage("Buying 3 times costs: " + buyPriceDisplay);
}
```

### 7. Preview Sell Reward

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
String sellPriceDisplay = ShopHelper.getSellPricesDisplay(items, player, 1);
if (sellPriceDisplay != null) {
    player.sendMessage("Selling gives: " + sellPriceDisplay);
}
```

If you need the raw result objects instead of formatted strings:

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
TakeResult buyCost = ShopHelper.getBuyPrices(items, player, 3);
GiveResult sellReward = ShopHelper.getSellPrices(items, player, 1);
```

These result objects are useful for display, logging, or secondary checks.

### 8. Listen to Transaction Events

UltimateShop provides two transaction events:

* `ItemPreTransactionEvent`
* `ItemFinishTransactionEvent`

#### 8.1 `ItemPreTransactionEvent`

This event fires before the actual transaction is finalized. It is suitable for:

* statistics
* external logging
* webhook pushes
* side-channel syncing

Example:

```java
import cn.superiormc.ultimateshop.api.ItemPreTransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ShopListener implements Listener {

    @EventHandler
    public void onPreTransaction(ItemPreTransactionEvent event) {
        String action = event.isBuyOrSell() ? "BUY" : "SELL";
        String shopId = event.getShop().getShopName();
        String productId = event.getItem().getProduct();
        int amount = event.getAmount();

        event.getPlayer().sendMessage(
                "About to execute " + action + " -> " + shopId + ":" + productId + " x" + amount
        );
    }
}
```

This event also gives you two important result objects:

* `getTakeResult()`: what will be taken in this transaction
* `getGiveResult()`: what will be given in this transaction

The common interpretation is:

* On buy: `takeResult` is usually the price the player pays, and `giveResult` is usually the product reward
* On sell: `takeResult` is usually the item(s) the player turns in, and `giveResult` is usually the sell reward

#### 8.2 `ItemFinishTransactionEvent`

This event fires after a successful transaction completes. It is suitable for:

* success statistics
* achievement unlocks
* quest/task progress integration
* post-success notifications

```java
@EventHandler
public void onFinish(ItemFinishTransactionEvent event) {
    Player player = event.getPlayer();
    player.sendMessage("Transaction completed: " + event.getItem().getProduct());
}
```

### 9. Bulk Sell: `ShopHelper.sellAll`

If you want your plugin to trigger UltimateShop's built-in bulk selling logic, use:

```java
Map<AbstractSingleThing, BigDecimal> result =
        ShopHelper.sellAll(player, player.getInventory(), 1.0D);
```

The third parameter, `multiplier`, is the sell reward multiplier.

For example:

* `1.0D`: normal value
* `2.0D`: double sell reward
* `0.5D`: half sell reward

#### This method already performs the real transaction

This is the most common misuse point.

Internally, `sellAll(...)` directly runs the sell flow. That means it will:

* check matching products
* actually remove sold items
* actually give sell rewards
* execute sell actions

The returned `Map<AbstractSingleThing, BigDecimal>` is better understood as a summary of the sell result, not something that still needs another manual `giveThing(...)` call.

Wrong usage:

```java
Map<AbstractSingleThing, BigDecimal> result = ShopHelper.sellAll(player, inv, 1.0D);
ShopHelper.giveThing(0, 0, player, 1.0D, result); // duplicates rewards
```

Correct understanding:

* `sellAll` already completes the transaction
* the return value is only for display, logging, or statistics

### 10. Manually Execute Give / Take

If you already have a `TakeResult` or `GiveResult` and want to execute it yourself, you can use either the result object methods or the helper methods on `ShopHelper`.

#### Execute a Take

```java
TakeResult takeResult = ShopHelper.getBuyPrices(player.getInventory(), player, 1);
if (takeResult != null && takeResult.getResultBoolean()) {
    takeResult.take(0, 1, player.getInventory(), player);
}
```

#### Execute a Give from a Result Map

```java
GiveResult giveResult = ShopHelper.getSellPrices(player.getInventory(), player, 1);
if (giveResult != null) {
    boolean success = ShopHelper.giveThing(0, 1, player, 1.0D, giveResult.getResultMap());
}
```

#### Execute a Take from a Result Map

```java
ShopHelper.takeThing(0, 1, player.getInventory(), player, takeResult.getResultMap());
```

This style is more appropriate when you are building your own custom business flow, not when you are repeating a transaction UltimateShop has already executed.

### 11. Open a Shop from Your Plugin

#### Option A: Use the Command, Best Compatibility

If you do not want to depend on internal GUI classes, the safest approach is to dispatch the built-in command:

```java
Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ultimateshop menu blocks Steve");
```

Based on the current command implementation, the common forms are:

* Player opens their own shop: `/shop menu <shop>`
* Console opens a shop for a player: `/shop menu <shop> <player>`

The advantages of this approach:

* no direct dependency on internal GUI classes
* better compatibility if the internal implementation changes later

#### Option B: Call `ShopGUI` Directly

If you explicitly want the direct internal path, you can do this:

```java
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;

ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

ObjectShop shop = item.getShopObject();
ShopGUI.openGUI(player, shop, false, false);
```

Parameter meaning:

* first `false`: whether to bypass menu conditions
* second `false`: whether this should be treated as a reopen

Note: `ShopGUI` is not in the `api` package. This is an internal-class integration and is more likely to be affected by version changes than the command approach.

### 12. Trigger Quick Buy / Sell Directly

If you already know the exact shop and product, you can also call the internal trade methods directly.

#### Direct Buy

```java
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;

ProductTradeStatus status = BuyProductMethod.startBuy(item, player, true, false, 3);
```

These parameters mean:

* `item`: target product
* `player`: player
* `true`: whether to force failure messages to display
* `false`: whether to skip the actual cost
* `3`: transaction amount

#### Direct Sell

```java
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;

ProductTradeStatus status = SellProductMethod.startSell(item, player, true, false, false, 3);
```

These boolean parameters control:

* whether to force failure messages
* whether to skip actual cost / settlement
* whether to enable max-sell logic

If you simply want your external plugin to perform one normal buy or sell, this is the most direct code path.

Still, this is internal API usage, so it is less stable than using the `api` package.

### 13. Practical Example: External GUI Entry with Remaining Usage Display

```java
ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

int used = ShopHelper.getBuyUseTimes(item, player);
int limit = item.getPlayerBuyLimit(player);

if (limit != -1) {
    player.sendMessage("Remaining buy count: " + Math.max(0, limit - used));
}

ShopGUI.openGUI(player, item.getShopObject(), false, false);
```

This is a very common pattern:

* your own NPC or menu plugin only provides the entry point
* all product logic, pricing, limits, and sell rules are still delegated to UltimateShop

That lets you reuse UltimateShop's existing systems as much as possible.

### 14. Understanding `TakeResult`, `GiveResult`, and `AbstractSingleThing`

These three types are easiest to understand together:

* `AbstractThings` represents a whole things container, such as the full `products`, `buy-prices`, or `sell-prices` section
* `AbstractSingleThing` represents one single entry inside that container
* `TakeResult` / `GiveResult` represent the final selected single things for one transaction, together with their calculated final amounts

A practical mental model is:

* `AbstractThings` = a group of candidate rules
* `AbstractSingleThing` = one candidate branch
* `TakeResult` / `GiveResult` = the final selected branches for this transaction

#### 14.1 What `AbstractSingleThing` Is

`AbstractSingleThing` is the abstract base class for all single things, defined in `AbstractSingleThing.java`.

Conceptually, it describes one unit entry that can be checked, calculated, given, or taken.

In the current implementation, the common subclasses are:

* `ObjectSingleProduct`: one product entry under `products`
* `ObjectSinglePrice`: one price entry under `buy-prices` or `sell-prices`

The most important fields and responsibilities on it are:

* `type`: the single thing type, inferred from config
* `singleSection`: the config section for this one single thing
* `applyCondition`: whether this branch should participate in selection
* `requireCondition`: whether this branch is allowed to complete after being selected
* `giveAction` / `takeAction`: actions that run when it is actually given or taken
* `things`: the parent `AbstractThings` container

The supported `ThingType` values are not limited to just "item". The detection logic lives in `AbstractSingleThing.java`:

* `HOOK_ITEM`
* `MATCH_ITEM`
* `CUSTOM`
* `HOOK_ECONOMY`
* `VANILLA_ECONOMY`
* `VANILLA_ITEM`
* `FREE`
* `RESERVE`

So the important idea is that `AbstractSingleThing` is not "one item". It is "one transaction-capable unit".

#### 14.1.1 How Developers Can Tell Which Type a Single Thing Is

UltimateShop does not require you to declare `ThingType` manually. It infers the type from the config fields.

The detection order is important, because it is effectively "first matching rule wins".

Based on the current source logic, the rules are:

1. If both `hook-plugin` and `hook-item` exist, it is `HOOK_ITEM`
2. If `match-item` exists and `MythicChanger` is loaded, it is `MATCH_ITEM`
3. If `match-placeholder` exists and this is not the free version, it is `CUSTOM`
4. If `economy-plugin` exists, it is `HOOK_ECONOMY`
5. If `economy-type` exists but `economy-plugin` does not, it is `VANILLA_ECONOMY`
6. If `material` or `item` exists, it is `VANILLA_ITEM`
7. If only `amount`-style numeric definition exists and none of the above matched, it is `RESERVE`
8. If none of the above matched, it is `FREE`

You can use this quick mapping:

* `hook-plugin` + `hook-item` -> `HOOK_ITEM`
* `match-item` -> `MATCH_ITEM`
* `match-placeholder` -> `CUSTOM`
* `economy-plugin` -> `HOOK_ECONOMY`
* only `economy-type` -> `VANILLA_ECONOMY`
* `material` or `item` -> `VANILLA_ITEM`
* only `amount` -> `RESERVE`
* nothing meaningful defined -> `FREE`

Some common examples:

This is a `Vault` economy entry:

```yaml
buy-prices:
  1:
    economy-plugin: Vault
    amount: 100
    placeholder: '{amount}$'
```

so to know whether this is a Vault price, you can do this:

```java
Map<AbstractSingleThing, BigDecimal> resultMap = takeResult.getResultMap();
for (AbstractSingleThing singleThing : resultMap.keySet()) {
   if (singleThing.getSingleSection().getString("economy-plugin", "").equals("Vault") {
       return "This price includes Vault";
   }
}
```

#### 14.2 What `AbstractSingleThing` Does During Transactions

It mainly handles four jobs:

1. deciding whether it should participate
2. reading how much of it the player currently has
3. checking whether the player has enough to pay or enough room to receive it
4. producing real give/take behavior

The key methods are:

* `getApplyCondition(...)`: checks `apply-conditions`
* `getRequireCondition(...)`: checks `require-conditions`
* `playerHasAmount(...)`: reads owned amount
* `playerHasEnough(...)`: checks affordability / availability and can optionally perform the take
* `playerCanGive(...)`: checks whether this thing can be given

This is also why the distinction matters so much:

* `apply-conditions` decide whether a branch is selected
* `require-conditions` decide whether a selected branch is allowed to continue

#### 14.3 What `TakeResult` Is

`TakeResult` is the result object for "what this transaction needs to take", defined in `TakeResult.java`.

Its most important internal structure is:

```java
Map<AbstractSingleThing, BigDecimal> resultMap
```

That means:

* key: the selected single thing
* value: the final calculated amount / cost for that single thing in this transaction

The key fields are:

* `resultBoolean`: whether the player has enough to pay / turn in these things
* `conditionBoolean`: whether all selected single things passed their `require-conditions`
* `empty`: whether the result is empty

The most important distinction is:

* `resultBoolean` answers "is it affordable / available"
* `conditionBoolean` answers "is it allowed"

`TakeResult.addResultMapElement(...)` checks `require-conditions` while the entries are being added.

Actual execution happens in `take(...)`.

That execution is not just "remove items". It does this:

* calls `playerHasEnough(..., true, cost)` for each selected single thing
* then runs the single thing's `takeAction`

So `TakeResult` is best understood as:

* first, "what needs to be taken"
* then, "execute the take and its related actions"

#### 14.4 What `GiveResult` Is

`GiveResult` is the result object for "what this transaction needs to give", defined in `GiveResult.java`.

Like `TakeResult`, it also stores:

```java
Map<AbstractSingleThing, BigDecimal> resultMap
```

That means:

* key: the selected single thing
* value: the final amount that should be given for that single thing

It also tracks:

* `conditionBoolean`
* `empty`

But it does not have a `resultBoolean` field.

That is because "can this actually be given" is not fixed when the result object is created. It is checked later inside `give(...)`, where inventory capacity and item delivery are evaluated together.

The `give(...)` flow is:

1. iterate over `resultMap`
2. call `playerCanGive(...)` on each `AbstractSingleThing`
3. collect all `GiveItemStack` objects
4. if any entry cannot be given, return `false`
5. if all entries can be given, actually give them

So the right way to think about `GiveResult` is:

* it describes "what should be given"
* whether it can actually be delivered is only known at `give(...)` time

#### 14.5 Where They Sit in the Full Transaction Flow

A buy flow can be understood like this:

1. `ObjectPrices.take(...)` calculates the cost and returns a `TakeResult`
2. `ObjectProducts.give(...)` calculates the reward and returns a `GiveResult`
3. the trade logic checks `TakeResult.getResultBoolean()`
4. then checks `TakeResult.getConditionBoolean()` and `GiveResult.getConditionBoolean()`
5. then executes `GiveResult.give(...)`
6. finally executes `TakeResult.take(...)`

The shared container abstraction is `AbstractThings.java`, which defines:

* `give(...) -> GiveResult`
* `take(...) -> TakeResult`

So the most stable external mental model is:

* `AbstractThings` chooses final branches from a candidate set
* `AbstractSingleThing` handles the logic of one branch
* `TakeResult` / `GiveResult` store and execute the final selected result

### 15. Hook Integration: Economy Sources and Item Sources

UltimateShop manages economy hooks and item hooks through `HookManager`.

Built-in implementations are auto-registered at startup, for example:

* Economy: Vault, PlayerPoints, CoinsEngine, UltraEconomy, and more
* Items: ItemsAdder, Oraxen, MMOItems, EcoItems, Nexo, CraftEngine, and more

But `HookManager` also exposes public registration methods:

* `registerNewEconomyHook(...)`
* `registerNewItemHook(...)`

That means other developers can register custom economy or item sources from their own plugin.

#### 15.1 Custom Economy Hook

The abstract base class is `AbstractEconomyHook.java`.

The core methods you need to implement are:

* `getEconomy(Player, currencyID)`: read balance
* `takeEconomy(Player, value, currencyID)`: withdraw
* `giveEconomy(Player, value, currencyID)`: deposit

Optional:

* `isEnabled()`: return `false` if the underlying provider is not ready

The `currencyID` parameter comes from the config field `economy-type`:

```yaml
buy-prices:
  1:
    economy-plugin: MyEconomy
    economy-type: gems
    amount: 100
    placeholder: '{amount} Gems'
```

Single-currency hooks like Vault will usually ignore `currencyID`, while multi-currency hooks like CoinsEngine use it to resolve the specific currency.

Minimal example:

```java
public final class MyEconomyHook extends AbstractEconomyHook {

    public MyEconomyHook() {
        super("MyEconomy");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        return MyEconomyApi.balance(player.getUniqueId(), currencyID);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        MyEconomyApi.withdraw(player.getUniqueId(), currencyID, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        MyEconomyApi.deposit(player.getUniqueId(), currencyID, value);
    }
}
```

Registration:

```java
HookManager.hookManager.registerNewEconomyHook("MyEconomy", new MyEconomyHook());
```

Important notes:

* the registration name must exactly match `economy-plugin` in config
* if you support multiple currencies, treat `economy-type` as your `currencyID`
* the default `AbstractEconomyHook.checkEconomy(...)` implementation checks balance first, then calls `takeEconomy(...)` when needed

#### 15.2 Custom Item Source Hook

The abstract base class is `AbstractItemHook.java`.

You need to implement two core methods:

* `getHookItemByID(Player, itemID)`: build an item from the configured ID
* `getIDByItemStack(ItemStack)`: reverse-resolve an item back into your source ID

This maps to config like:

```yaml
products:
  1:
    hook-plugin: MyItems
    hook-item: sword_of_flame
```

Minimal example:

```java
public final class MyItemHook extends AbstractItemHook {

    public MyItemHook() {
        super("MyItems");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String itemID) {
        ItemStack item = MyItemsApi.build(itemID);
        return item == null ? returnNullItem(itemID) : item;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return MyItemsApi.findId(hookItem);
    }
}
```

Registration:

```java
HookManager.hookManager.registerNewItemHook("MyItems", new MyItemHook());
```

Important notes:

* the registration name must exactly match `hook-plugin` in config
* the actual `hook-item` format is entirely defined by your hook
* if `getIDByItemStack(...)` cannot resolve an ID, UltimateShop can still build items from direct `hook-item` config, but reverse item-source detection features will not work

#### 15.3 Good Built-In References

If you want working examples to copy from, the best built-in references are:

* Economy hooks:
  * `EconomyVaultHook`: single currency, services-manager based
  * `EconomyCoinsEngineHook`: multi-currency, explicitly uses `currencyID`
* Item hooks:
  * `ItemItemsAdderHook`: simple ID -> ItemStack and ItemStack -> ID
  * `ItemMMOItemsHook`: composite ID format using `TYPE;;ID`

Together, these show the hook style UltimateShop expects.

