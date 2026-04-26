package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

public class ObjectMenu {

    public MenuType type;

    public static Map<String, ObjectMenu> commonMenus = new HashMap<>();

    public static Collection<String> notCommonMenuNames = new HashSet<>();

    public String fileName;

    private ObjectShop shop = null;

    private ObjectCondition condition;

    private ObjectAction openAction;

    private ObjectAction closeAction;

    public ConfigurationSection menuConfigs;

    protected final Map<MenuSender, Map<Integer, AbstractButton>> menuItems = new HashMap<>();

    protected final Map<String, AbstractButton> buttonItems = new HashMap<>();

    private boolean useGeyser;

    private boolean dynamicLayout;

    public ObjectMenu(String fileName, ObjectShop shop) {
        this.fileName = fileName;
        this.shop = shop;
        this.type = MenuType.Shop;
        initMenu();
        initButtons();
    }

    public ObjectMenu(ObjectShop shop) {
        this.fileName = shop.getShopName();
        this.shop = shop;
        this.type = MenuType.Shop;
        initMenu();
        initButtons();
    }

    public ObjectMenu(String fileName, ObjectItem item) {
        this.fileName = fileName;
        this.shop = item.getShopObject();
        this.type = MenuType.More;
        initMenu();
        initButtons();
    }

    public ObjectMenu(String fileName) {
        this.fileName = fileName;
        this.type = MenuType.Common;
        initMenu();
        initButtons();
        if (!UltimateShop.freeVersion) {
            initCustomCommand();
        }
    }

    public MenuType getType() {
        return type;
    }

    private void applyShopMenuOverrides() {
        if (shop == null) {
            return;
        }

        ConfigurationSection overrideSection = shop.getConfig().getConfigurationSection("settings.menu-settings");
        if (overrideSection == null) {
            return;
        }

        if (menuConfigs == null) {
            menuConfigs = new YamlConfiguration();
        }

        mergeSection(menuConfigs, overrideSection);
    }

    private void mergeSection(ConfigurationSection target, ConfigurationSection source) {
        for (String key : source.getKeys(false)) {
            Object value = source.get(key);
            if (value instanceof ConfigurationSection sourceSection) {
                ConfigurationSection targetSection = target.getConfigurationSection(key);
                if (targetSection == null) {
                    targetSection = target.createSection(key);
                }
                mergeSection(targetSection, sourceSection);
            } else {
                target.set(key, value);
            }
        }
    }

    public void initMenu() {
        if (type == MenuType.Common) {
            commonMenus.put(fileName, this);
        } else if (fileName != null && !fileName.isEmpty()) {
            notCommonMenuNames.add(fileName);
        }

        boolean hasMenuFile = fileName != null && !fileName.isEmpty();
        if (hasMenuFile) {
            File file = new File(UltimateShop.instance.getDataFolder() + "/menus/" + fileName + ".yml");
            if (!file.exists()){
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: We can not found your menu file: " +
                        fileName + ".yml!");
            } else {
                if (type == MenuType.Common) {
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded menu: " + fileName + ".yml!");
                }
                this.menuConfigs = YamlConfiguration.loadConfiguration(file);
            }
        }
        applyShopMenuOverrides();
        if (menuConfigs == null) {
            this.condition = new ObjectCondition();
            this.openAction = new ObjectAction();
            this.closeAction = new ObjectAction();
            this.useGeyser = true;
        } else if (shop != null) {
            this.condition = new ObjectCondition(menuConfigs.getConfigurationSection("conditions"));
            this.openAction = new ObjectAction(menuConfigs.getConfigurationSection("open-actions"), shop);
            this.closeAction = new ObjectAction(menuConfigs.getConfigurationSection("close-actions"));
            this.useGeyser = true;
        } else {
            this.condition = new ObjectCondition(menuConfigs.getConfigurationSection("conditions"), shop);
            this.openAction = new ObjectAction(menuConfigs.getConfigurationSection("open-actions"));
            this.closeAction = new ObjectAction(menuConfigs.getConfigurationSection("close-actions"));
            this.useGeyser = menuConfigs.getBoolean("bedrock.enabled", true);
        }
        this.dynamicLayout = menuConfigs.getBoolean("dynamic-layout", false) && !UltimateShop.freeVersion;
    }

    public void initShopItems(MenuSender menuSender) {
        if (menuConfigs == null) {
            return;
        }
        if (!dynamicLayout) {
            menuSender = MenuSender.empty;
        }

        MenuSender tempVal1 = menuSender;
        parseLayout(menuConfigs.getStringList("layout"), (slot, rawId) -> {
            String id = rawId;
            if (!tempVal1.isStatic()) {
                id = TextUtil.withPAPI(id, tempVal1.getPlayer());
            }

            AbstractButton button = getButtonByLayoutId(id, tempVal1, true);
            if (button != null) {
                getButtons(tempVal1).put(slot, button);
            }
        });
    }

    public void initButtons() {
        if (menuConfigs == null) {
            return;
        }

        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("buttons");
        if (tempVal1 == null) {
            return;
        }

        for (String button : tempVal1.getKeys(false)) {
            if (shop == null) {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button)));
            } else {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button), shop));
            }
        }

        if (!dynamicLayout) {
            if (type == MenuType.Shop) {
                initShopItems(MenuSender.empty);
            } else {
                initButtonItems(MenuSender.empty);
            }
        }
    }

    public void initButtonItems(MenuSender menuSender) {
        if (!dynamicLayout) {
            menuSender = MenuSender.empty;
        }

        MenuSender tempVal1 = menuSender;
        parseLayout(menuConfigs.getStringList("layout"), (slot, rawId) -> {
            String id = rawId;
            if (!tempVal1.isStatic()) {
                id = TextUtil.withPAPI(id, tempVal1.getPlayer());
            }

            AbstractButton buttonObj = getButtonByLayoutId(id, tempVal1, false);
            if (buttonObj != null) {
                getButtons(tempVal1).putIfAbsent(slot, buttonObj);
            }
        });
    }



    private AbstractButton getButtonByLayoutId(String id, MenuSender menuSender, boolean includeShopItems) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        if (!id.contains("||")) {
            return getSingleButtonById(id, menuSender, includeShopItems);
        }

        String[] candidates = id.split("\\|\\|");
        for (String candidate : candidates) {
            AbstractButton button = getSingleButtonById(candidate.trim(), menuSender, includeShopItems);
            if (button != null) {
                return button;
            }
        }
        return null;
    }

    private AbstractButton getSingleButtonById(String id, MenuSender menuSender, boolean includeShopItems) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        if (includeShopItems && shop != null) {
            if (!UltimateShop.freeVersion) {
                AbstractButton copyItem = shop.getCopyItem(id);
                if (copyItem != null && copyItem.canDisplay(menuSender)) {
                    return copyItem;
                }
            }

            AbstractButton button = shop.getButton(id);
            if (button != null && button.canDisplay(menuSender)) {
                return button;
            }

            AbstractButton product = shop.getProduct(id);
            if (product != null && product.canDisplay(menuSender)) {
                return product;
            }
        }

        AbstractButton buttonObj = buttonItems.get(id);
        if (buttonObj != null && buttonObj.canDisplay(menuSender)) {
            return buttonObj;
        }
        return null;
    }

    private void initCustomCommand() {
        String commandName = menuConfigs.getString("custom-command.name");
        if (commandName != null && !commandName.isEmpty()) {
            ObjectMenu menu = this;
            BukkitCommand command = new BukkitCommand(commandName) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    if (!(sender instanceof Player)) {
                        LanguageManager.languageManager.sendStringText("error.in-game");
                        return true;
                    }
                    CommonGUI.openGUI((Player) sender, fileName, false, false);
                    return true;
                }
            };
            command.setDescription(menu.getString("custom-command.description", "UltimateShop Custom Command for " + commandName));
            CommandUtil.registerCustomCommand(command);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cRegistered custom command for menu: " + fileName + ".");
        }
    }

    public String getString(String path, String defaultValue) {
        if (defaultValue == null) {
            return menuConfigs.getString(path);
        }
        return menuConfigs.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return menuConfigs.getInt(path, defaultValue);
    }

    public Map<Integer, AbstractButton> getMenu(MenuSender menuSender) {
        if (dynamicLayout) {
            getButtons(menuSender).clear();
            if (type == MenuType.Shop) {
                initShopItems(menuSender);
            } else {
                initButtonItems(menuSender);
            }
        }
        Map<Integer, AbstractButton> tempVal1 = getButtons(menuSender);
        if (tempVal1 == null) {
            return new TreeMap<>(menuItems.get(MenuSender.empty));
        }
        return new TreeMap<>(tempVal1);
    }

    public ObjectCondition getCondition() {
        return condition;
    }

    public void doOpenAction(Player player, boolean reopen) {
        if (openAction != null) {
            openAction.runAllActions(new ObjectThingRun(player, reopen));
        }
    }

    public void doCloseAction(Player player) {
        if (closeAction != null) {
            closeAction.runAllActions(new ObjectThingRun(player));
        }
    }

    public String getName() {
        return fileName;
    }

    public ConfigurationSection getConfig() {
        return menuConfigs;
    }

    public boolean isUseGeyser() {
        return useGeyser;
    }

    public boolean isDynamicLayout() {
        return dynamicLayout;
    }

    protected Map<Integer, AbstractButton> getButtons() {
        return getButtons(MenuSender.empty);
    }

    protected Map<Integer, AbstractButton> getButtons(MenuSender menuSender) {
        if (!dynamicLayout) {
            menuSender = MenuSender.empty;
        }

        Map<Integer, AbstractButton> tempVal1 = menuItems.get(menuSender);
        if (tempVal1 == null) {
            menuItems.put(menuSender, new TreeMap<>());
            tempVal1 = menuItems.get(menuSender);
        }
        return tempVal1;
    }

    protected void parseLayout(List<String> layout, BiConsumer<Integer, String> itemHandler) {
        int slot = 0;
        for (String singleLine : layout) {
            int c = 0;
            while (c < singleLine.length()) {
                String id;
                if (singleLine.charAt(c) == '`') {
                    int end = singleLine.indexOf('`', c + 1);
                    if (end == -1) {
                        id = String.valueOf(singleLine.charAt(c));
                        c++;
                    } else {
                        id = singleLine.substring(c + 1, end);
                        c = end + 1;
                    }
                } else {
                    id = String.valueOf(singleLine.charAt(c));
                    c++;
                }

                itemHandler.accept(slot, id);
                slot++;
            }
        }
    }

}
