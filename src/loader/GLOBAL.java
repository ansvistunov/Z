
/*
 * File: GLOBAL.java
 *
 * Created: Mon Mar 22 17:44:49 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;

import java.util.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import sun.io.*;

class waitwin_inspector extends Thread{
	static waitwin_inspector wi = null;
	long time;
	public waitwin_inspector(long time){
		this.time = time;
		if (wi != null) wi.interrupt();
		wi = this;
		start();
	}
	public static void goOut(long time){
		if (wi != null) wi.interrupt();
		new waitwin_inspector(time);
	}
	public void run(){
		try{
			sleep(time);
			GLOBAL.waitwin.setVisible(false);
		}catch(InterruptedException e){
			//e.printStackTrace();
		}
	}
}

public class GLOBAL extends GLOBALPROP{
	static Window waitwin;
	static boolean waitwinflag;
	static Label waitwin_label;
	static String defaultlabel;
	public static Frame nafigator;
	//public static curd;


	public static String[] parse(int num,String s,String d){
		//System.out.println("~loader.GLOBAL::parse args "+num+
		//				   ",{"+s+"},{"+d+"}");
		if (s == null) return parse(num,d,null);
		StringTokenizer st = new StringTokenizer(s,",");
		if (st.countTokens() < num) return parse(num,d,null);
		String[] r = new String[num];
		for (int i=0; i<num; ++i){
			try{
				r[i] = st.nextToken().trim();
			}catch(Exception e){
				System.out.println(
					GLOBAL.PRINT_RED+
					"~loader.GLOBAL::parse "+e+
					GLOBAL.PRINT_NORMAL);
				return parse(num,d,null);
			}
		}
		return r;
	}


//################################################################################
/*
 *  color manipulation
 */

	public static String[] parseColor(String s){
		//System.out.println("parse color "+s);
		return parse(6,s,DEFAULTCOLOR);
	}

	public static Color color(String s){
		//System.out.println("convert color "+s);
		s = s.toLowerCase().trim();
		if (s.compareTo("gray")==0){
			return Color.gray;
		}else if(s.compareTo("lightgray")==0){
			return Color.lightGray;
		}else if(s.compareTo("darkgray")==0){
			return Color.darkGray;
		}else if(s.compareTo("white")==0){
			return Color.white;
		}else if(s.compareTo("black")==0){
			return Color.black;
		}else if(s.compareTo("red")==0){
			return Color.red;
		}else if(s.compareTo("green")==0){
			return Color.green;
		}else if(s.compareTo("blue")==0){
			return Color.blue;
		}else if(s.compareTo("yellow")==0){
			return Color.yellow;
		}else if(s.compareTo("cyan")==0){
			return Color.cyan;
		}else if(s.compareTo("magenta")==0){
			return Color.magenta;
		}else if(s.compareTo("pink")==0){
			return Color.pink;
		}else if((s.charAt(0)=='#')||(s.charAt(0)=='$')){
			try{
				if (s.length()==4){
					return new Color(
						fromhex(s.substring(1,2)),
						fromhex(s.substring(2,3)),
						fromhex(s.substring(3,4)));
				}else if (s.length()==7){
					return new Color(
						fromhex(s.substring(1,3)),
						fromhex(s.substring(3,5)),
						fromhex(s.substring(5,7)));
				}else return Color.gray;
			}catch(Exception e){
				return Color.gray;
			}
		}else return Color.gray;
	}

	static int fromhex(String s){
		if ( s.length() == 1 ){
			switch(s.charAt(0)){
			case 'f': return 15;
			case 'e': return 14;
			case 'd': return 13;
			case 'c': return 12;
			case 'b': return 11;
			case 'a': return 10;
			case '9': return 9;
			case '8': return 8;
			case '7': return 7;
			case '6': return 6;
			case '5': return 5;
			case '4': return 4;
			case '3': return 3;
			case '2': return 2;
			case '1': return 1;
			}
			return 0;
		}else{
			return fromhex(s.substring(0,1))*16+fromhex(s.substring(1));
		}
	}

	public static Color color(String s,int type){
		String[] p_color = parse(6,prop.getProperty(s),DEFAULTCOLOR);
		return color(p_color[type]);
	}

	public static Color[] colors(String s){
		//System.out.println("make colors "+s);
		String[] p_color = parse(6,s,DEFAULTCOLOR);
		Color[] cl = new Color[6];
		for (int i=0; i<6; ++i) cl[i]=color(p_color[i]);
		return cl;
	}

//################################################################################
/*
 * font manipulation
 */

	public static int fontFam(String s){
		s = s.toUpperCase();
		if (s.compareTo("BOLD")==0){
			return Font.BOLD;
		}else if(s.compareTo("ITALIC")==0){
			return Font.ITALIC;
		}else if(s.compareTo("BOLD|ITALIC")==0){
			return Font.BOLD|Font.ITALIC;
		}else if(s.compareTo("ITALIC|BOLD")==0){
			return Font.BOLD|Font.ITALIC;
		}else if(s.compareTo("PLAIN")==0){
			return Font.PLAIN;
		}else return Font.BOLD;
	}

	public static int fam(String s){
		String[] fn = parse(3,prop.getProperty(s),DEFAULTFONT);
		return fontFam(fn[1]);
	}

	public static String face(String s){
		String[] fn = parse(3,prop.getProperty(s),DEFAULTFONT);
		return fn[0];
	}

	public static int size(String s){
		String[] fn = parse(3,prop.getProperty(s),DEFAULTFONT);
		int r = Integer.valueOf(fn[1]).intValue();
		if (r<1) return 14;
		return r;
	}

	public static Font font(String s){
		String[] p_font = parse(3,prop.getProperty(s),DEFAULTFONT);
		return new Font(p_font[0],fontFam(p_font[1]),
						 Integer.valueOf(p_font[2]).intValue());
	}

	public static Font font1(String s){
		String[] p_font = parse(3,s,DEFAULTFONT);
		return new Font(p_font[0],fontFam(p_font[1]),
						 Integer.valueOf(p_font[2]).intValue());
	}



//################################################################################
/*
 * properties manipulation
 */

	public static String pr(String s){
		//System.out.println("~loader.GLOBAL::pr  "+s+"="+prop.getProperty(s));
		return prop.getProperty(s);
	}

	public static String pr(String s,String def){
		//System.out.println("~loader.GLOBAL::pr  "+s+"="+prop.getProperty(s));
		String foo =  prop.getProperty(s);
		//System.out.println("value is "+foo);
		if ( (foo == null) || (foo.compareTo("") == 0) ) return def;
		else return foo;
	}


//################################################################################
/*
 * dialog manipulation
 */

	public static void messag(String messag,boolean error){
		//Exception e = new Exception();
		//e.printStackTrace();

		messag(messag,error,nafigator);
	}

	public static boolean sure(String messag,boolean error){
		//Exception e = new Exception();
		//e.printStackTrace();

		return sure(messag,error,nafigator);
	}

	public static void messag(String messag,boolean error,Frame fr){
		try{
			new DMS(messag,false,error,fr);
		}catch(Exception e){
		}
	}

	public static boolean sure(String messag,boolean error,Frame fr){
		try{
			new DMS(messag,true,error,fr);
			return true;
		}catch(CancelSureException e){
			return false;
		}
	}


//################################################################################
/*
 * encoding tools
 */

	public static String c2b(String s){
		try{
			String r;
			if (ctb == null)
				return s;
			else{
				char[] c = new char[s.length()];
				s.getChars(0,c.length,c,0);
				
				//byte[] b = ctb.convertAll(c);
				
				ByteBuffer bb = ctb.encode(CharBuffer.wrap(c)); //add
				
				r = new String(bb.array(),0,0,bb.capacity()); //add
			}
			//System.out.println(
			//"~loader.GLOBAL::c2b \n\tfrom "+s+"\n\tto "+r);
			return r;
		}catch(Exception e){
			e.printStackTrace();
			return s;
		}
	}

	public static String c2b(String s,String f){
		//System.out.println("encoding "+s+" in class "+f);
		if (ctb == null) return s;
		if (pr(f,"OFF").toUpperCase().compareTo("ON")==0)
			return c2b(s);
		else return s;

	}

	public static String b2c(String s,String f){
		if (btc == null) return s;
		if (pr(f,"OFF").toUpperCase().compareTo("ON")==0)
			return b2c(s);
		else return s;

	}

	public static String b2c(String s){
		try{
			String r;
			if (btc == null)
				return s;
			else{
				byte[] b = new byte[s.length()];
				s.getBytes(0,b.length,b,0);
				//char[] c = btc.convertAll(b);
				
				CharBuffer cb = btc.decode(ByteBuffer.wrap(b));
				
				r = new String(cb.array());
			}
			//System.out.println("~loader.GLOBAL::b2c\n\tfrom "+s+"\n\tto "+r);
			return r;
		}catch(Exception e){
			e.printStackTrace();
			return s;
		}
	}

	public static void setencoding(){

		String encoding = pr(AWT_LOCALE,null);
		if (encoding == null){
			btc = null;
			ctb = null;
			return;
		}
		try{
			//btc = ByteToCharConverter.getConverter(encoding);
			btc = Charset.forName(encoding).newDecoder(); //add
			
			//ctb = CharToByteConverter.getConverter(encoding);
			ctb = Charset.forName(encoding).newEncoder(); //add
			
			
			System.out.println("encoding="+encoding);
			
			
			
			//System.out.println("btc encoding "
			//+btc.getCharacterEncoding()+"\n"+
			//"ctb encoding "+ctb.getCharacterEncoding());
		}catch(Exception e){
			e.printStackTrace();
			btc = null;
			ctb = null;
		}
	}



//################################################################################
/*
 *  debuging initialization
 */


	public static void setdebuging(){

		try{
			loader_debug = Integer.valueOf(pr(DEBUG_LOADER,"0")).intValue();
		}catch(Exception e){}
		try{
			protocol_debug =
				Integer.valueOf(pr(DEBUG_PROTOCOL,"0")).intValue();
		}catch(Exception e){}
		try{
			rml_debug = Integer.valueOf(pr(DEBUG_RML,"0")).intValue();
		}catch(Exception e){}
		try{
			views_debug = Integer.valueOf(pr(DEBUG_VIEWS,"0")).intValue();
		}catch(Exception e){}
		try{
			dstore_debug = Integer.valueOf(pr(DEBUG_DSTORE,"0")).intValue();
		}catch(Exception e){}
		try{
			parser_debug = Integer.valueOf(pr(DEBUG_PARSER,"0")).intValue();
		}catch(Exception e){}
		try{
			calc_debug = Integer.valueOf(pr(DEBUG_CALCULATOR,"0")).intValue();
		}catch(Exception e){}

		try{
			loader_exception =
				(pr(EXCEPTION_LOADER,"OFF").toUpperCase().compareTo("ON") == 0);
		}catch(Exception e){}

		if (pr(DEBUG_COLOR,"OFF").toUpperCase().equals("OFF") |
			pr(SYSTEM_OUT,"DEFAULT").toUpperCase().equals("WINDOW")){
			PRINT_RED = "";
			PRINT_NORMAL = "";
			PRINT_LIGHT = "";
			PRINT_GREEN = "";
			PRINT_BLUE = "";
			PRINT_YELLOW = "";
			PRINT_WHITE = "";
			PRINT_BLACK = "";
		}
	}



//################################################################################
/*
 * loading information
 */

	static int waitcounter = 0;
	public static void waitin(){
		if(waitwinflag){
			waitwin_text("");
		}
	}
	public static void waitout(){
		if (waitwinflag){
			new waitwin_inspector(500);
		}

	}
	public static void addmessage(String s){
		waitwin_text("from RDBMS "+s+"  lines");
	}
	public static void waitwin_text(String s){
		try{			document.StatusPanel p = ((body.Nafigator)nafigator).curd.getStatusPanel();			//System.out.println("+++ status panel = "+p+";s="+s);
			if (p!=null) {				p.setText(s);				return;
			}		}catch(Exception e) {}
		if (waitwinflag){
			try{
				Rectangle r = null;
				String text = defaultlabel+"  "+s;
				Font fn = waitwin.getFont();
				FontMetrics fm = waitwin.getFontMetrics(fn);
				int h = fm.getHeight();
				waitwin_inspector.goOut(60000);
				if (!waitwin.isVisible()) {
					waitwin.setVisible(true);
				}
				waitwin.setSize(fm.stringWidth(text)+20,h+10);
				r = waitwin.getBounds();
				Rectangle r1 = new Rectangle(0,0,fm.stringWidth(text)+20,h+10);
				Graphics g = waitwin.getGraphics();
				r = (r.width>r1.width)?r:r1;
				g.setClip((Shape)r);
				g.setColor(waitwin.getBackground());
				g.fillRect(0,0,r.width,r.height);
				g.setColor(waitwin.getForeground());
				g.drawString(text,0,h+5);
				waitwin.toFront();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


	static {

		try{
			
			
			
			
			//LFIS fs = new LFIS(GLOBAL.PROPNAME);
			//
			Properties pr = new Properties();
			
			
			FileInputStream fr = new FileInputStream(GLOBAL.PROPNAME);
			pr.load(fr);
			fr.close();
			
			String propname_encoding = pr.getProperty(GLOBAL.PROP_ENCODING, "UTF-8");
			String out_encoding = pr.getProperty(GLOBAL.OUT_ENCODING, "UTF-8");
			
			System.setOut(new java.io.PrintStream(System.out, true, out_encoding));
			
			Charset cs = Charset.forName(propname_encoding);
			fr = new FileInputStream(GLOBAL.PROPNAME); //TO DO потом заменить - не нужно читать два раза
			InputStreamReader isr = new InputStreamReader(fr,cs);
			
			pr = new Properties();
			
			
			pr.load(isr);
			GLOBAL.prop = pr;
			isr.close();

			GLOBAL.setencoding();
			GLOBAL.setdebuging();

			GLOBAL.loader = new Loader(GLOBAL.pr(GLOBAL.DOC_SERVER,""));
			Loader cloader = new Loader(GLOBAL.pr(GLOBAL.CLASS_SERVER,""));
			GLOBAL.cl = new CLoader(cloader);
			((CLoader)GLOBAL.cl).dump_state();

		}catch(Exception e){
			System.out.println(GLOBAL.PRINT_RED+
							   "~loader.Boot::constructor \n"+
							   GLOBAL.PRINT_NORMAL+
					  "\tBroken with init properties of curent system");
			//System.out.println("~loader.Boot::constructor  exception"+e);
			System.exit(0);
		}


		Frame fr = new Frame();
		waitwin_label = new Label(
			(defaultlabel=" "+pr(LOADING_STRING,"LOADING")),Label.LEFT);
		waitwinflag = (pr(LOADING_WINDOW,"OFF").trim().toUpperCase().equals("ON"));
		if ( waitwinflag) {
			waitwin = new Window(fr);
			waitwin.setBackground(
				color(pr(LOADING_BACKGROUND,"RED").trim()));
			waitwin.setForeground(
				color(pr(LOADING_FOREGROUND,"WHITE").trim()));
			waitwin.setLocation(0,0);
			waitwin.setLayout(new BorderLayout());
			Font fn = font(LOADING_FONT);
			waitwin.setFont(fn);
			//waitwin.add("Center",waitwin_label);
			//waitwin.setSize(100,50);
			FontMetrics fm = waitwin.getFontMetrics(fn);
			waitwin.setSize(fm.stringWidth(waitwin_label.getText())+20,fm.getHeight()+10);
		}
	}


}




