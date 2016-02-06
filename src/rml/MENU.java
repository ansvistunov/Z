
package rml;
import views.*;
import java.util.*;

/**
*/
public class MENU implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Menu menu = new views.Menu();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)menu);
		}
		menu.init(prop);
		Object[] objs = Parser.getContent(prop,aliases);
		menu.addChildren(objs);
		return (Object)menu;
	}
}
