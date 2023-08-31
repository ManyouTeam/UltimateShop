package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.io.File;

public class InitManager {
    public static InitManager initManager;

    private final boolean first;

    public InitManager(boolean first) {
        initManager = this;
        this.first = first;
        init();
    }

    public void init() {
        resourceOutputFix(new File("message.yml"));
        resourceOutput(new File("shops/example.yml"));
        resourceOutput(new File("menus/main.yml"));
        resourceOutput(new File("menus/example-shop-menu.yml"));
    }
    private void resourceOutput(File file) {
        if (!first) {
            return;
        }
        UltimateShop.instance.saveResource(file.getPath(), false);
    }

    private void resourceOutputFix(File file) {
        if (file.getParentFile() != null) {
            CommonUtil.mkDir(file.getParentFile());
        }
        if (!first) {
            return;
        }
        UltimateShop.instance.saveResource(file.getPath(), false);
    }
}
