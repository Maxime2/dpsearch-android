package com.dataparksearch;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Random;

import android.util.Log;

public class TCPClient { // implements Runnable {
	
    /**
     * Shared buffer used by {@link #getUrlContent(String)} when reading results
     * from an API request.
     */
    private static byte[] sBuffer = new byte[512];
    /**
     * Thrown when there were problems parsing the response to an API call,
     * either because the response was empty, or it was malformed.
     */
    
    public String message = null; 
    public String hostname = null; 
    
    public String run() {
         try {
        	 
        	// InetAddress serverAddr = InetAddress.getByName("78.46.20.199");//TCPServer.SERVERIP
        	 InetAddress serverAddr = InetAddress.getByName(hostname);
        	 
        	 Log.d("TCP", "C: Connecting...");
        	 Socket socket = new Socket(serverAddr, TCPDesktopServer.SERVERPORT);
		     try {
		    	 Log.d("TCP", "C: Sending: '" + message + "'");
		    	 PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
		    	 
		    	 final Random myRandom = new Random();
		    	 String encquery = URLEncoder.encode(message, "utf-8");
		    	 out.println("GET q=" + encquery + "&"+ String.valueOf(myRandom.nextInt()) +"&sp=0&s=IRPD&tmplt=strings.htm\0");
		    	 Log.d("TCP", "C: Sent. '" + message + "'   '" + encquery + "'");
		    	 
		    	// PrintReader in = new PrintReader( new BufferedReader( new InputStreamReader(socket.getInputStream())),true);
		    	 InputStream inputStream = socket.getInputStream();
		    	 
		            ByteArrayOutputStream content = new ByteArrayOutputStream();

		            // Read response into a buffered stream
		            int readBytes = 0;
		            while ((readBytes = inputStream.read(sBuffer)) != -1) {
		                content.write(sBuffer, 0, readBytes);
		            }

		            // Return result from buffered stream
		            String SERP =  new String(content.toByteArray());
		    	 
		         Log.d("SERP", SERP);
		         
		         return SERP;
		    	 
             } catch(Exception e) {
                 Log.e("TCP", "S: Error", e);
		      } finally {
		        socket.close();
		      }
         } catch (Exception e) {
              Log.e("TCP", "C: Error", e);
         }
         
         return new String("eof");
    }
}
