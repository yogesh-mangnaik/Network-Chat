package com.mangnaik.server;

import java.net.InetAddress;

public class ServerClient {
	public String name;
	public int port;
	public InetAddress address;
	private final int ID;
	public int attempt = 0;
	public String number;
	
	public ServerClient(String name, InetAddress address, int port, final int ID, String number){
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;
		this.number = number;
	}
	
	public int getID(){
		return ID;
	}
}
