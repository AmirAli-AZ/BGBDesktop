package com.bgbrowser.bgbdesktop;

import com.bgbrowser.bgbdesktop.utils.ExtensionManager;
import com.bgbrowser.bgbdesktop.utils.ThemeManager;
import com.bgbrowser.extensionframework.Browser;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.nio.file.Path;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        syncIconsAndThemes();

        primaryStage.setTitle("BGB");
        var scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("app-view.fxml"))));

        var loadedTheme = ThemeManager.load();
        ThemeManager.setTheme(scene, loadedTheme);
        ThemeManager.save(loadedTheme);

        primaryStage.setScene(scene);
        primaryStage.show();

        var thread = new Thread(() -> {
            try {
                for (Path extension : ExtensionManager.getExtensions()) {
                    ExtensionManager.loadExtension(extension, new Browser(primaryStage));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void syncIconsAndThemes() {
        Window.getWindows().addListener((ListChangeListener<? super Window>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Window window : c.getAddedSubList()) {
                        if (window instanceof Stage stage) {
                            ThemeManager.setTheme(stage.getScene(), ThemeManager.load());
                            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icons/app_icon.png")).toExternalForm()));
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
