package echo.repository;

import java.util.List;

import echo.model.Message;

public interface MessageRepository {
    // create
    void saveMessage(Message message);
    
    // read
    Message getMessageById(String id);
    boolean existById(String id);
    List<Message> getAllMessages();

    // update
    void updateMessage(Message message);

    // delete
    void deleteMessageById(String id);

    // util methods
    List<Message> getMessagesBySenderId(String senderId);
    List<Message> getMessagesByConversationId(String conversationId);
}
