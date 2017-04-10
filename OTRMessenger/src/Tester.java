import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.otrmessenger.EncrypterAES;
import org.otrmessenger.Key;
import org.otrmessenger.Message;

public class Tester {

	public static void main(String[] args) {
		Key k = null;;
		try {
			k = new Key("hello", "salt", "AES");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(k);
		Message m = new Message("hello1");
		System.out.println(m);
		
		EncrypterAES enc = null;
		try {
			enc = new EncrypterAES(k);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println("IV len = " + enc.getIV().length);

		try {
			System.out.println(enc.decrypt(enc.encrypt(m)));
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
	}

}
