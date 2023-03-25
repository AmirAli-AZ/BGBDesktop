package com.bgbrowser.extensionframework;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Browser {

    private final Stage stage;

    private final SimpleObjectProperty<WebView> currentTabWebviewProperty = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<Tab> currentTabProperty = new SimpleObjectProperty<>();

    public Browser(Stage stage) {
        this.stage = stage;

    }

    public Stage getStage() {
        return stage;
    }

    public WebView getCurrentTabWebview() {
        return currentTabWebviewProperty.get();
    }

    private void setCurrentTabWebview(WebView currentTabWebview) {
        currentTabWebviewProperty.set(currentTabWebview);
    }

    public ReadOnlyObjectProperty<WebView> currentTabWebviewProperty() {
        return currentTabWebviewProperty;
    }

    public Tab getCurrentTab() {
        return currentTabProperty.get();
    }

    private void setCurrentTab(Tab currentTab) {
        currentTabProperty.set(currentTab);
    }

    public ReadOnlyObjectProperty<Tab> currentTabProperty() {
        return currentTabProperty;
    }
}
