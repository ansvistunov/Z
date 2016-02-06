
/*
 * File: Nil.java
 *
 * Created: Wed Jun 16 15:54:38 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc;
import calc.objects.*;

public class Nil implements GlobalValuesObject,class_type{

//	public static Nil NIL;
	
	public void setValue(Object obj) throws Exception{}
    public Object getValue() throws Exception{return this;}
    public void setValueByName(String name, Object obj) throws Exception {}
    public Object getValueByName(String name) throws Exception {return null;}

	public String type(){
		return "NIL";
	}

	public String toString(){
		return "NIL";
	}

	public boolean equals(Object o){
		return (o instanceof Nil);
	}
}

