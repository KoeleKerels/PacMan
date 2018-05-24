package client.presentation.panes;

import client.data.Storage;
import com.jfoenix.controls.JFXButton;
import managers.StateManager;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;


public class LoginPane extends HBox {
    public LoginPane() {
        HBox hBox = new HBox();
        Text text = new Text("UserName: ");
        text.setId("user-name-text");

        TextField textField = new TextField();
        textField.setId("user-name-field");

        hBox.getChildren().addAll(text, textField);

        VBox vBox = new VBox();
        vBox.getChildren().add(hBox);

        JFXButton connect = new JFXButton("Connect");
        connect.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LauncherPane.setNewCenter(new LobbiesPane());
        });
        connect.setId("connect-button");
        vBox.getChildren().add(connect);
        this.getChildren().add(vBox);
    }
}