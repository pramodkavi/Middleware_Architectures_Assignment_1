import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Client <serverIP> <port> <clientType>");
            return;
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        String clientType = args[2];

        try {
            Socket socket = new Socket(serverIP, port);
            System.out.println("Connected to server: " + serverIP + ":" + port);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the client type to the server
            writer.println(clientType.toUpperCase());

            if (clientType.equalsIgnoreCase("PUBLISHER")) {
                BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while ((line = keyboardReader.readLine()) != null) {
                    writer.println(line);
                    if (line.equalsIgnoreCase("terminate")) {
                        break;
                    }
                }
            } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("terminate")) {
                        break;
                    }
                    System.out.println("Received from server: " + message);
                }
            }

            System.out.println("Disconnected from server.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
