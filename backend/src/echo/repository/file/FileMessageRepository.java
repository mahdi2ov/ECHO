package echo.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.model.Message;
import echo.repository.MessageRepository;
import echo.util.FileUtil;

public class FileMessageRepository implements MessageRepository {
    private final Path messageData;
    private final List<Message> inMemoryMessageData;

    // constructor
    public FileMessageRepository() {
        messageData = Paths.get(AppConfig.getMessagesPath());
        inMemoryMessageData = new ArrayList<>();
        readFile();
    }

    // create
    @Override
    public synchronized void saveMessage(Message message) {
        inMemoryMessageData.add(message);
        writeFile();
    }

    // read
    @Override
    public synchronized Message getMessageById(String id) {
        for (Message message : inMemoryMessageData) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean existById(String id) {
        return getMessageById(id) != null;
    }

    @Override
    public synchronized List<Message> getAllMessages() {
        return new ArrayList<>(inMemoryMessageData);
    }

    // update
    @Override
    public synchronized void updateMessage(Message message) {
        for (int i = 0; i < inMemoryMessageData.size(); i++) {
            if (inMemoryMessageData.get(i).getId().equals(message.getId())) {
                inMemoryMessageData.set(i, message);
                break;
            }
        }
        writeFile();
    }

    // delete
    @Override
    public synchronized void deleteMessageById(String id) {
        for (int i = 0; i < inMemoryMessageData.size(); i++) {
            if (inMemoryMessageData.get(i).getId().equals(id)) {
                inMemoryMessageData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // util methods
    @Override
    public synchronized List<Message> getMessagesBySenderId(String senderId) {
        List<Message> messagesFound = new ArrayList<>();
        for (int i = 0; i < inMemoryMessageData.size(); i++) {
            if (inMemoryMessageData.get(i).getSenderId().equals(senderId)) {
                messagesFound.add(inMemoryMessageData.get(i));
            }
        }
        return messagesFound;
    }

    @Override
    public synchronized List<Message> getMessagesByConversationId(String conversationId) {
        List<Message> messagesFound = new ArrayList<>();
        for (int i = 0; i < inMemoryMessageData.size(); i++) {
            if (inMemoryMessageData.get(i).getConversationId().equals(conversationId)) {
                messagesFound.add(inMemoryMessageData.get(i));
            }
        }
        return messagesFound;
    }

    @Override
    public synchronized List<Message> getMessagesByConversationIdSince(String conversationId, LocalDateTime since) {
        List<Message> messagesFound = new ArrayList<>();
        for (int i = 0; i < inMemoryMessageData.size(); i++) {
            Message message = inMemoryMessageData.get(i);
            if (message.getConversationId().equals(conversationId) && message.getCreatedAt().isAfter(since)) {
                messagesFound.add(message);
            }
        }
        return messagesFound;
    }

    // private util methods for using in this class
    private synchronized void readFile() {
        inMemoryMessageData.clear();

        try (BufferedReader reader = Files.newBufferedReader(messageData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryMessageData.add(FileUtil.deserializeMessage(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private synchronized void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(messageData, StandardCharsets.UTF_8)) {
            for (Message message : inMemoryMessageData) {
                writer.write(FileUtil.serializeMessage(message));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
