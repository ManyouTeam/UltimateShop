package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SignText;
import org.bukkit.DyeColor;
import java.util.Locale;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SignTextBackCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "sign-text-back";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection value = section.getConfigurationSection("sign-text-back");
        if (value != null) {
            SignText.Builder builder = SignText.signText();
            if (value.contains("lines")) {
                builder.lines(value.getStringList("lines").stream()
                        .map(line -> PaperTextUtil.modernParse(CommonUtil.modifyString(player, line, args), player))
                        .toList());
            }
            String color = value.getString("color");
            if (color != null) {
                try {
                    builder.color(DyeColor.valueOf(color.toUpperCase(Locale.ENGLISH)));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (value.contains("glowing")) {
                builder.hasGlowingText(value.getBoolean("glowing"));
            }
            item.setData(DataComponentTypes.SIGN_TEXT_BACK, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.SIGN_TEXT_BACK)) {
            SignText value = item.getData(DataComponentTypes.SIGN_TEXT_BACK);
            if (value != null) {
                ConfigurationSection text = section.createSection("sign-text-back");
                text.set("lines", value.lines().stream().map(PaperTextUtil::changeToString).toList());
                text.set("color", value.color().name());
                text.set("glowing", value.hasGlowingText());
            }
        }
    }
}
