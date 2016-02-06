package views;
import rml.*;
import java.awt.*;
import java.util.StringTokenizer;
import loader.GLOBAL;
import calc.*;
import java.util.*;

public class Column implements GlobalValuesObject{
    private String title_font_face="Serif";
    private int title_font_size=12;
    private int title_font_family=0;
    //int title_font_color = 0;
    Color title_font_color = Color.black;
    Font title_font = null;

    public int type = Integer.MIN_VALUE;//говорит о том, что type не определен

    String alias=null;
    String visible = "YES";
    private String font_face="Serif";
    private int font_size=10;
    private int font_family=0;
    //int font_color= 0;
    Color font_color = Color.black;
    Font font = null;

    String halignment = "LEFT";
    String valignment = "CENTER";

    //int bg_color = Color.white.getRGB();
    Color bg_color = Color.white;

    private String editable="HAND";
    String target;
    String exp;
    String editExp;
    String edit;
    String[] depends = null;
    int size = 50;

    String title = "TITLE";

    int dw = 2;
    int dh = 2;

    FontMetrics fm = null;

    dbi.DATASTORE ds = null;

    Object parent = null;
    Hashtable aliases = null;

    public Validator validator = new Validator();
    Calc calc = null;

    public Column(){
    }

    public Column(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){return title;}

    public void setParent(Object p) {
        parent = p;
    }

    public Object getParent(){return parent;}

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        if (prop==null) return;
        this.aliases = aliases;

        sp = (String)prop.get("TITLE_FONT_FACE");
        if (sp!=null) title_font_face = sp;
        ip = (Integer)prop.get("TITLE_FONT_SIZE");
        if (ip!=null) title_font_size = ip.intValue();
        ip = (Integer)prop.get("TITLE_FONT_FAMILY");
        if (ip!=null) title_font_family = ip.intValue();
        sp = (String)prop.get("TITLE_FONT_COLOR");
        if (sp!=null) title_font_color = UTIL.getColor(sp);

        title_font = new Font(title_font_face,title_font_family,title_font_size);

        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) font_face = sp;
        ip = (Integer)prop.get("FONT_SIZE");
        if (ip!=null) font_size = ip.intValue();
        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) font_family = ip.intValue();
        sp = (String)prop.get("FONT_COLOR");
        if (sp!=null) font_color = UTIL.getColor(sp);

        font = new Font(font_face,font_family,font_size);
        fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

        sp = (String)prop.get("HALIGNMENT");
        if (sp!=null) halignment = sp;
        sp = (String)prop.get("VALIGNMENT");
        if (sp!=null) valignment = sp;

        sp = (String)prop.get("BG_COLOR");
        if (sp!=null) bg_color = UTIL.getColor(sp);

        ip = (Integer)prop.get("SIZE");
        if (ip!=null) size = ip.intValue();

        sp = (String)prop.get("ALIAS");
        if (sp!=null) alias = sp;
        sp = (String)prop.get("VISIBLE");
        if (sp!=null) visible = sp;
        sp = (String)prop.get("EDITABLE");
        if (sp!=null) editable = sp;
        sp = (String)prop.get("TARGET");
        if (sp!=null) target = sp;
        sp = (String)prop.get("DEP");
        if (sp!=null) setdep(sp);
        sp = (String)prop.get("EXP");
        if (sp!=null) exp = sp;
        sp = (String)prop.get("EDITEXP");
        if (sp!=null) editExp = sp;
        sp = (String)prop.get("EDIT");
        if (sp!=null) edit = sp;
        sp = (String)prop.get("TITLE");
        if (sp!=null) title = sp;
        sp = (String)prop.get("TYPE");
        if (sp!=null) setType(sp);
        sp = (String)prop.get("EDITMASK");
        if (sp!=null) validator.setMask(sp);

        if (exp!=null) calc = new Calc(exp);
    }

    public String getEditable(){return editable;}

    public void addChildren(Object[] objs) {
        if (objs==null) return;
        for (int i=0;i<objs.length;i++) {
            if (objs[i] instanceof dbi.DATASTORE) {
                ds = (dbi.DATASTORE)objs[i];
            }
        }
    }

    public void retrieve() {
        try {
            if (ds!=null) ds.retrieve();
        }
        catch(Exception e) {
            System.out.println("~views.Column::retrieve() : "+e);
        }
    }
    void setdep(String dep) {
        dep = dep.toUpperCase();
        dep = dep.trim();
        StringTokenizer st = new StringTokenizer(dep,",");
        int count = st.countTokens();
        if (count==0) return;
        depends = new String[count];
        for (int i = 0; i < count; i++) {
            depends[i] = st.nextToken().trim().toUpperCase();
            if (GLOBAL.views_debug > 0)
            System.out.println(depends[i]);
        }

    }
    public boolean isVisible() {
        return visible.equals("YES");
    }

    public void setType(String t) {
        t = t.toUpperCase();
        if (t.equals("NUMBER")) type = java.sql.Types.NUMERIC;
        if (t.equals("STRING")) type = java.sql.Types.VARCHAR;
        if (t.equals("DATE"))   type = java.sql.Types.DATE;
        validator.setType(Grid.getJType(type));
    }

    public void setType(int type) {
        this.type = type;
        //System.out.println("inside column setType");
        validator.setType(Grid.getJType(type));
    }

    public int getType() {return type;}

    public dbi.DATASTORE getParentDS() {
        if (parent instanceof views.Grid)
            return ((views.Grid)parent).ds;
        if (parent instanceof views.ReportGrid)
            return ((views.ReportGrid)parent).ds;
        return null;
    }

    public void setValue(Object o) {
        dbi.DATASTORE ds = null;
        if ((ds = getParentDS()) != null) {
            ds.setValue(target,o);
            if (depends!=null) {
                //System.out.println("calculating depends for column");
                calcDep();
            }
        }
    }

    public Object getValue() {
        dbi.DATASTORE ds = null;
        if ((ds=getParentDS()) != null)
            return ds.getValue(target);
        else return null;
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void calcHandbookDep() {
        //System.out.println("alias="+aliases);
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.Column c = (views.Column)aliases.get(depends[i]);
            if (c!=null) {
                c.calcHandbookExp();
            }
            else System.out.println("~views.Column::calcDep() : object views.Column not found for alias "+depends[i]);
        }
    }
    public void calcDep() {
        //System.out.println("alias="+aliases);
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.Column c = (views.Column)aliases.get(depends[i]);
            if (c!=null) {
                c.calc();
            }
            else System.out.println("~views.Column::calcDep() : object views.Column not found for alias "+depends[i]);
        }
    }
    public void calcHandbookExp() {
        try{
            if (editExp!=null) {
                Calc c = new Calc(editExp);
                if (c!=null) c.eval(aliases);
            }
        }catch(Exception e) {
            System.out.println("~views.Field::calcHandbookExp() : "+e);
        }
    }

    public void calc() {
        //System.out.println("calc expression "+getexp());
        try{
            if (calc!=null) calc.eval(aliases);
        }catch(Exception e) {
            //e.printStackTrace();
            System.out.println("~views.Column::calc() : "+e);
        }

    }

}
