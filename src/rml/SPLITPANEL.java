
package rml;
import views.*;
import java.util.*;
import java.awt.*;

public class SPLITPANEL implements ParsedObject{
    public Object doParsing(Proper prop, Hashtable aliases) {		
	/**/	views.SplitPanel spanel = new views.SplitPanel();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias,(Object)spanel);
		}		
	/* */	spanel.init(prop);
		
	/*	*/Object[] objs = Parser.getContent(prop,aliases);		
	/* */	spanel.addChildren(objs);		
		
		return (Object)spanel;
    }
}
