
/*
 * File: DBStateBroker.java
 *
 * Created: Fri Jul  2 13:27:41 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package document;

import java.util.*;
import dbi.*;
import java.io.*;

public class  DBStateBroker {
	Hashtable rtable = new Hashtable();
	Hashtable brokers = new Hashtable();
	Document doc;
	String other = null;
	class shell{
		public shell(){}
		public shell(Object o) {object = o;}
		public Object object;
	}
	class Writer implements loader.Callback{
		shell state;
		public Writer(shell s){
			state = s;
		}
		public Object callback(Object o) throws Exception{
			System.out.println("writer callback "+state.object);
			try{
				ObjectOutputStream oos = new ObjectOutputStream((OutputStream)o);
				oos.writeObject(state.object);
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			return null;
		}
	}
	class Reader implements loader.Callback{
		shell state;
		public Reader(shell s){
			state = s;
		}
		public Object callback(Object o) throws Exception{
			try{
				ObjectInputStream ois = new ObjectInputStream((InputStream)o);
				state.object = (State)ois.readObject();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			System.out.println("reader callback "+state.object);
			return null;
		}
	}

	public State registre(StateSaver ss, String key) throws Exception{
		if (rtable.containsKey(key)){
			throw new Exception(
				"~~~~DBStateBroker:: this key ("+key+") already registred !");
		}else{
			shell state = new shell();
			rtable.put(key,ss);
			try{
				String dskey = DATASTORE.key(doc.mypath+"/"+doc.myname+other+"##"+key);
				DATASTORE.readObject(dskey,(loader.Callback)new Reader(state));
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return (State)state.object;
		}
	}

	public void registreBroker(DBStateBroker dbsbroker){
		brokers.put((Object)dbsbroker,"");
	}

	public void uregistreBroker(DBStateBroker dbsbroker){
		brokers.remove((Object)dbsbroker);
	}
	
	public void saveall() {
		for (Enumeration en = rtable.keys();en.hasMoreElements();){
			try{
				State state = new State();
				String key = (String)en.nextElement();
				((StateSaver)rtable.get(key)).saveState(state);
				String dskey = DATASTORE.key(doc.mypath+"/"+doc.myname+other+"##"+key);
				DATASTORE.saveObject(dskey,
									  (loader.Callback)new Writer(new shell(state)));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		for (Enumeration en = brokers.keys();en.hasMoreElements();){
			try{
				DBStateBroker dbsbroker =
					(DBStateBroker)en.nextElement();
				dbsbroker.saveall();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	public DBStateBroker(Document d){
		doc = d;
		other = "";
	}
	public String getOtherForKey(){
		return other;
	}
	public DBStateBroker(Document d,String _other){
		doc = d;
		other = "*"+_other+"*";
	}
}









