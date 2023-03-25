module com.bgbrowser.bgbdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires com.bgbrowser.extensionframework;

    opens com.bgbrowser.bgbdesktop.ui to javafx.fxml;
    opens com.bgbrowser.bgbdesktop.ui.controllers to javafx.fxml;
    exports com.bgbrowser.bgbdesktop;
}