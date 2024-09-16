package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.ThingType;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectSinglePrice;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectDisplayPlaceholder implements Comparable<ObjectDisplayPlaceholder>{

    private final AbstractSingleThing thing;

    private ConfigurationSection singleSection;

    public ObjectDisplayPlaceholder(AbstractSingleThing thing) {
        this.thing = thing;
        this.singleSection = thing.singleSection;
        if (thing instanceof ObjectSinglePrice && ((ObjectSinglePrice) thing).getCustomPrice()) {
            this.singleSection = ConfigManager.configManager.config.
                    getConfigurationSection("prices." + singleSection.getString("custom-type"));
        }
    }

    public String getDisplayName(int multi, BigDecimal amount, boolean alwaysStatic) {
        return thing.getDisplayName(multi, amount, alwaysStatic);
    }

    @Override
    public int hashCode() {
        int result = 17; // 初始值选择一个非零素数
        result = 31 * result + thing.type.hashCode(); // 使用类型字段计算哈希码

        if (thing.type == ThingType.HOOK_ECONOMY) {
            String economyPlugin = singleSection.getString("economy-plugin");
            String economyType = singleSection.getString("economy-type");

            result = 31 * result + (economyPlugin != null ? economyPlugin.hashCode() : 0);
            result = 31 * result + (economyType != null ? economyType.hashCode() : 0);
        } else if (thing.type == ThingType.VANILLA_ECONOMY) {
            String economyType = singleSection.getString("economy-type");
            result = 31 * result + (economyType != null ? economyType.hashCode() : 0);
        } else if (thing.type == ThingType.HOOK_ITEM) {
            String hookPlugin = singleSection.getString("hook-plugin");
            String hookType = singleSection.getString("hook-type");

            result = 31 * result + (hookPlugin != null ? hookPlugin.hashCode() : 0);
            result = 31 * result + (hookType != null ? hookType.hashCode() : 0);
        } else if (thing.type == ThingType.VANILLA_ITEM || thing.type == ThingType.MATCH_ITEM || thing.type == ThingType.CUSTOM) {
            result = 31 * result + convertConfigurationSection(singleSection).hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ObjectDisplayPlaceholder otherPrice = (ObjectDisplayPlaceholder) obj;

        if (singleSection.getString("placeholder") != null && otherPrice.singleSection.getString("placeholder") != null) {
            return singleSection.getString("placeholder").equals(otherPrice.singleSection.getString("placeholder"));
        }

        if (thing.type == ThingType.FREE || thing.type == ThingType.UNKNOWN) {
            return true;
        }

        if (thing.type == ThingType.HOOK_ECONOMY) {
            if (singleSection.getString("economy-plugin") == null) {
                return true;
            } else {
                if (singleSection.getString("economy-type") == null) {
                    return singleSection.getString("economy-plugin").equals(otherPrice.singleSection.getString("economy-plugin"));
                }
                return singleSection.getString("economy-plugin").equals(otherPrice.singleSection.getString("economy-plugin"))
                        && singleSection.getString("economy-type").equals(otherPrice.singleSection.getString("economy-type"));
            }
        }

        if (thing.type == ThingType.VANILLA_ECONOMY) {
            if (singleSection.getString("economy-type") == null) {
                return true;
            }
            return singleSection.getString("economy-type").equals(otherPrice.singleSection.getString("economy-type"));
        }

        if (thing.type == ThingType.HOOK_ITEM) {
            if (singleSection.getString("hook-plugin") == null) {
                return true;
            } else {
                if (singleSection.getString("hook-item") == null) {
                    return singleSection.getString("hook-plugin").equals(otherPrice.singleSection.getString("hook-plugin"));
                }
                return singleSection.getString("hook-plugin").equals(otherPrice.singleSection.getString("hook-plugin"))
                        && singleSection.getString("hook-type").equals(otherPrice.singleSection.getString("hook-type"));
            }
        }

        if (thing.type == ThingType.VANILLA_ITEM || thing.type == ThingType.MATCH_ITEM  || thing.type == ThingType.CUSTOM) {
            return areConfigurationSectionsEqual(singleSection, otherPrice.singleSection);
        }

        return super.equals(obj);
    }

    private boolean areConfigurationSectionsEqual(ConfigurationSection section1, ConfigurationSection section2) {
        if (section1 == section2) {
            return true;
        }
        if (section1 == null || section2 == null) {
            return false;
        }
        if (!section1.getKeys(true).equals(section2.getKeys(true))) {
            return false;
        }
        List<String> notCheckKey = List.of("apply", "start-apply", "end-apply", "placeholder", "conditions", "amount");
        for (String key : section1.getKeys(true)) {
            if (notCheckKey.contains(key)) {
                continue;
            }
            if (!section1.get(key).equals(section2.get(key))) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> convertConfigurationSection(ConfigurationSection section) {
        Map<String, Object> values = section.getValues(false);
        Map<String, String> result = new HashMap<>();

        List<String> notCheckKey = List.of("apply", "start-apply", "end-apply", "placeholder", "conditions", "amount");

        for (String key : values.keySet()) {
            if (notCheckKey.contains(key)) {
                continue;
            }
            Object value = values.get(key);
            if (value != null) {
                result.put(key, value.toString());
            }
        }

        return result;
    }

    public AbstractSingleThing getThing() {
        return thing;
    }

    @Override
    public int compareTo(@NotNull ObjectDisplayPlaceholder o) {
        if (this.equals(o)) {
            return 0;
        }
        return thing.compareTo(o.getThing());
    }
}
