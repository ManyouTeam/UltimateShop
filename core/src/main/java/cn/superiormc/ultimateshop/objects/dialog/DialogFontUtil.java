package cn.superiormc.ultimateshop.objects.dialog;

public class DialogFontUtil {

    public static final int TILE_WIDTH = 128;

    public static final int BACKGROUND_TILE_HEIGHT = 126;

    public static String getBackgroundCharacter(int y, int verticalTile, int tile) {
        if (y < 0 || y > 8190 || y % 9 != 0 || verticalTile < 0 || verticalTile >= 8 || tile < 0 || tile >= 8) {
            throw new IllegalArgumentException("Invalid Ore background position");
        }
        return Character.toString(0x100000 + y / 9 * 64 + verticalTile * 8 + tile);
    }

    public static String getIconTexture(String sprite) {
        if (sprite == null) {
            return null;
        }
        java.util.regex.Matcher match = java.util.regex.Pattern.compile(
                "<sprite:\"([a-z0-9_.-]+):[a-z0-9_/-]+\":([a-z0-9_.:/-]+)>").matcher(sprite);
        if (!match.find()) {
            return null;
        }
        String texture = match.group(2);
        if (!texture.contains(":")) {
            texture = match.group(1) + ":" + texture;
        }
        return texture.contains("..") ? null : texture;
    }

    public static String getIconId(String texture) {
        return "ore/icons/" + texture.replace(':', '/');
    }

    public static String getIconFontId(String texture, int size, int background, int loreRow) {
        return getIconId(texture) + "_" + size + "_" + Integer.toHexString(background) + "_" + loreRow + "_anchored";
    }

    public static String getIconCharacter(int row) {
        return Character.toString(0xf2000 + row);
    }

    public static int getTileCount(int width) {
        return (width - 2) / TILE_WIDTH + 1;
    }

    public static int getSpaceCharacter(int decimalPlace, int digit, boolean negative) {
        return 0xf1000 + decimalPlace * 20 + (negative ? 10 : 0) + digit;
    }

    public static String getSpace(int pixels) {
        if (Math.abs(pixels) > 1999) {
            throw new IllegalArgumentException("Ore Dialog spacing exceeds 1999 pixels");
        }
        StringBuilder result = new StringBuilder();
        int remaining = Math.abs(pixels);
        for (int place = 0; remaining > 0; place++) {
            int digit = remaining % 10;
            if (digit != 0) {
                result.appendCodePoint(getSpaceCharacter(place, digit, pixels < 0));
            }
            remaining /= 10;
        }
        return result.toString();
    }

    public static String getSpace(double pixels) {
        if (!Double.isFinite(pixels) || Math.abs(pixels) > 1999) {
            throw new IllegalArgumentException("Ore Dialog spacing exceeds 1999 pixels");
        }
        int halves = (int) Math.round(pixels * 2);
        return getSpace(halves / 2) + (halves % 2 == 0 ? "" : Character.toString(halves > 0 ? 0xf1080 : 0xf1081));
    }

    public static String getRowCharacter(int row) {
        return getRowCharacter(row, 0);
    }

    public static String getRowCharacter(int row, int tile) {
        return Character.toString(0xf0000 + row * 8 + tile);
    }
}
