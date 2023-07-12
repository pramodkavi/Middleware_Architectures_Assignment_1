import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // parameters which are given in the beginning are stored in the args array
    // if args has more than one parameter or has no paremeter then it doesn't procced the process
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        // Given parameter is taken as server port 
        int port = Integer.parseInt(args[0]);

        try {
            // serverSocket which is ServerSocket store a instance address is used to listen to incoming client connections which came through given port
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            // Wait for a client is connected to the server then accept client connections and retrun a new Socket object 
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress()); //prints the IP address of the connected client

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Client: " + line);

                if (line.equalsIgnoreCase("terminate")) {
                    break;
                }
            }

            System.out.println("Client disconnected.");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
