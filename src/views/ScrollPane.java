
package views;
import java.awt.*;
import rml.Proper;

public class ScrollPane extends java.awt.ScrollPane implements Retrieveable {
     public int bg_color = Color.lightGray.getRGB();
     public void init(Proper prop) {
        String sp ;
        if (prop!=null) {
            sp = (String)prop.get("BG_COLOR");
            if (sp != null) {
                try {
                    int red = Integer.parseInt(sp.substring(1,3),16);
                    int green = Integer.parseInt(sp.substring(3,5),16);
                    int blue = Integer.parseInt(sp.substring(5,7),16);
                    bg_color = ((red<<16) + (green<<8) + blue);                    
                }
                catch(Exception e) {
                    System.out.println("~views.ScrollPane::init() : " + e.getMessage());
                }
            }            
            setBackground(new Color(bg_color));
        }
     }
     
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
     
     public void fromDS(){
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
}
