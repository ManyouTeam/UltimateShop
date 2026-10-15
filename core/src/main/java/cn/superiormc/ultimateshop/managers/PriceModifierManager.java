package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.objects.items.pricemodifiers.DurabilityPriceModifier;
import cn.superiormc.ultimateshop.objects.items.pricemodifiers.LorePriceModifier;
import cn.superiormc.ultimateshop.objects.items.pricemodifiers.MythicChangerPriceModifier;
import cn.superiormc.ultimateshop.objects.items.pricemodifiers.NbtPriceModifier;
import cn.superiormc.ultimateshop.objects.items.pricemodifiers.PriceModifier;
import cn.superiormc.ultimateshop.objects.items.pricemodifiers.PriceModifierChain;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class PriceModifierManager extends AbstractManager {

    public static PriceModifierManager priceModifierManager;

    private Map<String, Function<ConfigurationSection, PriceModifier>> priceModifierFactories;

    private volatile PriceModifierChain sellPriceModifiers;

    public PriceModifierManager() {
        priceModifierManager = this;
        initPriceModifierTypes();
        loadPriceModifiers();
    }

    private void initPriceModifierTypes() {
        priceModifierFactories = new HashMap<>();
        registerNewPriceModifier("durability", DurabilityPriceModifier::new);
        registerNewPriceModifier("lore", LorePriceModifier::new);
        registerNewPriceModifier("nbt", NbtPriceModifier::new);
        registerNewPriceModifier("match_item", MythicChangerPriceModifier::new);
    }

    public synchronized void loadPriceModifiers() {
        List<PriceModifier> modifiers = new ArrayList<>();
        ConfigurationSection section = ConfigManager.configManager.getSection("sell.price-modifier");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.equals("item-sell-menu")) {
                    continue;
                }
                PriceModifier modifier = createPriceModifier(section.getConfigurationSection(key));
                if (modifier != null) {
                    modifiers.add(modifier);
                }
            }
        }
        sellPriceModifiers = new PriceModifierChain(modifiers);
    }

    public synchronized void registerNewPriceModifier(
        String type, Function<ConfigurationSection, PriceModifier> factory) {
        if (type == null || type.isBlank() || factory == null) {
            return;
        }
        String typeID = type.trim().toLowerCase(Locale.ROOT);
        if (!priceModifierFactories.containsKey(typeID)) {
            priceModifierFactories.put(typeID, factory);
            if (sellPriceModifiers != null) {
                loadPriceModifiers();
            }
        }
    }

    private PriceModifier createPriceModifier(ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", true)) {
            return null;
        }
        String type = section.getString("type", section.getName()).toLowerCase(Locale.ROOT);
        Function<ConfigurationSection, PriceModifier> factory = priceModifierFactories.get(type);
        return factory == null ? null : factory.apply(section);
    }

    public PriceModifierChain getSellPriceModifiers() {
        return sellPriceModifiers;
    }

    @Override
    public synchronized void onPluginDisable() {
        sellPriceModifiers = new PriceModifierChain(List.of());
        priceModifierFactories.clear();
        priceModifierManager = null;
    }
}
