
package views;
import java.awt.*;
import rml.*;
import java.util.*;

public class Line extends Component {
    int left,top,size;
    public int LINE_HORIZONTAL=0;
    public int LINE_VERTICAL=1;
    int type=LINE_HORIZONTAL;
    Object parent;
    public void init (Proper prop, Hashtable aliases) {
        Integer ip;
        String sp;
        ip = (Integer)prop.get("LEFT");
        if (ip!=null) left=ip.intValue();
        ip = (Integer)prop.get("TOP");
        if (ip!=null) top=ip.intValue();
        ip = (Integer)prop.get("SIZE");
        if (ip!=null) size=ip.intValue();
        sp = (String)prop.get("TYPE");
        if (sp!=null && sp.equals("HORIZONTAL")) type=LINE_HORIZONTAL;
        else type=LINE_VERTICAL;
    }

    public void paint(Graphics g, int a) {
        SmartLine sl = new SmartLine(g);        
        sl.setType(type);
        if (parent instanceof ReportForm) {
            if (((ReportForm)parent).isPrint)
                sl.isPrint=true;
            else sl.isPrint=false;                
        }
        sl.draw(left, top, size, a);
    }
}
