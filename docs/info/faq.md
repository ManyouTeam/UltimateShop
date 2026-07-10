# ❓FAQ

## Q: Player don't cost money when buy product.

A: That is simply because you give self `*` permission so plugin will bypass price check for you. For common player, it will work correctly.

## Q: My item can not be sold in shop, but I think they are same item.

A: No, actually they have small different, but you dind't find them. The simplest way is to change the `sell-methods` option in `config.yml` from `Bukkit` to `ItemFormat`.

## Q: Does the /shop command has permission?

A: Use `/shop` can directly open a menu called `main`. This is a feature called **Auto Open**, and you can disable it at `config.yml` file with `menu.auto-open.enabled` option. If you only want some players to be exposed to this command, you can set `conditions` for the menu so that only players who meet the specified conditions can open the menu. For more info, please view [Menus](../menus/general-menus.md) page.

## Q: Why it say condition not meet when open daily example menu?

A: That menu already set a condition and you can find it at `menus/daily-shop-example.yml`.

## **Q: Why can I only sell 64x (or other amunt) items at once?**

**A:** The person asking this question may never know a truth: before using the plugin, you should be familiar with the `config.yml` file. In `config.yml`, there is an option called `sell.max-amount`, and I want you to know its purpose by looking at its name.

## Q: How to change the max amount limit of buy more menu?

A: There is a option called `menu.buy-more.<buy more menu name>.max-amount` option in `config.yml` file, or `buy-more-menu.max-amount` option in your product configs if you are setting up a separate buy more menu for a certain product.

## **Q: What does start-apply mean?**

A: This means which times this price will apply. If you set it to 5, price will apply after player buy or sell this product 5 times.

This option will only work for `ANY` or `ALL` price-mode.

## Q: Item dupe when quickly click item outside menu.

A: Some other plugin or mod lead to this problem. Try enable `ignore-click-outside` option jn `config.yml` file.

## Q: What is different from free version and premium version?

A: Check [this](compare.md) page for more info.

## Q: Can I set different add lore for each product?

A: Yes, in [Shop](../shops/shops.md) page we have told you that `add-lore` also works in each product configs!

## Q: Shop menu can not open after use once!

A: Make sure your `config.yml` is latest format, if not, update it. Or, you are using menu cooldown system, disable it may fix.

## Q: How can I translate item name in buy more menu and plugin message?

A: View [Localized Item Name](../features/localized-item-name-premium.md) page.&#x20;

## Q: Why my item name is in Chinese?

A: Disable [Localized Item Name](../features/localized-item-name-premium.md) feature.

## Q: UltimateShop print Error:XXX message in console.

A: What I want to tell you is already put in the error message itself, like:

#### Error: Can not get prices section in your shop config!!

If there are no issues with the shop, there is no need to pay attention to it. The error message reported by the plugin is a prompt, and the plugin will automatically attempt to fix it once it detects this error.

## Q: How to reset dynamic price?

A: View [Dynamic Price](../dynamic-prices/dynamic-price.md) page.
