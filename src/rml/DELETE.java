
/*
 * File: DELETE.java
 *
 * Created: Mon Apr 12 09:41:59 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */

package rml;
import dbi.*;
import java.util.*;
import loader.*;


/**
*/
public class DELETE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		if(GLOBAL.dstore_debug>0)
			System.out.println("rml.DELETE.doParsing called");
		dbi.Operation op = new dbi.Operation();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)op);
		}
		String parent = (String)prop.get("PARENT");
		String dep = (String)prop.get("DEP");
		String where = (String)prop.get("WHERE");
		op.setParameters(aliases,parent,where,dep,dbi.Operation.Delete);
		return (Object)op;
	}	
}
