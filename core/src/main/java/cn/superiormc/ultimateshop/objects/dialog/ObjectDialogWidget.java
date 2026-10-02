package cn.superiormc.ultimateshop.objects.dialog;

import java.util.List;

public class ObjectDialogWidget {

    private final ObjectDialogTemplate template;

    private final int x;

    private final int y;

    private final String name;

    private final List<String> lore;

    private final List<String> hoverLore;

    private final String actionId;

    private final String actionLabel;

    private final boolean product;

    private final String icon;

    private final String iconText;

    private final Boolean showSprite;

    private final boolean selected;

    public ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                              String actionId, String actionLabel, boolean product) {
        this(template, x, y, name, lore, actionId, actionLabel, product, null);
    }

    public ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                              String actionId, String actionLabel, boolean product, String icon) {
        this(template, x, y, name, lore, actionId, actionLabel, product, icon, false);
    }

    public ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                              String actionId, String actionLabel, boolean product, String icon, boolean selected) {
        this(template, x, y, name, lore, actionId, actionLabel, product, icon, selected, lore);
    }

    public ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                              String actionId, String actionLabel, boolean product, String icon, boolean selected,
                              List<String> hoverLore) {
        this(template, x, y, name, lore, actionId, actionLabel, product, icon, selected, hoverLore, null);
    }

    public ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                              String actionId, String actionLabel, boolean product, String icon, boolean selected,
                              List<String> hoverLore, String iconText) {
        this(template, x, y, name, lore, actionId, actionLabel, product, icon, selected, hoverLore, iconText, null);
    }

    private ObjectDialogWidget(ObjectDialogTemplate template, int x, int y, String name, List<String> lore,
                               String actionId, String actionLabel, boolean product, String icon, boolean selected,
                               List<String> hoverLore, String iconText, Boolean showSprite) {
        this.showSprite = showSprite;
        this.iconText = iconText;
        this.selected = selected;
        this.icon = icon;
        this.template = template;
        this.x = x;
        this.y = y;
        this.name = name;
        this.lore = List.copyOf(lore);
        this.hoverLore = List.copyOf(hoverLore);
        this.actionId = actionId;
        this.actionLabel = actionLabel;
        this.product = product;
    }

    public boolean intersects(ObjectDialogWidget other) {
        return x < other.x + other.template.getWidth() && x + template.getWidth() > other.x
                && y < other.y + other.template.getHeight() && y + template.getHeight() > other.y;
    }

    public ObjectDialogTemplate getTemplate() {
        return template;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getHoverLore() {
        return hoverLore;
    }

    public String getActionId() {
        return actionId;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public String getIcon() {
        return icon;
    }

    public String getIconText() {
        return iconText;
    }

    public Boolean getShowSprite() {
        return showSprite;
    }

    public ObjectDialogWidget withShowSprite(Boolean value) {
        return new ObjectDialogWidget(template, x, y, name, lore, actionId, actionLabel, product,
                icon, selected, hoverLore, iconText, value);
    }

    public boolean isProduct() {
        return product;
    }

    public boolean isSelected() {
        return selected;
    }
}
