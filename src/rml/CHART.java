
package rml;
import views.*;
import java.util.*;

/**
*/
public class CHART implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Chart gr = new views.Chart();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)gr);
		}
		gr.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		gr.addDs((dbi.DATASTORE)objs[0]);
		return (Object)gr;
	}
}
