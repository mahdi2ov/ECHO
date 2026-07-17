package echo.network.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import echo.controller.AuthController;
import echo.controller.ChatController;
import echo.controller.GroupController;
import echo.controller.SavedMessagesController;
import echo.controller.UserController;
import echo.dto.request.AddReactionRequest;
import echo.dto.request.DeleteMessageRequest;
import echo.dto.request.EditMessageRequest;
import echo.dto.request.RemoveReactionRequest;
import echo.dto.request.SavedMessageRequest;
import echo.dto.request.SendMessageRequest;
import echo.network.websocket.ECHOWebSocket;
import echo.repository.MessageRepository;
import echo.util.JsonUtil;

public class ECHOHttp implements HttpHandler {
    private final AuthController authController;
    private final UserController userController;
    private final GroupController groupController;
    private final ChatController chatController;
    private final SavedMessagesController savedMessagesController;
    private final MessageRepository messageRepository;
    private final ECHOWebSocket webSocketServer;

    public ECHOHttp(AuthController authController, UserController userController, GroupController groupController,
                      ChatController chatController, SavedMessagesController savedMessagesController,
                      MessageRepository messageRepository, ECHOWebSocket webSocketServer) {
        this.authController = authController;
        this.userController = userController;
        this.groupController = groupController;
        this.chatController = chatController;
        this.savedMessagesController = savedMessagesController;
        this.messageRepository = messageRepository;
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        applyCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 204, "");
            return;
        }

        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        String body = readBody(exchange);

        if (path.equals("/auth/signup") && method.equals("POST")) {
            sendResponse(exchange, 200, authController.signup(JsonUtil.parseSignupRequest(body)));
            return;
        }
        if (path.equals("/auth/login") && method.equals("POST")) {
            sendResponse(exchange, 200, authController.login(JsonUtil.parseLoginRequest(body)));
            return;
        }
        if (path.equals("/auth/forgot-password") && method.equals("POST")) {
            sendResponse(exchange, 200, authController.forgotPassword(JsonUtil.parseForgotPasswordRequest(body)));
            return;
        }
        if (path.equals("/auth/logout") && method.equals("POST")) {
            sendResponse(exchange, 200, authController.logout());
            return;
        }

        if (path.equals("/users/contacts") && method.equals("POST")) {
            sendResponse(exchange, 200, userController.addContact(JsonUtil.parseUserRelationRequest(body)));
            return;
        }
        if (path.equals("/users/contacts") && method.equals("DELETE")) {
            sendResponse(exchange, 200, userController.deleteContact(JsonUtil.parseUserRelationRequest(body)));
            return;
        }
        if (path.equals("/users/blocked-users") && method.equals("POST")) {
            sendResponse(exchange, 200, userController.blockUser(JsonUtil.parseUserRelationRequest(body)));
            return;
        }
        if (path.equals("/users/blocked-users") && method.equals("DELETE")) {
            sendResponse(exchange, 200, userController.unblockUser(JsonUtil.parseUserRelationRequest(body)));
            return;
        }
        if (path.equals("/users/profile/get") && method.equals("POST")) {
            sendResponse(exchange, 200, userController.getUserProfile(JsonUtil.parseProfileRequest(body)));
            return;
        }
        if (path.equals("/users/profile") && method.equals("PUT")) {
            sendResponse(exchange, 200, userController.updateProfile(JsonUtil.parseProfileRequest(body)));
            return;
        }
        if (path.equals("/users/contacts/list") && method.equals("POST")) {
            sendResponse(exchange, 200, userController.listContacts(JsonUtil.parseListUserRelationRequest(body)));
            return;
        }
        if (path.equals("/users/blocked-users/list") && method.equals("POST")) {
            sendResponse(exchange, 200, userController.listBlockedUsers(JsonUtil.parseListUserRelationRequest(body)));
            return;
        }

        if (path.equals("/groups") && method.equals("POST")) {
            sendResponse(exchange, 200, groupController.createGroup(JsonUtil.parseGroupRequest(body)));
            return;
        }
        if (path.equals("/groups") && method.equals("DELETE")) {
            sendResponse(exchange, 200, groupController.deleteGroup(JsonUtil.parseGroupRequest(body)));
            return;
        }
        if (path.equals("/groups") && method.equals("PUT")) {
            sendResponse(exchange, 200, groupController.updateGroup(JsonUtil.parseUpdateGroupRequest(body)));
            return;
        }
        if (path.equals("/groups/members/add") && method.equals("POST")) {
            sendResponse(exchange, 200, groupController.addMemberToGroup(JsonUtil.parseGroupMembershipRequest(body)));
            return;
        }
        if (path.equals("/groups/members/remove") && method.equals("DELETE")) {
            sendResponse(exchange, 200, groupController.removeMemberFromGroup(JsonUtil.parseGroupMembershipRequest(body)));
            return;
        }
        if (path.equals("/groups/admins/add") && method.equals("POST")) {
            sendResponse(exchange, 200, groupController.addAdminToGroup(JsonUtil.parseGroupMembershipRequest(body)));
            return;
        }
        if (path.equals("/groups/admins/remove") && method.equals("DELETE")) {
            sendResponse(exchange, 200, groupController.removeAdminFromGroup(JsonUtil.parseGroupMembershipRequest(body)));
            return;
        }
        if (path.equals("/groups/members/list") && method.equals("POST")) {
            sendResponse(exchange, 200, groupController.getGroupMembers(JsonUtil.parseGroupMembersRequest(body)));
            return;
        }

        if (path.equals("/messages/poll") && method.equals("POST")) {
            sendResponse(exchange, 200, chatController.pollMessages(JsonUtil.parsePollMessagesRequest(body)));
            return;
        }
        if (path.equals("/messages") && method.equals("POST")) {
            SendMessageRequest request = JsonUtil.parseSendMessageRequest(body);
            String response = chatController.sendMessage(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null) {
                webSocketServer.broadcastToConversation(request.getConversationId(), response);
            }
            return;
        }
        if (path.matches("/messages/[^/]+/reactions/[^/]+") && method.equals("DELETE")) {
            String[] parts = path.split("/");
            String messageId = parts[2];
            String userId = parts[4];
            RemoveReactionRequest request = JsonUtil.parseRemoveReactionRequest(body);
            if (request.getMessageId() == null) {
                request = new RemoveReactionRequest(messageId, userId);
            }
            String conversationId = null;
            if (messageRepository.getMessageById(messageId) != null) {
                conversationId = messageRepository.getMessageById(messageId).getConversationId();
            }
            String response = chatController.removeReaction(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null && conversationId != null) {
                webSocketServer.broadcastToConversation(conversationId, response);
            }
            return;
        }
        if (path.matches("/messages/[^/]+/reactions") && method.equals("POST")) {
            String[] parts = path.split("/");
            String messageId = parts[2];
            AddReactionRequest request = JsonUtil.parseAddReactionRequest(body);
            if (request.getMessageId() == null) {
                request = new AddReactionRequest(messageId, request.getUserId(), request.getReactionType());
            }
            String conversationId = null;
            if (messageRepository.getMessageById(messageId) != null) {
                conversationId = messageRepository.getMessageById(messageId).getConversationId();
            }
            String response = chatController.addReaction(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null && conversationId != null) {
                webSocketServer.broadcastToConversation(conversationId, response);
            }
            return;
        }
        if (path.matches("/messages/[^/]+") && method.equals("DELETE")) {
            String messageId = path.substring("/messages/".length());
            DeleteMessageRequest request = JsonUtil.parseDeleteMessageRequest(body);
            if (request.getMessageId() == null) {
                request = new DeleteMessageRequest(messageId, request.getRequesterId());
            }
            String conversationId = null;
            if (messageRepository.getMessageById(messageId) != null) {
                conversationId = messageRepository.getMessageById(messageId).getConversationId();
            }
            String response = chatController.deleteMessage(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null && conversationId != null) {
                webSocketServer.broadcastToConversation(conversationId, response);
            }
            return;
        }
        if (path.matches("/messages/[^/]+") && method.equals("PUT")) {
            String messageId = path.substring("/messages/".length());
            EditMessageRequest request = JsonUtil.parseEditMessageRequest(body);
            if (request.getMessageId() == null) {
                request = new EditMessageRequest(messageId, request.getRequesterId(), request.getNewContent());
            }
            String conversationId = null;
            if (messageRepository.getMessageById(messageId) != null) {
                conversationId = messageRepository.getMessageById(messageId).getConversationId();
            }
            String response = chatController.editMessage(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null && conversationId != null) {
                webSocketServer.broadcastToConversation(conversationId, response);
            }
            return;
        }
        if (path.equals("/reports") && method.equals("POST")) {
            sendResponse(exchange, 200, chatController.reportMessage(JsonUtil.parseReportMessageRequest(body)));
            return;
        }
        if (path.equals("/conversations/list") && method.equals("POST")) {
            sendResponse(exchange, 200, chatController.listConversations(JsonUtil.parseListConversationsRequest(body)));
            return;
        }
        if (path.equals("/conversations/info") && method.equals("POST")) {
            sendResponse(exchange, 200, chatController.getConversationInfo(JsonUtil.parseConversationInfoRequest(body)));
            return;
        }
        if (path.equals("/saved-messages") && method.equals("POST")) {
            SavedMessageRequest request = JsonUtil.parseSavedMessageRequest(body);
            String response = savedMessagesController.saveMessage(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null) {
                webSocketServer.broadcastToConversation("SAVED_MESSAGE-" + request.getUserId(), response);
            }
            return;
        }
        if (path.equals("/saved-messages") && method.equals("DELETE")) {
            SavedMessageRequest request = JsonUtil.parseSavedMessageRequest(body);
            String response = savedMessagesController.deleteMessage(request);
            sendResponse(exchange, 200, response);
            if (isSuccess(response) && webSocketServer != null) {
                webSocketServer.broadcastToConversation("SAVED_MESSAGE-" + request.getUserId(), response);
            }
            return;
        }

        sendResponse(exchange, 404, "{\"success\":false,\"message\":\"Route not found.\",\"data\":null}");
    }

    private void applyCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response == null ? new byte[0] : response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        if (statusCode == 204) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            exchange.sendResponseHeaders(statusCode, bytes.length);
        }
        if (statusCode != 204 && bytes.length > 0) {
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }

    private boolean isSuccess(String response) {
        return response != null && response.contains("\"success\":true");
    }
}
