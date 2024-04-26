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
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"InputStream correctly obtained");

                    while (!Thread.currentThread().isInterrupted()) { // dummy control as a WHILE(TRUE)
                        String messageString = inputStream.readUTF();

                        Message message = parseMessage(messageString);

                        System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"New message received\n"+
                                "Message: "+message.getMessageType()+"-"+message.getMessage()+"-"+message.getChatID());

                        networkManager.newMessageReceived(message);
                    }
                } catch (IOException e) {
                    // notifying the NetworkManager that the connection has been probably lost
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"IOEXCEPTION: "+e.getMessage());
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Socket with node "+ipAddress+ " detected as corrupted");
                    networkManager.connectionLost(ipAddress, socket);
                    socket = null;
                }catch (InterruptedException e) {
                    System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"Thread interrupted: " + e.getMessage());
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
        HashMap<InetAddress,Integer> vectorClock = new HashMap<>();

        if (fields.length == 5) {
            try {

                //senderIP = InetAddress.getByName(fields[0]); - Incorrect parsing, no need of taking it from the message
                chatID = fields[1];
                text = fields[2];
                type = MessageType.valueOf(fields[3]);
                String trimmedString = fields[4].trim().substring(1,fields[4].length()-1); // cutting off the { ... } separators
                for(String entry: trimmedString.split(",")){
                    InetAddress key = InetAddress.getByName(entry.split("=")[0].split("/")[1]);
                    int value = Integer.parseInt(entry.split("=")[1]);
                    vectorClock.put(key,value);
                }

                vectorClock.put(senderIP, Integer.valueOf(fields[4]));
            } catch (IllegalArgumentException e) {
                System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"ERROR on parsing the message:"+e.getMessage());
            } catch (UnknownHostException e) {
                System.out.println("RECEIVER: ["+ LocalTime.now()+"]"+"ERROR on parsing the address while reading the vector clock:"+e.getMessage());
            }
        }

        return new Message(senderIP, chatID, text, type, vectorClock);
    }
}
