
/*
 * File: ToolBar.java
 *
 * Created: Wed Jun 30 15:56:30 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package views;

import java.awt.*;
import rml.*;
import java.util.*;

public class ToolBar extends Panel implements Retrieveable{
	Panel pan = new Panel();
	Retrieveable main = null;
	public void retrieve(){
		if ( main != null )
			main.retrieve();
	}
	public void update(){
		if (main!=null)
			main.update();
	}
	public void fromDS(){
		if (main!=null)
			main.fromDS();
	}
	public void toDS(){
		if (main!=null)
			main.toDS();
	}
	public ToolBar(){
		setLayout(new BorderLayout());
		pan.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	}
	public void init(Proper p,Hashtable aliases){
		String s = (String)p.get("ALIGN");
		if ( (s == null) || (s.toUpperCase().equals("SOUTH")) ){
			add("South",pan);
		}else{
			add("North",pan);
		}
		s = (String)p.get("BACKGROUND");
		if (s!=null) pan.setBackground(loader.GLOBAL.color(s));
		s = (String)p.get("FOREGROUND");
		if (s!=null) pan.setForeground(loader.GLOBAL.color(s));
		s = (String)p.get("FONT");
		if (s!=null) pan.setFont(loader.GLOBAL.font(s));
		Object[] o = Parser.getContent(p,aliases);
		add("Center",(Component)o[0]);
		if ( o[0] instanceof Retrieveable ) main = (Retrieveable)o[0];
		for ( int i=1; i<o.length;++i){
			pan.add((Component)o[i]);
		}
	}
}






