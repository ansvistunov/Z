
package views;

import rml.*;
import calc.*;
import loader.GLOBAL;
import java.awt.*;
import java.net.URL;

public class IMage extends Canvas{
	String alias;
	int left;
	int top;
	int width=200;
	int height=200;
	Image im;
	String iname;
	boolean resize = false;
	public IMage(){
	}
	public void init(Proper prop){
		String sp;
		Integer ip;
		alias = (String)prop.get("ALIAS");
		ip = (Integer)prop.get("LEFT");
		if (ip!=null) left = ip.intValue();
		ip = (Integer)prop.get("TOP");
		if (ip!=null) top = ip.intValue();
		ip = (Integer)prop.get("WIDTH");
		if (ip!=null) width = ip.intValue();
		ip = (Integer)prop.get("HEIGHT");
		if (ip!=null) height = ip.intValue();
		sp = (String)prop.get("RESIZE","NO");
		if (sp.toUpperCase().equals("YES")) resize = true;
		iname = (String)prop.get("SRC");
		im = getImage(iname);
		setLocation(left,top);
		setSize(width,height);
		//if (im!=null)
		//	setSize(im.getWidth(this),im.getHeight(this));			
		//setIncRate(5000);
	}
	/*public void update(Graphics g) {
		paint(g);
	}*/
	public void paint(Graphics g){
		if (im!=null){
			if (!resize)
				g.drawImage(im,0,0,this);
			else
				g.drawImage(im,0,0,width,height,this);
		}
	}
	
	/*public boolean imageUpdate(Image img,int flags,int x,int y,int w,int h){
        //boolean loading = (flags & (ALLBITS|ABORT)) == 0;		//boolean loading = (flags & (FRAMEBITS|ABORT)) == 1;		//boolean nextFrame = (flags & FRAMEBITS)!=0;
        //if (nextFrame) repaint();
        //return loading;		//return true;		//System.out.println("flag="+flags);
		//System.out.println("incRate="+getIncRate());
		if ((flags&SOMEBITS) != 0) return true;		try{Thread.sleep(250);}
		catch(Exception e){}		return super.imageUpdate(img,flags,x,y,w,h);
    }*/
	
	Image getImage(String name) {
		if (name==null || name.equals("")) return null;
		String docserver = GLOBAL.pr(GLOBAL.DOC_SERVER);
		if (docserver==null || docserver.equals("")) return null;
		if (!docserver.endsWith("/")) docserver+="/";
		URL u = null;
		try{
			u = new URL(docserver+name);
		}catch(Exception e){
			return null;
		}
		Toolkit t = Toolkit.getDefaultToolkit();
		Image ret = t.getImage(u);
		//t.prepareImage(ret,-1,-1,null/*this*/);
		return ret;
	}
}
