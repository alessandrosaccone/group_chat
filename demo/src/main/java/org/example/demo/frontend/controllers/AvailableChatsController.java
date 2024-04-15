package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.example.demo.GroupChatApplication;
import org.example.demo.backend.classes.Chat;

import java.util.ArrayList;

/**
 * The controller for the join chat page
 */
public class AvailableChatsController extends GuiController {
    @FXML
    private ChoiceBox<String> availableChatsSet = new ChoiceBox<>();
    private final ArrayList<Chat> chats = new ArrayList<>();
    private Stage stage;
    public void setup(ArrayList<Chat> chats, Stage stage){
        this.chats.addAll(chats);
        availableChatsSet.getItems().addAll(chats.stream().map(Chat::getID).toList());
        this.stage = stage;
    }
    @FXML
    private void handleJoinButtonClick(){
        String selectedChat = availableChatsSet.getValue();
        Chat chat = chats.stream().filter(c -> c.getID().equals(selectedChat)).findFirst().orElse(null);
        stage.close();
        chats.clear();
        availableChatsSet.getItems().clear();
        assert chat != null;
        GroupChatApplication.joinChat(chat);
    }
}
