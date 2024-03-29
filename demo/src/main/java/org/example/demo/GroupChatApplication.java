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
    static String fxml = "main-page.fxml";
    static Map<String, Scene> scenes = new HashMap<String, Scene>();
    private static final Map<String, GuiController> guiControllers = new HashMap<String, GuiController>();

    @Override
    public void start(Stage stage) throws IOException {
        setupApplication(stage);
    }

    private static void setupApplication(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GroupChatApplication.class.getResource(fxml));
        scenes.put(fxml, new Scene(fxmlLoader.load(), 900, 600));
        GuiController ctrl = fxmlLoader.getController();
        GroupChatApplication.guiControllers.put(fxml, ctrl);
        Scene scene = scenes.get("main-page.fxml");
        stage.setTitle("High Available Group Chat Application");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("Closing application...");
                System.out.println("APPLICATION CLOSED!!");
                System.exit(0);
            }
        });
        stage.setScene(scene);
        ((TitlePageController) GroupChatApplication.guiControllers.get("main-page.fxml")).updateInfo();
        stage.show();
    }

    public static void runApplication(Stage stage) {
        GroupChatApplication.setNetworkManager(new NetworkManagerImpl(1234,
                GroupChatApplication.getAddresses()));
        App.runApplication(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setNetworkManager(NetworkManager networkManager) {
        GroupChatApplication.networkManager = networkManager;
    }

    public static ArrayList<InetAddress> getAddresses() {
        return GroupChatApplication.addresses;
    }

    public static NetworkManager getBackend() {
        return GroupChatApplication.networkManager;
    }

    public static void addHostAddress(String ipAddress){
        try {
            addresses.add(InetAddress.getByName(ipAddress));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}