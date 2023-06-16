package connection;

import java.io.*;
import java.util.Arrays;

public class FileTransfer {
    String id;
    long length,counter;
    File file;
    OutputStream write;
    InputStream read;
    byte[] hash;

    public FileTransfer(File f) throws FileNotFoundException {
        read = new FileInputStream(f);
        file = f;
        length = file.length();
        counter = 0;
        id = file.getName() + " " +length +"B";

        //TODO HASH
    }
    public FileTransfer(String id,File f) throws FileNotFoundException{
        this.id = id;
        length = Long.parseLong(id.substring(id.lastIndexOf(' ')+1,id.lastIndexOf('B')));
        write = new FileOutputStream(f);
        file = f;
        counter = 0;
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
        if(read != null)
            read.close();
        else
            write.close();
    }
    public boolean ended(){
        return counter == length;
    }

}
