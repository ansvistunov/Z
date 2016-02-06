
package rml;
import views.*;
import java.util.*;
import java.awt.*;

public class TABSET implements ParsedObject{
    public Object doParsing(Proper prop, Hashtable aliases) {		
	/**/	views.TabSet ts = new views.TabSet();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)ts);
		}		
	/* */	ts.init(prop);
		
	/*	*/Object[] objs = Parser.getContent(prop,aliases);		
	/* */	ts.addChildren(objs);		
		
		return (Object)ts;
    }
}
