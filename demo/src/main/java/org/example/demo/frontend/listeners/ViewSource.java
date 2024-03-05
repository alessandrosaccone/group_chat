package org.example.demo.frontend.listeners;

/**
 * Interface exporting the method from the view to the backend.
 * It has three methods.
 */
public interface ViewSource {
    /**
     * Method that creates a chat
     * @param chatName - the name of the chat
     * @param ips - an array containing all the ips of the chat members
     */
    void notifyCreateChat(String chatName, String[] ips);

    /**
     * Method that allow this client to join an existing chat
     * @param ip - the ip of this client
     */
    void notifyJoinChat(String ip);

    /**
     * Method that quit from this chat
     */
    void quitChat();
}
