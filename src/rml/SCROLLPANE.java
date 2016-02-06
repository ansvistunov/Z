package rml;
import views.*;
import java.util.*;
import java.awt.*;

public class SCROLLPANE implements ParsedObject{
    RetPanel workPanel = new RetPanel();
    public Object doParsing(Proper prop, Hashtable aliases) {		
		views.ScrollPane sp = new views.ScrollPane();
		sp.init(prop);
		String alias = (String)prop.get("ALIAS");
		Object[] objs = Parser.getContent(prop,aliases);		
		
		if (objs.length>1) {System.out.println("Into SCROLLPANE must be added only one child !");}
		
		
		Rectangle r = ((Component)objs[0]).getBounds();
		//System.out.println(r);
		//try{Thread.sleep(5000);}catch(Exception e) {}
		workPanel.setLayout(null);
		workPanel.setBounds(0,0,r.x+r.width,r.y+r.height);		
		workPanel.add((Component)objs[0]);
		//workPanel.setBackground(new Color(sp.bg_color));
		
		sp.add(workPanel);		
		if (alias!=null){
			aliases.put(alias.toUpperCase(),(Object)sp);
		}
		return (Object)sp;
    }
}