package connection.fileTransferProcedure;

import connection.Connection;
import connection.ConnectionState;
import transfer.FileTransfer;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import static connection.Connection.*;

public class FileReceive implements FileTransferProcedure{

    private Connection connection;
    private FileTransfer transfer;

    public FileReceive(Connection connection,String id) {
        this.connection = connection;
        connection.addLog(RECEIVE_LOG + "transfer approve request " + id);
        connection.setState(ConnectionState.FILE_TRANSFER);
        var chooser = new JFileChooser(".");
        if (connection.conListner.confirm("Accept transfer " + id + " from " + connection.name) &&
                    chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                    transfer = new FileTransfer(id, chooser.getSelectedFile());
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
            connection.send(ACCEPT_FILE_PRE, id);
            connection.addLog(SEND_LOG + "accept " + id);
            }
        else {
            connection.send(REJECT_FILE_PRE, id);
            connection.addLog(SEND_LOG + "reject " + id);
            connection.setState(ConnectionState.IDLE);
        }
    }

    @Override
    public void receive(byte[] bytes) {
        if(connection.receivePrefix(bytes).equals(FILE_PRE)){
            try {
                transfer.writeBytes(connection.receiveBytesContent(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(transfer.ended()){
                try {
                    transfer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.removeTransferLog(SEND_LOG+transfer.getId());
                connection.addLog(SEND_LOG+"successful transfer of "+transfer.getId());
                connection.setState(ConnectionState.IDLE);
            }
            else{
                connection.addTransferLog(SEND_LOG+ transfer.getId(), 100.0*transfer.getCounter()/transfer.getLength());
            }
        }
    }
}
