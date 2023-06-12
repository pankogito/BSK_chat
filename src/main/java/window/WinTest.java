package window;

import connection.ConnectionManager;
import network.NetworkManager;

import aplication.BasicLoginListner;
import aplication.LoginListner;


import javax.swing.*;
import java.io.IOException;

public class WinTest {

    public static void main(String[] args) {
        BasicLoginListner listner = new BasicLoginListner();
        var log = new LoginWindow(listner);
        log.setVisible(true);
        log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            var con = new ConnectionManager();
            var net  = new NetworkManager(-1,con);
            var win = new MainFrame(net,con);
            win.setVisible(true);
            win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
