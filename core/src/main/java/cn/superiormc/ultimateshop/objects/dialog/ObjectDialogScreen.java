package cn.superiormc.ultimateshop.objects.dialog;

import cn.superiormc.ultimateshop.managers.ErrorManager;

import java.util.ArrayList;
import java.util.List;

public class ObjectDialogScreen {

    private final int width;

    private int height;

    private boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    private final List<ObjectDialogWidget> widgets = new ArrayList<>();

    public ObjectDialogScreen(int width, int height) {
        if (width < 144 || width > 900 || height < 54 || height > 8190 || height % 9 != 0) {
            valid = false;
            ErrorManager.errorManager.sendErrorMessage("§cError: Invalid Ore Dialog dimensions");
        }
        this.width = width;
        this.height = height;
    }

    public boolean fits(ObjectDialogWidget widget) {
        if (widget.getX() < 0
            || widget.getY() < 0
            || widget.getY() % 9 != 0
            || widget.getX() + widget.getTemplate().getWidth() > width
            || widget.getY() + widget.getTemplate().getHeight() > height) {
            return false;
        }
        return widgets.stream().noneMatch(widget::intersects);
    }

    public void addWidget(ObjectDialogWidget widget) {
        if (!fits(widget)) {
            valid = false;
            ErrorManager.errorManager.sendErrorMessage(
                "§cError: Ore widget overlaps or is outside the screen: " + widget.getActionId());
            return;
        }
        widgets.add(widget);
    }

    public void ensureHeight(int minimum) {
        int required = ((minimum + 8) / 9) * 9;
        if (required > 8190) {
            valid = false;
            ErrorManager.errorManager.sendErrorMessage("§cError: Ore content exceeds 8190 pixels");
            return;
        }
        height = Math.max(height, required);
    }

    public int getWidth() {
        return width;
    }

    public void compactTop() {
        int firstRow = widgets.stream().mapToInt(ObjectDialogWidget::getY).min().orElse(9);
        int offset = Math.max(0, firstRow - 9) / 9 * 9;
        offset = Math.min(offset, Math.max(0, height - 54) / 9 * 9);
        if (offset == 0) {
            return;
        }
        for (int index = 0; index < widgets.size(); index++) {
            ObjectDialogWidget widget = widgets.get(index);
            widgets.set(index, widget.withPosition(widget.getX(), widget.getY() - offset));
        }
        height -= offset;
    }

    public int getHeight() {
        return height;
    }

    public List<ObjectDialogWidget> getWidgets() {
        return List.copyOf(widgets);
    }
}
