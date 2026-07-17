package echo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import echo.dto.request.AddReactionRequest;
import echo.dto.request.ConversationInfoRequest;
import echo.dto.request.DeleteMessageRequest;
import echo.dto.request.EditMessageRequest;
import echo.dto.request.ListConversationsRequest;
import echo.dto.request.PollMessagesRequest;
import echo.dto.request.RemoveReactionRequest;
import echo.dto.request.ReportMessageRequest;
import echo.dto.request.SendMessageRequest;
import echo.dto.response.ChatInfoDTO;
import echo.dto.response.ConversationDTO;
import echo.dto.response.MessageDTO;
import echo.model.Conversation;
import echo.model.ConversationType;
import echo.model.Group;
import echo.model.Message;
import echo.model.User;
import echo.repository.GroupRepository;
import echo.service.ChatService;
import echo.service.ConversationService;
import echo.service.UserService;
import echo.util.JsonUtil;
import echo.util.ResponseFactory;

public class ChatController {
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final UserService userService;
    private final GroupRepository groupRepository;

    // constructor
    public ChatController(ChatService chatService, ConversationService conversationService, GroupRepository groupRepository,
                            UserService userService) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }
    
    // message methods
    public String sendMessage(SendMessageRequest request) {
        try {
            Message message = chatService.sendMessage(request.getConversationId(), request.getSenderId(),
                        request.getContent(), request.getAttachmentOriginalName(),
                        request.getAttachmentMimeType(), request.getAttachmentBase64());
            MessageDTO messageDTO = MessageDTO.toMessageDTO(message);
            return ResponseFactory.success("Message sent.", JsonUtil.toJson(messageDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String deleteMessage(DeleteMessageRequest request) {
        try {
            chatService.deleteMessage(request.getMessageId(), request.getRequesterId());
            return ResponseFactory.success("Message deleted.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String editMessage(EditMessageRequest request) {
        try {
            Message message = chatService.editMessage(request.getMessageId(), request.getRequesterId(),
                        request.getNewContent());
            MessageDTO messageDTO = MessageDTO.toMessageDTO(message);
            return ResponseFactory.success("Message edited.", JsonUtil.toJson(messageDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String addReaction(AddReactionRequest request) {
        try {
            Message message = chatService.addReaction(request.getMessageId(), request.getUserId(), request.getReactionType()); 
            MessageDTO messageDTO = MessageDTO.toMessageDTO(message);
            return ResponseFactory.success("Reaction added.", JsonUtil.toJson(messageDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String removeReaction(RemoveReactionRequest request) {
        try {
            Message message = chatService.removeReaction(request.getMessageId(), request.getUserId());
            MessageDTO messageDTO = MessageDTO.toMessageDTO(message);
            return ResponseFactory.success("Reaction removed.", JsonUtil.toJson(messageDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String reportMessage(ReportMessageRequest request) {
        try {
            chatService.addReport(request.getReportedMessageId(), request.getReporterUserId(), request.getReason());
            return ResponseFactory.success("Report added.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    // listing conversations and messages
    public String listConversations(ListConversationsRequest request) {
        try {
            // getting sorted conversations
            List<Conversation> conversations = conversationService.getSortedLastConversation(request.getUserId());

            // generate title and profileImagePath for each conversation
            List<String> conversationJsonItems = new ArrayList<>();

            for (Conversation conversation : conversations) {
                String title;
                String profileImagePath;

                if (conversation.getType() == ConversationType.GROUP) {
                    Group group = groupRepository.getGroupByConversationId(conversation.getId());

                    if (group == null) {
                        throw new RuntimeException("Group not found.");
                    }

                    title = group.getName();
                    profileImagePath = group.getProfileImagePath();
                } else {
                    String otherUserId = null;

                    for (String memberId : conversation.getMembersId()) {
                        if (!memberId.equals(request.getUserId())) {
                            otherUserId = memberId;
                            break;
                        }
                    }

                    if (otherUserId == null) {
                        throw new RuntimeException("User not found.");
                    }

                    User otherUser = userService.findUser(otherUserId);
                    if (otherUser == null) {
                        throw new RuntimeException("User not found.");
                    }

                    title = otherUser.getUsername();
                    profileImagePath = otherUser.getProfileImagePath();
                }

                // add conversation item into conversations
                conversationJsonItems.add(JsonUtil.toJson(ConversationDTO.toConversationDTO(conversation, title, profileImagePath)));
            }

            return ResponseFactory.success("Conversations.", JsonUtil.toJsonObjectArray(conversationJsonItems));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String getConversationInfo(ConversationInfoRequest request) {
        try {
            ChatInfoDTO chatInfoDTO = chatService.getConversationInfo(request.getConversationId(), request.getRequesterId());
            return ResponseFactory.success("ConversationInfo.", JsonUtil.toJson(chatInfoDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }   
    }

    public String pollMessages(PollMessagesRequest request) {
        try {
            LocalDateTime since = request.getSince();
            List<Message> messages = chatService.getMessagesSince(request.getConversationId(), since);

            List<String> messageJsonItems = new ArrayList<>();
            for (Message message : messages) {
                messageJsonItems.add(JsonUtil.toJson(MessageDTO.toMessageDTO(message)));
            }

            return ResponseFactory.success("New messages.", JsonUtil.toJsonObjectArray(messageJsonItems));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    /// in this version only cli admin can see and resolve reports
    // public String listReports() {
    //     List<Report> reports = chatService.getAllReports();
 
    //     List<String> reportJsonItems = new ArrayList<>();
    //     for (Report report : reports) {
    //         reportJsonItems.add(JsonUtil.toJson(report));
    //     }
 
    //     return ResponseFactory.success("Reports.", JsonUtil.toJsonArray(reportJsonItems));
    // }
 
    // public String resolveReport(String reportId) {
    //     try {
    //         chatService.resolveReport(reportId);
    //         return ResponseFactory.success("Report resolved.", null);
    //     } catch (RuntimeException exception) {
    //         return ResponseFactory.error(exception.getMessage());
    //     }
    // }
}
