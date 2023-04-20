package window;

import aplication.BasicLoginListner;
import aplication.LoginListner;

import javax.swing.*;

public class WinTest {

    public static void main(String[] args) {
        BasicLoginListner listner = new BasicLoginListner();
        var log = new LoginWindow(listner);
        log.setVisible(true);
        log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
