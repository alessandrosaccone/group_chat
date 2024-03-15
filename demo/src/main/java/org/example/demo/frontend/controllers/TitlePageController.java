package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.demo.backend.interfaces.NetworkManager;
import org.example.demo.frontend.listeners.ViewListener;
import org.example.demo.frontend.listeners.ViewSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TitlePageController extends GuiController implements Initializable, ViewSource, ViewListener {
    @FXML
    private Label ipAddressLabel;
    @FXML
    private Button createChatButton;
    @FXML
    private Button joinChatButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleCreateChatButtonClick() {
        System.out.println("Button create chat clicked!");
        try {
            networkManager.createNewChat(this.addresses);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/chat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage newStage = (Stage) ipAddressLabel.getScene().getWindow();
            newStage.setTitle("Chat Creation");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleJoinChatButtonClick() {
        System.out.println("Button join chat clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/chat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setTitle("Chatting session");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCurrentChat(List<String> messages) {
        //NOT IMPLEMENTED
    }

    @Override
    public void updateChatName(String chatName) {
        //NOT IMPLEMENTED
    }

    @Override
    public void updateIp() {
        String ipAddress = networkManager.getLocalAddress().toString();
        ipAddressLabel.setText(ipAddress);
    }

    @Override
    public void notifyCreateChat(String chatName, String[] ips) {
        //TO BE IMPLEMENTED
    }

    @Override
    public void notifyJoinChat(String ip) {
        //TO BE IMPLEMENTED
    }

    @Override
    public void quitChat() {
        //NOT IMPLEMENTED
    }

    @Override
    public void setController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void setAddresses(ArrayList<InetAddress> addresses) {
        this.addresses = addresses;
    }
}
