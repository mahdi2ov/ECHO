package echo.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import echo.model.Admin;
import echo.model.Attachment;
import echo.model.Conversation;
import echo.model.ConversationType;
import echo.model.Group;
import echo.model.Message;
import echo.model.MessageEdit;
import echo.model.Reaction;
import echo.model.ReactionType;
import echo.model.Report;
import echo.model.User;

public class FileUtil {
    // private constructor to prevent from instantiation
    private FileUtil() {
    }

    // user 
    public static String serializeUser(User user) {
        return String.format("%s||%s||%s||%s||%s||%s||%s||%s", escape(user.getUsername()), escape(user.getEmail()),
                    escape(user.getId()), escape(user.getPasswordHash()), escape(user.getPasswordsalt()), escape(user.getProfileImagePath()),
                    user.getLocked(), escape(user.getLockUntil() == null ? "" : DateTimeUtil.format(user.getLockUntil())));
    }

    public static User deserializeUser(String userLine) {
        String[] userParts = userLine.split("\\|\\|", -1);

        String username = unescape(userParts[0]);
        String email = unescape(userParts[1]);
        String id = unescape(userParts[2]);
        String passwordHash = unescape(userParts[3]);
        String passwordSalt = userParts[4].isEmpty() ? null : unescape(userParts[4]);
        String profileImagePath = userParts[5].isEmpty() ? null : unescape(userParts[5]);
        boolean locked = Boolean.parseBoolean(userParts[6]);
        LocalDateTime lockUntil = userParts[7].isEmpty() ? null : DateTimeUtil.parse(unescape(userParts[7]));

        User user = new User(username, email, id, passwordHash);
        user.setPasswordsalt(passwordSalt);
        user.setProfileImagePath(profileImagePath);
        user.setLocked(locked);
        user.setLockUntil(lockUntil);

        return user;
    }

    // message
    public static String serializeMessage(Message message) {
        String editHistory = serializeEditHistory(message.getEditHistory());
        String reactions = serializeReactions(message.getReactions());
        String attachment = serializeAttachment(message.getAttachment());

        return String.format("%s||%s||%s||%s||%s||%s||%s||%s||%s||%s", escape(message.getId()), escape(message.getSenderId()),
                    escape(message.getConversationId()), escape(message.getContent()), escape(DateTimeUtil.format(message.getCreatedAt())),
                    message.getEdited(), message.getDeleted(), editHistory, reactions, attachment);
    }

    public static Message deserializeMessage(String messageLine) {
        String[] messageParts = messageLine.split("\\|\\|", -1);

        String id = unescape(messageParts[0]);
        String senderId = unescape(messageParts[1]);
        String conversationId = unescape(messageParts[2]);
        String content = unescape(messageParts[3]);
        LocalDateTime createdAt = DateTimeUtil.parse(unescape(messageParts[4]));
        boolean edited = Boolean.parseBoolean(messageParts[5]);
        boolean deleted = Boolean.parseBoolean(messageParts[6]);
        List<MessageEdit> editHistory = deserializeEditHistory(messageParts[7]);
        List<Reaction> reactions = deserializeReactions(messageParts[8]);
        Attachment attachment = deserializeAttachment(messageParts[9]);

        Message message = new Message(id, senderId, conversationId, content, createdAt);
        message.setEdited(edited);
        message.setDeleted(deleted);

        for (MessageEdit edit : editHistory) {
            message.getEditHistory().add(edit);
        }
        for (Reaction reaction : reactions) {
            message.getReactions().add(reaction);
        }
        message.setAttachment(attachment);

        return message;
    }

    // messageEdit as list for editHistory
    private static String serializeEditHistory(List<MessageEdit> editHistory) {
        if (editHistory == null || editHistory.isEmpty()) {
            return "";
        }
        
        List<String> serializedMessageEdits = new ArrayList<>();
        for (MessageEdit edit : editHistory) {
            serializedMessageEdits.add(String.format("%s::%s", escape(edit.getOldContent()), escape(DateTimeUtil.format(edit.getEditedAt()))));
        }

        return String.join(",,", serializedMessageEdits);
    }

    private static List<MessageEdit> deserializeEditHistory(String editHistoryLine) {
        List<MessageEdit> editHistory = new ArrayList<>();
        if (editHistoryLine == null || editHistoryLine.isEmpty()) {
            return editHistory;
        }

        String[] stringEditHistory = editHistoryLine.split(",,", -1);
        for (String messageEditLine : stringEditHistory) {
            String[] messageEditParts = messageEditLine.split("::", -1);
            String oldContent = unescape(messageEditParts[0]);
            LocalDateTime editedAt = DateTimeUtil.parse(unescape(messageEditParts[1]));
            editHistory.add(new MessageEdit(oldContent, editedAt));
        }

        return editHistory;
    }

    // reaction as list for reactions
    private static String serializeReactions(List<Reaction> reactions) {
        if (reactions == null || reactions.isEmpty()) {
            return "";
        }

        List<String> stringReactions = new ArrayList<>();
        for (Reaction reaction : reactions) {
            stringReactions.add(String.format("%s::%s::%s", escape(reaction.getUserId()), escape(reaction.getEmoji().name()),
                    escape(DateTimeUtil.format(reaction.getReactedAt()))));
        }

        return String.join(",,", stringReactions);
    }

    private static List<Reaction> deserializeReactions(String reactionsLine) {
        List<Reaction> reactions = new ArrayList<>();
        if (reactionsLine == null || reactionsLine.isEmpty()) {
            return reactions;
        }

        String[] stringReactions = reactionsLine.split(",,", -1);
        for (String reactionLine : stringReactions) {
            String[] reactionParts = reactionLine.split("::", -1);
            String userId = unescape(reactionParts[0]);
            ReactionType emoji = ReactionType.valueOf(unescape(reactionParts[1]));
            LocalDateTime reactedAt = DateTimeUtil.parse(unescape(reactionParts[2]));
            reactions.add(new Reaction(userId, emoji, reactedAt));
        }

        return reactions;
    }

    // group
    public static String serializeGroup(Group group) {
        return String.format("%s||%s||%s||%s||%s||%s||%s", escape(group.getId()), escape(group.getName()),
                    escape(group.getDescription() == null ? "" : group.getDescription()),
                    escape(group.getProfileImagePath() == null ? "" : group.getProfileImagePath()),
                    escape(group.getConversationId()), escape(group.getOwnerId()),
                    String.join(",,", escapeList(group.getAdminsId())));
    }

    
    public static Group deserializeGroup(String groupLine) {
        String[] groupParts = groupLine.split("\\|\\|", -1);

        String id = unescape(groupParts[0]);
        String name = unescape(groupParts[1]);
        String description = groupParts[2].isEmpty() ? null : unescape(groupParts[2]);
        String profileImagePath = groupParts[3].isEmpty() ? null : unescape(groupParts[3]);
        String conversationId = unescape(groupParts[4]);
        String ownerId = unescape(groupParts[5]);

        Group group = new Group(id, name, ownerId, conversationId);
        group.setDescription(description);
        group.setProfileImagePath(profileImagePath);

        if (!groupParts[6].isEmpty()) {
            String[] adminsId = groupParts[6].split(",,", -1);
            for (String adminId : adminsId) {
                group.getAdminsId().add(unescape(adminId));
            }
        }

        return group;
    }

    // conversation
    public static String serializeConversation(Conversation conversation) {
        return String.format("%s||%s||%s||%s||%s||%s", escape(conversation.getId()), escape(conversation.getType().name()),
                    conversation.getPinned(), conversation.getArchived(),
                    conversation.getLastMessageAt() == null ? "" : escape(DateTimeUtil.format(conversation.getLastMessageAt())),
                    String.join(",,", escapeList(conversation.getMembersId())));
    }

    public static Conversation deserializeConversation(String conversationLine) {
        String[] conversationParts = conversationLine.split("\\|\\|", -1);

        String id = unescape(conversationParts[0]);
        ConversationType type = ConversationType.valueOf(unescape(conversationParts[1]));
        boolean pinned = Boolean.parseBoolean(conversationParts[2]);
        boolean archived = Boolean.parseBoolean(conversationParts[3]);
        LocalDateTime lastMessageAt = conversationParts[4].isEmpty() ? null : DateTimeUtil.parse(unescape(conversationParts[4]));

        Conversation conversation = new Conversation(id, type);
        conversation.setPinned(pinned);
        conversation.setArchived(archived);
        conversation.setLastMessageAt(lastMessageAt);

        if (!conversationParts[5].isEmpty()) {
            String[] membersId = conversationParts[5].split(",,", -1);
            for (String memberId : membersId) {
                conversation.getMembersId().add(unescape(memberId));
            }
        }

        return conversation;
    }

    // report 
    public static String serializeReport(Report report) {
        return String.format("%s||%s||%s||%s||%s||%s", escape(report.getId()), escape(report.getReportedMessageId()),
                    escape(report.getReporterUserId()), escape(report.getReason()),
                    escape(DateTimeUtil.format(report.getReportedAt())), report.getResolved());
    }

    public static Report deserializeReport(String reportLine) {
        String[] reportParts = reportLine.split("\\|\\|", -1);

        String id = unescape(reportParts[0]);
        String reportedMessageId = unescape(reportParts[1]);
        String reporterUserId = unescape(reportParts[2]);
        String reason = unescape(reportParts[3]);
        LocalDateTime reportedAt = DateTimeUtil.parse(unescape(reportParts[4]));
        boolean resolved = Boolean.parseBoolean(reportParts[5]);

        Report report = new Report(id, reportedMessageId, reporterUserId, reason, reportedAt);
        report.setResolved(resolved);

        return report;
    }

    // attachment
    public static String serializeAttachment(Attachment attachment) {
        if (attachment == null) {
            return "";
        }

        // different seperator beacause attachment save in message
        return String.format("%s::%s::%s::%s::%d", escape(attachment.getFileId()), escape(attachment.getOriginalName()),
                    escape(attachment.getFilePath()), escape(attachment.getMimeType()), attachment.getFileSize());
    }

    public static Attachment deserializeAttachment(String attachmentLine) {
        if (attachmentLine == null || attachmentLine.isEmpty()) {
            return null;
        }

        String[] attachmentParts = attachmentLine.split("::", -1);

        String fileId = unescape(attachmentParts[0]);
        String originalName = unescape(attachmentParts[1]);
        String filePath = unescape(attachmentParts[2]);
        String mimeType = unescape(attachmentParts[3]);
        long fileSize = Long.parseLong(attachmentParts[4]);

        return new Attachment(fileId, originalName, filePath, mimeType, fileSize);
    }

    // contact and blockedUser
    public static String serializeContactsAndBlockedUsers(String userId, List<String> contacts, List<String> blockedUsers) {
        return String.format("%s||%s||%s", escape(userId), String.join(",,", escapeList(contacts)),
                    String.join(",,", escapeList(blockedUsers)));
    }

    public static List<List<String>> deserializeContactsAndBlockedUsers(String contactAndBlockedUserLine) {
        String[] contactAndBlockedUserParts = contactAndBlockedUserLine.split("\\|\\|", -1);

        List<String> contacts = new ArrayList<>();
        if (!contactAndBlockedUserParts[1].isEmpty()) {
            for (String contactId : contactAndBlockedUserParts[1].split(",,", -1)) {
                contacts.add(unescape(contactId));
            }
        }

        List<String> blockedUsers = new ArrayList<>();
        if (!contactAndBlockedUserParts[2].isEmpty()) {
            for (String blockedUserId : contactAndBlockedUserParts[2].split(",,", -1)) {
                blockedUsers.add(unescape(blockedUserId));
            }
        }

        List<List<String>> contactsAndBlockedUsers = new ArrayList<>();
        contactsAndBlockedUsers.add(contacts);
        contactsAndBlockedUsers.add(blockedUsers);

        return contactsAndBlockedUsers;
    }

    // admin cli
    public static String serializeAdmin(Admin admin) {
        return String.format("%s||%s||%s", escape(admin.getUsername()), escape(admin.getPasswordHash()),
                    escape(admin.getPasswordSalt()));
    }

    public static Admin deserializeAdmin(String adminLine) {
        String[] adminParts = adminLine.split("\\|\\|", -1);

        String username = unescape(adminParts[0]);
        String passwordHash = unescape(adminParts[1]);
        String passwordSalt = unescape(adminParts[2]);

        Admin admin = new Admin(username, passwordHash);
        admin.setPasswordSalt(passwordSalt);

        return admin;
    }

    // private methods for escape/unscape text
    private static String escape(String value) {
        // check for null safety
        if (value == null) {
            return "";
        }

        // escaping text
        return value.replace("\\", "\\\\").replace("|", "\\|")
                .replace(",", "\\,").replace(":", "\\:").replace("\n", "\\n")
                .replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String unescape(String value) {
        // check for null safety
        if (value == null) {
            return "";
        }

        // unscaping text
        return value.replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\t", "\t").replace("\\|", "|").replace("\\,", ",")
                .replace("\\:", ":").replace("\\\\", "\\");
    }

    // method for escaping a list of items
    private static List<String> escapeList(List<String> values) {
        List<String> result = new ArrayList<>();
        if (values == null) {
            return result;
        }

        for (String value : values) {
            result.add(escape(value));
        }

        return result;
    }
}
