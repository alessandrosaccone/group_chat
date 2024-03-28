package org.example.demo.frontend.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private List<Label> hostsLabels;
    @FXML
    private Label numOfPartecipants;
    @FXML
    private Label advLabel;

    private int numberOfUsers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleCreateChatButtonClick() {
        System.out.println("Button create chat clicked!");
        try {
            GroupChatApplication.runApplication();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/chat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.out.println("Closing the chat whose id is " + GroupChatApplication.getChatId());
                    GroupChatApplication.getBackend().deleteChat(GroupChatApplication.getChatId());
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
        }
    }

    private void cleanSetup() {
        numUserChoice.setValue(1);
        for(Label label : hostsLabels)
            label.setText("");
        addressIp.setText("");
        numberOfUsers = 0;
        numOfPartecipants.setText("1");
    }

    @Override
    public void updateCurrentChat(List<Message> messages) {
        //NOT IMPLEMENTED
    }

    @Override
    public void updateInfo() {
        numUserChoice.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10);
        numUserChoice.setValue(0);
        numUserChoice.setOnAction(e -> {
            numberOfUsers = numUserChoice.getValue();
        });
        for(Label l : hostsLabels)
            l.setText("");
    }

    @FXML
    private void addHostButtonClick(){
        if(numberOfUsers == 0 || (GroupChatApplication.getAddresses().size() == numberOfUsers - 1)){
            advLabel.setText("The number of users is zero or the max has been reached!");
            return;
        }
         String address = addressIp.getText();
         if(address.equals(""))
             advLabel.setText("Please, insert an address!!");
         else {
             GroupChatApplication.addHostAddress(address);
             System.out.println(address);
             addressIp.setText("");
             hostsLabels.get(GroupChatApplication.getAddresses().size() - 1).setText(address);
             numOfPartecipants.setText(Integer.toString(GroupChatApplication.getAddresses().size() + 1));
             advLabel.setText("");
         }
    }

}
