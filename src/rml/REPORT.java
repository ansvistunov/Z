
package rml;

import java.awt.*;
import java.util.*;
import views.Report;

public class REPORT implements ParsedObject{
    public Object doParsing(Proper prop,Hashtable aliases){
		Report rep = new Report();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)rep);
		}
		rep.init(prop, aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		rep.addChildren(objs);
		return (Object)rep;
	}

}
