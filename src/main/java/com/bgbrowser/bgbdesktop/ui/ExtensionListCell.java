package com.bgbrowser.bgbdesktop.ui;

import com.bgbrowser.bgbdesktop.utils.ExtensionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.nio.file.Path;

public class ExtensionListCell extends ListCell<Path> {

    private Label label;

    private HBox root;

    @Override
    protected void updateItem(Path item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);

        if (empty || item == null) {
            setGraphic(null);
        }else {
            if (root == null)
                createContent();
            label.setText(item.getFileName().toString());
            setGraphic(root);
        }
    }

    private void createContent() {
        label = new Label();
        label.setFont(Font.font(14));
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        var deleteButton = new Button();
        deleteButton.setPrefSize(30, 30);
        deleteButton.getStyleClass().add("icon-button");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(actionEvent -> {
            var item = getItem();
            try {
                ExtensionManager.deleteExtension(item);
                getListView().getItems().remove(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        var deleteIcon = new FontIcon(FontAwesome.TRASH);
        deleteIcon.setIconSize(15);
        deleteButton.setGraphic(deleteIcon);

        root = new HBox(3, label, deleteButton);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.CENTER_LEFT);
    }
}
