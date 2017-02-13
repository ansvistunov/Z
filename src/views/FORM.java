
package views;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.math.*;
import java.text.*;
import rml.Proper;
import rml.RmlMem2File;
import views.edit.EditMaketAdapter;
import dbi.*;
import loader.GLOBAL;
import calc.*; 
import calc.objects.class_method;

public class FORM extends Panel implements Retrieveable,
        GlobalValuesObject,class_method{
    //private Panel panel = new Panel();
    private Vector childs = new Vector();//дети-Fields(и только они!)
    private int left,top,width,height;
    private String font_face = "Serif";//им€ фонта
    private int font_family =  0;//стиль фонта(Plain,Bold,Italic)
    private int font_size = 12;
    private int font_color = 0, bg_color = 16777215, font_bg_color = 0;
    private int title_font_color = 0, title_bg_color = 16777215, title_font_bg_color = 0;
    private String title_font_face;
    private int title_font_family;
    private int title_font_size;
    private String alias; 
    private String colontitul;    
    private DATASTORE ds;
    PopupMenu popupMenu;
    views.Menu menu;
    ActionListener popupAL;
    Hashtable aliases;
   
    
    
    
    
    public FORM() {
        super();
        setLayout(null);
        setFont(new Font(font_face,font_family,font_size));
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        addKeyListener(EditMaketAdapter.createEditMaketAdapter(this));
        //enableEvents(AWTEvent.KEY_EVENT_MASK);
        //panel.setLayout(null);
        //add(panel);
    }
    
    public DATASTORE getDatastore() {
        return ds;
    }
    
    public void addChildren(Object[] children) {        
        if (children==null||children.length==0) return;
        //System.out.println("number of child in form="+children.length);
        for (int i = 0; i < children.length; i++) {
            Object o = children[i];
            if (o instanceof DATASTORE) {
                setDATASTORE((DATASTORE)o);                
                break;
            }
        }
        if (ds==null) {//если датасторе не указано, создаем фиктивное
            ds = new dbi.DATASTORE();
            ds.setSql("select user from dual");
        }
        for (int i = 0; i < children.length; i++) {
            Object o = children[i];
            if (o instanceof views.Field) {
                
                //≈сли target=null, значит это вычисл€емое поле,
                //не св€занное с базой данных.Ќо соответствующий
                //столбец в Datastore об€зан быть.
                if (((Field)o).gettarget() == null) {
                    int type = ((Field)o).getType();
                    if (type==Integer.MIN_VALUE) {
                        System.out.println("views.FORM.addChildren says : type for computed field not defined!");
                        continue;
                    }
                    ((Field)o).settarget(ds.addColumn(type));
                }
                add((Field)o);
                continue;
            }
            if (o instanceof views.Label) {super.add((views.Label)o);continue;}
            if (o instanceof DATASTORE) {setDATASTORE((DATASTORE)o);continue;}
            if (o instanceof views.Menu) {
                popupMenu = new PopupMenu();
                popupAL = new PopupAL();
                menu = (views.Menu)o;
                java.awt.Menu m = menu.getMenu();
                if (m==null) {
                    System.out.println("~views.FORM::addChildren:popmenu=null");
                    continue;
                }
                int ic = m.getItemCount();
                for (int j=0;j<ic;j++) {
                    MenuItem mi = m.getItem(0);                        
                    if (mi==null) continue;
                    mi.addActionListener(popupAL);
                    popupMenu.add(mi);
                }
                add(popupMenu);                    
                menu.setMenu(popupMenu);
            }
            if (o instanceof views.RadioGroup) {
                super.add((views.RadioGroup)o);
            }
            if (o instanceof views.CheckGroup) {
                super.add((views.CheckGroup)o);
            }
			if (o instanceof views.IMage){
				super.add((views.IMage)o);
			}
			if (o instanceof views.mButton) {
				super.add((views.mButton)o);
			}
			if (o instanceof views.FORM) {
				super.add((views.FORM)o);
			}
        }        
    }
    
    public void setDATASTORE(DATASTORE ds) {
        this.ds = ds;
    }
    
    public void retrieve() {        
        if (ds == null) {
            //if (GLOBAL.views_debug>0)
            System.out.println("~views.FORM::retrieve() : ds = null");
            return;
        }
        
        try {
            ds.retrieve();            
            fromDS();
        }catch(Exception e) {
            System.out.println("~views.FORM::retrieve() : Catch exception from DATASTORE.retrieve() : " + e);
            return;
        }
        //вызываем retrieve дл€ всех встроенных Retriveable
        Component[] components = this.getComponents();
        for (int i=0;i<components.length;i++){
        	if (components[i] instanceof Retrieveable) {
        		((Retrieveable)components[i]).retrieve();
        	}
        }
        
        
    }
    
    public void update() {
        toDS();
        try {
            ds.update();        
        }
        catch(SQLException e) {System.out.println("~views.FORM::update() : " + e);}
    }
    
    public void fromDS(){
        if (GLOBAL.views_debug>0)
        System.out.println("fromDS in form called !");
        
        for (int i = 0; i < childs.size(); i++) {
            views.Field f = (views.Field)childs.elementAt(i);
            if (f==null) continue;
            String col = f.gettarget();
            if (col==null) {
                if (GLOBAL.views_debug>0)
                System.out.println("~views.FORM::fromDS() : target not defined for field "+f);
                continue;
            }
            if (col.indexOf(DATASTORE.compute)==0) {//значит, target дл€ этого филда начинаетс€ c @@COMPUTE
                try {
                    f.setValue(f.validator.toObject(f.gettext()));
                }catch(Exception e) {f.settext(null);}
                continue;
            }
	        if (GLOBAL.views_debug>0)
	            System.out.println("Field.retrieve...colName="+col);        
            
            Object value = null;
            try {                
                value = ds.getValue(0,col);
            }catch(Exception e) {
                System.out.println("exception in views.FORM::fromDS() :"+e);
            }
            int type = 0;
            try{
                type = ds.getType(col);
            }catch(Exception e) {
                System.out.println("~views.FORM::fromDS() : Cannot get type! Ex="+e);
            }
            
            if (value==null) {
                //value = new String() 
                //continue;
            }  
            if (GLOBAL.views_debug>0)
                System.out.println("views.FORM::fromDS() says : value="+value);  
            
            f.setType(type);
            if (GLOBAL.views_debug>0)
                System.out.println("views.FORM::fromDS() says : Type ="+ type);            
            f.setValue(value);
        }        
        for (int i = 0; i < childs.size(); i++) {
            views.Field f = (views.Field)childs.elementAt(i);
            if (f!=null) f.calcDep();
        }
    } 
    
    public void toDS(){
        if (GLOBAL.views_debug>0)
        System.out.println("views.FORM::toDS() called");
        
        
        for (int i = 0; i < childs.size(); i++) {
            views.Field f = (views.Field)childs.elementAt(i);
            if (f==null) {
                System.out.println("~views.FORM.toDS : child-field is null!");
                continue;
            }
            if (f.editField!=null) {
                if (!f.fromEditField(f.editField.getvalue())){                    
                    throw new Error("Bad value from field!");
                }
            }            
        }

    }
    
    public void setSize(Dimension d) {
        super.setSize(d);
        //panel.setSize(d);
        width = d.width;
        height = d.height;
    }
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x,y,width,height);
        //panel.setBounds(0,0,x+width, y+height);
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
    public void setfont_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1,3),16);
            int green = Integer.parseInt(color.substring(3,5),16);
            int blue = Integer.parseInt(color.substring(5,7),16);
            setfont_color_i((red<<16) + (green<<8) + blue);
        }
        catch(Exception e) {System.out.println("Exception inside Field.setfont_color: " + e.getMessage());}
        
    }
    public void setfont_color_i(int color) {
        setForeground(new Color(color));
        //Color col = getForeground();
        font_color = color;


    }

    public void setbg_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1,3),16);
            int green = Integer.parseInt(color.substring(3,5),16);
            int blue = Integer.parseInt(color.substring(5,7),16);
            setbg_color_i((red<<16) + (green<<8) + blue);
        }
        catch(Exception e) {System.out.println("Exception inside Field.setbg_color: " + e.getMessage());}
        
    }
    public void setbg_color_i(int color) {
        setBackground(new Color(color));
        bg_color = color;
    }

    public void setalias(String alias) {
        this.alias = alias;
    }
    
    public void setcolontitul(String colontitul) {
        this.colontitul = colontitul;
    }
    
    public String getcolontitul() {
        return colontitul;
    }

    
    public void add(Field f) {
        super.add(f);
        childs.addElement(f);
    }
    public void init(Proper prop, Hashtable aliases) {
        Integer ip;
        String sp;
        int left=0;
        int top=0;
        int width=0;
        int height=0;
        this.aliases = aliases;
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) left = ip.intValue(); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'left' not found !");
        }
        
        ip = ((Integer)prop.get("TOP"));
        if (ip!=null) top = ip.intValue(); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'top' not found !");
        }

        ip = ((Integer)prop.get("WIDTH"));
        if (ip!=null) width = ip.intValue(); else {System.out.println("Property 'width' not found !");}
        
        ip = ((Integer)prop.get("HEIGHT"));
        if (ip!=null) height = ip.intValue(); else {System.out.println("Property 'height' not found !");}
        
        setBounds(left,top,width,height);
        
        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) setfont_face(sp); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'font_face' not found !");
        }
        
        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) setfont_family(ip.intValue()); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'font_family' not found !");
        }
        

        ip = ((Integer)prop.get("FONT_SIZE"));
        if (ip!=null) setfont_size(ip.intValue()); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'font_size' not found !");
        }
    
        
        sp = ((String)prop.get("FONT_COLOR"));
        if (sp!=null) setfont_color(sp); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'font_color' not found !");
        }


        sp = ((String)prop.get("BG_COLOR"));
        if (sp!=null) setbg_color(sp); else {
            if (GLOBAL.views_debug>0)
            System.out.println("Property 'bg_color' not found !");
        }
        
        EditMaketAdapter.getEditMaketAdapter(this).setAliases(aliases);
        
        sp = (String)prop.get("ALIAS");
        if (sp!=null) setalias(sp); else {System.out.println("Property 'alias' not found !");
        
        //////////////////////////////////////////////////////////////////
        
        ///////////////////////////////////////////////////////////////////////
        }
    }
    
    public Field getField(String name) {
        for (int i = 0;i<childs.size();i++) {
            Field f = (Field)childs.elementAt(i);
            if ( f.getalias().equals(name)) return f;
        }
        return null;
    }
    
    //методы интерфейса GlobalValuesObject
    public Object getValue(){return this;} 
    public void setValue(Object o){}
    public void setValueByName(String name,Object o){}
    public Object getValueByName(String name) {
        Field f = getField(name);
        Object ob =null;
        if (f!=null) ob = f.getValue();
        //if (ob==null) System.out.println("!!!!!!!!ob=null");
        return ob;
        //if (ob!=null) return ob; 
        //else return "NULL";//null;
    }
    
    public void processMouseEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(this,e.getX(),e.getY());
        }
        if (e.getButton() == MouseEvent.BUTTON1) requestFocus();
    }
    
    
    
    /*
     	public boolean keyDown(Event e,int key) {
    	System.out.println("views.Form:: KeyDown");
    	return false;
    }
    */
    
    
    
    
    
    
    
    
    class PopupAL implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                document.ACTION.doAction(command,aliases,null);
            }
            catch(Exception ex) {
                System.out.println("exception inside document.ACTION:doAction : "+e);
            }
            if (e.getSource() instanceof views.Item) {
                Calc calc = ((views.Item)e.getSource()).getCalc();
                if (calc!=null) {
                    try {
                        calc.eval(aliases);
                    }catch(Exception ex) {
                        System.out.println("views.Grid$PopupAL::actionPerformed : "+ex);
                    }
                }
            }
        }
    }
    
    
	    
    
    //ћетоды интерфейса class_method
    public Object method(String method,Object arg) throws Exception{

        if (method.equals("GETMENU")){
                 return menu;
        }else if (method.equals("SETMENU")){
         try{
                popupMenu = new PopupMenu();
                if(popupAL==null) popupAL = new PopupAL();
                //java.awt.Menu m = ((views.Menu)arg).getMenu();
                views.Menu m = (views.Menu)arg;
                int ic = m.getItemCount();
                for (int j=0;j<ic;j++) {
                    MenuItem mi = m.getItem(0);                        
                    if (mi==null) continue;
                    mi.removeActionListener(popupAL);
                    mi.addActionListener(popupAL);
                    popupMenu.add(mi);
					}
                add(popupMenu);
                menu = m;
                menu.setMenu(popupMenu);    

         }catch(Exception e){
            throw new RTException("RunTime"," Exception "+e.getMessage()+" in method setmenu (svr_grid )");
					
				}

         return new Double(0);

		}
        return new Double(0);
	}
    
    
}

