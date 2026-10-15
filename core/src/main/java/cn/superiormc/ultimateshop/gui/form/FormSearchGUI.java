package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchResultButton;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FormSearchGUI extends FormGUI {

    private final ObjectSearchMenu menu;

    private final boolean bypass;

    private String keywords = "";

    private boolean enteringKeywords;

    private boolean finished;

    public FormSearchGUI(Player player, ObjectSearchMenu menu, boolean bypass) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
    }

    @Override
    public void constructGUI() {
        form = null;
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        if (enteringKeywords) {
            constructInput();
        } else {
            constructResults();
        }
    }

    @Override
    public void openGUI(boolean reopen) {
        finished = false;
        constructGUI();
        super.openGUI(reopen);
    }

    @Override
    public void updateGUI() {
        super.updateGUI();
        if (form == null && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
            finishGUI();
        }
    }

    @Override
    public void closeGUI() {
        finishGUI();
    }

    @Override
    public void finishGUI() {
        if (finished) {
            return;
        }
        finished = true;
        super.finishGUI();
    }

    private String text(String path, String fallback) {
        return TextUtil.parse(player, CommonUtil.parseLang(player,
                ConfigManager.configManager.getMenuRendererText(
                    getMenu() == null ? null : getMenu().getConfig(),
                    player, "form", "search." + path, fallback)));
    }

    private String title() {
        return TextUtil.parse(player, CommonUtil.parseLang(player, menu.getString("title", "Search")));
    }

    private void constructInput() {
        CustomForm.Builder builder = CustomForm.builder().title(title());
        builder.input(text("input", "Search"), "", keywords);
        builder.validResultHandler((submitted, response) -> handleResponse(submitted, () -> {
            String input = response.next();
            keywords = input == null ? "" : input;
            showResults();
        }));
        builder.closedOrInvalidResultHandler((submitted, response) -> handleResponse(submitted, this::showResults));
        form = builder.build();
    }

    private void constructResults() {
        SimpleForm.Builder builder = SimpleForm.builder().title(title());
        builder.content(TextUtil.parse(player, CommonUtil.parseLang(player, menu.getString("content", ""))));
        List<Runnable> actions = new ArrayList<>();
        builder.button(ButtonComponent.of(text("buttons.search", "Search")));
        actions.add(() -> {
            enteringKeywords = true;
            updateGUI();
        });
        List<ObjectItem> matched = keywords.trim().isEmpty()
                ? Collections.emptyList() : ShopHelper.getTargetItems(keywords, player);
        if (matched == null) {
            matched = Collections.emptyList();
        }
        int limit = Math.min(matched.size(), menu.getResultSlots().size());
        for (int i = 0; i < limit; i++) {
            ObjectItem item = matched.get(i);
            ObjectSearchResultButton result = new ObjectSearchResultButton(item, menu.getResultLore());
            ConfigurationSection displaySection = item.getDisplayItem(player, 1).getSection();
            ObjectDisplayItemStack display = new ObjectDisplayItemStack(player,
                    result.getDisplayItem(player, 1).getItemStack(),
                    displaySection == null ? item.getButtonConfig() : displaySection, item);
            addButton(builder, actions, display,
                    () -> {
                        finishGUI();
                        new FormInfoGUI(player, item, null, this::reopenResults).openGUI(true);
                    });
        }
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player, "form")).entrySet()) {
            AbstractButton button = entry.getValue();
            addButton(builder, actions, button.getDisplayItem(player, 1),
                    () -> {
                        finishGUI();
                        button.clickEvent(ClickType.LEFT, player);
                    });
        }
        builder.validResultHandler((submitted, response) -> handleResponse(submitted, () -> {
            int index = response.clickedButtonId();
            if (index >= 0 && index < actions.size()) {
                actions.get(index).run();
            }
        }));
        builder.closedOrInvalidResultHandler((submitted, response) -> handleResponse(submitted, this::finishGUI));
        form = builder.build();
    }

    private void addButton(SimpleForm.Builder builder, List<Runnable> actions,
                           ObjectDisplayItemStack display, Runnable action) {
        ButtonComponent component = display.parseToBedrockButton(getMenu());
        if (component != null) {
            builder.button(component);
            actions.add(action);
        }
    }

    private void showResults() {
        enteringKeywords = false;
        updateGUI();
    }

    private void reopenResults() {
        enteringKeywords = false;
        openGUI(true);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
