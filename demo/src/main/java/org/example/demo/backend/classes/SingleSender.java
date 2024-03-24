package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SingleSender extends Thread {
    private final InetAddress ipAddress;
    private final NetworkManager networkManager;
    private Socket socket;
    private DataOutputStream outputStream;

    public SingleSender(InetAddress ipAddress, NetworkManager networkManager) {
        this.ipAddress = ipAddress;
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = networkManager.getSockets(ipAddress);
                    if (socket != null) {
                        outputStream = new DataOutputStream(socket.getOutputStream());
                        ArrayList<Message> toBeSent = networkManager.getMessagesToBeSent(ipAddress);
                        for (Message message : toBeSent) {
                            String msg = parseMessage(message);
                            outputStream.writeUTF(msg);
                        }
                        // Clear the list of messages after sending
                        toBeSent.clear();
                    } else
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        networkManager.connectionLost(ipAddress, socket);
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                // Handle thread interruption
                System.out.println("Thread interrupted: " + e.getMessage());
                } finally {
                    // Clean up resources
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            System.out.println("Error closing the outputStream");
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Handle the exception
                        }
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


