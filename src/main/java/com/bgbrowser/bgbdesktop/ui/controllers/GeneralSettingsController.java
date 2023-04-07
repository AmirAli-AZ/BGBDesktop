package com.bgbrowser.bgbdesktop.ui.controllers;

import com.bgbrowser.bgbdesktop.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeneralSettingsController implements Initializable {

    @FXML
    private ScrollPane root;

    @FXML
    private RadioButton customRadioButton;

    @FXML
    private TextField customTextField;

    @FXML
    private RadioButton googleRadioButton;

    @FXML
    private ToggleGroup searchEngineToggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSearchEngineSettings();
    }

    private void initSearchEngineSettings() {
        try {
            var name = ConfigManager.getProperty("searchEngineName");
            var searchEngine = ConfigManager.getProperty("searchEngine");

            if (searchEngine == null || searchEngine.isEmpty()) {
                searchEngine = "https://www.google.com/search?q=";
                name = "Google";
                ConfigManager.saveSearchEngine(new URL(searchEngine), name);
            }

            switch (name.toLowerCase()) {
                case "google" -> searchEngineToggleGroup.selectToggle(googleRadioButton);
                case "custom" -> {
                    searchEngineToggleGroup.selectToggle(customRadioButton);
                    customTextField.setText(searchEngine);
                }

                default -> searchEngineToggleGroup.selectToggle(googleRadioButton);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        searchEngineToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.equals(googleRadioButton)) {
                    ConfigManager.saveSearchEngine(new URL("https://www.google.com/search?q="), "Google");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        customTextField.setOnAction(actionEvent -> {
            try {
                if (!customTextField.getText().isEmpty())
                    ConfigManager.saveSearchEngine(new URL(customTextField.getText()), "Custom");
            }catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
