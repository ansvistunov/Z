
/*
 * File: EDITOR.java
 *
 * Created: Tue Jun 22 13:32:02 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;
import calc.*;
import java.awt.*;
import loader.GLOBAL;
import java.awt.event.*;

public class EDITOR extends BaseExternFunction{
	class L implements ActionListener{
		Dialog dialog = null;
		public L(Dialog d){
			dialog = d;
		}
		public void actionPerformed(ActionEvent ev){
			action();
			dialog.hide();
		}
		public void action(){}
	}
	class OKL extends L{
		public OKL(editor ed){
			super(ed);
		}
	}
	class CANL extends L{
		editor ed;
		public CANL(editor ed){
			super(ed);
			CANL.this.ed = ed;
		}
		public void action(){
			ed.restore();
		}
	}
	class editor extends Dialog{
		TextArea area = new TextArea();
		String text = null;
		public editor(){
			super(new Frame(),"",true);
			try{
				Panel p = new Panel();
				p.setLayout(new BorderLayout());
				p.add("Center",area);
				Panel buttons = new Panel();
				Button ok = new Button("Ok");
				Button cancel = new Button("Cancel");
				buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
				buttons.add(ok);
				buttons.add(cancel);
				p.add("South",buttons);
				ok.addActionListener(new OKL(this));
				cancel.addActionListener(new CANL(this));
				add(p);
				setSize(640,480);
			}catch(Exception e){
			}
		}
		public String edit(String s){
			area.setText(s);
			text = s;
			show();
			return area.getText();
		}
		public void restore(){
			area.setText(text);
		}
	}
	static Dimension size = new Dimension(640,480);
	public Object eval() throws Exception{
		try{
			editor ed = new editor();
			ed.setSize(size);
			String s =  ed.edit((String)expr.eval());
			size = ed.getSize();
			return s;
		}catch(Exception e){
			e.printStackTrace();
			throw new RTException("CastException","EDITOR :: call as ($editor 'text')");
		}
	}
}

