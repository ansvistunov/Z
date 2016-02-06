
package views.printing;

import loader.GLOBAL;
import java.awt.*;
import java.net.*;
import java.io.*;

public class RPrintJob extends PrintJob{
    //BufferedOutp
    DataOutputStream dos = null;
    RGraphics g = null;
    String pname="DEFAULT";
    String orientation = "PORTRAIT";
    protected boolean first = true;//=true до первого запроса getGraphics()
    protected Socket sok = null;
    
    public RPrintJob(String host, int port,String pname, String or,int bsize) throws Exception
    {
        //String rp = GLOBAL.pr(GLOBAL.PRINTING_HOST,"");
        
        try{
            sok = new Socket(InetAddress.getByName(host),port);
            dos = new DataOutputStream(
                new BufferedOutputStream(sok.getOutputStream(),bsize)); 
            g = new RGraphics(dos);
            if (pname!=null) this.pname=pname;
            if (or!=null) orientation = or;
        }catch(Exception e){
            throw new Exception("can't create RRrintJob");
        } 
    }
    
    public void end(){
        //System.out.println("!!!!inside RPrintJob.end()");
        try {
            dos.writeUTF(c.END_PRINTING+","+"1"+","+"1");
        }catch(Exception e){}
        //на всякий случай
        
        if (g!=null) g.flush();
        try {
            dos.close();
            sok.close();
        }catch(Exception e){};        
    }
    
    public void finalize(){
    }
    
    public Graphics getGraphics(){
        if (first) {
            first=false;
            try {
            dos.writeUTF(c.BEGIN_PRINTING+","+"1"+","+"1"+",\""
                +pname+"\""+orientation);
            }catch(Exception e){}
        }
        return g;
    }
    
    public Dimension getPageDimension(){return null;}
    
    public int getPageResolution(){return 0;}
    
    public boolean lastPageFirst(){return false;}
}
