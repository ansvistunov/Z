
/*
 * File: TOOLBAR.java
 *
 * Created: Wed Jun 30 15:44:27 1999
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
public class TOOLBAR2 implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		try{
			views.ToolBar2 tb = new views.ToolBar2();
			String alias = (String)prop.get("ALIAS");
			if (alias!=null){
				aliases.put(alias.toUpperCase(),(Object)tb);
			}
			tb.init(prop,aliases);
			return (Object)tb;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}	
}

