package views;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import loader.GLOBAL;
public class BASE_FIELD extends MyTextArea/*TextField*/ {
    //поля(свойства) класса FIELD
    private int left,top,width,height;
    private String font_face = "Serif";//имя фонта
    private int font_family =  0;//стиль фонта(Plain,Bold,Italic)
    private int font_size = 12;
    private int font_color = 0, bg_color = 16777215, font_bg_color = 0;
    private int title_font_color = 0, title_bg_color = 16777215, title_font_bg_color = 0;
    private String title_font_face;
    private int title_font_family;
    private int title_font_size;
    private String alias;
    private String exp;
    private int border;
    private String editable = "HAND";
    private String target=null;//имя поля DataStore для операций Retrieve/Update
    private String handbook;//имя справочника, из которого возможен выбор данных
                            //для данного FIELD
    private String title;
    private String text;
    private String dep;
    private String handdep;
    //views.EditField par = null;
    public BASE_FIELD(){
        super();
        //this.par = par;
        //enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        setFont(new Font(font_face,font_family,font_size));
    }
    public BASE_FIELD(int f){
        super(0);        
        //this.par = par;
        //enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        setFont(new Font(font_face,font_family,font_size));
    }
    /*public BASE_FIELD(String text){
        super(GLOBAL.c2b(text,GLOBAL.FIELD));
        //this.par = par;
        //enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        //setFont(new Font(font_face,font_family,font_size));
    }*/
    public void setSize(Dimension d) {
        super.setSize(d);
        width = d.width;
        height = d.height;
    }
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x,y,width,height);
        this.left = x;
        this.top = y;
        this.width = width;
        this.height = height;
    }

    public synchronized void setfont_face(String name) {
        Font f = new Font(name,font_family,font_size);
        if (f != null ) {
            setFont(f);
            this.font_face = name;
        }

    }

    public String getfont_face(){return font_face;}

    public synchronized void setfont_family(int fam){
        Font f = new Font(font_face,fam,font_size);
        if (f!=null) {
            setFont(f);
            font_family = fam;
        }
    }

    public int  getfont_family(){return font_family;}

    public synchronized void setfont_size(int size) {
        Font f = new Font(font_face,font_family,size);
        if (f != null) {
            setFont(f);
            font_size = size;
        }
    }
    public void setfont_color(int color) {
        setForeground(new Color(color));
        //Color col = getForeground();
        font_color = color;


    }

    public void setbg_color(int color) {
        setBackground(new Color(color));
        bg_color = color;
    }

    public void setalias(String alias) {
        this.alias = alias;
    }
    
    public void settarget(String target) {
        this.target = target;
    }    

    public String gettarget() {
        return target;
    }
    
    public String getalias() {return alias;}

    public void setexp(String exp) {
        this.exp = exp;
    }

    public String getexp() {return exp;}

    public void seteditable(String edit) {
        this.editable = edit;        
        if (edit.compareTo("READONLY")==0 || 
            edit.compareTo("HANDBOOK")==0) {
            setEditable(false);
        }
        else setEditable(true);
    }

    public String geteditable() {return editable;}

    public void sethandbook(String handbook) {
        this.handbook = handbook;
    }

    public String gethandbook(){return handbook;}

    public void settitle(String title) {
        this.title = title;
    }

    public String gettitle() {return title;}

    public void settext(String text) {
        setText(GLOBAL.c2b(text,GLOBAL.FIELD));
        this.text = text;
    }

    public String gettext() {
        //System.out.println("inside gettext in BASE_FIELD");
        return text=GLOBAL.b2c(getText(),GLOBAL.FIELD);/*GLOBAL.CONST*/
    }

    public void setborder(int border) {
        this.border = border;
    }

    public int getborder() {return border;}

    public void setdep(String dep) {
        this.dep = dep;
    }
    
    public void sethanddep(String handdep) {
        this.handdep = handdep;
    }

    public String getdep() {
        return dep;        
    } 
    
}
