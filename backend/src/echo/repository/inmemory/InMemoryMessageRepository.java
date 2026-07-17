package echo.repository.inmemory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.model.Message;
import echo.repository.MessageRepository;

public class InMemoryMessageRepository implements MessageRepository {
    // in memory implementation for deadline one
    private final Map<String, Message> messagesById;

    // constructor
    public InMemoryMessageRepository() {
        this.messagesById = new HashMap<>();
    }
    
    // create
    @Override
    public synchronized void saveMessage(Message message) {
        messagesById.put(message.getId(), message);
        
    }
    
    // read
    @Override
    public synchronized Message getMessageById(String id) {
        return messagesById.get(id);
    }

    @Override
    public synchronized boolean existById(String id) {
        return getMessageById(id) != null;
    }

    @Override
    public synchronized List<Message> getAllMessages() {
        return new ArrayList<>(messagesById.values());
    }

    // update
    @Override
    public synchronized void updateMessage(Message message) {
        messagesById.put(message.getId(), message);
        
    }

    // delete
    @Override
    public synchronized void deleteMessageById(String id) {
        messagesById.remove(id);
    }

    // util methods
    @Override
    public synchronized List<Message> getMessagesBySenderId(String senderId) {
        List<Message> messagesFound = new ArrayList<>();
        for (Message message : messagesById.values()) {
            if (message.getSenderId().equals(senderId)) {
                messagesFound.add(message);
            }
        }
        return messagesFound;
    }

    @Override
    public synchronized List<Message> getMessagesByConversationId(String conversationId) {
        List<Message> messagesFound = new ArrayList<>();
        for (Message message : messagesById.values()) {
            if (message.getConversationId().equals(conversationId)) {
                messagesFound.add(message);
            }
        }
        return messagesFound;
    }

    @Override
    public synchronized List<Message> getMessagesByConversationIdSince(String conversationId, LocalDateTime since) {
        List<Message> messagesFound = new ArrayList<>();
        for (Message message : messagesById.values()) {
            if (message.getConversationId().equals(conversationId) && message.getCreatedAt().isAfter(since)) {
                messagesFound.add(message);
            }
        }
        return messagesFound;
    }
}
