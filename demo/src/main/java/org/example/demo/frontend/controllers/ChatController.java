package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.demo.backend.enums.MessageType;
import org.example.demo.frontend.listeners.ViewListener;
import org.example.demo.frontend.listeners.ViewSource;

import java.util.List;

public class ChatController extends GuiController implements ViewListener, ViewSource {
    @FXML
    private Button sendButton;
    @FXML
    private Button quitButton;
    @FXML
    private List<Label> messageSlots;
    @FXML
    private TextArea yourMessageArea;
    private final String[] messagesList = {"", "", "", "", "", "", "", "", "", ""};

    @FXML
    private void handleSendButtonClick() {
        String message = yourMessageArea.getText();
        if(message == null || message.equals("")){
            System.err.println("No message written");
            yourMessageArea.setText("NO MESSAGE WRITTEN.\n" +
                    "PLEASE, WRITE SOMETHING HERE!");
            return;
        }
        System.out.println(message);
        yourMessageArea.setText("");
        displayMessages(message);
        networkManager.setMessageToBeSent("alpha", message, MessageType.TEXT_MESSAGE);
    }

    @FXML
    private void handleQuitButtonClick() {
        System.out.println("Quit button clicked!");

    }

    @Override
    public void updateCurrentChat(List<String> messages) {
        //TO BE IMPLEMENTED
    }

    @Override
    public void updateChatName(String chatName) {
        //TO BE IMPLEMENTED
    }

    @Override
    public void updateIp() {
        //NOT IMPLEMENTED
    }

    @Override
    public void notifyCreateChat(String chatName, String[] ips) {
        //NOT IMPLEMENTED
    }

    @Override
    public void notifyJoinChat(String ip) {
        //NOT IMPLEMENTED
    }

    @Override
    public void quitChat() {
        //TO BE IMPLEMENTED
    }

    private void displayMessages(String message){
        for(int i=0; i<messagesList.length - 1; i++){
            messagesList[i] = messagesList[i  + 1];
        }
        messagesList[messagesList.length - 1] = message;

        for(int i=0;i<messagesList.length; i++)
            messageSlots.get(i).setText(messagesList[i]);
    }
}
