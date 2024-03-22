package org.example.demo.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.demo.backend.interfaces.NetworkManager;
import org.example.demo.frontend.listeners.ViewSource;

import java.net.InetAddress;
import java.util.ArrayList;

public class ChatCreationController extends GuiController implements ViewSource {
    @FXML
    private Button createChatButton;
    @FXML
    private void handleCreateChatButtonClick() {
        System.out.println("Create chat button clicked!");
    }

    @Override
    public void notifyCreateChat(String chatName, String[] ips) {
        //TO BE IMPLEMETED
    }

    @Override
    public void notifyJoinChat(String ip) {
        //NOT IMPLEMENTED
    }

    @Override
    public void quitChat() {
        //NOT IMPLEMENTED
    }

}
