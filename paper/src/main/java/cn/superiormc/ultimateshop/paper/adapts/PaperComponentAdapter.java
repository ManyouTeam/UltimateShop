package cn.superiormc.ultimateshop.paper.adapts;

import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PaperComponentAdapter {

    private static final ComponentCodecRegistry registry = createRegistry();

    private static ComponentCodecRegistry createRegistry() {
        ComponentCodecRegistry registry = new ComponentCodecRegistry();

        if (CommonUtil.getMinorVersion(21, 5)) {
            register(registry, "v1_21_5.Paper1215Codecs");
        }
        if (CommonUtil.getMinorVersion(21, 6)) {
            register(registry, "v1_21_6.Paper1216Codecs");
        }
        if (CommonUtil.getMinorVersion(21, 11)) {
            register(registry, "v1_21_11.Paper12111Codecs");
        }
        if (CommonUtil.getYearVersion(26, 1, 0)) {
            register(registry, "v26_1.Paper261Codecs");
        }
        if (CommonUtil.getYearVersion(26, 2, 0)) {
            register(registry, "v26_2.Paper262Codecs");
        }
        if (CommonUtil.getYearVersion(26, 3, 0)) {
            register(registry, "v26_3.Paper263Codecs");
        }

        return registry;
    }

    private static void register(ComponentCodecRegistry registry, String className) {
        try {
            VersionCodecs codecs = (VersionCodecs) Class
                    .forName("cn.superiormc.ultimateshop.paper.adapts." + className)
                    .getDeclaredConstructor().newInstance();
            codecs.registerCodecs(registry);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not load Paper component codecs: " + className, exception);
        }
    }

    public static ItemStack editItemStack(ItemStack item, Player player,
                                          ConfigurationSection section, String... args) {
        for (ComponentCodec codec : registry.getCodecs()) {
            codec.apply(item, player, section, args);
        }
        return item;
    }

    public static ConfigurationSection serializeItemStack(ItemStack item, ConfigurationSection section) {
        for (ComponentCodec codec : registry.getCodecs()) {
            codec.write(item, section);
        }
        return section;
    }

}
