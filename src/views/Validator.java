package views;
import java.util.*;
import java.text.*;

public class Validator{
    int type=-1;//0-Double;1-String;2-java.util.Date
    String mask = null;
    SimpleDateFormat df = null;

    private String sep=" ";
    private String dot = ".";
    private int  cnt=0;

    public Validator(){}

    public void setType(int type){//сначала вызываем setType, затем setMask
        this.type = type;
        //this.mask = null;
        //System.out.println("type in validator ="+type);
        if (type==2) {
            df = new SimpleDateFormat("dd.MM.yyyy",Locale.UK);
        }
    }
    public void setMask(String mask) {
        this.mask = mask;
        setNumberFormat(mask);
    }

    public Object toObject(String str) throws Exception{
        //System.out.println("type = "+type);
        switch(type) {
            case 0:return undoFormat(str);
            case 1:return str;
            case 2:{
                java.util.Date date = df.parse(str,new ParsePosition(0));
                if (date==null) throw new Exception("Bad data format!");
                else return date;
            }
            default: return null;
        }
    }

    public String toString(Object o) throws Exception{
        if (o==null) return "";
        switch(type) {
            case 0:return formatNumber((Double)o);
            case 1:return (String)o;
            case 2:{
                 String str = df.format((java.util.Date)o,new StringBuffer(),new FieldPosition(0)).toString();
                 if (str==null || str.equals(""))
                    throw new Exception("Bad data !");
                 else return str;
            }
            default: return "";
        }
    }

    void setNumberFormat(String s){
        if (s==null) return;
        s = s.trim();
        int i1 = s.indexOf('#',0);
        if (i1>=0) {
           int i2 = s.indexOf('#',i1+1);
           if (i2>=0) {
            sep=s.substring(i1+1,i2);
            i1=i2;
           }
        }
        if (s.length() > i1+1){
            cnt = 0;
            String s2 = s.substring(i1+1, s.length());
            int pos1 = s2.indexOf('0',0);
            if (pos1>=0) {
                int pos2 = s2.indexOf('0',pos1+1);
                if (pos2>=0) {
                    dot = s2.substring(pos1+1,pos2);
                    for (int i=pos2+1;i<s2.length() && s2.charAt(i)=='0';i++) cnt++;
                    cnt++;
                }else dot = s2.substring(pos1+1,s2.length());
            }
        }
    }
    Double undoFormat(String src) throws Exception{
        src = src.trim();
        //if (src.length()==0) throw new NumberFormatException("Empty value!");
        if (src==null||src.length()==0) return null;
        //alex 16-03-2001 for ctrl_v from excel
        src = src.replace(',','.');
        //alex
        int endint = src.indexOf(dot);//конец целой част
        if (endint==-1) endint = src.length();
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<endint;i++) {
            if (sep.indexOf(src.charAt(i))==-1) {
                sb.append(src.charAt(i));
            }
        }//"засунули" в sb целую част
        sb.append('.');//десятичная точка
        for (int i=endint+dot.length();i<Math.min(cnt+endint+dot.length(),src.length());i++) {
            if (sep.indexOf(src.charAt(i))==-1) {
                sb.append(src.charAt(i));
            }
        }//"засунули" в sb дробную част
        //Пробуем вернуть Double
        try {
            //System.out.println("StringBuffer="+sb.toString());
            return new Double(sb.toString());
        }catch(Exception e) {throw e;}
     }

    String formatNumber(Double obj){
        Double dd;
        double d = 0;
        String src, lt, rt;
        String sign = "";
        long lg;
        int i;
        dd  = obj;
        d   = dd.doubleValue();
        if (d<0) {
            d = -d;
            sign = "-";
        }
        src = dd.toString();

        if (cnt == 0) {
            lg = Math.round(d);
            rt = "";
            lt = (new Long(lg)).toString();
        } else{
            lg = new Double( Math.floor(d) ).longValue();
            //lt = (new Long(lg)).toString();
            d -= lg;
            for(i = 1; i <= cnt; i++) d *=10;
            if (d==0) {
                char[] chars = new char[cnt];
                for (int j=0;j<chars.length;j++) chars[j] = '0';
                rt = new String(chars);
            }else {
                rt = new Long(Math.round(d)).toString();
                String addstr = "";
                if (cnt-rt.length() > 0) {
                    char[] tmp = new char[cnt-rt.length()];
                    for (i=0;i<tmp.length;i++) tmp[i] = '0';
                    addstr=new String(tmp);
                    rt = addstr+rt;
                }else
                if (cnt-rt.length()<0){
                //значит, после округления дробной части произошел
                //перенос в старший разряд
                    rt = rt.substring(1,rt.length());
                    lg++;
                }
            }
            lt = (new Long(lg)).toString();
        }
        StringBuffer stb = new StringBuffer(lt);
        int ln = stb.length(), inc = 0;
        for(i = 1; i < ln + inc ; i++)
            if (i%3 == 0) stb.insert(ln + inc - i , sep);
        if (rt.equals("")) return sign+stb.toString();else return (sign+stb.toString()+dot+rt);
    }

    private String formatNumberOld(Double obj){
        Double dd;
        double d = 0;
        String src, lt, rt;
        long lg;
        int i;
        /*if (obj instanceof String) {
            src = (String)obj;
            try{
                dd = new Double(src);
            } catch(NumberFormatException e){
                System.out.println( "views.Validator::formatNumber() : Wrong argument.");
                return null;
            }
            d = dd.doubleValue();
        }
        if (obj instanceof Double){*/
        dd  = obj;
        d   = dd.doubleValue();
        src = dd.toString();
        //}
        if (cnt == 0) {
            lg = Math.round(d);
            rt = "";
            lt = (new Long(lg)).toString();
        } else{
            lg = new Double( Math.floor(d) ).longValue();
            lt = (new Long(lg)).toString();
            d -= lg;
            for(i = 1; i <= cnt; i++) d *=10;
            if (d==0) {
                char[] chars = new char[cnt];
                for (int j=0;j<chars.length;j++) chars[j] = '0';
                rt = new String(chars);
            }else {
                rt = (new Long( Math.round(d) )).toString();
                String addstr = "";
                if (cnt-rt.length() > 0) {
                    char[] tmp = new char[cnt-rt.length()];
                    for (i=0;i<tmp.length;i++) tmp[i] = '0';
                    addstr=new String(tmp);
                }
                rt = addstr+rt;

            }
        }
        StringBuffer stb = new StringBuffer(lt);
        int ln = stb.length(), inc = 0;
        for(i = 1; i < ln + inc ; i++)
            if (i%3 == 0) stb.insert(ln + inc - i , sep);
        if (rt.equals("")) return stb.toString();else return (stb.toString()+dot+rt);
    }

}
