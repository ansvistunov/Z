
package rml;
import views.*;
import java.util.*;

/**
*/
public class RADIOGROUP implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.RadioGroup gr = new views.RadioGroup();
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
