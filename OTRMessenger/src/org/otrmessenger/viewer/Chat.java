package org.otrmessenger.viewer;
import org.otrmessenger.viewer.Host;
import org.otrmessenger.viewer.User;
import java.util.Base64;

import com.google.protobuf.ByteString;

import org.otrmessenger.messaging.Messaging.Message;
import org.otrmessenger.crypto.EncrypterAES;
import org.otrmessenger.crypto.Key;
import org.otrmessenger.crypto.KeyPair;
import org.otrmessenger.crypto.Signer;
import org.otrmessenger.History;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
//import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Chat  {
	
	private Host host;
	private User other;
	private History history;
	private EncrypterAES AES = null;
	private JFrame frame;
	private JTextField messageField;
	private boolean checkSigs;
	JTextArea HistoryArea;
	
	
	/**
	 * Create the application.
	 */
	public Chat(String name, Host h) {
	    this.host = h;
	    this.other = new User(name);
	    getUserSigningKey();
	    this.history = new History();
		initialize(name);
        this.frame.setVisible(true);
	}
	
	public byte[] createEncrypter(byte[] optPubKey){
	    PublicKey othersEncKey;
        DHParameterSpec dhParamSpec;
        KeyPair myKeyPair;
	    if (optPubKey.length == 0){
            othersEncKey = this.host.requestEncryptionKey(other);
            dhParamSpec = ((DHPublicKey)othersEncKey).getParams();
            KeyPairGenerator myKeyPairGen = null;
            try {
                myKeyPairGen = KeyPairGenerator.getInstance("DH");
            } catch (NoSuchAlgorithmException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                myKeyPairGen.initialize(dhParamSpec);
            } catch (InvalidAlgorithmParameterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myKeyPair = new KeyPair(myKeyPairGen.generateKeyPair());
	    }
	    else{
	        try {
                othersEncKey = KeyFactory.getInstance("DiffieHellman").generatePublic(
                        new X509EncodedKeySpec(optPubKey));
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
	        dhParamSpec = ((DHPublicKey) this.host.getPublicKey()).getParams();
	        myKeyPair = this.host.getKeyPair();
	    }

        KeyAgreement ourKeyAgree = null;
        try {
            ourKeyAgree = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            ourKeyAgree.init(myKeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            ourKeyAgree.doPhase(othersEncKey, true);
        } catch (InvalidKeyException | IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] sharedKey = ourKeyAgree.generateSecret();
        Key sKey = new Key(Base64.getEncoder().encodeToString(sharedKey), "salt", "AES");
        this.AES = new EncrypterAES(sKey);
        return myKeyPair.getPublic().getEncoded();
	}
	
	public void getUserSigningKey(){
	    checkSigs = this.host.requestSigningKey(other);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String name) {
		frame = new JFrame("Chatting with "+name);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		HistoryArea = new JTextArea();
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
			    messageField.setText("");
                Message.Builder msgBuilder = Message.newBuilder();
                msgBuilder.setToUsername(ByteString.copyFromUtf8(other.getUsername()));
                msgBuilder.setFromUsername(ByteString.copyFromUtf8(host.getUsername()));
                msgBuilder.setText(ByteString.copyFromUtf8(newMessage));
                if (AES == null){
                    byte[] pk = createEncrypter(new byte[]{});
                    msgBuilder.setPubkey(ByteString.copyFrom(pk));
                }
                Message histMessage = msgBuilder.build();
                Message m = host.signMessage(AES.encrypt(histMessage));
                System.out.println(m);
                if (host.sendMessage(other, m)){
                    history.addMsg(histMessage);
                    updateHistoryArea(HistoryArea);
                }
                else{
                    msgBuilder.setText(ByteString.copyFromUtf8("user " + other.getUsername() + " is not available"));
                    m = msgBuilder.build();
                    history.addMsg(m);
                    updateHistoryArea(HistoryArea);
                }
			}
		});
		btnSend.setBounds(339, 217, 93, 35);
		frame.getContentPane().add(btnSend);
	}
	

	private void updateHistoryArea(JTextArea historyArea){
	    StringBuilder strBuilder = new StringBuilder();
	    
	    for(int i = 0; i < history.numMessages(); i++){
	        Message m = history.getMsg(i);
	        strBuilder.append(m.getFromUsername().toStringUtf8());
	        strBuilder.append("> ");
	        strBuilder.append(m.getText().toStringUtf8());
	        strBuilder.append("\n");
	    }
	    
	    historyArea.setText(strBuilder.toString());
	    
	}
	
	public User getOther(){
	    return this.other;
	}

	public void receiveMessage(Message msg){
	    if (checkSigs){
	        if (!other.verifyMessage(msg)){
	            Message.Builder msgBuilder = Message.newBuilder();
	            msgBuilder.setFromUsername(msg.getFromUsername());
	            msgBuilder.setToUsername(msg.getToUsername());
	            msgBuilder.setText(ByteString.copyFromUtf8("User's signature didn't match this message"));
	            msg = msgBuilder.build();
	        }

	        System.out.println(msg);
	        byte[] pubKey = msg.getPubkey().toByteArray();
	        if(pubKey.length > 0){
	            //need to decrypt message
	            createEncrypter(pubKey);
	            msg = AES.decrypt(msg);
	        }
	        else if(AES != null){
	            msg = AES.decrypt(msg);
	        }
	    }
	    history.addMsg(msg);
	    updateHistoryArea(HistoryArea);
	}
}
