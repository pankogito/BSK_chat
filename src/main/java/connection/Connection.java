package connection;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connection {

    private Socket socket;
    private ConnectionState state;
    public final String name;
    private Thread thread;
    private String logs;
    public UpdateListner logListner;

    public Connection(Socket socket) {
        this.socket = socket;
        name = socket.getInetAddress().toString() +":"+ socket.getPort()+"\n";
        logs = "connceted to " + name;
        state = ConnectionState.TO_REPLACE;
        thread = new Thread(this::receiveRun);
        thread.start();
    }

    public void receiveRun(){
        try {
            var in = socket.getInputStream();
            while(true){

                if(in.available() > 0){
                    System.out.println(in.available());
                    var string = new String(in.readNBytes(in.available()), StandardCharsets.UTF_8);
                    System.out.println("From "+socket.getPort()+" received: "+string);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void addLog(String log){
        logs += log +"\n";
        if(logListner != null)
            logListner.logUpdate();
    }
    public String getLogs(){
        return logs;
    }
    public void setCipherSetting(boolean cbc){
        if(cbc)
            addLog("=> cipher change request CBC");
        else
            addLog("=> cipher change request ECB");
    }
    public void sendMessage(String message){
        addLog("=> "+message);
    }
    public void askForFileTransfer(File file){
        addLog("=> transfer approve request "+ file.getName()+ " " + file.length()+"B");
    }
    public void transferFile(File file){
        addLog("=> transfer file "+ file.getName()+ " " + file.length()+"B");
    }
}
