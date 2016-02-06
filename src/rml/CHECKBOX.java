
package rml;
import views.*;
import java.util.*;

/**
*/
public class CHECKBOX implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.CheckBox box = new views.CheckBox();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)box);
		}
		box.init(prop,aliases);
		return (Object)box;
	}
}
