import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 4) {
            System.out.println("Usage: java Client <serverIP> <port> <clientType> <topic>");
            return;
        }

        // Parse command-line arguments
        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        String clientType = args[2];
        String topic = args[3];

        try {
            // Establish a connection to the server using the specified server IP and port
            Socket socket = new Socket(serverIP, port);
            System.out.println("Connected to server: " + serverIP + ":" + port);

            // Initialize input and output streams for communication with the server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the client type and topic to the server
            writer.println(clientType.toUpperCase());
            writer.println(topic);

            if (clientType.equalsIgnoreCase("PUBLISHER")) {
                // Client is a PUBLISHER
                BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
                String line;
                // Read input from the user and send it to the server as messages
                while ((line = keyboardReader.readLine()) != null) {
                    writer.println(line);
                    if (line.equalsIgnoreCase("terminate")) {
                        // If the user enters "terminate," stop sending messages
                        break;
                    }
                }
            } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                // Client is a SUBSCRIBER
                // Receive messages from the server and display them
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        // If the server sends "terminate," stop receiving messages
                        break;
                    }
                    System.out.println("Received from server: " + message);
                }
            }

            // Client has finished its task, close the socket and disconnect from the server
            System.out.println("Disconnected from server.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
