package rml;
import views.Group;
import java.util.*;

public class GROUP implements ParsedObject{
    public Object doParsing(Proper prop,Hashtable aliases){
		Group gr = new Group();
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
