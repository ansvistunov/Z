
/*
 * File: Quoted.java
 *
 * Created: Thu May 13 12:05:41 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc;
import calc.objects.*;

public class Quoted implements Const,class_method,class_type{
	OP e;
	public Quoted(OP e){
		this.e = e;
	}
	public OP getOP(){ return e; }
	public Object method(String method,Object arg) throws Exception{
		if ( method.equals("EVAL") ){
			return e.eval();
		}else throw new RTException("HasMethodException",
									"object Quoted has not method "+method);
	}
	public String type() throws Exception{
		return "QUOTED";
	}
	

}
