
/*
 * File: DMS.java
 *
 * Created: Wed Mar 24 10:45:47 1999
 *
 * Copyright(c) by Almanex Tec. 
 *
 * Author: Alexey Chen
 */

package loader;

import java.awt.*;

public class DMS extends Dialog{
	boolean cancel = false;
	boolean ok = false;
	static Button ButOk = new Button(GLOBAL.c2b(
		GLOBAL.pr(GLOBAL.MSG_OKBUTTON,"Ok"),GLOBAL.BUTTON));
	static Button ButCancel = new Button(GLOBAL.c2b(
		GLOBAL.pr(GLOBAL.MSG_CANCELBUTTON,"Cancel"),GLOBAL.BUTTON));
	static String title;
	static Dimension dm;
	static Font fn;
	static Font tfnt;
	static FontMetrics fm;
	static FontMetrics tfm;
	static Label label = new Label("",Label.CENTER);
        static views.Label txt = new views.Label();
	static {
		Panel p = new Panel();
		dm = p.getToolkit().getScreenSize();
		ButOk = new Button(GLOBAL.c2b(
			GLOBAL.pr(GLOBAL.MSG_OKBUTTON,"Ok"),GLOBAL.BUTTON));
		Button ButCancel = new Button(GLOBAL.c2b(
			GLOBAL.pr(GLOBAL.MSG_CANCELBUTTON,"Cancel"),GLOBAL.BUTTON));
		tfnt = GLOBAL.font(GLOBAL.FONT_MSG_TEXT);
		fn = GLOBAL.font(GLOBAL.FONT_MSG_BTN);
	}

	public DMS(String mesg,boolean cancel,boolean error,Frame fr) 
		throws CancelSureException{
		super(fr,"",true);

		ButOk.setFont(fn);
		ButCancel.setFont(fn);
		label.setFont(tfnt);
		fm = getFontMetrics(fn);
		tfm = getFontMetrics(tfnt);
		
		// set message title .... it's realy need ???
		if ( error ) title =
						 GLOBAL.c2b(GLOBAL.pr(GLOBAL.TITLE_ERROR,"error"),
									GLOBAL.DTITLE);
	    else if( cancel ) title =
							  GLOBAL.c2b(
								  GLOBAL.pr(GLOBAL.TITLE_SURE,
											"U R sure ?"),GLOBAL.DTITLE);
		else title = GLOBAL.c2b(GLOBAL.pr(
			GLOBAL.TITLE_MESSAG,"message"),GLOBAL.DTITLE);

		setTitle(title);
		this.cancel = cancel;

		// set colors !
		String[] p_color;
		if ( error ) {
			p_color =
				GLOBAL.parse(6,GLOBAL.pr(GLOBAL.CLR_MSG_ERROR),
							 GLOBAL.DEFAULTCOLOR);
		}else if ( cancel ) {
			p_color =
				GLOBAL.parse(6,GLOBAL.pr(GLOBAL.CLR_MSG_QUEST),
							 GLOBAL.DEFAULTCOLOR);
		}else{
			p_color =
				GLOBAL.parse(6,GLOBAL.pr(GLOBAL.CLR_MSG_INFO),
							 GLOBAL.DEFAULTCOLOR);
		}

		setBackground(GLOBAL.color(p_color[GLOBAL.TEXTBG]));
		setForeground(GLOBAL.color(p_color[GLOBAL.TEXTFG]));
		ButOk.setBackground(GLOBAL.color(p_color[GLOBAL.BTNBG]));
		ButOk.setForeground(GLOBAL.color(p_color[GLOBAL.BTNFG]));
		ButCancel.setBackground(GLOBAL.color(p_color[GLOBAL.BTNBG]));
		ButCancel.setForeground(GLOBAL.color(p_color[GLOBAL.BTNFG]));

		// set size !
		setResizable(false);
                //int width = tfm.stringWidth(mesg)+40;
		int wb = fm.stringWidth(ButOk.getLabel()) +
			fm.stringWidth(ButCancel.getLabel())+50;
                //width = wb>width?wb:width;
                setSize(300,200);
                move((dm.width-300)/2,(dm.height-200)/3);
   		setLayout(new BorderLayout());
		Panel p = new Panel();
		add(p);
		p.setLayout(new BorderLayout());
                //label.setText(mesg);
                //p.add("Center",label);
                //txt.setEditable(false);
                txt.setFocusable(false);
                
                txt.setValignment("CENTER");
                txt.setHalignment("CENTER");
                txt.setMultiLine(true);
                txt.setWordWrap(true);
                txt.setBounds(10, 10, 100, 100);
                
                txt.setBackground(GLOBAL.color(p_color[GLOBAL.TEXTBG]));
                txt.setForeground(GLOBAL.color(p_color[GLOBAL.TEXTFG]));
                txt.setFont(fn);
                p.add(txt);
		Panel bp = new Panel();
		p.add("South",bp);
		bp.setLayout(new FlowLayout(FlowLayout.CENTER));
		bp.add(ButOk);
		if (cancel) bp.add(ButCancel);
		txt.setFm(fm);
		txt.setFont(fn);
		txt.setValue(mesg);
                ButOk.requestFocus();

		try{
			show();
		}catch(Exception e){
			e.printStackTrace();
		}
		dispose();
		if (!ok) throw new CancelSureException();
	}
	public boolean handleEvent(Event ev){
		if (ev.id == Event.ACTION_EVENT){
			if (ev.target == ButOk) {
				ok = true;
			}else if (ev.target == ButCancel) {				
				ok = false;
			}else return super.handleEvent(ev);
			hide();
			return true;
		}else return super.handleEvent(ev);
	}
}


