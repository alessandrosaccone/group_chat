package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class ConnectionAcceptor extends Thread{
    private final ServerSocket serverSocket;
    private final NetworkManager networkManager;

    public ConnectionAcceptor(ServerSocket serverSocket, NetworkManager networkManager){
        this.serverSocket = serverSocket;
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        // dummy condition
        while (!Thread.currentThread().isInterrupted()) {
            try {
                //serverSocket.setSoTimeout(0);
                Socket clientSocket = serverSocket.accept();
                //serverSocket.setSoTimeout(0);
                System.out.println("CONNACCEPTOR: ["+ LocalTime.now()+"]"+"New connection accepted with node "+ clientSocket.getInetAddress());
                networkManager.setNewConnection(clientSocket.getInetAddress(), clientSocket);
            } catch (IOException e) {
                System.out.println("CONNACCEPTOR: ["+ LocalTime.now()+"]"+"Error accepting a connection in Connection Acceptor");
                e.printStackTrace();
            }
        }
    }
}
