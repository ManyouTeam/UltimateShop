package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class SellAllGUI extends InvGUI {

    public SellAllGUI(Player owner) {
        super(owner);
        constructGUI();
    }

    @Override
    public void openGUI() {
        if (inv == null) {
            return;
        }
        owner.getPlayer().openInventory(inv);
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, ConfigManager.configManager.getInt
                            ("menu.sell-all.size", 54),
                    TextUtil.parse(ConfigManager.configManager.getString("menu.sell-all.title")));
        }
    }

    @Override
    public boolean clickEventHandle(ClickType type, int slot) {
        return false;
    }

    @Override
    public boolean closeEventHandle() {
        for (String shop : ConfigManager.configManager.shopConfigs.keySet()) {
            for (ObjectItem products : ConfigManager.configManager.getShop(shop).getProductList()) {
                SellProductMethod.startSell(inv,
                        shop,
                        products.getProduct(),
                        owner.getPlayer(),
                        false,
                        false,
                        true,
                        1);
            }
        }
        ItemStack[] storage = Arrays.stream(inv.getStorageContents()).filter(Objects::nonNull).toArray(ItemStack[]::new);
        owner.getInventory().addItem(storage);
        return true;
    }

    @Override
    public boolean dragEventHandle(Set<Integer> slots) {
        owner.updateInventory();
        return false;
    }

}
