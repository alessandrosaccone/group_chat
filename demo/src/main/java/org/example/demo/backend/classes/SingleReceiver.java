package org.example.demo.backend.classes;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.example.demo.backend.enums.MessageType;

public class SingleReceiver extends Thread {
    private final InetAddress ipAddress;
    private final NetworkManagerImpl networkManager;

    public SingleReceiver(InetAddress ipAddress, NetworkManagerImpl networkManager) {
        this.ipAddress = ipAddress;
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = networkManager.getSockets(ipAddress);

                    if (socket == null) {
                        Thread.sleep(1000); // wait for 1 second before retrying
                        continue;
                    }

                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                    while (!Thread.currentThread().isInterrupted()) {
                        String messageString = inputStream.readUTF();

                        Message message = parseMessage(messageString);

                        networkManager.newMessageReceived(message);
                    }
                } catch (EOFException e) {
                    networkManager.connectionLost(ipAddress, null);
                } catch (IOException e) {
                    e.printStackTrace(); // Generic handling
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
            e.printStackTrace(); // Generic handling
            //Thread.currentThread().interrupt(); // Preserve the interruption status
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
