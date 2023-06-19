package transfer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class InMemoTransfer extends MultiFrameTransfer{
    byte[] bytes;
    public InMemoTransfer(String id,byte[] bytes) {
        super(id,bytes.length);
        this.bytes = bytes;
    }
    public InMemoTransfer(String id,int l){
        super(id,l);
        bytes = new byte[l];
    }

    @Override
    public byte[] readBytes(int i) throws IOException, NullPointerException {
        byte[] b = Arrays.copyOfRange(bytes, (int) counter, (int) (counter+i));
        counter += i;
        return b;
    }

    @Override
    public void writeBytes(byte[] b) throws IOException, NullPointerException {
        int max = (int) (counter + b.length);
        if(length < max)
            max = (int) length;
        for(int i= 0;counter+i<max;i++)
            bytes[(int) (counter+i)]=b[i];
        counter = max;
    }
    public byte[] getBytes(){
        return bytes;
    }
    @Override
    public void close() throws IOException {}

}
