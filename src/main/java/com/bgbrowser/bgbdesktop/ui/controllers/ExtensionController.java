package com.bgbrowser.bgbdesktop.ui.controllers;

import com.bgbrowser.bgbdesktop.ui.ExtensionListCell;
import com.bgbrowser.bgbdesktop.utils.ExtensionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class ExtensionController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private ListView<Path> listview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listview.setCellFactory(param -> new ExtensionListCell());
        var thread = new Thread(() -> {
            try {
                for (Path extension : ExtensionManager.getExtensions()) {
                    Platform.runLater(() -> listview.getItems().add(extension));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    void addExtension(ActionEvent actionEvent) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Choose extension");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar File", "*.jar"));
        var file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            try {
                ExtensionManager.installExtension(file.toPath());
                listview.getItems().add(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
