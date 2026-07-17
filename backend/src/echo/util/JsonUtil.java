package echo.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import echo.dto.request.AddReactionRequest;
import echo.dto.request.ConversationInfoRequest;
import echo.dto.request.DeleteMessageRequest;
import echo.dto.request.EditMessageRequest;
import echo.dto.request.ForgotPasswordRequest;
import echo.dto.request.GroupMembersRequest;
import echo.dto.request.GroupMembershipRequest;
import echo.dto.request.GroupRequest;
import echo.dto.request.ListConversationsRequest;
import echo.dto.request.ListUserRelationRequest;
import echo.dto.request.LoginRequest;
import echo.dto.request.PollMessagesRequest;
import echo.dto.request.ProfileRequest;
import echo.dto.request.RemoveReactionRequest;
import echo.dto.request.ReportMessageRequest;
import echo.dto.request.SavedMessageRequest;
import echo.dto.request.SendMessageRequest;
import echo.dto.request.SignupRequest;
import echo.dto.request.UpdateGroupRequest;
import echo.dto.request.UserRelationRequest;
import echo.dto.response.ChatInfoDTO;
import echo.dto.response.ConversationDTO;
import echo.dto.response.GroupDTO;
import echo.dto.response.MessageDTO;
import echo.dto.response.UserDTO;
import echo.model.Attachment;
import echo.model.MessageEdit;
import echo.model.Reaction;
import echo.model.ReactionType;

public class JsonUtil {
    // private constructor to prevent instantiation
    private JsonUtil() {
    }
    
    /// static methods for converting dto objects to json (method overloading)
    // userDTO
    public static String toJson(UserDTO dto) {
        return String.format("{\"id\":%s,\"username\":%s,\"profileImagePath\":%s}",
                jsonSafeString(dto.getId()), jsonSafeString(dto.getUsername()), jsonSafeString(dto.getProfileImagePath()));
    }

    public static String toJson(List<UserDTO> users) {
        StringBuilder array = new StringBuilder();
        array.append("[");

        for (int i = 0; i < users.size(); i++) {
            array.append(toJson(users.get(i)));

            if (i != users.size() - 1) {
                array.append(",");
            }
        }

        array.append("]");

        return array.toString();
    }

    // groupDTO 
    public static String toJson(GroupDTO dto) {
        return String.format(
                "{\"id\":%s,\"name\":%s,\"description\":%s,\"profileImagePath\":%s,\"conversationId\":%s,\"ownerId\":%s,\"adminsId\":%s}",
                    jsonSafeString(dto.getId()), jsonSafeString(dto.getName()), jsonSafeString(dto.getDescription()),
                    jsonSafeString(dto.getProfileImagePath()), jsonSafeString(dto.getConversationId()),
                    jsonSafeString(dto.getOwnerId()), jsonSafeStringArray(dto.getAdminsId()));
    }

    // conversationDTO
    public static String toJson(ConversationDTO dto) {
    return String.format(
            "{\"id\":%s,\"title\":%s,\"profileImagePath\":%s,\"pinned\":%b,\"archived\":%b,\"lastMessageAt\":%s,\"type\":%s}",
                    jsonSafeString(dto.getId()), jsonSafeString(dto.getTitle()), jsonSafeString(dto.getProfileImagePath()), dto.isPinned(),
                    dto.isArchived(), dto.getLastMessageAt() == null ? "null" : jsonSafeString(DateTimeUtil.format(dto.getLastMessageAt())), 
                    jsonSafeString(dto.getType().name()));
    }

    // messageDTO
    public static String toJson(MessageDTO dto) {
    return String.format("{\"id\":%s,\"senderId\":%s,\"conversationId\":%s,\"content\":%s,\"createdAt\":%s,\"edited\":%b,\"deleted\":%b,\"editHistory\":%s,\"reactions\":%s,\"attachment\":%s}",
                    jsonSafeString(dto.getId()), jsonSafeString(dto.getSenderId()), jsonSafeString(dto.getConversationId()),
                    jsonSafeString(dto.getContent()), dto.getCreatedAt() == null ? "null" : jsonSafeString(DateTimeUtil.format(dto.getCreatedAt())),
                    dto.isEdited(), dto.isDeleted(), jsonMessageEditArray(dto.getEditHistory()),
                    jsonReactionArray(dto.getReactions()), dto.getAttachment() == null ? "null" : toJson(dto.getAttachment()));
    }

    // chatInfoDTO
    public static String toJson(ChatInfoDTO dto) {
        if (dto == null) return "null";
        
        return String.format("{\"conversationId\":%s,\"title\":%s,\"subtitle\":%s,\"type\":%s,\"otherUserId\":%s,\"groupId\":%s}",
                    jsonSafeString(dto.getConversationId()), jsonSafeString(dto.getTitle()), jsonSafeString(dto.getSubtitle()),
                    jsonSafeString(dto.getType() != null ? dto.getType().name() : null),
                    jsonSafeString(dto.getOtherUserId()), jsonSafeString(dto.getGroupId()));
    }

    // messageEdit for messageDTO 
    public static String toJson(MessageEdit messageEdit) {
        return String.format("{\"oldContent\":%s,\"editedAt\":%s}", jsonSafeString(messageEdit.getOldContent()),
                    messageEdit.getEditedAt() == null ? "null" : jsonSafeString(DateTimeUtil.format(messageEdit.getEditedAt())));
    }
    private static String jsonMessageEditArray(List<MessageEdit> editHistory) {
        // checking content for null safety
        if (editHistory == null) {
            return "null";
        }

        StringBuilder array = new StringBuilder();
        array.append("[");
        for (int i = 0; i < editHistory.size(); i++) {
            if (i > 0) {
                array.append(",");
            }

            array.append(toJson(editHistory.get(i)));
        }
        array.append("]");
        return array.toString();
    }

    // reaction for messageDTO
    public static String toJson(Reaction reaction) {
        return String.format("{\"userId\":%s,\"emoji\":%s,\"reactedAt\":%s}",
                    jsonSafeString(reaction.getUserId()), jsonSafeString(reaction.getEmoji().toString()),
                    reaction.getReactedAt() == null ? "null" : jsonSafeString(DateTimeUtil.format(reaction.getReactedAt())));
    }
    private static String jsonReactionArray(List<Reaction> reactions) {
        // checking content for null safety
        if (reactions == null) {
            return "null";
        }

        StringBuilder array = new StringBuilder();
        array.append("[");
        for (int i = 0; i < reactions.size(); i++) {
            if (i > 0) {
                array.append(",");
            }

            array.append(toJson(reactions.get(i)));
        }
        array.append("]");
        return array.toString();
    }

    // attachment for messageDTO
    public static String toJson(Attachment attachment) {
        return String.format("{\"fileId\":%s,\"originalName\":%s,\"filePath\":%s,\"mimeType\":%s,\"fileSize\":%d}",
                    jsonSafeString(attachment.getFileId()), jsonSafeString(attachment.getOriginalName()), 
                    jsonSafeString(attachment.getFilePath()), jsonSafeString(attachment.getMimeType()), attachment.getFileSize());
    }
    
    public static String toApiResponseJson(boolean success, String message, String dataJson) {
        return String.format("{\"success\":%b,\"message\":%s,\"data\":%s}",
                    success, jsonSafeString(message), dataJson == null ? "null" : dataJson);
    }
    
    // private methods for converting to json and only use in this class
    private static String jsonSafeString(String content) {
        // checking content for null safety
        if (content == null) {
            return "null";
        }

        return "\"" + content.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    public static String jsonSafeStringArray(List<String> content) {
        // checking content for null safety
        if (content == null) {
            return "null";
        }

        StringBuilder array = new StringBuilder();
        array.append("[");
        for (int i = 0; i < content.size(); i++) {
            if (i > 0) {
                array.append(",");
            }

            array.append(jsonSafeString(content.get(i)));
        }
        array.append("]");

        return array.toString(); 
    }

    public static String toJsonObjectArray(List<String> objects) {
        if (objects == null) {
            return "null";
        }

        StringBuilder array = new StringBuilder();
        array.append("[");

        for (int i = 0; i < objects.size(); i++) {
            if (i > 0) {
                array.append(",");
            }

            array.append(objects.get(i));
        }

        array.append("]");

        return array.toString();
    }

    /// static methods for parsing json to request dto objects
    // auth requests
    public static LoginRequest parseLoginRequest(String json) {
        return new LoginRequest(extractStringField(json, "username"), extractStringField(json, "password"));
    }

    public static SignupRequest parseSignupRequest(String json) {
        return new SignupRequest(extractStringField(json, "username"), extractStringField(json, "email"),
                                extractStringField(json, "password"), extractStringField(json, "confirmPassword"));
    }

    public static ForgotPasswordRequest parseForgotPasswordRequest(String json) {
        return new ForgotPasswordRequest(extractStringField(json, "username"), extractStringField(json, "email"));
    }

    public static ProfileRequest parseProfileRequest(String json) {
        return new ProfileRequest(extractStringField(json, "userId"), extractStringField(json, "username"),
                                extractStringField(json, "profileImagePath"));
    }

    public static UserRelationRequest parseUserRelationRequest(String json) {
        return new UserRelationRequest(extractStringField(json, "userId"), extractStringField(json, "otherUserId"));
    }

    public static ListUserRelationRequest parseListUserRelationRequest(String json) {
        return new ListUserRelationRequest(extractStringField(json, "userId"));
    }

    // group requests
    public static GroupRequest parseGroupRequest(String json) {
        return new GroupRequest(extractStringField(json, "requesterId"), extractStringField(json, "name"));
    }

    public static UpdateGroupRequest parseUpdateGroupRequest(String json) {
        return new UpdateGroupRequest(extractStringField(json, "requesterId"), extractStringField(json, "groupId"),
                                extractStringField(json, "name"),extractStringField(json, "description"),
                                extractStringField(json, "profileImagePath"));
    }

    public static GroupMembershipRequest parseGroupMembershipRequest(String json) {
        return new GroupMembershipRequest(extractStringField(json, "requesterId"), extractStringField(json, "userId"),
                                extractStringField(json, "groupId"));
    }

    public static GroupMembersRequest parseGroupMembersRequest(String json) {
        return new GroupMembersRequest(extractStringField(json, "groupId"));
    }

    // chat requests
    public static SendMessageRequest parseSendMessageRequest(String json) {
        return new SendMessageRequest(extractStringField(json, "senderId"), extractStringField(json, "conversationId"),
                                extractStringField(json, "content"), extractStringField(json, "attachmentOriginalName"),
                                extractStringField(json, "attachmentMimeType"), extractStringField(json, "attachmentBase64"));
    }

    public static EditMessageRequest parseEditMessageRequest(String json) {
        return new EditMessageRequest(extractStringField(json, "messageId"), extractStringField(json, "requesterId"),
                                extractStringField(json, "newContent"));
    }

    public static DeleteMessageRequest parseDeleteMessageRequest(String json) {
        return new DeleteMessageRequest(extractStringField(json, "messageId"), extractStringField(json, "requesterId"));
    }

    public static ReportMessageRequest parseReportMessageRequest(String json) {
        return new ReportMessageRequest(extractStringField(json, "reportedMessageId"), extractStringField(json, "reporterUserId"),
                                extractStringField(json, "reason"));
    }

    public static SavedMessageRequest parseSavedMessageRequest(String json) {
        return new SavedMessageRequest(extractStringField(json, "userId"), extractStringField(json, "messageId"));
    }

    public static AddReactionRequest parseAddReactionRequest(String json) {
        String reactionTypeStr = extractStringField(json, "reactionType");
        ReactionType reactionType = reactionTypeStr != null ? ReactionType.valueOf(reactionTypeStr) : null;

        return new AddReactionRequest( extractStringField(json, "messageId"), extractStringField(json, "userId"), reactionType);
    }

    public static RemoveReactionRequest parseRemoveReactionRequest(String json) {
        return new RemoveReactionRequest(extractStringField(json, "messageId"), extractStringField(json, "userId"));
    }

    // conversation requests
    public static ListConversationsRequest parseListConversationsRequest(String json) {
        return new ListConversationsRequest(extractStringField(json, "userId"));
    }

    public static ConversationInfoRequest parseConversationInfoRequest(String json) {
        return new ConversationInfoRequest(extractStringField(json, "conversationId"), extractStringField(json, "requesterId"));
    }

    public static PollMessagesRequest parsePollMessagesRequest(String json) {
        String sinceStr = extractStringField(json, "since");
        LocalDateTime since = sinceStr != null ? DateTimeUtil.parse(sinceStr) : null;

        return new PollMessagesRequest(extractStringField(json, "conversationId"), since);
    }

    // private method for extracting value from json text with regex
    private static String extractStringField(String json, String field) {
        // check for null safety
        if (json == null) {
            return null;
        }
        
        // getting field value using regex
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);

        // return value
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
