package echo.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.model.Conversation;
import echo.model.ConversationType;
import echo.repository.ConversationRepository;
import echo.util.FileUtil;

public class FileConversationRepository implements ConversationRepository {
    private final Path conversationData;
    private final List<Conversation> inMemoryConversationData;

    // constructor
    public FileConversationRepository() {
        conversationData = Paths.get(AppConfig.getConversationsPath());
        inMemoryConversationData = new ArrayList<>();
        readFile();
    }

    // create
    @Override
    public void saveConversation(Conversation conversation) {
        inMemoryConversationData.add(conversation);
        writeFile();
    }

    // read
    @Override
    public Conversation getConversationById(String id) {
        for (Conversation conversation : inMemoryConversationData) {
            if (conversation.getId().equals(id)) {
                return conversation;
            }
        }
        return null;
    }

    @Override
    public boolean existById(String id) {
        return getConversationById(id) != null;
    }

    @Override
    public List<Conversation> getAllConversations() {
        return new ArrayList<>(inMemoryConversationData);
    }

    // update
    @Override
    public void updateConversation(Conversation conversation) {
        for (int i = 0; i < inMemoryConversationData.size(); i++) {
            if (inMemoryConversationData.get(i).getId().equals(conversation.getId())) {
                inMemoryConversationData.set(i, conversation);
                break;
            }
        }
        writeFile();
    }

    // delete
    @Override
    public void deleteById(String id) {
        for (int i = 0; i < inMemoryConversationData.size(); i++) {
            if (inMemoryConversationData.get(i).getId().equals(id)) {
                inMemoryConversationData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // util methods
    @Override
    public List<Conversation> getConversationsByUserId(String userId) {
        List<Conversation> conversationsFound = new ArrayList<>();
        for (int i = 0; i < inMemoryConversationData.size(); i++) {
            if (inMemoryConversationData.get(i).getMembersId().contains(userId)) {
                conversationsFound.add(inMemoryConversationData.get(i));
            }
        }
        return conversationsFound;
    }

    @Override
    public List<Conversation> getConversationsByType(ConversationType type) {
        List<Conversation> conversationsFound = new ArrayList<>();
        for (int i = 0; i < inMemoryConversationData.size(); i++) {
            if (inMemoryConversationData.get(i).getType() == type) {
                conversationsFound.add(inMemoryConversationData.get(i));
            }
        }
        return conversationsFound;
    }

    // private util methods for using in this class
    private void readFile() {
        inMemoryConversationData.clear();

        try (BufferedReader reader = Files.newBufferedReader(conversationData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryConversationData.add(FileUtil.deserializeConversation(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(conversationData, StandardCharsets.UTF_8)) {
            for (Conversation conversation : inMemoryConversationData) {
                writer.write(FileUtil.serializeConversation(conversation));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
