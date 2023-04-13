package network;

import java.net.InetAddress;
import java.util.Objects;

public class InetAddressAndPort {

    public final InetAddress inetAddress;
    public final int port;


    public InetAddressAndPort(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InetAddressAndPort that = (InetAddressAndPort) o;
        return port == that.port && Objects.equals(inetAddress, that.inetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress, port);
    }

    @Override
    public String toString() {
        return "InetAddressAndPort{" +
                "inetAddress=" + inetAddress +
                ", port=" + port +
                '}';
    }
}
