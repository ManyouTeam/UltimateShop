# ❓常见问题

## 问：为什么玩家购买商品不扣钱？

答：通常是因为你给自己授予了 `*` 权限，插件因此跳过了价格检查。普通玩家不会受影响。

## 问：我的物品看起来与商店中的一样，为什么无法出售？

答：它们实际上存在细微差异，只是你没有发现。最简单的解决办法是将 `config.yml` 中的 `sell-methods` 从 `Bukkit` 改为 `ItemFormat`。

## 问：`/shop` 命令有权限要求吗？

答：执行 `/shop` 可以直接打开名为 `main` 的菜单，这是**自动打开**功能。可通过 `config.yml` 中的 `menu.auto-open.enabled` 关闭。若只希望部分玩家使用，可以为菜单设置 `conditions`，让满足条件的玩家才能打开。详见[菜单](../menus/general-menus.md)。

## 问：打开每日商店示例菜单时，为什么提示条件不满足？

答：该菜单本身已设置条件，请查看 `menus/shop_menu_templates/daily-shop-menu.yml`。

## 问：为什么一次只能出售 64 个或其他固定数量的物品？

答：使用插件前应先熟悉 `config.yml`。其中的 `sell.max-amount` 用于限制出售数量，请检查该选项。

## 问：如何修改批量购买菜单的数量上限？

答：使用 `config.yml` 中的 `menu.buy-more.<buy more menu name>.max-amount`。如果为某件商品单独配置了批量购买菜单，则使用商品配置中的 `buy-more-menu.max-amount`。

## 问：`start-apply` 是什么意思？

答：它表示价格从第几次交易开始生效。例如设为 5，玩家购买或出售该商品达到 5 次后，就会应用此价格。

该选项仅适用于 `ANY` 或 `ALL` 价格模式。

## 问：快速点击菜单外的物品时出现复制问题。

答：这通常由其他插件或模组导致。可以尝试启用 `config.yml` 中的 `ignore-click-outside`。

## 问：免费版与付费版有什么区别？

答：请查看[功能对比](compare.md)。

## 问：能否为每件商品设置不同的附加描述？

答：可以。[商店](../shops/shops.md)页面已说明，`add-lore` 同样可用于单个商品的配置。

## 问：商店菜单打开一次后就无法再次打开。

答：请确认 `config.yml` 使用最新格式，否则先更新配置。也可能是菜单冷却系统所致，关闭冷却后再试。

## 问：如何翻译批量购买菜单和插件消息中的物品名称？

答：请查看[物品名称本地化](../features/localized-item-name-premium.md)。&#x20;

## 问：为什么我的物品名称显示为中文？

答：关闭[物品名称本地化](../features/localized-item-name-premium.md)功能即可。

## 问：UltimateShop 在控制台输出 `Error:XXX`。

答：具体原因已写在错误消息中，例如：

#### Error: Can not get prices section in your shop config!!（无法读取商店配置中的价格配置段）

如果商店没有实际异常，可以不作处理。这类错误消息起提示作用；插件检测到此错误后会自动尝试修复。

## 问：如何重置动态价格？

答：请查看[动态价格](../dynamic-prices/dynamic-price.md)。
