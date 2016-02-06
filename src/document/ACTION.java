
/*
 * File: ACTION.java
 *
 * Created: Thu Apr  8 16:59:07 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package document;

import java.util.*;
import views.Retrieveable;
import dbi.DATASTORE;
import calc.*;
import java.text.*;

public class ACTION{
	static final int RETRIEVE = 0;
	static final int CREATE = 1;
	static final int OPEN = 2;
	static final int EXECUTE = 3;
	static final int EXECEXPR = 4;	

	class Command{
		int cmd = -1;
		boolean newf = false;
		boolean overf = false;
		String target = "";
		Object[] arg = null;
		String args = "";
		public Command next = null;
		public Command(String s,Hashtable aliases) throws Exception{			
			StringTokenizer st = new StringTokenizer(s);
			if ( st.countTokens() == 0 ) return;
			String str = st.nextToken().trim().toUpperCase();
			if ( str.compareTo("RETRIEVE") == 0 ){
				cmd = RETRIEVE;
			}else if ( str.compareTo("OPEN") == 0 ){
				cmd = OPEN;
			}else if ( str.compareTo("OPENNEW") == 0 ){
				cmd = OPEN;
				newf = true;
			}else if ( str.compareTo("OPENOVER") == 0 ){
				cmd = OPEN;
				overf = true;
				/*}else if ( str.compareTo("OPENNEWOVER") == 0 ){
				cdm = OPEN;
				newf = true;
				overf = true;*/
			}else if ( str.compareTo("CREATE") == 0 ){
				cmd = CREATE;
			}else if ( str.compareTo("CREATENEW") == 0 ){
				cmd = CREATE;
				newf = true;
			}else if ( str.compareTo("CREATEOVER") == 0 ){
				cmd = CREATE;
				overf = true;
				/*}else if ( str.compareTo("CREATENEWOVER") == 0 ){
				cmd = CREATE;
				newf = true;
				overf = true;*/
			}else if ( str.compareTo("EXECUTE") == 0 ){
				cmd = EXECUTE;
			}else if ( str.compareTo("EXECEXPR") == 0 ){
				cmd = EXECEXPR;
			}			
			else {
				throw new
					Exception("~document.ACTION$Command::<init>\n\t"+
						str+" unknown command");
			}
			target = st.nextToken().trim();
			try{
				args = st.nextToken("");
				StringTokenizer st1 = new StringTokenizer(args,",");
				int n = st1.countTokens();
				arg = new Object[n];
				try{
					for(int i = 0;st1.hasMoreTokens();++i){
						String a = st1.nextToken().trim();
						System.out.println("--------\n"+a);
						if ( a.length() == 0 ) arg[i] = a;
						else if (a.charAt(0) == '&') {
							arg[i] = aliases.get(a.substring(1));
							System.out.println("add object "+arg[i]+" in ARGUMENTS."+i);
						}else try{
							a.toUpperCase();
							arg[i] = (Object)Double.valueOf(a);
						}catch(Exception e){
							//System.out.println(e);
							try{
								arg[i] = (Object)DateFormat.getDateInstance().parse(a);
							}catch(Exception e1){
								//System.out.println(e1);
								arg[i] = (Object)a;
							}
						}
					}
				}catch(Exception e){
					//e.printStackTrace();
				}
				//for (int i=0;i<n;++i){
				//	arg[i] = Lisp.eval(st1.nextToken(),aliases);
				//}
			}catch(Exception e){
				//e.printStackTrace();
				//System.out.println("@@@"+e);
			}
		}

		public void doCmd(Hashtable aliases,Actioner actor) throws Exception{
			if (cmd == RETRIEVE){
				//System.out.println(aliases);
				Object tg = aliases.get(target.toUpperCase());
				if (tg instanceof Retrieveable){
					((Retrieveable)tg).retrieve();
				}else if ( tg instanceof DATASTORE ) {
					((DATASTORE)tg).retrieve();
				}
				actor.notifyActioner();
			}else if (cmd == EXECUTE) {
				dbi.EXECUTOR.execute(arg,aliases);
				actor.notifyActioner();
			}else if ((cmd == OPEN)||(cmd == CREATE)){
				if (cmd == CREATE)  Document.resetIt = true;
				if (newf){
					Document.callDocumentNewWindow(target,arg,aliases,actor);
				}else if (overf) {
					Document.callDocumentSomeWindow(target,arg,aliases,actor);
				}else {
					Document.callDocumentSomeWindow(target,arg,aliases,actor);
				}
			}else if (cmd == EXECEXPR) {
				String expr = null;
				try{
				//StringBuffer sb = new StringBuffer();
				//for (int i=0;i<arg.length;i++)
				//	expr+=(String)arg[i]+" ";
                                expr=(String)arg[0];
				//System.out.println("*** Expression="+expr);
				//System.out.println("arg.length="+arg.length);
				//for (int i=0;i<arg.length;i++){
				//	System.out.println("arg["+i+"]="+arg[i]);
				//}
				Calc c = new Calc(expr);
				c.eval(aliases);
				}
				catch(Exception e){System.out.println("Exception in ACTION.doCmd():"+e);
				    return;
				}
				actor.notifyActioner();
			}
			else if (cmd == -1) {
				return;
			}else throw new Exception("~document.ACTION$Command::doCmd "+cmd);
		}
	}

	class Action implements Actioner{
		Command cmd = null;
		Hashtable aliases;
		NotifyInterface ni;
		public Action(String s,Hashtable aliases,NotifyInterface ni)
			throws Exception{
			this.ni = ni;
			Command end = null;
			cmd = null;
			this.aliases = aliases;
			//System.err.println("*** string in action="+s);
			StringTokenizer st = new StringTokenizer(s,";");
			while(st.hasMoreTokens()){
				String str = st.nextToken();
				Command cd = new Command(str,aliases);
				if ( cmd == null ) end = cmd = cd;
				else {
					end.next = cd;
					end = cd;
				}
			}
		}
		public void doAction() throws Exception{
			if (cmd == null){
				if (ni!=null) ni.notifyIt();
				return;
			}
			Command cd = cmd;
			cmd = cmd.next;
			cd.doCmd(aliases,this);
		}
		public void notifyActioner(){
			try{
				doAction();
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
	}

	ACTION(){
	}

	void action(String action, Hashtable aliases, NotifyInterface ni)
		throws Exception{
		//System.out.println("~document.ACTION::doAction() begin \n\t"+action);
		try{
			action = Calc.macro(action,aliases);
			Action a = new Action(action,aliases,ni);
			a.doAction();
		}catch(Exception e){
			//e.printStackTrace();
			if ( e instanceof java.sql.SQLException )
				dbi.DATASTORE.er.addMessage(e.getMessage());
			throw e;
		}
		//System.out.println("~document.ACTION::doAction() end \n\t"+action);
	}

	public static void doAction(String action, Hashtable aliases, NotifyInterface ni)
		throws Exception{
		if ( (action == null) || (action.trim().length()==0) ) return;
		(new ACTION()).action(action,aliases,ni);
	}
}
