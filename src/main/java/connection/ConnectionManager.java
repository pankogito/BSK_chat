package connection;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ConnectionManager implements ConnectionListner{

    private List<Connection> connections;

    public ConnectionManager() {
        connections = new LinkedList<>();
    }

    @Override
    public void recordOpen(Socket socket) {
        connections.add(new Connection(socket));
    }
}
