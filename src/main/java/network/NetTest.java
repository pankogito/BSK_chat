package network;

import connection.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NetTest {

    public static void main(String[] args) {

        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Open Service on Port: ");
            var cons = new ConnectionManager();
            var net = new NetworkManager(Integer.parseInt(in.nextLine()),cons);
            System.out.println("Service Open");
            System.out.print("Connect? [Y/N] ");
            if(in.nextLine().equalsIgnoreCase("Y")){
                System.out.print("Connect to Port: ");
                var port = Integer.parseInt(in.nextLine());
                var inetAndPort = new InetAddressAndPort(
                        InetAddress.getByName("localhost"),
                        port
                );
                net.openSocket(inetAndPort);
            }


            while(true){
                System.out.print("Select to port: ");
                try {
                    var inet = new InetAddressAndPort(
                            InetAddress.getByName("localhost"),
                            Integer.parseInt(in.nextLine())
                    );
                    Socket socket = null;

                    synchronized (net){
                        if(net.getMap().containsKey(inet)){
                            socket = net.getMap().get(inet);
                        }
                    }
                    if(socket !=null){
                        System.out.print("Send to "+inet.port+": ");
                        socket.getOutputStream().write(in.nextLine().getBytes(StandardCharsets.UTF_8));
                    }
                    else{
                        System.out.println("Socket "+inet.port+" doesn't exist");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }




            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
