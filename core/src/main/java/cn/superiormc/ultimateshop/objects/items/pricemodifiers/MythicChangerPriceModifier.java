package cn.superiormc.ultimateshop.objects.items.pricemodifiers;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MythicChangerPriceModifier implements PriceModifier {

    private final ConfigurationSection section;

    private final Map<String, RuleOperation> ruleOperations = new LinkedHashMap<>();

    private volatile Field managerField;

    private volatile Method matchMethod;

    private volatile boolean matcherResolved;

    public MythicChangerPriceModifier(ConfigurationSection section) {
        this.section = section;
        ConfigurationSection rulesSection = section.getConfigurationSection("rules");
        if (rulesSection == null) {
            return;
        }
        for (String key : rulesSection.getKeys(false)) {
            ConfigurationSection rule = rulesSection.getConfigurationSection(key);
            if (rule == null) {
                continue;
            }
            String configuredOperation = rule.getString("operation", "MULTIPLY");
            RuleOperation operation = RuleOperation.parse(configuredOperation);
            if (operation == null) {
                NumericValuePriceModifier.reportInvalidOperation(
                        rule, configuredOperation, "ADD or MULTIPLY");
                continue;
            }
            ruleOperations.put(key, operation);
        }
    }

    @Override
    public BigDecimal getMultiplier(Player player, ItemStack itemStack, BigDecimal currentPrice) {
        if (!CommonUtil.checkPluginLoad("MythicChanger")) {
            return BigDecimal.ONE;
        }
        ConfigurationSection rulesSection = section.getConfigurationSection("rules");
        if (rulesSection == null) {
            return BigDecimal.ONE;
        }

        List<RuleValue> matchedRules = new ArrayList<>();
        for (String key : rulesSection.getKeys(false)) {
            ConfigurationSection rule = rulesSection.getConfigurationSection(key);
            RuleOperation operation = ruleOperations.get(key);
            if (rule == null || operation == null
                    || !matches(rule.getConfigurationSection("match-item"), player, itemStack)) {
                continue;
            }
            String valueExpression = TextUtil.withPAPI(rule.getString("value", "1"), player);
            Optional<BigDecimal> value = MathUtil.tryCalculate(valueExpression);
            if (value.isEmpty()) {
                reportInvalidValue(rule, valueExpression);
                continue;
            }
            matchedRules.add(new RuleValue(operation, value.get()));
        }
        if (matchedRules.isEmpty()) {
            return BigDecimal.ONE;
        }

        String mode = section.getString("mode", "STACK").toUpperCase(Locale.ROOT);
        if (mode.equals("MAX") || mode.equals("HIGHEST")) {
            return matchedRules.stream().map(RuleValue::asMultiplier).max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        }
        if (mode.equals("MIN") || mode.equals("LOWEST")) {
            return matchedRules.stream().map(RuleValue::asMultiplier).min(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        }

        BigDecimal result = BigDecimal.ONE;
        for (RuleValue rule : matchedRules) {
            if (rule.isAdd()) {
                result = result.add(rule.value());
            } else {
                result = result.multiply(rule.value());
            }
        }
        return result;
    }

    private static void reportInvalidValue(ConfigurationSection rule, String value) {
        if (ErrorManager.errorManager == null) {
            return;
        }
        String path = rule == null ? null : rule.getCurrentPath();
        String location = path == null || path.isEmpty()
                ? (rule == null ? "unknown price modifier rule" : rule.getName())
                : path;
        ErrorManager.errorManager.sendErrorMessage("§cError: Invalid price modifier value '"
                + value + "' at " + location + ". This rule is ignored.");
    }

    private boolean matches(ConfigurationSection matchSection, Player player, ItemStack itemStack) {
        if (matchSection == null) {
            return false;
        }
        try {
            resolveMatcher();
            if (managerField == null || matchMethod == null) {
                return false;
            }
            Object manager = managerField.get(null);
            if (manager == null) {
                return false;
            }
            return Boolean.TRUE.equals(matchMethod.invoke(manager, matchSection, player, itemStack));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    private synchronized void resolveMatcher() throws ReflectiveOperationException {
        if (matcherResolved) {
            return;
        }
        try {
            Class<?> managerClass = Class.forName("cn.superiormc.mythicchanger.manager.MatchItemManager");
            managerField = managerClass.getField("matchItemManager");
            matchMethod = managerClass.getMethod("getMatch",
                    ConfigurationSection.class, Player.class, ItemStack.class);
        } finally {
            matcherResolved = true;
        }
    }

    private enum RuleOperation {
        ADD,
        MULTIPLY;

        private static RuleOperation parse(String value) {
            if (value == null) {
                return null;
            }
            try {
                return valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }

    private record RuleValue(RuleOperation operation, BigDecimal value) {

        private boolean isAdd() {
            return operation == RuleOperation.ADD;
        }

        private BigDecimal asMultiplier() {
            return isAdd() ? BigDecimal.ONE.add(value) : value;
        }
    }
}
