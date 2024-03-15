package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.demo.backend.interfaces.NetworkManager;
import org.example.demo.frontend.listeners.ViewListener;
import org.example.demo.frontend.listeners.ViewSource;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatController extends GuiController implements ViewListener, ViewSource {
    @FXML
    private Button sendButton;
    @FXML
    private Button quitButton;
    @FXML
    private List<Label> messageSlots;

    @FXML
    private void handleSendButtonClick() {
        System.out.println("Send button clicked!");
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

    @Override
    public void setController(NetworkManager networkManager) {

    }

    @Override
    public void setAddresses(ArrayList<InetAddress> addresses) {
        this.addresses = addresses;
    }
}
