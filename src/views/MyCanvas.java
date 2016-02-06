package views;
import java.awt.*;
import java.awt.event.*;
public class MyCanvas extends Canvas {
    boolean beginDrag = false;
    int count;
    Panel offpanel = new Panel();
    Image offimage = null;
    Frame workframe = new Frame(); 
    Grid parent = null;
    FButton[] wbut;
    HotRegions hr = null;
    boolean insideTitleBar = false;
    Cursor lrCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    int lc = 0;
    int begin;
    int end;
    int reg;
    final int REPDELAY=400;
    int repdelay =REPDELAY;
    boolean clean;
    boolean painted=false;
    boolean paintCurrentRow = false;
    Graphics dragGraphics = null;    
    public MyCanvas(Grid g) {
        super();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        enableEvents(AWTEvent.ACTION_EVENT_MASK);
        parent = g;
		setBackground(parent.getBackground());
    }
    
    public void init() {        
        try {
        /*workframe.setBounds(0,0,10,10);
        workframe.setLayout(null);
        offpanel.setLayout(null);        
        offpanel.removeAll();
        offpanel.setSize(getSize().width,parent.sizeRow);*/
        

        /*wbut = new Button[parent.numColumns];
        int size = 0;
        int[] regs = new int[parent.numColumns];
        for (int i=0;i<parent.numColumns;i++) {
            wbut[i] = new Button(parent.getVColumn(i).title);
            wbut[i].setBackground(parent.tbbg_color);
            wbut[i].setForeground(parent.getVColumn(i).title_font_color);
            wbut[i].setFont(parent.getVColumn(i).title_font);
            size+=parent.getVColumn(i).size;
            regs[i] = size;
            wbut[i].setBounds(size-parent.getVColumn(i).size,0,parent.getVColumn(i).size,parent.headColSize);            
            offpanel.add(wbut[i]);
        }*/
		//
		wbut = new FButton[parent.numColumns];
        int size = 0;
        int[] regs = new int[parent.numColumns];
        for (int i=0;i<parent.numColumns;i++) {
            wbut[i] = new FButton(parent.getVColumn(i).title);
            wbut[i].setBackground(parent.tbbg_color);
            wbut[i].setForeground(parent.getVColumn(i).title_font_color);
            wbut[i].setFont(parent.getVColumn(i).title_font);
            size+=parent.getVColumn(i).size;
            regs[i] = size;			
            wbut[i].setBounds(size-parent.getVColumn(i).size,0,parent.getVColumn(i).size,parent.headColSize);
        } 
		
		//        
        hr = new HotRegions(regs);
        }
        catch(Exception e) {
            System.out.println("~views.MyCanvas::init : "+e);
        }

    }

    /*public boolean mouseDrag(Event e,int x,int y) {
        return true;
    }*/

    public void mousePressed(MouseEvent e) {
        //alex patch возвращает фокус в grid
        parent.emptyButton.requestFocus();
        //alex
        if (getCursor().equals(lrCursor)) {
                beginDrag = true;
                begin=e.getX();
                reg = hr.getPointReg(e.getX());
                lc = hr.getXReg(reg);//begin;
                dragGraphics = getGraphics();
                dragGraphics.setXORMode(getBackground());
                dragGraphics.setColor(Color.gray);
                dragGraphics.drawLine(lc,0,lc,getSize().height);
                //
                //lc = new LineComp(getSize().height);
                //parent.add(lc,0);
                //lc.setLocation(parent.headRowSize+begin,0);
                //lc.repaint();
                return;
        }
        if (parent.numRows==0) return;
        int[] xy = getRC(e.getX(),e.getY());
        if (xy[0]==-1 || xy[1]==-1) return;
        switch (e.getClickCount()){
            case 1:{
                parent.emptyButton.requestFocus();
                boolean b = parent.editMode;
                parent.processEnter();
                if (xy[1]>parent.endColumn) parent.redrawTitleBar=true;
                else parent.redrawTitleBar=false;
                parent.setCurrentRow(xy[0], true);
                parent.setCurrentColumn(xy[1],true);
                if (b) parent.createField();
                repdelay=0;                
                if (parent.redrawTitleBar) parent.repaint();
                //else repaint();
                break;
            }
            case 2:{
                //while(!painted);
                doubleClickReaction();
            }
        }
        return;
    }

    public void mouseReleased(MouseEvent e) {
        if (beginDrag) {
            if (dragGraphics != null) {
                dragGraphics.drawLine(lc,0,lc,getSize().height);
                dragGraphics = null;
            }
            if (parent.field!=null) {
                parent.remove(parent.field);
                parent.field=null;
                parent.editMode=false;
            }
            end = e.getX();
            hr.moveReg(reg,end-begin);
            int x1 = hr.getXReg(reg);
            int x0;
            if (reg>0) x0 = hr.getXReg(reg-1);
            else x0 = 0;
            parent.getVColumn(reg+parent.beginColumn).size = x1-x0;
            //Пересчет размеров заголовков для колонок
            int size = 1;//0;
            for (int i=0;i<parent.numColumns;i++) {
                size+=parent.getVColumn(i).size;
                wbut[i].setBounds(size-parent.getVColumn(i).size,0,parent.getVColumn(i).size,parent.headColSize);
            }
            clean=true;
            repdelay=0;
            parent.repaint();
        }
        beginDrag = false;
        return;
    }
    
    public void mouseDrag(MouseEvent e) {        
        if (dragGraphics != null) {
            dragGraphics.drawLine(lc,0,lc,getSize().height);
            lc = e.getX();
            dragGraphics.drawLine(lc,0,lc,getSize().height);
        }
    }
 
    public void mouseMove(MouseEvent e){
        
        if ((e.getY()>=0) && (e.getY()<parent.headColSize)){                
            insideTitleBar = true;                
        }
        else insideTitleBar = false;        
        if (insideTitleBar) {
            int reg = hr.getPointReg(e.getX());
            if (reg>=0) {
                setCursor(lrCursor);
            }
            else setCursor(defaultCursor);
         }
        else setCursor(defaultCursor);
    }
    
    public void processMouseEvent(MouseEvent e) {
        if (e.getID()==MouseEvent.MOUSE_PRESSED) {
            mousePressed(e);
        }
        if (e.getID()==MouseEvent.MOUSE_RELEASED) {
            mouseReleased(e);
        }
        //System.out.println("Mouse Event occurs");        
        if (e.isPopupTrigger()) {
            createPopupMenu(e.getX(),e.getY());
        }
    }
    
    public void processMouseMotionEvent(MouseEvent e) {
        if (e.getID()==MouseEvent.MOUSE_MOVED) {
            mouseMove(e);
        }
        if (e.getID()==MouseEvent.MOUSE_DRAGGED) {
            mouseDrag(e);
        }
    }

    public void repaint() {
        painted=false;
        repaint(repdelay);
        //if (parent.redrawTitleBar) repaint(repdelay);
        //else       
       // repaint(repdelay,0,parent.headColSize,getSize().width,getSize().height);
        repdelay=REPDELAY;
    }
    public void repaintCurrentRow() {
        paintCurrentRow = true;
        repaint();
    }
    
    public void update(Graphics g) {
        if (clean) {clean=false;super.update(g);}
        else paint(g);        
    }
    
    Graphics bg=null;
    public void paint(Graphics g) {        
        int beg = parent.beginColumn;//начальный столбец для рисования
        int end = parent.endColumn;//конечный столбец для рисования
        boolean bonusCol = false;
        if (end<parent.helpArray.length-1) {
            end++;
            bonusCol = true;
        }
		rebuildoffpanel(g,beg,end);
        /*try {        
        if (parent.redrawTitleBar && parent.headColSize>0){
            rebuildoffpanel(beg,end);         
            int iw = 0;
            if (bonusCol) iw = getSize().width; else iw = parent.getVisibleColumnsSize();            
            if (offimage==null) offimage = createImage(getToolkit().getScreenSize().width,parent.headColSize);
            else {
                if (bg==null) bg = offimage.getGraphics();            
                bg.setColor(getBackground()); 
                bg.fillRect(0,0,offimage.getWidth(null), offimage.getHeight(null));
            }
            if (offimage==null) {
                System.out.println("offimage=null");
            }else {
                if (bg==null) bg = offimage.getGraphics();
                offpanel.printAll(bg);
                g.drawImage(offimage,1,0,Color.black,this);                
                offpanel.printAll(bg);
                g.drawImage(offimage,1,0,Color.black,this);
            }        
		}
		}
        catch(Exception e) {System.out.println("~views.MyCanvas::paint (1) : "+e);}
		*/
        try{           
            int delta = parent.getColumnX(beg);
            int ib,ie;
            if (paintCurrentRow){
                ib = parent.currentRow - parent.beginRow;
                ie = ib+1; 
            }else{
                ib = 0;
                ie = parent.visibleRows;
            }
            for (int i = 0; i < parent.visibleRows; i++) {                
                boolean selRow = parent.selection.contains(new Integer(parent.beginRow+i));
                if (selRow) {
                        g.setClip(0,0,getSize().width,getSize().height);
                        Color prev=g.getColor();
                        //g.setColor(parent.gridColor);
                        //g.setColor(Color.blue);
                        g.setColor(parent.currow_color);
                        int barsize = 0;
                        for (int k=beg;k<=end;k++)
                            barsize+=parent.getVColumn(k).size;
                        
                        g.fillRect(0,i*parent.sizeRow+parent.headColSize,barsize,parent.sizeRow);
                        g.setColor(prev);
                }
                for (int j=beg;j<=end;j++) {                    
                    //if (parent.items[i][j]==null) break;
                    g.setFont(parent.getVColumn(j).font);
                    int x = parent.getColumnX(j);
                    x-=delta;
                    int y = parent.getRowY(i);                    
                    int height = parent.sizeRow;
                    int width = parent.getVColumn(j).size;
                    //String str = parent.items[i][j];
                    String str = parent.getSourceText(i+parent.beginRow-1,j);//(items[i][j];
                    int[] xy = getOutPoint(parent.getVColumn(j),x,y,str);
                    g.setClip(x+1,y+1,width-1,height-1);
                    if ( ((parent.beginRow+i==parent.currentRow) && (j==parent.currentColumn)) ||
                           parent.ctrlA == true) {
                        Color prev=g.getColor();                       
                        
                        //g.setColor(Color.red);
                        g.setColor(parent.currow_bg_color);
                        g.fillRect(x,y,width,height);                        
                        //g.setColor(Color.blue);
						g.setColor(parent.currow_color);
                        g.drawString(str,xy[0],xy[1]);
                        
                        g.setColor(prev);
                    }else {
                        if (!selRow) {
                            g.setColor(getBackground());
                            g.fillRect(x,y,width,height);
                            g.setColor(parent.getVColumn(j).font_color);                                                    
                        }
                        else g.setColor(Color.white);
                        g.drawString(str,xy[0],xy[1]);
                    }
                }                
            }
        
        
        g.setClip(0,0,getSize().width,getSize().height);
        //if (parent.needDrawGrid) drawGrid(g);
        if (true) drawGrid(g);
        //parent.needDrawGrid = false;
        parent.redrawTitleBar = true;
        painted=true;
        paintCurrentRow = false;
        }
        catch(Exception e) {System.out.println("exception inside views.MyCanvas::paint (2) : " + e);}
    }
    
    public void drawGrid(Graphics g) {
        if (parent.numRows==0) return;
        g.setColor(parent.gridColor);
        int r = parent.visibleRows; 
        int c = parent.numColumns;
        int width = 0;//numColumns*sizeColumn;
        int beg = parent.beginColumn;
        int end = parent.endColumn;
        if (end<parent.helpArray.length-1) end++;
        for (int i = beg; i <= end; i++ ) {
            width+=parent.getVColumn(i).size;
        }        
        int height = parent.visibleRows*parent.sizeRow;
        int size = 0;
        for (int i = 0; i < r; i++) {
             g.drawLine(0,parent.headColSize+i*parent.sizeRow,
                        width,parent.headColSize+i*parent.sizeRow);
        }
        size=0;
        for (int j = beg; j <= end; j++) {
            if (j > beg) size+=parent.getVColumn(j-1).size;
            g.drawLine(size, parent.headColSize,
                       size,parent.headColSize + height);
                
        }
        
        g.drawRect(0,parent.headColSize, width, height);

    }
    
    
    
    class KL extends KeyAdapter {
        public void KeyPressed(KeyEvent e) {
            //System.out.println("Key pressed.Code="+((int)e.getKeyChar()));
        }
    }
    
    public void rebuildoffpanel(Graphics g,int begb,int endb) {        
		int x = 0;
		g.translate(1,0);
		for (int i=begb; i<=endb;i++){			
			wbut[i].paint(g);
			x+=wbut[i].getSize().width;
			g.translate(wbut[i].getSize().width,0);
		}
		g.translate(-1,0);
		g.translate(-x,0);
    }
	/*public void rebuildoffpanel(int begb,int endb) {
        int size=0;        
        offpanel.setSize(parent.getVisibleColumnsSize(),parent.sizeRow);
        for (int i=0; i<wbut.length;i++){
            if (i<begb || i>endb) {
                wbut[i].setVisible(false);                
            }
            else{
                wbut[i].move(size,0);
                wbut[i].setVisible(true);
                size+=wbut[i].getSize().width;
            }
        }
    }*/
    
    public int[] getOutPoint(Column col, int x, int y, String str) {
        int xp,yp;
        int width = col.size;
        int height = parent.sizeRow;
        if (col==null) return null;
        //FontMetrics fm = new FontMetrics(col.font);
        int sw = col.fm.stringWidth(str);
        int sh = col.fm.getHeight()-col.fm.getDescent();
        int desc = 0;//col.fm.getDescent();
        int wwidth = width - 2*col.dw;
        int wheight = height - 2*col.dh;
        if (col.halignment.equals("LEFT")) {
            xp = x+col.dw;            
        }else
        if (col.halignment.equals("RIGHT")) {
            xp = x+ col.dw + wwidth - sw;            
        }else
        if (col.halignment.equals("CENTER")) {
            xp = x + col.dw + (wwidth - sw)/2;
        }
        else xp = x+col.dw;
        
        if (col.valignment.equals("BOTTOM")) {
            yp = y+col.dh+wheight-desc;            
        }else
        if (col.valignment.equals("TOP")) {
            yp = y + col.dh + sh - desc;            
        }else
        if (col.valignment.equals("CENTER")) {
            yp = y + col.dh+sh+(wheight-sh)/2 - desc;
        }
        else yp = y+col.dh+wheight-desc;
        
        int[] ret=new int[2];
        ret[0] = xp-1;
        ret[1] = yp;
        return ret;
    }
    
    public int[] getRC(int x, int y) {
        int bc = parent.beginColumn;
        int ec = parent.endColumn;
        int br = parent.beginRow;
        int er = parent.endRow;
        if (ec<parent.helpArray.length-1) ec++;
        int r=-1,c=-1;
        int size = 0;
        for (int i = bc;i<=ec;i++) {
            size+=parent.getVColumn(i).size;
            if (x>0 && x<size) {c = i;break;}
        }
        
        r = (y-parent.headColSize)/parent.sizeRow;
        if (r<0 || r>(er-br) || y-parent.headColSize<=0) r = -1;else r+=br;
        
        return new int[]{r,c};
    }
    
    public void doubleClickReaction(){
        if (parent.editable.equals("YES")) parent.createField();
        else
        if (parent.editable.equals("NO")) parent.doAction(parent.editAction);
    }
    
    public void createPopupMenu(int x, int y) {
        parent.showPopup(x,y);       
    }
    
    void drawCell(boolean set) {
        if (parent==null) return;
        int br = parent.beginRow;
        int cr = parent.currentRow;
        int bc = parent.beginColumn;
        int cc = parent.currentColumn;
        Graphics g = getGraphics();                       
        boolean selRow = parent.selection.contains(new Integer(cr));
        if (selRow) {
                g.setClip(0,0,getSize().width,getSize().height);
                Color prev=g.getColor();
                //g.setColor(parent.gridColor);
                //g.setColor(Color.blue);
                g.setColor(parent.currow_color);
                int barsize = 0;
                //for (int k=beg;k<=end;k++)
                barsize+=parent.getVColumn(cc).size;
                
                g.fillRect(parent.getColumnX(cc)-parent.getColumnX(bc),
                    (cr-br)*parent.sizeRow+parent.headColSize,
                    barsize,
                    parent.sizeRow);
                g.setColor(prev);
        }                
        g.setFont(parent.getVColumn(cc).font);
        int x = parent.getColumnX(cc);
        int delta = parent.getColumnX(bc);
        x-=delta;
        int y = parent.getRowY(cr-br);                    
        int height = parent.sizeRow;
        int width = parent.getVColumn(cc).size;
        //String str = parent.items[i][j];
        String str = parent.getSourceText(cr-1,cc);
        int[] xy = getOutPoint(parent.getVColumn(cc),x,y,str);
        g.setClip(x+1,y+1,width-1,height-1);
        if (set) {
            Color prev=g.getColor();            
            g.setColor(parent.currow_bg_color);
            g.fillRect(x,y,width,height);                        
            //g.setColor(Color.blue);
			g.setColor(parent.currow_color);
            g.drawString(str,xy[0],xy[1]);
            
            g.setColor(prev);
        }else {
            if (!selRow) {
                g.setColor(getBackground());
                g.fillRect(x,y,width,height);
                g.setColor(parent.getVColumn(cc).font_color);                                                    
            }
            else g.setColor(Color.white);
            g.drawString(str,xy[0],xy[1]);
        }
                                
                    
	}

    
    
    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("menu item choice:"+e.getActionCommand());
        }
    }

}
