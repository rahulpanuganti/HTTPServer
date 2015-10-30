package com.test.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
			String query = "", inputLine = "",host = "",connection = "",cachecontrol = "",
					accept = "",upgrade_insecure_requests = "",user_agent = "",
					accept_encoding = "",accept_language = "";
			ArrayList<String> input= new ArrayList<String>();
			System.out.println(this.getId());
			while(!(inputLine = reader.readLine()).equals("")){
				System.out.println(inputLine);
				if(inputLine.startsWith("GET /")) {
					query += inputLine;
				}
				if(inputLine.startsWith("Host:")) {
					host += inputLine;
				}
				if(inputLine.startsWith("Connection:")) {
					connection += inputLine;
				}
				if(inputLine.startsWith("Cache_Control:")) {
					cachecontrol += inputLine;
				}
				if(inputLine.startsWith("Accept:")) {
					accept += inputLine;
				}
				if(inputLine.startsWith("Upgrade-Insecure-Requests:")) {
					upgrade_insecure_requests += inputLine;
				}
				if(inputLine.startsWith("User-Agent:")) {
					user_agent += inputLine;
				}
				if(inputLine.startsWith("Accept-Encoding:")) {
					accept_encoding += inputLine;
				}
				if(inputLine.startsWith("Accept-Language:")) {
					accept_language += inputLine;
				}
			}
			input.add(query);
			input.add(host);
			input.add(connection);
			input.add(cachecontrol);
			input.add(accept);
			input.add(upgrade_insecure_requests);
			input.add(user_agent);
			input.add(accept_encoding);
			input.add(accept_language);
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
			FileInputStream fileInputStream;
		    if (outputFile.exists()) {
				fileInputStream = new FileInputStream(outputFile);
				byte[] file = new byte[(int) outputFile.length()];
				if (outputFile.exists()) {
					fileInputStream.read(file);
					byte[] response = "HTTP/1.1 200 OK\r\n Content-Type: text/html\r\n\r\n".getBytes();
					out.write(response);
					out.write(file);
				}
				fileInputStream.close();
			}
		    else {
		    	out.write("HTTP/1.1 404 Notfound\r\n".getBytes());
		    }
			out.flush();
		    
		    socket.close();
		    out.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
