package org.example.test.backend;

import org.example.demo.backend.classes.Message;
import org.example.demo.backend.enums.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;


class MessageTest {

    private InetAddress senderIP;
    private Message message;
    private String chatID, text;
    private MessageType type;
    private InetAddress addr1,addr2,addr3;
    private final HashMap<InetAddress, Integer> clock = new HashMap<>();

    @BeforeEach
    void setUp() {
        try{
            senderIP = InetAddress.getByName("192.168.10.9");
            addr1 = InetAddress.getByName("192.168.10.23");
            addr2 = InetAddress.getByName("192.168.0.55");
            addr3 = InetAddress.getByName("10.40.20.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        chatID = "1234";
        text = "ciao";
        type = MessageType.TEXT_MESSAGE;
        clock.put(addr1, 1);
        clock.put(addr2, 4);
        clock.put(addr3, 2);
        message = new Message(senderIP,chatID,text,type,clock);
    }

    @Test
    void getSenderIP() {
        Assertions.assertEquals(senderIP, message.getSenderIP());
    }

    @Test
    void getChatID() {
        Assertions.assertEquals(chatID, message.getChatID());
    }

    @Test
    void getMessage() {
        Assertions.assertEquals(text, message.getMessage());
    }

    @Test
    void getMessageType() {
        Assertions.assertEquals(type, message.getMessageType());
    }

    @Test
    void getVectorClock() {
        for (InetAddress node : message.getVectorClock().keySet()) {
            Assertions.assertEquals(clock.get(node), message.getVectorClock().get(node));
        }
    }
}