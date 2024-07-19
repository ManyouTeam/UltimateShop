package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormInfoGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectItemConfig;
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

    private final ObjectItemConfig itemConfig;

    public ObjectItem(ObjectShop shop, ConfigurationSection originalConfig) {
        super(originalConfig);
        this.shop = shop;
        this.type = ButtonType.SHOP;
        this.itemConfig = new ObjectItemConfig(this, originalConfig);
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
        displayItem = new ObjectDisplayItem(itemConfig.getConfigurationSection("display-item"),
                itemConfig.getConfigurationSection("display-item-conditions"),
                this);
    }

    private void initReward() {
        if (itemConfig.getConfigurationSection("products") == null) {
            reward = new ObjectProducts();
            return;
        }
        reward = new ObjectProducts(itemConfig.getConfigurationSection("products"),
                itemConfig.getString("product-mode", "ANY"),
                this);
    }

    private void initBuyPrice() {
        if (itemConfig.getConfigurationSection("buy-prices") == null) {
            if (itemConfig.getConfigurationSection("prices") == null) {
                buyPrice = new ObjectPrices();
            }
            else {
                buyPrice = new ObjectPrices(itemConfig.getConfigurationSection("prices"),
                        itemConfig.getString("price-mode", "ANY"),
                        this,
                        PriceMode.BUY);
            }
            return;
        }
        buyPrice = new ObjectPrices(itemConfig.getConfigurationSection("buy-prices"),
                itemConfig.getString("price-mode", "ANY"),
                this,
                PriceMode.BUY);
    }

    private void initSellPrice() {
        if (itemConfig.getConfigurationSection("sell-prices") == null) {
            if (itemConfig.getConfigurationSection("prices") == null) {
                sellPrice = new ObjectPrices();
            }
            else {
                sellPrice = new ObjectPrices(itemConfig.getConfigurationSection("prices"),
                        itemConfig.getString("price-mode", "ANY"),
                        this,
                        PriceMode.SELL);
            }
            return;
        }
        sellPrice = new ObjectPrices(itemConfig.getConfigurationSection("sell-prices"),
                itemConfig.getString("price-mode", "ANY"),
                this,
                PriceMode.SELL);
    }

    private void initBuyAction() {
        if (itemConfig.getStringList("buy-actions").isEmpty()) {
            buyAction = new ObjectAction();
            return;
        }
        buyAction = new ObjectAction(itemConfig.getStringList("buy-actions"));
    }

    private void initSellAction() {
        if (itemConfig.getStringList("sell-actions").isEmpty()) {
            sellAction = new ObjectAction();
            return;
        }
        sellAction = new ObjectAction(itemConfig.getStringList("sell-actions"));
    }

    private void initBuyLimit() {
        if (itemConfig.getConfigurationSection("limits") == null) {
            if (itemConfig.getConfigurationSection("buy-limits") == null) {
                buyLimit = new ObjectLimit();
            }
            else {
                buyLimit = new ObjectLimit(itemConfig.getConfigurationSection("buy-limits"),
                        itemConfig.getConfigurationSection("buy-limits-conditions"),
                        this);
            }
            return;
        }
        buyLimit = new ObjectLimit(itemConfig.getConfigurationSection("limits"),
                itemConfig.getConfigurationSection("limits-conditions"),
                this);
    }

    private void initSellLimit() {
        if (itemConfig.getConfigurationSection("limits") == null) {
            if (itemConfig.getConfigurationSection("sell-limits") == null) {
                sellLimit = new ObjectLimit();
            }
            else {
                sellLimit = new ObjectLimit(itemConfig.getConfigurationSection("sell-limits"),
                        itemConfig.getConfigurationSection("sell-limits-conditions"),
                        this);
            }
            return;
        }
        sellLimit = buyLimit;
    }

    private void initBuyMoreMenu() {
        ConfigurationSection buyMoreSection = itemConfig.getConfigurationSection("buy-more-menu");
        if (buyMoreSection == null) {
            new ObjectMoreMenu(ConfigManager.configManager.getSectionOrDefault(
                    "menu.select-more", "menu.buy-more"), this);
        } else {
            new ObjectMoreMenu(buyMoreSection, this);
        }
    }

    private void initBuyCondition() {
        List<String> section = itemConfig.getStringList("conditions");
        if (section.isEmpty()) {
            section = itemConfig.getStringList("buy-conditions");
        }
        buyCondition = new ObjectCondition(section);
    }

    private void initSellCondition() {
        List<String> section = itemConfig.getStringList("conditions");
        if (section.isEmpty()) {
            section = itemConfig.getStringList("sell-conditions");
        }
        sellCondition = new ObjectCondition(section);
    }

    public String getDisplayName(Player player) {
        if (itemConfig.getString("display-name") == null) {
            return ItemUtil.getItemName(displayItem.getDisplayItem(player));
        }
        return TextUtil.parse(itemConfig.getString("display-name"));
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
        return itemConfig.getSection();
    }

    public String getProduct() {
        return itemConfig.getSection().getName();
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
        return itemConfig.getBoolean("buy-more",
                shop.getShopConfig().getBoolean("settings.buy-more", true));
    }

    public List<String> getAddLore(Player player) {
        List<String> resultString = itemConfig.getStringListWithPAPI(player, "add-lore");
        if (resultString.isEmpty()) {
            return ConfigManager.configManager.getStringListWithPAPI(player, "display-item.add-lore");
        } else {
            return resultString;
        }
    }

    @Override
    public String toString() {
        return "Shop: " + shop.getShopName() + " Product: " + itemConfig.getSection().getName();
    }
}
