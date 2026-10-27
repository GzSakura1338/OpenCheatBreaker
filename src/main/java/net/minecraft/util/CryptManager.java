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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager {
   public static Logger LOGGER = LogManager.getLogger();

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
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
      try {
         return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static byte[] digestOperation(String algorithm, byte[]... data) {
      try {
         MessageDigest instance = MessageDigest.getInstance(algorithm);

         for (byte[] bytes : data) {
            instance.update(bytes);
         }

         return instance.digest();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static PublicKey decodePublicKey(byte[] encodedKey) {
      try {
         X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedKey);
         KeyFactory instance = KeyFactory.getInstance("RSA");
         return instance.generatePublic(x509EncodedKeySpec);
      } catch (NoSuchAlgorithmException e) {
      } catch (InvalidKeySpecException e) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
      return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
   }

   public static byte[] encryptData(Key key, byte[] data) {
      return cipherOperation(1, key, data);
   }

   public static byte[] decryptData(Key key, byte[] data) {
      return cipherOperation(2, key, data);
   }

   public static byte[] cipherOperation(int opMode, Key key, byte[] data) {
      try {
         return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
      } catch (IllegalBlockSizeException e) {
         e.printStackTrace();
      } catch (BadPaddingException e) {
         e.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   public static Cipher createTheCipherInstance(int opMode, String transformation, Key key) {
      try {
         Cipher instance = Cipher.getInstance(transformation);
         instance.init(opMode, key);
         return instance;
      } catch (InvalidKeyException e) {
         e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      } catch (NoSuchPaddingException e) {
         e.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher createNetCipherInstance(int opMode, Key key) {
      try {
         Cipher instance = Cipher.getInstance("AES/CFB8/NoPadding");
         instance.init(opMode, key, new IvParameterSpec(key.getEncoded()));
         return instance;
      } catch (GeneralSecurityException e) {
         throw new RuntimeException(e);
      }
   }
}
