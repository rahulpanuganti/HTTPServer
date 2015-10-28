package com.test.server;

import java.io.IOException;
import java.net.ServerSocket;

public class HTMLServer {
	public static void main(String args[]){
		
	        int portNumber = 8080;
	        boolean listening = true;
	        
	        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	            while (listening) {
		            new ServerThread(serverSocket.accept(), portNumber).start();
		        }
		    } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
	        }
	}
}
