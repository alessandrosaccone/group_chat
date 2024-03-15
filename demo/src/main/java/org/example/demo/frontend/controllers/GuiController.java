package org.example.demo.frontend.controllers;

import org.example.demo.backend.interfaces.NetworkManager;

public abstract class GuiController {
    NetworkManager networkManager;

    public abstract void setController(NetworkManager networkManager);
}
