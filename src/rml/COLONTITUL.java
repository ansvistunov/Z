package rml;
import java.util.*;

public class COLONTITUL extends ReportHT{
    public Object doParsing(Proper prop,Hashtable aliases){
		if (prop.get("TYPE")==null) {
		    prop.put("TYPE", "TOP");
		}
		return super.doParsing(prop, aliases);
	}
}
