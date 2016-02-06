package rml;


import java.util.Hashtable;
import java.util.Vector;

import loader.GLOBAL;

/*
 * "START",
		"TAG",
		"SP",
		"PROP",
		"SVSD",
		"INT",
		"FLOAT",
		"STRING",
		"IDENT",
		"SBK",
		"SOBK",
		"FINISH"
 * 
 * 
 * 
 * */


public class ParserObserver {
	Vector<RmlRectangle> recs = new Vector();
	
	Vector<String> keys = new Vector();
	
	
	boolean needStoreAlias = false; 
	boolean isAlias = false; 
	String currAlias;
	
	boolean isLeft = false;
	boolean isTop = false;
	boolean isWidth = false;
	boolean isHeight = false;
	
	RmlRectangle r;
	
	int i = -1;
	
	public void clear() {
		// TODO Auto-generated method stub
		recs.clear();
		keys.clear();
		r = null;
		currAlias = null;
		i = -1;
		
		needStoreAlias = false; 
		isAlias = false; 
		
		
		isLeft = false;
		isTop = false;
		isWidth = false;
		isHeight = false;
		
	};
	
	public void SS(int cc) {
		if (GLOBAL.parser_debug >1) System.out.println("SS cc="+cc);
	};
	public void STAG (int cc, String tag) {
		if (GLOBAL.parser_debug >1)System.out.println ("tag:"+tag +" at "+cc);
		if (currAlias!=null && r!=null) {
			if (i<keys.size() ) {
				keys.set(i, currAlias);
				recs.set(i, r);
			}else{
				System.out.println("ParserObserver STAG Error: prevAlias="+currAlias+" tag="+tag+" cc="+cc);
			}
		}
		
		if (tag.equals("FIELD") || tag.equals("LABEL") || tag.equals("BUTTON"))	{
			needStoreAlias=true;
			i++;
		}
		else needStoreAlias = false;
		
		r = null;
		isLeft=isTop=isWidth=isHeight=false;
		currAlias = null;
		
	};
	public void	SP(int cc){
		if (GLOBAL.parser_debug >1) System.out.println("SP cc="+cc);
	};
	public void SPROP(int cc, String propname){
		if (GLOBAL.parser_debug >1) System.out.println ("propname:"+propname +" at "+cc);
		if (propname.equals("ALIAS")) isAlias = true;
		else isAlias=false;
		if (propname.equals("LEFT")) {
			isLeft = true;
			if (r==null) r = new RmlRectangle();
			r.sx = cc;
		}
		else isLeft = false;
		if (propname.equals("TOP")) {
			isTop = true;
			if (r==null) r = new RmlRectangle();
			r.sy = cc;
		}
		else isTop = false;
		if (propname.equals("WIDTH")) {
			isWidth = true;
			if (r==null) r = new RmlRectangle();
			r.sw = cc;
		}
		else isWidth = false;
		if (propname.equals("HEIGHT")) {
			isHeight = true;
			if (r==null) r = new RmlRectangle();
			r.sh = cc;
		}
		else isHeight = false;
	};
			
	public void SVSD (int cc, String value){
		if (GLOBAL.parser_debug >1) System.out.println ("value:"+value +" at "+cc);
		if (isAlias && needStoreAlias) {
			if (r==null) r = new RmlRectangle();
			if (keys.size()-1 < cc) {
				keys.add(currAlias);
				recs.add(r);
				System.out.println(" added element to vector vector size="+keys.size()+" cc="+cc);
			}
			if (i<keys.size()) {
				keys.set(i, currAlias);
				recs.set(i, r);
			}else{
				System.out.println("ParserObserver SVSD Error: currAlias="+value+" cc="+cc);
			}
			
			currAlias = value;
			isAlias = false;
			//System.out.println("SVSD add "+currAlias+" "+r);
		}
		if (isLeft){
			if (r==null) r = new RmlRectangle();
			r.x = Integer.parseInt(value);
			r.ex = cc;
			isLeft = false;
		}
		if (isTop){
			if (r==null) r = new RmlRectangle();
			r.y = Integer.parseInt(value);
			r.ey = cc;
			isTop = false;
		}
		if (isWidth){
			if (r==null) r = new RmlRectangle();
			r.w = Integer.parseInt(value);
			r.ew = cc;
			isWidth = false;
		}
		if (isHeight){
			if (r==null) r = new RmlRectangle();
			r.h = Integer.parseInt(value);
			r.eh = cc;
			isHeight = false;
		}
		
	};
	public void SBK(int cc){
		if (GLOBAL.parser_debug >1) System.out.println("SBK cc="+cc);
	};
	public void SOBK(int cc){
		if (GLOBAL.parser_debug >1) System.out.println("SOBK cc="+cc);
	};
	public void SF(int cc){
		if (GLOBAL.parser_debug >1) System.out.println("SF cc="+cc);
		if (currAlias!=null && r!=null) {
			if (i<keys.size()) {
				keys.set(i, currAlias);
				recs.set(i, r);
			}else{
				System.out.println("ParserObserver SF Error: currAlias="+currAlias+" cc="+cc);
			}
		}
	}
	
	public Vector<RmlRectangle> getRecs() {
		return recs;
	}
	public Vector<String> getKeys() {
		return keys;
	}
}
