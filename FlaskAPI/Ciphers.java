package nss.my.iscte.student;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import java.security.InvalidAlgorithmParameterException;


public class Ciphers {
	private static final int IV_SIZE = 16;
	private SecretKeySpec secretKeySpec;

	public Ciphers(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		// Hashing key.
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(key.getBytes("UTF-8"));
		byte[] keyBytes = new byte[32];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
		secretKeySpec = new SecretKeySpec(keyBytes, "AES");
	}

	public String encrypt(String plainText) throws IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException{
		byte[] clean = plainText.getBytes();

		// Generating IV.
		byte[] iv = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		// Encrypt.
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encrypted = cipher.doFinal(clean);

		// Combine IV and encrypted part.
		byte[] encryptedIVAndText = new byte[IV_SIZE + encrypted.length];
		System.arraycopy(iv, 0, encryptedIVAndText, 0, IV_SIZE);
		System.arraycopy(encrypted, 0, encryptedIVAndText, IV_SIZE, encrypted.length);

		String s = new String(Base64.encode(encryptedIVAndText, Base64.DEFAULT));
		return s;
	}

	public String decrypt(String inputText) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException{
		// Extract IV.
		byte[] iv = new byte[IV_SIZE];
		byte[] encryptedIvTextBytes = Base64.decode(inputText,Base64.DEFAULT);
		System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		// Extract encrypted part.
		int encryptedSize = encryptedIvTextBytes.length - IV_SIZE;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(encryptedIvTextBytes, IV_SIZE, encryptedBytes, 0, encryptedSize);

		// Decrypt.
		javax.crypto.Cipher cipherDecrypt = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipherDecrypt.init(javax.crypto.Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

		return new String(decrypted);
	}
}
