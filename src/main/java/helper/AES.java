package helper;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

public class AES {

    static String skey = "I43HEN8JS6DIJSRT";
    static String salt = "k8hnksebjrrkshes3r";
	public static String encrypt(String strToEncrypt) {
        try {
            byte[] iv = {0, 1, 2, 3, 5, 7, 3, 0, 1, 4, 6, 3, 4, 9, 5, 3};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(skey.toCharArray(), salt.getBytes(), 21, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");	
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] encode = Base64.encode(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            return new String(encode);
        } catch (Exception e){
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            byte[] iv = {0, 1, 2, 3, 5, 7, 3, 0, 1, 4, 6, 3, 4, 9, 5, 3};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(skey.toCharArray(), salt.getBytes(), 21, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            byte[] cipherData = cipher.doFinal(Base64.decode(strToDecrypt.getBytes(StandardCharsets.UTF_8)));
            String decode = null;
            decode =  new String(cipherData, "UTF-8");
            return decode;
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}
