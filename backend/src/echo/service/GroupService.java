package echo.service;

import java.util.ArrayList;
import java.util.List;

import echo.model.Conversation;
import echo.model.Group;
import echo.model.User;
import echo.repository.ConversationRepository;
import echo.repository.GroupRepository;
import echo.repository.UserRepository;
import echo.util.IdGenerator;

public class GroupService {
    private final GroupRepository groupRepository;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    // construcor
    public GroupService(GroupRepository groupRepository, ConversationService conversationService,
                        ConversationRepository conversationRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
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
    
    public synchronized void deleteGroupById(String groupId, String requesterId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only owner member can delete the group.");
        }
        // delete group
        groupRepository.deleteGroupById(groupId);
    }

    public synchronized Group updateGroup(String groupId, String requesterId, String name, String description, String profileImagePath) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only owner member can update the group.");
        }

        group.setName(name);
        group.setDescription(description);
        group.setProfileImagePath(profileImagePath);
        
        groupRepository.updateGroup(group);
        return group;
    }

    public synchronized void deleteGroupByName(String name , String requesterId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupByName(name);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only owner member can delete the group.");
        }

        // delete group
        groupRepository.deleteGroupById(group.getId());
    }

    public synchronized void addAdmin(String groupId, String requesterId, String toAdminId) {
        // getting group if exists
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // requester should be owner of group
        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only owner of group can make members admin.");
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

    public synchronized void removeAdmin(String groupId, String requesterId, String toRemoveId) {
        // getting group if exists
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group doesn't exists.");
        }

        // requester should be owner of group
        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only owner of group can delete admins.");
        }

        // checking member is admin or not
        if (!group.getAdminsId().contains(toRemoveId)) {
            throw new RuntimeException("Member is not admin.");
        }

        // getting group's conversation and checking that user is a member or not
        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (!conversation.getMembersId().contains(toRemoveId)) {
            throw new RuntimeException("User is not a member of this group.");
        }

        // remove admin
        group.getAdminsId().remove(toRemoveId);

        // update group
        groupRepository.updateGroup(group);
    }
    
    // methods for admin of group
    public synchronized void addMember(String groupId, String requesterId, String userId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        // checking requsterId for admins
        boolean isAdmin = false;
        for (String adminId : group.getAdminsId()) {
            if (adminId.equals(requesterId)) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            throw new RuntimeException("Only admin of the group can add members.");
        }

        // getting conversation and checking member exist
        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (conversation.getMembersId().contains(userId)) {
            throw new RuntimeException("Member already exists.");
        }

        conversation.getMembersId().add(userId);

        conversationRepository.updateConversation(conversation);
    }

    public synchronized void removeMember(String groupId, String requesterId, String userId) {
        // getting group and check for exist
        Group group = groupRepository.getGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        // checking requsterId for admins
        boolean isAdmin = false;
        for (String adminId : group.getAdminsId()) {
            if (adminId.equals(requesterId)) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            throw new RuntimeException("Only admin of the group can delete members.");
        }

        Conversation conversation = conversationRepository.getConversationById(group.getConversationId());
        if (!conversation.getMembersId().contains(userId)) {
            throw new RuntimeException("Member already not exists.");
        }

        conversation.getMembersId().remove(userId);

        conversationRepository.updateConversation(conversation);
    }

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
            removeAdmin(groupId, userId, userId);
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

    public List<User> getGroupMembers(String groupId) {
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

        List<User> groupMembres = new ArrayList<>();
        for (String userId : conversation.getMembersId()) {
            groupMembres.add(userRepository.getUserById(userId));
        }

        return groupMembres;
    }
}
