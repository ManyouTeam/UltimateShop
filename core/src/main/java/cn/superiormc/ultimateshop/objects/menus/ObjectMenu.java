package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.DynamicCommandManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;

public class ObjectMenu {

    public static final String COMMON_MENU_FOLDER = "menus/common_menus";

    public static final String SHOP_MENU_TEMPLATE_FOLDER = "menus/shop_menu_templates";

    public static final String BUY_MORE_MENU_FOLDER = "menus/buy_more_menus";

    public static final String FAVOURITE_MENU_FOLDER = "menus/favourite_menus";

    public static final String SEARCH_MENU_FOLDER = "menus/search_menus";

    public static final String ITEM_SELL_MENU_FOLDER = "menus/item_sell_menus";

    public MenuType type;

    public static Map<String, ObjectMenu> commonMenus = new HashMap<>();

    public static Collection<String> notCommonMenuNames = new HashSet<>();

    public String fileName;

    private final String menuFolder;

    private ObjectCondition condition;

    private ObjectAction openAction;

    private ObjectAction closeAction;

    public ConfigurationSection menuConfigs;

    protected final Map<Integer, AbstractButton> menuItems = new TreeMap<>();

    protected final Map<String, AbstractButton> buttonItems = new HashMap<>();

    private boolean useGeyser;

    private boolean useDialog;

    private boolean dynamicLayout;

    public ObjectMenu(String fileName) {
        this(fileName, COMMON_MENU_FOLDER);
    }

    public ObjectMenu(String fileName, ConfigurationSection menuConfigs) {
        this(fileName, null, MenuType.Common);
        this.menuConfigs = menuConfigs;
        initMenu();
        initButtons();
        if (!UltimateShop.freeVersion) {
            initCustomCommand();
        }
    }

    protected ObjectMenu(String fileName, String menuFolder, MenuType type) {
        this.fileName = fileName;
        this.menuFolder = menuFolder;
        this.type = type;
    }

    protected ObjectMenu(String fileName, String menuFolder) {
        this(fileName, menuFolder, MenuType.Common);
        initMenu();
        initButtons();
        if (!UltimateShop.freeVersion) {
            initCustomCommand();
        }
    }

    protected void applyMenuOverrides() {
    }

    public MenuType getType() {
        return type;
    }

    public void initMenu() {
        if (type == MenuType.Common) {
            commonMenus.put(fileName, this);
        } else if (fileName != null && !fileName.isEmpty()) {
            notCommonMenuNames.add(fileName);
        }

        if (menuFolder != null) {
            File file = findMenuFile(menuFolder);
            if (!file.exists()) {
                ErrorManager.errorManager.sendErrorMessage("§cError: We can not found your menu file: " +
                    menuFolder + "/" + fileName + ".yml!");
            } else {
                if (type == MenuType.Common) {
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded menu: " + fileName + ".yml!");
                }
                this.menuConfigs = YamlConfiguration.loadConfiguration(file);
            }
        }
        applyMenuOverrides();
        if (menuConfigs != null && supportsGraphicalRenderer("arcmenu")) {
            ConfigManager.configManager.applyMenuUI(menuConfigs, supportsGraphicalRenderer("ore"));
        }
        if (menuConfigs == null) {
            this.condition = new ObjectCondition();
            this.openAction = new ObjectAction();
            this.closeAction = new ObjectAction();
            this.useGeyser = supportsForms();
            this.useDialog = false;
            return;
        }
        this.condition = new ObjectCondition(menuConfigs.getConfigurationSection("conditions"));
        this.openAction = createOpenAction(menuConfigs.getConfigurationSection("open-actions"));
        this.closeAction = new ObjectAction(menuConfigs.getConfigurationSection("close-actions"));
        this.useGeyser = supportsForms() && !UltimateShop.freeVersion
            && ConfigManager.configManager.getMenuUIMode(menuConfigs, "bedrock").equals("form");
        this.useDialog = !isInventoryOnly() && !UltimateShop.freeVersion
            && UltimateShop.methodUtil.methodID().equals("paper")
            && CommonUtil.getMinorVersion(21, 9)
            && ConfigManager.configManager.isMenuDialogEnabled(menuConfigs);
        this.dynamicLayout = menuConfigs.getBoolean("dynamic-layout", false) && !UltimateShop.freeVersion;
    }

    protected ObjectAction createOpenAction(ConfigurationSection section) {
        return new ObjectAction(section);
    }

    private File findMenuFile(String folderName) {
        File folder = new File(UltimateShop.instance.getDataFolder(), folderName);
        File file = findMenuFile(folder);
        if (file.exists() || COMMON_MENU_FOLDER.equals(folderName)) {
            return file;
        }

        return findMenuFile(new File(UltimateShop.instance.getDataFolder(), COMMON_MENU_FOLDER));
    }

    private File findMenuFile(File folder) {
        File directFile = new File(folder, fileName + ".yml");
        if (directFile.exists()) {
            return directFile;
        }
        for (File file : CommonUtil.getYamlFiles(folder)) {
            if (file.getName().equals(fileName + ".yml")) {
                return file;
            }
        }
        return directFile;
    }

    public void initButtons() {
        if (menuConfigs == null) {
            return;
        }

        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("buttons");
        if (tempVal1 != null) {
            for (String button : tempVal1.getKeys(false)) {
                buttonItems.put(button, createButton(tempVal1.getConfigurationSection(button)));
            }
        }

        if (!dynamicLayout) {
            buildLayout(MenuSender.empty, menuItems);
        }
    }

    protected ObjectButton createButton(ConfigurationSection section) {
        return new ObjectButton(section);
    }

    private void buildLayout(MenuSender menuSender, Map<Integer, AbstractButton> target) {
        parseLayout(menuConfigs.getStringList("layout"), (slot, rawId) -> {
            String id = rawId;
            if (dynamicLayout && !menuSender.isStatic()) {
                id = TextUtil.withPAPI(id, menuSender.getPlayer());
            }

            AbstractButton buttonObj = getButtonByLayoutId(id, slot, menuSender);
            if (buttonObj != null) {
                target.put(slot, buttonObj);
            }
        });
    }

    private AbstractButton getButtonByLayoutId(String id, int slot, MenuSender menuSender) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        if (!id.contains("||")) {
            return getSingleButtonById(id, slot, menuSender);
        }

        String[] candidates = id.split("\\|\\|");
        for (String candidate : candidates) {
            AbstractButton button = getSingleButtonById(candidate.trim(), slot, menuSender);
            if (button != null) {
                return button;
            }
        }
        return null;
    }

    protected AbstractButton getSingleButtonById(String id, int slot, MenuSender menuSender) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        AbstractButton buttonObj = buttonItems.get(id);
        if (canDisplayLayoutButton(buttonObj, menuSender)) {
            return buttonObj;
        }
        return null;
    }

    protected boolean canDisplayLayoutButton(AbstractButton button, MenuSender sender) {
        return button != null && button.isVisibleInMenu(sender.getPresentation()) && button.canDisplay(sender);
    }

    private void initCustomCommand() {
        String commandName = menuConfigs.getString("custom-command.name");
        if (commandName != null && !commandName.isEmpty()) {
            BukkitCommand command = createCustomCommand(commandName, fileName);
            command.setDescription(getString("custom-command.description", "UltimateShop Custom Command for " + commandName));
            DynamicCommandManager.register(command);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cRegistered custom command for menu: " + fileName + ".");
        }
    }

    private static BukkitCommand createCustomCommand(String commandName, String menuId) {
        return new BukkitCommand(commandName) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player player)) {
                    LanguageManager.languageManager.sendStringText("error.in-game");
                    return true;
                }
                CommonGUI.openGUI(player, menuId, false, false);
                return true;
            }
        };
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
        MenuSender effectiveSender = menuSender == null ? MenuSender.empty : menuSender;
        boolean typeVisibility = effectiveSender.getPresentation() != null && hasMenuVisibilitySettings();
        if (!dynamicLayout && !typeVisibility) {
            return new TreeMap<>(menuItems);
        }
        Map<Integer, AbstractButton> result = new TreeMap<>();
        buildLayout(effectiveSender, result);
        return result;
    }

    public Map<Integer, AbstractButton> getUiMenu(MenuSender sender) {
        Map<Integer, AbstractButton> result = getMenu(sender);
        int key = 10000;
        List<AbstractButton> candidates = getUiCandidates();
        for (AbstractButton button : candidates) {
            int candidateKey = key++;
            if (!result.containsValue(button) && canDisplayUiCandidate(button, sender)) {
                result.put(candidateKey, button);
            }
        }
        return result;
    }

    public Map<Integer, List<AbstractButton>> getPossibleUiButtons(String presentation) {
        Map<Integer, List<AbstractButton>> result = getPossibleMenuButtons(presentation);
        Set<AbstractButton> present = new HashSet<>();
        result.values().forEach(present::addAll);
        int key = 10000;
        for (AbstractButton button : getUiCandidates()) {
            int candidateKey = key++;
            if (!present.contains(button) && button.isVisibleInMenu(presentation)) {
                result.put(candidateKey, List.of(button));
            }
        }
        return result;
    }

    protected boolean canDisplayUiCandidate(AbstractButton button, MenuSender sender) {
        return canDisplayLayoutButton(button, sender);
    }

    protected List<AbstractButton> getUiCandidates() {
        List<AbstractButton> result = new ArrayList<>();
        for (String id : new TreeSet<>(buttonItems.keySet())) {
            AbstractButton button = buttonItems.get(id);
            if (button.getButtonConfig() != null && button.getButtonConfig().contains("ui.placement")) {
                result.add(button);
            }
        }
        return result;
    }

    public Map<Integer, List<AbstractButton>> getPossibleMenuButtons(String presentation) {
        Map<Integer, List<AbstractButton>> result = new TreeMap<>();
        MenuSender sender = MenuSender.of(null, presentation);
        parseLayout(menuConfigs.getStringList("layout"), (slot, rawId) -> {
            Set<AbstractButton> candidates = new LinkedHashSet<>();
            if (dynamicLayout && rawId.contains("%")) {
                collectDynamicLayoutCandidates(candidates);
            } else {
                for (String id : rawId.split("\\|\\|")) {
                    AbstractButton button = getSingleButtonById(id.trim(), slot, sender);
                    if (button != null) {
                        candidates.add(button);
                        ConfigurationSection config = button.getButtonConfig();
                        if (config == null || !config.isConfigurationSection("display-conditions")
                            || config.getConfigurationSection("display-conditions").getKeys(false).isEmpty()) {
                            break;
                        }
                    }
                }
            }
            candidates.removeIf(button -> !button.isVisibleInMenu(presentation));
            if (!candidates.isEmpty()) {
                result.put(slot, new ArrayList<>(candidates));
            }
        });
        return result;
    }

    protected void collectDynamicLayoutCandidates(Set<AbstractButton> candidates) {
        candidates.addAll(buttonItems.values());
    }

    public Map<Integer, AbstractButton> getShopEntryButtons(MenuSender sender) {
        return getNavigationButtons(sender, false);
    }

    public Map<Integer, AbstractButton> getMenuNavigationButtons(MenuSender sender) {
        return getNavigationButtons(sender, true);
    }

    private Map<Integer, AbstractButton> getNavigationButtons(MenuSender sender, boolean includeCommonMenus) {
        Map<Integer, AbstractButton> result = getMenu(sender);
        String presentation = "arcmenu".equals(sender.getPresentation()) ? "arcmenu" : "dialog";
        result.entrySet().removeIf(entry -> {
            AbstractButton button = entry.getValue();
            return !(includeCommonMenus ? button.hasMenuNavigationAction() : button.hasShopMenuAction())
                || (includeCommonMenus && button.hasCloseAction()) || !button.canDisplay(sender)
                || !button.isVisibleInMenu(presentation);
        });
        return result;
    }

    protected boolean hasMenuVisibilitySettings() {
        return buttonItems.values().stream().anyMatch(button -> button.getButtonConfig() != null
            && button.getButtonConfig().contains("menu-visibility"));
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

    public boolean isUseDialog() {
        return useDialog;
    }

    public boolean isUseDialog(Player player) {
        return useDialog && (ConfigManager.configManager.getForcedMenuUIMode().isEmpty()
            || !CommonUtil.isBedrockPlayer(player));
    }

    protected boolean isInventoryOnly() {
        return false;
    }

    protected boolean supportsForms() {
        return !isInventoryOnly();
    }

    public boolean supportsGraphicalRenderer(String renderer) {
        return !isInventoryOnly() && ("arcmenu".equals(renderer) || "ore".equals(renderer));
    }

    public String getDialogLayout() {
        String layout = ConfigManager.configManager.getDialogLayout(menuConfigs);
        if ("ore".equalsIgnoreCase(layout) && !supportsGraphicalRenderer("ore")) {
            return "multi-action";
        }
        return layout;
    }

    public boolean isDynamicLayout() {
        return dynamicLayout;
    }

    protected Map<Integer, AbstractButton> getButtons() {
        return menuItems;
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
