package org.otrmessenger.viewer;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddFriend {

	private JFrame frame;
	private JTextField textField;
	private JTextField GroupField;
	private JButton btnAdd;
	private JButton btnCancel;
	private Host myself;

	/**
	 * Create the application.
	 */
	public AddFriend(Host m) {
	    this.myself = m;
		initialize();
		this.frame.setVisible(true);
	}

	public AddFriend() {
	    this(null);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Add a Friend");
		frame.setBounds(100, 100, 350, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(46, 37, 61, 16);
		frame.getContentPane().add(lblName);
		
		textField = new JTextField();
		textField.setBounds(119, 29, 194, 33);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel failedText = new JLabel("Friend not available");
		failedText.setBounds(43, 100, 300, 16);
		frame.getContentPane().add(failedText);
		failedText.setForeground(Color.RED);
		failedText.setVisible(false);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String newUserName = textField.getText();
			    if (newUserName.length() > 0){
			        boolean success = myself.addFriend(newUserName);
			        if(!success){
			            failedText.setVisible(true);
			        }
			        else{
			            frame.dispose();
			        }
			    }
			}
		});
		btnAdd.setBounds(43, 132, 117, 29);
		frame.getContentPane().add(btnAdd);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    frame.dispose();
			}
		});
		btnCancel.setBounds(196, 132, 117, 29);
		frame.getContentPane().add(btnCancel);
		
		JLabel lblGroup = new JLabel("Group");
		lblGroup.setBounds(46, 82, 61, 16);
		frame.getContentPane().add(lblGroup);
		
		GroupField = new JTextField();
		GroupField.setBounds(119, 74, 194, 33);
		frame.getContentPane().add(GroupField);
		GroupField.setColumns(10);
	}
	
	public Host getHost(){
	    return myself;
	}

}
