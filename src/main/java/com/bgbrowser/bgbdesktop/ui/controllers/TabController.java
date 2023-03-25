package com.bgbrowser.bgbdesktop.ui.controllers;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
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
import java.util.Objects;

public class TabController {

    private VBox root;

    private TextField searchTextField;

    private WebView webview;

    private ProgressBar progressBar;

    private WebEngine webEngine;

    private final Tab tab;

    public TabController(Tab tab) {
        this.tab = tab;
        initGraphics();
        init();
    }

    private void initGraphics() {
        var backButton = new Button();
        backButton.setPrefSize(30, 30);
        backButton.setOnAction(back);
        backButton.getStyleClass().add("icon-button");
        var backIcon = new FontIcon(FontAwesome.ARROW_LEFT);
        backIcon.setIconSize(12);
        backButton.setGraphic(backIcon);

        var forwardButton = new Button();
        forwardButton.setPrefSize(30, 30);
        forwardButton.setOnAction(forward);
        forwardButton.getStyleClass().add("icon-button");
        var forwardIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
        forwardIcon.setIconSize(12);
        forwardButton.setGraphic(forwardIcon);

        var refreshButton = new Button();
        refreshButton.setPrefSize(30, 30);
        refreshButton.setOnAction(refresh);
        refreshButton.getStyleClass().add("icon-button");
        var refreshIcon = new FontIcon(FontAwesome.REFRESH);
        refreshIcon.setIconSize(12);
        refreshButton.setGraphic(refreshIcon);

        searchTextField = new TextField();
        searchTextField.setPromptText("URL");
        searchTextField.setFont(Font.font(14));
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        HBox.setMargin(searchTextField, new Insets(5));

        var menuButton = new Button();
        menuButton.setPrefSize(30, 30);
        menuButton.getStyleClass().add("icon-button");
        var menuIcon = new FontIcon(FontAwesome.BARS);
        menuIcon.setIconSize(12);
        menuButton.setGraphic(menuIcon);

        var contextMenu = createMenu();
        menuButton.setOnMouseClicked(mouseEvent -> contextMenu.show(menuButton, Side.BOTTOM, 0, 0));

        var hbox = new HBox(3, backButton, forwardButton, refreshButton, searchTextField, menuButton);
        hbox.setPadding(new Insets(0, 5, 0, 5));
        hbox.setAlignment(Pos.CENTER);

        progressBar = new ProgressBar();
        progressBar.getStyleClass().add("browser-progress-bar");
        progressBar.setMinHeight(20);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        webview = new WebView();
        VBox.setVgrow(webview, Priority.ALWAYS);

        root = new VBox(hbox, progressBar, webview);
    }

    public void init() {
        webEngine = webview.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                searchTextField.setText(webEngine.getLocation());
                if (tab != null)
                    tab.textProperty().bind(webEngine.titleProperty());
            }
        });

        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        progressBar.visibleProperty().bind(
                progressBar.progressProperty().greaterThan(0).and(progressBar.progressProperty().lessThan(1))
        );

        searchTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                webEngine.load(searchTextField.getText());
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

        return new ContextMenu(extensionManagerMenuItem);
    }

    private final EventHandler<ActionEvent> refresh = actionEvent -> {
        webEngine.reload();
    };

    public Parent getRoot() {
        return root;
    }

    public WebView getWebview() {
        return webview;
    }
}
