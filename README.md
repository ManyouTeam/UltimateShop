# 🛒  Welcome to **UltimateShop**

> **UltimateShop** is a powerful shop plugin for Spigot!

---

## 🔒 No Need to Worry About Custom Item Changes

**UltimateShop** uses **NBT-based item recognition** instead of comparing entire items.  
It fully supports compatibility with most popular item plugins like **MMOItems**, **eco**, **ItemsAdder**, **Nexo**, **Oraxen**, **MythicMobs**, **CraftEngine** etc..

Even if an item is enchanted, renamed with an anvil, or modified by other plugins (like lore changes),  
UltimateShop can still recognize it correctly and allow it to be sold.

You don’t need commands to give players items.  
UltimateShop’s built-in item syntax supports all these plugins natively —  
just **two lines of configuration** enable buying and selling functionality.

Need to tweak items from other plugins (e.g., change name, replace lore)?  
UltimateShop fully supports modifying items based on plugin-provided templates.

---

## 🧭 Menu System

UltimateShop includes a fully customizable menu system inspired by **TrMenu** slot configuration.

- Customize item layout for each shop.
- Support auto-check limits, prevent misclicks.
- Set custom click actions for buttons or products.
- Add custom buttons with actions and conditions.
- Fully configurable menus both inside and outside shops.
- One config file working both for Java chest UI and Bedrock form UI. (PREMIUM)
- Support auto update menu buttons and titles. (Update title require PREMIUM)

---

## ⏳ Item Limits and Cooldowns

UltimateShop allows you to configure:
- Global and personal limits for **buy** and **sell** (4 attributes);
- Global and personal cooldowns for **buy** and **sell** (4 attributes).

That’s a total of **8 configurable attributes**, usually only found in premium shop plugins!

Reset modes include:
- Daily/weekly/monthly reset;
- Timer-based reset;
- Cron expression reset;
- Permanent (no reset);
- Custom reset via placeholders from other plugins.
  Support recalculating the reset time with each purchase or selling, or saving the time until the next reset time arrives.


Personal limits can be conditional — for example, VIPs can have higher limits.  
Both personal and global limits support math expressions and PlaceholderAPI variables.  
You can even create a **real-stock system**, where items can only be bought after others sell them, keeping the economy balanced.

Cooldowns ensure players must wait a period after each buy or sell before repeating.

---

## 💰 Highly Customizable Prices and Products

**UltimateShop** uses a **many-to-many relationship** between products and prices.
This means:

- A **single product** can be bought or sold using **multiple different price options**.  
  For example, one item could be purchased with Vault currency, PlayerPoints, or by trading another material.

- At the same time, a **single price rule** can apply to **multiple different products**.  
  So you don’t need to duplicate price settings for every item — one price definition can serve all products that link to it.

Prices and products can both be defined using **items or currency**.  
Supports:
- 10+ economy plugins;
- Multiple apply times, rules and conditions per single price;
- Math operations and PlaceholderAPI variables;
- Seasonal or time-based pricing;
- Discounts and random shops.

With this flexibility, you can:
- Set VIP discounts;
- Create daily limited offers;
- Product cheaper after buy 10 times;
- Rotate daily random shops;
- Implement a **dynamic market** where frequent purchases increase prices, and frequent sales lower them.

You can even exchange money for points or create custom virtual currencies —  
no need for extra plugins.

Suppport:
- Use item or economy as products or prices.
- Use placeholder to check price and use actions to take money.
- Use contains lore or name etc. check item and take them.

---

## 📦 Item and Economy Format

UltimateShop supports **item and economy format** in:
- Prices and products;
- Menus and display items.

Powered by the **ManyouItems**, you can:
- Sell detailed vanilla items (e.g., custom cloaks, mob spawners);
- Support partial Mod items;
- Sell custom tool, custom armor, custom food, etc.;
- Almost all vanilla item component can be easily configure by using ItemFormat.
- Retrieve items directly from other plugins with just two lines of config.

Economy format supports:
- Vanilla XP and XP levels;
- 10+ third-party economy plugins.

---

## 📄 Powerful Placeholders

Support use those placeholders almost everywhere you can!

Including:
- Math Placeholder
- Cron Placeholder
- Random Placeholder
- Conditional Placeholder
- Lang Placeholder
- Compare Placeholder
etc.

---

## ⚙️ Actions and Conditions

UltimateShop allows actions and conditions to be triggered by:
- Buying or selling items;
- Fail actions;
- Clicking buttons;
- Opening/Closing menus;
- and much more!

**Available actions:**
- Run commands;
- Spawn entities;
- Play sounds;
- Teleport players;
- and 10+ much more!

**Available conditions:**
- PlaceholderAPI;
- World, biome, or permission checks.

---

## 🧱 Advanced Features (Some PREMIUM Only)

- **Fully MiniMessage and Legacy Color Parser support**.
- **Common Message/Action Bar/Title/Boss Bar/Sound** support in language message!
- **Per Player Language**: Display the corresponding custom text content based on the player's client language.
- **Buy More Menu**: Choose quantity, support buy only, sell only and common buy more menu.
- **Quick-Sell Menu**: Drag and drop items for instant auto-sell.
- **Plugin Enchant Support**: Add plugin enchantments like AdvancedEnchantments via item syntax.
- **Sell Wand**: Quickly sell items inside containers by clicking them.
- **Sell Chest**: Auto selling items inside.
- **Bedrock Menu Support**: Detect Floodgate players and auto-convert GUI to Bedrock FormUI.

---

### ❤️ UltimateShop — The Most Flexible Economy Shop System


Consider respect my work and buy the plugin here, you can get free support, subbmit suggestion service. [Click to buy](https://www.spigotmc.org/resources/ultimateshop-premium-menu-dynamic-price-limits-apply-settings-sell-all-and-more-1-17-1-20.113069/)

You can also get free version here. [Click to download](https://www.spigotmc.org/resources/ultimateshop-menus-limits-apply-settings-10-directly-hook-and-more-1-17-1-20.110601/)