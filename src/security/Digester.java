package security;

import logic.misc.ConfigLoader;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class Digester {

    private final static String SALT = ConfigLoader.HASH_SALT;
    private final static String KEY = ConfigLoader.ENCRYPT_KEY;
    private static MessageDigest digester;

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that returns a hashed string
     * @param string String that needs to be hashed
     * @return The hashed String
     */
    public static String hash(String string){
        if (string == null || string.length() == 0){
            throw new IllegalArgumentException("Error");
            //LOGGING
        }

        string = string + Digester.SALT;

        return Digester._hash(string);
    }

    /**
     * Private method used by hash() to hash and return the String.
     * @param string String that needs to be hashed
     * @return The hashed String
     */
    private static String _hash(String string){
        digester.update(string.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        return hexString.toString();
    }

    /**
     * Method called to encrypt a String.
     * @param string String that needs to be encrypted
     * @return The encrypted String
     */
    public static String encrypt(String string) {

        String encrypted_string = string;

        if(ConfigLoader.ENCRYPTION.equals("TRUE")){
            encrypted_string = base64Encode(xorWithKey(encrypted_string.getBytes(), KEY.getBytes()));
        }
        return encrypted_string;
    }

    /**
     * Method called to decrypt a String.
     * @param string String that needs to be decrypted
     * @return The decrypted String
     */
    public static String decrypt(String string) {

        String decrypted_string = string;

        if(ConfigLoader.ENCRYPTION.equals("TRUE")) {
            decrypted_string = new String(xorWithKey(base64Decode(string), KEY.getBytes()));
        }
        return decrypted_string;
    }

    /**
     * Private method used by encrypt() and decrypt() to encrypt and decrypt with XOR
     * @param a The byte-array for conversion
     * @param key The specified Key in config.json.
     * @return returns the XOR byte-array.
     */
    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }


    /**
     * Private method used by decrypt() to decrypt a String
     * @param string String that needs to be decrypted
     * @return Returns the decrypted String
     */
    private static byte[] base64Decode(String string) {
        try {
            BASE64Decoder d = new BASE64Decoder();
            return d.decodeBuffer(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
            //LOGGING
        }
    }

    /**
     * Private method used by encrypt() to encrypt a byte-array
     * @param bytes the byte-array to encrypt
     * @return Returns a encrypted String
     */
    private static String base64Encode(byte[] bytes) {
        BASE64Encoder enc = new BASE64Encoder();
        return enc.encode(bytes).replaceAll("\\s", "");

    }
}