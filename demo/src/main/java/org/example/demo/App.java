package org.example.demo;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.demo.backend.classes.Message;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.example.demo.backend.interfaces.NetworkManager;
import org.example.demo.frontend.controllers.ChatController;
import org.example.demo.frontend.controllers.GuiController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class App {
    private static NetworkManager networkManager;
    private final static ArrayList<InetAddress> addresses = new ArrayList<>();
    private static String chatId;

    static String fxml = "chat.fxml";

    static Map<String, Scene> scenes = new HashMap<String, Scene>();

    private static final Map<String, GuiController> guiControllers = new HashMap<String, GuiController>();

    private static void setupApplication(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        scenes.put(fxml, new Scene(fxmlLoader.load(), 900, 600));
        GuiController ctrl = fxmlLoader.getController();
        App.guiControllers.put(fxml, ctrl);
        Scene scene = scenes.get("chat.fxml");
        stage.setTitle("High Available Group Chat Application");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("Closing the chat whose id is " + App.getChatId());
                App.getBackend().deleteChat(App.getChatId());
                App.getBackend().closeAllConnections();
                System.out.println("CHAT CLOSED!!");
            }
        });
        stage.setScene(scene);
        ((ChatController) fxmlLoader.getController()).setUpChat();
        stage.show();
    }

    public static void runApplication() {
        App.setNetworkManager(new NetworkManagerImpl(1234,
                App.getAddresses()));
        chatId = getBackend().createNewChat(getAddresses());
        System.out.println("Chat " + chatId + " creation");
        addListener((ChatController) guiControllers.get("chat.fxml"));
    }

    private static void setNetworkManager(NetworkManager networkManager) {
        App.networkManager = networkManager;
    }

    public static ArrayList<InetAddress> getAddresses() {
        return App.addresses;
    }

    public static NetworkManager getBackend() {
        return App.networkManager;
    }

    public static String getChatId() {
        return App.chatId;
    }

    private static void addListener(ChatController controller){
        new Thread(() -> {
            System.out.println("Listener to the receiving queue initialized for chat "
                    + App.getChatId() + "!!");
            ArrayList<Message> messages;
            while(true){
                messages = App.getBackend().getMessagesToBeShown(
                        App.getChatId()
                );
                if(messages.size() > 0){
                    System.out.println("Messages received on chat " + App.getChatId());
                    controller.updateCurrentChat(messages);
                    break;
                }
            }
        }).start();
    }

    public static void addHostAddress(String ipAddress){
        try {
            addresses.add(InetAddress.getByName(ipAddress));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
