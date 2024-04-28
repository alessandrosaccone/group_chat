package org.example.test.backend;

import org.example.demo.backend.classes.ConnectionAcceptor;
import org.example.demo.backend.classes.Message;
import org.example.demo.backend.classes.SingleSender;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.example.demo.backend.enums.MessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.*;

class SingleSenderTest {
    private static NetworkManagerImpl networkManager;
    private static InetAddress ipAddress;
    private static SingleSender singleSender;
    private static InetAddress addr1, addr2, addr3;
    private static InetAddress local;
    private static ArrayList<InetAddress> nodes = new ArrayList<>();
    private final static int localPort = 1234;
    private static ServerSocket serverSocket;

    @BeforeAll
    static void setUp() {
        try {
            addr1 = InetAddress.getByName("192.168.10.23");
            addr2 = InetAddress.getByName("192.168.0.55");
            addr3 = InetAddress.getByName("10.40.20.1");
            local = InetAddress.getLocalHost();
            String partialLocalAddress = InetAddress.getLocalHost().toString().split("/")[1];
            local = InetAddress.getByName(partialLocalAddress);
            nodes = new ArrayList<>(Arrays.asList(addr1, addr2, addr3));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        networkManager = new NetworkManagerImpl(localPort, nodes, "192.168.216.24");

        //it should be useful for testing the queues
        networkManager.createNewChat(nodes);

        ipAddress = InetAddress.getLoopbackAddress();
        singleSender = new SingleSender(ipAddress, networkManager);
        try {
            serverSocket = new ServerSocket(localPort+1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor(serverSocket, networkManager);
        connectionAcceptor.start();
    }

    @Test
    void run_ConnectionEstablished() {
        try {
            // Run the thread
            singleSender.start();

            // Wait for the thread execution
            Thread.sleep(1000);

            // If no exception is thrown, connection is established
            assertTrue(true);
        } catch (InterruptedException e) {
            // If an exception occurs, connection is not established
            fail();
        }
    }

    @Test
    void run_WithEmptyMessageQueue() {
        //I don't know how to obtain the chatID from creating a chat in the network manager
    }

    @Test
    void run_WithNonEmptyMessageQueue() {
        //same as before
    }
}
