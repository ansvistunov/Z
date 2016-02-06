
/*
 * File: SCRIPTNAFIGATOR.java
 *
 * Created: Thu Jun 17 12:51:02 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package rml;
import views.*;
import java.util.*;

/**
*/
public class SCRIPTNAFIGATOR implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		calc.objects.rml.ScriptNafigator sn =
			new calc.objects.rml.ScriptNafigator(prop,aliases);
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)sn);
		}
		return (Object)sn;
	}	
}
