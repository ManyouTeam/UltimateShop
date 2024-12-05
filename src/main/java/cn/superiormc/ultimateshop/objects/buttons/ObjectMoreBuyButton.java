package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectMoreBuyButton extends AbstractButton {

    private final ObjectItem item;

    public ObjectMoreBuyButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.CONFIRM;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return ObjectDisplayItemStack.getAir();
        }
        String amount = tempVal1.getString("amount", "1");
        ObjectDisplayItemStack addLoreDisplayItem = new ObjectDisplayItemStack(BuildItem.buildItemStack(player, tempVal1,
                MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue()));
        return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, true, getClickType());
    }

    public String getClickType() {
        return config.getString("click-action", null);
    }
}
