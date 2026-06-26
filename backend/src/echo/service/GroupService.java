package echo.service;

import java.util.List;

import echo.model.Conversation;
import echo.model.Group;
import echo.repository.ConversationRepository;
import echo.repository.GroupRepository;
import echo.util.IdGenerator;

public class GroupService {
    private final GroupRepository groupRepository;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;

    // construcor
    public GroupService(GroupRepository groupRepository, ConversationService conversationService,
                        ConversationRepository conversationRepository) {
        this.groupRepository = groupRepository;
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions

    public synchronized Group createGroup(String ownerId, String name) {
        // create a group conversation for group and add owner member to it
        Conversation groupConversation = conversationService.createGroupConversation(ownerId);
        
        // create group
        Group group = new Group(IdGenerator.nextGroupId(), name, ownerId, groupConversation.getId());
        
        // owner member is admin
        group.getAdminsId().add(ownerId);

        // sava group
        groupRepository.saveGroup(group);
        return group;
    }
    
    public synchronized void deleteGroupById(String groupId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // delete group
        groupRepository.deleteGroupById(groupId);
    }

    public synchronized void deleteGroupByName(String name) {
        // getting group and check for exist
        Group group = groupRepository.getGroupByName(name);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // delete group
        groupRepository.deleteGroupById(group.getId());
    }

    public synchronized void addAdmin(String groupId, String toAdminId) {
        // getting group if exists
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // getting group's conversation and checking that user is a member or not
        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (!conversation.getMembersId().contains(toAdminId)) {
            throw new RuntimeException("User is not a member of this group.");
        }

        // make user admin
        group.getAdminsId().add(toAdminId);

        // update group
        groupRepository.updateGroup(group);
    }
    public synchronized void removeAdmin(String groupId, String toRemove) {
        // getting group if exists
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // checking member is admin or not
        if (!group.getAdminsId().contains(toRemove)) {
            throw new RuntimeException("Member is not admin.");
        }

        // getting group's conversation and checking that user is a member or not
        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (!conversation.getMembersId().contains(toRemove)) {
            throw new RuntimeException("User is not a member of this group.");
        }

        // remove admin
        group.getAdminsId().remove(toRemove);

        // update group
        groupRepository.updateGroup(group);
    }
    
    // methods for admin of group
    public synchronized void addMember() {}
    public synchronized void removeMember() {}

    // methods for admin cli because for add or remove member or delete group upper methods check for admin
    // methods are not synchronized because there is only one cli admin
    public void cliAddMember(String groupId, String userId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }
        
        // add user
        conversationService.addMemberToGroupConversation(group.getConversationId(), userId);
    }

    public void cliRemoveMember(String groupId, String userId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        // checking some conditions
        if (group.getOwnerId() != null && group.getOwnerId().equals(userId)) {
            throw new RuntimeException("Owner cannot be removed from group.");
        }
        if (group.getOwnerId().equals(userId)) {
            if (group.getAdminsId().size() == 1) {
                throw new RuntimeException("Group must have at least one admin.");
            }
            removeAdmin(groupId, userId);
        }

        // remove user
        conversationService.removeMemberFromGroupConversation(group.getConversationId(), userId);
    }

    public void cliDeleteGroup(String groupId) {
        // checking group for exist
        if (!groupRepository.existById(groupId)) {
            throw new RuntimeException("Group not found");
        }

        // delete group
        groupRepository.deleteGroupById(groupId);
    }

    public List<Group> listGroups() {
        return groupRepository.getAllGroups();
    }

    public List<String> getGroupMembers(String groupId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        // getting conversation of group and check for exist
        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        return conversation.getMembersId();
    }
}
