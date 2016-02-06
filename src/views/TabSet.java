
package views;

import java.awt.*;
import java.util.*;
import symantec.itools.awt.*;
import rml.*;
import calc.*;
import calc.objects.*;

public class TabSet extends TabPanel implements Retrieveable,
        class_method, class_type,GlobalValuesObject{
    String font_face="Serif";
    int font_family=0;
    int font_size=12;
    public int bg_color = Color.lightGray.getRGB();
    boolean tabs_on_top = true;
    String[] labels;

    public TabSet() {
        super();
        setFont(new Font(font_face,font_family,font_size));
    }

     public void retrieve() {
        int count = countTabs();
        for (int i = 0; i < count; i++) {
            Component c = getTabPanel(i);
            if (c instanceof views.Retrieveable) ((views.Retrieveable)c).retrieve();
        }
     }
     public void update() {
        int count = countTabs();
        for (int i = 0; i < count; i++) {
            Component c = getTabPanel(i);
            if (c instanceof views.Retrieveable) ((views.Retrieveable)c).update();
        }
     }
     public void toDS(){
        int count = countTabs();
        for (int i = 0; i < count; i++) {
            Component c = getTabPanel(i);
            if (c instanceof views.Retrieveable) ((views.Retrieveable)c).toDS();
        }
     }
     public void fromDS(){
        int count = countTabs();
        for (int i = 0; i < count; i++) {
            Component c = getTabPanel(i);
            if (c instanceof views.Retrieveable) ((views.Retrieveable)c).fromDS();
        }
     }
    /* begin of init */
    public void init(Proper prop){
        String sp;
        Integer ip;
        if (prop!=null){
            sp = (String)prop.get("FONT_FACE");
            if (sp!=null) {
                font_face = sp;
            }
            ip = (Integer)prop.get("FONT_FAMILY");
            if (ip!=null) {
                font_family = ip.intValue();
            }
            ip = (Integer)prop.get("FONT_SIZE");
            if (ip!=null) {
                font_size = ip.intValue();
            }
            setFont(new Font(font_face,font_family,font_size));

            sp = (String)prop.get("BG_COLOR");
            if (sp!=null) {
                try {
                    int red = Integer.parseInt(sp.substring(1,3),16);
                    int green = Integer.parseInt(sp.substring(3,5),16);
                    int blue = Integer.parseInt(sp.substring(5,7),16);
                    bg_color = ((red<<16) + (green<<8) + blue);
                }
                catch(Exception e) {
                    System.out.println("~views.TabSet::init() : " + e.getMessage());
                }
            }
            setBackground(new Color(bg_color));
            sp = (String)prop.get("TABS_ON_TOP");
            if (sp!=null) {
                if (sp.equals("YES")) tabs_on_top = true;
                if (sp.equals("NO")) tabs_on_top = false;
            }
            try {
                setTabsOnBottom(!tabs_on_top);
            }
            catch(Exception e) {
                    System.out.println("~views.TabSet::init() : " + e.getMessage());
            }

            sp = (String)prop.get("LABELS");
            if (sp!=null) {
                labels = parseLabels(sp);
                try {
                    setPanelLabels(labels);
                }
                catch(Exception e) {
                    System.out.println("~views.TabSet::init() : " + e.getMessage());
                }
            }

        }
    }
    /* end of init */

    String[] parseLabels(String s) {
        StringTokenizer st = new StringTokenizer(s,",");
        int count = st.countTokens();
        String[] work = new String[count];
        for (int i = 0; i < count; i++) {
            work[i] = st.nextToken();
        }
        return work;
    }

    public void addChildren(Object[] objs) {
        if (objs==null) return;
        try {
            for (int i = 0; i < objs.length; i++) {
                Component c = (Component)objs[i];
                addTabPanel(labels[i],true,c);

            }
        }
        catch(Exception e) {
            System.out.println("~views.TabSet::addChildren() : " + e.getMessage());
        }
    }

    public void setValue(Object value){}
    public Object getValue(){return this;}
    public Object getValueByName(String name){return null;}
    public void setValueByName(String name,Object value){}

    public Object method(String method,Object arg ) throws Exception{
        if (method.equals("SETCURRENTTAB")) {
            if (!(arg instanceof Double)) return new Nil();
            int cur = ((Double)arg).intValue();
            setCurrentTab(cur);
            System.out.println("method setCurentTab in views.TabSet called");
        }else throw new RTException("HasNotMethod","method "+method+
							  " not defined in class views.TabSet!");
		return new Nil();
    }
    
    public String type(){return "TABSET";}

}
