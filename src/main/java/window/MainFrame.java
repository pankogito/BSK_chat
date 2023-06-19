package window;

import cipher.AsymmetricCipher;
import connection.Connection;
import connection.ConnectionManager;
import connection.ConnectionState;
import network.InetAddressAndPort;
import network.NetworkManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class MainFrame extends JFrame {

    JComboBox<String> userList;
    JTextField address,port,message;
    JButton connect,disconnect,send,sendFile,listen;
    JTextArea textArea;
    JScrollPane textScroll;
    JCheckBox cbc;

    private ConnectionManager connectionManager;
    private Connection con;
    private NetworkManager networkManager;
    public MainFrame(NetworkManager networkManager,ConnectionManager connectionManager){
        this.networkManager = networkManager;
        this.connectionManager = connectionManager;
        connectionManager.listner = this::userUpdate;

        setLayout(null);
        setSize(400,440);

        port = new JTextField("5555");
        port.setSize(200,20);
        port.setLocation(20,20);
        add(port);

        listen = new JButton("listen");
        listen.setSize(100,20);
        listen.setLocation(230,20);
        listen.addActionListener(this::listenAction);
        add(listen);

        address = new JTextField("localhost:5555");
        address.setSize(200,20);
        address.setLocation(20,50);
        add(address);

        connect = new JButton("connect");
        connect.setSize(100,20);
        connect.setLocation(230,50);
        connect.addActionListener(this::connectAction);
        add(connect);

        userList = new JComboBox<>();
        userList.addItem("aaaa0");
        userList.addItem("aaaa1");
        userList.addItem("aaaa2");
        userList.setSize(200,20);
        userList.setLocation(20,80);
        userList.addActionListener(this::setUserAction);
        add(userList);

        disconnect = new JButton("disconnect");
        disconnect.setSize(100,20);
        disconnect.setLocation(230,80);
        disconnect.addActionListener(this::disconnectAction);
        add(disconnect);

        textArea = new JTextArea("A\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        textArea.setLineWrap(true);
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(Color.BLACK);
        textScroll = new JScrollPane(textArea);

        textScroll.setSize(310,200);
        textScroll.setLocation(20,110);
        add(textScroll);

        message = new JTextField("message");
        message.setSize(200,20);
        message.setLocation(20,320);
        add(message);

        send = new JButton("send");
        send.setSize(100,20);
        send.setLocation(230,320);
        send.addActionListener(this::sendAction);
        add(send);

        sendFile = new JButton("send file");
        sendFile.setSize(100,20);
        sendFile.setLocation(230,350);
        sendFile.addActionListener(this::sendFileAction);
        add(sendFile);

        cbc = new JCheckBox("CBC");
        cbc.setSize(100,20);
        cbc.setLocation(15,350);
        cbc.addActionListener(this::cipherChangeAction);
        add(cbc);

    }
    public void setCurrentConnection(Connection connection){
        if(con != null){
            con.logListner = null;
            con.stateListner = null;
        }

        con = connection;
        con.logListner = this::logUpdate;
        con.conListner = this::userConfirm;
        con.stateListner = this::stateUpdate;
        logUpdate();
        stateUpdate();
    }
    public void listenAction(ActionEvent e){
        try {
            networkManager.startServer(Integer.parseInt(port.getText()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void setUserAction(ActionEvent e){
        int i = userList.getSelectedIndex();
        if( i < 0)
            return;
        con = connectionManager.getConnection(i);
        setCurrentConnection(con);
        logUpdate();
    }
    public void connectAction(ActionEvent e){
        String addressString = address.getText();
        int port = 5555;
        if(addressString.contains(":")){
            int pose = addressString.lastIndexOf(":");
            port = Integer.parseInt(addressString.substring(pose+1));
            addressString = addressString.substring(0,pose);
        }
        var chooser = new JFileChooser(".");
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            try {
                var reader = new FileInputStream(chooser.getSelectedFile());
                var assumption = new AsymmetricCipher(reader.readAllBytes());
                var inet = new InetAddressAndPort(InetAddress.getByName(addressString),port);
                networkManager.openSocket(inet,assumption);

            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } catch (SignatureException | InvalidKeyException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
    public void disconnectAction(ActionEvent e){

    }
    public void sendAction(ActionEvent e){
        con.sendMessage(message.getText());
    }
    public void sendFileAction(ActionEvent e){
        var fileChooser = new JFileChooser(".");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            var file = fileChooser.getSelectedFile();
            var question = "Send file:"+file.getPath().toString()+" to "+con.name+"?";
            var dialog = new JDialog();
            if (JOptionPane.showConfirmDialog(this,question)==JOptionPane.OK_OPTION){
                con.askForFileTransfer(file);
            }
        }
    }
    public void cipherChangeAction(ActionEvent e){
        con.setCipherSetting(cbc.isSelected());
    }
    public void logUpdate(){
        StringBuilder string = new StringBuilder(con.getLogs() + "\n--------------------------------------------------------\n");
        for( var s: con.getTransferLogs().values()){
            string.append(s).append("\n");
        }
        textArea.setText(string.toString());
    }
    public void stateUpdate(){
        var state = con.getState();
        System.out.println(state);
        if(state == ConnectionState.UNSAFE
                || state == ConnectionState.CIPHER_NEGOTIATION){
            send.setEnabled(false);
            sendFile.setEnabled(false);
            cbc.setRequestFocusEnabled(true);
        }
        else if(state == ConnectionState.IDLE){
            send.setEnabled(true);
            sendFile.setEnabled(true);
            cbc.setRequestFocusEnabled(true);
        }
        else if(state == ConnectionState.FILE_TRANSFER){
            send.setEnabled(true);
            sendFile.setEnabled(false);
            cbc.setRequestFocusEnabled(false);
        }

    }
    public void userUpdate() {
        userList.removeAllItems();
        for(var con: connectionManager.getConnections()){
            userList.addItem(con.name);
        }
    }
    public boolean userConfirm(String question){
        return JOptionPane.showConfirmDialog(this,question)==JOptionPane.OK_OPTION;
    }

}
