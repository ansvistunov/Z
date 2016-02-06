
package rml;
import views.*;
import java.util.*;

public class EDITOR implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Editor ed = new views.Editor();		
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)ed);
		}
		ed.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		ed.addChildren(objs);
		return (Object)ed;
	}
}
