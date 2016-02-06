
package rml;
import views.*;
import java.util.*;

/**
*/
public class COLUMN implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Column col = new views.Column();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)col);
		}
		col.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		col.addChildren(objs);
		return (Object)col;
	}
}
