/*
 * File: GROUPTREPORT.java
 *
 * Created: Thu Mar 18 15:17:47 1999
 *
 * Copyright(c) by Alexey Swistunow
 */
package rml;
import dbi.*;
import java.util.*;
import loader.*;


/**
*/
public class GROUPREPORT implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		if(GLOBAL.dstore_debug>0)
			System.out.println("rml.GROUPREPORT.doParsing called");
		dbi.GroupReport ds = new dbi.GroupReport();
		ds.setReadOnly(true);
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)ds);
		}
		
		ds.setAliases(alias,aliases);
		String str = (String)prop.get("QUERY");
		if(str!=null) ds.setSql(str);
		str = (String)prop.get("UNIQUE");
		if(str!=null) ds.setUnique(str);
		str = (String)prop.get("UPDATEABLE");
		if(str!=null) ds.setUpdateable(str);
		str = (String)prop.get("LINKS");
		if(str!=null) ds.setLinks(str);
		str = (String)prop.get("DEFAULT");
		if(str!=null) ds.setDefaults(str);
		String head = (String)prop.get("HEAD");
		if(head==null || head.compareTo("NO")==0){ds.setHead(false);}else{ds.setHead(true);};
		str = (String)prop.get("GROUPING");
		String str2 = (String)prop.get("SORTING");
		String str3 = (String)prop.get("TREEPARAM");
		if(GLOBAL.dstore_debug>0) System.out.println("rml.GROUPREPORT.doParsing str="+str+" str1="+str2);
		if(str!=null && str2!=null) {
			ds.setParameters(str,str2,str3);
			//ds.resolveAllGroups();
			if(GLOBAL.dstore_debug>0) System.out.println("rml.GROUPREPORT.doParsing calling resolveAllGroups");
		}
		//if(GLOBAL.dstore_debug>1)
			//System.out.println(ds.getNode("0/0"));
		//ds.printAllGroups();
		str = (String)prop.get("SORTORDER");
		if(str!=null)ds.setSortOrder(str);
		
		Object[] objs = Parser.getContent(prop,aliases);
		//ds.addOperation(objs);
		ds.addSubStores(objs);
		
		return (Object)ds;
	}	
}



