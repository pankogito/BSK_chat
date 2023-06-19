package window;

import cipher.AsymmetricCipher;
import connection.ConnectionManager;
import network.NetworkManager;

import aplication.BasicLoginListner;


import javax.swing.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

public class WinTest {

    public static void main(String[] args) {
        BasicLoginListner listner = new BasicLoginListner();
        var log = new LoginWindow(listner);
        log.setVisible(true);
        log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            var con = new ConnectionManager(new AsymmetricCipher(new byte[0],new byte[0]),new LinkedList<>());
            var net  = new NetworkManager(-1,con);
            var win = new MainFrame(net,con);
            win.setVisible(true);
            win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }


    }
}
