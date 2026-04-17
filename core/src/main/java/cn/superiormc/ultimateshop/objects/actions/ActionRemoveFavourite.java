package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

public class ActionRemoveFavourite extends AbstractRunAction {

    public ActionRemoveFavourite() {
        super("remove_favourite");
        setRequiredArgs("menu", "shop", "item");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return;
        }
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        String menuName = singleAction.getString("menu", player, amount);
        ObjectMenu menu = ObjectMenu.commonMenus.get(menuName);
        if (!(menu instanceof ObjectFavouriteMenu)) {
            LanguageManager.languageManager.sendStringText(player, "error.menu-not-found", "menu", menuName);
            return;
        }

        ObjectItem item = ShopHelper.getItemFromID(
                singleAction.getString("shop", player, amount),
                singleAction.getString("item", player, amount));
        if (item == null) {
            return;
        }

        if (CacheManager.cacheManager.getObjectCache(player).removeFavouriteProduct(menuName, item)) {
            LanguageManager.languageManager.sendStringText(player,
                    "favourite-removed",
                    "item", item.getDisplayName(player),
                    "menu", menuName);
        }
    }
}
