
/*
 * File: CLoader.java
 *
 * Created: Mon Mar 22 13:24:01 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;

import java.util.*;

public class CLoader extends ClassLoader{
	Loader loader;
	Hashtable cache = new Hashtable();
	Hashtable nafing = new Hashtable();
	public CLoader(Loader loader){
		this.loader = loader;
	}

	public static String CName2FName(String s){
		return s.replace('.','/'/*System.getProperty("file.separator","/").charAt(0)*/)+".class";
	}
	
	byte[] loadClassData(String name) throws Exception{
		return loader.loadByName_bytes(CName2FName(name));
	}
	
	protected Class loadClass(String name, boolean resolve)
		throws ClassNotFoundException{
		try{
			if (GLOBAL.loader_debug>1)
				System.out.println(GLOBAL.PRINT_GREEN+
							   "~loader.CLoader::loadclass loading class "+
							   name+
							   GLOBAL.PRINT_NORMAL);
             Class c = (Class)cache.get(name);
             if (c == null) {
				 if ( nafing.containsKey(name) ) {
					 //System.out.println("~~~~~~~~Class "+name+" not found");
					 throw new Exception();
				 }
				 try{
					 c = findSystemClass(name);
				 }catch(ClassNotFoundException e){
					 byte data[];
					 try{
						 data = loadClassData(name);
					 }catch(Exception ecc){
						 //System.out.println("~~~~~~~~Class "+name+" not loaded");
						 nafing.put(name,"");
						 throw ecc;
					 }
					 c = defineClass(data, 0, data.length);
					 if (Compiler.compileClass(c)) {
						 if (GLOBAL.loader_debug>1)
							 System.out.println(GLOBAL.PRINT_BLUE+
							   "~loader.CLoader::loadclass success compiling class "+
												name+
												GLOBAL.PRINT_NORMAL);
					 }else{
						 if (GLOBAL.loader_debug>1)
							 System.out.println(GLOBAL.PRINT_RED+
							   "~loader.CLoader::loadclass error compiling class "+
												name+
												GLOBAL.PRINT_NORMAL);
					 }
					 cache.put(name, c);
				 }

             }
             if (resolve)
                 resolveClass(c);
             return c;
		}catch(Exception ec){
			/*if (GLOBAL.loader_exception) 
				System.out.println(
					GLOBAL.PRINT_RED+
					"~loader.CLoader::loadClass exception \n\t"+
					GLOBAL.PRINT_NORMAL+ec);*/
			throw new ClassNotFoundException("class "+name);
		}catch(Error err){
			System.out.println("Error with loading class: \n\t" + name);
			throw err;
		}

	}
	public void dump_state(){
		try{
			if (GLOBAL.loader_debug==0) return;
			System.out.println("~loader.CLoader::dump_state \n");
			Enumeration e = cache.keys();
			while(e.hasMoreElements()){
				Object o = e.nextElement();
				System.out.println("\t"+o+" = "+cache.get(o));
			}
			loader.dump_state();			
		}catch(Exception e){			
		}
	}
}
         
 
