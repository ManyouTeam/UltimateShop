package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.io.File;

public class InitManager {
    public static InitManager initManager;

    public InitManager() {
        initManager = this;
        File file = new File(UltimateShop.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            UltimateShop.instance.saveDefaultConfig();
        }
        init();
    }

    public void init() {
        resourceOutput("message.yml", true);
        resourceOutput("shops/baits.yml", false);
        resourceOutput("shops/blocks.yml", false);
        resourceOutput("shops/crops.yml", false);
        resourceOutput("shops/crops1.yml", false);
        resourceOutput("shops/drops.yml", false);
        resourceOutput("shops/logs.yml", false);
        resourceOutput("shops/ores.yml", false);
        resourceOutput("menus/main.yml", false);
        resourceOutput("menus/buy-more.yml", false);
        resourceOutput("menus/example-shop-menu.yml", false);
    }
    private void resourceOutput(String fileName, boolean fix) {
        File tempVal1 = new File(UltimateShop.instance.getDataFolder(), fileName);
        if (!tempVal1.exists() && fix) {
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            UltimateShop.instance.saveResource(tempVal2.getPath(), false);
        }
    }
}
