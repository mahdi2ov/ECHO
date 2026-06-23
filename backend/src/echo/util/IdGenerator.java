package echo.util;

public class IdGenerator {
    private static long userCounter = 1;
    private static long messageCounter = 1;
    private static long groupCounter = 1;
    private static long conversationCounter = 1;
    private static long attachmentCounter = 1;
    private static long reportCounter = 1;
    
    // private constructor because methods of this class are used as static methods
    private IdGenerator() {
    }

    // get next id for each type
    public static synchronized String nextUserId() {
        return "USER-" + userCounter++;
    }
    public static synchronized String nextMessageId() {
        return "Message-" + messageCounter++;
    }
    public static synchronized String nextGroupId() {
        return "Group-" + groupCounter++;
    }
    public static synchronized String nextConversationId() {
        return "Conversation-" + conversationCounter++;
    }
    public static synchronized String nextAttachmentId() {
        return "Attachment-" + attachmentCounter++;
    }
    public static synchronized String nextReportId() {
        return "Report-" + reportCounter++;
    }
}
