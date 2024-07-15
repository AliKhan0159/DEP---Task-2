package chat_application;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerChatDb {

    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, PrintWriter> userWriters = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The Chat Server is Connected and Running.....");
        ServerSocket listener = new ServerSocket(59065);
        try {
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("A New Client is Connected: " + clientSocket.getInetAddress());
                new Handler(clientSocket).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

  
                out.println("SUBMITNAME");
                username = in.readLine();
                synchronized (userWriters) {
                    userWriters.put(username, out);
                }

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

              
                broadcast(username + " has joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                synchronized (userWriters) {
                    userWriters.remove(username);
                }
                broadcast(username + " has Left the Chat!");
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
