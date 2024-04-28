package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Random;

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
                // randomized time for repeating the request
                Random random = new Random(System.currentTimeMillis());
                Thread.sleep(Math.abs(random.nextLong()%1000));
                // trying to connect to nodeIP:hostPort
                Socket socket = new Socket(nodeIP, hostPort);
                // connection established: the NetworkManager is notified that the task has been completed
                System.out.println("CONNREQUESTOR: ["+ LocalTime.now()+"]"+"Connection established with node "+nodeIP+" port "+hostPort);
                networkManager.setNewConnection(nodeIP, socket);
                networkManager.RequestorTerminated(nodeIP);
                return;

            } catch (IOException e) {
                System.err.println("CONNREQUESTOR: ["+ LocalTime.now()+"]"+"Error while trying to connect to " + nodeIP + ":" + hostPort);
            } catch (InterruptedException ex) {
                //throw new RuntimeException(ex);
            }
        }
    }
}
