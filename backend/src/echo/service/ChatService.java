package echo.service;

import java.util.List;

import echo.model.Conversation;
import echo.model.Message;
import echo.model.MessageEdit;
import echo.model.Report;
import echo.repository.ConversationRepository;
import echo.repository.MessageRepository;
import echo.repository.ReportRepository;
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

    // constructor
    public ChatService(MessageRepository messageRepository, MessageValidator messageValidator,
            ConversationService conversationService, ConversationRepository conversationRepository,
            ReportRepository reportRepository) {
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
        this.reportRepository = reportRepository;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions

    // message methods
    public synchronized Message sendMessage(String conversationId, String senderId, String content) {
        Conversation conversation = conversationRepository.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }
        // validate message
        List<String> validationErrors = messageValidator.validate(content, senderId);
        if (!validationErrors.isEmpty()) {
            throw new RuntimeException(String.join("\n", validationErrors));
        }

        // encrypt message
        String encryptedContent = EncryptionUtil.encrypt(content);
        Message message = new Message(IdGenerator.nextMessageId(), senderId, conversationId, encryptedContent);

        // save message
        messageRepository.saveMessage(message);

        // set last message for conversation
        conversationService.setConversationLastMessageAt(conversationId, DateTimeUtil.now());
        return message;
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
    

    // report methods
    public void addReport(String reportedMessageId, String reporterUserId, String reason) {
        Report report = new Report(IdGenerator.nextReportId(), reportedMessageId, reporterUserId, reason);
        reportRepository.saveReport(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.getAllReports();
    }

    public void resolveReport(String reportId) {
        // check report for exist
        Report report = reportRepository.getReportById(reportId);
        if (report == null) {
            throw new RuntimeException("Report not found.");
        }

        // resolve report
        report.setResolved(true);
        reportRepository.updateReport(report);
    }

    // reaction methods
    // public synchronized Reaction addReaction(String messageId, String userId, ReactionType reactionType) {}
    // public synchronized Reaction deleteReaction(String messageId, String userId) {}
}
