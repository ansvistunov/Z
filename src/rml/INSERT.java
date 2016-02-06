/*
 * File: GROUPTREPORT.java
 *
 * Created: Thu Mar 18 15:17:47 1999
 *
 * Copyright(c) by Alexey Swistunow
 */
package rml;
import dbi.*;
import java.util.*;
import loader.*;


/**
*/
public class INSERT implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		if(GLOBAL.dstore_debug>0)
			System.out.println("rml.INSERT.doParsing called");
		dbi.Operation op = new dbi.Operation();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)op);
		}
		String parent = (String)prop.get("PARENT");
		String dep = (String)prop.get("DEP");
		String where = (String)prop.get("WHERE");
		op.setParameters(aliases,parent,where,dep,dbi.Operation.Insert);
		return (Object)op;
	}	
}



