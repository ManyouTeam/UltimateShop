package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ObjectCopyItem extends AbstractButton {

    private final ObjectItem item;

    private final ConfigurationSection section;

    private ObjectDisplayItem displayItem;

    private final int defaultBuyAmount;

    private final int defaultSellAmount;

    public ObjectCopyItem(ConfigurationSection section, ObjectItem item) {
        super(section);
        this.type = ButtonType.SHOP;
        this.section = section;
        this.item = item;
        this.defaultBuyAmount = ObjectItem.parseDefaultTradeAmount(section.getString("default-buy-amount"),
                item.getDefaultBuyAmount(), section.getCurrentPath() + ".default-buy-amount");
        this.defaultSellAmount = ObjectItem.parseDefaultTradeAmount(section.getString("default-sell-amount"),
                item.getDefaultSellAmount(), section.getCurrentPath() + ".default-sell-amount");
        initDisplayItem();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded sub button for product " + item.getProduct() + " in shop " +
                item.getShop() + "!");
    }

    private void initDisplayItem() {
        if (section.contains("display-item")) {
            displayItem = new ObjectDisplayItem(section.getConfigurationSection("display-item"),
                    section.getConfigurationSection(ConfigManager.configManager.getString("conditions.display-item-key")),
                    item);
        } else {
            displayItem = item.getDisplayItemObject();
        }
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        if (displayItem == null) {
            return item.getDisplayItem(player, multi);
        }
        return displayItem.getDisplayItem(player, multi, defaultBuyAmount, defaultSellAmount);
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        item.clickEvent(type, player, defaultBuyAmount, defaultSellAmount, this);
    }

    public ObjectItem getTargetItem() {
        return item;
    }

    @Override
    public Boolean getShowSprite() {
        Boolean own = super.getShowSprite();
        return own == null ? item.getShowSprite() : own;
    }
}
