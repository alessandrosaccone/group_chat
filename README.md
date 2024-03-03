# Highly available, causally ordered group chat

Implement a distributed group chat application. Users can create and delete rooms. For each room, the set of participants is specified at creation time and is never modified. 
Users can post new messages for a room they are participating to. 
Within each room, messages should be delivered in causal order.
The application should be fully distributed, meaning that user clients should exchange messages without relying on any centralized server.
The application should be highly available, meaning that users should be able to use the chat (read and write messages) even if they are temporarily disconnected from the network. 

Assumptions
• Clients and links are reliable, but clients can join and leave the network at any time.
