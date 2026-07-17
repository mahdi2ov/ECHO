package echo.network.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ECHOWebSocket {
    private final int port;
    private final Map<String, Set<ClientConnection>> conversationClients;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public ECHOWebSocket(int port) {
        this.port = port;
        this.conversationClients = new HashMap<String, Set<ClientConnection>>();
    }

    public void start() {
        running = true;
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                acceptLoop();
            }
        });
        serverThread.start();
    }

    public synchronized void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        for (Set<ClientConnection> room : conversationClients.values()) {
            for (ClientConnection client : room) {
                client.closeQuietly();
            }
        }
        conversationClients.clear();
    }

    public synchronized void broadcastToConversation(String conversationId, String message) {
        Set<ClientConnection> room = conversationClients.get(conversationId);
        if (room == null || room.isEmpty()) {
            return;
        }

        List<ClientConnection> toRemove = new ArrayList<ClientConnection>();
        for (ClientConnection client : room) {
            if (!client.send(message)) {
                toRemove.add(client);
            }
        }

        for (ClientConnection client : toRemove) {
            room.remove(client);
            client.closeQuietly();
        }

        if (room.isEmpty()) {
            conversationClients.remove(conversationId);
        }
    }

    private void acceptLoop() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                final Socket socket = serverSocket.accept();
                Thread clientThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleClient(socket);
                    }
                });
                clientThread.start();
            }
        } catch (IOException exception) {
            if (running) {
                System.err.println("WebSocket server stopped: " + exception.getMessage());
            }
        }
    }

    private void handleClient(Socket socket) {
        ClientConnection client = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream outputStream = socket.getOutputStream();

            String requestLine = reader.readLine();
            if (requestLine == null || !requestLine.startsWith("GET ")) {
                socket.close();
                return;
            }

            String path = requestLine.split(" ")[1];
            String conversationId = extractQueryParam(path, "conversationId");
            if (conversationId == null || conversationId.isEmpty()) {
                writeHttpError(outputStream, "400 Bad Request", "Missing conversationId");
                socket.close();
                return;
            }

            String webSocketKey = null;
            String line;
            while ((line = reader.readLine()) != null && line.length() > 0) {
                String lower = line.toLowerCase();
                if (lower.startsWith("sec-websocket-key:")) {
                    webSocketKey = line.substring(line.indexOf(':') + 1).trim();
                }
            }

            if (webSocketKey == null || webSocketKey.isEmpty()) {
                writeHttpError(outputStream, "400 Bad Request", "Missing Sec-WebSocket-Key");
                socket.close();
                return;
            }

            String acceptKey = createWebSocketAcceptKey(webSocketKey);
            String response = "HTTP/1.1 101 Switching Protocols\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            client = new ClientConnection(socket, conversationId);
            addClient(conversationId, client);
            readLoop(client);
        } catch (IOException exception) {
            if (client != null) {
                removeClient(client.getConversationId(), client);
            }
            closeSocket(socket);
        } finally {
            if (client != null) {
                removeClient(client.getConversationId(), client);
                client.closeQuietly();
            }
        }
    }

    private void readLoop(ClientConnection client) {
        try {
            while (running && !client.isClosed()) {
                String message = readTextFrame(client.getInputStream());
                if (message == null) {
                    break;
                }
                if (message.length() > 0) {
                    broadcastToConversation(client.getConversationId(), message);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private synchronized void addClient(String conversationId, ClientConnection client) {
        Set<ClientConnection> room = conversationClients.get(conversationId);
        if (room == null) {
            room = new HashSet<ClientConnection>();
            conversationClients.put(conversationId, room);
        }
        room.add(client);
    }

    private synchronized void removeClient(String conversationId, ClientConnection client) {
        Set<ClientConnection> room = conversationClients.get(conversationId);
        if (room == null) {
            return;
        }
        room.remove(client);
        if (room.isEmpty()) {
            conversationClients.remove(conversationId);
        }
    }

    private String readTextFrame(InputStream inputStream) throws IOException {
        int first = inputStream.read();
        if (first == -1) {
            return null;
        }
        int second = inputStream.read();
        if (second == -1) {
            return null;
        }

        int opcode = first & 0x0F;
        int masked = second & 0x80;
        long payloadLength = second & 0x7F;

        if (payloadLength == 126) {
            payloadLength = (inputStream.read() << 8) | inputStream.read();
        } else if (payloadLength == 127) {
            payloadLength = 0;
            for (int i = 0; i < 8; i++) {
                payloadLength = (payloadLength << 8) | (inputStream.read() & 0xFF);
            }
        }

        byte[] mask = null;
        if (masked != 0) {
            mask = new byte[4];
            if (inputStream.read(mask) != 4) {
                return null;
            }
        }

        byte[] payload = new byte[(int) payloadLength];
        int offset = 0;
        while (offset < payload.length) {
            int read = inputStream.read(payload, offset, payload.length - offset);
            if (read == -1) {
                return null;
            }
            offset += read;
        }

        if (masked != 0 && mask != null) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (payload[i] ^ mask[i % 4]);
            }
        }

        if (opcode == 0x8) {
            return null;
        }
        if (opcode != 0x1) {
            return "";
        }

        return new String(payload, StandardCharsets.UTF_8);
    }

    private void writeHttpError(OutputStream outputStream, String status, String message) throws IOException {
        String body = message == null ? "" : message;
        String response = "HTTP/1.1 " + status + "\r\n"
                + "Content-Type: text/plain; charset=UTF-8\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n"
                + body;
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String extractQueryParam(String path, String key) {
        int questionMark = path.indexOf('?');
        if (questionMark == -1) {
            return null;
        }

        String query = path.substring(questionMark + 1);
        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String[] kv = pairs[i].split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1];
            }
        }
        return null;
    }

    private String createWebSocketAcceptKey(String clientKey) {
        try {
            String magic = clientKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(magic.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create websocket accept key.", exception);
        }
    }

    private void closeSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static class ClientConnection {
        private final Socket socket;
        private final String conversationId;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private boolean closed;

        ClientConnection(Socket socket, String conversationId) throws IOException {
            this.socket = socket;
            this.conversationId = conversationId;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.closed = false;
        }

        String getConversationId() {
            return conversationId;
        }

        InputStream getInputStream() {
            return inputStream;
        }

        synchronized boolean send(String message) {
            if (closed) {
                return false;
            }

            try {
                byte[] payload = message == null ? new byte[0] : message.getBytes(StandardCharsets.UTF_8);
                int length = payload.length;
                outputStream.write(0x81);
                if (length <= 125) {
                    outputStream.write(length);
                } else if (length <= 65535) {
                    outputStream.write(126);
                    outputStream.write((length >> 8) & 0xFF);
                    outputStream.write(length & 0xFF);
                } else {
                    outputStream.write(127);
                    long value = length;
                    for (int i = 7; i >= 0; i--) {
                        outputStream.write((int) ((value >> (8 * i)) & 0xFF));
                    }
                }
                outputStream.write(payload);
                outputStream.flush();
                return true;
            } catch (IOException exception) {
                closed = true;
                return false;
            }
        }

        synchronized boolean isClosed() {
            return closed;
        }

        synchronized void closeQuietly() {
            if (closed) {
                return;
            }
            closed = true;
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
