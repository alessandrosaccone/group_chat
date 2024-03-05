package org.example.demo.frontend.listeners;

import java.util.List;

public interface ViewListener {
    void updateCurrentChat(List<String> messages);
    void updateChatName(String chatName);
    void updateIp(String ip);
}
