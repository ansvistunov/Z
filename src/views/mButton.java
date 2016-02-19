
/*
 * File: mButton.java
 *
 * Created: Wed Jun 30 16:17:29 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package views;

import java.awt.*;
import java.awt.event.*;
import rml.*;
import views.FORM;
import views.edit.EditMaketAdapter;
import calc.*;
import java.util.*;

public class mButton extends java.awt.Button implements Selectable{
	/**/
	String aaction;
	/**/
	
	Calc calc = null;
	Hashtable aliases = null;	int left, top, width,height;
	Color bg_color;
	
	public mButton(){
		super("");
	}
	
	public FORM getFormParent() {
        Container parent = super.getParent();
        if (parent instanceof FORM) return (FORM)parent;else return null;
    }
    
    public Color getNormalBgColor() {
    	return bg_color;
    	
    }
	
	
	public void init(Proper p,Hashtable aliases){
		String s = (String)p.get("LABEL");		Integer ip;
		if (s!=null) setLabel(s);
		s = (String)p.get("ACTION");
		if (s!=null) calc = new Calc(s);
		aaction = (String)p.get("AACTION");
		
		s = (String)p.get("BACKGROUND");
		if (s!=null) setBackground(loader.GLOBAL.color(s));
		
		bg_color = getBackground();
		
		s = (String)p.get("FOREGROUND");
		if (s!=null) setForeground(loader.GLOBAL.color(s));
		s = (String)p.get("FONT");
		if (s!=null) setFont(loader.GLOBAL.font(s));		ip = (Integer)p.get("LEFT");		if (ip!=null) left = ip.intValue();		ip = (Integer)p.get("TOP");		if (ip!=null) top = ip.intValue();		ip = (Integer)p.get("WIDTH");		if (ip!=null) width = ip.intValue();		ip = (Integer)p.get("HEIGHT");		if (ip!=null) height = ip.intValue();		setBounds(left, top, width, height);
		this.aliases = aliases;
		enableEvents(-1);
	}
	protected void processActionEvent(ActionEvent ev){
		
		FORM parent = getFormParent();
		if (parent != null)
			if (EditMaketAdapter.isEditMaket()) {
				if ((ev.getModifiers() & 128 /*ActionEvent.CTRL_MASK????? */)!=0) EditMaketAdapter.getEditMaketAdapter(parent).addMarkChild(this);
				else EditMaketAdapter.getEditMaketAdapter(parent).setMarkChild(this);
				
				//System.out.println("ev="+ev+" mod="+ev.getModifiers()+" act="+ev.getActionCommand()+" cm="+ActionEvent.CTRL_MASK);
				repaint();
				return;
			}
		
		//System.out.println("action");
		try{
			if (calc!=null) calc.eval(aliases);
		}catch(Exception e){
		}
		
		/********/
		if (aaction!=null){
		try {
            document.ACTION.doAction(aaction,aliases,null);
        }
        catch(Exception ex) {
        }
        }
		/********/
	}
}




