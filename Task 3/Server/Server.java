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
            // Create a server socket to listen for incoming client connections
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                // Wait for a client to connect and accept the connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new client handler to handle the client's communication
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);

                // Start a new thread for the client handler to handle communication with this
                // client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the server socket when done
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to broadcast a message to all clients subscribed to a particular topic
    public static void broadcastMessage(String message, String topic, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender && clientHandler.getTopic().equals(topic)) {
                // Send the message to all subscribers for the specified topic
                clientHandler.sendMessage(message);
            }
        }
    }

    // Method to remove a client handler when a client disconnects
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

// Class to handle communication with a single client
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientType;
    private String topic;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            // Initialize the input and output streams for communication with the client
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the client type (PUBLISHER or SUBSCRIBER) and the topic sent by the
            // client
            clientType = reader.readLine();
            topic = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get the topic of this client (PUBLISHER or SUBSCRIBER)
    public String getTopic() {
        return topic;
    }

    @Override
    public void run() {
        try {
            if (clientType.equalsIgnoreCase("PUBLISHER")) {
                // Handle communication with a publisher client
                while (true) {
                    // Read messages from the publisher and broadcast them to subscribers of the
                    // topic
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        // If the publisher sends "terminate," stop the communication loop
                        break;
                    }
                    Server.broadcastMessage(message, topic, this);
                    System.out.println("Client: " + topic + " - " + message);
                }
            } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                // Handle communication with a subscriber client
                while (true) {
                    // Read messages from the publisher and display them for the subscriber
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        // If the subscriber sends "terminate," stop the communication loop
                        break;
                    }
                    System.out.println("Received from publisher: " + message);
                }
            }

            // Client disconnected, cleanup and close the socket
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            Server.removeClient(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to this client
    public void sendMessage(String message) {
        writer.println(message);
    }
}
