package cipher;

import java.util.Arrays;

public class AsymmetricCipher {
    private byte[] publicKey,privateKey;

    public AsymmetricCipher(byte[] publicKey) {
        this.publicKey = publicKey;
        this.privateKey =  null;
    }

    public AsymmetricCipher(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] encrypt(byte[] bytes){
        if(privateKey == null)
            return null;
        else
            return bytes;
    }
    public byte[] decrypt(byte[] bytes){
        return bytes;
    }
    public byte[] sign(byte[] bytes){
        if(privateKey == null)
            return null;
        return bytes;
    }
    public byte[] signed(byte[] sign){
        return sign;
    }
    public boolean verifySing(byte[] sign,byte[] hash){
        return Arrays.equals(signed(sign),hash);
    }
}
