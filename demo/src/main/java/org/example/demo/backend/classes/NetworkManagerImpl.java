package org.example.demo.backend.classes;

import org.example.demo.backend.enums.MessageType;
import org.example.demo.backend.interfaces.NetworkManager;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class NetworkManagerImpl implements NetworkManager {
    private final HashMap<InetAddress, ArrayList<Message>> messagesToBeSent;
    private final HashMap<String, ArrayList<Message>> chatMessagesToBeShown;
    private final HashMap<String, ArrayList<Message>> chatMessageWaiting;
    private final HashMap<String,ArrayList<Message>> messagesForNotExistingChats;
    private final ArrayList<String> IDProposal;
    private final HashMap<String,Chat> chats;
    private final ArrayList<InetAddress> nodes;
    private final HashMap<InetAddress, Socket> sockets;
    private ServerSocket serverSocket;
    private InetAddress localAddress;
    private final int acceptPort;
    private ConnectionAcceptor connectionAcceptor;
    private final HashMap<InetAddress,ConnectionRequestor> connectionRequestors;
    private final ArrayList<SingleReceiver> receivers;
    private final ArrayList<SingleSender> senders;

    public NetworkManagerImpl(int localPort, ArrayList<InetAddress> nodes, String localHost){
        // parameters initialization
        this.messagesToBeSent = new HashMap<>();
        this.chatMessagesToBeShown = new HashMap<>();
        this.chatMessageWaiting = new HashMap<>();
        this.messagesForNotExistingChats = new HashMap<>();
        for(InetAddress node: nodes){
            this.messagesToBeSent.put(node, new ArrayList<>());
        }
        this.IDProposal = new ArrayList<>();
        this.chats = new HashMap<>();
        this.connectionRequestors = new HashMap<>();
        this.receivers = new ArrayList<>();
        this.senders = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);
        this.sockets = new HashMap<>();
        this.acceptPort = localPort;
        for(InetAddress node: nodes){ this.sockets.put(node,null);}
        try {
            this.serverSocket = new ServerSocket(localPort);
            //String partialLocalAddress = InetAddress.getLocalHost().toString().split("/")[1];
            //just for the project...
            this.localAddress = InetAddress.getByName(localHost);
        } catch (IOException e) {
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Server Socket error: "+ e.getMessage());
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Application terminated due to a set up error.");
            System.exit(-1);

        }
        // threads initialization
        threadInitializer();
    }

                                // ------- thread management part ------- //

    private synchronized void threadInitializer(){
        // initializing and starting the ConnectionAcceptor thread over the serverSocket
        this.connectionAcceptor = new ConnectionAcceptor(serverSocket, this);
        this.connectionAcceptor.start();
        /* initializing and starting (for each IP address of the nodes we need to connect to):
           - one ConnectionRequestor thread
           - one SingleReceiver thread
           - one SingleSender thread
         */
        for(InetAddress node: this.nodes){
            ConnectionRequestor requestor = new ConnectionRequestor(node,this.acceptPort ,this);
            this.connectionRequestors.put(node, requestor);
            requestor.start();
            SingleReceiver receiver = new SingleReceiver(node, this);
            this.receivers.add(receiver);
            receiver.start();
            SingleSender sender = new SingleSender(node, this);
            this.senders.add(sender);
            sender.start();
        }
        System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" All thread correctly started");
    }

    @Override
    // this method just "remove" from the set of requestor the Requestor that is just terminated
    public synchronized void RequestorTerminated(InetAddress node) {
        this.connectionRequestors.replace(node,null);
        System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Requestor for node "+ node.toString()+ "has terminated its job");
    }

    // this method brutally terminates all the thread and related socket connections
    @Override
    public synchronized void closeAllConnections() {
        try{
            //TODO: convert in log
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Number of sender to close: "+this.senders.size());
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Number of receiver to close: "+this.receivers.size());
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" Number of requestor to close: "+this.connectionRequestors.size());
            // interrupting connectionAcceptor
            this.connectionAcceptor.interrupt();
            this.serverSocket.close();
            // interrupting senders and receivers
            for(SingleSender sender: this.senders){ sender.interrupt();}
            for(SingleReceiver receiver: this.receivers) { receiver.interrupt();}
            // interrupting nodeRequestor
            for(InetAddress node: this.nodes){
                if(this.connectionRequestors.get(node) != null){ this.connectionRequestors.get(node).interrupt();}
                if(this.sockets.get(node) != null) { this.sockets.get(node).close(); }
                //TODO: convert in log
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+" All connections with " + node.getHostAddress() +" closed");
            }
        } catch (IOException e) {
            // In any case the application closes
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+ "Error on closing sockets");
            throw new RuntimeException(e);
        }

    }

                                // ------- network management part ------- //
    @Override
    public synchronized InetAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    // this method return the set of references of all the sockets sorted by IPAddress
    public synchronized HashMap<InetAddress, Socket> getSockets() {
        return this.sockets;
    }

    @Override
    // this method return the reference to the socket related to the passed IPAddress
    public synchronized Socket getSockets(InetAddress IPAddress) {
        return this.sockets.get(IPAddress);
    }

    @Override
    /* when invoked, this method deletes the reference to the corrupted socket (related to the passed node)
       from the list of sockets (if exists) and invoke a new ConnectionRequestor (if not already exists)
     */
    public synchronized void connectionLost(InetAddress node, Socket corruptedSocket) {
        System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Connection lost with node "+node.toString());
        // checking if the corrupted socket has been already removed but no Requestor related to the node IP is working on
        if(this.sockets.get(node) == null && this.connectionRequestors.get(node) == null ) {
            ConnectionRequestor requestor = new ConnectionRequestor(node, this.acceptPort,this);
            this.connectionRequestors.replace(node, requestor);
            requestor.start();
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"New requestor for node "+ node+ " has been activated");
        }
        // checking if it's the first time someone notify that the socket is corrupted
        else if(this.sockets.get(node)!=null && this.sockets.get(node).equals(corruptedSocket)){
            this.sockets.replace(node, null);
            ConnectionRequestor requestor = new ConnectionRequestor(node, this.acceptPort,this);
            this.connectionRequestors.replace(node, requestor);
            requestor.start();
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"New requestor for node "+ node+ " has been activated");
        }
        else {
        /*
        All the other cases:
        - socket NULL and requestor NOT NULL --> do nothing (someone is already trying to establish a new connection)
        - socket NOT NULL and socket different --> do nothing (the new connection has been already established)
        - socket NOT NULL and requestor NOT NULL --> it shouldn't happen
        NOTE: It's assumed that when a new connection is established, the Requestor thread updates the socket list
              and remove itself from the requestor list before terminating.
         */
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"No new requestor needed for node " + node +
                    "\nAvailable connections: "+ this.sockets.keySet());
        }
    }

    @Override
    /* this method is invoked when an Acceptor/Requestor succeeds on establish a connection. It verifies that a socket with
       that node do not already exists and in positive case, updates the set of socket. Otherwise, just close the socket
       NOTE: a better implementation would terminate the related thread (if exists) but it would require a sophisticated thread
       termination management. Anyway, we can assume that eventually also the Acceptor will succeed on establish a connection and,
       since it already exists, it will be closed (by both of the sides) and the thread will terminate.
      */
    public synchronized void setNewConnection(InetAddress node, Socket newSocket) {
        if(this.sockets.putIfAbsent(node, newSocket) != null){
            try{
                newSocket.close();
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+ "Connection with node "+node.toString()+ "already existing. Connection discarded\n"+
                        "Available connections: "+ this.sockets.keySet());

            } catch (IOException e) {
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Error on closing socket for node "+node.toString());
                throw new RuntimeException(e);
            }
        }
    }

                            // ------- application level chat management ------- //

    @Override
    // this method return the set of messages to be sent at the specific nodes.
    public synchronized ArrayList<Message> getMessagesToBeSent(InetAddress receiver) {
        ArrayList<Message> toBeSent = new ArrayList<>(this.messagesToBeSent.get(receiver));
        this.messagesToBeSent.replace(receiver, new ArrayList<>());
        return toBeSent;
    }

    @Override
    // this method returns the set of all messages to be shown at the application level sorted by chat and
    // the deletes them.
    public synchronized HashMap<String,ArrayList<Message>> getMessagesToBeShown() {
        HashMap<String,ArrayList<Message>> toBeShown = new HashMap<>();
        for(Chat chat: this.chats.values()){
            toBeShown.put(chat.getID(),this.getMessagesToBeShown(chat.getID()));
        }
        return toBeShown;
    }

    @Override
    // this method returns the set of messages to be shown at the application level filtered by chat and deletes them
    // NOTE that in case the chat do no exist anymore, it also deletes it from the keys of the queue
    public synchronized ArrayList<Message> getMessagesToBeShown(String chatID) {
        ArrayList<Message> toBeShown = new ArrayList<>();
        if(this.chatMessagesToBeShown.containsKey(chatID)){
            toBeShown = new ArrayList<>(this.chatMessagesToBeShown.get(chatID));
            this.chatMessagesToBeShown.replace(chatID, new ArrayList<>());
        }
        // checking if the chat has been just created (REDUNDANT --> it should never happen)
        if(this.chats.containsKey(chatID) && !this.chatMessagesToBeShown.containsKey(chatID)){
            this.chatMessagesToBeShown.replace(chatID, new ArrayList<>());
        }
        // checking if the chat has been deleted
        if (!this.chats.containsKey(chatID)) {
            this.chatMessagesToBeShown.remove(chatID);
        }
        return toBeShown;
    }

    @Override
    /* this method can be invoked by the application level or by the NetworkManager class itself. It adds the new message
        to the queue related to the receiver nodes and increments its own vector clock.
     */
    public synchronized void setMessageToBeSent(String chatID, String text, MessageType type) {
        Chat chat = this.chats.get(chatID);
        if(chat!=null){
            // incrementing vector clock of the related chat
            chat.getVectorClock().replace(this.localAddress, chat.getVectorClock().get(this.localAddress)+1);
            this.chats.replace(chat.getID(), chat);
            // creating the message
            Message message = new Message(this.localAddress,chatID,text,type,chat.getVectorClock());
            // putting the message on the queue of each node participating in the chat (not considering myself)
            for(InetAddress node: chat.getParticipants()){
                if(!node.equals(this.localAddress)){
                    ArrayList<Message> newMessages = new ArrayList<>(this.messagesToBeSent.get(node));
                    newMessages.add(message);
                    this.messagesToBeSent.replace(node, newMessages);
                }
            }
            // adding the message on the local queue of the messages to be shown
            ArrayList<Message> newMessagesLocal = new ArrayList<>(this.chatMessagesToBeShown.get(chatID));
            newMessagesLocal.add(message);
            this.chatMessagesToBeShown.replace(chatID, newMessagesLocal);
        }
        // TODO: implement with exception
        // Until now, if the chat do not exists, the message is simply not sent
        else{ System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"ERROR: chatID not existing!");}
    }

    @Override
    /* when invoked, this method start the procedure for the creation of a new chat. It calculates a possible global
        unique ID and sent it to all the nodes participating in the new chat.
     */
    public synchronized String createNewChat(ArrayList<InetAddress> participants){
        // since this method can be invoked only by the application level, I need to add the local address to the
        // list of participants
        ArrayList<InetAddress> participantsComplete = new ArrayList<>(participants);
        if(!participantsComplete.contains(this.localAddress)){ participantsComplete.add(this.localAddress); }
        // creating a random unique possible ID and verify it do not already exist locally
        String uniqueID;
        do{
            Random random = new Random(System.nanoTime());
            uniqueID = Integer.toString(Math.abs((int)(random.nextDouble()*Math.pow(10,6))));
        } while( !this.isIDUnique(uniqueID) );
        // putting a new entry on the set of IDProposal and in the sets related to the chats
        Chat newChat = new Chat(uniqueID,participantsComplete);
        this.IDProposal.add(uniqueID);
        this.chats.put(uniqueID, newChat);
        this.chatMessagesToBeShown.put(uniqueID, new ArrayList<>());
        this.chatMessageWaiting.put(uniqueID, new ArrayList<>());
        System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"New chat created with ID "+uniqueID);
        // sending the creation message (with the set of participants) to all the participants
        this.setMessageToBeSent(uniqueID, participantsComplete.toString(),MessageType.CREATION_REQUEST);
        return uniqueID;
    }

    @Override
    /* This method can be invoked only by the application level. It creates a new message of type DELETION_ORDER,
        put it on the queues of the right nodes (invoking the method setMessageTobeSent) and delete al the local
        references to that chat
     */
    public synchronized void deleteChat(String chatID){
        if(this.chats.get(chatID)!=null){
           this.setMessageToBeSent(chatID,"",MessageType.DELETION_ORDER);
           this.chats.remove(chatID);
           System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Chat "+chatID+" deleted");
        }
        // TODO: implement with exceptions
        // Until now, if the chat do not exists, simply do nothing
        else{ System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"ERROR: chatID not existing!"); }
    }

    @Override
    // this method returns the list of current active chats
    public synchronized ArrayList<Chat> getChats(){
        return new ArrayList<>(this.chats.values());
    }

    @Override
    // this method returns the chat with the ID passed as parameter
    // it has been implemented as an ArrayList.get method. If the object do not exists, it return null
    // TODO: possible implementation: use exceptions
    public synchronized Chat getChat(String chatID){
        Chat chat = this.chats.get(chatID);
        if(chat==null){
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"ERROR! Requested chat not exists!");
        }
        return chat;
    }

                            // ------- Thread level message management ------- //
    @Override
    // this method return the vector clock related to the passed chat
    // it has been implemented as an ArrayList.get method. If the object do not exists, it return null
    // TODO: possible implementation: use exceptions
    public synchronized HashMap<InetAddress, Integer> getChatVectorClock(String chatID) {
        Chat chat = this.chats.get(chatID);
        if(chat == null){
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"ERROR: chatID not existing!");
            return null;
        }
        return chat.getVectorClock();
    }

    @Override
    /* this method manages the reception of the messages.
       First, It verifies if the message is coherent with the related chat vector clock and decide what to do
       (increment the vector clock of the chat or put the message in the waiting queue). This is done by invoking
       the private method isVectorClockCoherent().
       If the vector clock was updated, manages the messages and redo the verification for the message in the
       waiting queue of the related chat. Otherwise, does nothing.
     */
    public synchronized void newMessageReceived(Message newMessage) {
        String chatID = newMessage.getChatID();
        String text = newMessage.getMessage();
        InetAddress senderIP = newMessage.getSenderIP();
        // a CREATION_REQUEST message must be managed in a particular way since the majority on the method assume
        // that the chat already exists locally (fact that cannot subsist in this case)
        if(newMessage.getMessageType().equals(MessageType.CREATION_REQUEST)){
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"CREATION_REQUEST message received with ID"+ chatID);
            if(this.isIDUnique(chatID)){
                // creation of a new chat
                Chat newChat = new Chat(chatID, this.getAddressByMessage(text));
                // merging the vector clocks (just incrementing the one related to the sender)
                newChat.getVectorClock().replace(senderIP, newMessage.getVectorClock().get(senderIP));
                // saving locally the new chat
                this.chats.put(chatID, newChat);
                // inserting the creation message at the application level
                ArrayList<Message> toBeShown = new ArrayList<>();
                toBeShown.add(newMessage);
                this.chatMessagesToBeShown.put(chatID, new ArrayList<>(toBeShown));
                // inserting messages for the chat arrived before the chat was created
                ArrayList<Message> newMessagesToConsider = new ArrayList<>();
                if(this.messagesForNotExistingChats.containsKey(chatID)){
                    newMessagesToConsider.addAll(this.messagesForNotExistingChats.get(chatID));
                    this.messagesForNotExistingChats.remove(chatID);
                }
                this.chatMessageWaiting.put(chatID, newMessagesToConsider);
                // chat is considered created (if no CREATION_REJECT messages are received)
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Chat "+ chatID+ " created");
            }
            else{
                // we cannot simply invoke the method setMessageToBeSent since it assumes the existence of the chat
                // (which in reality exists, but it's a different one with the same ID).
                // we just sent the message on our own.
                HashMap<InetAddress, Integer> incrementedClock = new HashMap<>(newMessage.getVectorClock());
                incrementedClock.replace(this.localAddress, incrementedClock.get(this.localAddress)+1);
                // so we create it with the correct vector clock
                Message message = new Message(this.localAddress,chatID,text,MessageType.CREATION_REJECT, incrementedClock);
                // and then put into the queue of the receivers
                for(InetAddress node:this.getAddressByMessage(text)){
                    if(!node.equals(this.localAddress)){
                        ArrayList<Message> newMessages = new ArrayList<>(this.messagesToBeSent.get(node));
                        newMessages.add(message);
                        this.messagesToBeSent.replace(node, newMessages);
                    }
                }
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Chat with ID "+ chatID+ " already exist. CREATION_REJECT message sent to the participants");
            }
        }
        else if(isVectorClockCoherent(newMessage)){
            System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Message of type "+ newMessage.getMessageType().toString()+ " received");
            // manages message
            switch (newMessage.getMessageType()){
                case CREATION_REJECT -> {
                    // someone rejected the chat ID, chat is deleted. The node which proposed the ID retry with another one
                    if(this.IDProposal.contains(chatID)){
                        ArrayList<InetAddress> addresses = new ArrayList<>(this.chats.get(chatID).getParticipants());
                        this.createNewChat(addresses);
                        this.IDProposal.remove(chatID);
                    }
                    this.chats.remove(chatID);
                    this.chatMessageWaiting.remove(chatID);
                    System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"ChatID "+ chatID+ " rejected by "+ senderIP);
                }
                case DELETION_ORDER -> {
                    this.IDProposal.remove(chatID); // the remove do NOT raise exception if the entry doesn't exist
                    this.chats.remove(chatID);
                    this.chatMessageWaiting.remove(chatID);
                    System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Chat "+ chatID+ " deleted by "+ senderIP);
                }
                case TEXT_MESSAGE -> {
                    // check if some waiting message can be freed by invoking in loop this procedure
                    for(Message message: this.chatMessageWaiting.get(chatID)){ this.newMessageReceived(message);}
                }
            }
        }
    }

                                 // ------- utility method part ------- //

    // this method verify if the unique id passed as input already exists locally or not
    private synchronized boolean isIDUnique(String uniqueID){
        boolean alreadyCreated, alreadyExist = false;
        for (String chatID: this.chats.keySet()){
            if(!alreadyExist){ alreadyExist = uniqueID.equals(chatID); }
        }
        alreadyCreated = this.IDProposal.contains(uniqueID);

        return !(alreadyCreated || alreadyExist);
    }
    
    // this method return the set of InetAddresses related to the List passed in the text of the message
    private synchronized ArrayList<InetAddress> getAddressByMessage(String message){
        ArrayList<InetAddress> inetAddresses = new ArrayList<>();
        /* list of nodes participating in the chat is of type "[address1,address2,...] we need to:
            - remove the square parenthesis (message.substring())
            - split the string considering the ',' separator
            - convert it into an arrayList
         */

        ArrayList<String> hostNames = new ArrayList<>(Arrays.asList(message.substring(2, message.length() - 1).split(", /")));
        for(String address: hostNames){
            try{
                inetAddresses.add(InetAddress.getByName(address));
            } catch (UnknownHostException e) {
                // TODO: implements in a better way
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Error on identifying the participants of the chat");
                throw new RuntimeException(e);
            }
        }
        return inetAddresses;
    }

    /* This method verifies if the vector clock is coherent with respect to the chat vector clock. If it's
        it increments the vector clock of the chat and put the message in the queue for the application level.
        Otherwise, it put it in the waiting list.
     */
    private synchronized boolean isVectorClockCoherent(Message newMessage){
        boolean allLessOrEqual = true;
        InetAddress senderIP = newMessage.getSenderIP();
        Chat chat = this.chats.get(newMessage.getChatID());
        if(chat!=null){
            // checking if MessageVectorClock[i] <= ChatVectorClock[i] for all i!=sender
            for(InetAddress i: chat.getVectorClock().keySet()){
                if(!i.equals(senderIP) && allLessOrEqual){
                    allLessOrEqual = newMessage.getVectorClock().get(i) <= chat.getVectorClock().get(i);
                }
            }
            // checking if also subsist the condition that MessageVectorClock[j] = ChatVectorClock[j]+1 where j = sender
            // if it's then insert the message in the queue of the messages to be shown at the application level and merge the vector clock
            if(allLessOrEqual && newMessage.getVectorClock().get(senderIP) == chat.getVectorClock().get(senderIP)+1){
                ArrayList<Message> updatedMessages = new ArrayList<>(this.chatMessagesToBeShown.get(chat.getID()));
                updatedMessages.add(newMessage);
                this.chatMessagesToBeShown.replace(chat.getID(), updatedMessages);
                // merging the vector clocks (just incrementing the one related to the sender
                chat.getVectorClock().replace(senderIP, newMessage.getVectorClock().get(senderIP));
                this.chats.replace(chat.getID(), chat);
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Message received in casual order");
                return true;
            }
            else{
                // else insert the messages on the waiting queue
                ArrayList<Message> updatedWaitingMessages = new ArrayList<>(this.chatMessageWaiting.get(chat.getID()));
                updatedWaitingMessages.add(newMessage);
                this.chatMessageWaiting.replace(chat.getID(), updatedWaitingMessages);
                System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Message received not in order, put in the waiting queue");
                return false;
            }
        }
        // if the chat is null, the message is putted in a queue waiting for the creation request
        ArrayList<Message> messagesUpdated;
        if(this.messagesForNotExistingChats.get(newMessage.getChatID())==null){
            messagesUpdated = new ArrayList<>();
            messagesUpdated.add(newMessage);
            this.messagesForNotExistingChats.put(newMessage.getChatID(), messagesUpdated);
        }else {
            messagesUpdated = new ArrayList<>(this.messagesForNotExistingChats.get(newMessage.getChatID()));
            messagesUpdated.add(newMessage);
            this.messagesForNotExistingChats.replace(newMessage.getChatID(), messagesUpdated);
        }
        System.out.println("NETMAN: ["+ LocalTime.now()+"]"+"Message received for a non existing chat");
        return false;
    }

}
