package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormInfoGUI;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
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

    private ObjectAction failAction;

    private ObjectCondition buyCondition;

    private ObjectCondition sellCondition;

    private ObjectLimit buyLimit;

    private ObjectLimit sellLimit;

    private ObjectMoreMenu buyMoreMenu;

    private boolean buyMore;

    private final ObjectItemConfig itemConfig;

    public final boolean empty;

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
        initFailAction();
        initBuyCondition();
        initSellCondition();
        initBuyLimit();
        initSellLimit();
        initBuyMore();
        if (getBuyMore()) {
            initBuyMoreMenu();
        }
        initDisplayItem();
        this.empty = reward.empty && buyPrice.empty && sellPrice.empty;
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
        buyAction = new ObjectAction(itemConfig.getActionOrConditionSection("buy-actions"), this);
    }

    private void initSellAction() {
        sellAction = new ObjectAction(itemConfig.getActionOrConditionSection("sell-actions"), this);
    }

    private void initFailAction() {
        failAction = new ObjectAction(itemConfig.getActionOrConditionSection("fail-actions"), this);
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

    private void initBuyMore() {
        buyMore = itemConfig.getBoolean("buy-more",
                shop.getShopConfig().getBoolean("settings.buy-more", true));
    }

    private void initBuyMoreMenu() {
        ConfigurationSection buyMoreSection = itemConfig.getConfigurationSection("buy-more-menu");
        if (buyMoreSection == null) {
            if (buyPrice.empty) {
                if (!sellPrice.empty) {
                    buyMoreMenu = new ObjectMoreMenu(ConfigManager.configManager.getSectionOrDefault(
                            "menu.buy-more", "menu.buy-more-menu.only-sell"), this);
                } else {
                    buyMore = false;
                }
            } else if (sellPrice.empty) {
                buyMoreMenu = new ObjectMoreMenu(ConfigManager.configManager.getSectionOrDefault(
                        "menu.buy-more", "menu.buy-more-menu.only-buy"), this);
            } else {
                buyMoreMenu = new ObjectMoreMenu(ConfigManager.configManager.getSectionOrDefault(
                        "menu.buy-more", "menu.buy-more-menu.default"), this);
            }
        } else {
            buyMoreMenu = new ObjectMoreMenu(buyMoreSection, this);
        }
    }

    private void initBuyCondition() {
        ConfigurationSection section = itemConfig.getActionOrConditionSection("conditions");
        if (section == null) {
            section = itemConfig.getActionOrConditionSection("buy-conditions");
        }
        buyCondition = new ObjectCondition(section, this);
    }

    private void initSellCondition() {
        ConfigurationSection section = itemConfig.getActionOrConditionSection("conditions");
        if (section == null) {
            section = itemConfig.getActionOrConditionSection("sell-conditions");
        }
        sellCondition = new ObjectCondition(section, this);
    }

    public String getDisplayName(Player player) {
        if (itemConfig.getString("display-name") == null) {
            return ItemUtil.getItemName(displayItem.getDisplayItem(player).getItemStack());
        }
        return TextUtil.parse(player, itemConfig.getString("display-name"));
    }

    public ObjectPrices getBuyPrice() {
        if (buyPrice == null) {
            return new ObjectPrices();
        }
        return buyPrice;
    }

    public ObjectPrices getSellPrice() {
        if (sellPrice == null) {
            return new ObjectPrices();
        }
        return sellPrice;
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
        if (empty) {
            return;
        }
        if (UltimateShop.useGeyser && CommonUtil.isBedrockPlayer(player)) {
            FormInfoGUI infoGUI = new FormInfoGUI(player, this);
            infoGUI.openGUI(true);
            return;
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        String tempVal1 = ConfigManager.configManager.getClickAction(type);
        switch (tempVal1){
            case "buy" :
                if (!buyPrice.empty &&
                        BuyProductMethod.startBuy(getShop(), getProduct(), player, !b).getStatus() != ProductTradeStatus.Status.DONE) {
                    failAction.runAllActions(new ObjectThingRun(player, type));
                }
                return;
            case "sell" :
                if (!sellPrice.empty &&
                        SellProductMethod.startSell(getShop(), getProduct(), player, !b).getStatus() != ProductTradeStatus.Status.DONE) {
                    failAction.runAllActions(new ObjectThingRun(player, type));
                }
                return;
            case "buy-or-sell" :
                if (buyPrice.empty && !sellPrice.empty) {
                    if (SellProductMethod.startSell(getShop(), getProduct(), player, !b).getStatus() != ProductTradeStatus.Status.DONE) {
                        failAction.runAllActions(new ObjectThingRun(player, type));
                    }
                }
                else if (!buyPrice.empty && BuyProductMethod.startBuy(getShop(), getProduct(), player, !b).getStatus() != ProductTradeStatus.Status.DONE) {
                    failAction.runAllActions(new ObjectThingRun(player, type));
                }
                return;
            case "sell-all" :
                if (!sellPrice.empty && SellProductMethod.startSell(getShop(),
                        getProduct(),
                        player,
                        !b,
                        false,
                        true,
                        1).getStatus() != ProductTradeStatus.Status.DONE) {
                    failAction.runAllActions(new ObjectThingRun(player, type));
                }
                return;
            case "select-amount" :
                if (getBuyMore()) {
                    BuyMoreGUI.openGUI(player, this);
                }
                return;
        }
        if (!UltimateShop.freeVersion) {
            ObjectAction action = new ObjectAction(
                    ConfigManager.configManager.getSection("menu.click-event-actions." + tempVal1),
                    this);
            action.runAllActions(new ObjectThingRun(player, type));
            if (action.getLastTradeStatus() != null &&
                    action.getLastTradeStatus().getStatus() != ProductTradeStatus.Status.DONE) {
                failAction.runAllActions(new ObjectThingRun(player, type));
            }
        }
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        if (displayItem == null) {
            return ObjectDisplayItemStack.getAir();
        }
        return displayItem.getDisplayItem(player, multi);
    }

    public ItemStack getDisplayItem(Player player) {
        if (displayItem == null) {
            return new ItemStack(Material.AIR);
        }
        return displayItem.getDisplayItem(player).getItemStack();
    }

    public ObjectDisplayItem getDisplayItemObject() {
        return displayItem;
    }

    public boolean getBuyCondition(Player player) {
        if (buyCondition == null) {
            return true;
        }
        return buyCondition.getAllBoolean(new ObjectThingRun(player));
    }

    public boolean getSellCondition(Player player) {
        if (sellCondition == null) {
            return true;
        }
        return sellCondition.getAllBoolean(new ObjectThingRun(player));
    }

    public boolean getBuyMore() {
        return buyMore;
    }

    public ObjectMoreMenu getBuyMoreMenu() {
        return buyMoreMenu;
    }

    public List<String> getAddLore() {
        List<String> resultString = itemConfig.getStringList("add-lore");
        if (resultString.isEmpty()) {
            return ConfigManager.configManager.getStringList("display-item.add-lore");
        } else {
            return resultString;
        }
    }

    public String getBuyTimesResetMode() {
        if (itemConfig.getString("buy-limits-reset-mode") != null) {
            return itemConfig.getString("buy-limits-reset-mode");
        } else if (itemConfig.getString("buy-times-reset-mode") != null) {
            return itemConfig.getString("buy-times-reset-mode");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-mode", "NEVER");
    }

    public String getBuyTimesResetTime() {
        if (itemConfig.getString("buy-limits-reset-time") != null) {
            return itemConfig.getString("buy-limits-reset-time");
        } else if (itemConfig.getString("buy-times-reset-time") != null) {
            return itemConfig.getString("buy-times-reset-time");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-time", "00:00:00");
    }

    public String getBuyTimesResetFormat() {
        if (itemConfig.getString("buy-times-reset-time-format") != null) {
            return itemConfig.getString("buy-times-reset-time-format");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-time-format", "yyyy-MM-dd HH:mm:ss");
    }

    public String getSellTimesResetMode() {
        if (itemConfig.getString("sell-limits-reset-mode") != null) {
            return itemConfig.getString("sell-limits-reset-mode");
        } else if (itemConfig.getString("sell-times-reset-mode") != null) {
            return itemConfig.getString("sell-times-reset-mode");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-mode", "NEVER");
    }

    public String getSellTimesResetTime() {
        if (itemConfig.getString("sell-limits-reset-time") != null) {
            return itemConfig.getString("sell-limits-reset-time");
        } else if (itemConfig.getString("sell-times-reset-time") != null) {
            return itemConfig.getString("sell-times-reset-time");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-time", "00:00:00");
    }

    public String getSellTimesResetFormat() {
        if (itemConfig.getString("sell-times-reset-time-format") != null) {
            return itemConfig.getString("sell-times-reset-time-format");
        }
        return ConfigManager.configManager.getString("use-times.default-reset-time-format", "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String toString() {
        return "Shop: " + shop.getShopName() + " Product: " + itemConfig.getSection().getName();
    }
}
