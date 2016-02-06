package views;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.math.*;
import java.sql.*;
import java.text.*;
import rml.*;
import calc.*;
import document.NotifyInterface;
import loader.GLOBAL;
public class EditField extends Panel implements
    GlobalValuesObject,NotifyInterface{
    private BASE_FIELD field = null;//new BASE_FIELD(this);
    private Button button = new Button("...");
    public static final int BW = 20;
    private int bw = BW;
    int type;
    Object parent = null;
    public boolean alwaysShowButton = true;
    String[] depends = null;
    Hashtable aliases =null;
    String edit=null;//строка-action при редактировании путем выбора из справочника
    String editExp=null;//строка-выражение, описывающая связь между значением FIELD'а
                        //и столбцами DATASTORE, которое возвращено из справочника
    String visible="YES";
    /******************/
    public TextComponent getBaseField(){return field;}
    /******************/
    public Validator validator = new Validator();
    public EditField(boolean multiLine){
        super();
        if (!multiLine) field = new BASE_FIELD();
        else field = new BASE_FIELD(0);
        setLayout(null);
        add(field);
        add(button);
        button.addActionListener(new ButtonListener());
        enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        //button.addFocusListener(new FL(this));
        //field.addFocusListener(new FL(this));
        //field.addKeyListener(new KL(this));
        showButton(false);button.disable();
    }
    public EditField(String text, boolean multiLine){
        this(multiLine);
        settext(text);
    }

    public FORM getFormParent() {
        Container parent = super.getParent();
        if (parent instanceof FORM) return (FORM)parent;else return null;
    }

    public Object getFieldParent(){return parent;}
    public void setFieldParent(Object parent) {this.parent = parent;}
    public void init(Proper prop, Hashtable aliases) {
        Integer ip;
        String sp;
        int LEFT=0;
        int TOP=0;
        int width=0;
        int height=0;
        this.aliases = aliases;
        sp = (String)prop.get("ALWAYSSHOWBUTTON");
        if (sp!=null) {
            if (sp.equals("YES")) alwaysShowButton = true;
            if (sp.equals("NO")) alwaysShowButton = false;
        }
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) LEFT = ip.intValue();

        ip = ((Integer)prop.get("TOP"));
        if (ip!=null) TOP = ip.intValue();

        ip = ((Integer)prop.get("WIDTH"));
        if (ip!=null) width = ip.intValue();

        ip = ((Integer)prop.get("HEIGHT"));
        if (ip!=null) height = ip.intValue();

        setBounds(LEFT,TOP,width,height);

        sp = (String)prop.get("EDITABLE");
        if (sp!=null) seteditable(sp);
        else seteditable("HAND");

        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) setfont_face(sp);

        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) setfont_family(ip.intValue());


        ip = ((Integer)prop.get("FONT_SIZE"));
        if (ip!=null) setfont_size(ip.intValue());


        sp = ((String)prop.get("FONT_COLOR"));
        if (sp!=null) setfont_color(sp);


        sp = ((String)prop.get("BG_COLOR"));
        if (sp!=null) setbg_color(sp);

        sp = (String)prop.get("ALIAS");
        if (sp!=null) setalias(sp);

        sp = (String)prop.get("VISIBLE");
        if (sp!=null) visible = sp;

        sp = (String)prop.get("EXP");
        if (sp!=null) setexp(sp);

        sp =(String)prop.get("DEP");
        if (sp!=null) setdep(sp);
        sp = (String)prop.get("TARGET");
        if (sp!=null) settarget(sp);

        sp = (String)prop.get("VALUE");
        if (sp!=null) settext(sp);

        sp = (String)prop.get("EDIT");
        if (sp!=null) edit=sp;

        sp = (String)prop.get("EDITEXP");
        if (sp!=null) editExp = sp;

        if (visible.equals("NO")) setVisible(false);
        if (alwaysShowButton) {
                String ed = geteditable();
                if ( ed.compareTo("HANDBOOK")==0 || ed.compareTo("ALL")==0) showButton(true);
                else {showButton(false);button.disable();}
            }

    }

    /*----------------*/
    public void setBounds(Rectangle r) {
        setBounds(r.x,r.y,r.width,r.height);
    }
    /*----------------*/
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x,y,width,height);
        field.setBounds(0,0,width - bw,height);
        button.setBounds(width - bw,1,bw,height-1);
    }

    public synchronized void setfont_face(String name) {
        field.setfont_face(name);
    }

    public String getfont_face(){return field.getfont_face();}

    public synchronized void setfont_family(int fam){
        field.setfont_family(fam);
    }

    public int  getfont_family(){return field.getfont_family();}

    public synchronized void setfont_size(int size) {
        field.setfont_size(size);
    }
    public void setfont_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1,3),16);
            int green = Integer.parseInt(color.substring(3,5),16);
            int blue = Integer.parseInt(color.substring(5,7),16);
            field.setfont_color((red<<16) + (green<<8) + blue);
        }
        catch(Exception e) {System.out.println("Exception inside EditField.setfont_color: " + e.getMessage());}

    }
    public void setfont_color_i(int color) {
        field.setfont_color(color);
    }

    public void setbg_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1,3),16);
            int green = Integer.parseInt(color.substring(3,5),16);
            int blue = Integer.parseInt(color.substring(5,7),16);
            field.setbg_color((red<<16) + (green<<8) + blue);
        }
        catch(Exception e) {System.out.println("Exception inside EditField.setbg_color: " + e.getMessage());}

    }
    public void setbg_color_i(int color) {
        field.setbg_color(color);
    }

    public void setalias(String alias) {
        field.setalias(alias);
    }

    public String getalias() {return field.getalias();}


    public void settarget(String t) {
        field.settarget(t);
    }

    public String  gettarget() {
        return field.gettarget();
    }
    public void setexp(String exp) {
        field.setexp(exp);
    }

    public String getexp() {return field.getexp();}

    public void seteditable(String edit) {
        //System.out.println("--------------seteditable run-------------");
        field.seteditable(edit);
        if (edit.equals("HANDBOOK") || edit.equals("ALL")) showButton(true);

    }

    protected void showButton(boolean show) {
        if (show) {
            bw = BW;
            button.enable();
            setBounds(getBounds());
        }
        else {
            bw = 0;
            //button.disable();
            setBounds(getBounds());
        }
    }
    public String geteditable() {return field.geteditable();}

    public void sethandbook(String handbook) {
        field.sethandbook(handbook);
    }

    public String gethandbook(){return field.gethandbook();}

    public void settitle(String title) {
        field.settitle(title);
    }

    public String gettitle() {return field.gettitle();}

    public void setValue(Object o) {
        //System.out.println( "SetValue  O="+o);
        try {
            setvalue(validator.toString(o));
        }catch(Exception e) {
            System.out.println("~views.EditField::setValue : "+e);
        }
        
    }
    public void setvalue(String text) {
        field.settext(text);
    }
    public void settext(String text) {
        field.settext(text);
    }

    public Object getValue() {
        try {
            return validator.toObject(getvalue());
        }catch(Exception e) {
            System.out.println("~views.EditField::getValue : "+e);
            return null;
        }
    }




    public String getvalue() {
        return field.gettext();
    }
    public String gettext() {return field.gettext();}

    public void setborder(int border) {
        field.setborder(border);
    }

    public int getborder() {return field.getborder();}

    public void setdep(String dep) {
        //field.setdep(dep);
        dep = dep.toUpperCase();
        dep = dep.trim();
        StringTokenizer st = new StringTokenizer(dep,",");
        int count = st.countTokens();
        if (count==0) return;
        depends = new String[count];
        for (int i = 0; i < count; i++) {

            depends[i] = st.nextToken().trim();
            if (GLOBAL.views_debug > 0)
            System.out.println(depends[i]);
        }

    }

    public void sethanddep(String handdep) {
        field.sethanddep(handdep);
    }


    public String[] getdep() { return depends;//return field.getdep();

    }

    public void calcHandbookDep() {
        //System.out.println("alias="+aliases);
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.EditField f = (views.EditField)aliases.get(depends[i]);
            if (f!=null) {
                f.calcHandbookExp();
            }
            else System.out.println("~views.EditField::calcDep() : object views.EditField not found for alias "+depends[i]);
        }
    }
    public void calcDep() {
        //System.out.println("alias="+aliases);
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.EditField f = (views.EditField)aliases.get(depends[i]);
            if (f!=null) {
                f.calc();
            }
            else System.out.println("~views.EditField::calcDep() : object views.EditField not found for alias "+depends[i]);
        }
    }
    public void calcHandbookExp() {
        try{
            if (editExp!=null) {
                Calc c = new Calc(editExp);
                if (c!=null) c.eval(aliases);
            }
        }catch(Exception e) {
            System.out.println("~views.EditField::calcHandbookExp() : "+e);
        }
    }

    public void calc() {
        //System.out.println("calc expression "+getexp());
        try{
            if (getexp()!=null) {
                Calc c = new Calc(getexp());
                if (c!=null) c.eval(aliases);
            }
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("~views.EditField::calc() : "+e);
        }

    }

    public void setValueByName(String str,Object o) {}
    public Object getValueByName(String str) {return null;}

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == button) {
                bclick(EditField.this,e);
            }
        }
    }

    public void bclick(EditField f,ActionEvent e) {
        if (edit!=null) {
            try {
                document.ACTION.doAction(edit,aliases,this);
            }
            catch(Exception ex) {
                System.out.println("~views.EditField::bclick : "+ex);
            }
        }
    }

    public boolean gotFocus(Event e, Object what) {
        if (!alwaysShowButton) {
            String ed = geteditable();
            if (ed.compareTo("HANDBOOK")==0 || ed.compareTo("ALL")==0) {showButton(true);}
        }
        return true;
    }
    public boolean lostFocus(Event e, Object what) {
        if (button.getSize().width!=0 && e!=null) return true;
        if (getParent()==null) return true;
        if (parent instanceof Field) {
            Field f = (Field)parent;
            Object val = null;
            try {
                //val = f.validator.toObject(field.getText());
                val = f.validator.toObject(gettext());
            }catch(Exception ex){}
            if (val!=null) {f.setValue(val);f.calcDep();}
            else {
                FORM fp=null;
                dbi.DATASTORE ds=null;
                if ((fp=f.getFormParent())!=null) {
                    if ((ds=fp.getDatastore())!=null) {
                        if (f.gettarget()!=null) {
                            f.setValue(ds.getValue(0,f.gettarget()));
                        }
                    }
                }
            }
			//
			
			//
            f.remove(f.editField);
            f.editField = null;
            f.inFocus = false;
            return true;            
        }
        
        /*if (!alwaysShowButton) {
            String ed = geteditable();
            if (ed.compareTo("HANDBOOK")==0 || ed.compareTo("ALL")==0) {showButton(false);}
        }
        if (depends!=null) calcDep();
        Object par = getFieldParent();
        if ((par!=null) && (par instanceof views.FORM)) {
            ((views.FORM)par).toDS();
            ((views.FORM)par).fromDS();
        }*/
        return true;
    }

    public void setedit(String edit){this.edit = edit;}
    public void setaliases(Hashtable aliases){this.aliases=aliases;}
    public boolean handleEvent(Event e) {
        if (e.id == Event.KEY_ACTION) {
            return super.handleEvent(e);
        }
        if (e.id == Event.KEY_PRESS) {
            if (e.key == Event.ESCAPE) return false;
            if (e.key == Event.ENTER && ((e.modifiers&Event.CTRL_MASK)==0)) {
                if (parent instanceof Field) {
                    //setVisible(false);
                    showButton(false);                    
                    lostFocus(null,null);                    
                    ((Field)parent).calcDep();
                    return true;
                }
                //System.out.println("Enter in edit field pressed");
                return false;
            }
            if ((e.key == ((int)'E'-64) ) && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                if (parent instanceof Field) {
                    //setVisible(false);
                    showButton(false);                    
                    lostFocus(null,null);                    
                    ((Field)parent).calcExtra();
                    return true;
                }
                //System.out.println("Enter in edit field pressed");
                return false;
            }
            return super.handleEvent(e);
        }        
        return super.handleEvent(e);
        
    }
    /*public void processKeyEvent(KeyEvent e) {
        //if (e.getID() == KeyEvent.KEY_ACTION) {

            //return super.handleEvent(e);
        //}
        System.out.println("processKeyEvent called in EditField");
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == Event.ESCAPE) {}//return false;
            if (e.getKeyCode() == Event.ENTER) {}//return false;
            //return super.processKeytEvent(e);
        }
        //return super.handleEvent(e);
    }*/

    public void notifyIt() {
        dbi.DATASTORE ds2 = (dbi.DATASTORE)aliases.get("STORE");
        System.out.println("Notify it in EditField called");
        System.out.println("ds2="+ds2);
        if (ds2!=null) {
            if (editExp!=null) {
                try {
                    Calc c = new Calc(editExp);
                    c.eval(aliases);
                }
                catch(Exception e) {
                    System.out.println("~views.EditField::notifyIt : " + e);
                }
                if (depends!=null) {
                    calcHandbookDep();
                    calcDep();
                }
            }
            if (parent instanceof NotifyInterface) {
                //if (parent instanceof views.Grid) {
                //    ((views.Grid)parent).processEnter();
                //}
                ((NotifyInterface)parent).notifyIt();
            }
        }        
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
        validator.setType(Grid.getJType(type));
    }
    
    public int getType() {return type;}
}
