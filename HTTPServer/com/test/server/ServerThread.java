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
		String urlHeader = "Referer: http://localhost:" + portNumber + "/";
		System.err.println("Listening");
		try (PrintWriter out = new PrintWriter(socket.getOutputStream());
			 BufferedReader reader = new BufferedReader(
					 new InputStreamReader(socket.getInputStream()));) {
			String inputLine;
			while(!(inputLine = reader.readLine()).equals("")){
				if(inputLine.startsWith("Referer:")){
					break;
				}
			}
			inputLine = inputLine.replace(urlHeader, "");
			System.out.println(inputLine);
			File htmlFile = null;
			if(inputLine.isEmpty()) {
				htmlFile = new File("index.html");
				System.out.println("1");
			}
			else{
				htmlFile = new File(inputLine);
			}
			if(htmlFile.isDirectory() || !htmlFile.exists()){
				System.out.println("2");
				inputLine = inputLine  + "/index.html";
				inputLine.replace("//", "/");
				System.err.println(inputLine);
				htmlFile = null;
				htmlFile = new File(inputLine);
			}
			System.out.println(htmlFile.getAbsolutePath());
			FileReader fileReader = new FileReader(htmlFile);
			BufferedReader getfile = new BufferedReader(fileReader);
		    String file;
		    while((file=getfile.readLine()) != null){
		    	out.println(file);
		    }
		    out.flush();
		    getfile.close();
		    socket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
