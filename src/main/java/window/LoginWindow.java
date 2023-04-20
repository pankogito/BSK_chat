package window;

import aplication.LoginListner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class LoginWindow extends JFrame {
    private JLabel userLabel,passLabel;
    private JTextField user;
    private JPasswordField password;
    private JButton accept;
    private LoginListner listner;
    public LoginWindow(LoginListner listner){
        setLayout(null);
        setSize(200,150);
        userLabel = new JLabel("user:");
        userLabel.setSize(100,20);
        userLabel.setLocation(20,20);
        add(userLabel);

        user = new JTextField();
        user.setSize(100,20);
        user.setLocation(70,20);
        add(user);

        passLabel = new JLabel("pass:");
        passLabel.setSize(100,20);
        passLabel.setLocation(20,50);
        add(passLabel);

        password = new JPasswordField();
        password.setSize(100,20);
        password.setLocation(70,50);
        add(password);

        accept = new JButton("accept");
        accept.setSize(100,20);
        accept.setLocation(20,80);
        accept.addActionListener(this::acceptAction);
        add(accept);

        this.listner = listner;
    }

    public void acceptAction(ActionEvent e){
        listner.action(user.getText(),password.getPassword());
    }
}
