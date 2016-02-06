
package views;
import java.awt.*;
import rml.*;
import java.util.*;
import loader.GLOBAL;
import dbi.DATASTORE;
import calc.GlobalValuesObject;
public class ReportForm extends Container {
    String alias = null;
    dbi.DATASTORE ds = null;
    int beginRow = 0;
    dbi.Group currentGroup = null;
    //Field[] fields = null;
    Hashtable fields = new Hashtable();
    Label[] labels = null;
    Line[] lines = null;
    public boolean isPrint = false;
    String type = null; 
    
    public void paint(Graphics g, int a) {
        //System.out.println("paint in form called!");
        //System.out.println("clip="+g.getClip());
        g.setColor(getBackground());
        Rectangle r = getBounds();
        g.fillRect(r.x*a/100,r.y*a/100,r.width*a/100, r.height*a/100);
        Field[] fs = getFields();
        if (fs!=null) {
            for (int i=0;i<fs.length;i++){
                fs[i].setScaleFont(a);
                fs[i].paint(g,a);
            }
        }
        if (labels!=null) {
            for (int i=0;i<labels.length;i++){
                labels[i].setScaleFont(a);
                labels[i].paint(g,a);
            }
        }
        if (lines!=null) {
            for (int i=0;i<lines.length;i++){
                lines[i].paint(g,a);
            }
        }        
    }
    
    public String getType(){return type;}
    
    public void init(Proper prop, Hashtable aliases) {        
        Integer ip;
        String sp;
        int left=0;
        int top=0;
        int width=0;
        int height=0;
        
        setLayout(null);        
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) left = ip.intValue();
        
        ip = ((Integer)prop.get("TOP"));
        if (ip!=null) top = ip.intValue();

        ip = ((Integer)prop.get("WIDTH"));
        if (ip!=null) width = ip.intValue();
        
        ip = ((Integer)prop.get("HEIGHT"));
        if (ip!=null) height = ip.intValue();
        
        setBounds(left,top,width,height);
        
        sp = ((String)prop.get("BG_COLOR"));
        if (sp!=null) {
            setBackground(UTIL.getColor(sp));        
        }
        else {
            setBackground(Color.white);
        }
        
        sp = (String)prop.get("ALIAS");
        if (sp!=null) alias = sp;
        //else {System.out.println("Property 'alias' not found !");}
        
        sp = (String)prop.get("TYPE");//это св-во используется для колонтитулов
        if (sp!=null) type = sp;
    }
    
    public void addChildren(Object[] objs) {
        Vector vlines = new Vector();
        for (int i = 0; i < objs.length; i++) {
            Object o = objs[i];
            if (o instanceof views.Field) {
                if (((Field)o).gettarget() == null) {
                    int type = ((Field)o).getType();
                    if (type==Integer.MIN_VALUE) {
                        System.out.println("views.FORM.addChildren says : type for computed field not defined!");
                        continue;
                    }                    
                }
                add((Field)o);
                ((Field)o).setFieldParent(this);
                ((Field)o).needTranslate = true;
                String alias = ((Field)o).getalias();
                if (alias==null) System.out.println("alias for field "+(Field)o+" not defined.This field will not be added into ReportForm!");
                else fields.put(alias, (Field)o);
                continue;
            }
            if (o instanceof views.Label) {
                add((views.Label)o);
                ((Label)o).parent = this;
                ((Label)o).needTranslate = true;
                continue;
            }
            
            if (o instanceof views.Line) {
                ((views.Line)o).parent = this;
                vlines.addElement(o);
            }
        }
        //fields = getFields();
        labels = getLabels();
        if (vlines.size()>0) {
            lines = new Line[vlines.size()];
            vlines.copyInto(lines);
        }
    }
    
    public void setDatastore(DATASTORE ds) {
        this.ds = ds;
    }
    
    public DATASTORE getDatastore(){return this.ds;}
    
    public dbi.Group getStore(){
        return currentGroup;
    }
    
    public Field[] getFields() {       
        if (fields==null) return null;               
        Enumeration e = fields.elements();
        if (e==null) return null;
        Vector v = new Vector(); 
        while(e.hasMoreElements()) {
            v.addElement((Field)e.nextElement());
        }
        if (v.size()==0) return null;
        Field[] ret = new Field[v.size()];
        v.copyInto(ret);
        return ret;
    }
    //public void setFields(Field[] fields) {
    //    this.fields = fields;
    //}
    
    public Field getField(String alias) {
        if (fields!=null) return (Field)fields.get(alias);
        else return null;
    }
    
    public Label[] getLabels() {
        Vector temp = new Vector();
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++ ) {
            if (comps[i] instanceof views.Label) temp.addElement(comps[i]);
        }
        Label[] ret = new Label[temp.size()];
        temp.copyInto(ret);        
        return ret;        
    }
    
    public void fillFields() {
        Field[] fs = getFields();
        if (fs==null) return; 
        for (int i = 0; i < fs.length;i++) {
            if (!fs[i].isComputed) {
                Object val = ds.getValue(currentGroup.begrow,fs[i].gettarget());
                //System.out.println("---value="+val);
                fs[i].setType(ds.getType(fs[i].gettarget()));
                fs[i].needSetString = true;
                fs[i].setValue(val);
            }else {
                if (currentGroup==null) return;
                Object o = currentGroup.getValueByName(fs[i].getalias());
                fs[i].needSetString = true;
                fs[i].setValue(o);
            }
        }    
    }
	public void fillFields2() {//for colontituls
        Field[] fs = getFields();
        if (fs==null) return;
		for (int i=0;i<fs.length;i++) if (fs[i]!=null) fs[i].needSetString=true; 
        for (int i = 0; i < fs.length;i++) {
            if (!fs[i].isComputed) {
                Object val = ds.getValue(/*currentGroup.begrow*/0,fs[i].gettarget());
                //System.out.println("---value="+val);
                fs[i].setType(ds.getType(fs[i].gettarget()));
                fs[i].needSetString = true;
                fs[i].setValue(val);
				fs[i].calcDep();
            }else {
				fs[i].calc();
				fs[i].calcDep();
			}
        }
		for (int i=0;i<fs.length;i++) if (fs[i]!=null) fs[i].needSetString=false; 
    
    }

}
