
package views;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

public class MyTextArea2 extends TextArea implements ClipboardOwner{

    Clipboard clipboard = null;
    private boolean isMustDie = false;
    public boolean multiLine = false;

    public MyTextArea2() {
        this("");
    }

    public MyTextArea2(String text) {
        super(text);
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        clipboard = getToolkit().getSystemClipboard();
        Properties prop  = System.getProperties();
        String osname = (String)prop.get("os.name");
        if (osname!=null) osname = osname.toUpperCase();
        if (osname.indexOf("WINDOWS")>=0) isMustDie = true;
    }



    public void processKeyEvent(KeyEvent e) {
        if (e.getID()!=KeyEvent.KEY_PRESSED) return;
        if (e.getKeyCode()==KeyEvent.VK_C && e.isControlDown()){
            if (!isMustDie) {
                copy();
                e.consume();
            }
        }

        if (e.getKeyCode()==KeyEvent.VK_V && e.isControlDown()){
            if (!isMustDie){
                paste();
                e.consume();
            }
        }
        if (e.getKeyCode()==KeyEvent.VK_X && e.isControlDown()){
            if (!isMustDie) {
                cut();
                e.consume();
            }
        }
    }

    void copy() {
        /*String text = getText();
        char[] ch = text.toCharArray();
        int beg  = getSelectionStart();
        int end = getSelectionEnd();*/
        String sel = null;
        try{sel=getSelectedText();}catch(Exception e) {}
        //System.out.println("selection="+sel);
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
            String data = null;
            StringReader sr=null;
            try{
                if (tr!=null && tr.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
                    data = (String)tr.getTransferData(DataFlavor.stringFlavor);                    
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
                insert(data,getCaretPosition());
            }
        }
    }

    public void lostOwnership(Clipboard clipboard,Transferable contents) {
        //System.out.println("lostOwnership called");
    }
}
