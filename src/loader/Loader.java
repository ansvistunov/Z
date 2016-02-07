
/*
 * File: Loader.java
 *
 * Created: Mon Mar 22 08:41:51 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;

import java.io.*;
import java.net.*;
import java.util.*;


public class Loader {

	Protocol pro;
	URL url;

	public Loader (String hp) throws Exception{
		hp = hp.trim();
		try{
			URL url = new URL(hp);
			this.url = url;
			String proto = url.getProtocol();
			if(GLOBAL.loader_debug>0)
				System.out.println(GLOBAL.PRINT_BLUE+
								   "~loader.Loader::constructor \n\t"+
								   "URL "+url+
								   GLOBAL.PRINT_NORMAL);
			Class cl = Class.forName("loader."+proto.toUpperCase());
			pro = (Protocol)cl.newInstance();
			pro.init(url);
		}catch(Exception e){
			System.out.println(GLOBAL.PRINT_RED+
							   "~loader.Loader::constructor exception \n\t"+
							   GLOBAL.PRINT_NORMAL+e);
			throw new Exception();
		}
	}

	public void write(String file,String encoding,char[] text) throws Exception {
		
		 pro.write(file, encoding, text); //TODO тут возможно нужно поработать с кодировкой
	}
	
	public byte[] loadByName_bytes(String name) throws Exception{
		//GLOBAL.waitin();
		GLOBAL.waitwin_text(url+"/"+name);
		try{
			byte[] b = pro.getByName_bytes(name);
			return b;
		}catch(Exception e){
			/*if ( GLOBAL.loader_exception )
				System.out.println(
					GLOBAL.PRINT_RED+
					"~loader.Loader::loadByName_bytes exception \n\t"+
					GLOBAL.PRINT_NORMAL+e);*/
			throw new Exception("~loader.Loader::loadByName_bytes loading");
		}finally{
			GLOBAL.waitout();
		}
	}

	
	public char[] loadByName_chars(String name,boolean enc) throws Exception{
		try{
			char[] text = pro.getByName_chars(name,enc);
			return text;
		}catch(Exception e){
			/*if ( GLOBAL.loader_exception )
				System.out.println(
					GLOBAL.PRINT_RED+
					"~loader.Loader::loadByName_chars exception \n\t"+
					GLOBAL.PRINT_NORMAL+e);*/
			throw new Exception("~loader.Loader::loadByName_chars loading. Error reading file "+name);
		}
	}
	public char[] loadByName_chars(String name) throws Exception{
		return loadByName_chars(name,false);
	}
	public void dump_state(){
		try{
			System.out.println(GLOBAL.PRINT_RED+
							 "~loader.Loader::dump_state asign with URL \n\t"+
							   GLOBAL.PRINT_BLUE+
							   url+GLOBAL.PRINT_NORMAL);
			pro.dump_state();
		}catch(Exception e){
		}
	}
	public Object extendFunc(String name,Object arg) throws Exception{
		return pro.extendFunc(name,arg);
	}
}


