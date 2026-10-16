package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MusicCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "music";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String musicKey = section.getString("music");
        if (musicKey != null) {
            MusicInstrument musicInstrument = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).get(CommonUtil.parseNamespacedKey(musicKey));
            if (musicInstrument != null) {
                item.setData(DataComponentTypes.INSTRUMENT, musicInstrument);
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.INSTRUMENT)) {
            MusicInstrument instrument = item.getData(DataComponentTypes.INSTRUMENT);
            if (instrument != null) {
                NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT)
                        .getKey(instrument);
                if (key != null) {
                    section.set("music", key.asString());
                }
            }
        }
    }

}
