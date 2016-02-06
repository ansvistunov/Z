
/*
 * File: TreeView.java
 *
 * Created: Thu Apr  8 11:57:00 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package views;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import rml.Proper;
import loader.GLOBAL;
import rml.PROPS;
import document.ACTION;
import dbi.Handler;
import document.*;
import calc.*;

public class TreeView extends Panel implements Retrieveable,
Handler,ActionListener,NotifyInterface{
	static final String NODEFONT="NODEFONT";
	static final String POINTFONT="POINTFONT";
	static final String HILITE="HILITE";
	static final String NODECOLOR="NODE";
	static final String POINTCOLOR="POINT";
	static final String ROOTNAME="ROOTNAME";
	static final String HILITING="HILITING";
	static final String ACTION="ACTION";
	static final String NODEACTION="NODEACTION";
	static final String NODEHANDLER="NODEHANDLER";
//	static final String 
	
	V3n v3 = null;
	Object nafigator = null;
	String VNfnt = "";
	String VPfnt = "";
	Color[] colors = new Color[6];
	String root = "root";
	String action = null;
	String nodeaction = null;
	String nodehandler = null;
	boolean hilit = true;
	Hashtable aliases = null;
	PopupMenu popupMenu = null;
	
	class V3n extends V3{
		Object nafigator;

		int level(String path) throws Exception{
			//System.out.println(path);
			if (path.equals("/")){
				//System.out.println("path is /");
				return 0;
			}else while(path.startsWith("//")){path=path.substring(1);}
			int i = 0; int l = 0;
			while((i=path.indexOf('/'))!=-1)
				{++l;path=path.substring(i+1);}
			return l;
		}
		
		public V3n(Object nafigator) throws Exception{
			super(GLOBAL.font1(((TreeView)nafigator).VPfnt),
				  GLOBAL.font1(((TreeView)nafigator).VNfnt),
				  // bg,fg,selectbg,selectfg,node,point
				  ((TreeView)nafigator).colors,
				  ((TreeView)nafigator).root,
				  ((TreeView)nafigator).hilit,
				  nafigator);
		}

		void createPopupMenu(int x,int y){
			TreeView.this.createPopupMenu(x,y);
		}
		
		public void loadPoint(String path,String name,Object xarg){
			//System.out.println(path);
			Object arg  = ((TreeView)xarg).nafigator;
			try{
				if ( arg instanceof dbi.GroupReport ) {
					int row =
						Integer.valueOf(
							path.substring(path.lastIndexOf("/")+1)).intValue();
					((dbi.GroupReport)arg).setCurRow(row);
				}else{
					((NafigatorInterface)arg).setCurPath(path);
				}
				ARGV argv = new ARGV();
				argv.setValueByName("PATH",path);
				argv.setValueByName("LEVEL",new Double(level(path)));
				if (((TreeView)xarg).aliases!=null)
					((TreeView)xarg).aliases.put("_ARG_",argv);
				document.ACTION.doAction(((TreeView)xarg).action,
										 ((TreeView)xarg).aliases,null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public void loadNode(String path,String name,Object xarg){
			//System.out.println(path);
			Object arg  = ((TreeView)xarg).nafigator;
			try{
				if ( arg instanceof dbi.GroupReport ) {
					int row = 0;
					if (!path.equals("/")) {
						String s = path.substring(path.lastIndexOf("/")+1);
						String ss = s.substring(s.lastIndexOf("#")+1);
						row =
							Integer.valueOf(ss).intValue();
					}
					((dbi.GroupReport)arg).setCurRow(row);
				}else{
					((NafigatorInterface)arg).setCurPath(path);
				}
				//System.out.println("node action == "+((TreeView)xarg).nodeaction);
				ARGV argv = new ARGV();
				argv.setValueByName("PATH",path);
				//System.out.println(path+" "+argv);
				//System.out.println(level(path));
				argv.setValueByName("LEVEL",new Double(level(path)));
				if (((TreeView)xarg).aliases!=null)
					((TreeView)xarg).aliases.put("_ARG_",argv);
				document.ACTION.doAction(((TreeView)xarg).nodeaction,
										 ((TreeView)xarg).aliases,null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public void nodeHandler(String path,String name,Object xarg){
			//System.out.println(path);
			Object arg  = ((TreeView)xarg).nafigator;
			try{
				if ( arg instanceof dbi.GroupReport ) {
					String s = path.substring(path.lastIndexOf("/")+1);
					String ss = s.substring(s.lastIndexOf("#")+1);
					int row =
						Integer.valueOf(ss).intValue();
					((dbi.GroupReport)arg).setCurRow(row);
				}else{
					((NafigatorInterface)arg).setCurPath(path);
				}
				ARGV argv = new ARGV();
				argv.setValueByName("PATH",path);
				argv.setValueByName("LEVEL",new Double(level(path)));
				if (((TreeView)xarg).aliases!=null)
					((TreeView)xarg).aliases.put("_ARG_",argv);
				document.ACTION.doAction(((TreeView)xarg).nodehandler,
										 ((TreeView)xarg).aliases,null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public char[] loadData(String path,boolean enc,Object xarg) throws Exception{
			Object arg  = ((TreeView)xarg).nafigator;
			try{
				String s="";
				if ( arg instanceof dbi.GroupReport ) {
					s = "0"+path;
					s = ((dbi.GroupReport)arg).getNode(s);
				}else{
					//((NafigatorInterface)arg).setCurPath(path);
					s = ((NafigatorInterface)arg).loadDate(path);
				}
				return s.toCharArray();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
	}

	public TreeView(Proper prop,Hashtable aliases,Object[] objs)
		throws Exception{

		this.aliases = aliases;
		
		for(int i=0;i<objs.length;++i ){
			if ( (objs[i] instanceof dbi.GroupReport) ||
				 (objs[i] instanceof NafigatorInterface) ){
				nafigator = objs[i];
			}else if (objs[i] instanceof views.Menu){
                    popupMenu = new PopupMenu();
                    java.awt.Menu m = ((views.Menu)objs[i]).getMenu();
                    if (m==null) {
                        //System.out.println("~views.Grid::addChildren:popmenu=null");
                        return;
                    }
                    int ic = m.getItemCount();
                    for (int j=0;j<ic;j++) {
                        MenuItem mi = m.getItem(0);                        
                        if (mi==null) continue;
                        mi.addActionListener(this);
                        popupMenu.add(mi);
                    }
                    add(popupMenu);                    
			}else throw new
				Exception("~vews.TreeView::<init> Data Object Must be GroupReport or NafigatorInterface or Menu, but it is "+objs[0]);
		}
		VNfnt = (String)prop.get(NODEFONT,
							  GLOBAL.pr(GLOBAL.FONT_TREEVIEW_NODE,GLOBAL.DEFAULTFONT));
		VPfnt = (String)prop.get(POINTFONT,
							  GLOBAL.pr(GLOBAL.FONT_TREEVIEW_POINT,GLOBAL.DEFAULTFONT));
		// bg,fg,selectbg,selectfg,node,point
		String[] p = GLOBAL.parse(6,GLOBAL.pr(GLOBAL.CLR_TREEVIEW),
						 "lightgray,black,gray,white,black,black");
		colors[0] = GLOBAL.color((String)prop.get(PROPS.BACKGROUND,p[0]));
		//System.out.println((String)prop.get(PROPS.BACKGROUND,p[0])+"  "+colors[0]);
		colors[1] = GLOBAL.color((String)prop.get(PROPS.FOREGROUND,p[1]));
		String s = (String)prop.get(HILITE);
		String[] ss  = GLOBAL.parse(2,s,p[2]+","+p[3]);
		colors[2] = GLOBAL.color(ss[0]);
		colors[3] = GLOBAL.color(ss[1]);
		colors[4] = GLOBAL.color((String)prop.get(NODECOLOR,p[4]));
		colors[5] = GLOBAL.color((String)prop.get(POINTCOLOR,p[5]));
		root = (String)prop.get(ROOTNAME,root);
		hilit = (((String)prop.get(HILITING,"ON")).toUpperCase().trim().compareTo("ON")==0);
		action = (String)prop.get(ACTION,"");
		nodeaction = (String)prop.get(NODEACTION,"");
		nodehandler = (String)prop.get(NODEHANDLER,"");

		//System.out.println("nodeaction= "+nodeaction);

		if ( nafigator instanceof  dbi.GroupReport )
			((dbi.GroupReport)nafigator).addHandler(this);
		else ((NafigatorInterface)nafigator).addHandler(this);

	}

	public void retrieve(){
		//System.out.println("~views.TreeView::retrieve retrive "+this);
		if (nafigator instanceof NafigatorInterface) 
			try{
				//System.out.println("~views.TreeView::retrieve as NafigatorInterface");
				((NafigatorInterface)nafigator).retrieve();
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		else //( nafigator instanceof dbi.GroupReport )
                    try{
			((dbi.GroupReport)nafigator).retrieve();
                        }catch(Exception e){}

		if (v3 == null){
			try{
				v3 = new V3n(this);
				setLayout(new GridLayout(1,1));
				add(v3);
				validate();
			}catch(Exception e){
			}
		}else{
			v3.reloadAllNodes();
		}

		//System.out.println("~views.TreeView::retrieve retrive V3"+v3);
		
	}

	public void update(){
	}

	public void fromDS(){
		v3.reloadAllNodes();
	}
	
	public void toDS(){
	}
	
	public void notifyHandler(Object o){
		//System.out.println("!!!!!!!notifyed!!!!!!!");
		//System.out.println(v3);
		if ( v3 != null )
			v3.reloadAllNodes();
	}

	void createPopupMenu(int x,int y) {
		calc.ARGV argv = new calc.ARGV();
		Object s = v3.getcurentpath();
		if ( s instanceof String)
			if ( ((String) s).startsWith("//") )
				 s = ((String) s).substring(1);
		try{
			argv.setValueByName("PATH",s);
		}catch(Exception e){
		}
		aliases.put("_ARG_",argv);
        if (popupMenu!=null)
            popupMenu.show(this,x,y);
	}

	public void openNode(String path){
		v3.openNode(path) ;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		//System.out.println("Action performed:"+command);
		try {
			document.ACTION.doAction(command,aliases,this);
		}
		catch(Exception ex) {
			System.out.println("exception inside document.ACTION:doAction : "+e);
		}
	}
	public void notifyIt(){
		aliases.remove("_ARG_");
	}
}
