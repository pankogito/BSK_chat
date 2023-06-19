package cipher;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.List;

public class SymmetricCipher {
    public static int MESSAGE_SIZE = 512;
    public static int KEY_SIZE = 128;
    public static int IV_SIZE = 16;
    private static byte CBC = 1;

    private SecretKey key;
    private SecureRandom random;

    public boolean isCbc() {
        return cbc;
    }

    private boolean cbc;

    public SecretKey getKey() {
        return key;
    }
    public byte[] getHash(){
        return key.getEncoded();
    }

    public SymmetricCipher(boolean cbc) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();
        this.cbc = cbc;
        random  = new SecureRandom();
        System.out.println(cbc + " " + Arrays.toString(key.getEncoded()));
    }
    private SymmetricCipher(byte[] message){
        cbc = message[0] == CBC;
        key = new SecretKeySpec(Arrays.copyOfRange(message,1,KEY_SIZE/8+1),"AES");
        random = new SecureRandom();
    }
    public SymmetricCipher(byte[] message, List<AsymmetricCipher> potentiallySigners) throws IllegalArgumentException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        this(message);
        System.out.println(cbc + " " + Arrays.toString(key.getEncoded()));
        var sign = Arrays.copyOfRange(message,KEY_SIZE/8+1,KEY_SIZE/8+256+1);
        for(var signer:potentiallySigners){
            System.out.println(signer);
            if(signer.verifySing(sign, key.getEncoded()))
                return;
        }
        throw new IllegalArgumentException("untrusted sign");
    }
    public byte[] getMessage(AsymmetricCipher signer) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        var message = new byte[MESSAGE_SIZE];
        if(cbc)
            message[0] = CBC;
        int  i = 1;
        for(var b : key.getEncoded()){
            message[i] =  b;
            i++;
        }
        for(var b : signer.sign(key.getEncoded())){
            message[i] =  b;
            i++;
        }
        return message;
    }




    public byte[] encrypt(byte[] bytes){
        Cipher decryptionCipher = null;
        try {
            if(cbc){
                var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                var iv = new byte[IV_SIZE];
                random.nextBytes(iv);
                System.out.println("iv"+ Arrays.toString(iv));
                cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(iv));
                bytes = cipher.doFinal(bytes);
                var re = Arrays.copyOf(iv,iv.length+bytes.length);
                for( int i = 0;i<bytes.length;i++)
                    re[iv.length + i] = bytes[i];
                return re;
            }
            else{
                var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(bytes);
            }


        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] decrypt(byte[] bytes){

        Cipher cipher = null;
        try {
            if(cbc){
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                var iv = Arrays.copyOf(bytes, IV_SIZE);
                bytes = Arrays.copyOfRange(bytes,IV_SIZE,bytes.length);
                cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv));
                bytes = cipher.doFinal(bytes);
            }else{
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                bytes = cipher.doFinal(bytes);
            }
            return bytes;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean validateHash(byte[] hash){
        var h = key.getEncoded();
        if(hash.length > h.length){
            hash = Arrays.copyOfRange(hash,0,h.length);
        }
        return Arrays.equals(h,hash);
    }
}

