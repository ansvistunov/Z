
package views;
import java.awt.*;
import java.awt.event.*;
import rml.*;
import calc.*;
import calc.objects.*;
import java.util.*;

public class Menu implements GlobalValuesObject,class_method,class_type{
    java.awt.Menu menu = new java.awt.Menu();
    String label = "Submenu1";
    Color bg_color = Color.lightGray;
    
    public Menu(){}
    
    public void init(Proper prop) {
        String sp;
        sp = (String)prop.get("LABEL");
        if (sp!=null) label = sp;
        menu.setLabel(label);
        
        sp = (String)prop.get("BG_COLOR");
        if (sp!=null) bg_color = UTIL.getColor(sp);        
        
    }
    
    public Color getBackground(){return bg_color;}

    public void addChildren(Object[] objs) {
        if (objs==null) return;        
        for (int i=0;i<objs.length;i++) {
            if (objs[i] instanceof views.Item) {
                MenuItem mi = (views.Item)objs[i];
                //mi.setActionCommand(((views.Item)objs[i]).getAction());               
                mi.addActionListener(null);//нужно!
                menu.add(mi);
                continue;
            }
            if (objs[i] instanceof views.Menu) {
                java.awt.Menu m =((views.Menu)objs[i]).getMenu();
                m.addActionListener(null);//нужно!
                menu.add(m);
            }
        }
    }
    public java.awt.Menu getMenu(){
        return menu;
    }
    public java.awt.MenuItem getItem(int index){
        java.awt.MenuItem mi = menu.getItem(index);
        return mi;
    }
    public int getItemCount(){
        return menu.getItemCount();
    }
    
    public void setMenu(java.awt.Menu menu) {
        this.menu = menu;
    }
    
    public void append(MenuItem mi) {
        menu.add(mi);
    }
    public void set(MenuItem it,int i){
        menu.insert(it,i);
    }


    public String getLabel(){return label;}

           //Методы интерфейса GlobalValuesObject
    public void setValue(Object o){}
    public void setValueByName(String name, Object o) {
      try{
      int i = Integer.parseInt(name);
      if (i>menu.getItemCount()-1){
         append((Item)o);
      }else set((Item)o,i);
      }catch(NumberFormatException e) { label = o.toString(); }
    }
    public Object getValue(){return this;}
    public Object getValueByName(String name) {
      try{
        return getItem(Integer.parseInt(name));
      }catch(NumberFormatException e) { return label; }
    }
    public String type(){
      return "SVR_MENU";
    }
    public Object method(String method,Object arg) throws Exception{
      if (method.toUpperCase().equals("SIZE")){
         return new Double(menu.getItemCount());
      }else if (method.toUpperCase().equals("REMOVE")){
         try{
            int i = ((Double)arg).intValue();
            menu.remove(i);
            
         }catch(Exception e){
            throw new RTException("CastException","method REMOVE must have one Numeric parameter" + e.getMessage());
         }
         return new Double(0);
      }if (method.toUpperCase().equals("GET")){
         return getValueByName(""+((Double)arg).intValue());
      }if (method.toUpperCase().equals("PUT")){
         Double d = (Double)((Vector)arg).elementAt(0);
         String s = ""+d.intValue();
         setValueByName( s,((Vector)arg).elementAt(1));
         return new Double(0);
      }
      else throw new RTException("HasNotMethod","method "+method+
                       " not defined in class views.Menu!");
    }
   

}
