package connection;

import cipher.AsymmetricCipher;
import cipher.SymmetricCipher;
import connection.cipherNegotiation.CipherNegotiation;
import connection.cipherNegotiation.CipherReceive;
import connection.cipherNegotiation.CipherSend;
import connection.fileTransferProcedure.FileReceive;
import connection.fileTransferProcedure.FileSend;
import connection.fileTransferProcedure.FileTransferProcedure;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.copyOf;


public class Connection {
    public static final int FRAME_SIZE=1024;
    public static final String SEND_LOG=">>",RECEIVE_LOG="<<",
            MESSAGE_PRE = "MG",
            OFFER_FILE_PRE = "FO",ACCEPT_FILE_PRE = "FA",REJECT_FILE_PRE = "FR",FILE_PRE = "FL",
            CIPHER_PRE = "CP",ACCEPT_CIPHER_PRE = "CC",REJECT_CIPHER_PRE = "CR";
    private Socket socket;

    private AsymmetricCipher owner,client;
    private List<AsymmetricCipher> clients;
    private SymmetricCipher current;

    private ConnectionState state;
    public final String name;

    private Thread receiveThread,sendThread;

    private String logs;
    private HashMap<String,String> transferLogs;

    public UpdateListner logListner,stateListner;
    public ConfirmationListner conListner;

    private CipherNegotiation cipherNegotiation;
    private FileTransferProcedure fileTransferProcedure;

    public synchronized ConnectionState getState() {
        return state;
    }

    public synchronized void setCurrent(SymmetricCipher current) {
        this.current = current;
    }

    public synchronized void setState(ConnectionState state){
        this.state =state;
        if(stateListner != null)
            stateListner.logUpdate();
    }
    public Connection(Socket socket,AsymmetricCipher owner){
        this.socket = socket;
        this.owner = owner;

        name = socket.getInetAddress().toString() +":"+ socket.getPort()+"\n";
        logs = "connceted to " + name;
        transferLogs = new HashMap<>();
        state = ConnectionState.IDLE; //after adding cryptography replace with unsafe.
        receiveThread = new Thread(this::receiveRun);
        receiveThread.start();

    }
    public Connection(Socket socket,AsymmetricCipher owner,AsymmetricCipher client) {
        this(socket,owner);
        this.client = client;
        boolean cbc = JOptionPane.
                showConfirmDialog(null,"Use CBC?")==JOptionPane.OK_OPTION;
        cipherNegotiation = new CipherSend(this,cbc,owner,client);
        setState(ConnectionState.UNSAFE);
    }
    public Connection(Socket socket,AsymmetricCipher owner,List<AsymmetricCipher> clients) {
        this(socket,owner);
        setState(ConnectionState.UNSAFE);
        this.cipherNegotiation = new CipherReceive(this,owner,clients);
    }

    public void receiveRun(){
        try {
            var in = socket.getInputStream();
            while(true){

                if(in.available() >= FRAME_SIZE){
                    byte[] bytes = in.readNBytes(FRAME_SIZE);
                    if(current == null){
                        cipherNegotiation.receive(bytes);
                    }
                    else {
                        bytes = current.decrypt(bytes);
                        if(state == ConnectionState.UNSAFE){
                            cipherNegotiation.receive(bytes);
                        }
                        else if(state == ConnectionState.IDLE){
                            var prefix = receivePrefix(bytes);
                            if(prefix.equals(OFFER_FILE_PRE)){
                                fileTransferProcedure= new FileReceive(this,receiveStringContent(bytes));
                            }
                            else{
                                defaultReceive(bytes);
                            }
                        }
                        else if (state == ConnectionState.FILE_TRANSFER){
                            fileTransferProcedure.receive(bytes);
                            defaultReceive(bytes);
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public synchronized void addTransferLog(String id,double value){
        transferLogs.put(id,id+" "+Double.toString(value).substring(0,4)+'%');
        if(logListner != null)
            logListner.logUpdate();
    }
    public synchronized void removeTransferLog(String id){
        transferLogs.remove(id);
        if(logListner != null)
            logListner.logUpdate();
    }
    public synchronized void addLog(String log){
        logs += log +"\n";
        if(logListner != null)
            logListner.logUpdate();
    }
    public synchronized String getLogs(){
        return logs;
    }

    public synchronized HashMap<String, String> getTransferLogs() {
        return transferLogs;
    }

    public void setCipherSetting(boolean cbc){
        if(cbc)
            addLog(SEND_LOG + " cipher change request CBC");
        else
            addLog(SEND_LOG + " cipher change request ECB");
    }
    public void sendMessage(String message){
        send(MESSAGE_PRE,message);
        addLog(SEND_LOG+" "+message);
    }
    public void askForFileTransfer(File file){
        try {
                fileTransferProcedure = new FileSend(this,file);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    public void send(String prefix,String content){
        byte[] content_bytes = content.getBytes(StandardCharsets.UTF_8);
        send(prefix,content_bytes);
    }
    public synchronized void send(String prefix,byte[] content_bytes){
        byte[] prefix_bytes = prefix.getBytes(StandardCharsets.UTF_8);
        byte[] message = Arrays.copyOf(prefix_bytes,FRAME_SIZE);
        int i = prefix_bytes.length;
        for(var b: content_bytes){
            message[i] = b;
            i++;
        }
        try {
            if(current != null)
                message = current.encrypt(message);
            socket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int dataSize(){
        if(current==null ||!current.isCbc())
            return FRAME_SIZE;
        else
            return FRAME_SIZE-SymmetricCipher.IV_SIZE;
    }

    public synchronized void defaultReceive(byte[] bytes){
        String prefix = receivePrefix(bytes);
        if(prefix.equals(MESSAGE_PRE)){
            addLog(RECEIVE_LOG+" "+receiveStringContent(bytes));
        }
    }

    public String receivePrefix(byte[] bytes){
        return new String(Arrays.copyOfRange(bytes,0,2),StandardCharsets.UTF_8);
    }
    public String receiveStringContent(byte[] bytes){
        var s =new String(Arrays.copyOfRange(bytes,2,bytes.length),StandardCharsets.UTF_8);
        return s.substring(0,s.indexOf('\0'));
    }
    public byte[] receiveBytesContent(byte[] bytes){
        return Arrays.copyOfRange(bytes,2,bytes.length);
    }
}
