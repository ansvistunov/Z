
/*
 * File: OBJECT.java
 *
 * Created: Mon May  3 09:32:58 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */


package calc;

public class OBJECT {
	public OBJECT(){
	}
	public Object method(String method,OP args) throws Exception{
		throw new Exception("~calc.OBJECT:method not implemented");
	}
	public void field_set(String field,Object val) throws Exception{
		throw new Exception("~calc.OBJECT:field_set not implemented");
	}
	public Object field_get(String field) throws Exception{
		throw new Exception("~calc.OBJECT:field_get not implemented");
	}
	public String getType() throws Exception{
		return "OBJECT";
	}
	public String toString(){
		return "<UNKNOWN_OBJECT>";
	}
}


