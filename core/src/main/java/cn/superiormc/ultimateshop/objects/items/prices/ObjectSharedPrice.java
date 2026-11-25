package cn.superiormc.ultimateshop.objects.items.prices;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class ObjectSharedPrice {

    private final String id;

    private final ConfigurationSection template;

    public ObjectSharedPrice(String id, ConfigurationSection template) {
        this.id = id;
        this.template = template;
    }

    public String getID() {
        return id;
    }

    public ConfigurationSection resolve(ConfigurationSection price) {
        ConfigurationSection resolved = new MemoryConfiguration().createSection(price.getName());
        copyPriceFields(template, resolved);
        copyPriceFields(price, resolved);
        return resolved;
    }

    private void copyPriceFields(ConfigurationSection source, ConfigurationSection destination) {
        for (String key : source.getKeys(false)) {
            ConfigurationSection child = source.getConfigurationSection(key);
            if (child == null) {
                destination.set(key, source.get(key));
            } else {
                ConfigurationSection target = destination.getConfigurationSection(key);
                if (target == null) {
                    target = destination.createSection(key);
                }
                copyPriceFields(child, target);
            }
        }
    }
}
