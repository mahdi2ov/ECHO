package echo.controller;

import java.util.ArrayList;
import java.util.List;

import echo.dto.request.GroupMembersRequest;
import echo.dto.request.GroupMembershipRequest;
import echo.dto.request.GroupRequest;
import echo.dto.request.UpdateGroupRequest;
import echo.dto.response.GroupDTO;
import echo.dto.response.UserDTO;
import echo.model.Group;
import echo.model.User;
import echo.service.GroupService;
import echo.util.JsonUtil;
import echo.util.ResponseFactory;

public class GroupController {
    private final GroupService groupService;

    // constructor
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    
    public String createGroup(GroupRequest request) {
        try {
            Group group = groupService.createGroup(request.getRequesterId(), request.getName());
            GroupDTO groupDTO = GroupDTO.toGroupDTO(group);
            return ResponseFactory.success("Group created.", JsonUtil.toJson(groupDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String deleteGroup(GroupRequest request) {
        try {
            groupService.deleteGroupByName(request.getName(), request.getRequesterId());
            return ResponseFactory.success("Group deleted.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String updateGroup(UpdateGroupRequest request) {
        try {
            Group group = groupService.updateGroup(request.getGroupId(), request.getRequesterId(), request.getName(),
                            request.getDescription(), request.getProfileImagePath());
 
            GroupDTO groupDTO = GroupDTO.toGroupDTO(group);
            return ResponseFactory.success("Group updated.", JsonUtil.toJson(groupDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String addMemberToGroup(GroupMembershipRequest request) {
        try {
            groupService.addMember(request.getGroupId(), request.getRequesterId(), request.getUserId());
            return ResponseFactory.success("Member added.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
 
    public String removeMemberFromGroup(GroupMembershipRequest request) {
        try {
            groupService.removeMember(request.getGroupId(), request.getRequesterId(), request.getUserId());
            return ResponseFactory.success("Member removed.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String addAdminToGroup(GroupMembershipRequest request) {
        try {
            groupService.addAdmin(request.getGroupId(), request.getRequesterId(), request.getUserId());
            return ResponseFactory.success("Admin added.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
 
    public String removeAdminFromGroup(GroupMembershipRequest request) {
        try {
            groupService.removeAdmin(request.getGroupId(), request.getRequesterId(), request.getUserId());
            return ResponseFactory.success("Admin removed.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String getGroupMembers(GroupMembersRequest request) {
        try {
            List<User> members = groupService.getGroupMembers(request.getGroupId());
            List<UserDTO> membersDTO = new ArrayList<>();
            for (User user : members) {
                UserDTO userDTO = UserDTO.toUserDTO(user);
                membersDTO.add(userDTO);
            }
            return ResponseFactory.success("Group members.", JsonUtil.toJson(membersDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
}
