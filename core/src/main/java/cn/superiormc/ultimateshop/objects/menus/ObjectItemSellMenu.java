package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItemSellConfirmButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ObjectItemSellMenu extends ObjectMenu {

    private final List<Integer> inputSlots = new ArrayList<>();

    private final List<Integer> confirmSlots = new ArrayList<>();

    private ObjectItemSellConfirmButton confirmButton;

    public ObjectItemSellMenu(String fileName) {
        super(fileName, ITEM_SELL_MENU_FOLDER);
        this.type = MenuType.ItemSell;
        initItemSellStructure();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §f" + fileName + ".yml set as item sell type menu.");
    }

    public ObjectItemSellMenu(String fileName, ConfigurationSection menuConfig) {
        super(fileName, menuConfig);
        this.type = MenuType.ItemSell;
        initItemSellStructure();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §f" + fileName + ".yml set as item sell type menu.");
    }

    private void initItemSellStructure() {
        inputSlots.clear();
        confirmSlots.clear();
        if (menuConfigs == null) {
            return;
        }

        Set<String> inputIds = new LinkedHashSet<>();
        inputIds.add(menuConfigs.getString("input-item", "I"));
        inputIds.addAll(menuConfigs.getStringList("input-items"));
        String confirmId = menuConfigs.getString("confirm-item", "C");
        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (inputIds.contains(id)) {
                inputSlots.add(slot);
            }
            if (confirmId.equals(id)) {
                confirmSlots.add(slot);
            }
        });

        confirmButton = new ObjectItemSellConfirmButton(menuConfigs.getConfigurationSection("confirm-button"));
    }

    public List<Integer> getInputSlots() {
        return new ArrayList<>(inputSlots);
    }

    public List<Integer> getConfirmSlots() {
        return new ArrayList<>(confirmSlots);
    }

    public ObjectItemSellConfirmButton getConfirmButton() {
        return confirmButton;
    }

    public int getMaxSellAmount() {
        return getInt("max-sell-amount", -1);
    }

    @Override
    protected boolean isInventoryOnly() {
        return true;
    }
}
