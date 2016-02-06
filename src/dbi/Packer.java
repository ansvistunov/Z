package dbi;
import java.io.*;
public interface Packer{
    public Object unpack(InputStream is);
    public InputStream pack(Object o);
    }