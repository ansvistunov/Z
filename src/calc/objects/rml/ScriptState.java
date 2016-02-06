
/*
 * File: ScriptState.java
 *
 * Created: Fri Jul  2 14:26:07 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.objects.rml;

import calc.*;
import calc.objects.*;
import document.*;
import java.util.*;
import rml.Proper;

public class  ScriptState implements StateSaver,GlobalValuesObject,
class_type,class_iterator,class_method
{
	State state;
    public void setValue(Object obj) throws Exception{}
    public Object getValue() throws Exception{return this;}
    public void setValueByName(String name, Object obj) throws Exception{
		state.put(name,obj);
	}
    public Object getValueByName(String name) throws Exception{
		if (state.containsKey(name)) {
			return state.get(name);
		}else return new Nil();
	}
	public ScriptState(Proper prop,Hashtable aliases){
		state = new State();
		state.init(prop.hash);
		String key = (String)prop.get("KEY");
		if (key != null) try {
			state.init(
				((DBStateBroker)aliases.get("###DBSBROKER###")).registre(this,key));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveState(State s){
		s.init((Hashtable)state);
	}
	public Object method(String method,Object x) throws Exception {
		if ( method.equals("PUT") ){
			if ( (x instanceof Vector) && ((Vector)x).size()==2){
				((Hashtable)state).put(
					((Vector)x).elementAt(0),
					((Vector)x).elementAt(1));
				return this;
			}else throw new RTException("CASTEXCEPTION",
										"Must be ($STATE@put any,any)");
		}else if ( method.equals("GET") ||
				   method.equals("")){
			return ((Hashtable)state).get(x);
		}else if ( method.equals("TYPE")) {
			return "HASH";
		}else if ( method.equals("ITERATOR")) {
			return new HashIterator((Hashtable)state);
		}else throw new RTException("HASNOTMETHOD",
									"STATE has not method "+method);
	}

	public String type(){
		return "STATE";
	}
	public iterator iterator(){
		return new HashIterator((Hashtable)state);
	}
	public String toString(){
		return state.toString();
	}
}







