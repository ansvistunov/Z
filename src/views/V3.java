
/*
 * File: V3.java
 *
 * Created: Wed Apr  7 13:02:21 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */



package views;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import loader.GLOBAL;

/**
 * class Nafigator for V3 - TreeV
 * 
 */
public abstract class V3 extends Panel{	
	static int NodesID = 0;
	static int LEFT_STEP = 30;
	//final static int LEFT_STEP = 10;
	final static int V_OFFSET = 10;
	final static int H_OFFSET = 10;
	final static int BORDER = 5;
	final static int SCROL_BORDER = 15;
	final static int SCROL_STEP = 5;

	final static int LINER = 0;
	final static int DIT = 2;
	final static char VParser_POINT = 'P';
    final static char VParser_NODE = 'N';
	final static int MOUSE_MOVE = 1;
	final static int MOUSE_CLICK = 2;
	static{
	try{
		LEFT_STEP=Integer.parseInt(GLOBAL.pr(GLOBAL.OFFSET_TREEVIEW,"30"));
	}catch(Exception e) {}		
	}
	
	Object arg = null;
	Font VPfn = null;
	FontMetrics VPfm = null;
	Font VNfn = null;
    FontMetrics VNfm = null;
	Color bgColor = null;
	Color fgColor = null;
	Color VNcolor = null;
	Color VPcolor = null;
	Color bgSelectColor = null;
	Color fgSelectColor = null;
    boolean hiliting = false;
	public VP hilited = null;
	int WB = 0;  

/**************************************************/
//  VP
/**************************************************/
/**
 *
 */
	abstract class VP{
		protected Path path;
		protected String name;
		protected int height;
		protected int textwidth;
		protected int textheight;
		protected int width;
		protected Font fn;
		protected FontMetrics fm;
		protected Color color;
		
		public VP(Path path,String name,FontMetrics fm,Font fn,Color color){
			this.path = path;
			this.name = name;
			textwidth = fm.stringWidth(name)+ LEFT_STEP;
			textheight = fm.getHeight()+LINER;
			this.fm = fm;
			this.fn = fn;
			this.color = color;
			setSize();
			//System.out.println("height "+height+", dimension "+d);
		}

		public void setSize() {
			width = textwidth;
			height= textheight;
		};
		
		public Dimension getSize(){
			return new Dimension(width,height);
		}

		public boolean handle(int id,Point p){
			//System.out.println("handle for point "+this);
			VP vp = getObj(p);
			boolean result = false;
			if ( vp == this ) {
				if ( id == MOUSE_MOVE ){
					if (hilited != this){
						hilited = this;
						if (hiliting) return true; else return false;
					}else return false;
				}else result = action();
			}
			else if ( vp == null ) result =  false;
			else result = vp.handle(id,p);
			return result;
		}
		public void paint(Graphics g,int x,int y,int posx,int posy){
			//System.out.println("paint for point "+name+" in "+(x-posx)+","+(y-posy));
			g.setFont(fn);
			if ( hilited == this ){
				g.setColor(bgColor);
				g.fillRect(0,y-posy+DIT,WB,textheight);
				g.setColor(bgSelectColor);
				g.fillRect(x-posx,y-posy+DIT,textwidth,textheight);
				g.setColor(fgSelectColor);
				g.drawString(name,x-posx,y-posy+textheight);
			}else{
				g.setColor(bgColor);
				g.fillRect(0,y-posy+DIT,WB,textheight);
				g.setColor(color);
				g.drawString(name,x-posx,y-posy+textheight);
			}
		}
		public boolean action(){
			//System.out.println("action for point "+name);
			return false;
		}

		public VP getObj(Point p){
			return this;
		}
	}

/**************************************************/
//  VPoint 
/**************************************************/
/**
 *
 */
	class VPoint extends VP{

		public VPoint(Path path,String name,FontMetrics VPfm,
					  Font  VPfn,Color VPcolor){
			super(path,name,VPfm,VPfn,VPcolor);
		}
		public boolean action(){
			loadPoint(path.getpath(),name,arg);
			return false;
		}
	}


/**************************************************/
// VNode
/**************************************************/
/**
 *
 */
	class VNode extends VP{
		VP[] points = null; 
		boolean opened = false;
		int id=0;
		
		public void dump(String s){
			System.out.println(s+this);
			if ( points !=null ){
				for ( int i=0; i< points.length; ++i){
					if (points[i] instanceof VNode)
						((VNode)points[i]).dump(s+"   ");
					else System.out.println(s+"   "+points);
				}
			}
		}
		public String toString(){
			return "NODE "+id+
				"<"+name+"> "+path.getpath()+" open="+opened+" points="+points;
		}

		public VNode(Path path,String name,FontMetrics VNfm,
					  Font  VNfn,Color VNcolor){
			super(path,name,VNfm,VNfn,VNcolor);
			opened = false;
			points = null;
			id = ++NodesID;
		}


		public int openNode(String path,String ls){
			//System.out.println("!!! "+ls);
			VP n = null;
			opened = true;
			if ( path == null ) {
				n = this;
			}else{
				int idx = path.indexOf('/');
				if ( idx!=-1) {
					ls = ls+'/'+path.substring(0,idx); 
					path = path.substring(idx+1);
				}else{
					ls = path;
					path = null;
				}
				int h = 0;
				for(int i=0;i<points.length;++i){
					if (points[i] instanceof VNode){
						//System.out.println("check node "+points[i].path.getpath()
						//				   +" = "+"/"+ls);
						if ( points[i].path.getpath().equals("/"+ls) ){
							System.out.println("in node "+points[i]);
							if ( ((VNode)points[i]).points == null )
								try{
									((VNode)points[i]).action();
									//((VNode)points[i]).load();
								}catch(Exception e){
									e.printStackTrace();
								}
							((VNode)points[i]).opened = true;
							return ((VNode)points[i]).openNode(path,ls)+h;
						}
					}else{
						if ( points[i].path.path.equals(ls) )
							n = points[i];
					}
					h += points[i].height;
				}
			}
			opened = true;
			hilited = n;
			return 0;
		}

		public VP getObj(Point p){
			int y = this.textheight;
			if ((points!=null)||(opened)){
				if (p.y <= y) return this;
				for (int i = 0; i< points.length; ++i){
					int h =  points[i].height;
					if ((p.y >= y)&&(p.y <= y+h)) {
						p.y -= y;
						return points[i];
					}else y+=h;
				}
				return null;
			}else{
				return this;
			}
		}
		
		void _setSize(){
			super.setSize();
			if ((points!=null)&&(opened)){
				int res;
				for (int i = 0; i< points.length; ++i){
					points[i].setSize();
					res =  points[i].width;
					width = res>width?res:width;
					height += points[i].height;
				}
				width += LEFT_STEP;
			}
		}

		public void setSize(){_setSize();}

		public boolean action(){
			try{
				loadNode(path.getpath(),name,arg);
				if (!opened) {
					if (points == null) load();
					opened = true;
				}else{
					opened = false;
					//points = null;
				}
				return true;
			}catch(Exception e){
				GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_ERRORLOADNODE,
										"Can't load Node"),true);
				return false;
			}
		}

		public void paint(Graphics g,int x,int y,int posx,int posy){
			Rectangle r = g.getClipBounds();
			r.y+=posy;
			r.x+=posx;
			if (y+textheight > r.y)
				super.paint(g,x,y,posx,posy);
			y+=textheight;
			if ((points!=null)&&(opened)){
				for (int i = 0; i< points.length; ++i){
					int h = points[i].height;
					if (( y+h > r.y ) && (y < r.y+r.height))
						points[i].paint(g,x+LEFT_STEP,y,posx,posy);
					y+=h;
				}
			}
		}

		void load() throws Exception{
			loadNode(this);
		}

		VNode reloadAll(){
			try{
				VNode a = reloadNode(this);
				a.opened = true;
				return a;
 			}catch(Exception e){
				return this;
			}
		}
	}



/**************************************************/
// Viewer
/**************************************************/
/**
 *
 */

	class Viewer extends Panel {
		int posx = 0;
		int posy = 0;
		Scrollbar sb;
		Scrollbar hsb;
		//VNode vn;
		Dimension size;
		Container parent;

		class A implements AdjustmentListener{
			public void adjustmentValueChanged(AdjustmentEvent ev){
				posy = sb.getValue();
				repaint();
			}
		}
		
		class B implements AdjustmentListener{
			public void adjustmentValueChanged(AdjustmentEvent ev){
				posx = hsb.getValue();
				repaint();
			}
		}

		class C implements ComponentListener,MouseListener,MouseMotionListener{
			public void componentResized(ComponentEvent ev){
				init_scroller();
			}

			public void componentMoved(ComponentEvent ev){
			}

			public void componentShown(ComponentEvent ev){
				init_scroller();
			}
			public void componentHidden(ComponentEvent ev){
			}
			public void mouseClicked(MouseEvent ev){
			}

			public void mousePressed(MouseEvent ev){
				if ((ev.getID()==MouseEvent.MOUSE_PRESSED) &&
					((ev.getModifiers()&MouseEvent.BUTTON3_MASK)!=0)) {
					createPopupMenu(ev.getX(),ev.getY());
				}else if ((ev.getID()==MouseEvent.MOUSE_PRESSED) &&
						  ((ev.getModifiers()&MouseEvent.BUTTON1_MASK)!=0)) {
					if (hilited!=null){
						if (ev.isControlDown()&&(hilited instanceof VNode)){
							nodeHandler(((VNode)hilited).path.getpath(),
										((VNode)hilited).name,
										arg);
						}else{
							if (hilited.action()) {
								init_scroller();
								repaint();
							}
						}
					}
				}
			}

			public void mouseReleased(MouseEvent ev){
			}
			public void mouseEntered(MouseEvent ev){
			}
			public void mouseExited(MouseEvent ev){
				//hilited = null;
				//repaint();
			}
			public void mouseDragged(MouseEvent ev){
			}
			public void mouseMoved(MouseEvent ev){
				if (vn.handle(MOUSE_MOVE,
							  new Point(ev.getX()+posx,ev.getY()+posy-V_OFFSET)))
					vr.fastrepaint();
			}
		}


		Viewer(Scrollbar sb,Scrollbar hsb,Container parent) throws Exception{
			//this.vn = vn;
			this.sb = sb;
			this.hsb = hsb;
			this.parent = parent;
			C c = new C();
			this.addComponentListener(c);
			this.addMouseMotionListener(c);
			this.addMouseListener(c);
			setLayout(null);
			sb.addAdjustmentListener(new A());
			hsb.addAdjustmentListener(new B());
			if (!vn.action()){
				throw new Exception("~body.Nafigator$Viewer::<init> can't load node");
			}
		}

		public void init_scroller(){
			vn.setSize();
			Dimension dm = vn.getSize();
			Dimension dm1 = getSize();
			if (dm.height+BORDER > dm1.height){
				if (!sb.isVisible()){
					sb.setVisible(true);
					parent.validate();
				}
				sb.setMaximum(dm.height+BORDER+V_OFFSET);
				sb.setVisibleAmount(getSize().height-BORDER-V_OFFSET);
			}else{
				sb.setVisible(false);
				posy = 0;
				parent.validate();
			}
			if (dm.width+BORDER > dm1.width){
				if (!hsb.isVisible()){
					hsb.setVisible(true);
					parent.validate();
				}
				hsb.setMaximum(dm.width+LEFT_STEP);
				hsb.setVisibleAmount(getSize().width-LEFT_STEP);
			}else{
				hsb.setVisible(false);
				posx = 0;
				parent.validate();
			}
		}

		public void update(Graphics g){
			paint(g);
		}

		void fastrepaint(){
			Graphics g = getGraphics();
			//System.out.println("gaphics is "+g);
			g.setClip((Shape)getBounds());
 			paint(g);
		}
		public void paint(Graphics g){
			g.setColor(bgColor);
			WB = g.getClipBounds().width;
			g.fillRect(0,0,WB,2*V_OFFSET);
			g.fillRect(0,vn.getSize().height-posy+V_OFFSET,
					   WB,getSize().height);
			vn.paint(g,H_OFFSET,V_OFFSET,posx,posy);
			
		}
	}



/**************************************************/
// VParser
/**************************************************/
/**
 *
 */
	public class VParser{
		public String name;
		public String path;
		public char type;
		char[] text;
		StringTokenizer st;
		int count;
		int count1;
		public boolean next() throws Exception{
			try{
				name = "";
				path = "";
				//System.out.println("count is "+count1);
				String p = st.nextToken().trim();
				StringTokenizer sp = new StringTokenizer(p,"/");
				String res = sp.nextToken();
				type = res.charAt(0);
				name = sp.nextToken();
				path = sp.nextToken();
			}catch(Exception e){
				e.printStackTrace();
				throw e;		
			}
			return ( --count1 != 0 );
		}
		public int count(){
			return count;
		}
		public VParser(char[] text){
			this.text = text;
			String foo = new String(text);
			st = new StringTokenizer(foo,"\n");
			count = 0;
			try{
				while(true){
					String p = st.nextToken().trim();
					if (p.length()>3){
						//System.out.println("__"+p+","+p.length()+":"+count);
						++count;
					}
					else break;
				}
			}catch(Exception e){
			}
			//System.out.println("__count__ is "+count);
			st = new StringTokenizer(foo,"\n");
			count1=count-1;
		}
	}


	
/**************************************************/
// Path
/**************************************************/
/**
 *
 */
	class Path{
		Path root;
		String path;
		public Path newp(String path){
			return new Path(this,path);
		}
		public Path(Path root,String path){
			this.root = root;
			this.path = path;
		}
		public String getpath(){
			if (root == null) return path;
			return root.getpath()+"/"+path;
		}
		public char[] getlist() throws Exception{
			return loadData(getpath(),true,arg);
		}
		public String toString(){
			return getpath();
		}
	}


// *********************************
//	really V3 realese
//
	

	VNode vn;
	Viewer vr;
	Panel mainp;
/**
 *
 */
	public V3(Font pointFont,Font nodeFont,Color[] colors,
		  String root,boolean hilite,Object arg)
		throws Exception{
		
		this.arg = arg;
		VPfn = pointFont;
		VNfn = nodeFont;
		bgColor = colors[0];
		fgColor = colors[1];
		VNcolor = colors[4];
		VPcolor = colors[5];
		bgSelectColor = colors[2];
		fgSelectColor = colors[3];
		hiliting = hilite;
		VPfm = getFontMetrics(VPfn);
		VNfm = getFontMetrics(VNfn);

		try{
			setFont(VPfn);
			setBackground(bgColor);
			setForeground(fgColor);
			Scrollbar sb = new Scrollbar();
			sb.setVisible(false);
			Scrollbar hsb = new Scrollbar(Scrollbar.HORIZONTAL);
			hsb.setVisible(false);
			vn = new VNode(new Path(null,"/"),root,VNfm,
						   VNfn,VNcolor);
			//vn.opened = true;
			setLayout(new BorderLayout());
			add("East",sb);
			add("South",hsb);
			vr = new Viewer(sb,hsb,this);
			add("Center",vr);
		}catch(Exception e){
			throw e;
		}
	}

	public abstract void loadPoint(String path,String name,Object arg);
	public void loadNode(String path,String name,Object arg){
	}
	public abstract void nodeHandler(String path,String name,Object arg);
/*		{
		}*/
	
	public void reloadAllNodes(){
		//System.out.println("reload all nodes");
		hilited = null;
		//System.out.println(vn);
		vn = vn.reloadAll();
		//System.out.println(vn);
		vn.setSize();
		vr.init_scroller();
		validate();
		vr.repaint();
		//System.out.println("end reload all nodes");
	}

	void createPopupMenu(int x,int y){
		System.out.println("~views.V3::createPopupMenu ::: menu not defined !!!");
	}

	void loadNode(VNode vn) throws Exception{
		char[] text = vn.path.getlist();
		//System.out.println("path = "+vn.path+"\n"+(new String(text))); 
		loadNode(vn,text);
	}
	void loadNode(VNode vn,char[] text) throws Exception{
		VParser vp = new VParser(text);
		vn.points = new VP[vp.count()];
		int i = 0;
		int count = vp.count();
		for(;i<count;++i){
			vp.next();
			if (vp.type == VParser_POINT) {
				vn.points[i] = new
					VPoint(vn.path.newp(vp.path),vp.name.trim()
						   ,VPfm, VPfn,VPcolor);
			}else if (vp.type == VParser_NODE) {
				vn.points[i] = new
					VNode(vn.path.newp(vp.path),vp.name.trim()
						  ,VNfm, VNfn,VNcolor);
			}else throw new
					  Exception(
						  "~views.V3::load Uncnown type of list element");
		}
	}

	void openNode(String path){
		int h = vn.openNode(path,"");
		vr.posy = h;
		vn.setSize();
		vr.sb.setValue(vr.posy);
		vr.init_scroller();
		repaint();
	}
	VNode reloadNode(VNode vn) throws Exception{
		//System.out.println("reload node "+vn);
		//vn.dump("++");
		char[] text = vn.path.getlist();
		//System.out.println(new String(text));
		VParser vp = new VParser(text);
		VNode vnn = new VNode(vn.path,vn.name,VNfm, VNfn,VNcolor); 
		//System.out.println("~~VNN:"+vnn+
		//				   "\n~~VN:"+vn);
		//System.out.println("VN.POINTS "+calc.OP.printArray(vn.points));
		loadNode(vnn,text);
		//System.out.println("~~VNN:"+vnn+
		//					   "\n~~VN:"+vn);
        //System.out.println("VN.POINTS "+calc.OP.printArray(vn.points));
		for(int i=0;i<vnn.points.length;++i){
			if ((vnn.points[i]) instanceof VNode) for(int j=0;j<vn.points.length;++j)
				if ((vn.points[j]) instanceof VNode){
					//System.out.println("@@VNN:"+vnn.points[i]+
					//					   "\n@@VN:"+vn.points[j]);
					if ( (((VNode)vn.points[j]).points != null) &&
						 vn.points[j].name.equals(vnn.points[i].name) &&
						 vn.points[j].path.path.equals(vnn.points[i].path.path)){
						
						//System.out.println("VNN:"+vnn.points[i]+
						//				   "\nVN:"+vn.points[j]);
						//System.out.println("_VN.POINTS "+calc.OP.printArray(((VNode)vn.points[j]).points));
						vnn.points[i]=reloadNode((VNode)vn.points[j]);
						((VNode)vnn.points[i]).opened=true;
						//System.out.println("Node "+vnn.points[i]+" Loaded");
						break;
					}
				}
		}
		return vnn;
	}

	public Object getcurentpath(){
		if (hilited!=null){
			return hilited.path.getpath();
		}else return new calc.Nil();
	}
	
	public abstract char[] loadData(String path,boolean enc,Object arg) throws Exception;
/*		{
		return GLOBAL.loader.loadByName_chars(path,enc);
		}*/
}
