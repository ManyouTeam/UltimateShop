package cn.superiormc.ultimateshop.objects.dialog;

import java.util.ArrayList;
import java.util.List;

public class ObjectDialogScreen {

    private final int width;

    private int height;

    private final List<ObjectDialogWidget> widgets = new ArrayList<>();

    public ObjectDialogScreen(int width, int height) {
        if (width < 144 || width > 900 || height < 54 || height > 8190 || height % 9 != 0) {
            throw new IllegalArgumentException("Invalid Ore Dialog dimensions");
        }
        this.width = width;
        this.height = height;
    }

    public boolean fits(ObjectDialogWidget widget) {
        if (widget.getX() < 0 || widget.getY() < 0 || widget.getY() % 9 != 0
                || widget.getX() + widget.getTemplate().getWidth() > width
                || widget.getY() + widget.getTemplate().getHeight() > height) {
            return false;
        }
        return widgets.stream().noneMatch(widget::intersects);
    }

    public void addWidget(ObjectDialogWidget widget) {
        if (!fits(widget)) {
            throw new IllegalArgumentException("Ore Dialog widget overlaps or is outside the screen: " + widget.getActionId());
        }
        widgets.add(widget);
    }

    public void ensureHeight(int minimum) {
        int required = ((minimum + 8) / 9) * 9;
        if (required > 8190) {
            throw new IllegalArgumentException("Ore Dialog content exceeds 8190 pixels");
        }
        height = Math.max(height, required);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<ObjectDialogWidget> getWidgets() {
        return List.copyOf(widgets);
    }
}
