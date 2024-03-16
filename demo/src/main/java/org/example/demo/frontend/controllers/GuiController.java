package org.example.demo.frontend.controllers;

import org.example.demo.backend.interfaces.NetworkManager;

import java.net.InetAddress;
import java.util.ArrayList;

public abstract class GuiController {
    static NetworkManager networkManager;
    ArrayList<InetAddress> addresses;

    public void setController(NetworkManager manager){
        networkManager = manager;
    }
    public void setAddresses(ArrayList<InetAddress> adds){
        addresses = adds;
    }
}
