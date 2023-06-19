package connection.fileTransferProcedure;

import connection.Connection;
import connection.ConnectionState;
import transfer.FileTransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static connection.Connection.*;

public class FileSend implements  FileTransferProcedure{

    private FileTransfer fileTransfer;
    private Connection connection;
    private Thread thread;

    public FileSend(Connection connection,File file) throws FileNotFoundException {
        fileTransfer = new FileTransfer(file);
        this.connection = connection;
        connection.send(OFFER_FILE_PRE, fileTransfer.getId());
        connection.addLog(SEND_LOG + " transfer approve request "+ fileTransfer.getId());
        connection.setState(ConnectionState.FILE_TRANSFER);
    }

    @Override
    public void receive(byte[] bytes) {
        var prefix = connection.receivePrefix(bytes);
        if(prefix.equals(ACCEPT_FILE_PRE)){
            thread = new Thread(this::sendTransferThread);
            thread.start();
        }
        else if(prefix.equals(REJECT_FILE_PRE)){
            connection.setState(ConnectionState.IDLE);
        }
    }
    public void sendTransferThread(){
        while(fileTransfer.getCounter() < fileTransfer.getLength()){
            try {
                connection.send(FILE_PRE,fileTransfer.readBytes(connection.dataSize()-2));
                connection.addTransferLog(SEND_LOG+ fileTransfer.getId(), 100.0*fileTransfer.getCounter()/fileTransfer.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connection.removeTransferLog(SEND_LOG+fileTransfer.getId());
        connection.addLog(SEND_LOG+"successful transfer of "+fileTransfer.getId());
        connection.setState(ConnectionState.IDLE);
        try {
            fileTransfer.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
