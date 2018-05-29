package client.presentation.launcher.panes;

import client.data.Storage;
import com.jfoenix.controls.JFXButton;
import data.packets.Packet;
import data.launcher.Conversation;
import data.launcher.Message;
import data.packets.lobby.PacketLobbyLeave;
import data.packets.message.PacketMessageSend;
import enums.Command;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
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
            frame.setSize(new Dimension(800, 800));
            frame.setVisible(true);
        });

        JFXButton leave = new JFXButton("Leave");
        leave.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {

                Storage.getInstance().getObjectToServer().writeObject(new PacketLobbyLeave(name, Storage.getInstance().getUsername()));

                LauncherPane.setNewCenter(new LobbiesPane());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        //Click button
        sendButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                if (textField.getText().equals(""))
                    return;

                Storage.getInstance().getObjectToServer().writeObject(new PacketMessageSend(name, new Message(Storage.getInstance().getUsername(), textField.getText())));
                textField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //Enter key
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                try {
                    if (textField.getText().equals(""))
                        return;

                    Storage.getInstance().getObjectToServer().writeObject(new PacketMessageSend(name, new Message(Storage.getInstance().getUsername(), textField.getText())));
                    textField.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Text title = new Text("Name: " + name);
        title.setId("lobby-title");

        HBox chatBox = new HBox();
        chatBox.getChildren().addAll(textField, sendButton);

        textArea.setEditable(false);

        top.getChildren().addAll(title, launch, leave);
        this.getChildren().addAll(top, textArea, chatBox);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    double top = textArea.getScrollTop();
                    StringBuilder stringBuilder = new StringBuilder();

                    Conversation conversation = Storage.getInstance().getApplicationData().getLauncherData().getLobby(name).getConversation();
                    //   textArea.setText(conversation.toString());

                    conversation.getMessages().forEach(message -> {
                        stringBuilder.append("[" + message.getDateTime().getHour() + ":" + message.getDateTime().getMinute() + "] " +
                                message.getAuthor() + ": " + message.getText() + "\n");
                    });
                    textArea.setText(stringBuilder.toString());
                    textArea.setScrollTop(top + textArea.getHeight());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);
    }


}
