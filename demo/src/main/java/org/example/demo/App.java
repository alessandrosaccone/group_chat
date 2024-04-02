package org.example.demo;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.demo.backend.classes.Message;
import org.example.demo.frontend.controllers.ChatController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class App {
    /**
     * This attribute is needed for the GUI
     */
    private ChatController chatController;
    /**
     * This static attribute is a temp container for the selected addresses among all the available ones. After the chat
     * creation it will be cleaned in order to create a new chat. The same concerns hold for the currentChatId
     */
    private final static ArrayList<InetAddress> addresses = new ArrayList<>();
    private static String currentChatId;

    /**
     * These are the true attributes containing the addresses of the partecipants in this chat and the chatId
     */
    private final ArrayList<InetAddress> chatAddresses = new ArrayList<>();
    private String chatId;

    /**
     * Just a simple constructor that copy the selected addresses into the real ones
     */
    public App(){
        chatAddresses.addAll(addresses);
        chatId = currentChatId;
        addresses.clear();
        currentChatId = null;
    }

    /**
     * The setup method for the GUI for the chat page
     * @param stage - the stage of the application
     * @throws IOException - if anything goes wrong
     */
    private void setupApplication(Stage stage) throws IOException {
        String fxml = "chat.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        chatController = fxmlLoader.getController();
        stage.setTitle("High Available Group Chat Application");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("Closing the chat whose id is " + chatId);
                GroupChatApplication.getBackend().deleteChat(chatId);
                GroupChatApplication.deleteChat(chatId);
                System.out.println("CHAT CLOSED!!");
            }
        });
        chatController.setStage(stage);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method allow to launch the chat session
     * @param stage - the given stage
     */
    public void runChat(Stage stage) {
        try {
            setupApplication(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chatId = GroupChatApplication.getBackend().createNewChat(chatAddresses);
        chatController.setUpChat(chatId);
        System.out.println("Chat " + chatId + " creation");
        addListener(chatController, chatId);
    }

    /**
     * This listener method launches a thread whose behavior is to listen to new messages and start the process
     * for displaying it
     * @param controller - the chatController of the GUI
     * @param id - the id of the current chat
     */
    private static void addListener(ChatController controller, String id){
        new Thread(() -> {
            System.out.println("Listener to the receiving queue initialized for chat "
                    + id + "!!");
            ArrayList<Message> messages;
            while(true){
                messages = GroupChatApplication.getBackend().getMessagesToBeShown(
                        id
                );
                if(messages.size() > 0){
                    System.out.println("Messages received on chat " + id);
                    ArrayList<Message> finalMessages = messages;
                    Platform.runLater(() -> controller.updateCurrentChat(finalMessages));
                }
            }
        }).start();
    }

    /**
     * This method put into the list of the selected addresses the given address
     * @param ipAddress - the address to be managed
     */
    public static void addHostAddress(String ipAddress){
        try {
            addresses.add(InetAddress.getByName(ipAddress));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /*----- SETTERS AND GETTERS -----*/

    /**
     * The get method for the current selected addresses
     * @return - the list of the current selected addresses
     */
    public static ArrayList<InetAddress> getAddresses() {
        return App.addresses;
    }

    /**
     * The get method for the chat id
     * @return - the chat id
     */
    public String getChatId(){return this.chatId;}
}
