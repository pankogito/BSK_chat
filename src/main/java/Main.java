import aplication.BasicLoginListner;
import connection.ConnectionManager;
import network.NetworkManager;
import window.LoginWindow;
import window.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        var loginListner = new BasicLoginListner();
        var loginWindow = new LoginWindow(loginListner);
        loginWindow.setVisible(true);
        loginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while(loginListner.getUser() == null){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        loginWindow.setVisible(false);
        var user = loginListner.getUser();

        try {
            var connections = new ConnectionManager(user.getUser(), user.getTrusted());
            var network = new NetworkManager(-1,connections);
            var mainWindow = new MainFrame(network,connections);
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainWindow.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
