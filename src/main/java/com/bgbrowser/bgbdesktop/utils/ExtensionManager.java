package com.bgbrowser.bgbdesktop.utils;

import com.bgbrowser.extensionframework.BGBApplication;
import com.bgbrowser.extensionframework.Browser;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ExtensionManager {

    private static final List<Browser> browserInstances = new ArrayList<>();

    public static void installExtension(Path filePath) throws IOException {
        Files.copy(filePath, Paths.get(Environment.getExtensionsPath() + File.separator + filePath.getFileName()));
    }

    public static void deleteExtension(Path filePath) throws IOException {
        Files.deleteIfExists(filePath);
    }

    public static List<Path> getExtensions() throws IOException {
        try(var stream = Files.list(Environment.getExtensionsPath())){
            return stream.filter(path -> !Files.isDirectory(path) && path.toString().toLowerCase().endsWith(".jar"))
                    .collect(Collectors.toList());
        }
    }

    public static void loadExtension(Path path, Browser browser) throws Exception {
        try(var jarFile = new JarFile(path.toFile()); var classLoader = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()})){
            var entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }

                var className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace('/', '.');
                var clazz = classLoader.loadClass(className);
                var superClass = clazz.getSuperclass();

                if (superClass != null && superClass.equals(BGBApplication.class)) {
                    invokeInitialize(clazz, browser);
                }
            }
        }
    }

    private static void invokeInitialize(Class<?> clazz, Browser browser) throws Exception {
        var instance = clazz.getConstructor().newInstance();
        browserInstances.add(browser);
        var initialize = clazz.getMethod("initialize", Browser.class);
        Platform.runLater(() -> {
            try {
                initialize.invoke(instance, browser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static List<Browser> getBrowserInstances() {
        return browserInstances;
    }

    public static void setCurrentTab(Tab tab) throws Exception {
        for (Browser browser : getBrowserInstances()) {
            var setCurrentTab = browser.getClass().getDeclaredMethod("setCurrentTab", Tab.class);
            setCurrentTab.setAccessible(true);
            Platform.runLater(() -> {
                try {
                    setCurrentTab.invoke(browser, tab);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void setCurrentWebview(WebView webview) throws Exception {
        for (Browser browser : getBrowserInstances()) {
            var setCurrentWebview = browser.getClass().getDeclaredMethod("setCurrentTabWebview", WebView.class);
            setCurrentWebview.setAccessible(true);
            Platform.runLater(() -> {
                try {
                    setCurrentWebview.invoke(browser, webview);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
