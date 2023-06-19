package aplication;

import cipher.AsymmetricCipher;

import java.util.List;

public class UserData {
    AsymmetricCipher user;
    List<AsymmetricCipher> trusted;

    public UserData(AsymmetricCipher user, List<AsymmetricCipher> trusted) {
        this.user = user;
        this.trusted = trusted;
    }

    public AsymmetricCipher getUser() {
        return user;
    }

    public List<AsymmetricCipher> getTrusted() {
        return trusted;
    }
}
