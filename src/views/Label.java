

package views;
import java.awt.*;
import java.awt.event.MouseEvent;

import rml.*;
import views.FORM;

import java.util.*;
import calc.*;
import calc.objects.*;

public class Label extends Component implements GlobalValuesObject,class_type,
    class_method, Selectable{
    //private int left,top,width,height;
    private String font_face = "Serif";//"Dialog";//имя фонта
    private int font_family =  0;//стиль фонта(Plain,Bold,Italic)
    private int font_size = 12;
    //private int font_color = 0, bg_color = 16777215, font_bg_color = 0;
    
    int dw=2;
    int dh=2;
    String border = "NONE";
    
    Font scaleFont = null;//данный фонт используется для отрисовки
                          //с масштабированием 
    FontMetrics fm = null;
    String svalue = null;
    String halignment = "LEFT";
    public void setHalignment(String halignment) {
		this.halignment = halignment;
	}

	public void setValignment(String valignment) {
		this.valignment = valignment;
	}

	public void setWordWrap(boolean wordWrap) {
		this.wordWrap = wordWrap;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}
	String valignment = "CENTER";
    String visible="YES";
    
    Object parent = null;
    Hashtable aliases = null;
    
    public boolean needTranslate = false;
    boolean wordWrap = false;
    boolean multiLine = false;
    //private int title_font_color = 0, title_bg_color = 16777215, title_font_bg_color = 0;  
    
    Color bg_color;
    
    public Label() {
        super();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
    
    public FORM getFormParent() {
        Container parent = super.getParent();
        if (parent instanceof FORM) return (FORM)parent;else return null;
    }
    
    public Color getNormalBgColor() {
    	return bg_color;
    	
    }
    
    public void init(Proper prop, Hashtable aliases) {
        this.aliases = aliases;
        String sp;
        Integer ip;
        int x=0,y=0,width=40,height=20;
        if (prop!=null){
            ip = (Integer)prop.get("LEFT");
            if (ip!=null) {
                x = ip.intValue();
            }
            ip = (Integer)prop.get("TOP");
            if (ip!=null) {
                y = ip.intValue();
            }            
            ip = (Integer)prop.get("WIDTH");
            if (ip!=null) {
                width = ip.intValue();
            }
            ip = (Integer)prop.get("HEIGHT");
            if (ip!=null) {
                height = ip.intValue();
            }
            setBounds(x,y,width,height);            
            sp = (String)prop.get("FONT_FACE");
            if (sp!=null) {
                font_face = sp;
            }
            ip = (Integer)prop.get("FONT_FAMILY");
            if (ip!=null) {
                font_family = ip.intValue();
            }
            ip = (Integer)prop.get("FONT_SIZE");
            if (ip!=null) {
                font_size = ip.intValue();
            }
            scaleFont = new Font(font_face,font_family,font_size);
            setFont(scaleFont);
            fm = getFontMetrics(scaleFont);
            
            sp = (String)prop.get("FONT_COLOR");
            if (sp!=null) setForeground(UTIL.getColor(sp));
            else setForeground(Color.black);
            
            bg_color = getBackground();
            
            sp = (String)prop.get("BG_COLOR");
            if (sp!=null) setBackground(UTIL.getColor(sp));
            else setBackground(Color.white);
            
            sp = (String)prop.get("HALIGNMENT");
            if (sp!=null) halignment = sp;
            
            sp = (String)prop.get("VALIGNMENT");
            if (sp!=null) valignment = sp;
            
            sp = (String)prop.get("BORDER");
            if (sp!=null) border=sp;
            
            sp = (String)prop.get("MULTILINE");
            if (sp!=null && sp.toUpperCase().equals("YES"))
                multiLine = true;
            else multiLine = false;
            
            sp = (String)prop.get("WORDWRAP");
            if (sp!=null && sp.toUpperCase().equals("YES"))
                wordWrap = true;
            else wordWrap = false;
            
            sp = (String)prop.get("VALUE");
            if (sp!=null) svalue = sp;

            sp = (String)prop.get("VISIBLE");
            if (sp!=null) visible = sp;

            try {
              if (Calc.macro(visible, aliases).equals("NO")) setVisible(false);
            } catch (Exception e) {
            }
        }
    
    }
    
    public FontMetrics getFm() {
		return fm;
	}

	public void setFm(FontMetrics fm) {
		this.fm = fm;
	}

	public void setScaleFont(int a) {
        Font tmp = getFont();
        if (tmp==null) return;
        scaleFont = new Font(tmp.getName(),tmp.getStyle(), tmp.getSize()*a/100);
    }
    
    public void paint(Graphics g, int a) {
        try {
          if (Calc.macro(visible, aliases).equals("NO")) return;
        } catch (Exception e) {
        }
        int dx = getBounds().x;
        int dy = getBounds().y;
        if (! needTranslate) {dx=0;dy=0;}        
        g.translate(dx*a/100,dy*a/100);        
        int width = getSize().width;
        int height = getSize().height;        
        /*-----------*/        
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
        
        /*if (svalue!=null) {
            g.setColor(getForeground());
            int[] xy = UTIL.getOutPoint(width,height,fm,
                halignment,valignment,dw,dh,0,0,svalue);      
            g.setClip(0,0,(getSize().width-dw)*a/100,(getSize().height-dh)*a/100);
            g.drawString(svalue,xy[0]*a/100,xy[1]*a/100);
        }*/
        if (svalue!=null) {
            g.setColor(getForeground());
            if (multiLine) {//нужно распарсить строки и сделать выравнивание
                String svalue1;
                if (wordWrap) {
                    svalue1 = UTIL.makeWrap(svalue," ",getBounds().width-dw-3 ,fm);
                }else svalue1 = svalue;
                StringTokenizer st = new StringTokenizer(svalue1, "\n", true);
                int cnt = st.countTokens();//кол-во строк
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
    
    public void paint(Graphics g) {
        paint(g, 100);
    }
    
    public Object method(String method,Object arg) throws Exception{
        if (method.equals("SETVALUE") && (arg instanceof String) ) {
            svalue = (String)arg;
            Graphics temp = getGraphics();
            if (temp!=null) paint(temp);
            else repaint();            
        }else {
            throw new RTException("HasNotMethod","method "+method+
							  " not defined in class views.Label");
        }
        return new Nil();
    }
    public String type(){
		return "VIEWS_LABEL";
	}
    
    public void processMouseEvent(MouseEvent e) {
        switch(e.getID()) {
            case MouseEvent.MOUSE_PRESSED:
            	FORM parent = getFormParent();
    			if (parent != null)
    				if (parent.isEditMaket()) {
    					if (parent.addMarkChild(this)) {
    						//setBackground(Color.BLUE);
    					} else {
    						//setBackground(bg_color);
    					}
    					repaint();
    					break;
    				}
            break;
        }
    }
    
	
	//Методы интерфейса GlobalValuesObject
    public void setValue(Object o){svalue  = o.toString();}
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}
}
