package connection;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connection {

    private Socket socket;
    private ConnectionState state;
    private Thread thread;

    public Connection(Socket socket) {
        this.socket = socket;
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
}
