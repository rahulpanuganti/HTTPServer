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
			File htmlFile = null;
				if(query.contains(".")){
					htmlFile = new File(query);
					System.out.println("Has dot");
				}
				else {
					System.out.println("2");
					query = query  + "/index.html";
					if(query.startsWith("/")) {
						query = query.substring(1);
					}
					query = query.replace("//", "/");
					System.err.println(query);
					htmlFile = null;
					htmlFile = new File(query);
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
