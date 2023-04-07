package com.bgbrowser.bgbdesktop.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsController implements Initializable {

    @FXML
    private SplitPane root;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private ToggleButton generalButton, extensionManagerButton;

    @FXML
    private VBox container;


    private final FXMLLoader
            generalSettingsLoader = new FXMLLoader(getClass().getResource("/com/bgbrowser/bgbdesktop/general-settings-view.fxml")),
            extensionManagerLoader = new FXMLLoader(getClass().getResource("/com/bgbrowser/bgbdesktop/extension-view.fxml"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            var generalSettingsRoot= generalSettingsLoader.load();
            var extensionManagerRoot = extensionManagerLoader.load();

            VBox.setVgrow((Node) generalSettingsRoot, Priority.ALWAYS);
            VBox.setVgrow((Node) extensionManagerRoot, Priority.ALWAYS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            container.getChildren().clear();

            if (newValue == null)
                return;

            if (newValue.equals(generalButton)) {
                container.getChildren().add(generalSettingsLoader.getRoot());
            }else if (newValue.equals(extensionManagerButton)) {
                container.getChildren().add(extensionManagerLoader.getRoot());
            }
        });

        toggleGroup.selectToggle(generalButton);
    }
}
