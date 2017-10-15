/* CS 380 - Computer Networks
 * Assignment 1 : Server/Client
 * Ismail Abbas
 */

import java.io.*;
import java.util.Scanner;
import java.net.*;

/*
 * This class will represent the client in a client/server interaction 
 */
public final class EchoClient {

	private static Scanner kb;

    public static void main(String[] args) throws Exception {
        boolean initialRun;
        String exitStr = ""; 
        initialRun = true;      
        kb = new Scanner(System.in);
        
        	try (Socket s = new Socket("localhost", 22222)) {
				 // Message is to be sent, remember that out = write out
				 // Sockets are the way java programs let a device communicate with a server that is listening
				 // "exit" leaves the program
                 while(!exitStr.equals("exit")) {
                    String address = s.getInetAddress().getHostAddress();
                    OutputStream outStr = s.getOutputStream();
                    PrintStream outPrint = new PrintStream(outStr, true, "UTF-8");
                    System.out.print("Client> ");
                    exitStr = kb.nextLine();
                    outPrint.println(exitStr); 

                    // Message is to be received, remember that in = receive (kinda backwards but it works)
                    InputStream isStr = s.getInputStream();
                    InputStreamReader isStrRead = new InputStreamReader(isStr, "UTF-8");
                    BufferedReader br = new BufferedReader(isStrRead);
                    String serverMessage = br.readLine();
                    // if message from server is not null, print da message out
                    if(serverMessage != null && !serverMessage.equals("Server> exit")) {
                        System.out.println(serverMessage);
					}
                }
        	}
    }
}

















