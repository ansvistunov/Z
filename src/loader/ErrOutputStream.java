
/*
 * File: ErrPrintStream.java
 *
 * Created: Fri Jun 25 15:23:54 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package loader;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import calc.*;
import java.util.*;

public class ErrOutputStream extends OutputStream{
	class debugEditor extends Frame implements KeyListener{
		String path = null;
		String region = "";
		Frame fr;
		int curentpage = 0;
		String[] pages = {"","","","","","","","","",""};
		int[] pos = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		TextArea area = new TextArea();
		//Label l = new Label("",Label.CENTER);
		public debugEditor(Frame fr){
			super(GLOBAL.c2b(GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_TITLE,"Editor"),
							 GLOBAL.FTITLE));
			this.fr = this;
			area.setEditable(true);
			area.addKeyListener((KeyListener)this);
			//l.addKeyListener((KeyListener)this);
			//this.addKeyListener((KeyListener)this);
			setLayout(new BorderLayout());
			setBackground(GLOBAL.color(
				GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_BACKGROUND,
						  GLOBAL.pr(GLOBAL.SYSTEM_OUT_BACKGROUND,"#efefef"))));
			setForeground(GLOBAL.color(
				GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_FOREGROUND,
						  GLOBAL.pr(GLOBAL.SYSTEM_OUT_FOREGROUND,"black"))));
			setFont(GLOBAL.font1(
				GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_FONT,
						  GLOBAL.pr(GLOBAL.SYSTEM_OUT_FONT,"Courier,PLAIN,10"))));
			//add("North",l);
			add("Center",area);
			toPage(1);
			try{
				setSize(
					Integer.valueOf(
						GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_WIDTH,"400")).intValue(),
					Integer.valueOf(
						GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_HEIGHT,"500")).intValue());
			}catch(Exception e){}
			try{
				setLocation(
					Integer.valueOf(
						GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_X,"1")).intValue(),
					Integer.valueOf(
						GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_Y,"1")).intValue());
			}catch(Exception e){}
			area.requestFocus();
		}
		public void keyReleased(KeyEvent ev){/*nothing*/}
		public void keyTyped(KeyEvent ev){/*nothing*/}
		public void keyPressed(KeyEvent ev){
			if (ev.isControlDown()){
				switch(ev.getKeyCode()){
				case KeyEvent.VK_0: toPage(0); break;
				case KeyEvent.VK_1: toPage(1); break;
				case KeyEvent.VK_2: toPage(2); break;
				case KeyEvent.VK_3: toPage(3); break;
				case KeyEvent.VK_4: toPage(4); break;
				case KeyEvent.VK_5: toPage(5); break;
				case KeyEvent.VK_6: toPage(6); break;
				case KeyEvent.VK_7: toPage(7); break;
				case KeyEvent.VK_8: toPage(8); break;
				case KeyEvent.VK_9: toPage(9); break;
				case KeyEvent.VK_E: evalCurent(); break;
				case KeyEvent.VK_Q: setVisible(false); break;
				case  KeyEvent.VK_S: saveCurent(); break;
				case  KeyEvent.VK_O: loadCurent(); break;
				case  KeyEvent.VK_B: upBrowser(); break;
				case  KeyEvent.VK_T: lookAllTables(); break;
				case  KeyEvent.VK_C: copyRegion(); break;
				case  KeyEvent.VK_V: pasteRegion(); break;
				case  KeyEvent.VK_X: copyRegion();delRegion(); break;
				case  KeyEvent.VK_R: upDocument(); break;
				}
				ev.consume();
			}
		}

		void upBrowser(){
			savePage();
			String browser =
				GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_BROWSER,null);
			if ( browser != null ) try{
				Hashtable h = new Hashtable();
				Hashtable a = new Hashtable();
				rml.Proper p = null;
				if ( pages[curentpage].trim().equals("") ){
					p = new rml.Proper(0);
				}else {
					try{
						p = rml.Parser.createProper(pages[curentpage].toCharArray(),
													null);
					}catch(Exception ex1){
						GLOBAL.messag("Parsing error: "+ex1.getMessage(),true);
						throw ex1;
					}
				}
				h.put("@@ARG1",p);
				document.ACTION.doAction("opennew "+browser+" &@@ARG1",
										 h,
										 null);
				pages[curentpage] = p.toText();
				loadPage();
			}catch(Exception e){
				//e.printStackTrace();
			}
		}

		void upDocument(){
			savePage();
			document.Document doc = document.Document.getcurd();
			document.Document.setcurd(new document.Document());
			try{
				Hashtable h = new Hashtable();
				rml.Proper p = null;
				try{
						p = rml.Parser.createProper(pages[curentpage].toCharArray(),
												null);
				}catch(Exception ex1){
					GLOBAL.messag("Parsing error: "+ex1.getMessage(),true);
					throw ex1;
				}
				System.out.println(p.toText());
				document.Document.getcurd().aliases = h;
				h.put("@@TARGET",p);
				document.ACTION.doAction("opennew &@@TARGET",
										 h,
										 null);
			}catch(Exception e){
				e.printStackTrace();
			}
			document.Document.setcurd(doc);
		}

		void copyRegion(){
			region = area.getSelectedText();
		}

		void pasteRegion(){
			area.insert(region,area.getCaretPosition());
		}
		void delRegion(){
			//region = area.getSelectedText();
			//area.insert("",area.getCaretPosition());
		}

		void lookAllTables(){
		}

		void loadPage(){
			area.setText(
				GLOBAL.c2b(pages[curentpage]));
			if ( pos[curentpage] > 0) area.setCaretPosition(pos[curentpage]);
		}

		void savePage(){
			pages[curentpage] = GLOBAL.b2c(area.getText());
			pos[curentpage] = area.getCaretPosition();
		}

		void saveCurent(){
			try{
				String file = null;
				FileDialog fd = new FileDialog(fr,"",FileDialog.SAVE);
				if ( path != null ){
					fd.setDirectory(path);
				}
				while(true){
					fd.show();
					path = fd.getDirectory();
					file = fd.getFile();
					if (file == null ) return;
					java.net.URL url = new java.net.URL("file:///"+path);
					loader.FILE fl = new loader.FILE();
					fl.init(url);
					String encoding = "KOI8_R";
					try{
						savePage();
						fl.write(file,encoding,pages[curentpage]);
						System.out.println("Page "+curentpage+
										   "Saved in file "+path+file+" in page "+
										   curentpage);
						break;
					}catch(Exception e){
						GLOBAL.messag("Error write file "+path+file,
							true);
						continue;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
			}
		}

		void loadCurent(){
			try{
				FileDialog fd = new FileDialog(fr,"",FileDialog.LOAD);
				fd.setForeground(GLOBAL.color(
					GLOBAL.pr(GLOBAL.FILEDIALOG_FOREGROUND,
						  GLOBAL.pr(GLOBAL.SYSTEM_OUT_FOREGROUND,"black"))));
				fd.setBackground(GLOBAL.color(
					GLOBAL.pr(GLOBAL.FILEDIALOG_BACKGROUND,
						  GLOBAL.pr(GLOBAL.SYSTEM_OUT_BACKGROUND,"#efefef"))));
				if ( path != null ){
					fd.setDirectory(path);
				}
				String file = null;
				while(true){
					fd.show();
					path = fd.getDirectory();
					file = fd.getFile();
					if ( file == null) return;
					//System.out.println("@@@@:"+file);
					java.net.URL url = new java.net.URL("file:///"+path);
					loader.Protocol proto = new loader.FILE();
					proto.init(url);
					try{
						pages[curentpage] =
							new	String(proto.getByName_chars(file,true));
						loadPage();
						System.out.println("Loaded file "+path+file+" in page "+
										   curentpage);
						break;
					}catch(Exception e){
						GLOBAL.messag("Error read file "+
									  path+file,
									  true);
						continue;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
			}
		}

		void toPage(int i){
			savePage();
			curentpage = i;
			loadPage();
			setTitle(
				GLOBAL.c2b(GLOBAL.pr(GLOBAL.SYSTEM_EDITOR_TITLE,"Editor")+" N "+i,
							 GLOBAL.FTITLE));
		}
		void evalCurent(){
			Hashtable h = new Hashtable();
			Calc c = new Calc(area.getText());
			try{
				System.out.println("---\nresult of debug expression ::\n"+
							   c.eval(h)+
								   "\n----");
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}

	class CircleBuffer{
		String[] circle;
		int curindex = 0;
		int line = 0;
		public CircleBuffer(){
			int size = 200;
			try{
				size = Integer.valueOf(
					GLOBAL.pr(GLOBAL.SYSTEM_OUT_SIZE,"200")).intValue();
			}catch(Exception e){}
			circle = new String[size];
		}
		public synchronized void clear(){
			curindex = 0;
			circle[0] = null;
			circle[1] = null;
		}
		public synchronized void append(String a){
			try{
				circle[curindex] = "<"+(line++)+">\t"+a;
				++curindex;
				if (curindex == circle.length) curindex=0;
				circle[curindex] = null;
			}catch(Exception e){
				GLOBAL.messag("ErrOutputStream::"+e.toString(),true);
			}
		}

		public synchronized String getText(){
			try{
				StringBuffer ret = new StringBuffer(8*1024);
				ret.append("Zeta Project, 1999\n");
				ret.append(
					"Error/Message Screen may buffered "+circle.length+" lines\n\n");
				int x = curindex+1;
				if ( (x >= circle.length) || (circle[x] == null) ) x = 0;
				while ( circle[x] != null ){
					ret.append(circle[x]).append("\n");
					if(++x == circle.length) x=0;
				}
				return ret.toString();
			}catch(Exception e){
				GLOBAL.messag("ErrOutputStream::"+e.toString(),true);
				return "e.toString()";
			}
		}
	}
	class Vis extends Frame implements Runnable,KeyListener{
		TextArea area = new TextArea();
		debugEditor de = new debugEditor(this);
		CircleBuffer buffer;
		long time;
		boolean flushed = false;
		public void keyPressed(KeyEvent ev){/*nothing*/
			if (ev.isControlDown()) {
				if (ev.getKeyCode() == KeyEvent.VK_E){
					de.setVisible(true);
					ev.consume();
				}else if (ev.getKeyCode() == KeyEvent.VK_Z){
					buffer.clear();
					rewrite();
					ev.consume();
				}
			}
		}
		public void keyReleased(KeyEvent ev){/*nothing*/
			//GLOBAL.messag(ev.toString(),true);

		}
		public void keyTyped(KeyEvent ev){
			//GLOBAL.messag(ev.toString(),true);
		}
		public void run(){
			for(;;){
				__rewrite();
				try{
					Thread.sleep(300);
				}catch(InterruptedException e){
					GLOBAL.messag("ErrOutputStream::"+e.toString(),true);
					//e.printStackTrace();
					//break;
				}
			}
		}
		public Vis(CircleBuffer buf){
			super(GLOBAL.c2b(GLOBAL.pr(GLOBAL.SYSTEM_OUT_TITLE,"Error"),GLOBAL.FTITLE));
			buffer = buf;
			area.setEditable(false);
			area.addKeyListener(this);
			setLayout(new BorderLayout());
			setBackground(GLOBAL.color(
				GLOBAL.pr(GLOBAL.SYSTEM_OUT_BACKGROUND,"#efefef")));
			setForeground(GLOBAL.color(
				GLOBAL.pr(GLOBAL.SYSTEM_OUT_FOREGROUND,"black")));
			setFont(GLOBAL.font1(
				GLOBAL.pr(GLOBAL.SYSTEM_OUT_FONT,"Courier,PLAIN,10")));
			add("Center",area);
		}
		public synchronized void rewrite(){
			flushed = false;
		}
		synchronized void __rewrite(){
			if (flushed) return;
			flushed = true;
			String text = buffer.getText();
			area.setText(text);
			area.setCaretPosition(text.length());
		}
	}

	CircleBuffer buffer = new CircleBuffer();
	Vis v = new Vis(buffer);

	public ErrOutputStream(){
		try{
			v.setSize(
				Integer.valueOf(GLOBAL.pr(GLOBAL.SYSTEM_OUT_WIDTH,"400")).intValue(),
			    Integer.valueOf(GLOBAL.pr(GLOBAL.SYSTEM_OUT_HEIGHT,"500")).intValue());
		}catch(Exception e){}
		try{
			v.setLocation(Integer.valueOf(GLOBAL.pr(GLOBAL.SYSTEM_OUT_X,"1")).intValue(),
					 Integer.valueOf(GLOBAL.pr(GLOBAL.SYSTEM_OUT_Y,"1")).intValue());
		}catch(Exception e){}
		v.setVisible(true);
		v.rewrite();
		Thread th = new Thread(new ThreadGroup("ErrorManagers"),v);
		th.setDaemon(true);
		th.start();
	}

	public synchronized void write(int a){
		byte[] buf = new byte[1];
		buf[0] = (byte)a;
		write(buf,1,1);
	}

    StringBuffer sb = new StringBuffer(256);
	public synchronized void write(byte[] buf, int off, int len){
		for (int i = 0; i < len; ++i){
			if ( buf[i+off] == '\n' ){
				buffer.append(sb.toString());
				sb = new StringBuffer(256);
				v.rewrite();
			}else{
				sb.append((char)buf[i]);
			}
		}
	}
}





