package org.example.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

    private NetworkManager backend;

    static ArrayList<InetAddress> addresses = new ArrayList<>();

    static List<String> fxmls = new ArrayList<>(Arrays.asList("main-page.fxml", "chat-creation.fxml", "chat.fxml"));

    static Map<String, Scene> scenes = new HashMap<String, Scene>();

    private final Map<String, GuiController> guiControllers = new HashMap<String, GuiController>();

    @Override
    public void start(Stage stage) throws IOException {
        backend = new NetworkManagerImpl(1234, addresses);
        for(String fxml : fxmls){
            FXMLLoader fxmlLoader = new FXMLLoader(GroupChatApplication.class.getResource(fxml));
            scenes.put(fxml, new Scene(fxmlLoader.load(), 900, 600));
            GuiController ctrl = fxmlLoader.getController();
            ctrl.setController(backend);
            ctrl.setAddresses(addresses);
            guiControllers.put(fxml, ctrl);
        }
        Scene scene = scenes.get("main-page.fxml");
        stage.setTitle("Group Chat Application");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("Closing...");
            }
        });
        stage.setScene(scene);
        ((TitlePageController) guiControllers.get("main-page.fxml")).updateIp();
        stage.show();
        scenes.replace("main-page.fxml", scene);
    }

    public static void main(String[] args) {
        try {
            addresses.add(InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            System.err.println("Host not found");
            throw new RuntimeException(e);
        }
        launch();
    }
}