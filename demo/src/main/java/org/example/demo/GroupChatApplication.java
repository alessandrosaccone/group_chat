package org.example.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.demo.backend.classes.Chat;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.example.demo.backend.interfaces.NetworkManager;
import org.example.demo.frontend.controllers.AvailableChatsController;
import org.example.demo.frontend.controllers.GuiController;
import org.example.demo.frontend.controllers.TitlePageController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Main class of the application
 */

public class GroupChatApplication extends Application {
    /**
     * These three attributes are needed for the GUI
     */
    static List<String> fxmls = new ArrayList<>(Arrays.asList("main-page.fxml", "available-chats.fxml"));
    static Map<String, Scene> scenes = new HashMap<>();
    private static final Map<String, GuiController> guiControllers = new HashMap<>();
    /**
     * The network manager is the backend of the application leading all the stuffs needed to make this chat application
     * works properly.
     * THe addresses static attribute is the list of all the clients that use the chat application. Any of them can be
     * chosen to be part of a new chat
     * The chats attributes contains all the current chats of this client.
     */
    private static NetworkManager networkManager;
    private final static ArrayList<InetAddress> addresses = new ArrayList<>();
    private static final Map<String, App> chats = new HashMap<>();
    private static String yourIp;

    public static void deleteChat(String chatId) {
        chats.remove(chatId);
        System.out.println("GUI for chat "+ chatId + " has been deleted!");
    }

    /**
     * This method is for the GUI
     * @param stage - the stage of the application
     * @throws IOException - if anything goes wrong
     */
    @Override
    public void start(Stage stage) throws IOException {
        setupApplication(stage);
    }

    /**
     * The main method
     * @param args - null
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * This method is used to set up all the stuffs for the GUI
     * @param stage - the stage of the application
     * @throws IOException - if anything goes wrong
     */
    private static void setupApplication(Stage stage) throws IOException {
        for(String fxml : fxmls){
            FXMLLoader fxmlLoader = new FXMLLoader(GroupChatApplication.class.getResource(fxml));
            scenes.put(fxml, new Scene(fxmlLoader.load()));
            GuiController ctrl = fxmlLoader.getController();
            GroupChatApplication.guiControllers.put(fxml, ctrl);
            Scene scene = scenes.get("main-page.fxml");
            stage.setTitle("High Available Group Chat Application");
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.out.println("Closing application...");
                    if(getBackend() != null)
                        getBackend().closeAllConnections();
                    System.out.println("APPLICATION CLOSED!!");
                    System.exit(0);
                }
            });
            stage.setScene(scene);
            ((TitlePageController) GroupChatApplication.guiControllers.get("main-page.fxml")).updateInfo();
            stage.show();
        }
    }

    /**
     * This method is used to display the window containing the list of the available chats
     * @param chats - the current available chat
     */
    public static void showChats(ArrayList<Chat> chats) {
        Stage stage = new Stage();
        stage.setScene(scenes.get("available-chats.fxml"));
        ((AvailableChatsController) (guiControllers.get("available-chats.fxml"))).setup(chats, stage);
        stage.show();
    }

    /**
     * This method arrange the joining process for a given chat
     * @param chat - the chat in which the host wants to join
     */
    public static void joinChat(Chat chat) {
        App newChat = new App();
        chats.put(chat.getID(), newChat);
        newChat.joinChat(chat.getID());
        newChat.runChat(new Stage());
    }

    /**
     * This method instantiates the backend in order to establish the connection towards the nodes of the
     * confirmed network
     */
    public static void setupNetwork(){
        GroupChatApplication.setNetworkManager(new NetworkManagerImpl(1234,
                GroupChatApplication.getAddresses(), yourIp));
    }

    /**
     * This method setups the backend and create a new Chat session, put the created chat into the chats attribute and
     * then run it
     * @param stage - the stage of the application
     */
    public static void runApplication(Stage stage) {
        App newChat = new App();
        chats.put(newChat.getChatId(), newChat);
        newChat.createChat();
        newChat.runChat(stage);
    }

    /**
     * This method is used as bridge between the given ip of the current host
     * in the frontend and the creation of the backend
     * @param ip - the given current host ip
     */
    public static void addYourIp(String ip){
        yourIp = ip;
    }

    /**
     * This method inserts a new host address inside the addresses attribute
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
     * The set method for network manager
     * @param networkManager - the network manager to be set
     */
    public static void setNetworkManager(NetworkManager networkManager) {
        GroupChatApplication.networkManager = networkManager;
    }

    /**
     * The get method for the set of all the available addresses
     * @return - the list of all the available addresses
     */
    public static ArrayList<InetAddress> getAddresses() {
        return GroupChatApplication.addresses;
    }

    /**
     * The get method for the backend
     * @return - the backend
     */
    public static NetworkManager getBackend() {
        return GroupChatApplication.networkManager;
    }
}