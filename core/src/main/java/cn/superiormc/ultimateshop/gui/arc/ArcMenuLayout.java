package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

final class ArcMenuLayout {

    private ArcMenuLayout() {

    }

    static boolean contains(
        double x, double y, double left, double top, double width, double height) {
        return Double.isFinite(x)
            && Double.isFinite(y)
            && x >= left
            && x < left + width
            && y >= top
            && y < top + height;
    }

    static int clampAmount(long amount, int maximum) {
        return (int) Math.max(1L, Math.min(Math.max(1, maximum), amount));
    }

    static float easeOut(float value) {
        float t = Math.max(0, Math.min(1, value));
        return 1 - (1 - t) * (1 - t) * (1 - t);
    }

    static int textWidth(String value) {
        return TextUtil.getTextWidth(value);
    }

    static int blend(int start, int end, float progress) {
        float t = Math.max(0, Math.min(1, progress));
        int result = 0;
        for (int shift = 0; shift <= 16; shift += 8) {
            int a = start >> shift & 255;
            int b = end >> shift & 255;
            result |= Math.round(a + (b - a) * t) << shift;
        }
        return result;
    }

    static String ellipsize(String value, int maximum) {
        if (textWidth(value) <= maximum) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        int width = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\u00a7' && i + 1 < value.length()) {
                result.append(c).append(value.charAt(++i));
                continue;
            }
            int next = textWidth(String.valueOf(c));
            if (width + next + 9 > maximum) {
                break;
            }
            result.append(c);
            width += next;
        }
        return result.append('…').toString();
    }

    static double designPixelsPerBlock(double screenScale) {
        double scale = Double.isFinite(screenScale) ? Math.max(.75, Math.min(2, screenScale)) : 1.5;
        return 180 / scale;
    }

    static double overlayScale(double depth, double pixelsPerBlock, double distance) {
        return Math.max(.1, 1 - worldUnits(depth, pixelsPerBlock) / distance);
    }

    static double worldUnits(double pixels, double pixelsPerBlock) {
        if (!Double.isFinite(pixelsPerBlock) || pixelsPerBlock <= 0) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cArcMenu surface pixels-per-block must be finite and positive");
            return pixels / 180;
        }
        return pixels / pixelsPerBlock;
    }

    static float textScale(double pixelSize, double pixelsPerBlock) {
        return (float) worldUnits(pixelSize / .025, pixelsPerBlock);
    }

    static float panelScaleX(double width, double pixelsPerBlock) {
        return (float) worldUnits(width * 8, pixelsPerBlock);
    }

    static float panelScaleY(double height, double pixelsPerBlock) {
        return (float) worldUnits(height * 4, pixelsPerBlock);
    }

    // Above even the 57-pixel detail model's nearest face (5 + 57/2).

    static final double TOOLTIP_DEPTH = 80;

    static final double ACTIONS_DEPTH = 60;

    static double cursorSafeDepth(double depth, double cursorDepth, double canvasScale) {
        return depth - (TOOLTIP_DEPTH + 4) + (cursorDepth - 8) / canvasScale;
    }

    static double textAnchorY(double y, double height, double fontSize) {
        return textAnchorY(y, height, fontSize, 8);
    }

    static double textAnchorY(double y, double height, double fontSize, double padding) {
        return y + height - padding - 4 * fontSize;
    }

    static double textCenterY(double y, double height, double fontSize) {
        return textAnchorY(y, height, fontSize, (height - 9 * fontSize) / 2);
    }

    static Box place(double pointerX, double pointerY, double width, double height) {
        return place(pointerX, pointerY, width, height, 640, 360);
    }

    static Box place(
        double pointerX,
        double pointerY,
        double width,
        double height,
        double canvasWidth,
        double canvasHeight) {
        double x = pointerX + 12;
        if (x + width > canvasWidth - 6) {
            x = pointerX - width - 12;
        }
        return new Box(
            Math.max(6, Math.min(canvasWidth - 6 - width, x)),
            Math.max(6, Math.min(canvasHeight - 6 - height, pointerY + 12)));
    }

    static List<String> wrap(List<String> input, int maximum) {
        List<String> result = new ArrayList<>();
        for (String entry : input) {
            for (String text : entry.split("\n", -1)) {
                StringBuilder line = new StringBuilder();
                int width = 0;
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (c == '\u00a7' && i + 1 < text.length()) {
                        line.append(c).append(text.charAt(++i));
                        continue;
                    }
                    int next = ArcMenuLayout.textWidth(String.valueOf(c));
                    if (width + next > maximum && width > 0) {
                        result.add(line.toString());
                        line.setLength(0);
                        width = 0;
                    }
                    line.append(c);
                    width += next;
                }
                result.add(line.toString());
            }
        }
        if (result.size() > 38) {
            result = new ArrayList<>(result.subList(0, 37));
            result.add("…");
        }
        return result;
    }

    static final class Box {

        final double x;

        final double y;

        private Box(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
