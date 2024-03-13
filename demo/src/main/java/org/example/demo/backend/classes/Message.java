package org.example.demo.backend.classes;

import org.example.demo.backend.enums.MessageType;

import java.net.InetAddress;
import java.util.HashMap;

/*
Class representing one message.
 */
public class Message {
    private final InetAddress senderIP;
    private final String chatID, message;
    private final MessageType messageType;
    private final HashMap<InetAddress,Integer> vectorClock;

    public Message(InetAddress senderIP, String chatID, String message, MessageType messageType, HashMap<InetAddress,Integer> vectorClock) {
        this.senderIP = senderIP;
        this.chatID = chatID;
        this.message = message;
        this.messageType = messageType;
        this.vectorClock = new HashMap<>(vectorClock);
    }

    // classical getter: returns the IP of the sender of the message
    public InetAddress getSenderIP() {
        return senderIP;
    }

    // classical getter: returns the global ID related to the chat in which the message has been/has to be sent
    public String getChatID() {
        return chatID;
    }

    // classical getter: returns the string representing the text of the message
    public String getMessage() {
        return message;
    }

    // classical getter: returns the TYPE of the message (see the ENUM MessageType)
    public MessageType getMessageType() {
        return messageType;
    }

    // classical getter: return the vector clock of the message
    public HashMap<InetAddress,Integer> getVectorClock(){
        return this.vectorClock;
    }
}
