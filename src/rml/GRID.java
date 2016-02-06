
package rml;
import views.*;
import java.util.*;

/**
*/
public class GRID implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Grid gr = new views.Grid();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)gr);
		}
		gr.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		gr.addChildren(objs);
		return (Object)gr;
	}
}
