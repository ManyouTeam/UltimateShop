package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormInfoGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.ObjectLimit;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.prices.PriceMode;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ObjectItem extends AbstractButton {

    private final ObjectShop shop;

    private ObjectDisplayItem displayItem;

    private ObjectPrices buyPrice;

    private ObjectPrices sellPrice;

    private ObjectProducts reward;

    private ObjectAction buyAction;

    private ObjectAction sellAction;

    private ObjectCondition buyCondition;

    private ObjectCondition sellCondition;

    private ObjectLimit buyLimit;

    private ObjectLimit sellLimit;

    public ObjectItem(ObjectShop shop, ConfigurationSection config) {
        super(config);
        this.shop = shop;
        this.type = ButtonType.SHOP;
        initReward();
        initBuyPrice();
        initSellPrice();
        initBuyAction();
        initSellAction();
        initBuyCondition();
        initSellCondition();
        initBuyLimit();
        initSellLimit();
        if (getBuyMore()) {
            initBuyMoreMenu();
        }
        initDisplayItem();
    }

    private void initDisplayItem() {
        displayItem = new ObjectDisplayItem(config.getConfigurationSection("display-item"),
                config.getConfigurationSection("display-item-conditions"),
                this);
    }

    private void initReward() {
        if (config.getConfigurationSection("products") == null) {
            reward = new ObjectProducts();
            return;
        }
        reward = new ObjectProducts(config.getConfigurationSection("products"),
                config.getString("product-mode", "ANY"),
                this);
    }

    private void initBuyPrice() {
        if (config.getConfigurationSection("buy-prices") == null) {
            if (config.getConfigurationSection("prices") == null) {
                buyPrice = new ObjectPrices();
            }
            else {
                buyPrice = new ObjectPrices(config.getConfigurationSection("prices"),
                        config.getString("price-mode", "ANY"),
                        this,
                        PriceMode.BUY);
            }
            return;
        }
        buyPrice = new ObjectPrices(config.getConfigurationSection("buy-prices"),
                config.getString("price-mode", "ANY"),
                this,
                PriceMode.BUY);
    }

    private void initSellPrice() {
        if (config.getConfigurationSection("sell-prices") == null) {
            if (config.getConfigurationSection("prices") == null) {
                sellPrice = new ObjectPrices();
            }
            else {
                sellPrice = new ObjectPrices(config.getConfigurationSection("prices"),
                        config.getString("price-mode", "ANY"),
                        this,
                        PriceMode.SELL);
            }
            return;
        }
        sellPrice = new ObjectPrices(config.getConfigurationSection("sell-prices"),
                config.getString("price-mode", "ANY"),
                this,
                PriceMode.SELL);
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
            }
            else {
                buyLimit = new ObjectLimit(config.getConfigurationSection("buy-limits"),
                        config.getConfigurationSection("buy-limits-conditions"),
                        this);
            }
            return;
        }
        buyLimit = new ObjectLimit(config.getConfigurationSection("limits"),
                config.getConfigurationSection("limits-conditions"),
                this);
    }

    private void initSellLimit() {
        if (config.getConfigurationSection("limits") == null) {
            if (config.getConfigurationSection("sell-limits") == null) {
                sellLimit = new ObjectLimit();
            }
            else {
                sellLimit = new ObjectLimit(config.getConfigurationSection("sell-limits"),
                        config.getConfigurationSection("sell-limits-conditions"),
                        this);
            }
            return;
        }
        sellLimit = buyLimit;
    }

    private void initBuyMoreMenu() {
        ConfigurationSection buyMoreSection = config.getConfigurationSection("buy-more-menu");
        if (buyMoreSection == null) {
            new ObjectMoreMenu(ConfigManager.configManager.getSectionOrDefault(
                    "menu.select-more", "menu.buy-more"), this);
        } else {
            new ObjectMoreMenu(buyMoreSection, this);
        }
    }

    private void initBuyCondition() {
        List<String> section = config.getStringList("conditions");
        if (section.isEmpty()) {
            section = config.getStringList("buy-conditions");
        }
        buyCondition = new ObjectCondition(section);
    }

    private void initSellCondition() {
        List<String> section = config.getStringList("conditions");
        if (section.isEmpty()) {
            section = config.getStringList("sell-conditions");
        }
        sellCondition = new ObjectCondition(section);
    }

    public String getDisplayName(Player player) {
        if (config.getString("display-name") == null) {
            return ItemUtil.getItemName(displayItem.getDisplayItem(player));
        }
        return TextUtil.parse(config.getString("display-name"));
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
        return shop.getShopName();
    }

    public ObjectShop getShopObject() {
        return shop;
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (UltimateShop.useGeyser && CommonUtil.isBedrockPlayer(player)) {
            FormInfoGUI infoGUI = new FormInfoGUI(player, this);
            infoGUI.openGUI(true);
            return;
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        switch (ConfigManager.configManager.getClickAction(type)){
            case "buy" :
                if (!buyPrice.empty) {
                    BuyProductMethod.startBuy(getShop(), getProduct(), player, !b);
                }
                break;
            case "sell" :
                if (!sellPrice.empty) {
                    SellProductMethod.startSell(getShop(), getProduct(), player, !b);
                }
                break;
            case "buy-or-sell" :
                if (buyPrice.empty && !sellPrice.empty) {
                    SellProductMethod.startSell(getShop(), getProduct(), player, !b);
                }
                else if (!buyPrice.empty) {
                    BuyProductMethod.startBuy(getShop(), getProduct(), player, !b);
                }
                break;
            case "sell-all" :
                if (!sellPrice.empty) {
                    SellProductMethod.startSell(getShop(),
                            getProduct(),
                            player,
                            !b,
                            false,
                            true,
                            1);
                }
                break;
            case "select-amount" :
                if (getBuyMore()) {
                    OpenGUI.openMoreGUI(player, this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player, int multi) {
        if (displayItem == null) {
            return new ItemStack(Material.STONE);
        }
        return displayItem.getDisplayItem(player, multi);
    }

    public ItemStack getDisplayItem(Player player) {
        if (displayItem == null) {
            return new ItemStack(Material.STONE);
        }
        return displayItem.getDisplayItem(player);
    }

    public boolean getBuyCondition(Player player) {
        if (buyCondition == null) {
            return true;
        }
        return buyCondition.getBoolean(player);
    }

    public boolean getSellCondition(Player player) {
        if (sellCondition == null) {
            return true;
        }
        return sellCondition.getBoolean(player);
    }

    public boolean getBuyMore() {
        return config.getBoolean("buy-more",
                shop.getShopConfig().getBoolean("settings.buy-more", true));
    }

    public List<String> getAddLore(Player player) {
        List<String> resultString = config.getStringList("add-lore");
        if (resultString.isEmpty()) {
            return ConfigManager.configManager.getStringListWithPAPI(player, "display-item.add-lore");
        } else {
            return resultString;
        }
    }

    @Override
    public String toString() {
        return "Shop: " + shop.getShopName() + " Product: " + config.getName();
    }
}
