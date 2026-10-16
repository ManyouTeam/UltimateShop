package cn.superiormc.ultimateshop.paper.adapts.v1_21_6;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

public class AttributesCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "attributes";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<Map<?, ?>> attributes = new ArrayList<>(section.getMapList("attributes"));
        if (!section.isList("attributes")) {
            // Backwards compatibility for the old attribute-type section format.
            ConfigurationSection attributesKey = section.getConfigurationSection("attributes");
            if (attributesKey != null) {
                for (String attribute : attributesKey.getKeys(false)) {
                    ConfigurationSection attributeSection = attributesKey.getConfigurationSection(attribute);
                    if (attributeSection != null) {
                        Map<String, Object> attributeData = new LinkedHashMap<>(attributeSection.getValues(false));
                        attributeData.put("type", attribute);
                        attributes.add(attributeData);
                    }
                }
            }
        }
        if (section.isList("attributes") || !attributes.isEmpty()) {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
            for (Map<?, ?> attributeData : attributes) {
                Object attributeType = attributeData.get("type");
                if (attributeType == null) {
                    continue;
                }
                String attribute = attributeType.toString();
                Attribute attributeInst = Registry.ATTRIBUTE.get(CommonUtil.parseNamespacedKey(attribute));
                if (attributeInst == null) {
                    continue;
                }
                ConfigurationSection subSection = new org.bukkit.configuration.MemoryConfiguration();
                for (Map.Entry<?, ?> attributeEntry : attributeData.entrySet()) {
                    subSection.set(attributeEntry.getKey().toString(), attributeEntry.getValue());
                }

                String attribName = subSection.getString("name");
                double attribAmount = subSection.getDouble("amount");
                String attribOperation = subSection.getString("operation");

                String attribSlot = subSection.getString("slot");
                String attribDisplayMode = subSection.getString("display-mode");

                EquipmentSlotGroup slot = EquipmentSlotGroup.ANY;

                if (attribSlot != null) {
                    EquipmentSlotGroup targetSlot = EquipmentSlotGroup.getByName(attribSlot);
                    if (targetSlot != null) {
                        slot = targetSlot;
                    }
                }

                if (attribName != null && attribOperation != null) {
                    AttributeModifier modifier = new AttributeModifier(
                                CommonUtil.parseNamespacedKey(attribName),
                                attribAmount,
                                Enums.getIfPresent(AttributeModifier.Operation.class, attribOperation)
                                        .or(AttributeModifier.Operation.ADD_NUMBER),
                                slot);
                    if (CommonUtil.getMinorVersion(21, 6)) {
                        AttributeModifierDisplay attributeModifierDisplay = AttributeModifierDisplay.hidden();
                        if (attribDisplayMode != null) {
                            attributeModifierDisplay = switch (attribDisplayMode.toUpperCase()) {
                                case "HIDDEN" -> AttributeModifierDisplay.hidden();
                                case "RESET", "DEFAULT" -> AttributeModifierDisplay.reset();
                                case "OVERRIDE", "TEXT" -> AttributeModifierDisplay.override(PaperTextUtil.modernParse(
                                        subSection.getString("display-text")
                                        , player));
                                default -> attributeModifierDisplay;
                            };
                        }
                        builder.addModifier(attributeInst, modifier, slot, attributeModifierDisplay);
                    } else {
                        builder.addModifier(attributeInst, modifier, slot);
                    }
                }
            }
            item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers attributes = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (attributes != null) {
                List<Map<String, Object>> attributeList = new ArrayList<>();

                for (ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
                    Attribute attribute = entry.attribute();
                    AttributeModifier modifier = entry.modifier();
                    EquipmentSlotGroup slot = entry.getGroup();
                    AttributeModifierDisplay display = entry.display();
                    Map<String, Object> attributeData = new LinkedHashMap<>();
                    attributeData.put("type", attribute.getKey().getKey().toUpperCase(Locale.ENGLISH));
                    attributeData.put("name", modifier.getName());
                    attributeData.put("amount", modifier.getAmount());
                    attributeData.put("operation", modifier.getOperation().name());
                    attributeData.put("slot", slot.toString());

                    // Paper 1.21+ display mode
                    if (CommonUtil.getMinorVersion(21, 6)) {
                        if (display instanceof AttributeModifierDisplay.Hidden) {
                            attributeData.put("display-mode", "HIDDEN");
                        } else if (display instanceof AttributeModifierDisplay.Default) {
                            attributeData.put("display-mode", "RESET");
                        } else if (display instanceof AttributeModifierDisplay.OverrideText displayText) {
                            attributeData.put("display-mode", "OVERRIDE");
                            attributeData.put("display-text", displayText.text());
                        } else {
                            attributeData.put("display-mode", "DEFAULT");
                        }
                    }
                    attributeList.add(attributeData);
                }
                section.set("attributes", attributeList);
            }
        }
    }

}
