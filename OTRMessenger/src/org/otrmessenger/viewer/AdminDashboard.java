package org.otrmessenger.viewer;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AdminDashboard {

	private JFrame frame;
	private Host myself;
	private AllUsers windowAU;
	private OnlineUsers windowOU;
	private PublicKeys windowPK;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminDashboard window = new AdminDashboard();
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
	public AdminDashboard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("AdminDashboard");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblCurrentStatus = new JLabel("Current Status: ");//+status should be passed when creating Dashboard
		lblCurrentStatus.setBounds(137, 24, 198, 16);
		frame.getContentPane().add(lblCurrentStatus);
		
		JButton btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO for server
			}
		});
		btnLaunch.setBounds(44, 55, 117, 29);
		frame.getContentPane().add(btnLaunch);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO for server
			}
		});
		btnStop.setBounds(44, 84, 117, 29);
		frame.getContentPane().add(btnStop);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO for server
			}
		});
		btnReset.setBounds(44, 115, 117, 29);
		frame.getContentPane().add(btnReset);
		
		JButton btnListAllUsers = new JButton("List All Users");
		btnListAllUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowAU = new AllUsers();
				windowAU.frameAU.setVisible(true);
				//TODO complete it with actions, closing, etc.
			}
		});
		btnListAllUsers.setHorizontalAlignment(SwingConstants.LEFT);
		btnListAllUsers.setBounds(154, 166, 151, 29);
		frame.getContentPane().add(btnListAllUsers);
		
		JButton btnListOnlineUsers = new JButton("List Online Users");
		btnListOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowOU = new OnlineUsers();
				windowOU.frameOU.setVisible(true);
				//TODO complete it with actions, closing, etc.
			}
		});
		btnListOnlineUsers.setBounds(154, 197, 151, 29);
		frame.getContentPane().add(btnListOnlineUsers);
		
		JButton btnPublicKeys = new JButton("Public Keys ");
		btnPublicKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowPK = new PublicKeys();
				windowPK.framePK.setVisible(true);
				//TODO complete it with actions, closing, etc.
			}
		});
		btnPublicKeys.setHorizontalAlignment(SwingConstants.LEFT);
		btnPublicKeys.setBounds(154, 231, 151, 29);
		frame.getContentPane().add(btnPublicKeys);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 142, 438, 12);
		frame.getContentPane().add(separator);
	}

public class AllUsers {

	private JFrame frameAU;
	public AllUsers() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameAU = new JFrame("All Users");
		frameAU.setBounds(200, 100, 450, 300);
		frameAU.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameAU.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 16, 416, 245);
		frameAU.getContentPane().add(scrollPane);
		
		JTextArea txtrDisplayarea = new JTextArea();
		scrollPane.setColumnHeaderView(txtrDisplayarea);
		txtrDisplayarea.setEditable(false);
	}
}

public class OnlineUsers {

	private JFrame frameOU;				
	/**
	 * Create the application.
	 */
	public OnlineUsers() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameOU = new JFrame("Online Users");
		frameOU.setBounds(300, 100, 450, 300);
		frameOU.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameOU.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 47, 402, 199);
		frameOU.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblOf = new JLabel("Online: 1 of 12"); //this text should be read from server?
		lblOf.setHorizontalAlignment(SwingConstants.CENTER);
		lblOf.setBounds(155, 19, 137, 16);
		frameOU.getContentPane().add(lblOf);
	}
}
public class PublicKeys {

	private JFrame framePK;				
	/**
	 * Create the application.
	 */
	public PublicKeys() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		framePK = new JFrame("Online Users");
		framePK.setBounds(400, 100, 450, 300);
		framePK.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		framePK.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 47, 402, 199);
		framePK.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblOf = new JLabel("Online: 1 of 12"); //this text should be read from server?
		lblOf.setHorizontalAlignment(SwingConstants.CENTER);
		lblOf.setBounds(155, 19, 137, 16);
		framePK.getContentPane().add(lblOf);
	}
}

}

