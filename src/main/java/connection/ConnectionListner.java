package connection;

import cipher.AsymmetricCipher;

import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface ConnectionListner {

    void recordOpen(Socket socket, AsymmetricCipher assumption) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    void recordOpen(Socket socket);
}
