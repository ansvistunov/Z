package rml;
import views.*;
import java.util.*;

/**
*/
public class TREEVIEW2 implements ParsedObject{
	public Object doParsing(Proper prop,Hashtable aliases){
		views.TreeView2 tree = new views.TreeView2();
		String alias = (String)prop.get("ALIAS");
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)tree);
		}
		tree.init(prop,aliases);
		Object[] objs = Parser.getContent(prop,aliases);
		tree.addChildren(objs);
		return (Object)tree;
	}
}
