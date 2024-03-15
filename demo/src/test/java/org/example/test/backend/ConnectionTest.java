package org.example.test.backend;

import org.example.demo.backend.classes.ConnectionAcceptor;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

class ConnectionTest {
    private static NetworkManagerImpl networkManager;
    private final static int localPort = 1234;
    private static ArrayList<InetAddress> nodes = new ArrayList<>();
    private static InetAddress addr1, addr2, addr3;
    private static InetAddress local;

    private static ConnectionAcceptor connectionAcceptor;
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
        networkManager = new NetworkManagerImpl(localPort, nodes);
        try {
            serverSocket = new ServerSocket(localPort+1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connectionAcceptor = new ConnectionAcceptor(serverSocket, networkManager);
    }

    @Test
    void connectionAccepting() {
        connectionAcceptor.start();
    }
}
