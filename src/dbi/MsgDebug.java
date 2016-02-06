

package dbi;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import loader.*;

public class MsgDebug extends Frame{

		Panel p = new Panel();
		Button ButOk = new Button("Ok");
		Button ButCancel = new Button("Cancel");
                Button ButClear = new Button("Clear");
		boolean ok=false;
		TextArea ta;


	public MsgDebug(String title){
	    super(title);

		p.setLayout(new BorderLayout());
		
		ta = new TextArea();

		ta.setEditable(false);

		p.add("Center",ta);
		Panel bp = new Panel();
		p.add("South",bp);
		bp.setLayout(new FlowLayout(FlowLayout.CENTER));

		bp.add(ButOk);
		bp.add(ButCancel);
                bp.add(ButClear);
        add(p);
            addWindowListener(new WindowAdapter() {

              public void windowClosing(WindowEvent e) {
                hide();
              }

            });
		

		//if (ok) hide();

	}
	public void addMessage(String msg){
//            System.out.println(GLOBAL.c2b(msg,GLOBAL.FIELD));
	    ta.appendText(GLOBAL.c2b(msg,GLOBAL.FIELD));
	    //ta.appendText(msg);
	    //ta.setText(msg);
	    //fr.show();
	    }
	public boolean handleEvent(Event ev){
		if (ev.id == Event.ACTION_EVENT){
			if (ev.target == ButOk) {
				ok = true;
			}else if (ev.target == ButCancel) {
				ok = false;
                        }else if (ev.target == ButClear) {
                                ta.setText("");
                                return true;
			}else return super.handleEvent(ev);
			hide();
			return true;
		}else return super.handleEvent(ev);
	}
}


