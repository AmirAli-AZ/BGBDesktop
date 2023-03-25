package com.bgbrowser.bgbdesktop.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.bgbrowser.bgbdesktop.utils.ExtensionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

public class AppController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setOnKeyPressed(keyEvent -> {
            if (new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                var newTab = new Tab("New Tab");
                var controller = new TabController(newTab);
                newTab.setContent(controller.getRoot());
                newTab.setUserData(controller);
                tabPane.getTabs().add(newTab);
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;
            var thread = new Thread(() -> {
                var controller = ((TabController) newValue.getUserData());
                try {
                    ExtensionManager.setCurrentTab(newValue);
                    ExtensionManager.setCurrentWebview(controller.getWebview());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });
    }

}