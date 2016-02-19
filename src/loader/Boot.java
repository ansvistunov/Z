
/*
 * File: Boot.java
 *
 * Created: Mon Mar 22 10:43:36 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * auto repainted image
 */
class BitMap extends Panel{
	Image img;
	public BitMap(String url){
		img = getToolkit().getImage(url);
	}
	public void paint(Graphics g){
		g.drawImage(img,0,0,this);
	}
	public boolean imageUpdate(Image img,int flags,int x,int y,int w,int h){
        boolean loading = (flags & (ALLBITS|ABORT)) == 0;
        if (!loading) repaint();
        return loading;
    }
}

/**
 * startup class
 */
public class Boot extends Frame{
	static{
		try{
        System.loadLibrary("mydll");
		}catch(UnsatisfiedLinkError e) {
			System.out.println("Can't load library 'mydll'.Paper orientation functions disabled.");
		}
    }

	public static native int setLandscapeOrientation();
	public static native int setPortraitOrientation();
	final public static int NOERROR = 0;
	final public static int CONNECTERROR = 1;
	final public static int AUTHERROR = 2;
	final static int WIDTH = 600;//400;
	final static int HEIGHT = 480;//300;

	boolean showflag = true;

	TextField user = new TextField("",8);
	TextField pass = new TextField("",8);
	Button ok = new Button();

	public Boot(){
		super("");
		//written by And
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		//TimeZone tz = TimeZone.getDefault();
	        //tz.setRawOffset(0);
        	//TimeZone.setDefault(tz);
		//


		BitMap bitmap = new BitMap(GLOBAL.GIFNAME);
		Panel p = new Panel();
		Panel f = new Panel();
		Panel fx = new Panel();

		GLOBAL.DEFAULTCOLOR = GLOBAL.pr(GLOBAL.CLR_DEFAULT,GLOBAL._DEFAULTCOLOR);
		GLOBAL.DEFAULTFONT = GLOBAL.pr(GLOBAL.FONT_DEFAULT,GLOBAL._DEFAULTFONT);

		setFont(GLOBAL.font(GLOBAL.FONT_LOGIN_TEXT));

		Font f_font = GLOBAL.font(GLOBAL.FONT_LOGIN_FIELD);
		user.setFont(f_font);
		pass.setFont(f_font);

		ok.setFont(GLOBAL.font(GLOBAL.FONT_LOGIN_BTN));
		ok.setLabel(GLOBAL.c2b(
			GLOBAL.pr(GLOBAL.LOGIN_OK,"connect to system"),
			GLOBAL.BUTTON));

		setSize(WIDTH,HEIGHT);
		String[] p_color =
			GLOBAL.parse(6,GLOBAL.pr(GLOBAL.CLR_LOGIN),GLOBAL.DEFAULTCOLOR);

		setBackground(GLOBAL.color(p_color[GLOBAL.TEXTBG]));
		setForeground(GLOBAL.color(p_color[GLOBAL.TEXTFG]));
		ok.setBackground(GLOBAL.color(p_color[GLOBAL.BTNBG]));
		ok.setForeground(GLOBAL.color(p_color[GLOBAL.BTNFG]));
		user.setBackground(GLOBAL.color(p_color[GLOBAL.FLDBG]));
		user.setForeground(GLOBAL.color(p_color[GLOBAL.FLDFG]));
		pass.setBackground(GLOBAL.color(p_color[GLOBAL.FLDBG]));
		pass.setForeground(GLOBAL.color(p_color[GLOBAL.FLDFG]));

		setLayout(new BorderLayout());
		setResizable(false);

		pass.setEchoCharacter('*');

		add("Center",p);
		bitmap.setSize(400,210);
		fx.setLayout(new BorderLayout());
		p.setLayout(new BorderLayout());
		p.add("Center",bitmap);
		p.add("South",fx);

		f.setLayout(new FlowLayout(FlowLayout.CENTER));
		f.add(new Label(
			GLOBAL.pr(GLOBAL.LOGIN_LUSER,"User"),
			Label.RIGHT));
		user.setText(GLOBAL.c2b(GLOBAL.pr(GLOBAL.LOGIN_USER),
								GLOBAL.FIELD));
		f.add(user);
		f.add(new Label(GLOBAL.pr(GLOBAL.LOGIN_LPASSWORD,"Password"),
					Label.RIGHT));
		pass.setText(GLOBAL.c2b(GLOBAL.pr(GLOBAL.LOGIN_PASSWORD),
								GLOBAL.FIELD));
		f.add(pass);
		fx.add("Center",f);
		fx.add("South",ok);

		setTitle(GLOBAL.c2b(GLOBAL.pr(GLOBAL.TITLE_LOGIN,"login"),
							GLOBAL.FTITLE));

		Dimension dm = getToolkit().getScreenSize();
		move((dm.width-WIDTH)/2,(dm.height-HEIGHT)/2);

		// start system !
		if ( GLOBAL.pr(GLOBAL.LOGIN_AUTO,"OFF").
			toUpperCase().compareTo("ON") == 0){
			showflag = false;
			handleEvent(new Event(ok,Event.ACTION_EVENT,null));
		}else show();
	}

	public boolean handleEvent(Event ev){
		if ((ev.target == ok)&&(ev.id == Event.ACTION_EVENT)){
			try{
				/*System.out.println(
					"\033[1m~loader.Boot::handleEvent proper is "
					+GLOBAL.prop+"\033[0m");
				*/
				Class cs = GLOBAL.cl.loadClass(
					GLOBAL.pr(GLOBAL.DOC_SOMTHING));

				LoadableClass somthing = (LoadableClass)(cs.newInstance());

				if (!somthing.connect(user.getText(),pass.getText())){
					throw new Exception();
				}

				Thread main_thread = new Thread((Runnable)somthing);
				main_thread.start();

				hide();
				dispose();

				//System.out.println(
				//	"~loader.Boot::constructor  connect success");
			}catch(Exception e){
				e.printStackTrace();
				pass.setText("");
				if (!showflag) show();
				System.out.println(
					"~loader.Boot::constructor  connect failed\n----- "+e);
			}
			return true;
		}else if((ev.target == this)&&(ev.id == Event.WINDOW_DESTROY)){
			hide();
			dispose();
			System.exit(0);
			return true;
		}else return super.handleEvent(ev);
	}

	
	
	
	
	public static void main(String args[]){
		String s =
			GLOBAL.pr(GLOBAL.SYSTEM_OUT,"NULL").trim().toUpperCase();
		//System.out.println(s);
		
		if ( s.equals("WINDOW") ){
			PrintStream ps = new PrintStream((OutputStream)new ErrOutputStream());
			System.setErr(ps);
			System.setOut(ps);
		}else if (s.equals("NULL") ){
			PrintStream ps = new PrintStream((OutputStream)new NullOutputStream());
			System.setErr(ps);
			System.setOut(ps);
		}else if (s.equals("TRACER")){
			try{
				TracingPrintStream tps = new TracingPrintStream(new PrintStream(new File("out")));
				System.setErr(tps);
				System.setOut(tps);
			}catch (Exception e){
				e.printStackTrace();
			}
						//System.setOut(tps);;
		}
		//System.runFinalizersOnExit(true);
		new Boot();
	}
	
	
}






