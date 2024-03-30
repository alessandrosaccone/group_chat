package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
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
    @FXML
    private Label warningLabel;
    private Stage stage;
    private final String[] messagesList = {"", "", "", "", "", "", "", "", "", ""};
    private String thisChatId;

    @FXML
    private void handleSendButtonClick() {
        String message = yourMessageArea.getText();
        warningLabel.setText("");
        if(message == null || message.equals("")){
            System.err.println("No message written");
            warningLabel.setText("No message written");
            yourMessageArea.setText("PLEASE, WRITE SOMETHING HERE!");
            return;
        }
        if(message.length() > 35){
            warningLabel.setText("Message too long!");
            return;
        }
        System.out.println("Message <<" + message + ">> to be send. Juxtaposed to the sending queue");
        yourMessageArea.setText("");
        GroupChatApplication.getBackend().setMessageToBeSent(
                thisChatId, message, MessageType.TEXT_MESSAGE);
    }

    @FXML
    private void handleQuitButtonClick() {
        System.out.println("Quit button clicked!");
        GroupChatApplication.getBackend().deleteChat(thisChatId);
        GroupChatApplication.getBackend().closeAllConnections();
        yourMessageArea.setText("");
        stage.close();
    }

    @Override
    public void updateCurrentChat(List<Message> messages) {
        for(Message m : messages){
            if(m.getMessageType() != MessageType.CREATION_REQUEST &&
                    m.getMessageType() != MessageType.CREATION_REJECT){
                String sender = "From ";
                if(m.getSenderIP().equals(GroupChatApplication.getBackend().getLocalAddress()))
                    sender = sender + "You";
                else sender = sender + m.getSenderIP().toString();
                if(m.getMessageType() != MessageType.DELETION_ORDER) displayMessages(sender + " : " + m.getMessage());
                else {
                    System.err.println(sender + " left the chat");
                    warningLabel.setText(sender + " left the chat");
                }
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

    public void setUpChat(String id){
        chatIdLabel.setText(id);
        thisChatId = id;
    }

    public void setStage(Stage stage){this.stage = stage;}
}
