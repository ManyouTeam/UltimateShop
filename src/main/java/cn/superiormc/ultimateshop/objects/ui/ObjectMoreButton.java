package cn.superiormc.ultimateshop.objects.ui;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreButton extends AbstractButton {

    private ObjectItem item;

    private int amount;

    private int nowAmount;

    public ObjectMoreButton(ObjectItem item, int amount, int nowAmount) {
        super();
        this.item = item;
        this.amount = amount;
        this.nowAmount = nowAmount;
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        switch (ConfigManager.configManager.getClickAction(type)){
            case "buy" :
                BuyProductMethod.startBuy(item.getShop(),
                        item.getProduct(),
                        player,
                        false,
                        !b,
                        amount);
            case "sell" :
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        player,
                        false,
                        !b,
                        amount);
            default:
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cUnknown click action: "
                        + ConfigManager.configManager.getClickAction(type));
        }

    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        ItemStack tempVal1 = item.getDisplayItem(player);
        tempVal1.setAmount(amount);
        return tempVal1;
    }
}
