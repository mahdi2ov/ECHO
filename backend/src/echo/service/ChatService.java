package echo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import echo.config.AppConfig;
import echo.dto.response.ChatInfoDTO;
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
import echo.repository.ConversationRepository;
import echo.repository.GroupRepository;
import echo.repository.MessageRepository;
import echo.repository.ReportRepository;
import echo.repository.UserRepository;
import echo.security.EncryptionUtil;
import echo.security.validation.message.MessageValidator;
import echo.util.DateTimeUtil;
import echo.util.IdGenerator;

public class ChatService {
    private final MessageRepository messageRepository;
    private final MessageValidator messageValidator;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final ReportRepository reportRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // constructor
    public ChatService(MessageRepository messageRepository, MessageValidator messageValidator,
            ConversationService conversationService, ConversationRepository conversationRepository,
            ReportRepository reportRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
        this.reportRepository = reportRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions

    // message methods
    public synchronized Message sendMessage(String conversationId, String senderId, String content) {
        return sendMessage(conversationId, senderId, content, "", "", "");
    }

    public synchronized Message sendMessage(String conversationId, String senderId, String content,
                                             String attachmentOriginalName, String attachmentMimeType,
                                             String attachmentBase64) {
        // getting conversation and check for exist
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        // message should have content or an attachment
        if ((attachmentBase64 == null || attachmentBase64.isEmpty()) && content.isEmpty()) {
            throw new RuntimeException("Message should have content or an attachment.");
        }

        // validate if message have content
        if (!content.isEmpty()) {
            List<String> validationErrors = messageValidator.validate(content, senderId);
            if (!validationErrors.isEmpty()) {
                throw new RuntimeException(String.join("\n", validationErrors));
            }
        }

        // encrypt message
        String encryptedContent = EncryptionUtil.encrypt(content);
        Message message = new Message(IdGenerator.nextMessageId(), senderId, conversationId, encryptedContent);

        // saving attachment if message have
        if (attachmentBase64 != null && !attachmentBase64.isEmpty()) {
            Attachment attachment = saveAttachment(conversationId, attachmentOriginalName, attachmentMimeType,
                    attachmentBase64);
            message.setAttachment(attachment);
        }

        // save message
        messageRepository.saveMessage(message);

        // set last message for conversation
        conversationService.setConversationLastMessageAt(conversationId, DateTimeUtil.now());
        return message;
    }

    private Attachment saveAttachment(String conversationId, String originalName, String mimeType, String base64Data) {
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String fileId = IdGenerator.nextAttachmentId();

        Path conversationMediaDir = Paths.get(AppConfig.getMediaDirPath() + "\\" + conversationId);
        Path filePath = conversationMediaDir.resolve(fileId + "_" + originalName);

        try {
            Files.createDirectories(conversationMediaDir);
            Files.write(filePath, fileBytes);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save attachment.", exception);
        }
        
        return new Attachment(fileId, originalName, filePath.toString(), mimeType, fileBytes.length);
    }

    public synchronized Message editMessage(String messageId, String requsterId, String newContent) {
        // getting message by id
        Message message = messageRepository.getMessageById(messageId);

        // checking some conditions
        if (message == null) {
            throw new RuntimeException("Message not found.");
        }       
        if (!message.getSenderId().equals(requsterId)) {
            throw new RuntimeException("Only sender user can edit his message.");
        }
        if (message.getDeleted()) {
            throw new RuntimeException("Deleted message can't be edited.");
        }

        // validate new content
        List<String> validationErrors = messageValidator.validate(newContent, message.getSenderId());
        if (!validationErrors.isEmpty()) {
            throw new RuntimeException(String.join("\n", validationErrors));
        }

        // decrypt old message and save into history
        String decryptedOldContent = EncryptionUtil.decrypt(message.getContent());
        message.getEditHistory().add(new MessageEdit(decryptedOldContent));

        // encrypt new content and set to message
        String encryptedNewContent = EncryptionUtil.encrypt(newContent);
        message.setContent(encryptedNewContent);

        // update message
        messageRepository.updateMessage(message);
        return message;
    }

    public synchronized void deleteMessage(String messageId, String requsterId) {
        // getting message by id
        Message message = messageRepository.getMessageById(messageId);

        // checking some conditions
        if (message == null) {
            throw new RuntimeException("Message not found.");
        }
        if (!message.getSenderId().equals(requsterId)) {
            throw new RuntimeException("Only sender user can delete his message.");
        }
        if (message.getDeleted()) {
            throw new RuntimeException("Message already deleted.");
        }

        // set message as deleted
        message.setDeleted(true);

        // delete form database
        messageRepository.deleteMessageById(messageId);
    }

    // polling message method
    public synchronized List<Message> getMessagesSince(String conversationId, LocalDateTime since) {
        // check conversation for exist
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        return messageRepository.getMessagesByConversationIdSince(conversationId, since);
    }
    
    // reaction methods
    public synchronized Message addReaction(String messageId, String userId, ReactionType reactionType) {
        // getting message and checking some conditions
        Message message = messageRepository.getMessageById(messageId);
        if (message == null) {
            throw new RuntimeException("Message not found.");
        }
        if (message.getDeleted()) {
            throw new RuntimeException("Deleted message cannot be reacted to.");
        }

        // repeated reaction from a user should replace in old user reaction
        List<Reaction> reactions = message.getReactions();
        int existReactionIndex = -1;
        for (int i = 0; i < reactions.size(); i++) {
            Reaction reaction = reactions.get(i);
            if (reaction.getUserId().equals(userId)) {
                existReactionIndex = i;
                break;
            }
        }

        Reaction newReaction = new Reaction(userId, reactionType);

        if (existReactionIndex != -1) {
            reactions.set(existReactionIndex, newReaction);
        } else {
            reactions.add(newReaction);
        }

        // save reaction to database
        messageRepository.updateMessage(message);
        return message;
    }

    public synchronized Message removeReaction(String messageId, String userId) {
        // getting message and check for exist
        Message message = messageRepository.getMessageById(messageId);
        if (message == null) {
            throw new RuntimeException("Message not found.");
        }

        // delete reaction from database if exits
        List<Reaction> reactions = message.getReactions();
        for (int i = 0; i < reactions.size(); i++) {
            Reaction reaction = reactions.get(i);

            if (reaction.getUserId().equals(userId)) {
                reactions.remove(i);
                messageRepository.updateMessage(message);
                break;
            }
        }

        return message;
    }
    
    // report methods
    public synchronized void addReport(String reportedMessageId, String reporterUserId, String reason) {
        // creating a new report and save into database
        Report report = new Report(IdGenerator.nextReportId(), reportedMessageId, reporterUserId, reason);
        reportRepository.saveReport(report);
    }

    public synchronized void resolveReport(String reportId) {
        // check report for exist
        Report report = reportRepository.getReportById(reportId);
        if (report == null) {
            throw new RuntimeException("Report not found.");
        }
        
        // resolve report
        report.setResolved(true);
        reportRepository.updateReport(report);
    }

    public synchronized List<Report> getAllReports() {
        return reportRepository.getAllReports();
    }

    // conversation info method
    public synchronized ChatInfoDTO getConversationInfo(String conversationId, String requesterId) {
        // getting conversation by id
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        // check conversation for exist
        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        // create ChatInfoDTO based on conversation type
        if (conversation.getType() == ConversationType.GROUP) {
            Group group = groupRepository.getGroupByConversationId(conversationId);
            if (group == null) {
                throw new RuntimeException("Group not found.");
            }

            String title = group.getName(), subTitle;
            if (group.getDescription() != null) {
                subTitle = String.format("%s, %d Members", group.getDescription(), conversation.getMembersId().size());
            } else {
                subTitle = String.format("%d Members", conversation.getMembersId().size());
            }
            return new ChatInfoDTO(conversationId, title, subTitle, conversation.getType(), null, group.getId());
        } else {
            String otherUserId = null;
            for (String memberId : conversation.getMembersId()) {
                if (!memberId.equals(requesterId)) {
                    otherUserId = memberId;
                    break;
                }
            }
            if (otherUserId == null) {
                throw new RuntimeException("User not found.");
            }

            User otherUser = userRepository.getUserById(otherUserId);
            if (otherUser == null) {
                throw new RuntimeException("User not found.");
            }

            String title = otherUser.getUsername(), subTitle = "Private";
            return new ChatInfoDTO(conversationId, title, subTitle, conversation.getType(), otherUser.getId(), null);
        }
    }
}
