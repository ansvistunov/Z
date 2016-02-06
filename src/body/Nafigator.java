
/*
 * File: Nafigator.java
 *
 * Created: Wed Mar 31 15:20:31 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 * Author: Alexey Chen
 *
 */

package body;

import loader.*;
import java.awt.*;
//import java.awt.im.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import document.*;
import views.V3;

/**
 * class Nafigator for Nafigation on Document server
 *
 */
public class Nafigator extends Frame{

    NavigatorController nc=null;

    public Document curd = null;

	String startDocument = null;
	class V3n extends V3{
		public V3n() throws Exception{
			super(GLOBAL.font(GLOBAL.FONT_TREEVIEW_POINT),
				  GLOBAL.font(GLOBAL.FONT_TREEVIEW_NODE),
				  GLOBAL.colors(GLOBAL.pr(GLOBAL.CLR_TREEVIEW,GLOBAL.DEFAULTCOLOR)),
				  GLOBAL.pr(GLOBAL.NAFIGATOR_HEADER,"root"),
				  GLOBAL.pr(
					  GLOBAL.HILITING_TREEVIEW,
					  "OFF").toUpperCase().compareTo("ON")== 0,
				  null);
		}

		public void loadPoint(String path,String name,Object arg){
			Document.resetIt = false;
			loadDocument(path,name);
		}

		public void nodeHandler(String path,String name,Object arg){
			reloadAllNodes();
		}

		public char[] loadData(String path,boolean enc,Object arg) throws Exception{
			return GLOBAL.loader.loadByName_chars(path+"/.list",enc);
		}
	}

	Panel pn = new Panel();
	boolean documented = false;

	void loadDocument(String doc,String name){
		try{
			documented = true;
			Panel p = new Panel();
			p.setLayout(new GridLayout(1,1));
			Document d;
			try{
				d = Document.loadDocument(doc,null,GLOBAL.loader);
				//d.setNafigator(this);
			}catch(Exception e){
				//GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_CANTLOADDOCUMENT,
				//						"Can't load Document"),true);
				//e.printStackTrace();
				throw e;
			}
			d.callDocument(this,mainp);
		}catch(Exception e){
			documented = false;
		}finally{
		}

	}

	/*public boolean handleEvent(Event ev){
		if((ev.target == this)&&(ev.id == Event.WINDOW_DESTROY)){
			if ( !documented ){
				//
				nc.removeWorkspace(this);
				//
				return true;
			}else{
				Document.curd.close();
				return true;
			}
		}else return super.handleEvent(ev);
	}*/



	Panel mainp;
/**
 *
 
    public InputContext getInputContext() {
        return null;
    }
 */
	public Nafigator() throws Exception{
		super(GLOBAL.c2b(
			GLOBAL.pr(GLOBAL.TITLE_NAFIGATOR,"Nafigator"),GLOBAL.FTITLE));
		//
		setMenuBar(getMB());
		addFocusListener(new FL());
		addWindowListener(new WL());
                try {
                  setIconImage(Toolkit.getDefaultToolkit().getImage("icon.jpg"));
                } catch (Exception e) {
                }
		//System.out.println("After setMenuBar");
		//
                //enableInputMethods(false);
		int width = 600;
		int height = 400;
		int x = -1;
		int y = -1;
		try{
			width = Integer.valueOf(
				GLOBAL.pr(GLOBAL.NAFIGATOR_WIDTH,"400")).intValue();
		}catch(Exception e){
		}
		try{
			height = Integer.valueOf(
				GLOBAL.pr(GLOBAL.NAFIGATOR_HEIGHT,"400")).intValue();
		}catch(Exception e){
		}
		try{
			x = Integer.valueOf(
				GLOBAL.pr(GLOBAL.NAFIGATOR_X,"@")).intValue();
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			y = Integer.valueOf(
				GLOBAL.pr(GLOBAL.NAFIGATOR_Y,"@")).intValue();
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			startDocument =
				GLOBAL.pr(GLOBAL.DOC_START,null);
		}catch(Exception e){
		}

		try{
			GLOBAL.nafigator = this;
			setSize(width,height);
			if ( startDocument == null ) {
				mainp = new V3n();
			}else{
				mainp = new Panel();
				loadDocument(startDocument,"");
				mainp = curd.getPanel();
			}
			setLayout(new GridLayout(1,1));
			add(mainp);
			Dimension dm = getToolkit().getScreenSize();
			if ((x==-1)&&(y ==-1))
				setLocation((dm.width-width)/2,(dm.height-height)/2);
			else setLocation(x,y);
			validate();
			setVisible(true);
		}catch(Exception e){
			throw e;
		}
	}

	public Nafigator(NavigatorController nc) throws Exception{
	    this();
	    this.nc = nc;
	}

	public Component add(Component c){
		documented = (c != mainp);
		if (documented || (startDocument!=null)) {
			setTitle(GLOBAL.c2b(
				GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW,"MainWindow"),GLOBAL.FTITLE));
		}else{
			setTitle(GLOBAL.c2b(
				GLOBAL.pr(GLOBAL.TITLE_NAFIGATOR,"Nafigator"),GLOBAL.FTITLE));
		}
		return super.add(c);
	}
	void exit(){
		System.exit(0);
	}


    public MenuBar getMB(){
        MenuBar ret = new MenuBar();
        String title="Работа";
        String s1="Создать РабочееПространство";
        String s2="Удалить РабочееПространство";
        String s3="Выйти из Системы";
		String s4="Войти в Систему под другим именем";

        MenuItem i1,i2,i3,i4,sep;
        i1 = new MenuItem(s1);
        i2 = new MenuItem(s2);
        i3 = new MenuItem(s3);
		i4 = new MenuItem(s4);
		sep = new MenuItem("-");
        i1.setActionCommand("1");
        i1.addActionListener(new AL());
        i2.setActionCommand("2");
        i2.addActionListener(new AL());
        i3.setActionCommand("3");
        i3.addActionListener(new AL());
		i4.setActionCommand("4");
        i4.addActionListener(new AL());		
        Menu m = new Menu(title);
        m.add(i1);
        m.add(i2);
		m.add(i4);
		m.add(sep);
        m.add(i3);		ret.add(m);
				title = "Помощь";		m = new Menu(title);
		s1 = "Содержание";
		s2 = "О документе";
		s3 = "О программе";		i1 = new MenuItem(s1);		i2 = new MenuItem(s2);
		i3 = new MenuItem(s3);		i1.setActionCommand("5");		i1.addActionListener(new AL());		i2.setActionCommand("6");		i2.addActionListener(new AL());		i3.setActionCommand("7");		i3.addActionListener(new AL());
        m.add(i1);
		m.add(i2);		m.add(new MenuItem("-"));
		m.add(i3);		ret.add(m);
        return ret;

    }

    class WL extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            if ( !documented ){
				//
				nc.removeWorkspace(Nafigator.this);
				//
				return ;
                        }else{  //path py asw
                                //curd.close();//<--old code
                                curd.processAction(Document.ACT_CANCEL);
				return ;
			}
        }
        public void windowActivated(WindowEvent e) {
            //System.out.println("windowActivated nc="+nc+ " this="+Nafigator.this+" e="+e);
            if(nc!=null) nc.workspaceActivated(Nafigator.this);
        }
    }

    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("1")) {
                //System.out.println("Command 1 choiced");
                nc.newWorkspace();
                return;
            }

            if (e.getActionCommand().equals("2")) {
                //System.out.println("Command 2 choiced");
                nc.removeWorkspace(Nafigator.this);
                return;
            }
            if (e.getActionCommand().equals("3")) {
                nc.exit();
                return;
            }
			if (e.getActionCommand().equals("4")) {
				nc.changeLogon();                
                return;
	        }			if (e.getActionCommand().equals("5")) {
				String browser = GLOBAL.pr(GLOBAL.HELP_BROWSER,"");
				String page = GLOBAL.pr(GLOBAL.HELP_START_PAGE,"");
								if (browser.equals("") || page.equals("")) return;				try{					Runtime.getRuntime().exec(new String[]{browser,page});				}catch(Exception ex){}
                return;
	        }
			if (e.getActionCommand().equals("6")) {
				String browser = GLOBAL.pr(GLOBAL.HELP_BROWSER,"");
				String page = curd.getPage();
				//System.out.println("page="+page);				if (browser.equals("") || page==null || page.equals("")) return;				try{					Runtime.getRuntime().exec(new String[]{browser,page});				}catch(Exception ex){}
                return;
	        }			if (e.getActionCommand().equals("7")) {
				AboutDialog ad = new AboutDialog("О программе",Nafigator.this);
				ad.show();
				return;			}
        }
    }

    class FL extends FocusAdapter {
        public void focusGained(FocusEvent e) {
            if(nc!=null) nc.workspaceActivated(Nafigator.this);
        }
    }
}
