
package views;

import java.awt.*;
import java.util.*;
import rml.*;
import calc.objects.*;
import calc.*;

public class RadioGroup extends Panel implements GlobalValuesObject,class_method, class_type{
    Vector buttons = new Vector();

    boolean ha=false;//выравнивать кнопки по горизонтали или по вертикали k
    CheckboxGroup buttonGroup;
    String alias;
    protected int l=0,t=0;
    
    static int gap = 5;

    public RadioGroup() {
        super();
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;       
        
        buttonGroup = new CheckboxGroup();
        alias = (String)prop.get("ALIAS");
        ip = (Integer)prop.get("WIDTH");

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
            if (objs[i] instanceof RadioButton) {
                count++;
                buttons.addElement((RadioButton)objs[i]);
            }
        }        
        if (buttons.size()==0) return;
        
        
        //пересчитываем размер пенели в зависимости от размеров и числа компонентов
        int w=0,h=0;
        
        Frame fix = new Frame();//фиктивный фрейм, нужен чтобы getPreferredSize
                                //вернул правильное значение
        fix.setLayout(new FlowLayout());
        for(int i=0;i<buttons.size();i++) {
            fix.add((RadioButton)buttons.elementAt(i));
        }
        fix.pack();
        for (int i=0;i<buttons.size();i++) {
            Dimension d = ((RadioButton)buttons.elementAt(i)).getPreferredSize();
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
        if (ha) setLayout(new GridLayout(1,buttons.size()));
        else setLayout(new GridLayout(buttons.size(),1));
        
        for (int i=0;i<buttons.size();i++) {
            RadioButton button = (RadioButton)buttons.elementAt(i);
            button.setCheckboxGroup(buttonGroup);            
            add(button);            
        }
        for (int i=0;i<buttons.size();i++) {
            RadioButton button = (RadioButton)buttons.elementAt(i);
            if (button.check) button.setState(true);
            else button.setState(false);
        }
    }

    public Object method(String method,Object arg) throws Exception{
        if (method.equals("GETVALUE")) {
            for (int i=0;i<buttons.size();i++) {
                RadioButton button = (RadioButton)(buttons.elementAt(i));
                if ( button.getState())
                return button.getValue();
            }
            return null;
        }
        throw new RTException("HasNotMethod","method "+method+
        " not defined in class views.RadioGroup!");
    }
    
    public String type(){
        return "views.RadioGroup";
    }
    
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}
    public void setValue(Object value) {}
    public void setValueByName(String name, Object value){}
    
    
}
