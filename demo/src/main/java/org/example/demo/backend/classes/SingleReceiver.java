package org.example.demo.backend.classes;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
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
                        System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Socket with node "+ ipAddress+ "corrupted, waiting for a recovery...");
                        Thread.sleep(1000); // waiting for 1 second before retrying
                        socket = networkManager.getSockets(ipAddress);
                    }

                    inputStream = new DataInputStream(socket.getInputStream());

                    while (!Thread.currentThread().isInterrupted()) { // dummy control as a WHILE(TRUE)
                        String messageString = inputStream.readUTF();

                        Message message = parseMessage(messageString);

                        networkManager.newMessageReceived(message);
                        System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"New message received and sent to ne NETMANAGER");
                    }
                } catch (IOException e) {
                    // notifying the NetworkManager that the connection has been probably lost
                    networkManager.connectionLost(ipAddress, socket);
                    socket = null;
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Socket with node "+ipAddress+ " detected as corrupted");
                }catch (InterruptedException e) {
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Thread interrupted: " + e.getMessage());
                    e.printStackTrace(); // Generic handling
                }
            }
        } finally{
            // Clean up resources (just the outputStream. Socket will be closed by the NetworkManager)
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException e) { System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Error closing the inputStream"); }
            }
        }
    }


    private Message parseMessage(String messageString) {

        String[] fields = messageString.split("\\|");

        InetAddress senderIP = this.ipAddress;
        String chatID = null;
        String text = null;
        MessageType type = null;
        HashMap<InetAddress,Integer> vectorClock = null;

        if (fields.length == 5) {
            try {

                //senderIP = InetAddress.getByName(fields[0]); - Incorrect parsing, no need of taking it from the message
                chatID = fields[1];
                text = fields[2];
                type = MessageType.valueOf(fields[3]);
                vectorClock = new HashMap<>();
                vectorClock.put(senderIP, Integer.valueOf(fields[4]));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return new Message(senderIP, chatID, text, type, vectorClock);
    }
}
