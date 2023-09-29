package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleProduct extends AbstractSingleThing {

    private String amount;

    public ObjectSingleProduct() {
        super();
        amount = "0";
    }

    public ObjectSingleProduct(ConfigurationSection singleSection) {
        super(singleSection);
        amount = singleSection.getString("amount", "1");
    }

    public String getDisplayName(double amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        return CommonUtil.modifyString(tempVal1,
                "amount",
                String.valueOf(amount));
    }

    public double getAmount(Player player, int times) {
        return Double.parseDouble(TextUtil.withPAPI(amount, player));
    }

}
