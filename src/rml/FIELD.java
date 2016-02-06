/**
	Copyrigth(c) 1999 by Gama
	author Alexey Chen (xx.2.99)
*/

package rml;
import views.*;
import java.util.*;

/**
*/
public class FIELD implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.Field fld = new views.Field();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)fld);
		}
		fld.init(prop,aliases);
		return (Object)fld;
	}	
}
