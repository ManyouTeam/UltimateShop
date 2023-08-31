package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectLimit;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectItem extends AbstractButton {

    private String shop;

    private ObjectDisplayItem displayItem;

    private ObjectPrices buyPrice;

    private ObjectPrices sellPrice;

    private ObjectProducts reward;

    private ObjectAction buyAction;

    private ObjectAction sellAction;

    private ObjectLimit buyLimit;

    private ObjectLimit sellLimit;

    public ObjectItem(String shop, ConfigurationSection config) {
        super(config);
        this.shop = shop;
        this.type = ButtonType.SHOP;
        initDisplayItem();
        initReward();
        initBuyPrice();
        initSellPrice();
        initBuyAction();
        initSellAction();
        initBuyLimit();
        initSellLimit();
        initBuyMoreMenu();
    }

    private void initDisplayItem() {
        if (config.getConfigurationSection("display-item") == null) {
            if (config.getConfigurationSection("products") == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get display-item section in your shop config!!");
                displayItem = null;
                return;
            } else {
                String tempVal1 = null;
                for (String s : config.getConfigurationSection("products").getKeys(false)) {
                    tempVal1 = s;
                    if (tempVal1 != null) {
                        break;
                    }
                }
                displayItem = new ObjectDisplayItem(config.getConfigurationSection("products." +
                        tempVal1), this);
            }
        }
        displayItem = new ObjectDisplayItem(config.getConfigurationSection("display-item"), this);
    }

    private void initReward() {
        if (config.getConfigurationSection("products") == null) {
            buyPrice = new ObjectPrices();
            return;
        }
        reward = new ObjectProducts(config.getConfigurationSection("products"), config.getString("product-mode", "ANY"));
    }

    private void initBuyPrice() {
        if (config.getConfigurationSection("buy-prices") == null) {
            if (config.getConfigurationSection("prices") == null) {
                buyPrice = new ObjectPrices();
                return;
            }
            else {
                buyPrice = new ObjectPrices(config.getConfigurationSection("prices"), config.getString("price-mode", "ANY"));
                return;
            }
        }
        buyPrice = new ObjectPrices(config.getConfigurationSection("buy-prices"), config.getString("price-mode", "ANY"));
    }

    private void initSellPrice() {
        if (config.getConfigurationSection("sell-prices") == null) {
            if (buyPrice.getMode() == ThingMode.UNKNOWN) {
                sellPrice = buyPrice;
                return;
            }
            else {
                sellPrice = buyPrice;
            }
        }
        sellPrice = new ObjectPrices(config.getConfigurationSection("sell-prices"), config.getString("price-mode", "ANY"));
    }

    private void initBuyAction() {
        if (config.getStringList("buy-actions").isEmpty()) {
            buyAction = new ObjectAction();
            return;
        }
        buyAction = new ObjectAction(config.getStringList("buy-actions"));
    }

    private void initSellAction() {
        if (config.getStringList("sell-actions").isEmpty()) {
            sellAction = new ObjectAction();
            return;
        }
        sellAction = new ObjectAction(config.getStringList("sell-actions"));
    }

    private void initBuyLimit() {
        if (config.getConfigurationSection("limits") == null) {
            if (config.getConfigurationSection("buy-limits") == null) {
                buyLimit = new ObjectLimit();
                return;
            }
            else {
                buyLimit = new ObjectLimit(config.getConfigurationSection("buy-limits"),
                        config.getConfigurationSection("buy-limits-conditions"));
                return;
            }
        }
        buyLimit = new ObjectLimit(config.getConfigurationSection("limits"),
                config.getConfigurationSection("limits-conditions"));
    }

    private void initSellLimit() {
        if (config.getConfigurationSection("limits") == null) {
            if (config.getConfigurationSection("sell-limits") == null) {
                sellLimit = buyLimit;
                return;
            }
            else {
                buyLimit = new ObjectLimit(config.getConfigurationSection("sell-limits"),
                        config.getConfigurationSection("sell-limits-conditions"));
                return;
            }
        }
        sellLimit = buyLimit;
    }

    private void initBuyMoreMenu() {
        new ObjectMoreMenu(ConfigManager.configManager.getString("menu.select-more.menu"),
                this);
    }

    public String getDisplayName() {
        return displayItem.getDisplayItem().getItemMeta().hasDisplayName() ?
                displayItem.getDisplayItem().getItemMeta().getDisplayName() :
                displayItem.getDisplayItem().getType().name();
    }

    public ObjectPrices getBuyPrice() {
        if (buyPrice == null) {
            return new ObjectPrices();
        }
        else {
            return buyPrice;
        }
    }

    public ObjectPrices getSellPrice() {
        if (sellPrice == null) {
            return new ObjectPrices();
        }
        else {
            return sellPrice;
        }
    }

    public ObjectProducts getReward() {
        if (reward == null) {
            return new ObjectProducts();
        }
        else {
            return reward;
        }
    }

    public ObjectAction getBuyAction() {
        return buyAction;
    }

    public ObjectAction getSellAction() {
        return sellAction;
    }


    public int getPlayerBuyLimit(Player player) {
        ObjectLimit tempVal1 = buyLimit;
        if (buyLimit == null) {
            return -1;
        }
        return tempVal1.getPlayerLimits(player);
    }

    public int getPlayerSellLimit(Player player) {
        ObjectLimit tempVal1 = sellLimit;
        if (sellLimit == null) {
            return -1;
        }
        return tempVal1.getPlayerLimits(player);
    }

    public int getServerBuyLimit(Player player) {
        ObjectLimit tempVal1 = buyLimit;
        if (buyLimit == null) {
            return -1;
        }
        return tempVal1.getServerLimits(player);
    }

    public int getServerSellLimit(Player player) {
        ObjectLimit tempVal1 = sellLimit;
        if (sellLimit == null) {
            return -1;
        }
        return tempVal1.getServerLimits(player);
    }

    public ConfigurationSection getItemConfig() {
        return config;
    }

    public String getProduct() {
        return config.getName();
    }

    public String getShop() {
        return shop;
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        Bukkit.getConsoleSender().sendMessage(type.name() + ConfigManager.configManager.getClickAction(type));
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        switch (ConfigManager.configManager.getClickAction(type)){
            case "buy" :
                BuyProductMethod.startBuy(getShop(), getProduct(), player, !b);
                break;
            case "sell" :
                SellProductMethod.startSell(getShop(), getProduct(), player, !b);
                break;
            case "select-amount" :
                OpenGUI.openMoreGUI(player, this);
                break;
            default:
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cUnknown click action: "
                + ConfigManager.configManager.getClickAction(type));
                break;
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        return displayItem.getDisplayItem(player);
    }
}
