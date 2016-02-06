
/*
 * File: COMA.java
 *
 * Created: Thu Apr 29 10:16:32 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.*;
import loader.GLOBAL;

public class COMA extends OP{
	public COMA(){sym=';';sym2=' ';prior=0;}
	public Object eval() throws NullPointerException,ClassCastException,Exception{
		if ( GLOBAL.calc_debug >3 )
			System.out.println("~calc.COMA::eval aliases "+OP.aliases);
		Vector v;
		Object result;
		if (left!=null){
			result = doOP(left);
			if ( result instanceof Vector ) v = (Vector)result;
			else{
				v = new Vector();
				v.addElement(result);
			}
		}else v = new Vector();
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
	public String expr(){
		return "("+expare(left)+toString()+expare(right)+")";
	}
}
