package echo.dto.request;

public class SendMessageRequest {
    private final String senderId;
    private final String conversationId;
    private final String content;
    private final String attachmentOriginalName;
    private final String attachmentMimeType;
    private final String attachmentBase64;

    // constructor for message without attachment
    public SendMessageRequest(String senderId, String conversationId, String content) {
        this(senderId, conversationId, content, "", "", "");
    }

    // constructor for message with attachment
    public SendMessageRequest(String senderId, String conversationId, String content,
                               String attachmentOriginalName, String attachmentMimeType, String attachmentBase64) {
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.content = content;
        this.attachmentOriginalName = attachmentOriginalName;
        this.attachmentMimeType = attachmentMimeType;
        this.attachmentBase64 = attachmentBase64;
    }

    // getters
    public String getSenderId() {
        return senderId;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getContent() {
        return content;
    }
    public String getAttachmentOriginalName() {
        return attachmentOriginalName;
    }
    public String getAttachmentMimeType() {
        return attachmentMimeType;
    }
    public String getAttachmentBase64() {
        return attachmentBase64;
    }
}
