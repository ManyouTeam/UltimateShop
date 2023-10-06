package cn.superiormc.ultimateshop.methods.Create;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.hooks.PriceHook;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateProduct {

    public static void createShop(Player player, String... args) {
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            // shop createshop <商店名称> <菜单名称> <是否可以选择数量>
            if (args.length < 2) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.args");
                return;
            }
            File dir = new File(UltimateShop.instance.getDataFolder()+"/shops");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, args[0] + ".yml");
            if (file.exists()) {
                LanguageManager.languageManager.sendStringText(player,
                        "editor.shop-already-exists");
                return;
            }
            else {
                YamlConfiguration config = new YamlConfiguration();
                ConfigurationSection settingsSection = config.createSection("settings");
                Map<String, Object> data = new HashMap<>();
                if (ObjectMenu.commonMenus.containsKey(args[1]) ||
                        ObjectMenu.shopMenuNames.contains(args[1])) {
                    data.put("menu", args[1]);
                }
                else {
                    LanguageManager.languageManager.sendStringText(player,
                            "editor.menu-not-found");
                    return;
                }
                if (args.length == 2 || args[2].equals("true")) {
                    data.put("buy-more", "true");
                }
                else if (args[2].equals("false")) {
                    data.put("buy-more", "false");
                }
                else {
                    LanguageManager.languageManager.sendStringText(player,
                            "error.args");
                    return;
                }
                if (args.length == 3) {
                    data.put("shop-name", "{shop-name}");
                }
                else {
                    data.put("shop-name", args[3]);
                }
                for (String key : data.keySet()) {
                    settingsSection.set(key, data.get(key));
                }
                try {
                    config.save(file);
                    LanguageManager.languageManager.sendStringText(player,
                            "editor.shop-created",
                            "shop",
                            args[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void createProduct(Player player, String... args) {
        // createproduct <商店名称> <商品名称> <product 模式> <price 模式>
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (args.length < 2) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.args");
                return;
            }
            if (args[1].length() != 1) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.length-only-one");
                return;
            }
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[0]);
            if (tempVal1 == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.shop-not-found",
                        "shop",
                        args[0]);
                return;
            }
            String fileName = tempVal1.getShopName();
            File dir = new File(UltimateShop.instance.getDataFolder() + "/shops");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, fileName + ".yml");
            if (!file.exists()) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.shop-not-found",
                        "shop",
                        args[0]);
                return;
            }
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            if (itemsSection == null) {
                itemsSection = config.createSection("items");
            }
            ConfigurationSection productSection = itemsSection.getConfigurationSection(args[1]);
            if (productSection != null) {
                LanguageManager.languageManager.sendStringText(player,
                        "editor.product-already-exists",
                        "product",
                        args[1]);
                return;
            }
            productSection = itemsSection.createSection(args[1]);
            // 插入基础选项
            Map<String, Object> tempVal2 = new HashMap<>();
            if (args.length >= 3) {
                tempVal2.put("price-mode", args[2]);
            }
            else {
                tempVal2.put("price-mode", "CLASSIC");
            }
            if (args.length >= 4) {
                tempVal2.put("product-mode", args[3]);
            }
            else {
                tempVal2.put("product-mode", "CLASSIC");
            }
            for (String key : tempVal2.keySet()) {
                productSection.set(key, tempVal2.get(key));
            }
            // 插入商品选项
            ConfigurationSection subProductsSection = productSection.createSection("products");
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem.getType().isAir()) {
                LanguageManager.languageManager.sendStringText(player,
                        "editor.hand-item-empty");
                return;
            }
            ConfigurationSection subProductsFirstSection = subProductsSection.createSection("1");
            XItemStack.serialize(handItem, subProductsFirstSection);
            file.delete();
            try {
                config.save(file);
                ReloadPlugin.reload(player);
                LanguageManager.languageManager.sendStringText(player,
                        "editor.product-created",
                        "product",
                        args[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setProductPrice(Player player, boolean buyOrSell, String... args) {
        // setproductbuyprice <商店名称> <商品名称> <价格插件/物品> <物品ID> <价格/数量>
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (args.length < 5) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.args");
                return;
            }
            int startApply = 0;
            if (args.length >= 6) {
                startApply = Integer.parseInt(args[4]);
            }
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[0]);
            if (tempVal1 == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.shop-not-found",
                        "shop",
                        args[0]);
                return;
            }
            ObjectItem tempVal2 = tempVal1.getProduct(args[1]);
            if (tempVal2 == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.product-not-found",
                        "product",
                        args[1]);
                return;
            }
            String fileName = tempVal1.getShopName();
            File dir = new File(UltimateShop.instance.getDataFolder() + "/shops");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, fileName + ".yml");
            if (!file.exists()) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.shop-not-found",
                        "shop",
                        args[0]);
                return;
            }
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            if (itemsSection == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.product-not-found",
                        "product",
                        args[1]);
                return;
            }
            ConfigurationSection productSection = itemsSection.getConfigurationSection(args[1]);
            if (productSection == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.product-not-found",
                        "product",
                        args[1]);
                return;
            }
            ConfigurationSection priceSection = null;
            if (buyOrSell) {
                priceSection = productSection.getConfigurationSection("buy-prices");
            }
            else {
                priceSection = productSection.getConfigurationSection("sell-prices");
            }
            if (priceSection == null) {
                if (buyOrSell) {
                    priceSection = productSection.createSection("buy-prices");
                }
                else {
                    priceSection = productSection.createSection("sell-prices");
                }
            }
            ConfigurationSection subPriceFirstSection = priceSection.createSection("1");
            // 插入基础选项
            if (args[2].equals("handItem")) {
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem.getType().isAir()) {
                    LanguageManager.languageManager.sendStringText(player,
                            "editor.hand-item-empty");
                    return;
                }
                XItemStack.serialize(handItem, subPriceFirstSection);
                subPriceFirstSection.set("placeholder", TextUtil.parse(CommonUtil.getItemName(handItem)));
                subPriceFirstSection.set("start-apply", startApply);
            }
            else if (ItemsHook.getHookItem(args[2], args[3]) != null) {
                subPriceFirstSection.set("hook-plugin", args[2]);
                subPriceFirstSection.set("hook-item", args[3]);
                subPriceFirstSection.set("amount", args[4]);
                subPriceFirstSection.set("placeholder", TextUtil.parse(
                        CommonUtil.getItemName(ItemsHook.getHookItem(args[2], args[3]))));
                subPriceFirstSection.set("start-apply", startApply);
            }
            else if (PriceHook.getPrice(player, args[2], args[3], 0D, false)) {
                subPriceFirstSection.set("economy-plugin", args[2]);
                subPriceFirstSection.set("economy-type", args[3]);
                subPriceFirstSection.set("amount", args[4]);
                subPriceFirstSection.set("placeholder", args[3] + " x{amount}");
                subPriceFirstSection.set("start-apply", startApply);
            }
            else if (args[2].equals("levels") || args[2].equals("exp")) {
                subPriceFirstSection.set("economy-type", args[2]);
                subPriceFirstSection.set("amount", args[3]);
                subPriceFirstSection.set("placeholder", args[2] + " x{amount}");
                subPriceFirstSection.set("start-apply", startApply);
            }
            else {
                LanguageManager.languageManager.sendStringText(player,
                        "editor.unsupported-price-type");
                return;
            }
            file.delete();
            try {
                config.save(file);
                ReloadPlugin.reload(player);
                LanguageManager.languageManager.sendStringText(player,
                        "editor.product-created",
                        "product",
                        args[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
