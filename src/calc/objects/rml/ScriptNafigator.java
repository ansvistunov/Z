
/*
 * File: ScriptNafigator.java
 *
 * Created: Thu Jun 17 11:33:43 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.objects.rml;

import calc.*;
import calc.objects.*;
import loader.GLOBAL;
import views.NafigatorInterface;
import views.Retrieveable;
import java.util.*;
import rml.Proper;
import dbi.Handler;

public class ScriptNafigator
	implements NafigatorInterface,GlobalValuesObject,
    class_type,class_method,class_field,Retrieveable{
	
	Handler handler = null;

	Hashtable aliases = null;
	Calc calc_retrieve = null;
	Calc calc_setCurPath = null;
	Calc calc_loadDate = null;

	String curpath = "/";
	
	public ScriptNafigator(Proper prop,Hashtable aliases){
		calc_retrieve = new Calc((String)prop.get("RETRIEVE","()"));
		calc_setCurPath = new Calc((String)prop.get("SETCURPATH","()"));
		calc_loadDate = new Calc((String)prop.get("LOADDATE","()"));
		this.aliases = aliases;
	}

	public void retrieve() {
		//System.out.println("ScriptNafigator::retrieve called");
		try{
			calc_retrieve.eval(aliases);
			if ( handler!= null ) handler.notifyHandler(null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setCurPath(String path) throws Exception{
		if ( path.startsWith("//") ) path = path.substring(1);
		ARGV argv = new ARGV();
		argv.setValueByName("PATH",path);
		argv.setValueByName("THIS",this);
		aliases.put("_ARG_",argv);
		curpath = path;
		calc_setCurPath.eval(aliases);
		aliases.remove("_ARG_");
	}
	public String loadDate(String path) throws Exception{
		if ( path.startsWith("//") ) path = path.substring(1);
		ARGV argv = new ARGV();
		argv.setValueByName("PATH",path);
		argv.setValueByName("THIS",this);
		aliases.put("_ARG_",argv);
		curpath = path;
		Object o = calc_loadDate.eval(aliases);
		//System.out.println(o);
		o = ((Object[])o)[0];
		//System.out.println(o);
		aliases.remove("_ARG_");
		return (String)o;
	}

// implementaion of GlobalValuesObject

	public void setValue(Object obj) throws Exception{}
    public Object getValue() throws Exception{return this;}
    public void setValueByName(String name, Object obj) throws Exception {}
    public Object getValueByName(String name) throws Exception {return null;}

	public String type() throws Exception{
		return "SCRIPTNAFIGATOR";
	}

	public String toString(){
		return "SCRIPTNAFIGATOR with curpath '"+curpath+"'";
	}

//implementaion of class_field

	public Object field(String field) throws Exception{
		if ( field.equals("CURPATH") ){
			return curpath;
		}else throw new RTException("HasFieldException",
									"object ScriptNafigator has not field "+field);
	}

	public Object set_field(String field,Object value) throws Exception{
		throw new RTException("HasFieldException",
				 "object ScriptNafigator has not one field then may be modifed");
	}

//implementaion of class_method
	
	public Object method(String method,Object arg) throws Exception{
		if ( method.equals("RETRIEVE") ) {
			retrieve();
			return new Nil();
		}else 
		if ( method.equals("OPENNODE") ) {
			if (arg instanceof String ){
				if ( handler != null ){
					((views.TreeView)handler).openNode(((String)arg).trim().substring(1));                   
				}
			}else  throw new RTException("CASTEXCEPTION",
						"OPENNODE has one STRING argument ");

			return new Nil();
		}else 
		throw new RTException("HasMethodException",
									"object ScriptNafigator has not method "+method);
	}

	public void addHandler(Handler h){
		handler = h;
	}

	public void update(){
	}

	public void fromDS(){
	}
	
	public void toDS(){
	}
	
}

