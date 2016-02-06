
package views;

import java.awt.*;
import java.util.*;
import rml.*;
import calc.objects.*;
import calc.*;

public class CheckGroup extends Panel {
    Vector boxes = new Vector();

    boolean ha=false;//выравнивать кнопки по горизонтали или по вертикали |
    String alias;
    
    static int gap = 5;
    
    int l,t;

    public CheckGroup() {
        super();
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        alias = (String)prop.get("ALIAS");
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) l = ip.intValue();
        ip = (Integer)prop.get("TOP");
        if (ip!=null) t = ip.intValue();
        sp = (String)prop.get("ALIGNMENT");
        if (sp!=null && sp.equals("HORIZONTAL")) ha = true; else ha = false;
        sp = (String)prop.get("BG_COLOR");
        if (sp!=null) setBackground(UTIL.getColor(sp));
        else setBackground(Color.lightGray);
    }

    public void addChildren(Object[] objs) {
        if (objs==null||objs.length==0) return;
        int count=0;
        for (int i=0;i<objs.length;i++) {
            if (objs[i] instanceof CheckBox) {
                count++;
                boxes.addElement((CheckBox)objs[i]);
            }
        }
        if (boxes.size()==0) return;
        //пересчитываем размер пенели в зависимости от размеров и числа компонентов
        int w=0,h=0;
        
        Frame fix = new Frame();//фиктивный фрейм, нужен чтобы getPreferredSize
                                //вернул правильное значение
        fix.setLayout(new FlowLayout());
        for(int i=0;i<boxes.size();i++) {
            fix.add((CheckBox)boxes.elementAt(i));
        }
        fix.pack();
        for (int i=0;i<boxes.size();i++) {
            Dimension d = ((CheckBox)boxes.elementAt(i)).getPreferredSize();
            if (ha) {
                w+=d.width+gap;
                if (h<d.height) h = d.height;
            }else {
                if (w<d.width) w = d.width;
                h+=d.height+gap;
            }
        }
        fix.removeAll();
        fix.dispose();
        fix=null;
        
        setBounds(l,t,w,h);
        
        if (ha) setLayout(new GridLayout(1,boxes.size()));
        else setLayout(new GridLayout(boxes.size(),1));
        
        for (int i=0;i<boxes.size();i++) {
            CheckBox box = (CheckBox)boxes.elementAt(i);
            add(box);            
        }        
    }
}
