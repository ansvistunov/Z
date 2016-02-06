
/*
 * File: TREEVIEW.java
 *
 * Created: Thu Apr  8 11:55:02 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package rml;

import java.util.Hashtable;

public class TREEVIEW implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		Object[] objs = Parser.getContent(prop,aliases);		
		views.TreeView tv;
		try{
			tv = new views.TreeView(prop,aliases,objs);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)tv);
		}
		return (Object)tv;
	}
}
