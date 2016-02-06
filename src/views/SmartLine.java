package views;
import java.awt.*;

public class SmartLine {
    int width = 10;
    int type = 0;//0-горизонтальная; 1-вертикальная
    public boolean isPrint = false;
    
    private int fwidth;
    private int fheight;
    private int fdesc;
    static int LW = 25;//толщина линии(при рисовании в контексте RGraphics);
                       //толщине линии в 1 пиксел(1/72 дюйма) соответствует
                       //LW=100.
    
    Graphics g = null;
    
    Font font = new Font("Serif",0,width);
    
    public SmartLine(Graphics g){
        this.g = g;
        //calcparam(g);
    }
    /*public SmartLine(int width) {
        this.width = width;
    }
    public SmartLine(int width, int type) {
        this.width = width;
        //font = new Font("Serif",0,width);
        if (type==0||type==1) this.type=type;
    }*/
    
    public void setType(int type) {
        if (type==0||type==1) this.type=type;        
    }
    
    public void setWidth(int width) {
        this.width = width;
        //font = new Font("Serif",0,width);
    }
    
    public void draw(int x, int y,int size, int a) {//a-масштабный коэффициент, a>0
        //if (!isPrint) {
	if (!(g instanceof views.printing.RGraphics)) {
            if (type==0){
                g.setClip(x*a/100,(y-25)*a/100,size*a/100,50*a/100);
                g.drawLine(x*a/100,y*a/100,(x+size-1)*a/100,y*a/100);
            }
            else if (type==1) {
                g.setClip((x-25)*a/100,y*a/100,50*a/100,size*a/100);
                g.drawLine(x*a/100,y*a/100,x*a/100,(y+size-1)*a/100);
            }
            return;
        }
        if (g instanceof views.printing.RGraphics) {
            //рисуем "тонкую" линию
            views.printing.RGraphics gr = (views.printing.RGraphics)g;
            //gr.setClip(0,0,Report.pageSize.width+1, Report.pageSize.height+1);
            switch (type) {
                case 0:{
                    gr.setClip(x,y-25,size,50);
                    gr.drawLine(x,y,x+size-1,y,LW);break;
                }
                
                case 1:{
                    gr.setClip(x-25,y,50,size);
                    gr.drawLine(x,y,x,y+size-1,LW);break;
                }
            }
            return;
        }
        g.setFont(font);       
        calcparam(g);
        int c=0;
        switch (type) {
            case 0:{
                c=size/fwidth+1;
                int dy = 0;
                //if (width>=10) dy=1;
                g.setClip(x,y-25,size,50);
                //if (y-fdesc+1<0) System.out.println("y-fdesc+1="+(y-fdesc+1));
                for (int i=0;i<c;i++) {                    
                    g.drawString("_",x+i*fwidth,y-fdesc+1+dy);
                }
                break;
            }
            case 1:{
                c = size/fheight + 2 ; 
                int off = -1;
                //if (font.getSize()>10) off = -1;
                g.setClip(x-25,y,50,size);
                for (int i=0;i<c;i++) {
                    g.drawString("|",x+off,y+i*fheight);
                }
            }
        }    
    }
    
    protected void calcparam(Graphics g) {
        //FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        FontMetrics fm = g.getFontMetrics(font);
        fwidth = fm.charWidth('_');
        fheight = fm.getAscent();
        if (fwidth>1) fwidth--;
        if (fheight>1) fheight--;        
        fdesc = fm.getDescent();        
    }
    
    /*private void test() {
        FilterDialog fd = new FilterDialog("sdfd",100,100); 
        FilterDialog$FilterStruct fs = fd.getFilterStruct();
    }*/
}
