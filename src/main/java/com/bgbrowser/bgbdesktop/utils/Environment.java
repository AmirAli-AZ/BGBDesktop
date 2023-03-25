package com.bgbrowser.bgbdesktop.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Environment {

    private static Path appData, extensions;

    static {
        appData = Paths.get(System.getProperty("user.home") + File.separator + "BGB");
        extensions = Paths.get(appData + File.separator + "extensions");
    }

    public static Path getAppDataPath() {
        try {
            Files.createDirectories(appData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appData;
    }

    public static Path getExtensionsPath() {
        try {
            Files.createDirectories(extensions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extensions;
    }
}
