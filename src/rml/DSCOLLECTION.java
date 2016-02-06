package rml;
import dbi.*;
import java.util.*;


/**

*/
public class DSCOLLECTION implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		dbi.DSCollection ds = new dbi.DSCollection();
		
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)ds);
		}
		ds.setNames(aliases);
		ds.setAliases((String)prop.get("ALIASES"));
		ds.setInitQuery((String)prop.get("INITQUERY"));
		ds.setSeqQuery((String)prop.get("SEQQUERY"));
		ds.setSeqNames((String)prop.get("SEQNAMES"));
		ds.setInitAction((String)prop.get("INITACTION"));
		return (Object)ds;
	}	
}
