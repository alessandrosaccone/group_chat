package org.example.test.backend;

import org.example.demo.backend.classes.Chat;
import org.junit.jupiter.api.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


class ChatTest {

    private Chat chat;
    private ArrayList<InetAddress> nodes;
    private InetAddress addr1,addr2,addr3;

    @BeforeEach
    void setUp() {
        try{
            addr1 = InetAddress.getByName("192.168.10.23");
            addr2 = InetAddress.getByName("192.168.0.55");
            addr3 = InetAddress.getByName("10.40.20.1");
            nodes = new ArrayList<>(Arrays.asList(addr1,addr2,addr3));
            chat = new Chat("1234",nodes);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getID() {
        Assertions.assertEquals("1234",chat.getID());
    }

    @Test
    void getParticipants() {
        InetAddress[] addresses = {addr1,addr2,addr3};
        Assertions.assertArrayEquals(addresses,chat.getParticipants().toArray());
    }

    @Test
    void getVectorClock() {
        HashMap<InetAddress, Integer> clock = new HashMap<>();
        clock.put(addr1, 0);
        clock.put(addr2, 0);
        clock.put(addr3, 0);
        for (InetAddress node : chat.getVectorClock().keySet()) {
            Assertions.assertEquals(clock.get(node), chat.getVectorClock().get(node));
        }

    }

    @Test
    void setVectorClock() {
        HashMap<InetAddress, Integer> clock = new HashMap<>();
        clock.put(addr1, 1);
        clock.put(addr2, 4);
        clock.put(addr3, 2);
        chat.setVectorClock(clock);
        for (InetAddress node : chat.getVectorClock().keySet()) {
            Assertions.assertEquals(clock.get(node), chat.getVectorClock().get(node));
        }
    }
}