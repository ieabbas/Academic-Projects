/* CS 380 - Computer Networks
 * Exercise 2 : CRC Client Checking
 * Ismail Abbas
 */

 import java.util.*;
 import java.io.*;
 import java.util.zip.CRC32;
 import java.net.*;
 import java.nio.*;
 
 /*
  * This class will connect to a socket on a specified server. It will then
  * receive 100 bytes at half a byte each. Then it will reconstruct the message
  * , and generate the CRC code as a sequence of four bytes and send it back to
  * the server.
  */
 public class Ex2Client {
	 
	/*
	 * This is...the main method which performs the general function of the
	 * class.
	 */
	 public static void main(String[]args) {
		  
		// Like everything that isn't guaranteed in life, you have to try
		// before giving up
		try {
            Socket s = new Socket("18.221.102.182"	,38102);

            System.out.println("\nConnected to Server.");
            System.out.println("Recieved bytes: \n");
            long crc = createCRC( getBytes(s) );
            sendCRC(s, crc);
			// If the crcCheck returns true, tell user response was good
            String validity = (crcCheck(s)) ? "\nResponse good." : "\nInvalid response.";
            System.out.println(validity);

            s.close();
            System.out.println("Disconneced from server.");
        } catch (Exception e) { e.printStackTrace(); }
    }
	  
	/*
	 * This method will receive the bytes from the server and print them
	 * after the've been shifted correctly
	 */
    public static byte[] getBytes(Socket s) {
        try {
            
            InputStream isStr = s.getInputStream();
            int count = 0;
            byte[] message = new byte[100];
            for(int k = 0; k < 100; ++k) {
				// If the count hits 10, clear the line and start again
                if(count == 10) {
                    System.out.println();
                    count = 0;
                }

                // Shifting bytes to have proper message
                int originalFirstHalf = isStr.read();
                int firstHalf = 16 * originalFirstHalf;
                int secondHalf = isStr.read();
                firstHalf += secondHalf;
                message[k] = (byte) firstHalf;
				
                // Print the bytes received as hexadecimal
                String firstH = Integer.toHexString(originalFirstHalf).substring(0,1).toUpperCase();
                String secondH = Integer.toHexString(secondHalf).substring(0,1).toUpperCase();

                System.out.print(firstH + secondH);
                ++count;
            }
            return message;
        } catch (Exception e) {}
        return null;
    }
	  
	/*
	 * This method will read the response from the server to test
	 * if the generated CRC code was correct
	 */
    public static boolean crcCheck(Socket s) {
        try {
            InputStream isStr = s.getInputStream();
            int successCode = isStr.read();
			// "If the boolean equals 1, return true for the method, else false"
            return (successCode == 1) ? true : false;
        } catch (Exception e) { }
		// This is just to satisfy Java coding conventions for methods
        return false;
    }

	/*
	 * This method will cast the CRC as an integer, as it is 4 bytes (Nima)
	 * and then send the CRC as a byte array
	 */
    public static void sendCRC(Socket s, long crc) {
        try {
            OutputStream outStr = s.getOutputStream();
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt( (int) crc);
            byte[] asArray = b.array();
            outStr.write(asArray);
            
        } catch (Exception e) {}
    }
	
	/*
	 * This method will create the CRC32 object to generate the CRC code
	 */
    public static long createCRC(byte[] b) {
		long crc;
        CRC32 newCode = new CRC32();
        newCode.reset();
        newCode.update(b, 0, 100);
        crc = newCode.getValue();
        System.out.println("\nGenerated CRC32: " + Long.toHexString(crc).toUpperCase() + ".");
        return crc;
    }
	
 }