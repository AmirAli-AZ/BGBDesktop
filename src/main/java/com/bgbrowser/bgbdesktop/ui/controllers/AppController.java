package com.bgbrowser.bgbdesktop.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.bgbrowser.bgbdesktop.utils.ExtensionManager;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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
        newTab("Home");

        root.setOnKeyPressed(keyEvent -> {
            if (new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                newTab("New Tab");
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

        tabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            while (c.next()) {
                if (c.getList().isEmpty())
                    System.exit(0);
            }
        });
    }

    private void newTab(String text) {
        var newTab = new Tab(text);
        var controller = new TabController(newTab);
        newTab.setContent(controller.getRoot());
        newTab.setUserData(controller);

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

}