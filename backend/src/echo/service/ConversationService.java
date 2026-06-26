package echo.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import echo.model.Conversation;
import echo.model.ConversationType;
import echo.repository.ConversationRepository;
import echo.util.IdGenerator;

public class ConversationService {
    private final ConversationRepository conversationRepository;

    // constructor
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions

    public synchronized Conversation createPrivateConversation(String memberId1, String memberId2) {
        // create a conversation
        Conversation conversation = new Conversation(IdGenerator.nextConversationId(), ConversationType.PRIVATE);

        // add two members of priavte chat
        conversation.getMembersId().add(memberId1);
        conversation.getMembersId().add(memberId2);

        // save conversation
        conversationRepository.saveConversation(conversation);
        return conversation;
    }

    public synchronized Conversation createGroupConversation(String ownerId) {
        // create a conversation
        Conversation conversation = new Conversation(IdGenerator.nextConversationId(), ConversationType.GROUP);

        // add owner member to the group chat
        conversation.getMembersId().add(ownerId);

        // save conversation
        conversationRepository.saveConversation(conversation);
        return conversation;
    }

    public synchronized Conversation addMemberToGroupConversation(String conversationId, String memberId) {
        // getting conversation if exists
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation dosn't exists.");
        }
        
        // checking this conversatino is group chat
        if (!conversation.getType().equals(ConversationType.GROUP)) {
             new RuntimeException("Only group chats can have more than two members.");
        }

        // check if member exist or not
        if (conversation.getMembersId().contains(memberId)) {
            throw new RuntimeException("Member already exists.");
        }
        
        // add member to conversation
        conversation.getMembersId().add(memberId);
        
        // update conversation
        conversationRepository.updateConversation(conversation);
        return conversation;
    }
    public synchronized Conversation removeMemberFromGroupConversation(String conversationId, String memberId) {
        // getting conversation if exists
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation doesn't exists.");
        }

        // checking this conversatino is group chat
        if (!conversation.getType().equals(ConversationType.GROUP)) {
             new RuntimeException("Only group chats can have more than two members.");
        }
        
        // check if member exist or not
        if (!conversation.getMembersId().contains(memberId)) {
            throw new RuntimeException("Member doesn't already exists.");
        }

        // remove member from conversation
        conversation.getMembersId().remove(memberId);

        // update conversation
        conversationRepository.updateConversation(conversation);
        return conversation;
    }

    public synchronized void setConversationLastMessageAt(String conversationId , LocalDateTime lastMessageAt) {
        // getting conversation if exists
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation dosn't exists.");
        }

        // set conversation last message
        conversation.setLastMessageAt(lastMessageAt);

        // update conversation
        conversationRepository.updateConversation(conversation);
    }

    public synchronized void setConversationPinned(String conversationId) {
        // getting conversation if exists
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation dosn't exists.");
        }

        // pinning conversation
        conversation.setPinned(true);

        // update conversation
        conversationRepository.updateConversation(conversation);
    }

    public synchronized void setConversationArchived(String conversationId) {
        // getting conversation if exists
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation dosn't exists.");
        }

        // archiving conversation
        conversation.setArchived(true);

        // update conversation
        conversationRepository.updateConversation(conversation);
    }

    public synchronized List<Conversation> getSortedLastConversation(String userId) {
        // input validation // TODO

        // getting list of user conversation
        List<Conversation> conversations = conversationRepository.getConversationsByUserId(userId);
        
        // sort conversations by lastmessge time
        Collections.sort(conversations);
        // reverse conversations for sort by new messages
        Collections.reverse(conversations);

        return conversations;
    }
}
