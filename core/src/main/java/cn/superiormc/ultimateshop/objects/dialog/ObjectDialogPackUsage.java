package cn.superiormc.ultimateshop.objects.dialog;

import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ObjectDialogPackUsage {

    private final Map<String, ObjectDialogTemplate> templates = new LinkedHashMap<>();

    private final Map<String, Set<Integer>> backgrounds = new LinkedHashMap<>();

    private final Map<String, Icon> icons = new LinkedHashMap<>();

    public void addBackground(
        ObjectDialogTemplate template,
        boolean product,
        boolean enabled,
        Collection<Integer> positions) {
        if (!template.isValid() || positions.isEmpty()) {
            return;
        }
        templates.putIfAbsent(template.getResourceId(), template);
        String id =
            template.getResourceId()
            + (product ? "_product" : "_button")
            + (enabled ? "_enabled" : "_disabled");
        backgrounds.computeIfAbsent(id, ignored -> new LinkedHashSet<>()).addAll(positions);
    }

    public void addIcon(
        ObjectDialogTemplate template, String texture, Collection<Integer> positions) {
        if (!template.isValid() || texture == null || template.getIconSize() == 0 || positions.isEmpty()) {
            return;
        }
        String id =
            DialogFontUtil.getIconFontId(
            texture,
            template.getIconSize(),
            template.getBackground(),
            template.getLoreRow());
        icons.computeIfAbsent(id, ignored -> new Icon(template, texture))
            .positions
            .addAll(positions);
    }

    public void collectDisplayIcons(
        ObjectDialogTemplate template,
        ConfigurationSection display,
        Collection<Integer> positions) {
        for (String sprite : MinecraftResourceManager.minecraftResourceManager.getConfiguredSprites(display)) {
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
