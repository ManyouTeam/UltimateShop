package cn.superiormc.ultimateshop.database.sql;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.DriverShim;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Driver;
import java.sql.DriverManager;

public abstract class DatabaseDialect {

    public abstract boolean matches(String jdbcUrl);

    public abstract String dateTimeType();

    public abstract int maxPoolSize();

    public abstract int minIdle();

    public abstract String createUseTimesTable();

    public abstract String createRandomPlaceholderTable();

    public abstract String upsertUseTimes();

    public abstract String upsertRandomPlaceholder();

    /** 是否支持批量写入 */
    public boolean supportBatch() {
        return true;
    }

    /** 是否强制单连接（SQLite） */
    public boolean forceSingleConnection() {
        return false;
    }

    public abstract void needExtraDownload(String jdbcUrl);

    public void loadDriver(String driverName, String mavenUrl, String driverClassName) {
        try {
            Path libPath = Paths.get("plugins/UltimateShop/libs/");
            if (!Files.exists(libPath)) Files.createDirectories(libPath);

            String jarName = driverName + ".jar";
            Path jarPath = libPath.resolve(jarName);

            // 如果本地不存在，则下载
            if (!Files.exists(jarPath)) {
                TextUtil.sendMessage(
                        null,
                        TextUtil.pluginPrefix() + " §fDownloading " + jarName + " ...");
                try (InputStream in = new URL(mavenUrl).openStream()) {
                    Files.copy(in, jarPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            URL jarUrl = jarPath.toUri().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, UltimateShop.instance.getClass().getClassLoader());

            // 注册 Driver
            Class<?> driverClass = Class.forName(driverClassName, true, loader);
            Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverShim(driverInstance));

            TextUtil.sendMessage(
                    null,
                    TextUtil.pluginPrefix() + " §f" + driverName + " loaded!");
        } catch (Throwable e) {
            TextUtil.sendMessage(
                    null,
                    TextUtil.pluginPrefix() + " §fFailed to load " + driverName + "!");
            e.printStackTrace();
        }
    }
}