
/*
 * File: Document.java
 *
 * Created: Thu Mar 18 16:15:09 1999
 *
 * Copyright(c) by Alexey Chen
 * Patched by Igor Kumagin 23.06.2001
 */

package document;

import dbi.*;
import java.awt.*;
import views.Retrieveable;
import rml.*;
import java.util.*;
import java.io.*;
import loader.*;
import body.Nafigator;
import calc.*;
import calc.objects.*;
import java.awt.event.*;

class ARG implements GlobalValuesObject{
	Object[] objs;
	ARG(Object[] o){
		objs = o;
	}
	public void setValue(Object obj){
	}
	public Object getValue(){
		return "NOTHING";
	}
	public void setValueByName(String name, Object obj) throws Exception{
        int i = name.indexOf('.');
		if (i != -1 ){
            ((GlobalValuesObject)
                objs[(int)Integer.valueOf(name.substring(0,i)).longValue()]).
                setValueByName(name.substring(i+1),obj);
        }else throw new RTException("CASTEXCEPTION","cant set in ARGUMENTS."+name);
	}
	public Object getValueByName(String name) throws Exception{
		try{
			return objs[(int)Integer.valueOf(name).longValue()];
		}catch(NumberFormatException e) {
		    int i = name.indexOf('.');
		    if (i != -1 ){
                return ((GlobalValuesObject)
                objs[(int)Integer.valueOf(name.substring(0,i)).longValue()]).
                getValueByName(name.substring(i+1));
		    } else throw e;
		}catch(NullPointerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			return "NOTHING";
		}
	}
}

public class Document implements
GlobalValuesObject,class_method,class_type,
WindowListener{

	public void windowOpened(WindowEvent e){}
	public void windowClosing(WindowEvent e){getcurd().processAction(ACT_CANCEL);}
	public void windowClosed(WindowEvent e){/*curd.processAction(ACT_CANCEL);*/}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}

	/** */
	final public static int ACT_CANCEL = 0;
	/** */
	final public static int ACT_DOK = 1;
	/** */
	final public static int ACT_HOK = 2;
	/** */
	final public static int ACT_NEW = 3;
	/** */
	final public static int ACT_SAVE = 4;		StatusPanel statusPanel;		public StatusPanel getStatusPanel(){return statusPanel;}


	DBStateBroker dbsbroker = new DBStateBroker(this);

	Calc closescript = null;

	public String myname ="";
	public String mypath ="";

    Integer version = new Integer(1);

	public static boolean resetIt = false;

    /** */
    Nafigator naf = null;
	//public static Document curd = null;
	/** */
	Document oldd = null;

	/** */
	Hashtable parent_aliases = null;
	/** */
	public Hashtable aliases = null;
	/** */
	public final Hashtable lhashable = new Hashtable();
	public static final Hashtable hashable = new Hashtable();

	/** */
	Panel parent_panel = null;
	/** */
	Panel mypanel = null;
	/** */
	Container cntr = null;

	/** */
	static int counter = 1;
	/** */
	static Dialog dg = null;

	/** */
	Component maincomponent = null;

	/** */
	Actioner actor = null;

	/** */
	Panel panel;

	/** */
	dbi.DSCollection collection = null;

	/** */
	class HandlerList{
		public HandlerList next;
		public Closeable handler;
		public HandlerList(Closeable c,HandlerList h){
			handler = c;
			next = h;
		}
	}
	
	/**
	 * редактор для документов
	 * 
	 * */
	static Editor editor;
	/**
	 * Аргументы для текущего документа
	 * */
	Object[] args;
	
	
	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}
	public static void setEditor(Editor editor){
		Document.editor = editor;
	}
	
	public static Editor getEditor(){
		return editor;
	}
	
	
	/** */
	//void setNafigator(Nafigator naf) {this.naf=naf;}
	public static Document getcurd(){
	    if (GLOBAL.nafigator!=null){
	        Nafigator n = (Nafigator)GLOBAL.nafigator;
	        return n.curd;
	    }
	    else return null;
	}
	public static void setcurd(Document d){
	    if (GLOBAL.nafigator!=null) {
	        Nafigator n = (Nafigator)GLOBAL.nafigator;
	        n.curd=d;
	    }
	}
	HandlerList hl = null;
	public void addHandler(Closeable handler){
		hl = new HandlerList(handler,hl);
	}
	void notifyHandlers(HandlerList hl){
		if ( hl!=null ){
			if (hl.next!=null) notifyHandlers(hl.next);
			try{
				hl.handler.closeNotify();
			}catch(Exception e){
				// nothing to do
			}
		}
		
	}

public Hashtable getParentAliases() {return parent_aliases;}

/***
 *
 */
	class BTN extends Button {
		Document doc;
		int action;
		public BTN(String label,Document d,int action){
			super(label);
			doc = d;
			this.action = action;
			setBackground(GLOBAL.color(GLOBAL.CLR_DOCUMENT,GLOBAL.BTNBG));
			setForeground(GLOBAL.color(GLOBAL.CLR_DOCUMENT,GLOBAL.BTNFG));
		}
		public boolean handleEvent(Event ev){
			//System.out.println("BTN event "+ev);
			if ( ev.id == Event.ACTION_EVENT ){
				super.handleEvent(ev);
				doc.processAction(action);
				return true;
			}else return super.handleEvent(ev);
		}
	}

	/** */
	public Panel getPanel(){return panel;}


	/** */
	void updateAllStore(){
	}


	/** */
	public void close() {
		// удалить документ
		// System.out.println("closescript is "+closescript);
		try {
			if (closescript != null)
				closescript.eval(aliases);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbsbroker.saveall();
		// pavel
		if (collection != null)
			collection.removeLocks();
		// end pavel
		cntr.removeAll();
		cntr.add(parent_panel);
		cntr.validate();
		/* igor: begin !!!!!!!!!! */
		Stack stc = new Stack();
		stc.push(cntr);
		Container tcmp;
		Component[] tcomps;
		int u;
		while (stc.size() > 0) {
			tcmp = (Container) stc.pop();
			tcomps = tcmp.getComponents();
			for (u = 0; u < tcomps.length; u++) {
				if (tcomps[u] instanceof Container)
					stc.push(tcomps[u]);
				tcomps[u].repaint();
			}
		}
		/* end !!!!!!!!!! */

		setcurd(oldd);
		// если это был последний документ в окне - закрыть окно
		if (--counter == 0)
			dg.hide();
		else {
			if (actor != null)
				actor.notifyActioner();
			String title;
			try {
				title = (String) ((Proper) parent_aliases.get("###propers###")).get("DOCUMENT_TITLE");
			} catch (Exception e) {
				title = GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW, "MainWindow");
			}
			if (title == null)
				title = GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW, "MainWindow");
			if (dg != null)
				dg.setTitle(GLOBAL.c2b(title, GLOBAL.DTITLE));
			else
				GLOBAL.nafigator.setTitle(GLOBAL.c2b(title, GLOBAL.FTITLE));
		}

		if (editor!=null) editor.closeNotify();
	}

	/** сброс документа к "первоначальному" состоянию. для коллекции перегенирируются первичные ключи 
	 * Для всех элементов вызывается метод fromDS, который обычно считывает данные из Datastore */
	public void reset() throws Exception{
		if ( collection != null ) {
		    collection.reset();
		    if (maincomponent==null)
		    System.out.println("maincomponent==null");
		    else
		    ((Retrieveable)maincomponent).fromDS();
		}
		else throw new Exception();
	}

	/** */
        public int processAction(int action) {
                System.out.println("process Action "+action);
                int success = 1;
		switch(action){
		case ACT_CANCEL:
			notifyHandlers(hl);
			close();
			break;
		case ACT_DOK:/* */
			try{
			    ((Retrieveable)maincomponent).toDS();
				if ( collection != null ) collection.update();
				else updateAllStore();
				// известить всех кто того желает , о том что документ закрывается
				notifyHandlers(hl);
				close();

			}catch(Exception e){
			    //if (e instanceof dbi.UpdateException){
			        //System.out.println("Construred Diait:");
				dbi.DATASTORE.er.addMessage(e.getMessage());
                                success = 0;
			        //};
			}

			break;
		case ACT_HOK:/* */
			try{
				// известить всех кто того желает , о том что документ закрывается
				notifyHandlers(hl);
				try{
					//parent_aliases.put(
					//"STORE",
					//(dbi.DATASTORE)aliases.get((String)
					//((Proper)aliases.get("###propers###")).get("RETURN")));
					parent_aliases.put(
					"STORE",
					(dbi.DATASTORE)aliases.get("RETURNSTORE"));
				}catch(NullPointerException e){
					parent_aliases.remove("STORE");
				}
				close();
			}catch(Exception e){
			    //if (e instanceof dbi.UpdateException){
			        dbi.DATASTORE.er.addMessage(e.getMessage());
                                success = 0;
			        //};
			}

			break;
		case ACT_SAVE:/* */
			try{
			    ((Retrieveable)maincomponent).toDS();
				if ( collection != null ) collection.update();
				else updateAllStore();
			}catch(Exception e){
			    //if (e instanceof dbi.UpdateException){
			        dbi.DATASTORE.er.addMessage(e.getMessage());
                                success = 0;
			       // };
			}
			break;
		case ACT_NEW:/* */
			try{
				reset();
			}catch(Exception e){
			    //if (e instanceof dbi.UpdateException){
			        dbi.DATASTORE.er.addMessage(e.getMessage());
                                success = 0;
			       // };
			}
			break;
		default:
		}
                return success;
	}

	/**
	 * загрузить и открыть документ
	 */
        public static synchronized Document callDocument(String docName,Object[] objs,
									Hashtable  aliases,
									Container cntr,
									Panel parent) throws Exception{
		Document dc;
		dc = Document.loadDocument(docName,objs,GLOBAL.loader);
		dc.parent_aliases = aliases;
		return dc.callDocument(cntr,parent);
	}
        
        /**
    	 * загрузить и открыть документ - из Prop
    	 */
        public static synchronized Document callDocument(String docName,Object[] objs,
    									Hashtable  aliases,
    									Container cntr,
    									Panel parent,Proper prop) throws Exception{
    		Document dc;
    		dc = Document.loadDocument(docName,objs,prop,GLOBAL.loader);
    		dc.parent_aliases = aliases;
    		return dc.callDocument(cntr,parent);
    	}

	/**
	 * загрузить и открыть документ в том же окне в котором открыт текущ
	 */
        public static synchronized Document callDocumentSomeWindow(String docName,Object[] objs,
									Hashtable  aliases) throws Exception{
		return callDocument(docName,objs,aliases,getcurd().cntr,getcurd().mypanel);
	}
        
        /**
    	 * загрузить и открыть документ в том же окне в котором открыт текущ
    	 */
        public static synchronized Document callDocumentSomeWindow(String docName,Object[] objs,
    									Hashtable  aliases,Proper prop) throws Exception{
    	return callDocument(docName,objs,aliases,getcurd().cntr,getcurd().mypanel,prop);
    }


	/**
	 * загрузить и открыть документ в том же окне в котором открыт
	 * текущий и зарегистрировать Actor.
	 */
        public static synchronized Document callDocumentSomeWindow(String docName,Object[] objs,
									Hashtable  aliases,Actioner actor) throws Exception{
		Document dc;
		dc = Document.loadDocument(docName,objs,GLOBAL.loader);
		dc.actor = actor;
		dc.parent_aliases = aliases;
		Document ret =  dc.callDocument(getcurd().cntr,getcurd().mypanel);
		if (editor!=null) 
			editor.loaded();
		return ret;
	}
        
        
	public static synchronized Document callDocumentSomeWindowFromMemory(String docName,char[] text, Object[] objs,
			Hashtable aliases, Actioner actor) throws Exception {
		Document dc;
		dc = Document.loadDocument(docName, objs, GLOBAL.loader, text);
		dc.actor = actor;
		dc.parent_aliases = aliases;
		return dc.callDocument(getcurd().cntr, getcurd().mypanel);
	} 
        

	/**
	 * загрузить и открыть документ в новом окне
	 */
	public static Document callDocumentNewWindow(String docName,Object[] objs,
									Hashtable  aliases) throws Exception{
		Document dc;
		dc = Document.loadDocument(docName,objs,GLOBAL.loader);
		dc.parent_aliases = aliases;
		return dc.callDocumentNewWindow();
	}

	public static Document callDocumentNewWindow(String docName,Object[] objs,
									Hashtable  aliases,Actioner actor) throws Exception{
		Document d =  callDocumentNewWindow(docName,objs, aliases);
		actor.notifyActioner();
		return d;
	}

	/**
	 * открыть документ в новом окне
	 */
	public Document callDocumentNewWindow() throws Exception{
		int old_counter = counter;
		counter = 0;
		//Frame fr = new Frame();
		//fr.move();
		Dialog dg = new Dialog(new Frame(),"",true);
		Dialog olddg = this.dg;
		this.dg = dg;
		dg.addWindowListener(this);
		int width,height;
		try{
			width = ((Integer)((Proper)aliases.get(
				"###propers###")).get("DOCUMENT_WIDTH")).intValue();
		}catch(Exception e){
			width = 600;
			//e.printStackTrace();
		}
		try{
			height = ((Integer)((Proper)aliases.get(
				"###propers###")).get("DOCUMENT_HEIGHT")).intValue();
		}catch(Exception e){
			height = 400;
			//e.printStackTrace();
		}
		dg.setSize(width,height);
		dg.setLayout(new GridLayout(1,1));
		
		//alex
		Dimension dm = dg.getToolkit().getScreenSize();
		dg.setLocation((dm.width-width)/2,(dm.height-height)/2);
		//end alex
		
		Panel p = new Panel();
		dg.add(p);
		callDocument(p,new Panel());
		try{
			dg.validate();
			dg.show();
		}catch(Exception e){
			e.printStackTrace();
		}
		counter = old_counter;
		dg.dispose();
		this.dg = olddg;
		//if ( dg != null ){
		//	dg.requestFocus();
		if (this.dg != null) {
			this.dg.requestFocus();
		}else{
			GLOBAL.nafigator.requestFocus();
		}
		return this;
	}

	/**
	 * открыть документ
	 */
	public Document callDocument(Container cntr,Panel parent) throws Exception{
		this.cntr = cntr;
		parent_panel = parent;
		cntr.removeAll();
		cntr.setLayout(new GridLayout(1,1));
		mypanel = getPanel();
		cntr.add(mypanel);
		String title;
		try{
			title =
				(String)((Proper)aliases.get("###propers###")).
				get("DOCUMENT_TITLE");
		}catch(Exception e){
			title = GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW,"MainWindow");
		}
		if ( title == null ) title = GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW,"MainWindow");
		if (dg != null)
			dg.setTitle(GLOBAL.c2b(title,GLOBAL.DTITLE));
		else
			GLOBAL.nafigator.setTitle(GLOBAL.c2b(title,GLOBAL.FTITLE));
		cntr.validate();
		++counter;
		oldd = getcurd();
		setcurd(this);
		
		System.out.println("Call document ended");
		
		return this;
	}


	public static synchronized Document loadDocument(String DocName,Object[] args,
			 Loader loader) throws Exception{
		
		return loadDocument(DocName,args,loader,null);
		
	}
	
	
    /**
	 * загрузить документ
	 */
        public static synchronized Document loadDocument(String DocName,Object[] args,
							 Loader loader, char[] __text)
		throws Exception{
		
			Proper prop;
				
				if (__text==null) { //идет загрузка из файла
					__text = loader.loadByName_chars(DocName,true);
				}//иначе - идет загрузка из редактора
				
				if (editor!=null) 
					editor.loadDocument(DocName, __text, args);
				
				try{
					prop = rml.Parser.createProper(__text,null);
				}catch(Exception e){
					throw e;
				}
				Document d = Document.loadDocument(DocName,args,prop,loader);
				int foo = DocName.lastIndexOf('/');
				if ( foo != -1 ) {
					d.myname = DocName.substring(foo+1);
					d.mypath = DocName.substring(0,foo);
				}else d.myname = DocName;
				return d;
			
	}
        
        /**
    	 * загрузить документ
    	 */
     public static synchronized Document loadDocument(String DocName,Object[] args,
    							 Proper prop, Loader loader ) throws Exception{
    	 try{
 			Document d;
 			DocName = DocName.trim();
 			if ( DocName.charAt(0) == '&' ){
 				if ( getcurd() != null)
 					if ( (d = (Document)getcurd().lhashable.get(DocName)) != null )
 						return d;
 			}else{
 				if ( (d = (Document)hashable.get(DocName)) != null )
 					return d;
 			}
 			d = new Document();
 			if (DocName.charAt(0) == '&') {
 				try{
 					prop = (Proper)getcurd().aliases.get(DocName.substring(1));
 					d.myname = (String)prop./*hash.*/get("###file###");
 					d.mypath = (String)prop./*hash.*/get("###path###");
 					//if (prop == null) throw new Exception();
 				}catch(Exception e){
 					//e.printStackTrace();
 					throw new calc.RTException("CastException",
 											   "&ALIAS <"+DocName.substring(1)
 											   +"> must containt Proper!!!!");
 				}
 			}else{
 				int foo = DocName.lastIndexOf('/');
				if ( foo != -1 ) {
					d.myname = DocName.substring(foo+1);
					d.mypath = DocName.substring(0,foo);
				}else d.myname = DocName;
				
 			}
 			try{
 				d.aliases = new Hashtable();
 				d.aliases.put("###propers###",prop);
 				d.aliases.put("###document###",d);
 				d.args = args;
 				d.aliases.put("ARGUMENTS",new ARG(args));
 				d.aliases.put("SELF",d);
 				d.aliases.put("GLOBAL",new ARGV());
 				d.aliases.put("_DATALOADER_",loader);
 				d.aliases.put("###DBSBROKER###",d.dbsbroker);




 				///
 				String styleFile = (String)prop.get("STYLE");
                                 String calc = (String)prop.get("CALC.LANGUAGE");
                                 if (calc!=null) d.aliases.put("CALC.LANGUAGE",calc) ;

                
                                 try{
                                    d.version = (Integer)prop.get("VERSION");
                                 }catch(Exception e){}
                                 if (d.version == null) d.version = new Integer(1);

 				//System.out.println("StyleFile = "+styleFile);
 				Hashtable __h = new Hashtable();
 				try{
 				if (styleFile != null){
 					styleFile = styleFile.trim();
 					byte[] text = loader.loadByName_bytes(styleFile);
 					System.out.println("content.length = "+text.length);
 				
 					Properties _p = new Properties();
 					ByteArrayInputStream is = new ByteArrayInputStream(text);
 					_p.load(is);
 					Enumeration e = _p.keys();
 					while(e.hasMoreElements()){
 						try{
 						String _tmp = (String)e.nextElement();
 						StringTokenizer st = new StringTokenizer(_tmp,".");
 						String tag = st.nextToken().toUpperCase();
 						String pr = st.nextToken().toUpperCase();
 						Object val = _p.get(_tmp);
 						try{
 							val = Integer.valueOf((String)val);
 						}catch(Exception f){}
 						System.out.println("tag="+tag+" pr="+pr+" val="+val);
 						if (__h.containsKey(tag)){
 							Hashtable _h = (Hashtable)__h.get(tag);
 							_h.put(pr,val);
 						}else{
 							Hashtable _h = new Hashtable();
 							_h.put(pr,val);
 							__h.put(tag,_h);
 						}
 						}catch(Exception e1){System.out.println("Error parsing style file: Bad format\n format is: tag.properties=value");} 
 							
 					//System.out.println("element="+e.nextElement());
 						
 				
 					}
 					recur(prop,0,_p,__h);
 					
 					//prop.dump();
 				}
 				
 				}catch(Exception e){System.out.println("Error processing style:"+e);}
 				///
 				//System.out.println(d.aliases);
                                 d.aliases.put("###VERSION###",new Double(d.version.intValue()));
 				Object[] ch = rml.Parser.getContent(prop,d.aliases);
 				
 				
 				//System.out.println("docement load aliases="+d.aliases);
 				
 				
 				//Зачем здесь подменяется Proper????? Видимо, чтобы освободить память
 				
 				Proper x = new Proper(-1);
 				x.hash = prop.hash;
 				d.aliases.put("###propers###",x);
 				prop = x;
 				
 				////////////////////
 				
 				Proper.clearDefault();
 				String script = (String)prop.get("PRELOADSCRIPT");
 				if (script!=null){
 					Calc c = new Calc(script);
 					c.eval(d.aliases);
 				}
 				String bl = (String)prop.get("BUTTONS_LOCATION");
 				boolean bot = true;
 				if (bl!=null && bl.toUpperCase().equals("RIGHT")) bot = false;
 				d.insertObjects(ch,(String)prop.get("BUTTONS"),bot);
 				script = (String)prop.get("POSTLOADSCRIPT");
 				if (script!=null){
 					Calc c = new Calc(script);
 					
 					//System.out.println(d.aliases);
 					c.eval(d.aliases);
 				}

 				script = (String)prop.get("CLOSESCRIPT");
 				if (script!=null){
 					d.closescript = new Calc(script);
 				}

 				String h = (String)prop.get("HASHABLE");
 				if ( DocName.charAt(0) == '&' ){
 					if ( getcurd() != null )
 						if ((h!=null)&& (h.trim().toUpperCase().compareTo("YES")==0))
 							getcurd().lhashable.put(DocName,d);
 				}else{
 					if ((h!=null)&& (h.trim().toUpperCase().compareTo("YES")==0))
 						hashable.put(DocName,d);
 				}
 			}catch(Exception e){
 				//System.out.println("~document.Document::loadDocument \n\t"+e);
 				//e.printStackTrace();
 				//System.exit(0);
 				throw e;
 			}
 			return d;
 		}catch(Exception e){
 			e.printStackTrace();
 			//System.out.println("~document.Document::loadDocument \n\t"+e);
 			GLOBAL.messag("BadDocument: "+e.getMessage(),true);
 			throw new Exception("BadDocument: "+e.getMessage());
 		}

    	 
     }   

	/**
	 * вставть компоненты в документ
	 */
	void insertObjects(Object[] objs,String buttons_desc,boolean bot) throws Exception{
		// objs должен содежать не болие двух элементов один из
		// которых визульный компонент а другой - DSCollection. Если
		// DSCollection отсутствует она создается по умолчанию
		try{
			//if (objs.length > 2) {
			//	GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_BADDOCUMENT,"Bad Document"),true);
			//	throw new Exception("Very mach objects");
			//}
			// Создаем панель документа
			panel = new Panel();
			Panel buttons = null; // кнопки описанные в buttons_desc
			Panel docpanel = new Panel(); // сам документ
			panel.setLayout(new BorderLayout());
			docpanel.setLayout(new GridLayout(1,1));
			Component doc = null;
			panel.add("Center",docpanel);
			// настройки для кнопок документов пропизываются в zeta.propers
			// парсим строку описания кнопок
			if (buttons_desc!= null) {
				buttons = new Panel();								GridBagLayout gbb = new GridBagLayout();				GridBagConstraints ccb = new GridBagConstraints();				ccb.weightx = 1.0;
				ccb.weighty = 0.0; 
				ccb.gridwidth = GridBagConstraints.REMAINDER;
				ccb.fill = GridBagConstraints.HORIZONTAL;				ccb.anchor = GridBagConstraints.NORTH;
				if (bot)//кнопки внизу					buttons.setLayout(new FlowLayout(FlowLayout.CENTER));				else buttons.setLayout(gbb);
				buttons.setFont(GLOBAL.font(GLOBAL.FONT_DOCUMENT));
				buttons.setBackground(GLOBAL.color(GLOBAL.CLR_DOCUMENT,GLOBAL.TEXTBG));
				buttons.setForeground(GLOBAL.color(GLOBAL.CLR_DOCUMENT,GLOBAL.TEXTFG));
				boolean fdok = false;
				boolean fsave = false;
				boolean fnew = false;
				boolean fhok = false;
				boolean fcancel = false;
				StringTokenizer st = new StringTokenizer(buttons_desc,",");
				String token;
				while(st.hasMoreTokens()){
					token = st.nextToken();
					if ( (!fdok) &&
						 (token.trim().toUpperCase().compareTo("D_OK")==0) )
						fdok = true;
					if ( (!fsave) &&
						 (token.trim().toUpperCase().compareTo("SAVE")==0) )
						fsave = true;
					if ( (!fhok) &&
						 (token.trim().toUpperCase().compareTo("HAND_OK")==0) )
						fhok = true;
					if ( (!fnew) &&
						 (token.trim().toUpperCase().compareTo("NEW")==0) )
						fnew = true;
					if ( (!fcancel) &&
						 (token.trim().toUpperCase().compareTo("CANCEL")==0) )
						fcancel = true;
				}				Component c = null;				boolean[] hlp = new boolean[]{fdok,fhok,fsave,fnew,fcancel};
				int lindex=-1;				for (int i=4;i>=0;i--) 					if (hlp[i]) {						lindex = i;						break;
					}						
				if (fdok){					c = new BTN(GLOBAL.pr(GLOBAL.DOC_BTN_DOK,"Ok"),
									this,ACT_DOK);					if (lindex==0) ccb.weighty=1.0;					if (!bot) gbb.setConstraints(c,ccb);
					buttons.add(c);
									}
				if (fhok){					c = new	BTN(GLOBAL.pr(GLOBAL.DOC_BTN_HOK,"Ok"),
									this,ACT_HOK);					if (lindex==1) ccb.weighty=1.0;					if (!bot) gbb.setConstraints(c,ccb);
					buttons.add(c);									}
				if (fsave){					c = new	BTN(GLOBAL.pr(GLOBAL.DOC_BTN_SAVE,"Save"),
									this,ACT_SAVE);
					if (lindex==2) ccb.weighty=1.0;					if (!bot) gbb.setConstraints(c,ccb);
					buttons.add(c);				}
				if (fnew){
					c = new	BTN(GLOBAL.pr(GLOBAL.DOC_BTN_NEW,"New"),
									this,ACT_NEW);					if (lindex==3) ccb.weighty=1.0;
					if (!bot) gbb.setConstraints(c,ccb);					buttons.add(c);				}
				if (fcancel){					c = new	BTN(GLOBAL.pr(GLOBAL.DOC_BTN_CANCEL,"Cancel"),
									this,ACT_CANCEL);
					if (lindex==4) ccb.weighty=1.0;					if (!bot) gbb.setConstraints(c,ccb);
					buttons.add(c);				}				
				if (bot) {
					GridBagLayout gb = new GridBagLayout();
					GridBagConstraints cc = new GridBagConstraints();
					Panel p = new Panel();
					p.setLayout(gb);		
					cc.weightx = 1.0;
					cc.gridwidth = GridBagConstraints.REMAINDER;
					cc.fill = GridBagConstraints.HORIZONTAL;
					gb.setConstraints(buttons,cc);	
					p.add(buttons);		
					statusPanel = new StatusPanel();
					gb.setConstraints(statusPanel,cc);				
					p.add(statusPanel);
					panel.add("South",p);				}else{					statusPanel = new StatusPanel();					panel.add("South",statusPanel);					panel.add("East",buttons);									}
			}else{
				statusPanel = new StatusPanel();				panel.add("South",statusPanel);
			}		

			// выбераем из objs коллекцию и дерево визуальных компоненто
			for(int i=0;i<objs.length;i++){
				//System.out.println(objs[i]);
				if (objs[i] instanceof dbi.DSCollection) {
					collection = (dbi.DSCollection)objs[i];
				}
				if (objs[i] instanceof Component) {
					doc = (Component)objs[i];
					docpanel.add(doc);
				}
			}

			// если визуального компонета не оказало
			if (doc == null) throw new Exception("Must be one Visual Component!");

			// если в документе нет коллекц
			if (collection == null) {
				collection = new dbi.DSCollection();
                                aliases.put("###COLLECTION###",collection);
				collection.setNames(aliases);
			}

			// собераем все DATASTORE  в коллекцию
			int count = aliases.size();
			Vector v = new Vector();
			for (Enumeration e = aliases.elements();e.hasMoreElements();){
				Object o = e.nextElement();
				//if(GLOBAL.dstore_debug>0)
					//System.out.println("document.Document.insertObjects o="+o);
				if ((o instanceof dbi.DATASTORE) && (o!=collection)) {
					if(GLOBAL.dstore_debug>1)
						System.out.println(
							"document.Document.insertObjects FOUND DATASTORE!!!");
					v.addElement(o);
				}
			}
			dbi.DATASTORE[] dss = new dbi.DATASTORE[v.size()];
			v.copyInto(dss);
			collection.setDatastores(dss);
			//System.out.println("before reset");
			//collection.reset();
			//System.out.println("after reset");

//////// !!!!!!!!!!!!!!!!!!!!
			maincomponent = doc;
			if ( resetIt ){
                resetIt = false;
				reset();
			}
			else
				((Retrieveable)doc).retrieve();
			//collection.reset();
			//System.out.println("after");
		}finally{
			// на всякий случай убедимся что закрыли окно сообщения о загрузке
			GLOBAL.waitout();
		}
	}		public static void recur(Proper prop, int rec,Properties p,Hashtable h){
		//StringBuffer sb = new StringBuffer();
		//for ( int i=0; i<rec ; ++i) sb.append("    ");
		//String s = sb.toString();
		Proper foo = prop;
		while ( foo != null ){
			//if ( rec != 0 ) sbb.append(s+"{"); 
			//sbb.append(foo.tag+"\n");			//System.out.println("tag="+foo.tag);
			//Enumeration e = foo.hash.keys();			Hashtable h1 = (Hashtable)h.get(foo.tag);
			Hashtable h2 = (Hashtable)h.get("+"+foo.tag);			if (h1!=null){				Enumeration e = h1.keys();
			while(e.hasMoreElements()){
				Object o = e.nextElement();
				Object o1 = foo./*hash.*/get(o);				if (o1==null){					foo./*hash.*/put(o,h1.get(o));				}
				} 			}			//System.out.println("h2="+h2);
			//System.out.println("h="+h);			if (h2!=null){				Enumeration e = h2.keys();
			while(e.hasMoreElements()){
				Object o = e.nextElement();
				//Object r = 
						foo./*hash.*/put(o,h2.get(o));
				//if (r!=null)System.out.println("ret="+r);				
				}			}
			recur(foo.content,rec+1,p,h);
			//if ( rec != 0 ) sbb.append(s+"}"); 
			//sbb.append("\n");
			foo = foo.next;
		}
	}


	/*
	 * dialog manipulation
	 */

		public void messag(String messag,boolean error){
			GLOBAL.messag(messag, error);
		}

		public boolean sure(String messag,boolean error){
			return GLOBAL.sure(messag,error);
		}

		

	
	
	
// implementaion of GlobalValuesObject

	public void setValue(Object obj) throws Exception{}
    public Object getValue() throws Exception{return this;}
    public void setValueByName(String name, Object obj) throws Exception {}
    public Object getValueByName(String name) throws Exception {return null;}
    
    
    

//implementaion of class_method

	public Object method(String method,Object arg) throws Exception{
		if ( method.equals("PATH") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@PATH must called with out arguments");*/

			return mypath;
		}else if ( method.equals("NAME") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@NAME must called with out arguments");*/
			return myname;
		}else if ( method.equals("ACTSAVE") ){
                        return new Double(processAction(ACT_SAVE));
		}else if ( method.equals("ACTCANCEL") ){
                        return new Double(processAction(ACT_CANCEL));
		}else if ( method.equals("ACTDOK") ){
                        return new Double(processAction(ACT_DOK));
		}else if ( method.equals("ACTHOK") ){
                        return new Double(processAction(ACT_HOK));
		}else if ( method.equals("ACTNEW") ){
                        return new Double(processAction(ACT_NEW));
		}else if ( method.equals("DOACTION") ){
			if ( !(arg instanceof String))
				throw new RTException("CastException",
				"DOCUMENT@DOACTION must called with one String argument");
			try{
				//Hashtable ali = OP.getAliases();
				//OP.setAliases(new Hashtable());
				try{
					ACTION.doAction((String)arg,(Hashtable)aliases.clone(),null);
				}catch(Exception e){
					throw e;
				}finally{
					//OP.setAliases(ali);
				}

			}catch(Exception e){
				throw new RTException("EXCEPTION",e.toString());
			}
			return new Nil();
		}else if ( method.equals("SERVERPATH") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@SERVERPATH must called with out arguments");*/
			return GLOBAL.pr(GLOBAL.DOC_SERVER);
		}else if ( method.equals("GETALIASES") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@SERVERPATH must called with out arguments");*/
			return aliases;
		}else if ( method.equals("WAITIN") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@SERVERPATH must called with out arguments");*/
			GLOBAL.waitin();
			return new Double(0);
		}else if ( method.equals("WAITOUT") ){
/*			if ( arg!=null )
				throw new RTException("CastException",
				"DOCUMENT@SERVERPATH must called with out arguments");*/
			GLOBAL.waitout();
			return new Double(0);
		}else if ( method.equals("LOAD") ){
			if ( !(arg instanceof String))
				throw new RTException("CastException",
				"DOCUMENT@LOAD must called with one String argument");
			try{
				char[] text =
					((Loader)aliases.get("_DATALOADER_")).
					loadByName_chars((String)arg,true);
				return rml.Parser.createProper(text,null);
			}catch(Exception e){
				throw new RTException("LoadException",
									  "Error load document");
			}
		}else if ( method.equals("PARSE") ){
			if ( !(arg instanceof String))
				throw new RTException("CastException",
				"DOCUMENT@PARSE must called with one String argument");
			try{
				return rml.Parser.createProper(((String)arg).toCharArray(),null);
			}catch(Exception e){
				throw new RTException("LoadException",
									  ""+e);
			}
                }else if(method.equals("READFILE") ){
                        try{
                              FileInputStream fis = new FileInputStream((String)arg);  
                              //DataInputStream dis = new DataInputStream(fis);
                              //String s = dis.readLine();
                              byte[] b = new byte[fis.available()];
                              fis.read(b);
                              fis.close();
                              return new String(b);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("SAVEFILE") ){
                        try{
                              Vector v = (Vector)arg;
                              FileOutputStream fos = new FileOutputStream((String)v.elementAt(0)); 
                              //DataOutputStream dos = new DataOutputStream(fos);
                              //dos.writeUTF((String)v.elementAt(1));
                              fos.write(((String)v.elementAt(1)).getBytes());
                              fos.close();
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("SHELL") ){
                        try{
                              Runtime rt = Runtime.getRuntime();
                              rt.exec((String)arg);
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("SHELLWAIT") ){
                        try{
                              Runtime rt = Runtime.getRuntime();
                              Process p = rt.exec((String)arg);
                              p.waitFor(); 
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("FILEDIALOG") ){
                        try{
                              int mode=FileDialog.SAVE;
                              if(((String)arg).equalsIgnoreCase("load"))
                              { mode=FileDialog.LOAD;}
                              else { mode=FileDialog.SAVE;}

                              FileDialog fd = new FileDialog(new Frame(), "", mode);
                              fd.show(); 
                              String s[] = {fd.getDirectory(), fd.getFile()};
                              return s;
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("IMPORT") ){
                        try{
                              Vector v = (Vector)arg;
                              FileInputStream fis = new FileInputStream((String)v.elementAt(0));  
                              byte[] b = new byte[fis.available()];
                              fis.read(b);
                              fis.close();
                              PackerSave ps = new PackerSave(b);
                              dbi.DATASTORE.saveObject((String)v.elementAt(1),ps);
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("EXPORT") ){
                        try{
                              Vector v = (Vector)arg;
                              PackerRead pr = new PackerRead();
                              Object o = dbi.DATASTORE.readObject((String)v.elementAt(1),pr);
                              FileOutputStream fos = new FileOutputStream((String)v.elementAt(0)); 
                              fos.write((byte[])o);
                              fos.close();
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }
                }else if(method.equals("RUSER") ){
                        try{
                              return new Double(0);
                        }catch(Exception e){
                                e.printStackTrace();
                                throw new RTException("RunTime",e.getMessage());
                        }

                }else if(method.equals("SURE") ){
                        Vector v = (Vector)arg;
                        String msg = (String)v.elementAt(0);
                        Double val = (Double)v.elementAt(1);
                        boolean b = GLOBAL.sure(msg, val.intValue() == 1);
                        return new Double(b?1:0);
                }else throw new RTException("HasMethodException",
									"object DOCUMENT has not method "+method);
	}
	
	public String getPage(){
		Proper p = (Proper)aliases.get("###propers###");
		if (p!=null) return (String)p.get("HTML_PAGE");
		else return null;
	}
	
	public String type() throws Exception{
		return "DOCUMENT";
	}
	public String toString(){
		return "DOCUMENT "+mypath+"/"+myname;
	}

}

