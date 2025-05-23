package cn.superiormc.ultimateshop.hooks.items;

import com.ssomar.executableitems.executableitems.manager.ExecutableItemsManager;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemExecutableItemsHook extends AbstractItemHook {

    public ItemExecutableItemsHook() {
        super("Oraxen");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        Optional<ExecutableItemInterface> itemInterface = ExecutableItemsManager.getInstance().getExecutableItem(hookItemID);
        return itemInterface.map(executableItemInterface -> executableItemInterface.buildItem(1, Optional.empty())).orElse(null);

    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        if (ExecutableItemsManager.getInstance().getObject(hookItem).isPresent()) {
            return ExecutableItemsManager.getInstance().getObject(hookItem).get().getId();
        }
        return null;
    }
}
