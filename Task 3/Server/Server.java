import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static List<ClientHandler> clientHandlers = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new client handler thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void broadcastMessage(String message, String topic, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender && clientHandler.getTopic().equals(topic)) {
                clientHandler.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientType;
    private String topic;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            clientType = reader.readLine(); // Read the client type (PUBLISHER or SUBSCRIBER)
            topic = reader.readLine(); // Read the topic
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public void run() {
        try {
            if (clientType.equalsIgnoreCase("PUBLISHER")) {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        break;
                    }
                    Server.broadcastMessage(message, topic, this);
                    System.out.println("Client : " + topic + " - " + message);

                }
            } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        break;
                    }
                    System.out.println("Received from publisher: " + message);
                }
            }

            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            Server.removeClient(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
