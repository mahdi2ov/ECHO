package echo.repository;

import java.util.List;

import echo.model.Conversation;
import echo.model.ConversationType;

public interface ConversationRepository {
    // create
    void saveConversation(Conversation conversation);


    // read
    Conversation getConversationById(String id);
    boolean existById(String id);
    List<Conversation> getAllConversations();

    // update
    void updateConversation(Conversation conversation);

    // delete
    void deleteById(String id);

    // util methods
    List<Conversation> getConversationsByUserId(String userId);
    List<Conversation> getConversationsByType(ConversationType type);
}
