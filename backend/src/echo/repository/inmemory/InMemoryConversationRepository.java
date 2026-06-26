package echo.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.model.Conversation;
import echo.model.ConversationType;
import echo.repository.ConversationRepository;

public class InMemoryConversationRepository implements ConversationRepository {
    // in memory implementation for deadline one\
    private final Map<String, Conversation> conversationsById;

    // constructor
    public InMemoryConversationRepository() {
        this.conversationsById = new HashMap<>();
    }
    
    // create
    @Override
    public synchronized void saveConversation(Conversation conversation) {
        conversationsById.put(conversation.getId(), conversation);
    }

    // read
    @Override
    public synchronized Conversation getConversationById(String id) {
        return conversationsById.get(id);
    }

    @Override
    public synchronized boolean existById(String id) {
        return getConversationById(id) != null;
    }

    @Override
    public synchronized List<Conversation> getAllConversations() {
        return new ArrayList<>(conversationsById.values());
    }

    // update
    @Override
    public synchronized void updateConversation(Conversation conversation) {
        conversationsById.put(conversation.getId(), conversation);
    }

    // delete
    @Override
    public synchronized void deleteById(String id) {
        conversationsById.remove(id);
    }
    
    // util methods
    @Override
    public synchronized List<Conversation> getConversationsByUserId(String userId) {
        List<Conversation> conversationsFound = new ArrayList<>();
        for (Conversation conversation : conversationsById.values()) {
            for (String conversationUserId : conversation.getMembersId()) {
                if (conversationUserId.equals(userId) && conversation.getLastMessageAt() != null) {
                    conversationsFound.add(conversation);
                    break;
                }
            }
        }
        return conversationsFound;
    }

    @Override
    public synchronized List<Conversation> getConversationsByType(ConversationType type) {
        List<Conversation> conversationsFound = new ArrayList<>();
        for (Conversation conversation : conversationsById.values()) {
            if (conversation.getType().equals(type)) {
                conversationsFound.add(conversation);
            }
        }
        return conversationsFound;
    }
}
