package cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class AsymmetricCipher {
    private byte[] publicKey, privateKey;
    private final String algorithm = "RSA";

    public AsymmetricCipher(byte[] publicKey) {
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    public AsymmetricCipher(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(keySpec);
    }

    public PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(keySpec);
    }


    public byte[] encrypt(byte[] bytes) throws Exception {
        if (privateKey == null)
            return null;
        else {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());

            return cipher.doFinal(bytes);
        }
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());

        return cipher.doFinal(bytes);
    }

    public byte[] sign(byte[] bytes) {
        if (privateKey == null)
            return null;
        return bytes;
    }

    public byte[] signed(byte[] sign) {
        return sign;
    }

    public boolean verifySing(byte[] sign, byte[] hash) {
        return Arrays.equals(signed(sign), hash);
    }

}
