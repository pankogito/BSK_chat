package cipher;

import java.util.Arrays;
import java.util.List;

public class SymmetricCipher {
    public static int MESSAGE_SIZE = 400096;

    public byte[] getKey() {
        return key;
    }

    public byte[] getEncryptIv() {
        return encryptIv;
    }

    public byte[] getDecryptIv() {
        return decryptIv;
    }

    public byte[] getHash() {
        return hash;
    }

    private byte[] key,encryptIv,decryptIv,hash;

    public SymmetricCipher(boolean cbc){
        hash = new byte[256];
        hash[ 0] = 127;
        hash[255] = 125;
        //TODO Random generation of key and iv if cbc == true, next make hash
    }

    public byte[] getMessage(AsymmetricCipher signer){
        var message = Arrays.copyOf(hash,MESSAGE_SIZE);
        //TODO copy key and iv
        System.out.println(message);
        return message;
    }

    public SymmetricCipher(byte[] message,AsymmetricCipher signer){
        hash = signer.signed(Arrays.copyOfRange(message,0,256));
        this.encryptIv = new byte[0];
        this.decryptIv = new byte[0];
        this.key = new byte[0];
        //TODO get key and iv from message. Verify sing.
    }
    public SymmetricCipher(byte[] message, List<AsymmetricCipher> potentiallySigners){
        hash = Arrays.copyOfRange(message,0,256);
        this.encryptIv = new byte[0];
        this.decryptIv = new byte[0];
        this.key = new byte[0];
        //TODO get key and iv from message. Verify sing.
    }

    public SymmetricCipher(byte[] key,byte[] iv,byte[] hash){
        this.encryptIv = iv;
        this.decryptIv = iv;
        this.key = key;
        this.hash = hash;
    }
    public byte[] encrypt(byte[] bytes){
        return bytes;
    }
    public byte[] decrypt(byte[] bytes){
        return bytes;
    }
    public boolean validateHash(byte[] hash){
        if(hash.length > this.hash.length){
            hash = Arrays.copyOfRange(hash,0,this.hash.length);
        }
        return Arrays.equals(this.hash,hash);
    }
}

