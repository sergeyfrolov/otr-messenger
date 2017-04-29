package org.otrmessenger.viewer;
import org.otrmessenger.viewer.Host;
import org.otrmessenger.viewer.User;
import org.otrmessenger.crypto.EncrypterAES;
import org.otrmessenger.History;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Chat  {
	
	private Host host;
	private User other;
	private History history;
	private EncrypterAES AES;
	private JFrame frame;
	private JTextField messageField;
	private static String othername="other"; //for testing
	
	
	/**
	 * Create the application.
	 */
	public Chat(String name) {
	    this.history = new History();
		initialize(name);
        this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String name) {
		frame = new JFrame("Chatting with "+name);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea HistoryArea = new JTextArea();
		HistoryArea.setEditable(false);
		HistoryArea.setLineWrap(true);
		HistoryArea.setWrapStyleWord(true);
		HistoryArea.setBounds(17, 17, 415, 169);
		frame.getContentPane().add(HistoryArea);
		
		messageField = new JTextField();
		messageField.setText("");
		messageField.setBounds(17, 212, 323, 42);
		frame.getContentPane().add(messageField);
		messageField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String newMessage = messageField.getText();
			}
		});
		btnSend.setBounds(339, 217, 93, 35);
		frame.getContentPane().add(btnSend);
	}

}
