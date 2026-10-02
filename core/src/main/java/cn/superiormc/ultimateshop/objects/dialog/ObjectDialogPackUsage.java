package cn.superiormc.ultimateshop.objects.dialog;

import cn.superiormc.ultimateshop.managers.ItemMaterialManager;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectDialogPackUsage {

    private final Map<String, ObjectDialogTemplate> templates = new LinkedHashMap<>();

    private final Map<String, Set<Integer>> backgrounds = new LinkedHashMap<>();

    private final Map<String, Icon> icons = new LinkedHashMap<>();

    public void collectMenu(ConfigurationSection menu, Map<Integer, List<Source>> slots, List<List<Source>> sidebar) {
        ObjectDialogTemplate grid = ObjectDialogTemplate.fromMenu(menu, menu.getString("dialog.products.template", "product"));
        List<int[]> configured = DialogLayoutUtil.getConfiguredProductPositions(menu);
        long maximumProducts = slots.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(source -> source.product && enabled(menu, entry.getKey(), source))).count();
        Set<State> states = new LinkedHashSet<>();
        states.add(new State(0, 0, 9));
        for (Map.Entry<Integer, List<Source>> entry : slots.entrySet()) {
            int slot = entry.getKey();
            List<Source> options = entry.getValue().stream().filter(source -> enabled(menu, slot, source)).toList();
            Set<State> next = new LinkedHashSet<>();
            boolean optional = options.isEmpty() || options.stream().allMatch(Source::canHide);
            for (State state : states) {
                if (optional) {
                    next.add(state);
                }
                for (Source source : options) {
                    if (source.close) {
                        next.add(state);
                        continue;
                    }
                    String fallback = source.product ? menu.getString("dialog.products.template", "product") : "button";
                    ObjectDialogTemplate template = ObjectDialogTemplate.fromMenu(menu,
                            DialogLayoutUtil.getTemplateName(menu, slot, source.config, fallback));
                    Set<Integer> positions = new LinkedHashSet<>();
                    if (source.product) {
                        if (configured.size() < maximumProducts) {
                            int[] position = DialogLayoutUtil.getGridProductPosition(menu, grid, state.products);
                            positions.add(DialogLayoutUtil.getPosition(menu, slot, source.config, position[0], position[1])[1]);
                        }
                        if (state.products < configured.size()) {
                            int[] position = configured.get(state.products);
                            positions.add(DialogLayoutUtil.getPosition(menu, slot, source.config, position[0], position[1])[1]);
                        }
                        addBackground(template, true, true, positions);
                        ConfigurationSection display = source.config == null ? null : source.config.getConfigurationSection("display-item");
                        if (source.sprites == null) {
                            collectDisplayIcons(template, display == null ? source.config : display, positions);
                        } else {
                            for (String sprite : source.sprites) {
                                addIcon(template, DialogFontUtil.getIconTexture(sprite), positions);
                            }
                        }
                        next.add(new State(state.products + 1, state.buttons, state.bottom));
                    } else {
                        int[] position = DialogLayoutUtil.getPosition(menu, slot, source.config, 12,
                                DialogLayoutUtil.getButtonY(menu, state.buttons));
                        positions.add(position[1]);
                        addBackground(template, false, true, positions);
                        int bottom = position[0] < menu.getInt("dialog.products.x", 144)
                                ? Math.max(state.bottom, position[1] + template.getHeight()) : state.bottom;
                        next.add(new State(state.products, state.buttons + 1, bottom));
                    }
                }
            }
            states = next;
        }
        if (!sidebar.isEmpty() && menu.getBoolean("dialog.sidebar.enabled", true)) {
            ObjectDialogTemplate template = ObjectDialogTemplate.fromMenu(menu, menu.getString("dialog.sidebar.template", "button"));
            Set<Integer> starts = new LinkedHashSet<>();
            for (State state : states) {
                starts.add(menu.getInt("dialog.sidebar.y", state.bottom + 9));
            }
            Set<Integer> indices = new LinkedHashSet<>(Set.of(0));
            for (List<Source> options : sidebar) {
                Set<Integer> next = new LinkedHashSet<>();
                Set<Integer> positions = new LinkedHashSet<>();
                for (int index : indices) {
                    for (int start : starts) {
                        positions.add(start + index * (template.getHeight() + menu.getInt("dialog.sidebar.gap-y", 9)));
                    }
                    next.add(index + 1);
                    if (options.stream().allMatch(Source::canHide)) {
                        next.add(index);
                    }
                }
                addBackground(template, false, true, positions);
                indices = next;
            }
        }
    }

    private static boolean enabled(ConfigurationSection menu, int slot, Source source) {
        boolean enabled = source.config == null || source.config.getBoolean("dialog.enabled", true);
        return menu.getBoolean("dialog.slots." + slot + ".enabled", enabled);
    }

    private record State(int products, int buttons, int bottom) {
    }

    public static class Source {

        private final ConfigurationSection config;

        private final boolean product;

        private final boolean close;

        private final boolean dynamic;

        private final Collection<String> sprites;

        public Source(ConfigurationSection config, boolean product, boolean close, boolean dynamic) {
            this(config, product, close, dynamic, null);
        }

        public Source(ConfigurationSection config, boolean product, boolean close, boolean dynamic, Collection<String> sprites) {
            this.config = config;
            this.product = product;
            this.close = close;
            this.dynamic = dynamic;
            this.sprites = sprites;
        }

        public boolean isProduct() {
            return product;
        }

        public boolean canHide() {
            return dynamic || (config != null && ((config.isConfigurationSection("display-conditions")
                    && !config.getConfigurationSection("display-conditions").getKeys(false).isEmpty())
                    || (!product && (config.isConfigurationSection("display-item-conditions")
                    || config.getString("display-item.name", "").contains("%")))));
        }
    }

    public void addBackground(ObjectDialogTemplate template, boolean product, boolean enabled, Collection<Integer> positions) {
        if (positions.isEmpty()) {
            return;
        }
        templates.putIfAbsent(template.getResourceId(), template);
        String id = template.getResourceId() + (product ? "_product" : "_button") + (enabled ? "_enabled" : "_disabled");
        backgrounds.computeIfAbsent(id, ignored -> new LinkedHashSet<>()).addAll(positions);
    }

    public void addIcon(ObjectDialogTemplate template, String texture, Collection<Integer> positions) {
        if (texture == null || template.getIconSize() == 0 || positions.isEmpty()) {
            return;
        }
        String id = DialogFontUtil.getIconFontId(texture, template.getIconSize(), template.getBackground(), template.getLoreRow());
        icons.computeIfAbsent(id, ignored -> new Icon(template, texture)).positions.addAll(positions);
    }

    public void collectDisplayIcons(ObjectDialogTemplate template, ConfigurationSection display, Collection<Integer> positions) {
        for (String sprite : ItemMaterialManager.getConfiguredSprites(display)) {
            addIcon(template, DialogFontUtil.getIconTexture(sprite), positions);
        }
    }

    public Map<String, ObjectDialogTemplate> getTemplates() {
        return templates;
    }

    public Map<String, Set<Integer>> getBackgrounds() {
        return backgrounds;
    }

    public Collection<Icon> getIcons() {
        return icons.values();
    }

    public boolean hasIcon(String id, int y) {
        Icon icon = icons.get(id);
        return icon != null && icon.positions.contains(y);
    }

    public Set<String> getTextures() {
        Set<String> result = new LinkedHashSet<>();
        icons.values().forEach(icon -> result.add(icon.texture));
        return result;
    }

    public static class Icon {

        private final ObjectDialogTemplate template;

        private final String texture;

        private final Set<Integer> positions = new LinkedHashSet<>();

        private Icon(ObjectDialogTemplate template, String texture) {
            this.template = template;
            this.texture = texture;
        }

        public ObjectDialogTemplate getTemplate() {
            return template;
        }

        public String getTexture() {
            return texture;
        }

        public Set<Integer> getPositions() {
            return positions;
        }
    }
}
