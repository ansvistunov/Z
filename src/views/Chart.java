package views;
import dbi.*;
import rml.Proper;
import java.util.*;
import java.awt.*;
public class Chart extends Panel implements 
Retrieveable{
    DATASTORE ds = null;
    Hashtable aliases;
    int style = 0;
    String alias;
    int lx=50;//отступы от края компонента по x,y
    int ly=60;
    int llx=5;//превышение размеров легенды  необходимых по x,y
    int lly=5;
    int ldx=50;//отступ края легенды от края компонента
    int ldy=50;
    int dh = 50;//высота круговой диаграммы
    double used_space = 95.0/100;
    int xcolumn = 0;//column, который надо рисовать по x
    int[] ycolumns = {1};//column, который надо рисовать по y
    int xlcolumn = 0;//column со значениями легенды х
    Color grColor = Color.black;
    Color[] dataColors = {Color.red,Color.blue,Color.cyan,Color.darkGray,
    Color.gray,Color.green,Color.lightGray,Color.magenta,Color.orange,
    Color.pink,Color.yellow };
    String[] names = {};
    public Chart(){}
    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        int LEFT=0;
        int TOP=0;
        int width=0;
        int height=0;
        this.aliases = aliases;
        
        sp = (String)prop.get("ALIAS");
        alias = sp;
        
        ip = (Integer)prop.get("STYLE");
        if (ip!=null) style = ip.intValue();
        
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) LEFT = ip.intValue();

        ip = ((Integer)prop.get("TOP"));
        if (ip!=null) TOP = ip.intValue();

        ip = ((Integer)prop.get("WIDTH"));
        if (ip!=null) width = ip.intValue();

        ip = ((Integer)prop.get("HEIGHT"));
        if (ip!=null) height = ip.intValue();
        setBounds(LEFT,TOP,width,height);
        //System.out.println(" "+LEFT+" "+TOP +" "+width+" "+height);
        ip = ((Integer)prop.get("FREE_SPACE"));
        if (ip!=null) used_space = 1-(ip.intValue())/100.0;
        
        ip = ((Integer)prop.get("LEFT_OFFSET"));
        if (ip!=null) lx = ip.intValue();
        
        ip = ((Integer)prop.get("BOTTOM_OFFSET"));
        if (ip!=null) ly = ip.intValue();
        
        ip = ((Integer)prop.get("XCOLUMN"));
        if (ip!=null) xlcolumn = ip.intValue();
        
        ip = ((Integer)prop.get("LEGENDA_X_OFFSET"));
        if (ip!=null) ldx = ip.intValue();
        
        ip = ((Integer)prop.get("LEGENDA_Y_OFFSET"));
        if (ip!=null) ldy = ip.intValue();
        
        ip = ((Integer)prop.get("LEGENDA_X_ASCEND"));
        if (ip!=null) llx = ip.intValue();
        
        ip = ((Integer)prop.get("LEGENDA_Y_ASCEND"));
        if (ip!=null) lly = ip.intValue();
        
        sp = (String)prop.get("DATACOLUMNS");
        try{System.out.println("sp="+sp);
            StringTokenizer st = new StringTokenizer(sp,",");
            int ct = st.countTokens();
            //System.out.println("ct="+ct);
            ycolumns = new int[ct];
            for (int i=0;i<ct;i++){ 
                String s = st.nextToken();
                //System.out.println("s="+s+" ycolumns="+ycolumns);
                ycolumns[i] =  Integer.parseInt(s);
                //System.out.println("s="+s+" ycolumns="+ycolumns);
                
            }
        }catch(Exception e){
            System.out.println("not setting datacolumns!!!");
            System.out.println(e);
        }
        sp = (String)prop.get("LEGENDA");
        try{System.out.println("sp="+sp);
            StringTokenizer st = new StringTokenizer(sp,",");
            int ct = st.countTokens();
            //System.out.println("ct="+ct);
            names = new String[ct];
            for (int i=0;i<ct;i++){ 
                String s = st.nextToken();
                //System.out.println("s="+s+" ycolumns="+ycolumns);
                names[i] =  s;
                //System.out.println("s="+s+" ycolumns="+ycolumns);
                
            }
        }catch(Exception e){
            System.out.println("not setting dadacolumns!!!");
            System.out.println(e);
        }
    }
    
    public double maxy(){
        double m = -100000;
        for(int i=0;i<ycolumns.length;i++){
            double d = max(ycolumns[i]);
            if (m<d) m = d; 
        }  
        return m;
    }
    public double miny(){
        double m = 100000;
        for(int i=0;i<ycolumns.length;i++){
            double d = max(ycolumns[i]);
            if (m>d) m = d; 
        }  
        return m;
    }
    public double maxy_all(){
        double m = -100000;
        try{
            for(int i=0;i<ds.getCountRows();i++){
                double d = 0;
                for (int j=0;j<ycolumns.length;j++){
                    d+=((Double)ds.getValue(i,ycolumns[j])).doubleValue();
                }
                if (m<d) m = d; 
            }
        }catch(Exception e){System.out.println("max_all:"+e);}
        return m;
    }
    public void addDs(DATASTORE ds){this.ds = ds;}
    public void paint_0(Graphics g){//bars
        drawGrid(g,0);
        System.out.println("paint_0 called used_spce="+used_space);
        //double min_x = min(xcolumn);
        //double max_x = max(xcolumn);FontMetrics
        //double min_y = min(ycolumn);
        double max_y = maxy_all();
        Dimension dim = getSize();
        int count_x = ds.getCountRows();
        System.out.println("count_x="+count_x);
        double mashtab_y = (dim.height-2*ly)/max_y;
        double mashtab_x = (dim.width-2*lx)/count_x;
        
        System.out.println(mashtab_x+" "+mashtab_y);
        for(int i=0;i<count_x;i++){
            int sy = 0;
            for(int j=0;j<ycolumns.length;j++){
                double y = ((Double)ds.getValue(i,ycolumns[j])).doubleValue();
                //System.out.println("y="+y);
                //System.out.println("exp="+y*mashtab_y);
                g.setColor(grColor);
                sy += (int)Math.round(y*mashtab_y);
                double lx2 = mashtab_x*(1-used_space);
                //g.drawRect((int)Math.round(lx+(mashtab_x*i)),(dim.height)-(int)Math.round(y*mashtab_y+ly),(int)Math.round(mashtab_x),(int)Math.round(y*mashtab_y));  
                g.drawRect((int)Math.round(lx+(mashtab_x*i)+lx2/2),(dim.height)-(sy+ly),(int)Math.round(mashtab_x*used_space),(int)Math.round(y*mashtab_y));  
                if(i==0) System.out.println((int)Math.round(lx+(mashtab_x*i))+" "+((dim.height)-(sy+ly))+" "+(int)Math.round(mashtab_x)+" "+sy);
                g.setColor(dataColors[j%dataColors.length]);
                g.fillRect((int)Math.round(lx+(mashtab_x*i)+lx2/2)+1,(dim.height)-(sy+ly)+1,(int)Math.round(mashtab_x*used_space)-1,(int)Math.round(y*mashtab_y)-1);  
                int h = g.getFontMetrics().getHeight();
                String value = String.valueOf(y);
                char[] ar = new char[value.length()];
                value.getChars(0,value.length()-1,ar,0);
                double l = g.getFontMetrics().charsWidth(ar,0,value.length()); 
                g.setColor(grColor);
                if (h<(int)Math.round(y*mashtab_y)) g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+(mashtab_x-l)/2),(dim.height)-(sy+ly)+h);
                if (j==ycolumns.length-1){
                    double sum = 0;
                    for (int k=0;k<ycolumns.length;k++)    
                        sum+=((Double)ds.getValue(i,ycolumns[k])).doubleValue();
                    value = "("+String.valueOf(sum)+")";
                    ar = new char[value.length()];
                    value.getChars(0,value.length()-1,ar,0);
                    l = g.getFontMetrics().charsWidth(ar,0,value.length()); 
                    System.out.println("paint big RESULT!!");
                    h = g.getFontMetrics().getHeight();
                    g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+(mashtab_x-l)/2),(dim.height)-(sy+ly));
                
                }
                
                //charWidth
                
                //g.fillRect((int)Math.round(lx+(mashtab_x*i))+1,(dim.height)-(int)Math.round(y*mashtab_y+ly)+1,(int)Math.round(mashtab_x)-1,(int)Math.round(y*mashtab_y)-1);  
            }
            
            //System.out.println("drawing:"+(lx+(mashtab_x*i))+" "+(int)Math.round((y/mashtab_y)+ly)+" "+mashtab_x+" "+ly);
        }
    }
    public void paint_1(Graphics g){
        drawGrid(g,1);
        double min_y = miny();
        double max_y = maxy();
        Dimension dim = getSize();
        int count_x = ds.getCountRows();
        System.out.println("count_x="+count_x);
        double mashtab_y = (dim.height-2*ly)/max_y;
        double mashtab_x = (dim.width-2*lx)/count_x;
        
        System.out.println(mashtab_x+" "+mashtab_y);
        for(int i=0;i<count_x-1;i++){
            //g.setColor(Color.gray);
            //g.drawLine((int)Math.round(lx+(mashtab_x*i)),ly,(int)Math.round(lx+(mashtab_x*i)),dim.height-ly);
            for (int j=0;j<ycolumns.length;j++){
                double y = ((Double)ds.getValue(i,ycolumns[j])).doubleValue();
                double y1 = ((Double)ds.getValue(i+1,ycolumns[j])).doubleValue();
                System.out.println("y="+y);
                System.out.println("exp="+y*mashtab_y);
                g.setColor(dataColors[j%dataColors.length]);
                g.drawLine((int)Math.round(lx+(mashtab_x*i)),(dim.height)-(int)Math.round(y*mashtab_y+ly),(int)Math.round(lx+(mashtab_x*(i+1))),(dim.height)-(int)Math.round(y1*mashtab_y+ly));
                g.drawString(String.valueOf(y),(int)Math.round(lx+(mashtab_x*i)),(dim.height)-(int)Math.round(y*mashtab_y+ly));
                if(i==count_x-2) g.drawString(String.valueOf(y1),(int)Math.round(lx+(mashtab_x*(i+1))),(dim.height)-(int)Math.round(y1*mashtab_y+ly));
                
                //g.setColor(dataColor);
                //g.fillRect((int)Math.round(lx+(mashtab_x*i))+1,(dim.height)-(int)Math.round(y*mashtab_y+ly)+1,(int)Math.round(mashtab_x)-1,(int)Math.round(y*mashtab_y)-1);  
            
                //System.out.println("drawing:"+(lx+(mashtab_x*i))+" "+(int)Math.round((y/mashtab_y)+ly)+" "+mashtab_x+" "+ly);
            }
        }
    }
    public void paint_2(Graphics g){//bars
        drawGrid(g,0);
        System.out.println("paint_3 called used_spce="+used_space);
        //double min_x = min(xcolumn);
        //double max_x = max(xcolumn);FontMetrics
        //double min_y = min(ycolumn);
        double max_y = maxy();
        Dimension dim = getSize();
        int count_x = ds.getCountRows();
        System.out.println("count_x="+count_x);
        double mashtab_y = (dim.height-2*ly)/max_y;
        double mashtab_x = (dim.width-2*lx)/count_x;
        
        System.out.println(mashtab_x+" "+mashtab_y);
        for(int i=0;i<count_x;i++){
            //int sy = 0;
            double lx2 = mashtab_x*(1-used_space);
            double adder = mashtab_x*used_space/ycolumns.length;
            System.out.println("adder="+adder);
            for(int j=0;j<ycolumns.length;j++){
                double y = ((Double)ds.getValue(i,ycolumns[j])).doubleValue();
                //System.out.println("y="+y);
                //System.out.println("exp="+y*mashtab_y);
                g.setColor(grColor);
                //sy += (int)Math.round(y*mashtab_y);
                
                //g.drawRect((int)Math.round(lx+(mashtab_x*i)),(dim.height)-(int)Math.round(y*mashtab_y+ly),(int)Math.round(mashtab_x),(int)Math.round(y*mashtab_y));  
                g.drawRect((int)Math.round(lx+(mashtab_x*i)+adder*j),(dim.height)-(int)Math.round(y*mashtab_y+ly),(int)Math.round(adder),(int)Math.round(y*mashtab_y));  
                //g.drawLine((int)Math.round(lx+(mashtab_x*i)),(dim.height)-(int)Math.round(y*mashtab_y+ly),(int)Math.round(lx+(mashtab_x*(i+1))),(dim.height)-(int)Math.round(y1*mashtab_y+ly));
                
                //if(i==0) System.out.println((int)Math.round(lx+(mashtab_x*i))+" "+((dim.height)-(sy+ly))+" "+(int)Math.round(mashtab_x)+" "+sy);
                g.setColor(dataColors[j%dataColors.length]);
                g.fillRect((int)Math.round(lx+(mashtab_x*i)+adder*j)+1,(dim.height)-(int)Math.round(y*mashtab_y+ly)+1,(int)Math.round(adder)-1,(int)Math.round(y*mashtab_y)-1);  
                
                //int h = g.getFontMetrics().getHeight();
                String value = String.valueOf(y);
                char[] ar = new char[value.length()];
                value.getChars(0,value.length()-1,ar,0);
                double l = g.getFontMetrics().charsWidth(ar,0,value.length()); 
                g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+adder*j+((adder-l)/2)),(dim.height)-(int)Math.round(y*mashtab_y+ly));
                //g.setColor(grColor);
                //if (h<(int)Math.round(y*mashtab_y)) g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+(mashtab_x-l)/2),(dim.height)-(sy+ly)+h);
                //if (j==ycolumns.length-1){
                //    double sum = 0;
                //    for (int k=0;k<ycolumns.length;k++)    
                //        sum+=((Double)ds.getValue(i,ycolumns[k])).doubleValue();
                //    value = "("+String.valueOf(sum)+")";
                //    ar = new char[value.length()];
                //    value.getChars(0,value.length()-1,ar,0);
                //    l = g.getFontMetrics().charsWidth(ar,0,value.length()); 
                //    System.out.println("paint big RESULT!!");
                //    h = g.getFontMetrics().getHeight();
                //    g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+(mashtab_x-l)/2),(dim.height)-(sy+ly));
                
                //}
                
                //charWidth
                
                //g.fillRect((int)Math.round(lx+(mashtab_x*i))+1,(dim.height)-(int)Math.round(y*mashtab_y+ly)+1,(int)Math.round(mashtab_x)-1,(int)Math.round(y*mashtab_y)-1);  
            }
            
            //System.out.println("drawing:"+(lx+(mashtab_x*i))+" "+(int)Math.round((y/mashtab_y)+ly)+" "+mashtab_x+" "+ly);
        }
    }
    public double summ(int column){
        double summ = 0;
        for(int i=0;i<ds.getCountRows();i++) 
        summ+=((Double)ds.getValue(i,column)).doubleValue();
        return summ;
    }
    public void paint_3(Graphics g){//bars
        drawGrid(g,-1);
        Dimension dim = getSize();
        double xoffset = (dim.width-2*lx-(dim.width-2*lx)*used_space)/2;
        double yoffset = (dim.height-2*ly-(dim.height-2*ly)*used_space)/2;
        g.drawOval((int)Math.round(lx+xoffset),(int)Math.round(ly+yoffset),(int)Math.round((dim.width-2*lx)*used_space),(int)Math.round((dim.height-2*ly)*used_space));
        g.drawArc((int)Math.round(lx+xoffset),(int)Math.round(ly+yoffset)+dh,(int)Math.round((dim.width-2*lx)*used_space),(int)Math.round((dim.height-2*ly)*used_space),0,-180);
        g.drawLine((int)Math.round(lx+xoffset),(int)Math.round(ly+yoffset)+(int)Math.round((dim.height-2*ly)*used_space/2),(int)Math.round(lx+xoffset),(int)Math.round(ly+yoffset)+dh+(int)Math.round((dim.height-2*ly)*used_space/2));
        g.drawLine((int)Math.round(lx+xoffset)+(int)Math.round((dim.width-2*lx)*used_space),(int)Math.round(ly+yoffset)+(int)Math.round((dim.height-2*ly)*used_space/2),(int)Math.round(lx+xoffset)+(int)Math.round((dim.width-2*lx)*used_space),(int)Math.round(ly+yoffset)+dh+(int)Math.round((dim.height-2*ly)*used_space/2));
        
        double all = summ(ycolumns[0]);
        int beg_gradus = 0;
        for (int i=0;i<ds.getCountRows();i++){
            double y = ((Double)ds.getValue(i,ycolumns[0])).doubleValue();    
            int gr_percent = (int)Math.round(y*360/all);
            System.out.println("gr_percent="+gr_percent);
            g.setColor(dataColors[i%dataColors.length]);
            g.fillArc((int)Math.round(lx+xoffset),(int)Math.round(ly+yoffset),(int)Math.round((dim.width-2*lx)*used_space),(int)Math.round((dim.height-2*ly)*used_space),beg_gradus,gr_percent);
            beg_gradus+=gr_percent;
        
        }
        //g.drawLine(lx,ly,lx,dim.height-ly);
        //g.drawLine(lx,dim.height-ly,dim.width-lx,dim.height-ly);    
        
    }
    public double min(int column){
        double m = 100000;
        for(int i=0;i<ds.getCountRows();i++){
            Double d = (Double)ds.getValue(i,column);
            if (d.doubleValue()<m) m=d.doubleValue();
        };
        return m;
        
    }
    public double max(int column){
        double m = -100000;
        for(int i=0;i<ds.getCountRows();i++){
            Double d = (Double)ds.getValue(i,column);
            if (d.doubleValue()>m) m=d.doubleValue();
        };
        return m;
    }
    public void drawLegenda(Graphics g){
        int x = lx;
        int y = ly;
        int h = g.getFontMetrics().getHeight();
        int m = 0;
        int k = -1;
        for(int i=0;i<names.length;i++){
            if(names[i].length()>m) {
                m = names[i].length();
                k = i;
            }
        }
        char[] ar = new char[names[k].length()];
        names[k].getChars(0,names[k].length()-1,ar,0);
        int max_len = g.getFontMetrics().charsWidth(ar,0,ar.length);
        g.setColor(grColor);
        g.drawRect(x+ldx,y+h+ldy,max_len+h+llx*2,names.length*h+lly*2);
        for(int i=0;i<ycolumns.length;i++){
            y += h;
            g.setColor(dataColors[i%dataColors.length]);
            g.fillRect(x+llx+ldx,y+lly+ldy,h,h);
            g.setColor(grColor);
            g.drawString(names[i],x+h+llx+ldx,y+h-2+lly+ldy);
        }        
    } 
    public void drawGrid(Graphics g,int st){//0-center aligment
                                            //1-left aligment
        g.setColor(grColor);                //-1 - nolabel
        Dimension dim = getSize();
        //System.out.println("dimension="+dim);
        g.drawLine(lx,ly,lx,dim.height-ly);
        g.drawLine(lx,dim.height-ly,dim.width-lx,dim.height-ly);    
        double mashtab_x = (dim.width-2*lx)/ds.getCountRows();
        FontMetrics fm = g.getFontMetrics();
        for (int i=0;i<ds.getCountRows();i++){
            String value = ds.getValue(i,xlcolumn).toString();
            char[] ar = new char[value.length()];
            value.getChars(0,value.length()-1,ar,0);
            double l = fm.charsWidth(ar,0,value.length());
            Font deffont = g.getFont();
            while (l>mashtab_x){
                Font fnt = g.getFont();
                //begin increase font
                int size = fnt.getSize();
                Font fnt2;
                //System.out.println("l="+l+" mashtab_x="+mashtab_x);
                if (size>1) {
                    fnt2 = new Font(fnt.getName(),fnt.getStyle(),size-1);
                    g.setFont(fnt2);
                }
                else break;
                l = g.getFontMetrics().charsWidth(ar,0,value.length());
                
            }//getFont
            switch(st){
            case 0:g.drawString(value,(int)Math.round(lx+(mashtab_x*i)+(mashtab_x-l)/2),(dim.height-ly)+ly/2);break;    
            case 1:g.drawString(value,(int)Math.round(lx+(mashtab_x*i)),(dim.height-ly)+ly/2);break;    
            case -1: break;
            }
            g.setFont(deffont);
        }
        
        
    }
    public void paint(Graphics g){
        System.out.println("paint called:"+g );
        //drawGrid(g);
        switch(style){
        case 0:paint_0(g);break;
        case 1:paint_1(g);break;
        case 2:paint_2(g);break;
        case 3:paint_3(g);break;
        default:System.out.println("Unknown style!");
        }
        drawLegenda(g);
    } 
    public void retrieve(){
        try{
        ds.retrieve();
        }catch(Exception e){}
    }
    public void update(){}
    public void fromDS(){}
    public void toDS(){}
}