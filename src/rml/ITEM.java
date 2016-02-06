
package rml;
import views.*;
import java.util.*;

/**
*/
public class ITEM implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		Item item = new Item();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)item);
		}
		item.init(prop);
		return (Object)item;
	}
}
