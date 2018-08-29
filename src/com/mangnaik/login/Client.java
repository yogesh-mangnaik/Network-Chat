package com.mangnaik.login;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int port;
	private JTextField txtMessage;
	private JTextArea txtrHistory;
	
	private DatagramSocket socket;
	private InetAddress ip;
	private Thread send;
	private Thread run ,listen;
	private int ID = -1;
	private boolean running = false;
	
	public Client(String name,String address, int port){
		this.name = name;
		this.port = port;
		boolean connect = openConnection(address);
		if(!connect){
			System.err.println("Connection failed!!!");
			console("Connection failed!!!");
		}	
		createWindow();
		console("Attempting connection to " + address + " : " + port + " user : " + name);
		String connection = "/c/" + name + "/e/" + "/pn/" + "179333786" + "/pn/";
		send(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();
	}
	
	private void createWindow(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Client Side");
		setLocationRelativeTo(null);
		setBounds(100, 100, 993, 607);
		setSize(880,550);
		setMinimumSize(new Dimension(880,550));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{28, 815,30 ,7};
		gridBagLayout.rowHeights = new int[]{35, 450,65};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		txtrHistory = new JTextArea();
		txtrHistory.setEditable(false);
		JScrollPane scroll = new JScrollPane(txtrHistory);
		
		txtrHistory.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 21));
		txtrHistory.setText("");
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.insets = new Insets(20,8,0,0);
		getContentPane().add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					String message;
					message = txtMessage.getText();
					if(!message.equals("")){
						send(message);
						txtMessage.requestFocusInWindow();
					}
				}
			}
		});
		txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));
		txtMessage.setText("");
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.insets = new Insets(0,5,5,0);
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		getContentPane().add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 5, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		getContentPane().add(btnSend, gbc_btnSend);
		btnSend.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String message;
				message = txtMessage.getText();
				if(!message.equals("")){
					send(message);
					txtMessage.requestFocusInWindow();
				}
			}
		});
		
		setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	
	public void send(final byte[] data){
		send = new Thread("send"){
			public void run(){
				DatagramPacket packet = new DatagramPacket(data,data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	public String recieve(){
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data,data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		process(message);
		return message;
	}
	
	public void run(){
		listen();
	}
	
	public void process(String message){
		if(message.startsWith("/m")){
			console(message.split("/m/|/e/")[1]);
		}
		if(message.startsWith("/p/")){
			String string = message.split("/p/")[1];
			String name = message.split("/dn/")[1];
			console(name + " : " + string);
		}
		if(message.contains("/d/")){
			console("You were kicked from server");
		}
	}
	
	
	public boolean openConnection(String address){
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void listen(){
		listen = new Thread("Listen"){
			public void run(){
				while(running){
					String message = recieve();
					if(message.startsWith("/c/")){
						ID = Integer.parseInt(message.split("/c/|/e/")[1]);
						console("Succesfully connected to the server!!! || id : " + ID );
					}
				}
			}
		};
		listen.start();
	}
	
	public void console(String message){
		txtrHistory.append(message + "\n\r");
		txtMessage.setText("");
	}
	
	public void send(String message){
		message = "/m/" + name + " : " + message + "/e/" + ID + "/i/";
		send(message.getBytes());
	}
}
