package network;

import cipher.AsymmetricCipher;
import connection.ConnectionListner;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    private ServerSocket server;

    private ConnectionListner listner;

    private final Map<InetAddressAndPort, Socket> map;

    public synchronized Map<InetAddressAndPort, Socket> getMap() {
        return map;
    }

    private Thread acceptHandlingThread;

    public NetworkManager(int port,ConnectionListner listner) throws IOException {
        this.listner = listner;
        map = new HashMap<>();

        startServer(port);

    }
    public void startServer(int port) throws IOException {
        if(acceptHandlingThread != null){
            acceptHandlingThread.interrupt();
        }
        if(port > 0){
            acceptHandlingThread = new Thread(this::acceptHandling);
            server = new ServerSocket(port);
            acceptHandlingThread.start();
        }
    }

    public synchronized void putSocket(InetAddressAndPort address,Socket socket,AsymmetricCipher assumption){
        if(! map.containsKey(address) || map.get(address).isClosed()){

            try {
                socket.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            map.put(address,socket);
            if(listner != null)
                listner.recordOpen(socket,assumption);
            System.out.println("Socket Open "+address);
        }
        else{
            System.out.println("Socket Already Exist "+address);
        }

    }
    public synchronized void openSocket(InetAddressAndPort address, AsymmetricCipher assumption) throws IOException{
        var socket = new Socket(address.inetAddress,address.port);
        putSocket(address,socket,assumption);
    }
    public synchronized void closeSocket(InetAddressAndPort address) throws IOException{
        if(map.containsKey(address)){
            map.get(address).close();
            map.remove(address);
        }
    }

    public void acceptHandling(){
        while(true){
            try {
                var socket = server.accept();
                var address = new InetAddressAndPort(socket.getInetAddress(),socket.getPort());


                putSocket(address,socket,null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
