
/*
 * File: Alias.java
 *
 * Created: Fri Apr 23 10:17:01 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.Hashtable;
import loader.GLOBAL;
import calc.objects.*;
//import lisp.GlobalValuesObject;

public class Alias extends OP implements Const{
	String field = null;
	public Alias(String alias){
		if ( GLOBAL.calc_debug >1 )
			System.out.println("~calc.Alias::<init>  alias is "+alias);
		prior = OP.ALIAS_PRIOR;
		int q;
		//alias.trim().toUpperCase();
		if ( (q=alias.indexOf('@')) != -1){
			field = alias.substring(q+1);
			alias = alias.substring(0,q);
		}
		if ( (q=alias.indexOf('.')) != -1){
			left = alias.substring(0,q);
			right = alias.substring(q+1);
		}else{
			left = alias;
			right = null;
		}
	}
	public Object getValue() throws
		Exception,NullPointerException,ClassCastException{
		GlobalValuesObject o;
		Object result = new Double(0);
		try{
		if ( right == null ){
		    //System.out.println ("only left element");
			if ( left.equals("NIL") ) return new Nil();
			o = (GlobalValuesObject)OP.aliases.get("V");
			if ( o!=null ){
				if ( GLOBAL.calc_debug >1 )
					System.out.println("~calc.Alias::getValue from V."+(String)left);
				result =  o.getValueByName((String)left);
			}else{
			    o =  (GlobalValuesObject)OP.aliases.get((String)left);
				if ( GLOBAL.calc_debug >1 )
					System.out.println("~calc.Alias::getValue from aliases "+(String)left);
				result = o.getValue();
			}
		}else if ( ((String)left).compareTo("G") == 0) {
			o = (GlobalValuesObject)OP.aliases.get((String)right);
				if ( GLOBAL.calc_debug >1 )
					System.out.println("~calc.Alias::getValue from aliases "+(String)right );
			result = o.getValue();
		}else {
			o = (GlobalValuesObject)OP.aliases.get((String)left);
				if ( GLOBAL.calc_debug >1 )
					System.out.println("~calc.Alias::getValue from aliases "+(String)left+"."+(String)right);
			result = o.getValueByName((String)right);
		}
		if ( GLOBAL.calc_debug > 2 ) 
			System.out.println(toString()+"="+result);
		if ( field != null ){
			if ( result instanceof String ){
				if ( field.equals("TYPE"))
					return "STRING";
				else if ( field.equals("LENGTH") )
					return new Double(((String)result).length());
				else throw new RTException("HASNOTFIELD",
										"String "+toString()+
										" has not field "+field);
			}else if ( result instanceof Double ){
				if ( field.equals("TYPE"))
					return "NUMBER";
				if ( field.equals("INT"))
					return ""+((Double)result).intValue();
				else throw new RTException("HASNOTFIELD",
										"Number "+toString()+
										" has not field "+field);
			}else if ( result instanceof Object[]){
				if ( field.equals("TYPE"))
					return "ARRAY";
				else if ( field.equals("LENGTH") ||
						  field.equals("SIZE"))
					return new Double(((Object[])result).length);
				else throw new RTException("HASNOTFIELD",
										"Array "+toString()+
										" has not field "+field);
			}else if ( result instanceof Hashtable){
				if ( field.equals("TYPE"))
					return "HASH";
				else throw new RTException("HASNOTFIELD",
										"Hash "+toString()+
										" has not field "+field);
			}else if ( (result instanceof class_type) &&  field.equals("TYPE")){
			}else if ( (result instanceof class_size) &&  field.equals("SIZE")){
			}else if ( result instanceof class_field ){
				return ((class_field)result).field(field);
			}else throw new RTException("HASNOTFIELD",
										"Object "+toString()+
										" has not any field");
		}
		}catch(NullPointerException e){
            e.printStackTrace();
			throw new RTException("NullException",""+this+" not initialized");
		}
		if ( result == null ){
		    throw new RTException("NullException",""+this+" not initialized");
		}
		return result;
	}

	public Object setValue(Object a) throws
		Exception,NullPointerException,ClassCastException{
		GlobalValuesObject o;
		if (GLOBAL.calc_debug > 2)
			System.out.println("~calc.Alias::setValue "+toString()+" set to "+a);

		if ( right == null ){
			if ( left.equals("NIL") ) return new Nil();
			o = (GlobalValuesObject)OP.aliases.get("V");
			//System.out.println("~calc.Alias::setValue o is "+o);
			if ( o!=null ){
				if (field == null) o.setValueByName((String)left,a);
				else {
					Object x = o.getValueByName((String)left);
					if ( x instanceof class_field ){
						((class_field)x).set_field(field,a);
					}else throw new RTException("HASNOTFIELD",
										"Object "+toString()+
										" has not any field");
				}
			}else{
				o = (GlobalValuesObject)OP.aliases.get((String)left);
				if (field == null) o.setValue(a);
				else {
					if ( o instanceof class_field ){
						((class_field)o).set_field(field,a);
					}else throw new RTException("HASNOTFIELD",
										"Object "+toString()+
										" has not any field");
				}
			}
		}else if ( ((String)left).compareTo("G") == 0) {
			o = (GlobalValuesObject)OP.aliases.get((String)right);
			if (field == null) o.setValue(a);
			else {
				Object x = o.getValueByName((String)left);
				if ( x instanceof class_field ){
					((class_field)x).set_field(field,a);
				}else throw new RTException("HASNOTFIELD",
								  "Object "+toString()+
								  " has not any field");
			}
		}else {
			o = (GlobalValuesObject)OP.aliases.get((String)left);
			if (field == null) o.setValueByName((String)right,a);
			else {
				Object x = o.getValueByName((String)right);
				if ( x instanceof class_field ){
					((class_field)x).set_field(field,a);
				}else throw new RTException("HASNOTFIELD",
								  "Global Object "+x+" AKA "+toString()+
								  " has not any field");
			}
		}
		return a;
	}
	public void getAliases(Hashtable h){
		h.put(name(),"");
	}
	public Object eval() throws Exception,NullPointerException,ClassCastException{
		return getValue();
	}
	public String toString(){
		return "__Alias#"+name();
	}
	public String expr(){
		return toString();
	}
	public String name(){
		return ""+left+((right!=null)?"."+right:"");
	}
}



