
/*
 * File: BUTTON.java
 *
 * Created: Wed Jun 30 15:44:39 1999
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
public class BUTTON implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.mButton btn = new views.mButton();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)btn);
		}
		btn.init(prop,aliases);
		return (Object)btn;
	}	
}
