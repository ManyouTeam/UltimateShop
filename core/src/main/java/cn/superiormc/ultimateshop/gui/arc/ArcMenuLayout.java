package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.managers.ErrorManager;

import cn.superiormc.ultimateshop.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

final class ArcMenuLayout {

    // BEGIN ARCMENU SHARED layout-ArcMenuLayout
    private ArcMenuLayout() {

    }
    // END ARCMENU SHARED layout-ArcMenuLayout


    // BEGIN ARCMENU SHARED layout-normalizeAngle
    static double normalizeAngle(double angle) {
        double circle = Math.PI * 2;
        return (angle % circle + circle) % circle;
    }
    // END ARCMENU SHARED layout-normalizeAngle


    // BEGIN ARCMENU SHARED layout-sectorAt
    static int sectorAt(double angle, double[] boundaries) {
        if (!Double.isFinite(angle) || boundaries.length < 2) {
            return -1;
        }
        double normalized = normalizeAngle(angle);
        for (int i = 0; i < boundaries.length - 1; i++) {
            if (normalized >= boundaries[i] && normalized < boundaries[i + 1]) {
                return i;
            }
        }
        return -1;
    }
    // END ARCMENU SHARED layout-sectorAt


    // BEGIN ARCMENU SHARED layout-spinTarget
    static double spinTarget(double current, double target, int turns) {
        return current + turns * Math.PI * 2 + normalizeAngle(target - current);
    }
    // END ARCMENU SHARED layout-spinTarget


    // BEGIN ARCMENU SHARED layout-spinProgress
    static double spinProgress(double progress) {
        double t = Math.max(0, Math.min(1, progress));
        return t * t * t * (10 + t * (-15 + 6 * t));
    }
    // END ARCMENU SHARED layout-spinProgress


    // BEGIN ARCMENU SHARED layout-contains
    static boolean contains(
        double x, double y, double left, double top, double width, double height) {
        return Double.isFinite(x)
            && Double.isFinite(y)
            && x >= left
            && x < left + width
            && y >= top
            && y < top + height;
    }
    // END ARCMENU SHARED layout-contains


    // BEGIN ARCMENU SHARED layout-clampAmount
    static int clampAmount(long amount, int maximum) {
        return (int) Math.max(1L, Math.min(Math.max(1, maximum), amount));
    }
    // END ARCMENU SHARED layout-clampAmount


    // BEGIN ARCMENU SHARED layout-easeOut
    static float easeOut(float value) {
        float t = Math.max(0, Math.min(1, value));
        return 1 - (1 - t) * (1 - t) * (1 - t);
    }
    // END ARCMENU SHARED layout-easeOut


    private static int glyphWidth(int code) {
        if (code == ' ') {
            return 4;
        }
        if (code < 128 && "!.,:;i|'".indexOf(code) >= 0) {
            return 2;
        }
        if (code == '`' || code == 'l') {
            return 3;
        }
        if (code < 128 && "\"()*I[]t{}".indexOf(code) >= 0) {
            return 4;
        }
        if (code < 128 && "<>fk".indexOf(code) >= 0) {
            return 5;
        }
        if (code == '@' || code == '~') {
            return 7;
        }
        return code < 256 ? 6 : 9;
    }

    // BEGIN ARCMENU SHARED layout-blend
    static int blend(int start, int end, float progress) {
        float t = Math.max(0, Math.min(1, progress));
        int result = 0;
        for (int shift = 0; shift <= 24; shift += 8) {
            int a = start >> shift & 255;
            int b = end >> shift & 255;
            result |= Math.round(a + (b - a) * t) << shift;
        }
        return result;
    }
    // END ARCMENU SHARED layout-blend


    // BEGIN ARCMENU SHARED layout-compositeColor
    static int compositeColor(int foreground, int background) {
        int foregroundAlpha = foreground >>> 24;
        int backgroundAlpha = background >>> 24;
        int remaining = 255 - foregroundAlpha;
        int alpha = foregroundAlpha * 255 + backgroundAlpha * remaining;
        if (alpha == 0) {
            return 0;
        }
        int result = ((alpha + 127) / 255) << 24;
        for (int shift = 0; shift <= 16; shift += 8) {
            int channel = ((foreground >> shift & 255) * foregroundAlpha * 255
                + (background >> shift & 255) * backgroundAlpha * remaining + alpha / 2) / alpha;
            result |= channel << shift;
        }
        return result;
    }
    // END ARCMENU SHARED layout-compositeColor


    // BEGIN ARCMENU SHARED layout-canvasScale
    static double canvasScale(double canvasWidth, double canvasHeight, double width, double height,
                              double pixelsPerBlock, double screenScale) {
        double fit = Math.min(canvasWidth / width, canvasHeight / height);
        return fit * Math.min(1, pixelsPerBlock / designPixelsPerBlock(screenScale));
    }
    // END ARCMENU SHARED layout-canvasScale


    // BEGIN ARCMENU SHARED layout-ellipsize
    static String ellipsize(String value, int maximum) {
        if (textWidth(value) <= maximum) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length();) {
            if (value.charAt(i) == '§' && i + 1 < value.length()) {
                result.append(value, i, i + 2);
                i += 2;
                continue;
            }
            int code = value.codePointAt(i);
            String next = new String(Character.toChars(code));
            if (textWidth(result.toString() + next + "…") > maximum) {
                break;
            }
            result.append(next);
            i += Character.charCount(code);
        }
        return result.append('…').toString();
    }
    // END ARCMENU SHARED layout-ellipsize


    // BEGIN ARCMENU SHARED layout-designPixelsPerBlock
    static double designPixelsPerBlock(double screenScale) {
        double scale = Double.isFinite(screenScale) ? Math.max(.75, Math.min(2, screenScale)) : 1.5;
        return 180 / scale;
    }
    // END ARCMENU SHARED layout-designPixelsPerBlock


    // BEGIN ARCMENU SHARED layout-overlayScale
    static double overlayScale(double depth, double pixelsPerBlock, double distance) {
        return Math.max(.1, 1 - worldUnits(depth, pixelsPerBlock) / distance);
    }
    // END ARCMENU SHARED layout-overlayScale


    // BEGIN ARCMENU SHARED layout-worldUnits
    static double worldUnits(double pixels, double pixelsPerBlock) {
        if (!Double.isFinite(pixelsPerBlock) || pixelsPerBlock <= 0) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cError: ArcMenu surface pixels-per-block must be finite and positive");
            return pixels / 180;
        }
        return pixels / pixelsPerBlock;
    }
    // END ARCMENU SHARED layout-worldUnits


    // BEGIN ARCMENU SHARED layout-textScale
    static float textScale(double pixelSize, double pixelsPerBlock) {
        return (float) worldUnits(pixelSize / .025, pixelsPerBlock);
    }
    // END ARCMENU SHARED layout-textScale


    // BEGIN ARCMENU SHARED layout-panelScaleX
    static float panelScaleX(double width, double pixelsPerBlock) {
        return (float) worldUnits(width * 8, pixelsPerBlock);
    }
    // END ARCMENU SHARED layout-panelScaleX


    // BEGIN ARCMENU SHARED layout-panelScaleY
    static float panelScaleY(double height, double pixelsPerBlock) {
        return (float) worldUnits(height * 4, pixelsPerBlock);
    }
    // END ARCMENU SHARED layout-panelScaleY


    static final double TOOLTIP_DEPTH = 80;

    static final double ACTIONS_DEPTH = 60;

    // BEGIN ARCMENU SHARED layout-cursorSafeDepth
    static double cursorSafeDepth(double depth, double cursorDepth, double canvasScale) {
        return depth - (TOOLTIP_DEPTH + 4) + (cursorDepth - 8) / canvasScale;
    }
    // END ARCMENU SHARED layout-cursorSafeDepth


    // BEGIN ARCMENU SHARED layout-textAnchorY-1
    static double textAnchorY(double y, double height, double fontSize) {
    // BEGIN ARCMENU SHARED layout-textAnchorY
        return textAnchorY(y, height, fontSize, 8);
    }
    // END ARCMENU SHARED layout-textAnchorY-1


    // BEGIN ARCMENU SHARED layout-textAnchorY-2
    static double textAnchorY(double y, double height, double fontSize, double padding) {
        return y + height - padding - 4 * fontSize;
    }
    // END ARCMENU SHARED layout-textAnchorY-2

    // END ARCMENU SHARED layout-textAnchorY


    // BEGIN ARCMENU SHARED layout-textCenterY
    static double textCenterY(double y, double height, double fontSize) {
    // BEGIN ARCMENU SHARED layout-textAnchorY
        return textAnchorY(y, height, fontSize, (height - 9 * fontSize) / 2);
    }
    // END ARCMENU SHARED layout-textCenterY


    static double[] place(double pointerX, double pointerY, double width, double height) {
    // BEGIN ARCMENU SHARED layout-place
        return place(pointerX, pointerY, width, height, 640, 360);
    }
    // END ARCMENU SHARED layout-textAnchorY


    static double[] place(
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
        return new double[] {
            Math.max(6, Math.min(canvasWidth - 6 - width, x)),
            Math.max(6, Math.min(canvasHeight - 6 - height, pointerY + 12))
        };
    }
    // END ARCMENU SHARED layout-place


    static int textWidth(String value) {
        return TextUtil.getTextWidth(value);
    }

    static List<String> wrap(List<String> input, int maximum) {
        List<String> result = new ArrayList<>();
        for (String entry : input) {
            result.addAll(TextUtil.wrapByWidth(entry, maximum));
        }
        return result;
    }
}
