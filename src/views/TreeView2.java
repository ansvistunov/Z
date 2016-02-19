package views;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import symantec.itools.awt.*;
import dbi.*;
import rml.*;
import document.*;
import loader.GLOBAL;
import calc.*;
import calc.objects.*;

public class TreeView2 extends Panel implements ActionListener,
	Retrieveable,class_method, class_type,GlobalValuesObject,Closeable{
	
	protected _TreeView tv;
        

		protected DataTree gr;
	String nodeAction;
	String listAction;
	String listAction2;
	String rootName;
	String alias;
	String ret;
	static Image open;
	static Image close;
	Hashtable aliases;
	Color background = Color.white;
	Color foreground = Color.black;
	PopupMenu popupMenu;
	boolean expandAll;
        boolean sorted;

	static{
		try{
			Toolkit t = Toolkit.getDefaultToolkit();
			open = t.getImage("./images/foldero.gif");
			close = t.getImage("./images/folderc.gif");
			if (open==null || close ==null) System.out.println("Images not found at ./images folder!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public TreeView2(){
		super();
		tv = new _TreeView(this);
		setLayout(new GridLayout(1,1));
		add(tv);
	}
	
	public void init(Proper prop, Hashtable aliases) {
		String sp;
		Integer ip;
		this.aliases = aliases;
		alias = (String)prop.get("ALIAS");
		listAction = (String)prop.get("LISTACTION");
		listAction2 = (String)prop.get("LISTACTION2");
		nodeAction = (String)prop.get("NODEACTION");
		rootName = (String)prop.get("ROOTNAME","");
		ret = (String)prop.get("RETURN","NO");
		sp = (String)prop.get("BACKGROUND");
		if (sp!=null) background = UTIL.getColor(sp);
		sp = (String)prop.get("FOREGROUND");
		if (sp!=null) foreground = UTIL.getColor(sp);
		if (((String)prop.get("EXPANDALL","NO")).equals("YES"))
			expandAll = true;
		else expandAll = false;
                if (((String)prop.get("SORTED","YES")).equals("YES"))
                        sorted = true;
                else sorted = false;
		
		Document doc = (Document)aliases.get("###document###");
        doc.addHandler(this);
   }	
	
	public void addChildren(Object[] obj){
		if (obj==null || obj.length==0) return;
		for (int i=0;i<obj.length;i++){
                        if (obj[i] instanceof DataTree) {
                                gr = (DataTree)obj[i];
			}else
			if (obj[i] instanceof views.Menu){
				popupMenu = new PopupMenu();
                java.awt.Menu m = ((views.Menu)obj[i]).getMenu();
                if (m==null) continue;
                int ic = m.getItemCount();
                for (int j=0;j<ic;j++) {
                    MenuItem mi = m.getItem(0);                        
                    if (mi==null) continue;
                    mi.addActionListener(this);
                    popupMenu.add(mi);
                }
                add(popupMenu);

			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();		
		try {
			document.ACTION.doAction(command,aliases,null);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			//System.out.println("exception inside document.ACTION:doAction : "+e);
		}
	}
	
	/*TreeNode l11 = new TreeNode("Уровень 1");
		TreeNode l12 = new TreeNode("Уровень 2");
		TreeNode l13 = new TreeNode("Уровень 3");
		tv.addChild(new TreeNode("Child 1"),l11);
		tv.addChild(new TreeNode("Child 2"),l11);
		tv.addChild(new TreeNode("Child 3"),l11);
		
		tv.addChild(new TreeNode("Child 5"),l12);
		tv.addChild(new TreeNode("Child 6"),l12);
		tv.addChild(new TreeNode("Child 7"),l12);
		
		tv.addChild(new TreeNode("Child 8"),l13);
		tv.addChild(new TreeNode("Child 9"),l13);
		tv.addChild(new TreeNode("Child 10"),l13);
		
		tv.addChild(l11,root);
		tv.addChild(l12,root);
		tv.addChild(l13,root);
		*/
	void createTree(){
		tv.clear();
		tv.setBackground(background);
		tv.setForeground(foreground);
		TreeNode root = new TreeNode(rootName,close,open,tv);		
		tv.append(root);
		dbi.Group r = gr.getRoot();
		if (r!=null) {
			root.setDataObject(r);
                        GLOBAL.waitwin_text("Sorting nodes.");
			recurse(r,root,new int[]{});
                        GLOBAL.waitwin_text("");
		}
		if (expandAll) tv.expandAll();
		else root.expand();
	}
	
	void rightClickReaction(MouseEvent e){
		if (popupMenu!=null)
			popupMenu.show(this,e.getX(),e.getY());
	}
	
	public void setCurrentRow(int r){
                if (gr!=null) ((dbi.DATASTORE)gr).setCurRow(r);
	}
	
	public void closeNotify() {
        dbi.DATASTORE ds2 = null;
        if (ret.equals("YES")) ds2 = returnSelection();        
        if (ds2!=null) {
            aliases.put("RETURNSTORE", ds2);            
        }
        else {}
    }
	
	dbi.DATASTORE returnSelection(){
                if (gr==null || ((dbi.DATASTORE)gr).getCountRows()==0) return null;
		TreeNode node = tv.getSelectedNode();
                System.out.println("tv.isList(node)="+tv.isList(node));
                System.out.println("node.getDataObject()="+node.getDataObject());
                
                //if (node==null || !tv.isList(node)) return null; <--old code
                // if tv.isList() == true - значит выбрана нода!!!
                //patch by asw
                if (node==null) return null;
		dbi.Group g = (dbi.Group)node.getDataObject();
		if (g==null) return null;		
		int[] keys = new int[]{g.begrow};
                return ((dbi.DATASTORE)gr).createFilter(keys);
	}
	
	
	//отображает стр-ру GroupReport на TreeView2
	protected void recurse(dbi.Group g,TreeNode node,int[] path){
		dbi.Group[] subgroups = g.getSubgroups();
		if (subgroups == null) return;
		
		int[] newpath = new int[path.length+1];
		if (newpath.length>1)
			System.arraycopy(path,0,newpath,0,newpath.length-1);

                TreeNode[] ar = new TreeNode[subgroups.length];
		for (int i=0;i<subgroups.length;i++) {
			newpath[newpath.length-1] = i;
			Object o = gr.getGroupValue(newpath);
			TreeNode n = new TreeNode(o==null?"":o.toString(),close,open,tv);
			n.setDataObject(subgroups[i]);
			
//                        tv.addChild(n,node);
                        ar[i] = n;
			recurse(subgroups[i],n,newpath);
		}

                if (sorted)
                  sortNodes(ar);

                for (int i = 0; i < ar.length; i++)
                  tv.addChild(ar[i], node);
                
                //System.out.println("TreeView2 recurse end. g="+g);    
                
        }
        
        public void sortNodes(TreeNode[] ar) {
          int k,j,n = ar.length;
          if (n == 0)
            return;

          k=n;
          for (j = n/2; j>=1; j--) {
              pros(ar, j, k);
          }
          for (k = n-1; k>=1; k--) {
              swap(ar, 1-1,k+1-1);
              pros(ar, 1, k);
          }

        }  // public void sortNodes(TreeNode[] ar)

        private void pros(TreeNode[] ar, int x, int k) {
            int y;
            while(true) {
                y=x+x;
                switch(sign(y-k)+2) {
                    case 1: {if ( compareRows(ar,y-1,y+1-1)<0 ) y++;}
                    case 2: {if ( compareRows(ar,x-1,y-1)>=0) return;
                             swap(ar,x-1,y-1);x=y;break;}
                    case 3: {return;}
                }
            }
        }  // private void pros(TreeNode[] ar, int x, int k)

        private int sign(int x) {
                if (x == 0) return 0;
                return x<0?-1:1;
        }

        private int compareRows(TreeNode[] ar, int i1, int i2) {
          return ar[i1].getText().compareTo(ar[i2].getText());
        }

        private void swap(TreeNode[] ar, int i1, int i2) {
          TreeNode tmp = ar[i1];
          ar[i1] = ar[i2];
          ar[i2] = tmp;
	}
	
	public void nodeAction(){
		//System.out.println("nodeAction called !");
		try{
			if (nodeAction!=null)
				document.ACTION.doAction(nodeAction,aliases,null);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void listAction(){
		//System.out.println("listAction called !;listAction="+listAction);
		try{
			if (listAction!=null)
				document.ACTION.doAction(listAction,aliases,null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void listAction2(){
		System.out.println("listAction2 called !;listAction2="+listAction2);
		try{
			if (listAction2!=null)
				document.ACTION.doAction(listAction2,aliases,null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void retrieve() {
                System.out.println("Treeview2 retrieve called gr="+gr);
		if (gr!=null){
                       try{
                        ((dbi.DATASTORE)gr).retrieve();
			createTree();			
                        }catch(Exception e){e.printStackTrace();}
		}
    }
    public void update() {
    }
    public void fromDS(){
	}
    public void toDS(){ 
    }
	
	/*public boolean imageUpdate(Image img,int flags,int x,int y,int w,int h){
		boolean loading = (flags & (ALLBITS|ABORT)) == 0;
		if (!loading) repaint();
		return loading;
	}*/
	
	//Методы интерфейса GlobalValuesObject
    public void setValue(Object o){}
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}
	
	//Методы интерфейса class_type
    public String type(){
		return "VIEWS_TREEVIEW2";
	}

    public int getLevel() {return tv.getCurrentLevel();}
    
	//Методы интерфейса class_method
	public Object method(String method,Object arg) throws Exception{
	    //обработка вызова метода CurrentValue
	    if (method.equals("GETLEVEL")) {
			if (tv==null) return new Double(0);
			else
			return new Double(tv.getCurrentLevel());
		}else 
	    throw new RTException("HasNotMethod","method "+method+
							  " not defined in class views.TreeView2");
	}
}
