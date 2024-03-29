package org.example.demo.frontend.controllers;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.demo.App;
import org.example.demo.GroupChatApplication;
import org.example.demo.backend.classes.Message;
import org.example.demo.frontend.listeners.ViewListener;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TitlePageController extends GuiController implements Initializable, ViewListener {
    @FXML
    private ChoiceBox<Integer> numUserChoice;
    @FXML
    private TextField addressIp;
    @FXML
    private ChoiceBox<String> setAddressesBox;
    @FXML
    private Label numOfPartecipants;
    @FXML
    private Label advLabel;
    @FXML
    private ChoiceBox<String> currentPartecipantsBox;
    @FXML
    private Button addHostButton;

    private int numberOfUsers;
    private boolean confirmedStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleCreateChatButtonClick() {
        System.out.println("Button create chat clicked!");
        //try {
            GroupChatApplication.runApplication(new Stage());
            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/chat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.out.println("Closing the chat whose id is " + App.getChatId());
                    GroupChatApplication.getBackend().deleteChat(App.getChatId());
                    GroupChatApplication.getBackend().closeAllConnections();
                    System.out.println("CHAT CLOSED!!");
                }
            });
            newStage.setTitle("High Available Group Chat Application");
            newStage.setScene(scene);
            ((ChatController) loader.getController()).setUpChat();
            newStage.show();
            cleanSetup();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        cleanSetup();
    }

    @FXML
    private void handleSelectPartecipantButtonClick(){
        if(!confirmedStatus){
            advLabel.setText("You must confirm the set of available addresses first");
            return;
        }
        advLabel.setText("");
        String chosenUser = setAddressesBox.getValue();
        if(chosenUser == null){
            advLabel.setText("Please, select a user from the set of available addresses");
            return;
        }
        if(currentPartecipantsBox.getItems().contains(chosenUser)){
            advLabel.setText("This user has been already added to the current chat room");
            return;
        }
        if(chosenUser.equals("you")){
            advLabel.setText("You are already in the chat room");
            return;
        }
        if(numUserChoice.getValue() != numberOfUsers)
            numberOfUsers = numUserChoice.getValue();
        if(App.getAddresses().size() + 1 == numberOfUsers){
            advLabel.setText("Max number of users reached for this chat room");
            return;
        }
        System.out.println("User " + chosenUser + " has been selected for the current chat room");
        currentPartecipantsBox.getItems().add(chosenUser);
        numOfPartecipants.setText(Integer.toString(currentPartecipantsBox.getItems().size() + 1));
        App.addHostAddress(chosenUser);
    }

    @FXML
    private void handleConfirmCurrentSetClick(){
        if(confirmedStatus)
            return;
        System.out.println("Current set of available addresses confirmed");
        addressIp.setDisable(true);
        addressIp.setOpacity(0.5);
        addHostButton.setDisable(true);
        addHostButton.setOpacity(0.5);
        confirmedStatus = true;
    }

    private void cleanSetup() {
        numUserChoice.setValue(2);
        addressIp.setText("");
        numberOfUsers = 0;
        numOfPartecipants.setText("1");
        setAddressesBox.getItems().clear();
        currentPartecipantsBox.getItems().clear();
    }

    @Override
    public void updateCurrentChat(List<Message> messages) {
        //NOT IMPLEMENTED
    }

    @Override
    public void updateInfo() {
        numUserChoice.getItems().addAll( 2, 3, 4, 5, 6, 7, 8, 8, 9, 10);
        numUserChoice.setValue(2);
        numUserChoice.setOnAction(e -> {
            numberOfUsers = numUserChoice.getValue();
        });
        setAddressesBox.getItems().add("you");
    }

    @FXML
    private void addHostButtonClick(){
        /*if(numberOfUsers == 0 || (App.getAddresses().size() == numberOfUsers - 1)){
            advLabel.setText("The number of users is zero or the max has been reached!");
            return;
        }*/
         String address = addressIp.getText();
         if(address.equals(""))
             advLabel.setText("Please, insert an address!!");
         else {
             GroupChatApplication.addHostAddress(address);
             System.out.println("Users whose address is " + address + " added to the set of users");
             addressIp.setText("");
             setAddressesBox.getItems().add(address);
             //numOfPartecipants.setText(Integer.toString(GroupChatApplication.getAddresses().size() + 1));
             advLabel.setText("");
         }
    }

}
