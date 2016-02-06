
/*
 * File: LABEL.java
 *
 * Created: Thu Apr 29 08:40:11 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.*;
import loader.GLOBAL;

class LABEL extends OP implements Unar{
	String label;
	public LABEL(String label){
		sym=':';sym2=':';
		prior=-1;this.label = label.trim().toUpperCase();
	}
	public Object eval() throws NullPointerException,ClassCastException,Exception{
		Vector v;
		Object result;
		result = doOP(left);
		if ( result instanceof Vector ) v = (Vector)result;
		else{
			v = new Vector();
			v.addElement(result);
		}
		if ( right != null ){
			result = doOP(right);
			if ( result instanceof Vector ) {
				Vector r = (Vector)result;
				for (Enumeration e = r.elements() ; e.hasMoreElements() ;) {
					v.addElement(e.nextElement());
				}
			}else{
				v.addElement(result);
			}
		}
		return v;
	}
	public Object evalLabel(String label) throws Exception{
		if ( GLOBAL.calc_debug > 2 )
			System.out.println("~calc.LABEL::evalLabel label "+label);
		if ( this.label.equals(label) ) return ((OP)right).eval();
		else return ((OP)left).evalLabel(label);
	}
	public String expr(){
		return "("+expare(left)+toString()+expare(right)+")";
	}
	public String toString(){
		return ":"+label+":";
	}
}
