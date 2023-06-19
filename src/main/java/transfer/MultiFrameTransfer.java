package transfer;

import java.io.*;
import java.util.Arrays;

public abstract class MultiFrameTransfer {
    String id;
    long length,counter;
    File file;

    public String getId() {
        return id;
    }

    public long getLength() {
        return length;
    }

    public long getCounter() {
        return counter;
    }
    public MultiFrameTransfer(String id,long l){
        this.id = id;
        length = l;
        counter = 0;
    }
    public abstract  byte[] readBytes(int i) throws IOException,NullPointerException;
    public abstract void writeBytes(byte[] b) throws IOException,NullPointerException;
    public abstract void close() throws IOException;
    public boolean ended(){
        return counter == length;
    }

}
