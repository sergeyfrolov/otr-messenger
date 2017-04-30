package org.otrmessenger.viewer;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;

public class OTRMessenger {

	private JFrame frame;
	private JTextField UsernameField;
	private JPasswordField passwordField;
	private static Host curr;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OTRMessenger window = new OTRMessenger();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OTRMessenger() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("OTR Messenger");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(52, 61, 76, 16);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(52, 112, 81, 16);
		frame.getContentPane().add(lblPassword);
		
		UsernameField = new JTextField();
		UsernameField.setBounds(157, 50, 205, 32);
		frame.getContentPane().add(UsernameField);
		UsernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(157, 101, 205, 32);
		frame.getContentPane().add(passwordField);

        JLabel err = new JLabel("Couldn't log in");
        err.setBounds(180, 243, 505, 32);
        err.setForeground(Color.RED);
        err.setVisible(false);
        frame.getContentPane().add(err);
		
		JButton btnLogIn = new JButton("Log In");
		btnLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                MessageDigest messageDigest = null;
                try {
                    messageDigest = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                messageDigest.update(passwordField.getText().getBytes());
                String encryptedString = new String(messageDigest.digest());
			    curr = new Host(UsernameField.getText(), encryptedString);
			    System.out.println("created curr");
			    boolean succ = curr.login();
			    if (succ){
                    frame.dispose();
			        new LandingPage(curr);
			    }
			    else{
			        err.setVisible(true);
			    }
			}
		});
		btnLogIn.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		btnLogIn.setBounds(241, 166, 120, 46);
		frame.getContentPane().add(btnLogIn);
		
		JButton btnSignIn = new JButton("Sign Up");
		btnSignIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                MessageDigest messageDigest = null;
                try {
                    messageDigest = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                messageDigest.update(passwordField.getText().getBytes());
                String encryptedString = new String(messageDigest.digest());
			    curr = new Host(UsernameField.getText(), encryptedString);
			    boolean succ = curr.signUp();
			    if (succ){
			        err.setText("Account created, login please");
			        err.setVisible(true);
			        UsernameField.setText("");
			        passwordField.setText("");
			    }
			    else{
			        err.setText("Couldn't create account");
			        err.setVisible(true);
			    }
			}
		});
		btnSignIn.setBounds(11, 243, 117, 29);
		frame.getContentPane().add(btnSignIn);
	}
}
