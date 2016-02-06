
package rml;
import java.util.*;

public class LABEL implements ParsedObject {
    
    public Object doParsing(Proper prop,Hashtable aliases){
		views.Label lab = new views.Label();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)lab);
		}
                lab.init(prop, aliases);
		return (Object)lab;
	}	
}
