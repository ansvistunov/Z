package rml;
import dbi.*;
import java.util.*;


/**

*/
public class GROSSTAB implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		dbi.GrossTab gt = new dbi.GrossTab();

		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)gt);
		}
		gt.setAliases(alias,aliases);
		String query = (String)prop.get("QUERY");
		gt.setSql(query);
		gt.setParameters(prop.get("ROWCONDITION"),prop.get("COLUMNCONDITION"),prop.get("DATACONDITION"),prop.get("EVAL"));
		return (Object)gt;
	}
}
