package cn.superiormc.ultimateshop.gui.inv.editor.subinventory;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditProductGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ChooseSingleProductGUI extends InvGUI {

    private Map<Integer, String> itemCache = new HashMap<>();

    private int needPages = 1;

    private int nowPage = 1;

    private ConfigurationSection section;

    public ChooseSingleProductGUI(Player player, EditProductGUI gui) {
        super(player);
        this.previousGUI = gui;
        this.section = gui.section.getConfigurationSection("products");
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (section == null) {
            return;
        }
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, 54,
                    TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                            "choose-single-product-gui.title")));
        }
        int i = 0;
        for (String tempVal1 : section.getKeys(false)) {
            itemCache.put(i, tempVal1);
            i ++;
        }
        if (itemCache.size() >= 45) {
            needPages = (int) (Math.ceil(itemCache.size() / 45));
        }
        for (int c = 0 ; c < 45 ; c ++) {
            String tempVal2 = itemCache.get((nowPage - 1)  * 45 + c);
            if (tempVal2 == null) {
                break;
            }
            ItemStack productItem = new ItemStack(Material.ANVIL);
            ConfigurationSection tempVal5 = section.getConfigurationSection(tempVal2);
            if (tempVal5 != null && tempVal5.contains("material")) {
                productItem = ItemUtil.buildItemStack(owner, tempVal5, (int)
                        MathUtil.doCalculate(TextUtil.withPAPI(tempVal5.getString("amount", "1"), owner)));
            }
            ItemMeta tempVal1 = productItem.getItemMeta();
            if (!tempVal1.hasDisplayName()) {
                tempVal1.setDisplayName(TextUtil.parse("&e" + tempVal2));
            }
            List<String> tempVal3 = new ArrayList<>();
            if (tempVal1.hasLore()) {
                tempVal3 = tempVal1.getLore();
            }
            tempVal3.addAll(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                    "choose-single-product-gui.single-product.add-lore")));
            tempVal1.setLore(tempVal3);
            productItem.setItemMeta(tempVal1);
            inv.setItem(c, productItem);
        }
        if (nowPage != 1) {
            ItemStack nextPageItem = new ItemStack(Material.ARROW);
            ItemMeta tempVal2 = nextPageItem.getItemMeta();
            tempVal2.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                    "choose-shop-gui.next-page.name")));
            tempVal2.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                    "choose-shop-gui.next-page.lore")));
            nextPageItem.setItemMeta(tempVal2);
            inv.setItem(52, nextPageItem);
        }
        if (nowPage != needPages) {
            ItemStack previousPageItem = new ItemStack(Material.ARROW);
            ItemMeta tempVal3 = previousPageItem.getItemMeta();
            tempVal3.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                    "choose-shop-gui.previous-page.name")));
            tempVal3.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                    "choose-shop-gui.previous-page.lore")));
            previousPageItem.setItemMeta(tempVal3);
            inv.setItem(46, previousPageItem);
        }
        ItemStack createNewProductItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta tempVal4 = createNewProductItem.getItemMeta();
        tempVal4.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "choose-single-product-gui.create.name")));
        tempVal4.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "choose-single-product-gui.create.lore")));
        createNewProductItem.setItemMeta(tempVal4);
        inv.setItem(53, createNewProductItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == 46) {
            nowPage--;
            constructGUI();
        }
        else if (slot == 52) {
            nowPage++;
            constructGUI();
        }
        else if (slot < 45 && inventory.getItem(slot) != null){
            return false;
        }
        return true;
    }

    @Override
    public void afterClickEventHandle(ItemStack item, ItemStack currentItem, int slot) {
        if (slot < 45) {
            ConfigurationSection tempVal1 = section.getConfigurationSection(itemCache.get((nowPage - 1)  * 45 + slot));
            if (tempVal1 == null) {
                return;
            }
            Map<String, Object> tempVal2 = ItemUtil.debuildItem(item);
            for (String key : tempVal1.getKeys(true)) {
                tempVal1.set(key, null);
            }
            for (String key : tempVal2.keySet()) {
                tempVal1.set(key, tempVal2.get(key));
            }
            constructGUI();
        }
    }
}
