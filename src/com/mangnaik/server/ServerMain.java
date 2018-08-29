package com.mangnaik.server;

public class ServerMain {
	public ServerMain(int port){
		System.out.println("Port : " + port);
		new Server(port);
	}

	public static void main(String[] args){
		int port;
		if(args.length!= 1){
			System.out.println("Usage : java - jar NetworkChatServer.jar [port]");
			System.out.println("Please try again");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
}
