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
					accept_encoding = "",accept_language = "",method = "",input = "";
			System.out.println(this.getId());
			while(!(inputLine = reader.readLine()).equals("")){
				System.out.println(inputLine);
				input += inputLine;
				if(inputLine.startsWith("GET /")) {
					query += inputLine;
					query = query.replace("GET ", "");
					query = query.replace(" HTTP/1.1", "");
					method = "GET";
				}
				if(inputLine.startsWith("Host:")) {
					host += inputLine;
					host = host.replace("Host: ", "");
				}
				if(inputLine.startsWith("Connection:")) {
					connection += inputLine;
					connection = connection.replace("Connection: ", "");
				}
				if(inputLine.startsWith("Cache_Control:")) {
					cachecontrol += inputLine;
					cachecontrol = cachecontrol.replace("Cache_Control: ", "");
				}
				if(inputLine.startsWith("Accept:")) {
					accept += inputLine;
					accept = accept.replace("Accept: ", "");
				}
				if(inputLine.startsWith("Upgrade-Insecure-Requests: ")) {
					upgrade_insecure_requests += inputLine;
					upgrade_insecure_requests = upgrade_insecure_requests.replace(
							"Upgrade-Insecure-Requests:", "");
				}
				if(inputLine.startsWith("User-Agent:")) {
					user_agent += inputLine;
					user_agent = user_agent.replace("User-Agent: ", "");
				}
				if(inputLine.startsWith("Accept-Encoding:")) {
					accept_encoding += inputLine;
					accept_encoding = accept_encoding.replace("Accept-Encoding: ", "");
				}
				if(inputLine.startsWith("Accept-Language:")) {
					accept_language += inputLine;
				}
			}
			MockRequest request = new MockRequest(
					socket, query, input, host, connection, cachecontrol, 
					accept, upgrade_insecure_requests, user_agent, accept_encoding, 
					accept_language, method);
			if(query.contains("favicon")) return;
			int end = query.indexOf(" ");
			//query = query.substring(0, end);
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
