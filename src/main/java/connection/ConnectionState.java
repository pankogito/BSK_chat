package connection;

public enum ConnectionState {
    UNSAFE,
    IDLE,
    CIPHER_NEGOTIATION,
    FILE_TRANSFER,
}
