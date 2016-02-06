
/*
 * File: DATE.java
 *
 * Created: 18.10.2000
 *
 * Copyright (c) by kot
 */

package calc.objects;
import calc.*;
import java.util.*;
import java.util.Vector;
import java.lang.*;
import java.text.*;

public class DATE implements class_constructor,class_method,class_type,GlobalValuesObject{

   Date dat;
   public DATE(){}
   public Object constructor(Object arg) throws Exception{
      if ( arg instanceof Vector ){
           Vector v = (Vector)arg; 
         Number zn0 = ((Number)v.elementAt(0));
         Number zn1 = ((Number)v.elementAt(1));
         Number zn2 = ((Number)v.elementAt(2));   
   this.dat = new Date(((Number)zn0).intValue(),((Number)zn1).intValue(),((Number)zn2).intValue());
   return this;
   }else if(arg instanceof String){
     SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");
     this.dat = form.parse(arg.toString());
     return this;
   }else if(arg instanceof Long){
   this.dat = new Date(Date.parse(arg.toString()));
   return this;
   }else if(arg instanceof Date){
   this.dat = (Date)arg;
   return this;
   }  
   throw new RTException("CastException",
                             "format for DATA must to be Objects or String or Long");     
   }

   public Object method(String method,Object arg) throws Exception{
      if ( method.equals("TONUMBER") ){
      return new Double(dat.getTime());
       }else if ( method.equals("TOSTRING") ){
      return dat.toString();
       }else if ( method.equals("VALUE") ){
      return dat;
       }else if ( method.equals("TOPLSTRING") ){
         String s;
         s = "'" + new Integer(dat.getDate()).toString();
         s = s + '.' + new Integer(dat.getMonth() + 1).toString();
         s = s + '.' + new Integer(dat.getYear() + 1900).toString() + "'";
      return s;
      }else if ( method.equals("MM") ){
      return new Double(dat.getMonth()+1);
        }else if ( method.equals("YYYY") ){
      return new Double(dat.getYear()+1900);
        }else if ( method.equals("DD") ){
      return new Double(dat.getDate());
        }else if ( method.equals("EQUALS") ){
            Date d = null;
            if (arg instanceof Date){d = (Date)arg;}
            if (arg instanceof DATE){d = ((DATE)arg).dat;}
            boolean b = dat.equals(d);
            if (b) return new Double(1);
            else return new Double(0);
        }else if ( method.equals("BEFORE") ){
            Date d = null;
            if (arg instanceof Date){d = (Date)arg;}
            if (arg instanceof DATE){d = ((DATE)arg).dat;}
            boolean b = dat.before(d);
            if (b) return new Double(1);
            else return new Double(0);
        }else if ( method.equals("AFTER") ){
            Date d = null;
            if (arg instanceof Date){d = (Date)arg;}
            if (arg instanceof DATE){d = ((DATE)arg).dat;}
            boolean b = dat.after(d);
            if (b) return new Double(1);
            else return new Double(0);
        }else if ( method.equals("MOVEBYSECOND") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.SECOND, d.intValue() );
            dat = c.getTime();
            return this;
        }else if ( method.equals("MOVEBYMINUTE") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.MINUTE, d.intValue() );
            dat = c.getTime();
            return this;
        }else if ( method.equals("MOVEBYHOUR") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.HOUR, d.intValue() );
            dat = c.getTime();
            return this;
        }else if ( method.equals("MOVEBYDAY") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.DATE, d.intValue() );
            dat = c.getTime();
            return this;
        }else if ( method.equals("MOVEBYMONTH") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.MONTH, d.intValue() );
            dat = c.getTime();
            return this;
        }else if ( method.equals("MOVEBYYEAR") ){
            Calendar c = Calendar.getInstance();
            Double d = null;
            if (arg instanceof Double)
             {d = (Double)arg;}
            if (arg instanceof String)
             {try {
                d = Double.valueOf((String)arg);
              }catch (NumberFormatException e) {
                throw new RTException("BadFormatException",
                             "wrong string for type Double");
              }
             }
            c.setTime(dat);
            c.add(Calendar.YEAR, d.intValue() );
            dat = c.getTime();
            return this;
        }
        else throw new RTException("HasMethodException",
                           "object DATA has not method "+method);
        //return null;
  }
   public String type() throws Exception{
      return "DATE";
   }

   public void setValue(Object o){
      if (o instanceof Date){
         this.dat = (Date)o;
      }        
    }
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return dat;}
    public Object getValueByName(String name) {return null;}

}  





