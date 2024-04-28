package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.demo.App;
import org.example.demo.GroupChatApplication;
import org.example.demo.backend.classes.Chat;
import org.example.demo.backend.classes.Message;
import org.example.demo.frontend.listeners.ViewListener;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This method is the controller of the main page of the Application. It manages all the stuffs regarding the
 * interactions with the user in order to initialize and launch a chat
 */
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
    @FXML
    private TextField yourIpTextField;
    @FXML
    private Button addYourIpButton;
    private int numberOfUsers;
    private boolean confirmedStatus;
    private boolean isYourIpAdded;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * This method handle the creation of a new chat. It launches the runApplication method that creates effectively the
     * new chat
     */
    @FXML
    private void handleCreateChatButtonClick() {
        System.out.println("Button create chat clicked!");
        if(App.getAddresses().isEmpty()){
            System.err.println("IS EMPTY");
            advLabel.setText("Please, select the chat's partecipants!");
            return;
        }
        if(App.getAddresses().size() + 1 != numberOfUsers){
            advLabel.setText("The number of user does not match with the chat size!");
            return;
        }
        GroupChatApplication.runApplication(new Stage());
        cleanSetup();
    }

    /**
     * This method allow the user to select the partecipants to this chat among all the available addresses
     */
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
        numOfPartecipants.setText(Integer.toString(currentPartecipantsBox.getItems().size()));
        App.addHostAddress(chosenUser);
    }

    /**
     * This method is used to confirm the set of all the available addresses that must be known to the Network manager
     */
    @FXML
    private void handleConfirmCurrentSetClick(){
        if(confirmedStatus)
            return;
        if(!isYourIpAdded){
            advLabel.setText("You have to insert your ip addresses in the given spot!!");
            return;
        }
        if(GroupChatApplication.getAddresses().size() == 0){
            advLabel.setText("You must populate the set of available addresses!");
            return;
        }
        System.out.println("Current set of available addresses confirmed");
        addressIp.setDisable(true);
        addressIp.setOpacity(0.5);
        addHostButton.setDisable(true);
        addHostButton.setOpacity(0.5);
        confirmedStatus = true;
        advLabel.setText("");
        GroupChatApplication.setupNetwork();
    }

    /**
     * This method manage the process of adding the ip of the current of to the backend
     */
    @FXML
    private void handleConfirmYourAddressClick() {
        String address = yourIpTextField.getText();
        if(isYourIpAdded){
            advLabel.setText("Your address has already been added!!");
            return;
        }
        if(address.equals(""))
            advLabel.setText("Please, insert ip your address in the given spot!!");
        else {
            GroupChatApplication.addYourIp(address);
            System.out.println("You have added your ip address to the system");
            setAddressesBox.getItems().add("you");
            yourIpTextField.setText("");
            advLabel.setText("");
            yourIpTextField.setDisable(true);
            yourIpTextField.setOpacity(0.5);
            addYourIpButton.setDisable(true);
            addYourIpButton.setOpacity(0.5);
            isYourIpAdded = true;
        }
    }

    /**
     * This method allows the user to insert a new addresses into the set of all the addresses
     */
    @FXML
    private void addHostButtonClick(){
        String address = addressIp.getText();
        if(address.equals(""))
            advLabel.setText("Please, insert an address!!");
        else if(setAddressesBox.getItems().contains(address)){
            advLabel.setText("You have already added this address!");
        } else {
            GroupChatApplication.addHostAddress(address);
            System.out.println("Users whose address is " + address + " added to the set of users");
            addressIp.setText("");
            setAddressesBox.getItems().add(address);
            advLabel.setText("");
        }
    }


    /**
     * This method allow the user to choose between all the available chats and then join one of them
     */
    @FXML
    private void handleSeeChatsButtonClick(){
        if(GroupChatApplication.getBackend() == null){
            advLabel.setText("You have to confirm the host addresses");
            System.err.println("No backend found");
            return;
        }
        ArrayList<Chat> chats = GroupChatApplication.getBackend().getChats();
        if(chats == null || chats.size() == 0){
            System.err.println("No chats available");
            advLabel.setText("No available chats");
            return;
        }
        System.err.println(chats.get(0).getID());
        GroupChatApplication.showChats(chats);
    }

    /**
     * This method cleans the attributes and the GUI after the creation of the chat in order to allow the creation
     * of another one
     */
    private void cleanSetup() {
        numUserChoice.setValue(2);
        addressIp.setText("");
        numberOfUsers = 0;
        numOfPartecipants.setText("0");
        currentPartecipantsBox.getItems().clear();
    }

    /**
     * Implementation of updateCurrentChat but here not implemented
     * @param messages - null
     */
    @Override
    public void updateCurrentChat(List<Message> messages) {
        //NOT IMPLEMENTED
    }

    /**
     * Implementation of updateInfo
     */
    @Override
    public void updateInfo() {
        numUserChoice.getItems().addAll( 2, 3, 4, 5, 6, 7, 8, 8, 9, 10);
        numUserChoice.setValue(2);
        numUserChoice.setOnAction(e -> {
            numberOfUsers = numUserChoice.getValue();
        });
    }

}
