package echo.controller;

import java.util.ArrayList;
import java.util.List;

import echo.dto.request.ListUserRelationRequest;
import echo.dto.request.ProfileRequest;
import echo.dto.request.UserRelationRequest;
import echo.dto.response.UserDTO;
import echo.model.User;
import echo.service.ContactService;
import echo.service.UserService;
import echo.util.JsonUtil;
import echo.util.ResponseFactory;

public class UserController {
    private final UserService userService;
    private final ContactService contactService;
    
    // constructor
    public UserController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }
    
    // contact
    public String addContact(UserRelationRequest request) {
        try {
            contactService.addContact(request.getUserId(), request.getOtherUserId());
            return ResponseFactory.success("Contact added.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String deleteContact(UserRelationRequest request) {
        try {
            contactService.deleteContact(request.getUserId(), request.getOtherUserId());
            return ResponseFactory.success("Contact deleted.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    // blocked user
    public String blockUser(UserRelationRequest request) {
        try {
            contactService.blockUser(request.getUserId(), request.getOtherUserId());
            return ResponseFactory.success("User blocked.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String unblockUser(UserRelationRequest request) {
        try {
            contactService.unblockUser(request.getUserId(), request.getOtherUserId());
            return ResponseFactory.success("User unblocked.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    // profile and setting
    public String getUserProfile(ProfileRequest request) {
        try {
            User user = userService.findUser(request.getUserId());
            if (user == null) {
                return ResponseFactory.error("User not found.");
            }
 
            UserDTO userDTO = UserDTO.toUserDTO(user);
            return ResponseFactory.success("User profile.", JsonUtil.toJson(userDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String updateProfile(ProfileRequest request) {
        try {
            User user = userService.updateProfile(request.getUserId(), request.getUsername(),
                            request.getProfileImagePath());
 
            UserDTO userDTO = UserDTO.toUserDTO(user);
            return ResponseFactory.success("Profile updated.", JsonUtil.toJson(userDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String listContacts(ListUserRelationRequest request) {
        try {
            List<User> contacts = userService.getContacts(request.getUserId());
            List<UserDTO> contactsDTO = new ArrayList<>();
            for (User user : contacts) {
                UserDTO userDTO = UserDTO.toUserDTO(user);
                contactsDTO.add(userDTO);
            }
            return ResponseFactory.success("Contacts.", JsonUtil.toJson(contactsDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
 
    public String listBlockedUsers(ListUserRelationRequest request) {
        try {
            List<User> blockedUsers = userService.getBlockedUsers(request.getUserId());
            List<UserDTO> blockedUsersDTO = new ArrayList<>();
            for (User user : blockedUsers) {
                UserDTO userDTO = UserDTO.toUserDTO(user);
                blockedUsersDTO.add(userDTO);
            }
            return ResponseFactory.success("Blocked users.", JsonUtil.toJson(blockedUsersDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
}
