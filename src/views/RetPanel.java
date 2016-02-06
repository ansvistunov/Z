
package views;
import java.awt.*;

public class RetPanel extends Panel implements Retrieveable {
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