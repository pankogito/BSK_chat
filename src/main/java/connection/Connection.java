package connection;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import static java.util.Arrays.copyOf;


public class Connection {
    public static final int FRAME_SIZE=1024;
    public static final String SEND_LOG=">>",RECEIVE_LOG="<<",
            MESSAGE_PRE = "MG",
            OFFER_FILE_PRE = "FO",ACCEPT_FILE_PRE = "FA",REJECT_FILE_PRE = "FR",FILE_PRE = "FL";
    private Socket socket;
    private ConnectionState state;
    public final String name;
    private Thread receiveThread,sendThread;
    private String logs;


    private HashMap<String,String> transferLogs;
    public UpdateListner logListner;
    public ConfirmationListner conListner;
    private FileTransfer sended,received;

    public Connection(Socket socket) {
        this.socket = socket;
        name = socket.getInetAddress().toString() +":"+ socket.getPort()+"\n";
        logs = "connceted to " + name;
        transferLogs = new HashMap<>();
        state = ConnectionState.TO_REPLACE;
        receiveThread = new Thread(this::receiveRun);
        receiveThread.start();
    }

    public void receiveRun(){
        try {
            var in = socket.getInputStream();
            while(true){

                if(in.available() >= FRAME_SIZE){
                    receive(in.readNBytes(FRAME_SIZE));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void addTransferLog(String id,double value){
        transferLogs.put(id,id+" "+Double.toString(value).substring(0,4)+'%');
        if(logListner != null)
            logListner.logUpdate();
    }
    public void removeTransferLog(String id){
        transferLogs.remove(id);
        if(logListner != null)
            logListner.logUpdate();
    }
    public void addLog(String log){
        logs += log +"\n";
        if(logListner != null)
            logListner.logUpdate();
    }
    public String getLogs(){
        return logs;
    }

    public HashMap<String, String> getTransferLogs() {
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
        if(sended == null){
            try {
                sended = new FileTransfer(file);
                send(OFFER_FILE_PRE, sended.id);
                addLog(SEND_LOG + " transfer approve request "+ sended.id);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void send(String prefix,String content){
        byte[] content_bytes = content.getBytes(StandardCharsets.UTF_8);
        send(prefix,content_bytes);
    }
    public void send(String prefix,byte[] content_bytes){
        byte[] prefix_bytes = prefix.getBytes(StandardCharsets.UTF_8);
        byte[] message = Arrays.copyOf(prefix_bytes,FRAME_SIZE);
        int i = prefix_bytes.length;
        for(var b: content_bytes){
            message[i] = b;
            i++;
        }
        try {
            //ENCRYPTION HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            socket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receive(byte[] bytes){
        bytes = decrypt(bytes);
        String prefix = receivePrefix(bytes);
        if(prefix.equals(MESSAGE_PRE)){
            addLog(RECEIVE_LOG+" "+receiveStringContent(bytes));
        }
        else if (prefix.equals(OFFER_FILE_PRE)){
            receiveFileOffer(receiveStringContent(bytes));
        }
        else if (prefix.equals(REJECT_FILE_PRE)){
            String id = receiveStringContent(bytes);
            if(sended.id.equals(id)){
                sended = null;
                addLog(RECEIVE_LOG+"rejected "+id);
            }
        }
        else if (prefix.equals(ACCEPT_FILE_PRE)){
            String id = receiveStringContent(bytes);
            if(sended.id.equals(id)){

                addLog(RECEIVE_LOG+"accepted "+id);
                sendThread = new Thread(this::sendFileThread);
                sendThread.start();
            }
        }
        else if(prefix.equals(FILE_PRE)){
            try {
                received.writeBytes(receiveBytesContent(bytes));
                if(received.ended()){
                    removeTransferLog(RECEIVE_LOG+received.id);
                    addLog(RECEIVE_LOG+"successful transfer of "+received.id);
                    received = null;
                }else {
                    addTransferLog(RECEIVE_LOG+ received.id,100.0*received.counter/received.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void receiveFileOffer(String id){
        addLog(RECEIVE_LOG+"transfer approve request "+id);
        if(received == null){
            if(conListner.confirm("Accept transfer " +id + " from "+name)){


                var chooser = new JFileChooser(".");
                if(chooser.showOpenDialog(null)== JFileChooser.APPROVE_OPTION){
                    try {
                        received = new FileTransfer(id,chooser.getSelectedFile());


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    send(ACCEPT_FILE_PRE,id);
                    addLog(SEND_LOG + "accept "+id);
                }
            }
            else{
                send(REJECT_FILE_PRE,id);
                addLog(SEND_LOG+"reject "+id);
            }

        }
        else if(!received.id.equals(id)){
            send(REJECT_FILE_PRE,id);
            addLog(SEND_LOG+"auto reject "+id);

        }
    }

    public byte[]decrypt(byte[] bytes){
        //DECRYPTION HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return bytes;
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
    public void sendFileThread(){
        while(sended.counter < sended.length){
            try {
                send(FILE_PRE,sended.readBytes(FRAME_SIZE-2));
                addTransferLog(SEND_LOG+ sended.id, 100.0*sended.counter/sended.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        removeTransferLog(SEND_LOG+sended.id);
        addLog(SEND_LOG+"successful transfer of "+received.id);
        try {
            sended.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sended = null;
    }
}
