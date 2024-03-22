package org.example.demo;

import javafx.application.Application;
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
import org.example.demo.frontend.controllers.TitlePageController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class GroupChatApplication extends Application {

    private static NetworkManager networkManager;
    private final static ArrayList<InetAddress> addresses = new ArrayList<>();
    private static String chatId;

    static List<String> fxmls = new ArrayList<>(Arrays.asList("main-page.fxml", "chat-creation.fxml", "chat.fxml"));

    static Map<String, Scene> scenes = new HashMap<String, Scene>();

    private final Map<String, GuiController> guiControllers = new HashMap<String, GuiController>();

    @Override
    public void start(Stage stage) throws IOException {
        GroupChatApplication.setNetworkManager(new NetworkManagerImpl(1234,
                GroupChatApplication.getAddresses()));

        for(String fxml : fxmls){
            FXMLLoader fxmlLoader = new FXMLLoader(GroupChatApplication.class.getResource(fxml));
            scenes.put(fxml, new Scene(fxmlLoader.load(), 900, 600));
            GuiController ctrl = fxmlLoader.getController();
            guiControllers.put(fxml, ctrl);
        }
        Scene scene = scenes.get("main-page.fxml");
        stage.setTitle("High Available Group Chat Application");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("Closing...");
                System.exit(0);
            }
        });
        stage.setScene(scene);
        ((TitlePageController) guiControllers.get("main-page.fxml")).updateIp();
        addListener((ChatController) guiControllers.get("chat.fxml"));
        stage.show();
        scenes.replace("main-page.fxml", scene);
    }

    public static void main(String[] args) {
        try {
            ArrayList<InetAddress> adds = new ArrayList<>();
            adds.add(InetAddress.getByName("localhost"));
            GroupChatApplication.setAddresses(adds);
        } catch (UnknownHostException e) {
            System.err.println("Host not found");
            throw new RuntimeException(e);
        }
        launch();
    }

    public static void setNetworkManager(NetworkManager networkManager) {
        GroupChatApplication.networkManager = networkManager;
    }

    public static void setAddresses(ArrayList<InetAddress> addresses) {
        GroupChatApplication.addresses.addAll(addresses);
    }

    public static void setChatId(String chatId) {
        GroupChatApplication.chatId = chatId;
    }

    public static ArrayList<InetAddress> getAddresses() {
        return GroupChatApplication.addresses;
    }

    public static NetworkManager getBackend() {
        return GroupChatApplication.networkManager;
    }

    public static String getChatId() {
        return GroupChatApplication.chatId;
    }

    private void addListener(ChatController controller){
        new Thread(() -> {
            System.out.println("Listener initialized!!");
            ArrayList<Message> messages;
            while(true){
                messages = GroupChatApplication.getBackend().getMessagesToBeShown(
                        GroupChatApplication.getChatId()
                );
                if(messages.size() > 0){
                    System.out.println("Messages received");
                    controller.updateCurrentChat(messages);
                    break;
                }
            }
        }).start();
    }
}