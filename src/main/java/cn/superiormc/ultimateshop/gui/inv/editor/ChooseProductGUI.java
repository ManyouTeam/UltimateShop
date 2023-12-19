package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ChooseProductGUI extends InvGUI {

    private Map<Integer, ObjectItem> itemCache = new HashMap<>();

    private ObjectShop shop;

    private int needPages = 1;

    private int nowPage = 1;

    public ChooseProductGUI(Player owner, ObjectShop shop) {
        super(owner);
        this.shop = shop;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        int i = 0;
        for (ObjectItem item : shop.getProductList()) {
            itemCache.put(i, item);
            i ++;
        }
        if (itemCache.size() > 54) {
            needPages = (int) (Math.ceil(itemCache.size() / 45));
        }
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, 54,
                    TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                            "choose-product-gui.title")));
        }
        if (needPages == 1) {
            for (int c = 0 ; c < 54 ; c ++) {
                ObjectItem item = itemCache.get(c);
                if (item == null) {
                    break;
                }
                ItemStack productItem = new ItemStack(Material.EMERALD);
                ItemMeta tempVal1 = productItem.getItemMeta();
                tempVal1.setDisplayName(TextUtil.parse("&e" + item.getDisplayName(owner)));
                tempVal1.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "choose-product-gui.product.lore")));
                productItem.setItemMeta(tempVal1);
                inv.setItem(c, productItem);
            }
        }
        else {
            for (int c = 0 ; c < 45 ; c ++) {
                ObjectItem item = itemCache.get(nowPage * 45 + c);
                if (item == null) {
                    break;
                }
                ItemStack productItem = new ItemStack(Material.EMERALD);
                ItemMeta tempVal1 = productItem.getItemMeta();
                tempVal1.setDisplayName(TextUtil.parse("&e" + item.getDisplayName(owner)));
                tempVal1.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "choose-product-gui.product.lore")));
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
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (needPages == 1) {
            OpenGUI.openEditProductGUI(owner, itemCache.get(slot));
        }
        else {
            if (slot < 45) {
                OpenGUI.openEditProductGUI(owner, itemCache.get(nowPage * 45 + slot));
            }
            else if (slot == 46) {
                nowPage--;
                constructGUI();
            }
            else if (slot == 52) {
                nowPage++;
                constructGUI();
            }
        }
        return true;
    }

    @Override
    public boolean closeEventHandle() {
        return true;
    }

    @Override
    public boolean dragEventHandle(Set<Integer> slots) {
        return true;
    }
}
