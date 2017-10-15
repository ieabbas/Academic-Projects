/* CS 380 - Computer Networks
 * Assignment 1 : Server/Client
 * Ismail Abbas
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * This class will represent the server and the corresponding interactions it will have with the client
 *
 */
public final class EchoServer {

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(22222)) {
            while (true) {
                try  {
                    Socket socket = serverSocket.accept();
                    // Create new thread to handle different clients
                    Thread clientThread = new Thread(new SecondThread(socket));
                    clientThread.start();
                } catch (Exception e) {}
            }
        }
    }

    // The class second thread will handle the additional thread for various clients
    static final class SecondThread implements Runnable {
        
        private Socket socket;
        private String address;

        // Will set the socket and the adress to be used
        public SecondThread(Socket socket) {
            this.socket = socket;
            address = socket.getInetAddress().getHostAddress();
        }

        public void run() {
            System.out.printf("Client connected: %s%n", address);
            try {
                // The user can now submit messages to their heart's content
                InputStream inStr = socket.getInputStream();
                InputStreamReader inStrRead = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String message = br.readLine();
				// While the message isn't null, become a copycat and say it back
                while(message != null) {
                    OutputStream outStr = socket.getOutputStream();
                    PrintStream outPrint = new PrintStream(outStr, true, "UTF-8");
                    String serverSend = "Server> " + message;
                    outPrint.println(serverSend);
                    message = br.readLine();                    
                }
				
				// End the conversation
                System.out.printf("Client disconnected: %s%n", address); // User exited, print that the client disconnected
                socket.close();
            } catch (Exception e) {}
        }
    }
    
}
