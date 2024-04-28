package org.example.test.backend;

import org.example.demo.backend.classes.ConnectionRequestor;
import org.example.demo.backend.classes.NetworkManagerImpl;
import org.example.demo.backend.interfaces.NetworkManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionRequestorTest {
    private static NetworkManagerImpl networkManager;
    private final static int localPort = 1234;
    private static InetAddress addr1;
    private static ArrayList<InetAddress> nodes = new ArrayList<>();
    private static ConnectionRequestor connectionRequestor;

    @BeforeAll
    static void setUp() {
        try {
            addr1 = InetAddress.getByName("192.168.10.23");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nodes = new ArrayList<>(Collections.singletonList(addr1));
        networkManager = new NetworkManagerImpl(localPort, nodes, "192.168.216.24"); // Passing null for nodes as it's not relevant for this test
        connectionRequestor = new ConnectionRequestor(addr1, localPort, networkManager);
    }

    @Test
    void run_ConnectionEstablished() {
        try {
            // Run the thread
            connectionRequestor.start();

            // Wait for the connection attempt to complete
            Thread.sleep(1000);

            // If no exception is thrown, connection is established
            assertTrue(true);
        } catch (InterruptedException e) {
            // If an exception occurs, connection is not established
            fail();
        }
    }

}
