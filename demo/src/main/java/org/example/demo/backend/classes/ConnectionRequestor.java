package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionRequestor extends Thread {
    private final InetAddress nodeIP;
    private final int hostPort;
    private final NetworkManager networkManager;

    public ConnectionRequestor(InetAddress nodeIP, int hostPort, NetworkManager networkManager) {
        this.nodeIP = nodeIP;
        this.hostPort = hostPort;
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = new Socket(nodeIP, hostPort);
                networkManager.setNewConnection(nodeIP, socket);
                networkManager.RequestorTerminated(nodeIP);
                return;
            } catch (IOException e) {
                System.err.println("Errore durante la connessione al nodo " + nodeIP + ":" + hostPort);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
