
package views;

import java.awt.*;
import java.util.*;
import rml.*;

public class RadioButton extends Checkbox {
    
    String value=null;
    
    boolean check = false;
    
    public RadioButton() {
        super();
    }
    
    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        
        value = (String)prop.get("VALUE");
        
        sp = (String)prop.get("CHECK");
        if (sp!=null && sp.equals("YES")) check = true;
        else check = false;
        
        sp = (String)prop.get("LABEL");
        if (sp!=null) setLabel(sp);
    }
    
    public Object getValue() {
        return value;
    }
}
