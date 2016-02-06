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
public class Field extends Container implements
    GlobalValuesObject, NotifyInterface,Selectable {

	protected Object pValue=null;

    private String font_face = "Serif";
    private int font_family = 0;
    private int font_size = 12;

    Font scaleFont = null;//данный фонт используется для отрисов
                          //с масштабированием
    FontMetrics fm = null;
    int dw = 2;
    int dh = 2;

    String alias = null;
    String halignment = "LEFT";
    String valignment = "CENTER";
    String border = "3DLOWERED";
    boolean isComputed = false;

    int type = Integer.MIN_VALUE;//говорит о том, что type не определен
    boolean inFocus = false;
    EditField editField = null;
    Object parent = null;
    //public boolean alwaysShowButton = true;
    String[] depends = null;
    Hashtable aliases =null;
    String edit=null;//строка-action при редактировании путем выбора из справочника
    String editExp=null;//строка-выражение, описывающая связь между значением FIELD'а
                        //и столбцами DATASTORE, которое возвращено из справочника
    String exp = null;
    String extraexp = null;
    String target = null;
    String editable = "HAND";

    double extrah = 0;

    String visible="YES";

    String svalue = null;

    views.Validator validator = new Validator();
    Calc calc = null;
    Calc extracalc = null;
    boolean needTranslate = false;//для отчетов = true
                                  //для документов = false
    public boolean needSetString = true;//определяет, нужно ли устанавливать String'овое
                                        //значение при вызове setValue(Object)
    boolean multiLine = false;
    boolean wordWrap = false;
    
    Color bg_color;
    
    public Field(){
        super();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.KEY_EVENT_MASK);

    }

    public Field(String text){
        this();
        settext(text);
    }

    public FORM getFormParent() {
        Container parent = super.getParent();
        if (parent instanceof FORM) return (FORM)parent;else return null;
    }
    public Color getNotmalBgColor() {
    	return bg_color;
    	
    }

    public Object getFieldParent(){return parent;}
    public void setFieldParent(Object parent) {this.parent = parent;}
    public void setScaleFont(int a) {
        Font tmp = getFont();
        if (tmp==null) return;
        scaleFont = new Font(tmp.getName(),tmp.getStyle(), tmp.getSize()*a/100);
    }


    /*--------begin of init---------------------------*/
    public void init(Proper prop, Hashtable aliases) {
        Integer ip;
        String sp;
        int LEFT=0;
        int TOP=0;
        int width=0;
        int height=0;
        this.aliases = aliases;

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

        sp = (String)prop.get("FONT_FACE");
        if (sp!=null) font_face = sp;
        ip = (Integer)prop.get("FONT_FAMILY");
        if (ip!=null) font_family = ip.intValue();
        ip = ((Integer)prop.get("FONT_SIZE"));
        if (ip!=null) font_size = ip.intValue();
        scaleFont = new Font(font_face,font_family,font_size);
        setFont(scaleFont);
        fm = getFontMetrics(scaleFont);

        sp = ((String)prop.get("FONT_COLOR"));
        if (sp!=null) setForeground(UTIL.getColor(sp));
        else setForeground(Color.black);


        sp = ((String)prop.get("BG_COLOR"));
        if (sp!=null) setBackground(UTIL.getColor(sp));
        else setBackground(Color.white);

        bg_color = this.getBackground(); //нужно для обработки выделения
        
        
        sp = (String)prop.get("ALIAS");
        if (sp!=null) alias = sp;

        sp = (String)prop.get("VISIBLE");
        if (sp!=null) visible = sp;

        sp = (String)prop.get("EXP");
        if (sp!=null) {
            exp = sp;isComputed = true;
            calc = new Calc(sp);
        }

        sp = (String)prop.get("EXTRAEXP");
        if (sp!=null) {
            extraexp = sp;
            extracalc = new Calc(sp);
        }

        sp =(String)prop.get("DEP");
        if (sp!=null) setdep(sp);
        sp = (String)prop.get("TARGET");
        if (sp!=null) settarget(sp);


        sp = (String)prop.get("EDIT");
        if (sp!=null) {
            edit=sp;
            calc = new Calc(sp);
        }

        sp = (String)prop.get("EDITEXP");
        if (sp!=null) {
            editExp = sp;
            calc = new Calc(sp);
        }

        ip = (Integer)prop.get("EXTRAH");
        if (ip!=null) extrah = ip.intValue();
        else
        extrah = Integer.parseInt(GLOBAL.pr(GLOBAL.DEFAULT_EXTRAH,"0"));

//pavel
        try {
          if (Calc.macro(visible, aliases).equals("NO")) setVisible(false);
        } catch (Exception e) {
        }
//end pavel

        sp = (String)prop.get("TYPE");
        if (sp!=null) setType(sp);

		sp = (String)prop.get("VALUE");
        if (sp!=null) {
			try{
				pValue=validator.toObject(sp);
				settext(sp);
			}catch(Exception e){}
		}

        sp = (String)prop.get("HALIGNMENT");
        if (sp!=null) halignment = sp;

        sp = (String)prop.get("VALIGNMENT");
        if (sp!=null) valignment = sp;

        sp = (String)prop.get("EDITMASK");
        if (sp!=null) validator.setMask(sp);

        sp = (String)prop.get("BORDER");
        if (sp!=null) setBorder(sp);
        else setBorder(border);

        sp = (String)prop.get("MULTILINE");
        if (sp!=null && sp.equals("YES")) multiLine = true;
        else multiLine = false;

        sp = (String)prop.get("WORDWRAP");
        if (sp!=null && sp.equals("YES")) wordWrap = true;
        else wordWrap = false;
    }
    /*--------end of init---------------------------*/


    /*public void setBounds(int x, int y, int width, int height){
        super.setBounds(x,y,width,height);
        field.setBounds(0,0,width - bw,height);
        button.setBounds(width - bw,1,bw,height-1);
    }*/







    void setBorder(String border) {
        this.border = border;
        if (border.equals("3DLOWERED")) {
            dw = 4;
            dh = 4;
        }else
        if (border.equals("BOX")) {
            dw=3;
            dh=3;
        }
    }

    public void setalias(String alias) {
        this.alias = alias;
    }
    public String getalias() {return alias;}


    public void settarget(String t) {
        this.target = t;
    }

    public String  gettarget() {
        return target;
    }
    public void setexp(String exp) {
        this.exp = exp;
    }

    public String getexp() {return exp;}

    public void seteditable(String edit) {
        this.editable = edit;
    }

    public String geteditable() {return editable;}

    public void setvalue(String text) {
        svalue = text;
    }
    public void settext(String text) {
        svalue = text;
    }

    public void setValue(Object o){
        FORM fp = null;
        dbi.DATASTORE ds = null;
        try{
        if ((fp=getFormParent())!=null) {
            if ((ds=fp.getDatastore())!=null)
                if (gettarget()!=null) {

                    if (needSetString) setvalue(validator.toString(o));
                    ds.setValue(0,gettarget(),o);
                    repaint();
                }
        }
        else if (getFieldParent() instanceof views.ReportForm){//для Field'а в ReportForm'e
            GlobalValuesObject go = ((views.ReportForm)getFieldParent()).getStore();
            if (go!=null && isComputed) go.setValueByName(alias, o);
            if (needSetString) setvalue(validator.toString(o));
        }
		pValue=o;
		//System.out.println("--- inside Field.setValue();alias="+alias+";value="+pValue);
        }
        catch(Exception e) {
            System.out.println("exception inside views.Field::setValue : "+e+";alias="+alias);
        }
    }

    public Object getValue() {
		//System.out.println("--- inside Field.getValue();alias="+alias+";value="+pValue);
		if (getFieldParent() instanceof views.ReportForm){//для Field'а в ReportForm'e
            try {
				dbi.DATASTORE ds = null;
				//System.out.println("--- instanceof views.ReportForm;alias="+alias);
                dbi.Group gr = ((views.ReportForm)getFieldParent()).getStore();
                ds = ((views.ReportForm)getFieldParent()).getDatastore();
                if (gr!=null) {
                    if (isComputed) return gr.getValueByName(alias);
                    else {
                        if (ds!=null) return ds.getValue(gr.begrow,gettarget());
                    }
                }
                //else {
                //    if (ds!=null) return ds.getValue(0,gettarget());
                //}
            }catch(Exception e) {}
		}
		return pValue;
		//System.out.println("---inside Field.getValue;alias="+alias);
        /*FORM fp = null;
        dbi.DATASTORE ds = null;
        if ((fp=getFormParent())!=null) {
            if ((ds=fp.getDatastore())!=null)
                if (gettarget()!=null)
                try{
                return ds.getValue(0,gettarget());
                }catch(Exception e) {
                   //System.out.println("***** DS is EMPTY *****;alias of field="+alias);
                   return null;
                }
        }else if (getFieldParent() instanceof views.ReportForm){//для Field'а в ReportForm'e
            try {
				//System.out.println("--- instanceof views.ReportForm;alias="+alias);
                dbi.Group gr = ((views.ReportForm)getFieldParent()).getStore();
                ds = ((views.ReportForm)getFieldParent()).getDatastore();
                if (gr!=null) {
                    if (isComputed) return gr.getValueByName(alias);
                    else {
                        if (ds!=null) return ds.getValue(gr.begrow,gettarget());
                    }
                }
                else {
                    if (ds!=null) return ds.getValue(0,gettarget());
                }
            }catch(Exception e) {
                System.out.println("~views.Field::getValue : "+e);
            }
        }
        return null;*/
    }

    public String getvalue() {
        return svalue;
    }
    public String gettext() {return svalue;}

    /*public void setborder(int border) {
        field.setborder(border);
    }*/

    //public int getborder() {return field.getborder();}

    public void setdep(String dep) {
        //field.setdep(dep);
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

    public String[] getdep() { return depends;//return field.getdep();

    }

    public void setType(String t) {
        t = t.toUpperCase();
        //System.out.println("string type ="+t);
        if (t.equals("NUMBER")) {
            type = java.sql.Types.NUMERIC;

        }
        if (t.equals("STRING")) {
            type = java.sql.Types.VARCHAR;

        }
        if (t.equals("DATE")) {
            type = java.sql.Types.DATE;
        }

        setType(type);
        //System.out.println("numeric type ="+type);

    }

    public void setType(int type) {
        this.type = type;
        validator.setType(Grid.getJType(type));
        if (GLOBAL.views_debug>0) {
            System.out.println("field with alias "+getalias()+" have type = "+type);
        }
    }
    public int getType() {
        return type;
    }

    public void notifyIt() {
        Container par = getParent();
        if (editField!=null) {
            remove(editField);
            editField = null;
            inFocus = false;
        }else return;

        dbi.DATASTORE ds2 = (dbi.DATASTORE)aliases.get("STORE");
        if (ds2!=null) {
            if (editExp!=null) {
                try {
                    if (calc!=null) calc.eval(aliases);
                }
                catch(Exception e) {
                    System.out.println("~views.EditField::notifyIt : " + e);
                }
                if (depends!=null) {
                    calcHandbookDep();
                    calcDep();
                }
            }
            if (par instanceof NotifyInterface)
                ((NotifyInterface)par).notifyIt();
        }
    }


    public void calcHandbookDep() {
        //System.out.println("alias="+aliases);
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.Field f = (views.Field)aliases.get(depends[i]);
            if (f!=null) {
                f.calcHandbookExp();
            }
            else System.out.println("~views.Field::calcDep() : object views.Field not found for alias "+depends[i]);
        }
    }
    public void calcDep() {
        //System.out.println("alias="+aliases);
        //System.out.println("calculating depends");
        if (depends==null) return;
        for (int i = 0; i < depends.length; i++ ){
            views.Field f = (views.Field)aliases.get(depends[i]);
            if (f!=null) {
                f.calc();
            }
            else System.out.println("~views.Field::calcDep() : object views.Field not found for alias "+depends[i]);
        }
    }
    public void calcHandbookExp() {
        try{
            if (editExp!=null) {
                if (calc!=null) calc.eval(aliases);
            }
        }catch(Exception e) {
            System.out.println("~views.Field::calcHandbookExp() : "+e);
        }
    }

    public void calc() {
        //System.out.println("calc expression "+getexp());
        try{
            if (exp!=null) {
                if (calc!=null) calc.eval(aliases);
            }
        }catch(Exception e) {
            //e.printStackTrace();
            System.out.println("~views.Field::calc() : "+e);
        }

    }

    public void calcExtra() {
        //System.out.println("calc expression "+getexp());
        try{
            if (extraexp!=null) {
                if (extracalc!=null) extracalc.eval(aliases);
            }
        }catch(Exception e) {
            //e.printStackTrace();
            System.out.println("~views.Field::calcExtra() : "+e);
        }

    }

    public void setValueByName(String str,Object o) {}
    public Object getValueByName(String str) {return null;}


    public void setedit(String edit){this.edit = edit;}
    public void setaliases(Hashtable aliases){this.aliases=aliases;}



    public void paint(Graphics g, int a) {
        //pavel
        try {
          if (Calc.macro(visible, aliases).equals("NO")) return;
        } catch (Exception e) {
        if (!isVisible()) return;
        }
        //end pavel
        int dx = getBounds().x;
        int dy = getBounds().y;
        if (! needTranslate) {dx=0;dy=0;}
        g.translate(dx*a/100,dy*a/100);
        //System.out.println("inside paint in field");
        int width = getSize().width;
        int height = getSize().height;
        /*-----------*/
        //g.setFont(scaleFont);
        g.setColor(getBackground());
        g.fillRect(0,0,width*a/100,height*a/100);
        /*-----------*/

        //g.setColor(Color.white);
        //g.fillRect(0,0,width,height);
        if (border.equals("3DLOWERED")){
            g.setColor(Color.darkGray);
            g.drawLine(0,0,width*a/100,0);
            g.drawLine(0,0,0,height*a/100);

            g.setColor(Color.white);
            g.drawLine(0,height*a/100,width*a/100,height*a/100);
            g.drawLine(width*a/100,height*a/100,width*a/100,0);

            g.setColor(Color.black);
            g.drawLine(a/100,a/100,(width-1)*a/100,a/100);
            g.drawLine(a/100,a/100,a/100,(height-1)*a/100);

            g.setColor(Color.lightGray);
            g.drawLine(a/100,(height-1)*a/100,(width-1)*a/100,(height-1)*a/100);
            g.drawLine((width-1)*a/100,(height-1)*a/100,(width-1)*a/100,a/100);
        }else
        if (border.equals("BOX")) {
            g.setColor(Color.black);
            SmartLine line = new SmartLine(g);
            line.setType(0);
            if (parent instanceof ReportForm) {
                if (((ReportForm)parent).isPrint)
                    line.isPrint=true;
                else line.isPrint=false;
            }
            line.draw(0,0,  width,a);
            line.draw(0,height,  width,a);
            line.setType(1);
            line.draw(0,0,  height+1,a);
            line.draw(width,0,  height+1,a);
        }

        g.setFont(scaleFont);

        if (svalue!=null) {
            g.setColor(getForeground());
            if (multiLine) {//нужно распарсить строки и сделать выравнивание
                String svalue1;
                if (wordWrap) {
                    svalue1 = UTIL.makeWrap(svalue," ",getBounds().width-dw-3 ,fm);
                }else svalue1 = svalue;
                StringTokenizer st = new StringTokenizer(svalue1, "\n", true);
                int cnt = st.countTokens();//кол-во стро
                String[] tok = new String[cnt];
                boolean ptisnl=false;
                int curind = 0;
                for (int i=0;i<cnt;i++) {
                    String next = st.nextToken();
                    if (!next.equals("\n")&&ptisnl) {
                        ptisnl = false;
                        tok[curind-1] = next;
                        continue;
                    }else {
                        if (next.equals("\n")) ptisnl = true;
                        tok[curind] = next;
                        curind++;
                    }
                }
                cnt = curind;
                int y1 = UTIL.getOutPoint(width,height,fm,
                    halignment,valignment,dw,dh,0,0,"A")[1];
                if (valignment.equals("TOP")){}
                if (valignment.equals("CENTER")) {
                    if (cnt%2==0) {
                       y1-=(fm.getHeight()*(cnt/2)-(fm.getHeight()/2));
                    }else {
                        y1-=(fm.getHeight()*(cnt/2));
                    }
                }
                if (valignment.equals("BOTTOM")) {
                    y1-=(fm.getHeight()*(cnt/2));
                }
                for (int i=0;i<curind;i++) {
                    String next = tok[i];
                    if (next.equals("\n")) next = "";
                    int[] xy = UTIL.getOutPoint(width,height,fm,
                        halignment,valignment,dw,dh,0,0,next);
                    g.setClip(0,0,(getSize().width-dw)*a/100,(getSize().height-dh)*a/100);
                    g.drawString(next,xy[0]*a/100,(y1+i*fm.getHeight())*a/100);
                }
            }else {
                int[] xy = UTIL.getOutPoint(width,height,fm,
                    halignment,valignment,dw,dh,0,0,svalue);
                g.setClip(0,0,(getSize().width-dw)*a/100,(getSize().height-dh)*a/100);
                g.drawString(svalue,xy[0]*a/100,xy[1]*a/100);
            }
        }

        g.translate(-dx*a/100,-dy*a/100);
    }

    public void paint(Graphics g){
        paint(g, 100);
    }

    public void processKeyEvent(KeyEvent e) {
        //System.out.println("processKeyEvent called in views.Field");
    }

	public void processMouseEvent(MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			FORM parent = getFormParent();
			if (parent != null)
				if (parent.isEditMaket()) {
					if (parent.addMarkChild(this)) {
						setBackground(Color.BLUE);
					} else {
						setBackground(bg_color);
					}
					repaint();
					break;
				}
			if (!inFocus)
				setFocus(true);
			break;
		}
	}

    public boolean setFocus(boolean b) {
        if (b) {
        	FORM parent = getFormParent();
        	if (parent!=null) if (parent.isEditMaket()) return false;
            if (inFocus) return true;
            boolean flag = true;
            Component[] siblings = getParent().getComponents();
            if (siblings!=null)
                for (int i=0;i<siblings.length;i++) {
                    if (siblings[i] instanceof Field && siblings[i]!=this) {
                        flag&=((Field)siblings[i]).setFocus(false);
                    }
                }
            if (flag) {
                inFocus = true;
                createEditField();
                return true;
            }else return false;
        }
        else {
            if (!inFocus) return true;
            if (editField!=null) {
                if (fromEditField(editField.getvalue())) {
                    remove(editField);
                    editField = null;
                    inFocus = false;
                    return true;
                }
                else return false;
            }else return true;
        }
        //return false;
    }

    public void createEditField() {
        editField = new EditField(multiLine);
        editField.getBaseField().setFont(getFont());
        editField.setFieldParent(this);
        int w = getSize().width;
        int h = getSize().height;

        if (Integer.parseInt(GLOBAL.pr(GLOBAL.NEED_EXTRAH,"0"))!=0)
            h*=extrah/100+1;
        editField.setSize(w,h);
	//
	//editField.setLocation(getLocation().x,getLocation().y);
	//
        editField.seteditable(editable);
        //System.out.println("editable = "+editable);
        editField.setaliases(aliases);
        editField.settext(svalue);
        editField.setType(getType());
        editField.validator = validator;
        editField.edit = this.edit;
        //editField.editExp = this.editExp;
        //System.out.println("edit = "+edit);
        add(editField,0);
        editField.getBaseField().requestFocus();
    }

    public boolean fromEditField(String str) {
        try{
            System.out.println("-------fromEditField called-----");
            Object o = validator.toObject(str);
            if (o==null) return true;
            setValue(o);
            calcDep();
        }catch(Exception e) {
            System.out.println("exeption inside views.Field::fromEditField : "+e);
            return false;
        }
        return true;
    }


}
