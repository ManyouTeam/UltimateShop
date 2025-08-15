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
import org.bukkit.configuration.Configuration;
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

    public Configuration menuConfigs;

    public Map<Integer, AbstractButton> menuItems = new TreeMap<>();

    public Map<String, AbstractButton> buttonItems = new HashMap<>();

    private boolean useGeyser;

    private boolean dynamicLayout;

    public ObjectMenu(String fileName, ObjectShop shop) {
        this.fileName = fileName;
        this.shop = shop;
        this.type = MenuType.Shop;
        initMenu();
        if (!dynamicLayout) {
            initShopItems(MenuSender.empty);
            initButtonItems(MenuSender.empty);
        }
    }

    public ObjectMenu(String fileName, ObjectItem item) {
        this.fileName = fileName;
        this.shop = item.getShopObject();
        this.type = MenuType.More;
        initMenu();
        if (!dynamicLayout) {
            initButtonItems(MenuSender.empty);
        }
    }

    public ObjectMenu(String fileName) {
        this.fileName = fileName;
        this.type = MenuType.Common;
        initMenu();
        if (!dynamicLayout) {
            initButtonItems(MenuSender.empty);
        }
        if (!UltimateShop.freeVersion) {
            initCustomCommand();
        }
    }

    public MenuType getType() {
        return type;
    }

    public void initMenu() {
        if (type == MenuType.Common) {
            commonMenus.put(fileName, this);
        } else {
            notCommonMenuNames.add(fileName);
        }
        File file = new File(UltimateShop.instance.getDataFolder() + "/menus/" + fileName + ".yml");
        if (!file.exists()){
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: We can not found your menu file: " +
                    fileName + ".yml!");
        } else {
            if (type == MenuType.Common) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded menu: " + fileName + ".yml!");
            }
            this.menuConfigs = YamlConfiguration.loadConfiguration(file);
        }
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
        int slot = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            int c = 0;
            while (c < singleLine.length()) {
                String id;

                if (singleLine.charAt(c) == '`') {
                    // 找到下一个 `
                    int end = singleLine.indexOf('`', c + 1);
                    if (end == -1) {
                        // 没闭合，当作普通字符处理
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

                if (!menuSender.isStatic()) {
                    id = TextUtil.withPAPI(id, menuSender.getPlayer());
                }

                // 放入物品
                if (!UltimateShop.freeVersion && shop.getCopyItem(id) != null) {
                    menuItems.put(slot, shop.getCopyItem(id));
                } else if (shop.getButton(id) != null) {
                    menuItems.put(slot, shop.getButton(id));
                } else if (shop.getProduct(id) != null) {
                    menuItems.put(slot, shop.getProduct(id));
                }

                slot++;
            }
        }
    }

    public void initButtonItems(MenuSender menuSender) {
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

        int slot = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            int c = 0;
            while (c < singleLine.length()) {
                String id;

                // 读取多字符ID
                if (singleLine.charAt(c) == '`') {
                    int end = singleLine.indexOf('`', c + 1);
                    if (end == -1) {
                        // 没闭合，退化为单字符
                        id = String.valueOf(singleLine.charAt(c));
                        c++;
                    } else {
                        id = singleLine.substring(c + 1, end);
                        c = end + 1;
                    }
                } else {
                    // 单字符ID
                    id = String.valueOf(singleLine.charAt(c));
                    c++;
                }

                if (!menuSender.isStatic()) {
                    id = TextUtil.withPAPI(id, menuSender.getPlayer());
                }

                // 放入按钮（如果存在）
                AbstractButton buttonObj = buttonItems.get(id);
                if (buttonObj != null) {
                    menuItems.put(slot, buttonObj);
                }

                slot++;
            }
        }
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
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fRegistered custom command for menu: " + fileName + ".");
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
            if (type == MenuType.Shop) {
                initShopItems(menuSender);
            }
            initButtonItems(menuSender);
        }
        return menuItems;
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