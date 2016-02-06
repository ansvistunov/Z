package rml;
import views.*;
import java.util.*;

public class ReportHT implements ParsedObject{
    ReportForm f = null;
    public ReportForm getForm(){return f;}

    public Object doParsing(Proper prop,Hashtable aliases){
		f = new ReportForm();

		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)this);
		}
		f.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		f.addChildren(objs);
		return this;
	}
}
