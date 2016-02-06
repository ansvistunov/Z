
package rml;
import views.*;
import java.util.*;

/**
*/
public class CHECKGROUP implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.CheckGroup gr = new views.CheckGroup();
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
