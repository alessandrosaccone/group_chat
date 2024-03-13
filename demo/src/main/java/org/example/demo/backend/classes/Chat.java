package org.example.demo.backend.classes;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/*
Class representing one chat.
It's identified by a global ID (unique for all the nodes/participants) and maintains a vector clock representing
the view of the message order (related to the specific chat) of the node.
 */
public class Chat {
    private final String ID;
    private final ArrayList<InetAddress> participants;
    private HashMap<InetAddress,Integer> vectorClock;

    public Chat(String ID, ArrayList<InetAddress> participants) {
        this.participants= new ArrayList<>();
        this.vectorClock = new HashMap<>();
        // initializing attributes
        this.ID = ID;
        this.participants.addAll(participants);
        for(InetAddress addr: participants){
            vectorClock.put(addr,0);
        }
    }

    // classical getter: returns the ID of the chat
    public String getID() {
        return ID;
    }

    // classical getter: returns the list of nodes participating in the chat
    public ArrayList<InetAddress> getParticipants() {
        return participants;
    }

    // classical getter: return the vector clock associated to the chat
    public HashMap<InetAddress, Integer> getVectorClock() {
        return vectorClock;
    }

    // getter: returns the clock associated to a single node (participant)
    public Integer getParticipantClock(InetAddress participant){
        return this.vectorClock.get(participant);
    }

    // classical setter: allow to update the vector clock
    public void setVectorClock(HashMap<InetAddress, Integer> vectorClock) {
        this.vectorClock = vectorClock;
    }
}
