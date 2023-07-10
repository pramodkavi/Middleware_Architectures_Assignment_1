import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <serverIP> <port>");
            return;
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(serverIP, port);
            System.out.println("Connected to server: " + serverIP + ":" + port);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);

                if (line.equalsIgnoreCase("terminate")) {
                    break;
                }
            }

            System.out.println("Disconnected from server.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
