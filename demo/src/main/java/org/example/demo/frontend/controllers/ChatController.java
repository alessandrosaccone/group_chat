package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.demo.GroupChatApplication;
import org.example.demo.backend.classes.Message;
import org.example.demo.backend.enums.MessageType;
import org.example.demo.frontend.listeners.ViewListener;

import java.util.List;

/**
 * This method is the controller of the GUI for the chat.
 */
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

    /**
     * This method manges the interaction with the user in order to send messages. It invokes the corresponding backend
     * method.
     */
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

    /**
     * This method manages the interaction to delete the current chat. After the invocation of the corresponding
     * backend method, it closes the chat window
     */
    @FXML
    private void handleQuitButtonClick() {
        System.out.println("Quit button clicked!" +
                "\nClosing the chat whose id is " + thisChatId);
        GroupChatApplication.getBackend().deleteChat(thisChatId);
        System.out.println("CHAT CLOSED!!");
        yourMessageArea.setText("");
        stage.close();
    }

    /**
     * This method allows the showing of the new messages. Obviously it manages not only text messages but also some
     * informative ones
     * @param messages - the incoming messages list
     */
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

    /**
     * Implementation of updateInfo, not implemented
     */
    @Override
    public void updateInfo() {
        //NOT IMPLEMENTED
    }

    /**
     * This method writes the incoming messages in the messages area
     * @param message - the given message to be displayed
     */
    private void displayMessages(String message){
        for(int i=0; i<messagesList.length - 1; i++){
            messagesList[i] = messagesList[i  + 1];
        }
        messagesList[messagesList.length - 1] = message;

        for(int i=0;i<messagesList.length; i++)
            messageSlots.get(i).setText(messagesList[i]);
    }

    /**
     * Method to set up the chat (to display the chatId)
     * @param id - the id of the current chat
     */
    public void setUpChat(String id){
        chatIdLabel.setText(id);
        thisChatId = id;
    }

    /**
     * Method used to close the window when quitting
     * @param stage - the window to be closed
     */
    public void setStage(Stage stage){this.stage = stage;}
}
