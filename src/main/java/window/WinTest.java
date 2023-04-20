package window;

import javax.swing.*;

public class WinTest {

    public static void main(String[] args) {
        var log = new LoginWindow(null);
        log.setVisible(true);
        log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
