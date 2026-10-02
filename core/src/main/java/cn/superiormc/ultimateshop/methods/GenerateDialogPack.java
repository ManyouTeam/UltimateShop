package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogPackUsage;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogTemplate;
import cn.superiormc.ultimateshop.objects.dialog.DialogFontUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public class GenerateDialogPack {

    public static void generate(Path file, Collection<ObjectDialogTemplate> templates, int format) throws IOException {
        generate(file, templates, format, null);
    }

    public static void generate(Path file, Collection<ObjectDialogTemplate> templates, int format, Path textures) throws IOException {
        generate(file, templates, format, textures, Set.of());
    }

    public static void generate(Path folder, Collection<ObjectDialogTemplate> templates, int format, Path textures,
                                Collection<String> icons) throws IOException {
        generateFolder(folder, templates, format, textures, icons, () -> true);
    }

    public static boolean generateFolder(Path folder, Collection<ObjectDialogTemplate> templates, int format, Path textures,
                                         Collection<String> icons, BooleanSupplier current) throws IOException {
        return generateFolder(folder, templates, format, textures, icons, current,
                List.of(0));
    }

    public static boolean generateFolder(Path folder, Collection<ObjectDialogTemplate> templates, int format, Path textures,
                                         Collection<String> icons, BooleanSupplier current,
                                         Collection<Integer> backgroundOffsets) throws IOException {
        ObjectDialogPackUsage usage = new ObjectDialogPackUsage();
        for (ObjectDialogTemplate template : templates) {
            for (String icon : icons) {
                usage.addIcon(template, icon, backgroundOffsets);
            }
            for (boolean product : new boolean[]{false, true}) {
                for (boolean enabled : new boolean[]{false, true}) {
                    usage.addBackground(template, product, enabled, backgroundOffsets);
                }
            }
        }
        return generateFolder(folder, usage, format, textures, current);
    }

    public static boolean generateFolder(Path folder, ObjectDialogPackUsage usage, int format, Path textures,
                                         BooleanSupplier current) throws IOException {
        if (format < 69 || format > 10000) {
            throw new IllegalArgumentException("Resource format must be in 69..10000");
        }
        Path output = folder.toAbsolutePath().normalize();
        Path parent = output.getParent();
        Files.createDirectories(parent);
        Path staging = Files.createTempDirectory(parent, ".ore-pack-build-");
        Path backup = null;
        try {
            JSONObject pack = new JSONObject().put("description", "UltimateShop Ore Dialog")
                    .put("min_format", format).put("max_format", format);
            write(staging, "pack.mcmeta", new JSONObject().put("pack", pack).toString(2).getBytes(StandardCharsets.UTF_8));
            JSONObject advances = new JSONObject();
            int power = 1;
            for (int place = 0; place < 4; place++, power *= 10) {
                for (int digit = 1; digit < 10; digit++) {
                    advances.put(Character.toString(DialogFontUtil.getSpaceCharacter(place, digit, false)), power * digit);
                    advances.put(Character.toString(DialogFontUtil.getSpaceCharacter(place, digit, true)), -power * digit);
                }
            }
            advances.put(Character.toString(0xf1080), 0.5);
            advances.put(Character.toString(0xf1081), -0.5);
            writeJson(staging, "assets/ultimateshop/font/ore/spacing.json", new JSONObject().put("type", "space").put("advances", advances));
            for (ObjectDialogPackUsage.Icon icon : usage.getIcons()) {
                writeIcon(staging, icon.getTemplate(), icon.getTexture(), textures, icon.getPositions());
            }
            for (ObjectDialogTemplate template : usage.getTemplates().values()) {
                for (boolean product : new boolean[]{false, true}) {
                    for (boolean enabled : new boolean[]{false, true}) {
                        String id = template.getResourceId() + (product ? "_product" : "_button") + (enabled ? "_enabled" : "_disabled");
                        Collection<Integer> positions = usage.getBackgrounds().get(id);
                        if (positions != null && !positions.isEmpty()) {
                            writeTemplate(staging, template, product, enabled, false, textures, positions);
                            if (!product && enabled) {
                                writeTemplate(staging, template, false, true, true, textures, positions);
                            }
                        }
                    }
                }
            }
            if (!current.getAsBoolean()) {
                return false;
            }
            if (Files.exists(output)) {
                backup = Files.createTempDirectory(parent, ".ore-pack-backup-");
                Files.delete(backup);
                moveDirectory(output, backup);
            }
            try {
                moveDirectory(staging, output);
            } catch (IOException exception) {
                if (backup != null) {
                    moveDirectory(backup, output);
                    backup = null;
                }
                throw exception;
            }
            return true;
        } finally {
            deleteGeneratedDirectory(staging, parent);
            if (backup != null) {
                deleteGeneratedDirectory(backup, parent);
            }
        }
    }

    private static void moveDirectory(Path source, Path destination) throws IOException {
        for (int attempt = 0; ; attempt++) {
            try {
                Files.move(source, destination);
                return;
            } catch (AccessDeniedException exception) {
                if (attempt == 3) {
                    throw exception;
                }
                try {
                    Thread.sleep(100L * (attempt + 1));
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while publishing Ore resource pack", interrupted);
                }
            }
        }
    }

    private static void deleteGeneratedDirectory(Path folder, Path parent) throws IOException {
        Path resolved = folder.toAbsolutePath().normalize();
        if (!resolved.startsWith(parent) || resolved.equals(parent)
                || !(resolved.getFileName().toString().startsWith(".ore-pack-build-")
                || resolved.getFileName().toString().startsWith(".ore-pack-backup-"))) {
            throw new IOException("Refusing to clean an unexpected generated folder: " + resolved);
        }
        if (Files.exists(resolved)) {
            try (Stream<Path> files = Files.walk(resolved)) {
                for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }

    private static void writeIcon(Path output, ObjectDialogTemplate template, String texture, Path textures,
                                  Collection<Integer> backgroundOffsets) throws IOException {
        if (textures == null || !texture.matches("[a-z0-9_.-]+:[a-z0-9_./-]+") || texture.contains("..")) {
            throw new IOException("Invalid Ore icon texture: " + texture);
        }
        Path base = textures.toRealPath();
        Path source = base.resolve(DialogFontUtil.getIconId(texture) + ".png").toRealPath();
        if (!source.startsWith(base) || Files.size(source) > 16 * 1024 * 1024) {
            throw new IOException("Invalid or oversized Ore icon texture: " + texture);
        }
        BufferedImage original = ImageIO.read(source.toFile());
        if (original == null || original.getWidth() > 4096 || original.getHeight() > 4096) {
            throw new IOException("Invalid Ore icon PNG: " + texture);
        }
        int size = template.getIconSize();
        BufferedImage icon = new BufferedImage(size + 1, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = icon.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(original, 0, 0, size, size, 0, 0, original.getWidth(),
                Math.min(original.getWidth(), original.getHeight()), null);
        graphics.dispose();
        icon.setRGB(size, size - 1, 0xff000000 | template.getBackground());
        String id = DialogFontUtil.getIconFontId(texture, size, template.getBackground(), template.getLoreRow());
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(icon, "png", data);
        write(output, "assets/ultimateshop/textures/" + id + ".png", data.toByteArray());
        JSONArray providers = new JSONArray();
        for (int y : new TreeSet<>(backgroundOffsets)) {
            providers.put(new JSONObject().put("type", "bitmap").put("file", "ultimateshop:" + id + ".png")
                    .put("height", size).put("ascent", 7 - 9 - y - template.getLoreRow() * 9)
                    .put("chars", new JSONArray().put(DialogFontUtil.getBackgroundCharacter(y, 0, 0))));
        }
        write(output, "assets/ultimateshop/font/" + id + ".json",
                new JSONObject().put("providers", providers).toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void writeTemplate(Path output, ObjectDialogTemplate template,
                                      boolean product, boolean enabled, boolean selected, Path textures, Collection<Integer> backgroundOffsets) throws IOException {
        String id = template.getResourceId() + (product ? "_product" : "_button") + (enabled ? "_enabled" : "_disabled")
                + (selected ? "_selected" : "");
        String texture = selected ? template.getSelectedTexture() : template.getTexture();
        int width = template.getWidth() - 1;
        BufferedImage image = new BufferedImage(width, template.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < width; x++) {
                int color = selected ? template.getSelectedBackground() : template.getBackground();
                if (product && y >= template.getDetailsRow() * 9) {
                    color = template.getFooter();
                }
                if (y == 0 || x == width - 1) {
                    color = selected ? template.getSelectedBorder() : template.getBorder();
                }
                if (x < 3) {
                    color = selected ? template.getSelectedBorder() : template.getAccent();
                }
                if (y >= image.getHeight() - 3) {
                    color = 0x141820;
                }
                if (!enabled && texture.isEmpty()) {
                    color = ((color & 0xfefefe) >> 1);
                }
                image.setRGB(x, y, 0xff000000 | color);
            }
        }
        if (!texture.isEmpty()) {
            if (textures == null) {
                throw new IOException("No textures folder was supplied for " + texture);
            }
            Path base = textures.toRealPath();
            Path source = base.resolve(texture).toRealPath();
            if (!source.startsWith(base) || Files.size(source) > 16 * 1024 * 1024) {
                throw new IOException("Invalid or oversized Dialog texture: " + texture);
            }
            BufferedImage custom = ImageIO.read(source.toFile());
            if (custom == null || custom.getWidth() > 4096 || custom.getHeight() > 4096) {
                throw new IOException("Invalid Dialog PNG: " + texture);
            }
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.drawImage(custom, 0, 0, width, template.getHeight(), null);
            graphics.dispose();
            if (!enabled) {
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < width; x++) {
                        image.setRGB(x, y, 0xff000000 | ((image.getRGB(x, y) & 0xfefefe) >> 1));
                    }
                }
            }
        }
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        write(output, "assets/ultimateshop/textures/" + id + ".png", data.toByteArray());
        JSONArray backgrounds = new JSONArray();
        for (int top = 0, verticalTile = 0; top < image.getHeight(); top += DialogFontUtil.BACKGROUND_TILE_HEIGHT, verticalTile++) {
            int tileHeight = Math.min(DialogFontUtil.BACKGROUND_TILE_HEIGHT, image.getHeight() - top);
            for (int tile = 0; tile < DialogFontUtil.getTileCount(template.getWidth()); tile++) {
                int left = tile * DialogFontUtil.TILE_WIDTH;
                int tileWidth = Math.min(DialogFontUtil.TILE_WIDTH, width - left);
                BufferedImage part = image.getSubimage(left, top, tileWidth, tileHeight);
                String textureId = id + "_background_" + verticalTile + "_" + tile;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(part, "png", bytes);
                write(output, "assets/ultimateshop/textures/" + textureId + ".png", bytes.toByteArray());
                for (int y : new TreeSet<>(backgroundOffsets)) {
                    backgrounds.put(new JSONObject().put("type", "bitmap").put("file", "ultimateshop:" + textureId + ".png")
                            .put("height", tileHeight).put("ascent", 7 - 9 - y - top)
                            .put("chars", new JSONArray().put(DialogFontUtil.getBackgroundCharacter(y, verticalTile, tile))));
                }
            }
        }
        write(output, "assets/ultimateshop/font/" + id + "_background.json",
                new JSONObject().put("providers", backgrounds).toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void writeJson(Path output, String path, JSONObject provider) throws IOException {
        write(output, path, new JSONObject().put("providers", new JSONArray().put(provider)).toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void write(Path output, String path, byte[] data) throws IOException {
        Path destination = output.resolve(path).normalize();
        if (!destination.startsWith(output)) {
            throw new IOException("Invalid resource-pack path: " + path);
        }
        Files.createDirectories(destination.getParent());
        Files.write(destination, data);
    }
}
