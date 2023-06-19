package aplication;

import cipher.AsymmetricCipher;

public interface LoginListner {
    void action(String user, char[] pass);
}
