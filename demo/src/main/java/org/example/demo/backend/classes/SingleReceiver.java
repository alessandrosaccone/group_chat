package org.example.demo.backend.classes;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.example.demo.backend.enums.MessageType;

public class SingleReceiver extends Thread {
    private final InetAddress ipAddress;
    private final NetworkManagerImpl networkManager;
    private DataInputStream inputStream;
    private Socket socket;

    public SingleReceiver(InetAddress ipAddress, NetworkManagerImpl networkManager) {
        this.ipAddress = ipAddress;
        this.networkManager = networkManager;
        socket = networkManager.getSockets(ipAddress);
    }

    @Override
    public void run() {
        try{
            // dummy condition (always true)
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // trying to retrieve a new connection if the old one has been lost
                    while (socket == null) {
                        Thread.sleep(1000); // waiting for 1 second before retrying
                        socket = networkManager.getSockets(ipAddress);
                    }

                    inputStream = new DataInputStream(socket.getInputStream());

                    while (!Thread.currentThread().isInterrupted()) {
                        String messageString = inputStream.readUTF();

                        Message message = parseMessage(messageString);

                        networkManager.newMessageReceived(message);
                    }
                } catch (IOException e) {
                    // notifying the NetworkManager that the connection has been probably lost
                    networkManager.connectionLost(ipAddress, socket);
                    socket = null;
                }catch (InterruptedException e) {
                    System.out.println("Thread interrupted: " + e.getMessage());
                    e.printStackTrace(); // Generic handling
                }
            }
        } finally{
            // Clean up resources (just the outputStream. Socket will be closed by the NetworkManager)
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException e) { System.out.println("Error closing the inputStream"); }
            }
        }
    }


    private Message parseMessage(String messageString) {

        String[] fields = messageString.split("\\|");

        InetAddress senderIP = null;
        String chatID = null;
        String text = null;
        MessageType type = null;
        HashMap<InetAddress,Integer> vectorClock = null;

        if (fields.length == 5) {
            try {
                senderIP = InetAddress.getByName(fields[0]);
                chatID = fields[1];
                text = fields[2];
                type = MessageType.valueOf(fields[3]);
                vectorClock = new HashMap<>();
                vectorClock.put(senderIP, Integer.valueOf(fields[4]));
            } catch (UnknownHostException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return new Message(senderIP, chatID, text, type, vectorClock);
    }
}
