
/*
 * File: INCLUDE.java
 *
 * Created: Wed Jun 30 15:23:25 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package rml;

import java.util.*;
import loader.*;
import calc.*;

public class INCLUDE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases) throws Exception{
		String fname = (String)prop.get("SRC");
		if ( fname == null )
			throw new Exception("INCLUDE must have src propers");
		char[] text =
			GLOBAL.loader.loadByName_chars(fname,true);
		Hashtable ali = new Hashtable();
		document.Document document = (document.Document)aliases.get("###document###");
		ali.put("###document###",document);
		ali.put("ARGUMENTS",aliases.get("ARGUMENTS"));
		ali.put("SELF",aliases.get("SELF"));
		ali.put("GLOBAL",new calc.ARGV());
		ali.put("_DATALOADER_",aliases.get("_DATALOADER_"));
		document.DBStateBroker dbroker =
			(document.DBStateBroker)aliases.get("###DBSBROKER###");
		document.DBStateBroker broker = new
			document.DBStateBroker(document,
								   dbroker.getOtherForKey()+"*"+fname);
		dbroker.registreBroker(broker);
		ali.put("###DBSBROKER###",broker);
		Proper p = rml.Parser.createProper(text,null);
		String script = (String)p.get("PRELOADSCRIPT");
		if (script!=null){
			Calc c = new Calc(script);
			c.eval(ali);
		}
 		Object o =
			Parser.getContent(rml.Parser.createProper(text,null),ali)[0];
		script = (String)p.get("POSTLOADSCRIPT");
		if (script!=null){
			Calc c = new Calc(script);
			c.eval(ali);
		}
		return o;
	}	
}




