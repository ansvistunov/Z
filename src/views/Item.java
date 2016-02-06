
package views;
import rml.*;
import calc.*;
import calc.objects.*;
public class Item extends java.awt.MenuItem implements GlobalValuesObject,class_type{    
    Calc calc;//нужен для вычисления выражений в меню    
    String exp;
    public Item(){
        setActionCommand("");
    }
    public void init(Proper prop) {
        String sp;
        Integer ip;
        sp = (String)prop.get("LABEL");
        if (sp!=null) setLabel(sp);
        sp = (String)prop.get("ACTION");
        if (sp!=null) setActionCommand(sp);
        else setActionCommand("");
        System.out.println("action command is "+getActionCommand());
        sp = (String)prop.get("EXP");
        if (sp!=null) {
            calc = new Calc(sp);
            exp = sp;
        }
    }
    
    public Calc getCalc() {return calc;}
       //Методы интерфейса GlobalValuesObject
    public void setValue(Object o){}
    public void setValueByName(String name, Object o) {
      if (name.toUpperCase().equals("LABEL")) setLabel(o.toString());
      else if(name.toUpperCase().equals("ACTION")) setActionCommand(o.toString());
      else if(name.toUpperCase().equals("EXP")) {exp = o.toString(); calc = new Calc(exp);}

    }
    public Object getValue(){return this;}
    public Object getValueByName(String name) {
      if (name.toUpperCase().equals("LABEL")) return getLabel();
      else if(name.toUpperCase().equals("ACTION")) return getActionCommand();
      else if(name.toUpperCase().equals("EXP")) return exp;
      else return new Nil();

    }
    public String type(){
      return "SVR_ITEM";
    }

    
}
