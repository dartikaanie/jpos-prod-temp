package helper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ThreeDES {
	
	public static String encrypt(String message, String key) throws Exception {
		byte[] byteEncrypt = encryptDES(message, key);
		return bytesToHex(byteEncrypt);
	}
	
	public static String decrypt(String message, String key) throws Exception {
		byte[] byteEncrypt = decryptDES(message, key);
		return bytesToHex(byteEncrypt);
	}

	public static byte[] encryptDES(String toEncrypt, String key) throws Exception {
	      // create a binary key from the argument key (seed);
	  
	      // create an instance of cipher
	      Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	      SecretKey sKey = new SecretKeySpec(key.getBytes(), "DESede");
	      // initialize the cipher with the key
	      cipher.init(Cipher.ENCRYPT_MODE, sKey);
	  
	      // enctypt!
	      byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
	  
	      return encrypted;
	   }
	  
	   public static byte[] decryptDES(String toDecrypt, String key) throws Exception {
	      // create a binary key from the argument key (seed)
		   Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	      SecretKey sKey = new SecretKeySpec(key.getBytes(), "DESede");
	      cipher.init(Cipher.DECRYPT_MODE, sKey);
	      byte[] decrypted = cipher.doFinal(toDecrypt.getBytes());
	      return decrypted;
	   }
	   
	   private static String bytesToHex(final byte[] bytes) {
	        final StringBuilder buf = new StringBuilder(bytes.length * 2);
	        for (final byte b : bytes) {
	            final String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) {
	                buf.append("0");
	            }
	            buf.append(hex);
	        }
	        return buf.toString();
	    }

	    private static byte[] hexToBytes(final String hex) {
	        final byte[] bytes = new byte[hex.length() / 2];
	        for (int i = 0; i < bytes.length; i++) {
	            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
	        }
	        return bytes;
	    }
}
