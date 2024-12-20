package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.io.*;

public class InitManager {
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
        resourceOutput("languages/en_US.yml", true);
        resourceOutput("languages/zh_CN.yml", true);
        resourceOutput("languages/cs_CZ.yml", true);
        resourceOutput("languages/sk_SK.yml", true);
        resourceOutput("languages/es_ES.yml", true);
        resourceOutput("languages/de_DE.yml", true);
        resourceOutput("languages/pt_BR.yml", true);
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
        resourceOutput("menus/main.yml", false);
        resourceOutput("menus/buy-more.yml", false);
        resourceOutput("menus/buy-more-buy.yml", false);
        resourceOutput("menus/buy-more-sell.yml", false);
        resourceOutput("menus/example-shop-menu.yml", false);
        resourceOutput("shops/daily.yml", false);
    }

    private void resourceOutput(String fileName, boolean regenerate) {
        File tempVal1 = new File(UltimateShop.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !regenerate) {
                return;
            }
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            UltimateShop.instance.saveResource(tempVal2.getPath(), false);
        }
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }
}
