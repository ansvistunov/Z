
package rml;
import views.*;
import java.util.*;

/**
*/
public class COLUMNTEMPLATE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.ColumnTemplate col = new views.ColumnTemplate();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)col);
		}
		col.init(prop,aliases);
		//Object[] objs = Parser.getContent(prop,aliases);
		//col.addChildren(objs);
		return (Object)col;
	}
}
