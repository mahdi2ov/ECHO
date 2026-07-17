package echo.service;

import java.util.List;

import echo.model.Conversation;
import echo.model.ConversationType;
import echo.model.Message;
import echo.model.User;
import echo.repository.ConversationRepository;
import echo.repository.MessageRepository;
import echo.repository.UserRepository;
import echo.security.EncryptionUtil;

public class SavedMessageService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    // constructor
    public SavedMessageService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                                UserRepository userRepository, ChatService chatService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    public Conversation createSavedMessagesConversation(String userId) {
        // genereate id and check save messages for exist
        String conversationId = "SAVED_MESSAGE-" + userId;
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation != null) {
            throw new RuntimeException("Saved messages conversation already exists.");
        }
        
        // create a conversation
        conversation = new Conversation(conversationId, ConversationType.PRIVATE);
        conversation.getMembersId().add(userId);

        // save conversation
        conversationRepository.saveConversation(conversation);

        return conversation;
    }

    public Conversation getSavedMessagesConversation(String userId) {
        // generate id
        String conversationId = "SAVED_MESSAGE-" + userId;

        return conversationRepository.getConversationById(conversationId);
    }

    public boolean hasSavedMessagesConversation(String userId) {
        return getSavedMessagesConversation(userId) != null;
    }

    public void deleteSavedMessagesConversation(String userId) {
        // generate id
        String conversationId = "SAVED_MESSAGE-" + userId;

        // check saved messages conversation for exist
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Saved messages conversation not found.");
        }

        conversationRepository.deleteById(conversationId);
    }
        
    public synchronized Message saveMessage(String userId, String messageId) {
        // gettin user and ckeck for exist
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // get message and checking some conditions
        Message message = messageRepository.getMessageById(messageId);
        if (message == null) {
            throw new RuntimeException("Message not found.");
        }
        if (message.getDeleted()) {
            throw new RuntimeException("Deleted message cannot be saved.");
        }

        // getting saved messages conversation and checking for exist
        Conversation savedMessagesConversation = getSavedMessagesConversation(userId);
        if (savedMessagesConversation == null) {
            savedMessagesConversation = createSavedMessagesConversation(userId);
        }

        // decrypting message content
        String messageContent = EncryptionUtil.decrypt(message.getContent());

        return chatService.sendMessage(savedMessagesConversation.getId(), userId, messageContent);
    }
    
    public void deleteMessage(String userId, String savedMessageId) {
        chatService.deleteMessage(savedMessageId, userId);
    }

    public List<Message> getSavedMessages(String userId) {
        // generate id
        String conversationId = "SAVED_MESSAGE-" + userId;
        
        return messageRepository.getMessagesByConversationId(conversationId);
    }
}
