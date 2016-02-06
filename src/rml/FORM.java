/*
 * File: FORM.java
 *
 * Created: Thu Mar 18 15:08:32 1999
 *
 * Copyright(c) by Alexey Chen
 */

package rml;
import views.*;
import java.util.*;

/**
*/
public class FORM implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.FORM frm = new views.FORM();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)frm);
		}
		frm.init(prop, aliases);
		Object[] objs = Parser.getContent(prop,aliases);		
		frm.addChildren(objs);
		return (Object)frm;
	}	
}
