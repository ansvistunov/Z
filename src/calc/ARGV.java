
/*
 * File: ARGV.java
 *
 * Created: Tue Apr 27 09:16:50 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc;

import java.util.*;
import loader.GLOBAL;

public class ARGV implements GlobalValuesObject{
	Hashtable h = new Hashtable();
	Object o;
	public Hashtable hash(){
		return h;
	}
	public ARGV(){
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.ARGV::<init> create new argv!");
	}
	public void setValue(Object obj){
		if ( obj == this )  new RTException("EXCEPTION",
							 "cicle reference on self");
		o = obj;
	}
	public Object getValue() throws Exception{
		if ( o == null ) return new Double(0);
		return o;
	}
	public void setValueByName(String name, Object obj) throws Exception{
        //System.out.println("in argv set value "+name);
		int i = name.indexOf('.');
		if ( obj == this )  new RTException("EXCEPTION",
							 "cicle reference on self");
		if ( i != -1 ){
			Object o = h.get(name.substring(0,i));
			if ( ! (o instanceof GlobalValuesObject) )
				throw new RTException("NullException",
							 "variable "+name.substring(0,i)+" is not container");
			((GlobalValuesObject)o).setValueByName(name.substring(i+1),obj);
			
		}else
			h.put(name,obj);
	}
	public Object getValueByName(String name) throws Exception{
        //System.out.println("in argv get value "+name);
		int i = name.indexOf('.');
		Object o;
		if ( i != -1 ){
			o = h.get(name.substring(0,i));
			if ( ! (o instanceof GlobalValuesObject) ){
				throw new RTException("NullException",
								   "variable "+name.substring(0,i)+" is not container");
			}
            try{
			    o = ((GlobalValuesObject)o).getValueByName(name.substring(i+1));
			}catch(NullPointerException e){
            	throw new RTException("NullException",
					"in argv: variable "+name+" is not initialized");
		    
			}
		}else{
			o =  h.get(name);
		}
		if (o == null) throw new RTException("NullException",
											 "in argv: variable "+name+" is not initialized");
		return o;
	}
	public String toString(){
		return "ARGV"; //h.toString();
	}
}
