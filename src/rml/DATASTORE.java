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
public class DATASTORE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		dbi.DATASTORE ds = new dbi.DATASTORE();
		
		String editable = (String)prop.get("EDITABLE");
		if ( (editable == null) || (editable.compareTo("NO") == 0) ){
			ds.setReadOnly(true);
		}else{
			ds.setReadOnly(false);
		}
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)ds);
		}
		
		ds.setAliases(alias,aliases);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetAliases initds="+ds+" alias="+alias);
		String str = (String)prop.get("QUERY");
		if(str!=null) ds.setSql(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsingset Sql initds="+ds+" alias="+alias);
		str = (String)prop.get("UNIQUE");
		if(str!=null) ds.setUnique(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetUnique initds="+ds+" alias="+alias);
		str = (String)prop.get("UPDATEABLE");
		if(str!=null) ds.setUpdateable(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetUpdateble initds="+ds+" alias="+alias);
		str = (String)prop.get("LINKS");
		if(str!=null) ds.setLinks(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetLinks initds="+ds+" alias="+alias);
		str = (String)prop.get("DEFAULT");
		if(str!=null) ds.setDefaults(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetDefaults initds="+ds+" alias="+alias);
		String head = (String)prop.get("HEAD");
		if(head==null ||
		   head.compareTo("NO")==0){ds.setHead(false);}else{ds.setHead(true);};
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing SetHead initds="+ds+" alias="+alias);
		str = (String)prop.get("ACTIONS");
		if(str!=null) ds.initActions(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing Actions init");
		Object[] objs = Parser.getContent(prop,aliases);
		ds.addOperation(objs);
		ds.addSubStores(objs);
		
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing Operation init");
		String in =  (String)prop.get("INSDEP");
		String up =  (String)prop.get("UPDEP");
		String del =  (String)prop.get("DELDEP");
		ds.setDeps(in,up,del);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing Deps init");
		str = (String)prop.get("DEFROW");
		if (str!=null) ds.setDefRow(str);
		str = (String)prop.get("SELACTION");
		if (str!=null) ds.setSelAction(str);
		if(GLOBAL.dstore_debug>2)
			System.out.println("rml.DATASTORE.doParsing ds="+ds+" alias="+alias);
		str = (String)prop.get("SORTORDER");
		if(str!=null)ds.setSortOrder(str);
		return (Object)ds;
		
	}	
}



