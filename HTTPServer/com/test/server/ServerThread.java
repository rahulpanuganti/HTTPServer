package com.test.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
	
	Socket socket;
	int portNumber;
	public ServerThread(Socket clientSocket,int portNumber) {
		socket = clientSocket;
		this.portNumber = portNumber;
	}
	
	@Override
	public void run(){
		String urlHeader = "GET /";
		System.err.println("Listening");
		try {
			OutputStream out = socket.getOutputStream();
			 BufferedReader reader = new BufferedReader(
					 new InputStreamReader(socket.getInputStream()));
			String query = "", inputLine = "";
			System.out.println(this.getId());
			while(!(inputLine = reader.readLine()).equals("")){
				System.out.println(inputLine);
				if(inputLine.startsWith(urlHeader)) {
					query = new String(inputLine);
					break;
				}
			}
			if(query.contains("favicon")) return;
			query = query.replace(urlHeader, "");
			int end = query.indexOf(" ");
			query = query.substring(0, end);
			System.out.println(query);
			File outputFile = null;
				if(query.contains(".")){
					outputFile = new File(query);
				}
				else {
					System.out.println("2");
					query = query  + "/index.html";
					if(query.startsWith("/")) {
						query = query.substring(1);
					}
					query = query.replace("//", "/");
					System.err.println(query);
					outputFile = null;
					outputFile = new File(query);
				}
			System.out.println(outputFile.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(outputFile);
		    byte[] file = new byte[(int)outputFile.length()];
		    fileInputStream.read(file);
		    	out.write(file);
		    out.flush();
		    fileInputStream.close();
		    socket.close();
		    out.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
