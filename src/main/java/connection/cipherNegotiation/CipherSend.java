package connection.cipherNegotiation;

import cipher.AsymmetricCipher;
import cipher.SymmetricCipher;
import connection.Connection;
import connection.ConnectionState;
import transfer.InMemoTransfer;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;

import static connection.Connection.ACCEPT_CIPHER_PRE;
import static connection.Connection.SEND_LOG;

public class CipherSend implements  CipherNegotiation{

    private InMemoTransfer sended;
    private Connection connection;
    private SymmetricCipher cipher;
    private Thread thread;

    public CipherSend( Connection connection,boolean cbc, AsymmetricCipher owner,AsymmetricCipher client) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        this.connection = connection;
        try {
            cipher = new SymmetricCipher(cbc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        var bytes = cipher.getMessage(owner);
        System.out.println(Arrays.toString(bytes));
        try {
            bytes = client.encrypt(bytes);
            System.out.println(Arrays.toString(bytes));
            this.sended = new InMemoTransfer("cipher",bytes);

            thread = new Thread(this::sendTransferThread);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receive(byte[] bytes) {
        if(connection.receivePrefix(bytes).equals(Connection.ACCEPT_CIPHER_PRE)){
            if(cipher.validateHash(connection.receiveBytesContent(bytes))){
                connection.addLog("CONNECTION IS SAFE");
                connection.setState(ConnectionState.IDLE);
                connection.send(ACCEPT_CIPHER_PRE,cipher.getHash());
            }
        }
    }
    public void sendTransferThread(){
        while(sended.getCounter() < sended.getLength()){
            try {
                connection.send(Connection.CIPHER_PRE,sended.readBytes(connection.dataSize()-2));
                connection.addTransferLog(SEND_LOG+ sended.getId(), 100.0*sended.getCounter()/sended.getLength());
            } catch (IOException e) {e.printStackTrace();}
        }
        connection.removeTransferLog(SEND_LOG+sended.getId());
        connection.addLog(SEND_LOG+"successful transfer of "+sended.getId());
        connection.setCurrent(cipher);
    }
}
