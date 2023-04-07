package com.bgbrowser.bgbdesktop.ui.controllers;

import com.bgbrowser.bgbdesktop.utils.Theme;
import com.bgbrowser.bgbdesktop.utils.ConfigManager;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class TabController {

    private VBox root;

    private TextField searchTextField;

    private WebView webview;

    private ProgressBar progressBar;

    private WebEngine webEngine;

    private Tab tab;

    public TabController(Tab tab) {
        this.tab = tab;
        initGraphics();
        init();
        this.tab.setContent(root);
    }

    private void initGraphics() {
        root = new VBox();

        var newTabButton = new Button();
        newTabButton.setPrefSize(30, 30);
        newTabButton.setOnAction(newTab);
        newTabButton.getStyleClass().add("icon-button");
        var plusIcon = new FontIcon(FontAwesome.PLUS);
        newTabButton.setGraphic(plusIcon);

        var backButton = new Button();
        backButton.setPrefSize(30, 30);
        backButton.setOnAction(back);
        backButton.getStyleClass().add("icon-button");
        var backIcon = new FontIcon(FontAwesome.ARROW_LEFT);
        backButton.setGraphic(backIcon);

        var forwardButton = new Button();
        forwardButton.setPrefSize(30, 30);
        forwardButton.setOnAction(forward);
        forwardButton.getStyleClass().add("icon-button");
        var forwardIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
        forwardButton.setGraphic(forwardIcon);

        var refreshButton = new Button();
        refreshButton.setPrefSize(30, 30);
        refreshButton.setOnAction(refresh);
        refreshButton.getStyleClass().add("icon-button");
        var refreshIcon = new FontIcon(FontAwesome.REFRESH);
        refreshButton.setGraphic(refreshIcon);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search or type url");
        searchTextField.setFont(Font.font(14));
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        HBox.setMargin(searchTextField, new Insets(5));

        var menuButton = new Button();
        menuButton.setPrefSize(30, 30);
        menuButton.getStyleClass().add("icon-button");
        var menuIcon = new FontIcon(FontAwesome.BARS);
        menuButton.setGraphic(menuIcon);

        var contextMenu = createMenu();
        menuButton.setOnMouseClicked(mouseEvent -> contextMenu.show(menuButton, Side.BOTTOM, 0, 0));

        var hbox = new HBox(3, backButton, forwardButton, refreshButton, newTabButton, searchTextField, menuButton);
        hbox.setPadding(new Insets(0, 5, 0, 5));
        hbox.setAlignment(Pos.CENTER);

        progressBar = new ProgressBar();
        progressBar.getStyleClass().add("browser-progress-bar");
        progressBar.setMinHeight(20);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        webview = new WebView();
        VBox.setVgrow(webview, Priority.ALWAYS);

        root.getChildren().addAll(hbox, progressBar, webview);
    }

    public void init() {
        webEngine = webview.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                searchTextField.setText(webEngine.getLocation());
                if (tab != null)
                    tab.setText(webEngine.getTitle());
            }
        });

        webEngine.setConfirmHandler(param -> {
            var confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, param);
            var result = confirmAlert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        });

        webEngine.setOnAlert(event -> {
            var alert = new Alert(Alert.AlertType.INFORMATION, event.getData());
            alert.setTitle("Alert");
            alert.show();
        });

        webEngine.setPromptHandler(param -> {
            var textInputDialog = new TextInputDialog(param.getDefaultValue());
            textInputDialog.setHeaderText(param.getMessage());
            var result = textInputDialog.showAndWait();
            return result.orElse(null);
        });

        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        progressBar.visibleProperty().bind(
                progressBar.progressProperty().greaterThan(0).and(progressBar.progressProperty().lessThan(1))
        );

        searchTextField.setOnAction(actionEvent -> {
            if (isValidURL(searchTextField.getText())) {
                webEngine.load(searchTextField.getText());
            }else {
                try {
                    var searchEngine = ConfigManager.getProperty("searchEngine");
                    if (searchEngine == null || searchEngine.isEmpty()) {
                        searchEngine = "https://www.google.com/search?q=";
                        ConfigManager.saveSearchEngine(new URL(searchEngine), "Google");
                    }
                    webEngine.load(searchEngine + searchTextField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private final EventHandler<ActionEvent> back = actionEvent -> {
        var history = webEngine.getHistory();
        history.go(
                history.getEntries().size() > 1 && history.getCurrentIndex() > 0 ? -1 : 0
        );
    };

    private final EventHandler<ActionEvent> forward = actionEvent -> {
        var history = webEngine.getHistory();
        history.go(
                history.getEntries().size() > 1 && history.getCurrentIndex() < history.getEntries().size() - 1  ? 1 : 0
        );
    };

    private final EventHandler<ActionEvent> newTab = actionEvent -> {
        if (tab == null)
            return;

        var tabPane = tab.getTabPane();
        var newTab = new Tab("New Tab");
        var controller = new TabController(newTab);
        newTab.setUserData(controller);

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    };

    private ContextMenu createMenu() {
        var extensionManagerMenuItem = new MenuItem("Extension Manager");
        extensionManagerMenuItem.setOnAction(actionEvent -> {
            try {
                var stage = new Stage();
                stage.setTitle("ExtensionManager");
                stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/bgbrowser/bgbdesktop/extension-view.fxml")))));
                stage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
        });

        var darkThemeCheckMenuItem = new CheckMenuItem("Dark Theme");
        darkThemeCheckMenuItem.setSelected(ConfigManager.load() == Theme.DARK);
        darkThemeCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            var theme = newValue ? Theme.DARK : Theme.LIGHT;
            ConfigManager.applyThemeToAllWindows(theme);
            try {
                ConfigManager.save(theme);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        var label = new Label("Zoom");
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        var zoomPercent = new Label("100%");

        var zoomInButton = new Button();
        zoomInButton.setStyle("-fx-background-color: transparent;");
        var plusIcon = new FontIcon(FontAwesome.PLUS);
        zoomInButton.setGraphic(plusIcon);
        zoomInButton.setOnAction(actionEvent -> {
            var zoom = zoom(0.10);
            zoomPercent.setText(zoom);
        });

        var zoomOutButton = new Button();
        zoomOutButton.setStyle("-fx-background-color: transparent;");
        var minusIcon = new FontIcon(FontAwesome.MINUS);
        zoomOutButton.setGraphic(minusIcon);
        zoomOutButton.setOnAction(actionEvent -> {
            var zoom = zoom(-0.10);
            zoomPercent.setText(zoom);
        });

        root.setOnKeyPressed(keyEvent -> {
            if (new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                var zoom = zoom(0.10);
                zoomPercent.setText(zoom);
            }else if (new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                var zoom = zoom(-0.10);
                zoomPercent.setText(zoom);
            }
        });

        var customContainer = new HBox(label, zoomInButton, zoomPercent, zoomOutButton);
        customContainer.setAlignment(Pos.CENTER_LEFT);

        var zoomMenuItem = new CustomMenuItem(customContainer, false);

        var settingsMenuItem = new MenuItem("Settings");
        settingsMenuItem.setOnAction(actionEvent -> {
            if (tab == null)
                return;

            var tabPane = tab.getTabPane();
            var settingsTab = new Tab("Settings");
            var loader = new FXMLLoader(getClass().getResource("/com/bgbrowser/bgbdesktop/settings-view.fxml"));
            try {
                settingsTab.setContent(loader.load());
            }catch (IOException e) {
                e.printStackTrace();
            }

            tabPane.getTabs().add(settingsTab);
            tabPane.getSelectionModel().select(settingsTab);
        });

        return new ContextMenu(
                extensionManagerMenuItem, darkThemeCheckMenuItem, zoomMenuItem, settingsMenuItem
        );
    }

    public String zoom(double zoom) {
        webview.setZoom(webview.getZoom() + zoom);
        return ((int) Math.floor(webview.getZoom() * 100)) + "%";
    }

    private final EventHandler<ActionEvent> refresh = actionEvent -> {
        webEngine.reload();
    };

    private boolean isValidURL(String url) {
        try {
            new URL(url);
        }catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public Parent getRoot() {
        return root;
    }

    public WebView getWebview() {
        return webview;
    }
}
