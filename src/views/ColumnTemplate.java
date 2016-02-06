
package views;

import java.util.*;
import rml.*;

public class ColumnTemplate {
    private String title_font_face;//="Dialog";
    private Integer title_font_size;//=12;
    private Integer title_font_family;//=0;
    String title_font_color;// = Color.black;
    private String font_face="Dialog";
    Integer font_size;//=10;
    Integer font_family;//=0;
    String font_color;// = Color.black;
    String halignment;// = "LEFT";
    String valignment;// = "CENTER";
    String bg_color;// = Color.white;
    String editable="HAND";
    Integer size;// = 50;
    String editMask;
	int type=-1;//not defined

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        if (prop==null) return;
        sp = (String)prop.get("TITLE_FONT_FACE");
        if (sp!=null) title_font_face = sp;

        ip = (Integer)prop.get("TITLE_FONT_SIZE");
        if (ip!=null) title_font_size = ip;

        ip = (Integer)prop.get("TITLE_FONT_FAMILY");
        if (ip!=null) title_font_family = ip;

        sp = (String)prop.get("TITLE_FONT_COLOR");
        if (sp!=null) title_font_color = sp;

        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) font_face = sp;
        ip = (Integer)prop.get("FONT_SIZE");
        if (ip!=null) font_size = ip;
        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) font_family = ip;
        sp = (String)prop.get("FONT_COLOR");
        if (sp!=null) font_color = sp;

        sp = (String)prop.get("HALIGNMENT");
        if (sp!=null) halignment = sp;
        sp = (String)prop.get("VALIGNMENT");
        if (sp!=null) valignment = sp;

        sp = (String)prop.get("BG_COLOR");
        if (sp!=null) bg_color = sp;

        ip = (Integer)prop.get("SIZE");
        if (ip!=null) size = ip;
        
        sp = (String)prop.get("EDITABLE");
        if (sp!=null) editable = sp;
        
        sp = (String)prop.get("EDITMASK");
        if (sp!=null) editMask = sp;
		
		sp = (String)prop.get("TYPE");
		if (sp!=null) {
			sp = sp.toUpperCase();
			if (sp.equals("NUMBER")) type=0;
			if (sp.equals("STRING")) type=1;
			if (sp.equals("DATA")) type=2;
		}		


    }

    public Proper getProperties() {
        Proper ret = new Proper();
        if (title_font_face!=null) ret.put("TITLE_FONT_FACE",title_font_face);
        if (title_font_size != null) ret.put("TITLE_FONT_SIZE",title_font_size);
        if (title_font_family!=null) ret.put("TITLE_FONT_FAMILY",title_font_family);
        if (title_font_color != null) ret.put("TITLE_FONT_COLOR", title_font_color);
        if (font_face != null) ret.put("FONT_FACE",font_face);
        if (font_size != null) ret.put("FONT_SIZE",font_size);
        if (font_family != null) ret.put("FONT_FAMILY", font_family);
        if (font_color != null) ret.put("FONT_COLOR", font_color);
        if (halignment != null) ret.put("HALIGNMENT", halignment);
        if (valignment != null) ret.put("VALIGNMENT", valignment);
        if (bg_color != null) ret.put("BG_COLOR", bg_color);
        if (size !=null) ret.put("SIZE", size);
        //if (visible != null) ret.put("VISIBLE", visible);
        if (editable != null) ret.put("EDITABLE", editable);
        if (editMask != null) ret.put("EDITMASK", editMask);
        
        return ret;
    }

    public static Proper getDefaultProperties() {
        Proper ret = new Proper();
        ret.put("TITLE_FONT_FACE","Dialog");
        ret.put("TITLE_FONT_SIZE",new Integer(12));
        ret.put("TITLE_FONT_FAMILY",new Integer(0));
        ret.put("TITLE_FONT_COLOR", "#000000");
        ret.put("FONT_FACE","Dialog");
        ret.put("FONT_SIZE",new Integer(12));
        ret.put("FONT_FAMILY", new Integer(0));
        ret.put("FONT_COLOR", "#000000");
        ret.put("HALIGNMENT", "LEFT");
        ret.put("VALIGNMENT", "CENTER");
        ret.put("BG_COLOR", "#FFFFFF");
        ret.put("SIZE", new Integer(100));
        ret.put("EDITABLE", "READONLY");        
        return ret;
    }
	public int getType(){return type;}

}
