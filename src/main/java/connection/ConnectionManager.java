package connection;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ConnectionManager implements ConnectionListner{

    private List<Connection> connections;
    public UpdateListner listner;

    public ConnectionManager() {
        connections = new LinkedList<>();
    }
    @Override
    public void recordOpen(Socket socket) {
        connections.add(new Connection(socket));
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
