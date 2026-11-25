package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectCustomPlaceholder;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class ActionAddCustomPlaceholder extends AbstractRunAction {

    public ActionAddCustomPlaceholder() {
        super("add_custom_placeholder");
        setRequiredArgs("placeholder", "amount");
    }

    @Override
    protected void onDoAction(ObjectSingleAction action, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        if (player == null) {
            return;
        }
        String id = action.getString("placeholder");
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(id);
        if (placeholder == null || !placeholder.isNumber()) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Expected a numeric custom placeholder: " + id);
            return;
        }
        ObjectCache cache = placeholder.isPerPlayer()
                ? CacheManager.cacheManager.getObjectCache(player)
                : CacheManager.cacheManager.serverCache;
        if (cache == null) {
            ErrorManager.errorManager.sendErrorMessage("§cError: No cache available for custom placeholder: " + id);
            return;
        }
        try {
            BigDecimal amount = "{amount}".equals(action.getString("amount"))
                    ? BigDecimal.valueOf(thingRun.getAmount())
                    : new BigDecimal(action.getString("amount", player, thingRun.getAmount()));
            if (cache.addCustomPlaceholderValue(placeholder, amount) == null) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Could not update custom placeholder: " + id);
            }
        } catch (RuntimeException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Could not update custom placeholder " + id
                    + ": " + exception.getMessage());
        }
    }
}
