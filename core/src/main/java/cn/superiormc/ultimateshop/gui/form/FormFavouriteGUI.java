package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEditModeButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEmptyButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormFavouriteGUI extends FormGUI {

    private final ObjectFavouriteMenu menu;

    private final boolean bypass;

    private final boolean editing;

    private final Map<ButtonComponent, ObjectFavouriteResultButton> resultActions = new LinkedHashMap<>();

    private final Map<ButtonComponent, Integer> normalActions = new LinkedHashMap<>();

    private ButtonComponent editModeComponent;

    public FormFavouriteGUI(Player owner, ObjectFavouriteMenu menu, boolean bypass) {
        this(owner, menu, bypass, false);
    }

    public FormFavouriteGUI(Player owner, ObjectFavouriteMenu menu, boolean bypass, boolean editing) {
        super(owner);
        this.menu = menu;
        this.bypass = bypass;
        this.editing = editing;
        constructGUI();
    }

    @Override
    public void constructGUI() {
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    menu.getName());
            return;
        }

        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        Map<FavouriteProductReference, ObjectItem> favouriteProducts = cache == null
                ? new LinkedHashMap<>()
                : cache.getResolvedFavouriteProducts(menu.getName());

        menuButtons = menu.getMenu(MenuSender.of(player));
        menuItems.clear();
        resultActions.clear();
        normalActions.clear();
        editModeComponent = null;

        SimpleForm.Builder builder = SimpleForm.builder();
        builder.title(TextUtil.parse(player, CommonUtil.parseLang(player, menu.getString("title", "Favourite"))));

        int displayed = 0;
        for (Map.Entry<FavouriteProductReference, ObjectItem> entry : favouriteProducts.entrySet()) {
            if (displayed >= menu.getResultSlots().size()) {
                break;
            }
            ObjectFavouriteResultButton resultButton = new ObjectFavouriteResultButton(entry.getValue(),
                    displayed,
                    editing ? menu.getEditingResultLore() : menu.getResultLore(),
                    editing);
            ButtonComponent component = addButton(builder, resultButton.getDisplayItem(player, 1));
            if (component != null) {
                resultActions.put(component, resultButton);
            }
            displayed++;
        }

        ObjectFavouriteEmptyButton emptyButton = menu.getEmptyButton();
        if (emptyButton != null) {
            for (int i = displayed; i < menu.getResultSlots().size(); i++) {
                addButton(builder, new ObjectDisplayItemStack(emptyButton.buildDisplayItem(player, favouriteProducts.size(), i + 1)));
            }
        }

        ObjectFavouriteEditModeButton editModeButton = menu.getEditModeButton();
        if (editModeButton != null) {
            editModeComponent = addButton(builder, editModeButton.getDisplayItem(player, editing, favouriteProducts.size()));
        }

        for (Map.Entry<Integer, AbstractButton> entry : menuButtons.entrySet()) {
            ButtonComponent component = addButton(builder, entry.getValue().getDisplayItem(player, 1));
            if (component != null) {
                normalActions.put(component, entry.getKey());
            }
        }

        if (menu.getString("bedrock.content", null) != null) {
            builder.content(TextUtil.parse(player, CommonUtil.parseLang(player, menu.getString("bedrock.content", ""))));
        }

        builder.validResultHandler(response -> {
            MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this);
            ButtonComponent clickedButton = response.clickedButton();
            if (clickedButton == null) {
                return;
            }
            if (editModeComponent != null && editModeComponent.equals(clickedButton)) {
                FormFavouriteGUI favouriteGUI = new FormFavouriteGUI(player, menu, true, !editing);
                favouriteGUI.openGUI(true);
                return;
            }
            ObjectFavouriteResultButton resultButton = resultActions.get(clickedButton);
            if (resultButton != null) {
                if (editing) {
                    openEditActionForm(resultButton);
                    return;
                }
                resultButton.getItem().clickEvent(org.bukkit.event.inventory.ClickType.LEFT, player);
                return;
            }
            Integer slot = normalActions.get(clickedButton);
            if (slot != null && menuButtons.get(slot) != null) {
                menuButtons.get(slot).clickEvent(org.bukkit.event.inventory.ClickType.LEFT, player);
            }
        });
        builder.closedOrInvalidResultHandler(response -> MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this));
        form = builder.build();
    }

    private ButtonComponent addButton(SimpleForm.Builder builder, ObjectDisplayItemStack displayItem) {
        if (displayItem == null) {
            return null;
        }
        ButtonComponent component = displayItem.parseToBedrockButton();
        if (component != null) {
            builder.button(component);
        }
        return component;
    }

    private void openEditActionForm(ObjectFavouriteResultButton resultButton) {
        SimpleForm.Builder builder = SimpleForm.builder();
        builder.title(TextUtil.parse(player, CommonUtil.parseLang(player,
                ConfigManager.configManager.getStringWithLang(player,
                        "menu.bedrock.favourite-edit.title",
                        "Edit Favourite: {item-name}",
                        "item-name", resultButton.getItem().getDisplayName(player)))));
        builder.content(TextUtil.parse(player, CommonUtil.parseLang(player,
                ConfigManager.configManager.getStringWithLang(player,
                        "menu.bedrock.favourite-edit.content",
                        "Choose how to edit this favourite entry.",
                        "item-name", resultButton.getItem().getDisplayName(player),
                        "index", String.valueOf(resultButton.getIndex() + 1)))));

        ButtonComponent moveForward = ButtonComponent.of(TextUtil.parse(player,
                ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.favourite-edit.buttons.forward", "Move Forward")));
        ButtonComponent moveBackward = ButtonComponent.of(TextUtil.parse(player,
                ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.favourite-edit.buttons.backward", "Move Backward")));
        ButtonComponent remove = ButtonComponent.of(TextUtil.parse(player,
                ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.favourite-edit.buttons.remove", "Remove")));
        ButtonComponent back = ButtonComponent.of(TextUtil.parse(player,
                ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.favourite-edit.buttons.back", "Back")));
        builder.button(moveForward);
        builder.button(moveBackward);
        builder.button(remove);
        builder.button(back);
        builder.validResultHandler(actionResponse -> {
            MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this);
            ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
            if (cache != null) {
                if (actionResponse.clickedButton().equals(moveForward)) {
                    cache.moveFavouriteProduct(menu.getName(), resultButton.getIndex(), resultButton.getIndex() - 1);
                } else if (actionResponse.clickedButton().equals(moveBackward)) {
                    cache.moveFavouriteProduct(menu.getName(), resultButton.getIndex(), resultButton.getIndex() + 1);
                } else if (actionResponse.clickedButton().equals(remove)) {
                    cache.removeFavouriteProduct(menu.getName(), resultButton.getItem());
                }
            }
            FormFavouriteGUI favouriteGUI = new FormFavouriteGUI(player, menu, true, true);
            favouriteGUI.openGUI(true);
        });
        builder.closedOrInvalidResultHandler(actionResponse -> {
            MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this);
            FormFavouriteGUI favouriteGUI = new FormFavouriteGUI(player, menu, true, true);
            favouriteGUI.openGUI(true);
        });
        form = builder.build();
        openGUI(true);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
