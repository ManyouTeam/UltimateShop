package cn.superiormc.ultimateshop.objects.items.pricemodifiers;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class PriceModifierRegistry {

    private static final Map<String, Function<ConfigurationSection, PriceModifier>> FACTORIES = new LinkedHashMap<>();

    static {
        FACTORIES.put("durability", DurabilityPriceModifier::new);
        FACTORIES.put("lore", LorePriceModifier::new);
        FACTORIES.put("nbt", NbtPriceModifier::new);
        FACTORIES.put("match_item", MythicChangerPriceModifier::new);
        // Legacy alias for configurations created before the type was renamed.
        FACTORIES.put("mythic_changer", MythicChangerPriceModifier::new);
    }

    private PriceModifierRegistry() {
    }

    public static void register(String type,
                                Function<ConfigurationSection, PriceModifier> factory) {
        if (type == null || type.isEmpty() || factory == null) {
            return;
        }
        synchronized (PriceModifierRegistry.class) {
            FACTORIES.put(type.toLowerCase(Locale.ROOT), factory);
        }
        ConfigManager manager = ConfigManager.configManager;
        if (manager != null) {
            manager.reloadSellPriceModifiers();
        }
    }

    public static PriceModifier create(ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", true)) {
            return null;
        }
        String type = section.getString("type", section.getName()).toLowerCase(Locale.ROOT);
        Function<ConfigurationSection, PriceModifier> factory;
        synchronized (PriceModifierRegistry.class) {
            factory = FACTORIES.get(type);
        }
        return factory == null ? null : factory.apply(section);
    }

    public static PriceModifierChain createChain(ConfigurationSection section) {
        List<PriceModifier> modifiers = new ArrayList<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                PriceModifier modifier = create(section.getConfigurationSection(key));
                if (modifier != null) {
                    modifiers.add(modifier);
                }
            }
        }
        return new PriceModifierChain(modifiers);
    }
}
