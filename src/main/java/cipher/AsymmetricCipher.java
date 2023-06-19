package cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;


public class AsymmetricCipher {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private final String algorithm = "RSA";

    public AsymmetricCipher(byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        this.publicKey = keyFactory.generatePublic(keySpec);
        this.privateKey = null;
    }

    public AsymmetricCipher(byte[] publicKey, byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpecPu = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        this.publicKey = keyFactory.generatePublic(keySpecPu);
        PKCS8EncodedKeySpec keySpecPr = new PKCS8EncodedKeySpec(privateKey);
        keyFactory = KeyFactory.getInstance(algorithm);
        this.privateKey = keyFactory.generatePrivate(keySpecPr);
    }


    public byte[] encrypt(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int encryptedBytesSize = (int) (256 * Math.ceil(bytes.length/245.0));
        byte[] encryptedBytes = new byte[encryptedBytesSize];
        ByteBuffer buffer = ByteBuffer.wrap(encryptedBytes);

        for (int i = 0; i < bytes.length; i += 245) {
            byte[] tile = new byte[Math.min(245, bytes.length - i)];
            System.arraycopy(bytes, i, tile, 0, tile.length);
            byte[] encrypted = cipher.doFinal(tile);
            buffer.put(encrypted);
        }

        return buffer.array();
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        if (privateKey == null)
            return null;
        else {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int decryptedBytesSize = (int) (bytes.length - ((Math.ceil((double) bytes.length /256) * 11)));
            byte[] decryptedBytes = new byte[decryptedBytesSize];
            ByteBuffer buffer = ByteBuffer.wrap(decryptedBytes);

            for (int i = 0; i < bytes.length; i += 256) {
                byte[] tile = new byte[Math.min(256, bytes.length - i)];
                System.arraycopy(bytes, i, tile, 0, tile.length);
                byte[] decrypted = cipher.doFinal(tile);
                buffer.put(decrypted);
            }

            return buffer.array();
        }

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
