package connection;

import cipher.AsymmetricCipher;

import java.net.Socket;

public interface ConnectionListner {

    void recordOpen(Socket socket, AsymmetricCipher assumption);
    void recordOpen(Socket socket);
}
