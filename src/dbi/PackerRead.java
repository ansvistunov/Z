package dbi;
import java.io.*;
public class PackerRead implements loader.Callback{

        public Object callback(Object o) throws Exception{
                InputStream is = (InputStream)o;
                Object ret=null;
                if (is==null) return null;
                try {
                    ObjectInputStream ois = new ObjectInputStream(is);
                    ret = ois.readObject();
                }catch(Exception e) {System.out.println("dbi.PackerRead : "+e);}
                return ret;
        }
}