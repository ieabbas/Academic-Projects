/* CS 380 - Computer Networks
 * Project 1 : Chat Client
 * Ismail Abbas
 */

import java.io.*;
import java.net.*;

/*
 * This class will run as a separate thread that will wait for messages from the server and print them out
 */
public class ServerThread implements  Runnable {
	
    // Chat client connects to socket
    private Socket socket;

	/*
	 * This is the default constructor, accepting a socket as a parameter
	 */
    public ServerThread(Socket s) {
        socket = s;
    }

	/*
	 * This method will actually execute the thread
	 */
    public void run() {
            try {
				String messageFromServer = "";
				// Pull stream from server to parse messages left by user
                InputStream isStr = socket.getInputStream();
                InputStreamReader isStrRead = new InputStreamReader(isStr, "UTF-8");
                BufferedReader br = new BufferedReader(isStrRead);
                // While there are still messages, keep accepting them
                while((messageFromServer = br.readLine()) != null) {
					// If the name is taken for the user, tell the user and exit the program
                    if(messageFromServer.equals("Name in use.")) {
                        System.out.println("--- User name occupied, closing connection ---\n");
                        socket.close();
                        System.exit(0);
                    } else if(messageFromServer.equals("Connection timeout imminent, closing connection.")) {
                        System.out.println("\n" + messageFromServer + "\n");
                        socket.close();
                        System.exit(0);
                    } else  {
                        System.out.println(messageFromServer);
                    }
                }
            } catch (Exception e) { }
    }

}
