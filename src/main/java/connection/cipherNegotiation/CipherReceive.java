package connection.cipherNegotiation;

import cipher.AsymmetricCipher;
import cipher.SymmetricCipher;
import connection.Connection;
import connection.ConnectionState;
import transfer.InMemoTransfer;
import transfer.MultiFrameTransfer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static connection.Connection.RECEIVE_LOG;

public class CipherReceive implements CipherNegotiation{

    private Connection connection;
    private InMemoTransfer received;
    private AsymmetricCipher owner;
    private List<AsymmetricCipher> clients;

    public CipherReceive(Connection connection, AsymmetricCipher owner, List<AsymmetricCipher> clients) {
        this.connection = connection;
        this.owner = owner;
        this.clients = clients;
    }

    public void receive(byte[] bytes){
        var prefix = connection.receivePrefix(bytes);
        if(prefix.equals(Connection.CIPHER_PRE)){
            if(received == null) {
                received = new InMemoTransfer("cipher", SymmetricCipher.MESSAGE_SIZE);
            }
            try {
                received.writeBytes(connection.receiveBytesContent(bytes));
                if(received.ended()){
                    connection.addLog(RECEIVE_LOG+ "successful transfer of "+received.getId());
                    connection.removeTransferLog(RECEIVE_LOG + received.getId());

                    var message = owner.decrypt(received.getBytes());
                    System.out.println(Arrays.toString(received.getBytes()));
                    var cipher = new SymmetricCipher(message,clients);
                    System.out.println(cipher);
                    connection.setCurrent(cipher);
                    connection.send(Connection.ACCEPT_CIPHER_PRE,cipher.getHash());
                    received.close();
                }
                else
                    connection.addTransferLog(RECEIVE_LOG+received.getId(),100.0*received.getCounter()/received.getLength());

            } catch (IOException e) {e.printStackTrace();} catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(prefix.equals(Connection.ACCEPT_CIPHER_PRE)){
            connection.addLog("CONNECTION IS SAFE");
            connection.setState(ConnectionState.IDLE);
        }

    }
}
