package client.presentation.launcher.panes;

import client.data.Storage;
import com.jfoenix.controls.JFXButton;
import data.launcher.Conversation;
import data.launcher.Message;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyPane extends VBox {
    private String name;
    private TextArea textArea = new TextArea();
    private TextField textField = new TextField();
    private JFXButton sendButton = new JFXButton("Send");
    public LobbyPane(String name) {
        this.name = name;
        this.setId("lobby-pane");
        HBox top = new HBox();

        JFXButton launch = new JFXButton("Launch");
        launch.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            JFrame frame = new JFrame("PacMan");
            frame.setSize(new Dimension(800,800));
            frame.setVisible(true);
        });

        JFXButton leave = new JFXButton("Leave");
        leave.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                Storage.getInstance().getObjectToServer().writeObject("lobby_leave");
                Storage.getInstance().getObjectToServer().writeObject(Storage.getInstance().getUsername());
                Storage.getInstance().getObjectToServer().writeObject(name);

                LauncherPane.setNewCenter(new LobbiesPane());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        sendButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                Storage.getInstance().getObjectToServer().writeObject("message_send");
                Storage.getInstance().getObjectToServer().writeObject(new Message(Storage.getInstance().getUsername(), textField.getText()));
                Storage.getInstance().getObjectToServer().writeObject(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Text title = new Text("Name: " + name);
        title.setId("lobby-title");

        HBox chatBox = new HBox();
        chatBox.getChildren().addAll(textField,sendButton);

        top.getChildren().addAll(title,launch, leave);
        this.getChildren().addAll(top,textArea,chatBox);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{
                    Conversation conversation = Storage.getInstance().getApplicationData().getLauncherData().getLobby(name).getConversation();
                    textArea.setText(conversation.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 1000,1000);
    }


}
