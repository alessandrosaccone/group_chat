package org.example.test.backend;

import org.example.demo.backend.classes.Message;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.example.demo.backend.enums.MessageType;
import org.junit.jupiter.api.*;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


class NetworkManagerImplTest {

    private static NetworkManagerImpl networkManager;
    private final static int localPort = 8888;
    private static ArrayList<InetAddress> nodes = new ArrayList<>();
    private static InetAddress addr1,addr2,addr3;
    private static InetAddress local;

    @BeforeEach
     void setUp() {
        try{
            addr1 = InetAddress.getByName("192.168.10.23");
            addr2 = InetAddress.getByName("192.168.0.55");
            addr3 = InetAddress.getByName("10.40.20.1");
            local = InetAddress.getLocalHost();
            String partialLocalAddress = InetAddress.getLocalHost().toString().split("/")[1];
            local = InetAddress.getByName(partialLocalAddress);
            nodes = new ArrayList<>(Arrays.asList(addr1,addr2,addr3));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        networkManager = new NetworkManagerImpl(localPort,nodes, "192.168.216.24");
    }

    @AfterEach
    void tearDown() {
        networkManager.closeAllConnections();
    }

    @Test
    void getLocalAddress() {
        Assertions.assertEquals(local, networkManager.getLocalAddress());
    }

    @Test
    void connectionLost() {
        Socket s = networkManager.getSockets().get(addr1);
        networkManager.connectionLost(addr1,s);
    }

    @Test
    void setNewConnection() {
        Socket s = networkManager.getSockets(addr2);
        networkManager.setNewConnection(addr1,s);
        Assertions.assertEquals(s,networkManager.getSockets(addr1));
    }

    @Test
    void setMessageToBeSent() {
        networkManager.getMessagesToBeSent(addr1); // messages erased

        networkManager.createNewChat(nodes);
        String chatID = networkManager.getChats().getFirst().getID();
        networkManager.setMessageToBeSent(chatID,"ciao",MessageType.TEXT_MESSAGE);
        // the first message is the one related to the chat created
        Assertions.assertEquals(2, networkManager.getMessagesToBeSent(addr1).size());
        Assertions.assertEquals(2, networkManager.getChat(chatID).getVectorClock().get(local));
    }

    @Test
    void createNewChat() {
        ArrayList<InetAddress> n = new ArrayList<>(nodes);
        n.add(local);
        networkManager.getMessagesToBeSent(addr1); // messages erased

        networkManager.createNewChat(nodes);
        Assertions.assertEquals(n, networkManager.getChats().getFirst().getParticipants());
        Assertions.assertEquals(1, networkManager.getMessagesToBeSent(addr1).size());
    }

    @Test
    void deleteChat() {
        networkManager.createNewChat(nodes);
        String chatID = networkManager.getChats().getFirst().getID();
        Assertions.assertNotNull(networkManager.getChat(chatID));
        networkManager.deleteChat(chatID);
        Assertions.assertNull(networkManager.getChat(chatID));
    }

    @Test
    void newMessageReceivedCreation() {
        HashMap<InetAddress, Integer> vectorClock = new HashMap<>();
        vectorClock.put(addr1, 0);
        vectorClock.put(addr2, 1);
        vectorClock.put(addr3, 0);
        vectorClock.put(local, 0);

        ArrayList<InetAddress> n = new ArrayList<>(nodes);
        n.add(local);
        // creation (and rejection - send) test
        Message creationMessage = new Message(addr2, "2222", n.toString(), MessageType.CREATION_REQUEST, vectorClock);
        Assertions.assertNull(networkManager.getChat("2222"));

        networkManager.getMessagesToBeSent(addr2); // messages erased
        networkManager.newMessageReceived(creationMessage);
        Assertions.assertEquals(0, networkManager.getMessagesToBeSent(addr2).size());

        Assertions.assertNotNull(networkManager.getChat("2222"));

        networkManager.getMessagesToBeSent(addr2); // messages erased
        networkManager.newMessageReceived(creationMessage);
        Assertions.assertEquals(1, networkManager.getMessagesToBeSent(addr2).size());
    }
    @Test
    void newMessageReceivedRejection() {
        HashMap<InetAddress, Integer> vectorClock = new HashMap<>();
        // rejection received
        networkManager.createNewChat(nodes);
        String chatIDtoReject = networkManager.getChats().getFirst().getID();
        vectorClock.put(addr1, 0);
        vectorClock.put(addr2, 0);
        vectorClock.put(addr3, 1);
        vectorClock.put(local, 1);
        Message rejectionMessage = new Message(addr3, chatIDtoReject, "", MessageType.CREATION_REJECT, vectorClock);
        networkManager.newMessageReceived(rejectionMessage);
        Assertions.assertNull(networkManager.getChat(chatIDtoReject));
    }

    @Test
    void newMessageReceivedDelete() {
        HashMap<InetAddress, Integer> vectorClock = new HashMap<>();
        // deletion order
        networkManager.createNewChat(nodes);
        String chatIDtoDelete = networkManager.getChats().getFirst().getID();
        vectorClock.put(addr1, 0);
        vectorClock.put(addr2, 0);
        vectorClock.put(addr3, 1);
        vectorClock.put(local, 1);
        Message DeleteMessage = new Message(addr3, chatIDtoDelete, "", MessageType.DELETION_ORDER, vectorClock);
        networkManager.newMessageReceived(DeleteMessage);
        Assertions.assertNull(networkManager.getChat(chatIDtoDelete));
    }

    @Test
    void newMessageReceived(){
        HashMap<InetAddress, Integer> vectorClock = new HashMap<>();
        // text message
        // not in order
        networkManager.createNewChat(nodes);
        String chat = networkManager.getChats().getFirst().getID();
        vectorClock.put(addr1,1);
        vectorClock.put(addr2,0);
        vectorClock.put(addr3,1);
        vectorClock.put(local,1);
        Message notOrderedMessage = new Message(addr3, chat, "ciao",MessageType.TEXT_MESSAGE, vectorClock);

        networkManager.getMessagesToBeShown(chat); // erase the queue

        networkManager.newMessageReceived(notOrderedMessage);
        Assertions.assertEquals(0, networkManager.getMessagesToBeShown(chat).size());

        vectorClock.replace(addr1,1);
        vectorClock.replace(addr2,0);
        vectorClock.replace(addr3,0);
        vectorClock.replace(local,1);
        Message OrderedMessage = new Message(addr1, chat, "ciao2",MessageType.TEXT_MESSAGE, vectorClock);
        networkManager.newMessageReceived(OrderedMessage);
        Assertions.assertEquals(2, networkManager.getMessagesToBeShown(chat).size());
    }
}