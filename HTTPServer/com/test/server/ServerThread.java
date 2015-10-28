package com.test.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		try (
			 BufferedReader reader = new BufferedReader(
					 new InputStreamReader(socket.getInputStream()));) {
			String query, inputLine = "";
			System.out.println(this.getId());
			while(!(query = reader.readLine()).equals("")){
				System.out.println(query);
				if(query.startsWith(urlHeader)) {
					inputLine = new String(query);
					break;
				}
			}
			if(inputLine.contains("favicon")) return;
			inputLine = inputLine.replace(urlHeader, "");
			int end = inputLine.indexOf(" ");
			inputLine = inputLine.substring(0, end);
			System.out.println(inputLine);
			File htmlFile = null;
				if(inputLine.contains(".")){
					htmlFile = new File(inputLine);
					System.out.println("Has dot");
				}
				else {
					System.out.println("2");
					inputLine = inputLine  + "/index.html";
					if(inputLine.startsWith("/")) {
						inputLine = inputLine.substring(1);
					}
					inputLine = inputLine.replace("//", "/");
					System.err.println(inputLine);
					htmlFile = null;
					htmlFile = new File(inputLine);
				}
				PrintWriter out = new PrintWriter(socket.getOutputStream());
			System.out.println(htmlFile.getAbsolutePath());
			FileReader fileReader = new FileReader(htmlFile);
			BufferedReader getfile = new BufferedReader(fileReader);
		    String file;
		    while((file=getfile.readLine()) != null){
		    	out.print(file);
		    }
		    out.flush();
		    getfile.close();
		    socket.close();
		    out.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
