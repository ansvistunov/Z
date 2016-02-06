
/*
 * File: RTException.java
 *
 * Created: Thu Apr 29 13:42:31 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

public class RTException extends Exception{
	public String type;
	public String trap;
	public RTException(String type,String messag){
		super(messag);
		this.type = type.trim().toUpperCase();
		trap="";
	}
	public RTException(String type,String messag,String trap){
		super(messag);
		this.type = type.trim().toUpperCase();
		this.trap=trap;
	}
        public RTException(){
                super("calc.RTException");
        }
	public String toString(){
                return "RunTime:"+type+":"+getMessage()+trap;
	}
}

