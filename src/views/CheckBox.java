
package views;

import java.awt.*;
import java.util.*;
import rml.*;
import calc.*;
import calc.objects.*;


public class CheckBox extends Checkbox implements GlobalValuesObject, class_type,
    class_method{

    String onValue=null;
    String offValue=null;

    public CheckBox() {
        super();
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;

        onValue = (String)prop.get("ONVALUE");
        offValue = (String)prop.get("OFFVALUE");

        sp = (String)prop.get("CHECK");
        if (sp!=null && sp.equals("YES")) setState(true);
        else setState(false);

        sp = (String)prop.get("LABEL");
        if (sp!=null) setLabel(sp);
    }

    public Object method(String method,Object arg) throws Exception{
        if (method.equals("GETVALUE")) {
            System.out.println("state="+getState());
            System.out.println("onvalue="+onValue);
            System.out.println("offvalue="+offValue);
            if (getState()) return onValue;
            else return offValue;
        }else if (method.equals("SETVALUE")) {
            if (arg.equals(onValue))
              setState(true);
            else if (arg.equals(offValue))
              setState(false);
            else
              throw new RTException("", "Unknown checkbox value");
            return null;
        }else
        throw new RTException("HasNotMethod","method "+method+
        " not defined in class views.CheckBox!");
    }

    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}
    public void setValue(Object value) {}
    public void setValueByName(String name, Object value){}
    
    public String type(){
        return "CheckBox";
    }

}
