package org.example.demo.backend.interfaces;

import org.example.demo.backend.classes.Chat;
import org.example.demo.backend.classes.Message;
import org.example.demo.backend.enums.MessageType;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public interface NetworkManager {

                                     // ------- thread management part ------- //

    // this method MUST be invoked by each requestor thread when terminating
    void RequestorTerminated(InetAddress node);

    // this method MUST be invoked only when the application terminates.
    // It brutally closes all the sockets and terminates all the thread.
    //TODO: implements a soft way to do this
    void closeAllConnections();

                                     // ------- network management part ------- //

    // this method returns the local address
    InetAddress getLocalAddress();

    // this method return the set of sockets
    HashMap<InetAddress, Socket> getSockets();

    // this method return the socket connected to a specific node with IP address passed as input
    Socket getSockets(InetAddress IPAddress);

    // when invoked, means a socket connection has been lost. The NetworkManager tries to recreate the socket connection with the passed participant
    void connectionLost(InetAddress node, Socket corruptedSocket);

    // this method allows to update the set of socket with the missing ones
    void setNewConnection(InetAddress node, Socket newSocket);

                                     // ------- application level chat management ------- //

    // this method returns the list of messages that the Sender to the specific receiver has not already sent
    ArrayList<Message> getMessagesToBeSent(InetAddress receiver);

    // this method allows to get all the messages that has to be shown at the application level
    HashMap<String,ArrayList<Message>> getMessagesToBeShown();

    // this method allows to get all the messages that has to be shown at the application level for a specific chat
    ArrayList<Message> getMessagesToBeShown(String chatID);

    // this method allows to insert a new Message to be sent in the queue of the NetworkManager
    void setMessageToBeSent(String chatID, String text, MessageType type);

    // this method MUST be invoked by the application level when creating a new chat. It starts the execution of the
    // agreement protocol for the unique global ID of the chat
    void createNewChat(ArrayList<InetAddress> participants);

    // this method is invoked only by the application level when deleting a chat. It starts the protocol for notifying
    // the others node that the chat has been deleted
    void deleteChat(String chatID);

    // this method returns the list of current active chats
    ArrayList<Chat> getChats();

    // this method returns the chat with the ID passed as parameter
    Chat getChat(String chatID);

                                    // ------- Thread level message management ------- //

    // this method returns the vector clock of the chat with ID passed as input
    HashMap<InetAddress, Integer> getChatVectorClock(String chatID);

    // this method allows to insert in the queue od the NetworkManager a new message received
    void newMessageReceived(Message newMessage);
}
