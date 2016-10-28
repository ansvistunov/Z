
package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import rml.*;
import views.edit.EditMaketAdapter;
import calc.*;
import calc.objects.*;


public class CheckBox extends Checkbox implements GlobalValuesObject, class_type,
    class_method{

    String onValue=null;
    String offValue=null;
    String aaction = null;
    Calc calc = null;
    Hashtable aliases;

    public CheckBox() {
        super();
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        
        this.aliases = aliases;

        onValue = (String)prop.get("ONVALUE");
        offValue = (String)prop.get("OFFVALUE");

        sp = (String)prop.get("CHECK");
        if (sp!=null && sp.equals("YES")) setState(true);
        else setState(false);

        sp = (String)prop.get("LABEL");
        if (sp!=null) setLabel(sp);
        
        sp = (String)prop.get("ACTION");
		if (sp!=null) {
			//System.out.println("action="+sp);
			calc = new Calc(sp);
			//System.out.println("calc="+calc);
			}
		aaction = (String)prop.get("AACTION");
		
		addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent event) {   
	        	 //System.out.println("CHECKBOX processEvent called ev="+event+" onValue="+onValue+" calc="+calc+" aaction="+aaction);
	        	 try{
	        		 System.out.println("calc="+calc); 
	     			if (calc!=null) calc.eval(aliases);
	     		}catch(Exception e){
	     			e.printStackTrace();
	     		}
	     		
	     		/********/
	     		if (aaction!=null){
	     		try {
	                 document.ACTION.doAction(aaction,aliases,null);
	             }
	             catch(Exception ex) {
	            	 ex.printStackTrace();
	             }
	             }
	     		/********/
	         }
	      });
        
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

    //protected void processItemEvent(ItemEvent event){
    //	System.out.println("CHECKBOX processEvent called ev="+event+" calc="+calc+" aaction="+aaction);
    //	
    //}
}
