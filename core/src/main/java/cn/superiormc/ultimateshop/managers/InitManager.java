package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class InitManager extends AbstractManager {

    private static final Set<String> MENU_SUBFOLDERS = Set.of(
        "shop_menu_templates", "buy_more_menus", "favourite_menus", "search_menus", "item_sell_menus");

    public static InitManager initManager;

    private boolean firstLoad = false;

    public InitManager() {
        initManager = this;
        File file = new File(UltimateShop.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            UltimateShop.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        migrateLegacyFolders();
        resourceOutput("languages/en_US.yml", true);
        resourceOutput("languages/zh_CN.yml", true);
        resourceOutput("languages/cs_CZ.yml", true);
        resourceOutput("languages/sk_SK.yml", true);
        resourceOutput("languages/es_ES.yml", true);
        resourceOutput("languages/de_DE.yml", true);
        resourceOutput("languages/pt_BR.yml", true);
        resourceOutput("languages/tr_TR.yml", true);
        resourceOutput("editor-enable-confirm.yml", true);
        resourceOutput("shops/concretes.yml", false);
        resourceOutput("shops/blocks.yml", false);
        resourceOutput("shops/blocks2.yml", false);
        resourceOutput("shops/farming.yml", false);
        resourceOutput("shops/fish.yml", false);
        resourceOutput("shops/flowers.yml", false);
        resourceOutput("shops/glass.yml", false);
        resourceOutput("shops/drops.yml", false);
        resourceOutput("shops/minerals.yml", false);
        resourceOutput("shops/logs.yml", false);
        resourceOutput("shops/redstone.yml", false);
        resourceOutput("shops/terracottas.yml", false);
        resourceOutput("shops/special.yml", false);
        resourceOutput("shops/transport.yml", false);
        resourceOutput("shops/wools.yml", false);
        resourceOutput("shops/example.yml", false);
        resourceOutput("shops/daily.yml", false);
        resourceOutput("menus/common_menus/main.yml", false);
        resourceOutput("menus/buy_more_menus/buy-more.yml", false);
        resourceOutput("menus/buy_more_menus/buy-more-buy.yml", false);
        resourceOutput("menus/buy_more_menus/buy-more-sell.yml", false);
        resourceOutput("menus/search_menus/search.yml", false);
        resourceOutput("menus/favourite_menus/favourite.yml", false);
        resourceOutput("menus/item_sell_menus/item-sell.yml", false);
        resourceOutput("menus/shop_menu_templates/example-shop-menu.yml", false);
        resourceOutput("menus/shop_menu_templates/daily-shop-menu.yml", false);
        resourceOutput("shared_use_times/example.yml", true);
        resourceOutput("placeholders/random_placeholders/daily.yml", false);
        resourceOutput("placeholders/random_placeholders/reset.yml", false);
        resourceOutput("placeholders/random_placeholders/rate_example.yml", false);
        resourceOutput("placeholders/custom_placeholders/example.yml", false);
        resourceOutput("sell_sticks/A.yml", false);
        resourceOutput("sell_sticks/B.yml", false);
        resourceOutput("sell_sticks/C.yml", false);
        resourceOutput("sell_chests/A.yml", false);
        resourceOutput("placeholders/conditional_placeholders/buy.yml", false);
        resourceOutput("placeholders/conditional_placeholders/sell.yml", false);
        resourceOutput("placeholders/conditional_placeholders/example.yml", false);
    }

    private void migrateLegacyFolders() {
        File dataFolder = UltimateShop.instance.getDataFolder();
        for (String name : MENU_SUBFOLDERS) {
            migrate(new File(dataFolder, name), new File(dataFolder, "menus/" + name));
        }
        File menuFolder = new File(dataFolder, "menus");
        File[] commonMenus = menuFolder.listFiles();
        if (commonMenus != null) {
            for (File file : commonMenus) {
                if (!MENU_SUBFOLDERS.contains(file.getName()) && !file.getName().equals("common_menus")) {
                    migrate(file, new File(menuFolder, "common_menus/" + file.getName()));
                }
            }
        }
        for (String name : List.of("random_placeholders", "custom_placeholders", "conditional_placeholders")) {
            migrate(new File(dataFolder, name), new File(dataFolder, "placeholders/" + name));
        }
    }

    private void migrate(File source, File target) {
        if (!source.exists()) {
            return;
        }
        try {
            if (source.isDirectory()) {
                Files.createDirectories(target.toPath());
                File[] children = source.listFiles();
                if (children == null) {
                    throw new IOException("Cannot list directory " + source);
                }
                for (File child : children) {
                    migrate(child, new File(target, child.getName()));
                }
                File[] remaining = source.listFiles();
                if (remaining != null && remaining.length == 0) {
                    Files.delete(source.toPath());
                }
            } else if (target.exists()) {
                ErrorManager.errorManager.sendErrorMessage("§cCannot migrate " + source + ": " + target
                    + " already exists. The old file was preserved; merge it manually into the new location.");
            } else {
                Files.createDirectories(target.toPath().getParent());
                Files.move(source.toPath(), target.toPath());
            }
        } catch (IOException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cCannot migrate " + source + " to " + target + ": " + exception.getMessage());
        }
    }

    private void resourceOutput(String fileName, boolean regenerate) {
        File tempVal1 = new File(UltimateShop.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !regenerate) {
                return;
            }
            if (tempVal1.getParentFile() != null) {
                CommonUtil.mkDir(tempVal1.getParentFile());
            }
            UltimateShop.instance.saveResource(fileName, false);
        }
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }
}
