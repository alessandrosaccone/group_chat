package org.example.demo.backend.enums;

/*
Possible type of messages.
CREATION_REQUEST,CREATION_REJECT: related to the agreement algorithm for a new chat ID
DELETION_ORDER: related to the deletion of a chat
TEXT_MESSAGE: related to standard messages exchanged in the chat by the users
 */
public enum MessageType {
    CREATION_REQUEST,
    CREATION_REJECT,
    DELETION_ORDER,
    TEXT_MESSAGE
}
