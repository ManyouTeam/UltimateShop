package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.InvUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.cryptomorin.xserieschanged.XItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SellAllGUI extends InvGUI {

    public SellAllGUI(Player owner) {
        super(owner);
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = InvUtil.createNewInv(owner, ConfigManager.configManager.getInt
                            ("menu.sell-all.size", 54),
                    TextUtil.parse(ConfigManager.configManager.getString("menu.sell-all.title")),
                    ConfigManager.configManager.getString("menu.sell-all.font"));
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        return ConfigManager.configManager.getIntList("menu.sell-all.black-slots").contains(slot);
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (owner.getPlayer() == null) {
            return true;
        }
        LanguageManager.languageManager.sendStringText(owner.getPlayer(), "start-selling");
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
        XItemStack.giveOrDrop(owner.getPlayer(), storage);
        return true;
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        owner.updateInventory();
        for (int i : newItems.keySet()) {
            if (ConfigManager.configManager.getIntList("menu.sell-all.black-slots").contains(i)) {
                return true;
            }
        }
        return false;
    }

}
