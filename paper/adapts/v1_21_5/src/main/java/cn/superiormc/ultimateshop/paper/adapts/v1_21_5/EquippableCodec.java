package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.*;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EquippableCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "equippable";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection equippable = section.getConfigurationSection("equippable");
        if (equippable != null) {
            String slotStr = equippable.getString("slot");
            if (slotStr != null) {
                EquipmentSlot slot = EquipmentSlot.valueOf(slotStr.toUpperCase());
                Equippable.Builder builder = Equippable.equippable(slot);
                String equipSound = equippable.getString("equip-sound");
                if (equipSound != null) {
                    builder.equipSound(CommonUtil.parseNamespacedKey(equipSound));
                }
                String assetId = equippable.getString("asset-id");
                if (assetId != null) {
                    builder.assetId(CommonUtil.parseNamespacedKey(assetId));
                }
                String cameraOverlay = equippable.getString("camera-overlay");
                if (cameraOverlay != null) {
                    builder.cameraOverlay(CommonUtil.parseNamespacedKey(cameraOverlay));
                }
                if (equippable.contains("allowed-entities")) {
                    List<TypedKey<EntityType>> keys = equippable.getStringList("allowed-entities")
                            .stream()
                            .map(CommonUtil::parseNamespacedKey)          // Key
                            .map(key -> TypedKey.create(RegistryKey.ENTITY_TYPE, key))
                            .toList();
                    if (!keys.isEmpty()) {
                        builder.allowedEntities(RegistrySet.keySet(RegistryKey.ENTITY_TYPE, keys)
                        );
                    }
                }
                if (equippable.contains("dispensable")) {
                    builder.dispensable(equippable.getBoolean("dispensable"));
                }
                if (equippable.contains("swappable")) {
                    builder.swappable(equippable.getBoolean("swappable"));
                }
                if (equippable.contains("damage-on-hurt")) {
                    builder.damageOnHurt(equippable.getBoolean("damage-on-hurt"));
                }
                if (equippable.contains("equip-on-interact")) {
                    builder.equipOnInteract(equippable.getBoolean("equip-on-interact"));
                }

                
                item.setData(DataComponentTypes.EQUIPPABLE, builder.build());
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.EQUIPPABLE)) {
            Equippable eq = item.getData(DataComponentTypes.EQUIPPABLE);
            if (eq != null) {
                ConfigurationSection equippableSection = section.createSection("equippable");
                equippableSection.set("slot", eq.slot().name());
                equippableSection.set("equip-sound", eq.equipSound().asString());
                if (eq.assetId() != null) {
                    equippableSection.set("asset-id", eq.assetId().asString());
                }
                if (eq.cameraOverlay() != null) {
                    equippableSection.set("camera-overlay", eq.cameraOverlay().asString());
                }
                // Equippable allowed entities
                if (eq.allowedEntities() != null && !eq.allowedEntities().values().isEmpty()) {
                    equippableSection.set("allowed-entities",
                            eq.allowedEntities().values().stream()  // <-- ?.values()
                                    .map(TypedKey::key)                //  NamespacedKey
                                    .map(Key::asString)              //  String
                                    .toList()
                    );
                }
                equippableSection.set("dispensable", eq.dispensable());
                equippableSection.set("swappable", eq.swappable());
                equippableSection.set("damage-on-hurt", eq.damageOnHurt());
                equippableSection.set("equip-on-interact", eq.equipOnInteract());
            }
        }
    }

}
