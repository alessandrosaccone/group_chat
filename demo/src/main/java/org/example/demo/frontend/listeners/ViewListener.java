package org.example.demo.frontend.listeners;

import org.example.demo.backend.classes.Message;

import java.util.List;

/**
 * This interface includes the two method that allow the GUI controllers to be invoked by external classes
 */
public interface ViewListener {
    /**
     * This method is used to display messages
     * @param messages - the list of the messages to be displayed
     */
    void updateCurrentChat(List<Message> messages);

    /**
     * This method is used to exchange some information regarding some stuffs about the chat characteristics
     */
    void updateInfo();
}
