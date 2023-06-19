package connection;

import cipher.AsymmetricCipher;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ConnectionManager implements ConnectionListner{

    private List<Connection> connections;
    public UpdateListner listner;
    public AsymmetricCipher owner;
    public List<AsymmetricCipher> users;

    public ConnectionManager(AsymmetricCipher owner,List<AsymmetricCipher> users) {
        connections = new LinkedList<>();
        this.owner = owner;
        this.users = users;
    }
    @Override
    public void recordOpen(Socket socket, AsymmetricCipher assumption) {
        if (assumption == null)
            recordOpen(socket);
        else{
            connections.add(new Connection(socket, owner,
                    assumption));
            if(listner != null)
                listner.logUpdate();
        }
    }
    @Override
    public void recordOpen(Socket socket) {
        connections.add(new Connection(socket, owner,users));
        if(listner != null)
            listner.logUpdate();
    }
    public Connection getConnection(int index){
        return connections.get(index);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void removeConnection(int index){
        connections.remove(index);
        if(listner != null)
            listner.logUpdate();
    }
}
