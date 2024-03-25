package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
                networkManager.setNewConnection(clientSocket.getInetAddress(), clientSocket);
            } catch (IOException e) {
                System.out.println("Error accepting a connection in Connection Acceptor");
                e.printStackTrace();
            }
        }
    }
}
