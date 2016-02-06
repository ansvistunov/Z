package rml;
import views.*;
import java.util.*;

/**
*/
public class LINE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Line l = new views.Line();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)l);
		}
		l.init(prop,aliases);		
		return (Object)l;
	}
}
