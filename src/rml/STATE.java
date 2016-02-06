
/*
 * File: STATE.java
 *
 * Created: Fri Jul  2 14:21:11 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package rml;
import java.util.*;
import calc.objects.rml.ScriptState;

public class STATE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		System.out.println("STATE parsed with values "+prop.hash);
		String alias = (String)prop.get("ALIAS");
		ScriptState ss = null;
		try{
			ss = new ScriptState(prop,aliases);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("STATE alias is "+alias);
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)ss);
		}
		return (Object)ss;
	}	
}








