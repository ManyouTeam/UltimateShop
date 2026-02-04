package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.libs.bstats.Metrics;
import cn.superiormc.ultimateshop.utils.TextUtil;

public final class LicenseManager {

    public static LicenseManager licenseManager;

    public final boolean valid;

    public LicenseManager() {
        licenseManager = this;
        new Metrics(UltimateShop.instance, 20783);
        this.valid = true;
        printStartupInfo();
    }

    private void printStartupInfo() {
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " No license found in your jar file. Seems that you are self-building this plugin.");
    }
}
