package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.demo.App;
import org.example.demo.GroupChatApplication;
import org.example.demo.backend.classes.Message;
import org.example.demo.backend.enums.MessageType;
import org.example.demo.frontend.listeners.ViewListener;

import java.util.List;

public class ChatController extends GuiController implements ViewListener {
    @FXML
    private Label chatIdLabel;
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
        System.out.println("Message <<" + message + ">> to be send. Juxtaposed to the sending queue");
        yourMessageArea.setText("");
        GroupChatApplication.getBackend().setMessageToBeSent(
                App.getChatId(), message, MessageType.TEXT_MESSAGE);
    }

    @FXML
    private void handleQuitButtonClick() {
        System.out.println("Quit button clicked!");
        GroupChatApplication.getBackend().deleteChat(App.getChatId());
        yourMessageArea.setText("You successfully left the chat!");
    }

    @FXML
    private void handleRejoinButtonClick(){
        System.out.println("Rejoining...");
        yourMessageArea.setText("You successfully rejoined the chat!!");
    }

    @Override
    public void updateCurrentChat(List<Message> messages) {
        for(Message m : messages){
            if(m.getMessageType() == MessageType.TEXT_MESSAGE){
                String sender;
                if(m.getSenderIP().equals(GroupChatApplication.getBackend().getLocalAddress()))
                    sender = "you";
                else sender = m.getSenderIP().toString();
                displayMessages(sender + " : " + m.getMessage());
            }

        }
    }

    @Override
    public void updateInfo() {
        //NOT IMPLEMENTED
    }

    private void displayMessages(String message){
        for(int i=0; i<messagesList.length - 1; i++){
            messagesList[i] = messagesList[i  + 1];
        }
        messagesList[messagesList.length - 1] = message;

        for(int i=0;i<messagesList.length; i++)
            messageSlots.get(i).setText(messagesList[i]);
    }

    public void setUpChat(){
        chatIdLabel.setText(App.getChatId());
    }
}
