package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectMoreButton extends AbstractButton {

    public ObjectMoreButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.SELECT_AMOUNT;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int unUsed) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return ObjectDisplayItemStack.getAir();
        }
        String amount = tempVal1.getString("amount", "1");
        return new ObjectDisplayItemStack(BuildItem.buildItemStack(player, tempVal1,
                MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue()));
    }
}
