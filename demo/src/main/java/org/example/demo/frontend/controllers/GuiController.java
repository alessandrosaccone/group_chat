package org.example.demo.frontend.controllers;

import org.example.demo.backend.interfaces.NetworkManager;

import java.net.InetAddress;
import java.util.ArrayList;

public abstract class GuiController {
    NetworkManager networkManager;
    ArrayList<InetAddress> addresses;

    public abstract void setController(NetworkManager networkManager);
    public abstract void setAddresses(ArrayList<InetAddress> addresses);
}
