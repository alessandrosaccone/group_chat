# Highly available, causally ordered group chat

Implement a distributed group chat application. Users can create and delete rooms. For each room, the set of participants is specified at creation time and is never modified. 
Users can post new messages for a room they are participating to. 
Within each room, messages should be delivered in causal order.
The application should be fully distributed, meaning that user clients should exchange messages without relying on any centralized server.
The application should be highly available, meaning that users should be able to use the chat (read and write messages) even if they are temporarily disconnected from the network. 

Assumptions: clients and links are reliable, but clients can join and leave the network at any time.

## How to launch the application
To run the application, follow these steps:
For first, you have to download a version of JavaFX (recomended version openjfx 20 or later). See this link https://gluonhq.com/products/javafx/
  1. Download the JAR file "group_chat.jar" from the deliverables folder
  2. Open a terminal and navigate to the directory in which you have saved the downloaded file
  3. Perform the command below
     java --module-path path/to/javafx/lib --add-module javafx.controls,javafx.fxml -jar group_chat.jar
