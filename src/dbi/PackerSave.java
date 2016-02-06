package dbi;
import java.io.*;
public class PackerSave implements loader.Callback{

   Object data; 

   public PackerSave(Object d){
        data = d;
   }

    public Object callback(Object o) throws Exception{
        try {
            ByteArrayOutputStream bos = (ByteArrayOutputStream)o; 
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.close();
            System.out.println("Object's packed size = "+bos.size());
        }catch(Exception e) {System.out.println("dbi.PackerSave :"+e);}
        return null;
   }
}