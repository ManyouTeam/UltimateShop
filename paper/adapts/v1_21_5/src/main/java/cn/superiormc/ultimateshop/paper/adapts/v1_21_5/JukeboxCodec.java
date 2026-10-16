package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JukeboxCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "song";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String song = section.getString("song");
        if (song != null) {
            JukeboxSong jukeboxSong = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getOrThrow(CommonUtil.parseNamespacedKey(song));
            JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(jukeboxSong);
            item.setData(DataComponentTypes.JUKEBOX_PLAYABLE, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.JUKEBOX_PLAYABLE)) {
            JukeboxPlayable jukeboxPlayable = item.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
            if (jukeboxPlayable != null) {
                JukeboxSong song = jukeboxPlayable.jukeboxSong();
                section.set("song", song.key().asString());
            }
        }
    }

}
