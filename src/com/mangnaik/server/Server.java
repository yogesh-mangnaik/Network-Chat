package com.mangnaik.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
	
public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private int port;
	private DatagramSocket socket;
	private boolean running = false;
	private Thread run,receive,send,server;
	private Scanner scan = new Scanner(System.in);

	public Server(int port){
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {				
			e.printStackTrace();
				return;
		}
		run = new Thread(this,"server");
		run.start();
	}
	
	@Override
	public void run() {
		running = true;
		System.out.println("Server started on port : " + port);
		receive();
		manageServer();
	}
	
	//manage server and the clients
	private void manageServer(){
		server = new Thread("Server Manager"){
			public void run(){
				String command;
				while(running){
					System.out.println("Enter 1 to kick with name\nEnter 2 to close the server\nEnter 3 to kick client with id\n"
							+ "Enter 4 to send message to all the clients\nEnter 5 to remove all the clients/nEnter 6 to get all Clients");
					int choice = scan.nextInt();
					
					try{
						if(choice == 1){
							command = scan.next();
							for(int i=0;i<clients.size();i++){
								if(command.equals(clients.get(i).name)){
									
									String message = "You were kicked from server!!!!!!!!!";
									send(message.getBytes(),clients.get(i).address,clients.get(i).port);
									clients.remove(i);
									System.out.println(command + " kicked from server.");
								}
							}
						}
						else if (choice == 2){
							socket.close();
							running = false;
						}
						
						else if (choice == 3){
							try{
								int id = scan.nextInt();
								for(int i=0;i<clients.size();i++){
									if(id == clients.get(i).getID()){
										String message = "You were kicked from server!!!!!!";
										send(message,clients.get(i).address,clients.get(i).port);
										System.out.println(clients.get(i).name + " kicked from server.");
										clients.remove(i);
									}
								}
							}
							catch(InputMismatchException e){
								e.printStackTrace();
							}					
						}
						
						else if (choice == 4){
							try{
								String message = scan.next();
								sendToAll("Server : " + message);
							}
							catch(InputMismatchException e){
								e.printStackTrace();
								System.out.println("Failed to send the data.");
							}
						}
						
						else if (choice == 5){
							clients.clear();
							System.out.println("Removed all the clients!!!!");
						}
						
						else if (choice == 6){
							for(int i = 0; i<clients.size(); i++){
								System.out.println(clients.get(i).name);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		};
		server.start();
	}
	
	
	//send the data
	private void sendToAll(String message){
		for(int i=0;i<clients.size();i++){
			ServerClient client = clients.get(i);
			send(message,client.address,client.port);
		}
	}
	
	private void sendToAll(byte[] data){
		for(int i=0;i<clients.size();i++){
			ServerClient client = clients.get(i);
			send(data,client.address,client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port){
		send = new Thread(){
			public void run(){
				DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message, InetAddress address, int port){
		message = "/m/" + message + "/e/";
 		send(message.getBytes(),address,port);
	}
	
	//receive the data
	public void receive(){
		receive = new Thread("Receive"){
			public void run(){
				while(running){	
					byte[] data = new byte[4096];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
						System.out.println("Packet received");
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}
	//process the received data
	private void process(DatagramPacket packet){
		String string = new String(packet.getData());
		System.out.println("Received String = " + string);
		int clientId = 0;
		int clientIndex = 0;
		boolean isClient = false;
		
		if (string.contains("/q/")){
			System.out.println("Request for clients");
			String clientNumber = string.split("/pn/")[1];
			String clientListMessage = "";
			String clientNumbers = "";
			for(int i=0;i<clients.size()-1;i++){
				if(!clients.get(i).number.equals(clientNumber)){
					clientListMessage += "/q/" + clients.get(i).name;
					clientNumbers += "/w/" + clients.get(i).number;
					System.out.println(clients.get(i).name);
				}
			}
			if(clients.size() != 0 ){
				if(!clients.get(clients.size()-1).number.equals(clientNumber)){
					clientListMessage += "/q/" + clients.get(clients.size()-1).name + "/e/";
					clientNumbers += "/w/" + clients.get(clients.size()-1).number + "/e/";
				}
				else{
					clientListMessage += "/e/";
					clientNumbers += "/e/";
				}
			}
			else{
				clientListMessage = "/e/";
				clientNumbers = "/e/";
			}
			
			System.out.println(clientListMessage);
			send((clientListMessage + clientNumbers).getBytes(),packet.getAddress(),packet.getPort());
		}	
		
		//new connection
		if(string.startsWith("/c/")){
			String clientName, ID;
			String clientNumber;
			int id;
			boolean isAlreadyClient = false;
			int position = 0; 
			clientNumber = string.split("/pn/")[1];
			System.out.println(clientNumber);
			for(int i=0; i<clients.size(); i++){
				if(clients.get(i).number.equals(clientNumber)){
					isAlreadyClient = true;
					position = i;
					break;
				}
			}
			if(isAlreadyClient){
				ID = Integer.toString(clients.get(position).getID());
				clientName = string.split("/c/|/e/")[1];
				System.out.println("Existing Client");
				System.out.println(string.split("/c/|/e/")[1] + " connected on port "  + packet.getPort() + " with ID " + ID);
				clients.get(position).name = clientName;
				clients.get(position).address = packet.getAddress();
				clients.get(position).port = packet.getPort();
				String connectionMessage = "/c/" + ID + "/e/";
				send(connectionMessage.getBytes(),packet.getAddress(),packet.getPort());
				System.out.println("ID Sent");
			}
			else{
				clientName = "/n/" + string.split("/c/|/e/")[1] + "/n/" + "/pn/" + clientNumber + "/pn/";
				sendToAll(clientName.getBytes());
				id = UniqueIdentifiers.getIdentifier();
				ID = Integer.toString(id);
				clients.add(new ServerClient(string.split("/c/|/e/")[1],packet.getAddress(),packet.getPort(),id,clientNumber));
				System.out.println("New Client Connected");
				System.out.println("Client Address : " + packet.getAddress());
				System.out.println(string.split("/c/|/e/")[1] + " connected on port "  + packet.getPort() + " with ID " + ID);
				String connectionMessage = "/c/" + ID + "/e/";
				send(connectionMessage.getBytes(),packet.getAddress(),packet.getPort());
			}
		}
		
		else if(!string.contains("/q/") && !string.contains("/p/")){
			clientId = Integer.parseInt(string.split("/e/|/i/")[1]);
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).getID() == clientId){
					isClient = true;
					clientIndex = i;
					break;
				}
			}
		}
		
		if(string.startsWith("/m/")&&isClient){
			String message = string.split("/m/|/e/")[1];
			sendToAll(message);
			System.out.println(message);
		}
		
		else if(string.startsWith("/d/")&&isClient){
			String message = clients.get(clientIndex).name + "disconnected from server.";
			sendToAll(message);
			System.out.println(message);
		}
		
		else if(string.startsWith("/p/")){
			String message = "/p/" + string.split("/p/")[1] + "/p/";
			System.out.println("Message : " + message.split("/p/")[1]);
			String number = string.split("/dn/")[1];
			System.out.println(number);
			boolean isDestinationValid = false;
			int position = 0;
			int position2 = 0;
			for(int i=0; i<clients.size();i++){
				if(clients.get(i).number.equals(number)){
					position = i;
					isDestinationValid = true;
				}
				if(clients.get(i).getID() == Integer.parseInt(string.split("/i/")[1])){
					position2 = i;
				}
			}
			
			if(isDestinationValid){
				message = message + "/dn/" + clients.get(position2).number + "/dn/";
				System.out.println("Sending message to " + clients.get(position).name);
				send(message.getBytes(),clients.get(position).address,clients.get(position).port);
			}
			else{
				System.out.println("Destination client does not exist");
			}
		}
	}
}
