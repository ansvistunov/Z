
package views;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

public class MyTextArea extends TextArea implements ClipboardOwner{

    Clipboard clipboard = null;
    private boolean isMustDie = false;
    public boolean multiLine = false;

    public MyTextArea() {//однострочная TextArea
        this("");
    }
    public MyTextArea(int f) {//многострочная TextArea
        this("", 0);
    }
    public MyTextArea(String text) {//однострочная TextArea
        super(text,1,255,TextArea.SCROLLBARS_NONE);
        clipboard = getToolkit().getSystemClipboard();
        Properties prop  = System.getProperties();
        String osname = (String)prop.get("os.name");
        if (osname!=null) osname = osname.toUpperCase();
        if (osname.indexOf("WINDOWS")>=0) isMustDie = true;
    }
    
    public MyTextArea(String text,int f) {//многострочная TextArea
        super(text);
        this.multiLine = true;
        //addKeyListener(this);
        clipboard = getToolkit().getSystemClipboard();
        Properties prop  = System.getProperties();
        //System.out.println("system properties is "+);
        String osname = (String)prop.get("os.name");
        if (osname!=null) osname = osname.toUpperCase();
        if (osname.indexOf("WINDOWS")>=0) isMustDie = true;
    }


    public boolean keyDown(Event e,int key) {    
        if (key==((int)'C')-64){
            if (!isMustDie) {
                copy();
                return true;
            }
            else return false;
        }            
        if (key==((int)'V')-64){
            if (!isMustDie){
                paste();
                return true;
            }
            else return false;
        }
        if (key==((int)'X')-64){
            if (!isMustDie) {
                cut();
                return true;
            }
            else return false;
        }
        if (key==10 && (e.modifiers & Event.CTRL_MASK)!=0){            
            if (!multiLine) return true;
            else {
                if (!isMustDie) {
                    insert("\n",getCaretPosition()); 
                    return true;
                }
                else return false;
            }
        }
        return false;
    }

    void copy() {
        String text = getText();
        char[] ch = text.toCharArray();
        int beg  = getSelectionStart();
        int end = getSelectionEnd();
        String sel = null;
        try{sel=getSelectedText();}catch(Exception e) {}
        System.out.println("selection="+sel);
        if (sel==null || sel.equals("")) return;
        StringSelection ss = new StringSelection(sel);
        if (clipboard!=null) clipboard.setContents(ss, this);

    }
    void cut() {
        copy();
        replaceRange("",getSelectionStart(),getSelectionEnd());
    }

    void paste(){
        if (clipboard!=null) {
            Transferable tr = clipboard.getContents(this);
            if (tr!=null) System.out.println("tr="+tr);
            String data = null;
            StringReader sr=null;
            try{
                if (tr!=null && tr.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
                    data = (String)tr.getTransferData(DataFlavor.stringFlavor);
                    System.out.println("return String data");
                }
                else
                if (tr!=null && tr.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
                    sr = (StringReader)tr.getTransferData(DataFlavor.plainTextFlavor);
                    if (sr!=null) {
                        int count=0;
                        try{
                            while(true) {sr.read();count++;}
                        }catch(IOException e){}
                        char[] ch = null;
                        if (count>0) {
                            ch = new char[count];
                            try {
                                sr.read(ch,0,ch.length);
                            }catch(IOException e){}
                            data = new String(ch);
                        }
                    }
                }
            }catch(Exception e){/*System.out.println("ex="+e);*/}
            if (data!=null) {
                System.out.println("data from clipboard: "+data);
                insert(data,getCaretPosition());
            }
        }
    }

    public void lostOwnership(Clipboard clipboard,Transferable contents) {
        //System.out.println("lostOwnership called");
    }


}

   
