package org.example.demo.backend.classes;

import org.example.demo.backend.interfaces.NetworkManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;

public class SingleSender extends Thread {
    private final InetAddress ipAddress;
    private final NetworkManager networkManager;
    private Socket socket;
    private DataOutputStream outputStream;
    private ArrayList<Message> toBeSent, toBeRemoved;

    public SingleSender(InetAddress ipAddress, NetworkManager networkManager) {
        this.ipAddress = ipAddress;
        this.networkManager = networkManager;
        socket = networkManager.getSockets(ipAddress);
        toBeSent = new ArrayList<>();
        toBeRemoved = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            // dummy condition (always true)
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // trying to retrieve a new connection if the old one has been lost
                    while(socket==null){
                        System.out.println("SENDER: ["+ LocalTime.now()+"]"+"Socket with node "+ ipAddress+ "corrupted, waiting for a recovery...");
                        Thread.sleep(1000);
                        socket = networkManager.getSockets(ipAddress);
                        if(socket!=null){
                            outputStream = new DataOutputStream(socket.getOutputStream());
                            System.out.println("SENDER: ["+ LocalTime.now()+"] Output stream correctly obtained");
                        }
                    }

                    toBeSent.addAll(networkManager.getMessagesToBeSent(ipAddress));

                    for (Message message : toBeSent) {
                        String msg = parseMessage(message);
                        outputStream.writeUTF(msg);
                        // saving all the element that have been removed (needed for consistency reasons, see exception handling section)
                        toBeRemoved.add(message);
                        System.out.println("SENDER: ["+ LocalTime.now()+"]"+"Message sent to node "+ ipAddress.toString()+
                                "\nMessage: "+message.getChatID()+"-"+message.getMessage());
                    }

                    toBeSent.clear();
                    toBeRemoved.clear();
                    Thread.sleep(1000);

                } catch (IOException e) {
                    System.out.println("SENDER: ["+ LocalTime.now()+"]"+"IOEXCEPTION: "+e.getMessage());
                    System.out.println("SENDER: ["+ LocalTime.now()+"]"+"Socket with node "+ipAddress+ " detected as corrupted");
                    // removing from the List toBeSent the already sent messages
                    for(Message message : this.toBeRemoved){ this.toBeSent.remove(message); }
                    this.toBeRemoved.clear();
                    networkManager.connectionLost(ipAddress, socket);
                    socket = null;
                } catch (InterruptedException e) {
                    // Handle thread interruption
                    System.out.println("SENDER: ["+ LocalTime.now()+"]"+"Thread interrupted: " + e.getMessage());
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
        res += message.getVectorClock().toString();
        return res;
    }
}


