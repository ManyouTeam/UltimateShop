package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EditProductGUI extends InvGUI {

    public static Map<Player, EditProductGUI> guiCache = new HashMap<>();

    public EditorProductMode editMode = EditorProductMode.NOT_EDITING;

    public ConfigurationSection section;

    private YamlConfiguration config;

    private final File file;

    public EditProductGUI(Player owner, ObjectItem item) {
        super(owner);
        String fileName = item.getShopObject().getShopName();
        File dir = new File(UltimateShop.instance.getDataFolder() + "/shops");
        if (!dir.exists()) {
            dir.mkdir();
        }
        this.file = new File(dir, fileName + ".yml");
        if (!file.exists()) {
            LanguageManager.languageManager.sendStringText(owner,
                    "error.shop-not-found",
                    "shop",
                    item.getShop());
            return;
        }
        this.config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        this.section = config.getConfigurationSection("items." + item.getItemConfig().getName());
        guiCache.put(owner, this);
    }

    @Override
    public void openGUI() {
        editMode = EditorProductMode.NOT_EDITING;
        constructGUI();
        if (inv != null) {
            owner.getPlayer().openInventory(inv);
        }
    }

    @Override
    protected void constructGUI() {
        // price type
        ItemStack priceTypeItem = new ItemStack(Material.WHEAT);
        ItemMeta tempVal1 = priceTypeItem.getItemMeta();
        tempVal1.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-product-gui.price-type.name")));
        tempVal1.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-product-gui.price-type.lore")),
                "value",
                section.getString("price-mode", "ANY")));
        priceTypeItem.setItemMeta(tempVal1);
        // product type
        ItemStack productTypeItem = new ItemStack(Material.CARROT);
        ItemMeta tempVal2 = productTypeItem.getItemMeta();
        tempVal2.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-product-gui.product-type.name")));
        tempVal2.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-product-gui.product-type.lore")),
                "value",
                section.getString("product-mode", "ANY")));
        productTypeItem.setItemMeta(tempVal2);
        // buy price
        ItemStack buyPricesItem = new ItemStack(Material.EMERALD);
        ItemMeta tempVal3 = buyPricesItem.getItemMeta();
        tempVal3.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-product-gui.buy-prices.name")));
        tempVal3.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-product-gui.buy-prices.lore"))));
        buyPricesItem.setItemMeta(tempVal3);
        // sell price
        ItemStack sellPricesItem = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta tempVal4 = buyPricesItem.getItemMeta();
        tempVal4.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-product-gui.sell-prices.name")));
        tempVal4.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "edit-product-gui.sell-prices.lore"))));
        sellPricesItem.setItemMeta(tempVal4);
        // display item
        ItemStack displayItem = new ItemStack(Material.ITEM_FRAME);
        ItemMeta tempVal5 = buyPricesItem.getItemMeta();
        tempVal5.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-product-gui.display-item.name")));
        tempVal5.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "edit-product-gui.display-item.lore"))));
        displayItem.setItemMeta(tempVal5);
        // finish
        ItemStack finishItem = new ItemStack(Material.GREEN_DYE);
        ItemMeta tempVal6 = finishItem.getItemMeta();
        tempVal6.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.finish.name")));
        tempVal6.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "create-shop-gui.finish.lore")));
        finishItem.setItemMeta(tempVal6);
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, 9,
                    TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                            "edit-product-gui.title")));
        }
        inv.setItem(0, priceTypeItem);
        inv.setItem(1, productTypeItem);
        inv.setItem(2, buyPricesItem);
        inv.setItem(3, sellPricesItem);
        inv.setItem(4, displayItem);
        inv.setItem(8, finishItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (!Objects.equals(inventory, getInv())) {
            return true;
        }
        if (slot == 0) {
            if (section.getString("price-mode", "ANY").equals("ANY")) {
                section.set("price-mode", "ALL");
            } else if (section.getString("price-mode").equals("ALL")) {
                section.set("price-mode", "CLASSIC_ANY");
            } else if (section.getString("price-mode").equals("CLASSIC_ANY")) {
                section.set("price-mode", "CLASSIC_ALL");
            } else if (section.getString("price-mode").equals("CLASSIC_ALL")) {
                section.set("price-mode", "ANY");
            }
            constructGUI();
        }
        if (slot == 1) {
            if (section.getString("product-mode", "ANY").equals("ANY")) {
                section.set("product-mode", "ALL");
            } else if (section.getString("product-mode").equals("ALL")) {
                section.set("product-mode", "CLASSIC_ANY");
            } else if (section.getString("product-mode").equals("CLASSIC_ANY")) {
                section.set("product-mode", "CLASSIC_ALL");
            } else if (section.getString("product-mode").equals("CLASSIC_ALL")) {
                section.set("product-mode", "ANY");
            }
            constructGUI();
        }
        if (slot == 8) {
            file.delete();
            try {
                config.save(file);
                ReloadPlugin.reload(owner);
                LanguageManager.languageManager.sendStringText(owner,
                        "editor.product-edited",
                        "product",
                        section.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            owner.closeInventory();
        }
        return true;
    }

    @Override
    public boolean closeEventHandle() {
        if (editMode == EditorProductMode.NOT_EDITING) {
            guiCache.remove(owner);
            return true;
        }
        return false;
    }

    @Override
    public boolean dragEventHandle(Set<Integer> slots) {
        return true;
    }
}
