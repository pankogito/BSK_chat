package connection;

import java.net.Socket;

public interface ConnectionListner {
    void recordOpen(Socket socket);
}
