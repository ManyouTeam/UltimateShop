package cn.superiormc.ultimateshop.hooks.protection;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionDominionHook extends AbstractProtectionHook {

    public ProtectionDominionHook() {
        super("Towny");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        try {
            DominionAPI dominionAPI = DominionAPI.getInstance();
            DominionDTO dominionDTO = dominionAPI.getDominionByLoc(location);
            if (dominionDTO != null) {
                return dominionAPI.checkPrivilegeFlag(dominionDTO, Flags.CONTAINER, player);
            }
            return true;
        } catch (Throwable throwable) {
            return true;
        }
    }
}
