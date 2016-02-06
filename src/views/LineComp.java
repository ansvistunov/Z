
package views;
import java.awt.*;

public class LineComp extends Panel {
    int height = 0;
    public LineComp(int height) {
        super();
        this.height = height;        
        setSize(1,height);
    }
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.drawLine(0,0,0,height);
    }
}
