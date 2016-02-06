
package rml;
import views.*;
import java.util.*;

/**
*/
public class RADIOBUTTON implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.RadioButton but = new views.RadioButton();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)but);
		}
		but.init(prop,aliases);		
		return (Object)but;
	}
}
