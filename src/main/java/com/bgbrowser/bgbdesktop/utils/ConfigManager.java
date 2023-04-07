package com.bgbrowser.bgbdesktop.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class ConfigManager {


    public static void setTheme(Scene scene, Theme theme) {
        var stylesheets = scene.getStylesheets();

        if (stylesheets.isEmpty())
            stylesheets.add(theme.getPath());
        else
            stylesheets.set(0, theme.getPath());
    }

    public static void applyThemeToAllWindows(Theme theme) {
        var windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof Stage stage) {
                var scene = stage.getScene();
                setTheme(scene, theme);
            }
        }
    }

    public static Theme load() {
        var theme = Theme.LIGHT;

        try {
            var themeName = getProperty("theme", "light");
            if (themeName.equalsIgnoreCase("dark"))
                theme = Theme.DARK;
        }catch (IOException e) {
            e.printStackTrace();
        }

        return theme;
    }

    public static void save(Theme theme) throws IOException {
        var properties = new Properties();
        if (isConfigFileExists()) {
            var fis = new FileInputStream(getConfigFile().toFile());
            properties.load(fis);
            fis.close();
        }
        properties.setProperty("theme", theme.getName());
        var fos = new FileOutputStream(getConfigFile().toFile());
        properties.store(fos, "DO NOT EDIT");
        fos.close();
    }


    public static Path getConfigFile() {
        return Paths.get(Environment.getAppDataPath() + File.separator + "config.properties");
    }

    public static boolean isConfigFileExists() {
        return Files.exists(getConfigFile());
    }

    public static String getProperty(String key) throws IOException {
        Objects.requireNonNull(key);

        var properties = new Properties();
        var fis = new FileInputStream(getConfigFile().toFile());
        properties.load(fis);
        fis.close();

        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) throws IOException {
        var value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    public static void saveSearchEngine(URL url, String searchEngineName) throws IOException {
        var properties = new Properties();
        if (isConfigFileExists()) {
            var fis = new FileInputStream(getConfigFile().toFile());
            properties.load(fis);
            fis.close();
        }
        properties.setProperty("searchEngine", url.toString());
        properties.setProperty("searchEngineName", searchEngineName);
        var fos = new FileOutputStream(getConfigFile().toFile());
        properties.store(fos, "DO NOT EDIT");
        fos.close();
    }
}
