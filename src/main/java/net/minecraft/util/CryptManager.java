package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptManager {
    public static SecretKey createNewSharedKey() {
        try {
            KeyGenerator instance = KeyGenerator.getInstance("AES");
            instance.init(128);
            return instance.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA");
            instance.initialize(1024);
            return instance.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("Key pair generation failed!");
            return null;
        }
    }

    public static byte[] getServerIdHash(String str, PublicKey publicKey, SecretKey secretKey) {
        try {
            return digestOperation("SHA-1", str.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] digestOperation(String str, byte[]... byteses) {
        try {
            MessageDigest instance = MessageDigest.getInstance(str);
            byte[][] byteses2 = byteses;
            int length = byteses.length;

            for (int i = 0; i < length; i++) {
                byte[] bytes = byteses2[i];
                instance.update(bytes);
            }

            return instance.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PublicKey decodePublicKey(byte[] bytes) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(bytes);
            KeyFactory instance = KeyFactory.getInstance("RSA");
            return instance.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.err.println("Public key reconstitute failed!");
            return null;
        }
    }

    public static SecretKey decryptSharedKey(PrivateKey privateKey, byte[] bytes) {
        return new SecretKeySpec(decryptData(privateKey, bytes), "AES");
    }

    public static byte[] encryptData(Key key, byte[] bytes) {
        return cipherOperation(1, key, bytes);
    }

    public static byte[] decryptData(Key key, byte[] bytes) {
        return cipherOperation(2, key, bytes);
    }

    public static byte[] cipherOperation(int n, Key key, byte[] bytes) {
        try {
            return createTheCipherInstance(n, key.getAlgorithm(), key).doFinal(bytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        System.err.println("Cipher data failed!");
        return null;
    }

    public static Cipher createTheCipherInstance(int n, String str, Key key) {
        try {
            Cipher instance = Cipher.getInstance(str);
            instance.init(n, key);
            return instance;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        System.err.println("Cipher creation failed!");
        return null;
    }

    public static Cipher cipherFromKey(int n, Key key) {
        try {
            Cipher instance = Cipher.getInstance("AES/CFB8/NoPadding");
            instance.init(n, key, new IvParameterSpec(key.getEncoded()));
            return instance;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
