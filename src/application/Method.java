package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Method {
	String alphabet = " AxByCzDuEvFwGrHsItJoKpLqMlNmOnPhQiRjSkTeUfVgWaXbYcZd9013254687=?@$#%^&*!~";

	public String caesarDecrypt(String encryptText, int keyD) {
		String plainText = "";

		for (int i = 0; i < encryptText.length(); i++) // loop through all characters
		{
			char plainCharacter = encryptText.charAt(i);
			int position = alphabet.indexOf(plainCharacter); // get the psoition in the alphabet
			int newPosition = Math.floorMod((position - keyD), alphabet.length()); // position of the cipher character
			char cipherCharacter = alphabet.charAt(newPosition);
			plainText += cipherCharacter; // appending this cipher character to the cipherText
		}

		return plainText;
	}

	public String caesarEncrypt(String decryptText, int keyD) {
		String plainText = "";
		for (int i = 0; i < decryptText.length(); i++) // loop through all characters
		{
			char plainCharacter = decryptText.charAt(i);
			int position = alphabet.indexOf(plainCharacter); // get the psoition in the alphabet
			int newPosition = Math.floorMod((position + keyD), alphabet.length()); // position of the cipher character
			char cipherCharacter = alphabet.charAt(newPosition);
			plainText += cipherCharacter; // appending this cipher character to the cipherText
		}

		return plainText;
	}

	public String desEncrypt(DES des1, String decryptText) {
		String plainText = "";
		try {
			byte[] encText = des1.encrypt(decryptText);
			String encTextString = Base64.getEncoder().encodeToString(encText);
			plainText = encTextString;
		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}
		return plainText;
	}

	public String desEncrypt(SecretKey secretKey, String decryptText) {
		String plainText = "";
		try {
			DES des = new DES();
			des.setSecretkey(secretKey);
			byte[] encText = des.encrypt(decryptText);
			String encTextString = Base64.getEncoder().encodeToString(encText);
			plainText = encTextString;
		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}

		return plainText;
	}
	
	public void saveKeyFile(String fileName, byte[] key) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		SecretKey reconstructedKey = new SecretKeySpec(key, 0, key.length, "DES");
		oos.writeObject(reconstructedKey);
		oos.close();
	}

	public String loadKeyFile(String fileName) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		SecretKey loadKey = (SecretKey) ois.readObject();
		byte[] loadKeyByte = loadKey.getEncoded();
//		System.out.println("load key byte " + loadKeyByte);
		String loadKeyString = Base64.getEncoder().encodeToString(loadKeyByte);
//		System.out.println("load key string " + loadKeyString);
		ois.close();
		return loadKeyString;
	}

	public byte[] desEncrypt(String keystring, String decryptText) {
		byte[] plainText = null;
		try {
			byte[] decodedKey = Base64.getDecoder().decode(keystring);
			SecretKey reconstructedKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
			DES des = new DES();
			des.setSecretkey(reconstructedKey);
			byte[] encText = des.encrypt(decryptText);
			plainText = encText;
			System.out.println("plain text" + plainText);
		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}

		return plainText;
	}
	
	public byte[] aesEncrypt(String keystring, String decryptText) {
		byte[] plainText = null;
		try {
			byte[] decodedKey = Base64.getDecoder().decode(keystring);
			SecretKey reconstructedKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
			AES aes = new AES();
			aes.setSecretkey(reconstructedKey);
			byte[] encText = aes.encrypt(decryptText);
			plainText = encText;
			System.out.println("plain text" + plainText);
		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}

		return plainText;
	}

	public String desDecrypt(String keystring, String encryptText) {
		String plainText = "";
		try {
			byte[] decodedKey = Base64.getDecoder().decode(keystring);
			SecretKey reconstructedKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
			DES des = new DES();
			des.setSecretkey(reconstructedKey);
			byte[] encryptByte = Base64.getDecoder().decode(encryptText);
			try {
				String decText = des.decrypt(encryptByte);
				plainText = decText;
			} catch (Exception e) {
				System.out.println("Error in DES Decryption: " + e);
			}
			
		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}

		return plainText;
	}
	

	public String aesEncrypt(SecretKey secretKey, String decryptText) {
		String plainText = "";
		try {
			AES aes = new AES();
			aes.setSecretkey(secretKey);
			byte[] encText = aes.encrypt(decryptText);
			String encTextString = Base64.getEncoder().encodeToString(encText);
			plainText = encTextString;
		} catch (Exception e) {
			System.out.println("Error in AES: " + e);
			e.printStackTrace();
		}

		return plainText;
	}

	public String aesDecrypt(String keystring, String encryptText) {
		String plainText = "";
		try {
			byte[] decodedKey = Base64.getDecoder().decode(keystring);
			SecretKey reconstructedKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
			AES aes = new AES();
			aes.setSecretkey(reconstructedKey);
			byte[] encryptByte = Base64.getDecoder().decode(encryptText);
			String decText = aes.decrypt(encryptByte);
			plainText = decText;

		} catch (Exception e) {
			System.out.println("Error in AES: " + e);
			e.printStackTrace();
		}
		return plainText;
	}

	public String hashEncrypt(String input) {

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] messageDigest = md.digest(input.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);

			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
