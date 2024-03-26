package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SingleSender extends Thread {
    private final InetAddress ipAddress;
    private final NetworkManager networkManager;
    private Socket socket;
    private DataOutputStream outputStream;

    public SingleSender(InetAddress ipAddress, NetworkManager networkManager) {
        this.ipAddress = ipAddress;
        this.networkManager = networkManager;
        socket = networkManager.getSockets(ipAddress);
    }

    @Override
    public void run() {
        try {
            // dummy condition (always true)
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // trying to retrieve a new connection if the old one has been lost
                    while(socket==null){
                        Thread.sleep(1000);
                        socket = networkManager.getSockets(ipAddress);
                    }

                    outputStream = new DataOutputStream(socket.getOutputStream());
                    ArrayList<Message> toBeSent = networkManager.getMessagesToBeSent(ipAddress);
                    for (Message message : toBeSent) {
                        String msg = parseMessage(message);
                        outputStream.writeUTF(msg);
                    }
                    // Clear the list of messages after sending
                    toBeSent.clear();

                } catch (IOException e) {
                    networkManager.connectionLost(ipAddress, socket);
                    socket = null;
                } catch (InterruptedException e) {
                    // Handle thread interruption
                    System.out.println("Thread interrupted: " + e.getMessage());
                }
            }
        } finally {
            // Clean up resources (just the outputStream. Socket will be closed by the NetworkManager)
            if (outputStream != null) {
                try { outputStream.close(); } catch (IOException e) { System.out.println("Error closing the outputStream"); }
            }
        }
    }

    private String parseMessage(Message message) {
        String res = "";
        res += message.getSenderIP() + "|";
        res += message.getChatID() + "|";
        res += message.getMessage() + "|";
        res += message.getMessageType() + "|";
        res += message.getVectorClock().get(message.getSenderIP());
        return res;
    }
}


