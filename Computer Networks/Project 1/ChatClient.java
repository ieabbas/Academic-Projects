/* CS 380 - Computer Networks
 * Project 1 : Chat Client
 * Ismail Abbas
 */

import java.io.*;
import java.util.Scanner;
import java.net.*;

/*
 * This class will assist in the creation of a ChatClient, representing the client class
 */
public final class ChatClient {

	public static void main(String[] args) {
		// Never got the full hang of it but it seems to work
		try {
			// Make a server connection with the specified port
			// The thread will handle the client messages
			// InetAddress address = "18.221.102.182";
			Socket socket = new Socket("18.221.102.182", 38001);
			Thread senderThread = new Thread(new SecondThread(socket));
			senderThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This class will represent the secondary thread that is designed to receive messages from the server specified in the instructions
	 */
	static class SecondThread implements Runnable {
		
		// Remember, protected = visible from same package
		protected Socket socket;
		protected String userName;

		/*
		 * This is the default constructor, left blank purposely
		 */
		public SecondThread() {
		}

		/*
		 * This constructor is the alternate constructor, taking in a socket argument
		 */
		public SecondThread(Socket s) {
			socket = s;
			userName = null;
		}
		
		/*
		 * This method will parse user input and send the input to the server
		 */
		public void sendMessage() {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			String message = null;
			try {
				OutputStream outStr = socket.getOutputStream();
				PrintStream outPrintStr = new PrintStream(outStr, true, "UTF-8");
				message = buffer.readLine();
				// If "exit", ditch the program and exits
				if (message.equals("exit")) {
					socket.close();
					return;
				}
				outPrintStr.println(message);
			} catch (Exception e) {
			}
		}

		/*
		 * This method will run the thread to receive messages from the server
		 */
		public void run() {
			Thread receiver = new Thread(new ServerThread(socket));
			receiver.start();

			try {
				OutputStream outStr = socket.getOutputStream();
				InputStream is = socket.getInputStream();
				// Checks name && if valid
				sendUserName();

				// If the connection works, sleep to let ServerReceiver thread has a chance to check the name
				if (socket.isConnected()) {
					// Connection was successful,
					// wait for a period of time so that ServerReciever thread
					// has a chance to check validity of user name
					Thread.sleep(300);
					System.out.println("--- YOU ARE NOW CONNECTED ---\n");
					int count = 0;
					// While the socket is not closed, tell the user you're ready for input
					while (!socket.isClosed()) {
						if (count == 0) {
							System.out.println("-Ready for input-");
							++count;
						}
						sendMessage();
					}
					System.out.println("\n--- DISCONNECTED ---\n");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*
		 *This method will send the attempted user name to the server to see if it has already been occupied
		 */
		public void sendUserName() {
			System.out.print("Username: ");
			String uName = null;
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			try {
				OutputStream outStr = socket.getOutputStream();
				PrintStream outPrintStr = new PrintStream(outStr, true, "UTF-8");
				uName = buffer.readLine();
				this.userName = uName;
				outPrintStr.println(userName);
				System.out.println();
			} catch (IOException e) {
			}
		}
	}
}
