
package views;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import calc.*;
import rml.*;

public class BaseEditor extends MyTextArea2 {

    private String font_face = "Serif";
    private int font_family = 0;
    private int font_size = 12;
    String alias;
    String visible;
    String exp;
    Calc calc;
    PopupMenu pm = new PopupMenu();
    private boolean popAdded = false;
    
    Hashtable aliases = null;
    Editor parent = null;

    public BaseEditor() {
        super();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);        
    }

    public void init(Proper prop, Hashtable aliases) {
        Integer ip;
        String sp;
        this.aliases = aliases;        
        if (prop==null) return;

        sp = (String)prop.get("EDITABLE");
        if (sp!=null) {
            System.out.println("property EDITABLE ="+sp);
            setEditable(sp);
        }

        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) font_face = sp;
        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) font_family = ip.intValue();
        ip = ((Integer)prop.get("FONT_SIZE"));
        if (ip!=null) font_size = ip.intValue();
        setFont(new Font(font_face,font_family,font_size));
        //fm = getFontMetrics(scaleFont);

        sp = ((String)prop.get("FONT_COLOR"));
        if (sp!=null) setForeground(UTIL.getColor(sp));
        else setForeground(Color.black);

        sp = ((String)prop.get("BG_COLOR"));
        if (sp!=null) setBackground(UTIL.getColor(sp));
        else setBackground(Color.white);

        sp = (String)prop.get("ALIAS");
        if (sp!=null) alias = sp;

        sp = (String)prop.get("VISIBLE");
        if (sp!=null) visible = sp;

        sp = (String)prop.get("EXP");
        if (sp!=null) {
            exp = sp;
            calc = new Calc(exp);
        }

        sp = (String)prop.get("VALUE");
        if (sp!=null) setText(sp);

        if (visible!=null && visible.equals("NO")) setVisible(false);
    }

    void setEditable(String ed) {
        if (ed.equals("READONLY")) {
            setEditable(false);
        }else setEditable(true);
    }

    public void processKeyEvent(KeyEvent e) {
        if (e.getID()==KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
                if (calc!=null)
                try {
                    calc.eval(aliases);
                }catch(Exception ex) {
                    System.out.println("views.BaseEditor::processKeyEvent : "+ex);
                }
                return;
            }
            if (parent!=null) parent.processShortcut(e.getKeyCode(), e.getModifiers());
        }
    }

    public void processMouseEvent(MouseEvent e) {      
        if (e.isPopupTrigger()) {
            if (parent!=null) {
                PopupMenu pm = parent.getPopupMenu(); 
                if (pm!=null) {
                    pm.show(parent,e.getX(),e.getY());
                }
            }
            e.consume();
        }
    } 
}
