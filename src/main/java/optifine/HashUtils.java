package optifine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static String getHashMd5(String str) {
        return getHash(str, "MD5");
    }

    public static String getHashSha1(String str) {
        return getHash(str, "SHA-1");
    }

    public static String getHashSha256(String str) {
        return getHash(str, "SHA-256");
    }

    public static String getHash(String str, String str2) {
        try {
            byte[] hash = getHash(str.getBytes("UTF-8"), str2);
            return toHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(bytes[i] & 255 | 256).substring(1, 3));
        }

        return sb.toString();
    }

    public static byte[] getHashMd5(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "MD5");
    }

    public static byte[] getHashSha1(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "SHA-1");
    }

    public static byte[] getHashSha256(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "SHA-256");
    }

    public static byte[] getHash(byte[] bytes, String str) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance(str);
        return instance.digest(bytes);
    }
}
