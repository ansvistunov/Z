
package views;
import java.awt.*;
import java.awt.event.*;

public class DragComponent extends Canvas {
    Image im = null;
    public DragComponent(int width, int height, Image im) {
        super();
        this.im = im;
        setSize(width, height);
    }
    
    public boolean mouseMove(Event e, int x, int y) {
        System.out.println("Mouse move");
        return true;
    }
    
    public boolean mouseDrag(Event e, int x, int y) {
        System.out.println("Mouse drag");
        return true;
    }

    public void update(Graphics g) {
        paint(g);
    }

    
    public void print(Graphics g) {
        return;
    }
    
    public void printAll(Graphics g) {
        return;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        int w = getSize().width/2;
        int h = getSize().height/2;
        //System.out.println("width = "+(w*2));
        //System.out.println("height = "+(h*2));
        if (im!=null) g.drawImage(im,0,0,getBackground(),this);
        //System.out.println("child count = "+parent.getComponentCount());
        //Graphics gp = parent.getGraphics();
        g.setColor(Color.red);
        //g.fillRect(0,0,w*2, h*2);
        g.drawLine(0,h,w*2,h);
        g.drawLine(w,0,w,h*2);
    }
        
}
