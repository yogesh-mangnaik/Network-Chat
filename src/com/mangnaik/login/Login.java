package com.mangnaik.login;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;
	 	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JLabel lblPort;
	private JTextField txtPort;

	public Login() {
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setBounds(100, 100, 473, 682);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setFont(new Font("Tahoma", Font.PLAIN, 24));
		txtName.setBounds(117, 126, 233, 35);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name : ");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblName.setBounds(190, 73, 86, 20);
		contentPane.add(lblName);
		
		JLabel lblAddress = new JLabel("IP Address : ");
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAddress.setBounds(164, 203, 138, 20);
		contentPane.add(lblAddress);
		
		txtAddress = new JTextField();
		txtAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtAddress.setFont(new Font("Tahoma", Font.PLAIN, 24));
		txtAddress.setBounds(117, 258, 233, 35);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblPort = new JLabel("Port : ");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPort.setBounds(199, 328, 69, 20);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setHorizontalAlignment(SwingConstants.CENTER);
		txtPort.setFont(new Font("Tahoma", Font.PLAIN, 24));
		txtPort.setBounds(117, 383, 233, 35);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 23));
		btnLogin.setBounds(164, 531, 138, 35);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String ipAddress = txtAddress.getText();
				int port = 0;
				try{
					port = Integer.parseInt(txtPort.getText());
				}catch(Exception t){
					t.printStackTrace();
				}
				login(name,ipAddress,port);
			}

			private void login(String name, String ipAddress, int port) {
				dispose();
				new Client(name,ipAddress, port);
			}
		});
		contentPane.add(btnLogin);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
