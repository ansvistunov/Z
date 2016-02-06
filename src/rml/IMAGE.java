package rml;
import views.*;
import java.util.*;


public class IMAGE implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		IMage im = new IMage();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)im);
		}
		im.init(prop);
		return (Object)im;
	}
}
