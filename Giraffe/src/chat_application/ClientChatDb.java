package chat_application;

import java.io.*;
import java.net.*;

public class ClientChatDb {

    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public ClientChatDb(String serverAddress) throws Exception {
        Socket mySocket = new Socket(serverAddress, 59065);
        in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        out = new PrintWriter(mySocket.getOutputStream(), true);

        new Thread(new IncomingReader()).start();

        while (true) {
            String userInput = stdIn.readLine();
            if (userInput != null && !userInput.trim().isEmpty()) {
                out.println(userInput);
            }
        }
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line.startsWith("SUBMITNAME")) {
                        System.out.print("Please Enter Your Username: ");
                        String username = stdIn.readLine();
                        out.println(username);
                    } else {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ClientChatDb myClient = new ClientChatDb("127.0.0.1");
    }
}
