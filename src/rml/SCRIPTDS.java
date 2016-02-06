/*
 * File: DATASTORE.java
 *
 * Created: Thu Mar 18 15:17:47 1999
 *
 * Copyright(c) by Alexey Chen
 */

package rml;
import dbi.*;
import java.util.*;
import loader.*;


/**
*/
public class SCRIPTDS implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		dbi.ScriptDs ds = new dbi.ScriptDs();

		
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)ds);
		}

		ds.setAliases(alias,aliases);
		String ret= (String)prop.get("RETRIEVE");
		String update = (String)prop.get("SAVE");
		String newstr = (String)prop.get("NEW");
		ds.setScripts(ret,update,newstr);
		return (Object)ds;
	}
}



