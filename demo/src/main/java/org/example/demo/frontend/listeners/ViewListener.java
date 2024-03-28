package org.example.demo.frontend.listeners;

import org.example.demo.backend.classes.Message;

import java.util.List;

public interface ViewListener {
    void updateCurrentChat(List<Message> messages);
    void updateInfo();
}
