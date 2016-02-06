
package views;

import java.awt.*;
import java.awt.event.*;
import rml.Proper;

public class SplitPanel extends Panel implements Retrieveable{    
    public final static int HORIZONTAL = 1;
    public final static int VERTICAL = 2;
    boolean gapMoving = true;
    boolean beginDrag = false;
    int type = HORIZONTAL;
    int gapSize = 7;
    int xGap;
    int yGap;
    DragComponent dc = null;
    Image offImage = null; 
    SplitLayout layout;
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    Cursor lrArrow = new Cursor(Cursor.E_RESIZE_CURSOR);
    Cursor udArrow = new Cursor(Cursor.N_RESIZE_CURSOR);
    Panel one = new RetPanel();
    Panel two = new RetPanel();
    
    Gap gap = new Gap();
    Color gapColor = Color.gray;
    int percent = 50;
    
    public SplitPanel() {
        super();
    }
    
    //public void paint(Graphics g) {
    //   super.paint(g);
    //}
    public void retrieve() {        
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof views.Retrieveable) ((views.Retrieveable)comps[i]).retrieve();
        }
    }
    
    public void update() {        
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof views.Retrieveable) ((views.Retrieveable)comps[i]).update();
        }
    }
    public void fromDS() {
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof views.Retrieveable) ((views.Retrieveable)comps[i]).fromDS();
        }
    }
    public void toDS(){
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof views.Retrieveable) ((views.Retrieveable)comps[i]).toDS();
        }
    }

    public void init(Proper prop) {
        if (prop!=null) {
            String sp = (String)prop.get("TYPE");
            if (sp!=null){
                if (sp.equals("HORIZONTAL")) type = HORIZONTAL;
                if (sp.equals("VERTICAL")) type = VERTICAL;
            }

            Integer ip = (Integer)prop.get("PERCENT");
            if (ip!=null) {
                int x = ip.intValue();
                if (x<0)  x = 0;
                if (x>100) x = 100;
                percent = x;
            }

            ip = (Integer)prop.get("GAPSIZE");
            if (ip!=null) {
                gapSize = ip.intValue();
            }

            sp = (String)prop.get("GAPCOLOR");
            if (sp!=null) {
                try {
                    int red = Integer.parseInt(sp.substring(1,3),16);
                    int green = Integer.parseInt(sp.substring(3,5),16);
                    int blue = Integer.parseInt(sp.substring(5,7),16);
                    gapColor = new Color((red<<16) + (green<<8) + blue);
                }
                catch(Exception e) {System.out.println("~views.SplitPanel.init() : " + e.getMessage());}
            }
        }
        else System.out.println("~views.SplitPanel.init() : prop=null!");
        one.setLayout(new GridLayout(1,1));
        two.setLayout(new GridLayout(1,1));
        layout = new SplitLayout(type,gapSize,percent);
        setLayout(layout);
        switch(type) {
            case HORIZONTAL:gap.setCursor(udArrow);break;
            case VERTICAL:gap.setCursor(lrArrow);break;
        }
        gap.setColor(gapColor);
        add(one);
        add(two);
        add(gap,0);
        gap.addMouseListener(new ML());
    }
    
    /*public SplitPanel(int type,int gapSize, int percent,Color color) {       
        this.type = type;
        this.gapSize = gapSize;
        one.setLayout(new GridLayout(1,1));
        two.setLayout(new GridLayout(1,1));
        layout = new SplitLayout(type,gapSize,percent);
        setLayout(layout);                
        switch(type) {
            case HORIZONTAL:gap.setCursor(udArrow);break;
            case VERTICAL:gap.setCursor(lrArrow);break;
        }
        gap.setColor(color);
        add(one);
        add(two);
        add(gap);
        gap.addMouseListener(new ML());        
    }
    
    public SplitPanel(int type) {
        this(type,5,50,Color.gray);
    }
    
    public SplitPanel() {
        this(HORIZONTAL);
    }
    
    public boolean mouseDrag(Event e, int x, int y) {
        System.out.println("x = " + x + "; y = " + y);
        return true;
    }*/
   
    
    public void addChildren(Object[] children) {
        if (children.length!=2) {
            System.out.println("SplitPanel.addChildren: Bad number of children !");
            return;
        }
        try {
            addOne((Component)children[0]);
            addTwo((Component)children[1]);
        }
        catch(Exception e) {
            System.out.println("SplitPanel.addChildren.Exception : " + e);            
        }
    }
    public void addOne(Component c) {
        one.add(c);
    }    
    public Component getOne() {return one;}
    
    public void addTwo(Component c) {
        two.add(c);
    }
    public Component getTwo(){return two;}
    
    public Component getGap(){return gap;}
    
    public void setGapMoving (boolean moving) {
        gapMoving = moving;
    }
    
    public boolean isGapMoving() {
        return gapMoving;
    }    
    
    class Gap extends Canvas {
        public Gap() {
            super();
        }
        
        public void setColor(Color color) {
            setBackground(color);
        }
		
        public void paint(Graphics g) {
            //Rectangle r = getBounds();			
			switch (type){
			case VERTICAL:{
				int h = getBounds().height;
				g.setColor(new Color(0xDFDFDF));
				g.drawLine(0,0,0,h);
				g.setColor(new Color(0xFFFFFF));
				g.drawLine(1,0,1,h);
				g.setColor(new Color(0xBFBFBF));
				g.fillRect(2,0,3,h);
				g.setColor(new Color(0x7F7F7F));
				g.drawLine(5,0,5,h);
				g.setColor(new Color(0x000000));
				g.drawLine(6,0,6,h);			
				break;
			}
			case HORIZONTAL:{
				int w = getBounds().width;
				g.setColor(new Color(0xDFDFDF));
				g.drawLine(0,0,w,0);
				g.setColor(new Color(0xFFFFFF));
				g.drawLine(0,1,w,1);
				g.setColor(new Color(0xBFBFBF));
				g.fillRect(0,2,w,3);
				g.setColor(new Color(0x7F7F7F));
				g.drawLine(0,5,w,5);
				g.setColor(new Color(0x000000));
				g.drawLine(0,6,w,6);			
				break;
			}
			}
		}
    }
    
    class SplitLayout implements LayoutManager {
        
        int type;
        int gapSize;
        int percent = SplitPanel.this.percent;
        
        public SplitLayout(int type, int gapSize){
            this.type = type;
            this.gapSize = gapSize;
        }
        
        public SplitLayout(int type, int gapSize, int percent) {
            this(type,gapSize);
            this.percent = percent;
        }
        
         
        public void addLayoutComponent(String name, Component comp){} 
        public void removeLayoutComponent(Component comp){}
        public Dimension preferredLayoutSize(Container parent){
            //return new Dimension(100,100);
            return null;
        }
        public Dimension minimumLayoutSize(Container parent){
            //return new Dimension(100,100);
            return null;
        }
        public void layoutContainer(Container parent){            
            int width = parent.getSize().width;
            int height = parent.getSize().height;
            int onex=0;
            int oney=0;            
            int onewidth=0 ;
            int oneheight=0;
            int twox=0;
            int twoy=0;
            int twowidth=0;
            int twoheight=0;            
            switch (type) {
                case SplitPanel.VERTICAL: {
                    int totalwidth = width-gapSize;
                    onewidth = totalwidth*percent/100;
                    twowidth = width-gapSize-onewidth;
                    oneheight = height;
                    twoheight = height;
                    twox = onewidth + gapSize;
                    twoy = 0;
                    ((SplitPanel)parent).getGap().setBounds(onewidth,0,gapSize,height);
                    ((SplitPanel)parent).xGap = onewidth;
                    ((SplitPanel)parent).yGap = 0;

                }break;
                
                case SplitPanel.HORIZONTAL: {
                    int totalheight = height-gapSize;
                    oneheight = totalheight*percent/100;
                    twoheight = height-gapSize-oneheight;
                    onewidth = width;
                    twowidth = width;
                    twox = 0;
                    twoy = oneheight + gapSize;
                    ((SplitPanel)parent).getGap().setBounds(0,oneheight,width,gapSize);
                    ((SplitPanel)parent).xGap = 0;
                    ((SplitPanel)parent).yGap = oneheight;
                }
            }
            Panel one = (Panel)((SplitPanel)parent).getOne();
            Panel two = (Panel)((SplitPanel)parent).getTwo();
            one.setBounds(0,0,onewidth,oneheight);
            one.validate();
            two.setBounds(twox,twoy,twowidth,twoheight);
            two.validate();
            
            
        }
        
        public void moveGap(int x, int y) {
            SplitPanel parent = SplitPanel.this;
            int width = parent.getSize().width;
            int height = parent.getSize().height;
            int onex=0;
            int oney=0;            
            int onewidth=0 ;
            int oneheight=0;
            int twox=0;
            int twoy=0;
            int twowidth=0;
            int twoheight=0;           
            switch (type) {
                case SplitPanel.VERTICAL: {
                    int totalwidth = width-gapSize;
                    if (x<0) x=0;
                    if (x+gapSize>width ) x = width - gapSize;
                    SplitPanel.this.xGap = x;
                    percent = x * 100 / totalwidth;
                    layoutContainer(parent);             
                }break;
                
                case SplitPanel.HORIZONTAL: {
                    int totalheight = height-gapSize;
                    if (y<0) y=0;
                    if (y+gapSize>height ) y = height - gapSize;
                    SplitPanel.this.yGap = y;
                    percent = y * 100 / totalheight;
                    layoutContainer(parent);
                }
            }   
        }
    }
    
    class ML extends MouseAdapter {
        public void mousePressed(MouseEvent e){
            SplitPanel.this.beginDrag = true;
            /*int w = SplitPanel.this.getSize().width;
            int h = SplitPanel.this.getSize().height;
            offImage = createImage(w,h);
            Graphics bg = offImage.getGraphics();
            SplitPanel.this.printAll(bg);
            dc = new DragComponent(w,h, offImage);
            dc.setVisible(true);
            SplitPanel.this.add(dc,0);
            dc.repaint();*/
        }
        public void mouseReleased(MouseEvent e) {           
            /*if (dc!=null) {
                SplitPanel.this.remove(dc);
                dc=null;
            }*/
            int dx = SplitPanel.this.xGap;
            int dy = SplitPanel.this.yGap;

            if (beginDrag) {
                SplitPanel.this.layout.moveGap(e.getX()+dx, e.getY()+dy);
            }
            SplitPanel.this.beginDrag = false;
        }
    }
}
