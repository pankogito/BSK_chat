package transfer;

import java.io.*;
import java.util.Arrays;

public class FileTransfer extends MultiFrameTransfer{
    File file;
    OutputStream write;
    InputStream read;


    public FileTransfer(File f) throws FileNotFoundException {
        super(f.getName() + " " +f.length() +"B",f.length());
        read = new FileInputStream(f);
        file = f;
    }
    public FileTransfer(String id,File f) throws FileNotFoundException{
        super(id,Long.parseLong(id.substring(id.lastIndexOf(' ')+1,id.lastIndexOf('B'))));
        write = new FileOutputStream(f);
        file = f;
    }
    public byte[] readBytes(int i) throws IOException,NullPointerException {
        if(read.available() < i)
            i = read.available();
        byte[] b = read.readNBytes(i);
        counter += i;
        return b;
    }
    public void writeBytes(byte[] b) throws ArrayIndexOutOfBoundsException, IOException,NullPointerException {
        if(length < counter + b.length) {
            b = Arrays.copyOfRange(b,0, (int) (length-counter));
        }
        write.write(b);
        counter += b.length;
    }
    public void close() throws IOException {
        if(write != null)
            write.close();
        if(read != null)
            read.close();
    }
}
