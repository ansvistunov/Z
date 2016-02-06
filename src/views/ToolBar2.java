
package views;

import java.awt.*;
import rml.*;
import java.util.*;

public class ToolBar2 extends Panel implements Retrieveable{
	//Panel pan = new Panel();
	//Retrieveable main = null;
	public void retrieve(){
		
	}
	public void update(){
		
	}
	public void fromDS(){
		
	}
	public void toDS(){
		
	}
	public ToolBar2(){
	    //System.out.println("inside toolbar2 constructor");
		setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));		
	}
	public void init(Proper p,Hashtable aliases){
		String s = (String)p.get("ALIGN");
		Integer i; 
		/*if ( (s == null) || (s.toUpperCase().equals("SOUTH")) ){
			add("South",pan);
		}else{
			add("North",pan);
		}*/
		if (s!=null) {
		    if (s.equals("LEFT")) ((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);  
		    else
		    if (s.equals("RIGHT")) ((FlowLayout)getLayout()).setAlignment(FlowLayout.RIGHT);
		    else
		    if (s.equals("CENTER")) ((FlowLayout)getLayout()).setAlignment(FlowLayout.CENTER);
		}
		i = (Integer)p.get("VGAP");
		if (i!=null) ((FlowLayout)getLayout()).setVgap(i.intValue());
		i = (Integer)p.get("HGAP");
		if (i!=null) ((FlowLayout)getLayout()).setHgap(i.intValue());
		
		s = (String)p.get("BACKGROUND");
		if (s!=null) setBackground(loader.GLOBAL.color(s));
		s = (String)p.get("FOREGROUND");
		if (s!=null) setForeground(loader.GLOBAL.color(s));
		s = (String)p.get("FONT");
		if (s!=null) setFont(loader.GLOBAL.font(s));
		Object[] o = Parser.getContent(p,aliases);		
		if (o!=null)
    		for ( int j=0; j<o.length;++j){
    			if (o[j] instanceof Component) 
    			add((Component)o[j]);
    		}
	}
}






