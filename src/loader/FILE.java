
/*
 * File: FILE.java
 *
 * Created: Mon Mar 22 08:50:57 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;

import java.net.URL;
import java.io.*;
import sun.io.*;
import java.util.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FILE implements Protocol{

	String root = "/";
	
	public void init(URL rul) throws Exception{
		root = "/"+rul.getFile()+"/";
	}

	public Object extendFunc(String s,Object o) throws Exception{
		if (s.equals("DIR")){
			File f = new File(root+((String)o));
			String[] list = f.list();
		    char[] types = new char[list.length];
			for (int i=0;i<list.length;++i) {
				File ff = new File(f,list[i]);
				if (ff.isDirectory()) types[i] = 'D';
				else types[i] = 'F';
			}
			Object[] r = {list,types};
			return r;
		}else 
		if (s.equals("WRITE")){
			String name  = null;
			String text  = "";
			rml.Proper p = null;
			if ( o instanceof Vector ){
				if ( ((Vector)o).elementAt(0) instanceof rml.Proper ){
					p = (rml.Proper)((Vector)o).elementAt(0);
				}else{
					text = ((Vector)o).toString();
				}
				name = (String)((Vector)o).elementAt(1);
			}else{
				if ( o instanceof rml.Proper ){
					p = (rml.Proper)o;
				}else{
					text = o.toString();
				}
			}
			if (name == null) {
				
			}
			if ( p!= null ){
				
			}
		}else 
		if (s.equals("REMOVE")){
		}else 
		if (s.equals("MKDIR")){
		}else 
		throw new ProtoFunctionNotImplemented();
		return null;
	}

	public char[] getByName_chars(String path,boolean enc)
		throws Exception{
		try{
			byte[] b = getByName_bytes(path);
			char[] text = new char[b.length];
			if (enc) {
				int foo = 0;
				for(;(foo<b.length)&&(b[foo]!='\n')&&(b[foo]!='\r');++foo){}

				String encoding = new String(b,0,foo);
				
				if (GLOBAL.protocol_debug>0)
					System.out.println("~loader.FILE::getByName_chars \n\t"+
									   "Document encoding "+encoding);
			
				/*ByteToCharConverter c =
					ByteToCharConverter.getConverter(encoding);*/
				
				CharsetDecoder c = Charset.forName(encoding).newDecoder(); //add
				ByteBuffer bb = ByteBuffer.wrap(b, foo+1, b.length- (foo+1));//add
				CharBuffer cb = CharBuffer.wrap(text);//add

				//c. convert(b,foo+1,b.length,text,0,text.length);
				
				c.decode(bb, cb, true);
				
			}else{
				for (int i=0;i<text.length;++i){
					text[i] = (char)b[i];
				}
			}
			return text;
			
		}catch(Exception e){
			if(GLOBAL.loader_exception)
				e.printStackTrace();
				System.out.println(
					GLOBAL.PRINT_GREEN+
					"~loader.FILE::getByName_chars exception \n\t"+
					e+
					GLOBAL.PRINT_NORMAL);

			throw new Exception();
		}
	}
	

	public void write(String file,String encoding,char[] text) throws Exception {
		
		 write(file, encoding, new String(text)); //TODO тут возможно нужно поработать с кодировкой
	}
	
	public void write(String file,String encoding,String text)
		throws	Exception{
		try{
			if ( encoding == null ) encoding = "KOI8_R";
			byte[] data = text.getBytes(encoding);
			File f = new File(root+file);
			FileOutputStream fs = new FileOutputStream(f);
			fs.write(encoding.getBytes());
			fs.write('\n');
			fs.write(data);
			fs.close();
		}catch(Exception e){
			e.printStackTrace();
			if(GLOBAL.loader_exception)
				System.out.println(
					GLOBAL.PRINT_GREEN+
					"~loader.FILE::write exception \n\t"+
					e+
					GLOBAL.PRINT_NORMAL);

			throw new Exception();
		}

	}
	
	public char[] getByName_chars(String path) throws Exception{
		return getByName_chars(path,false);
	}

	public byte[] getByName_bytes(String path) throws Exception{
		try{
			if (GLOBAL.protocol_debug>0)
				System.out.println(GLOBAL.PRINT_BLUE+
								   "FILE "+root+path+
								   GLOBAL.PRINT_NORMAL);
			File f = new File(root+path);
			FileInputStream fs = new FileInputStream(f);
			long flen = f.length();
			byte[] text = new byte[(int)flen];
			if ( fs.read(text) != text.length)
				throw new Exception("NotCorrectly readed file \n\t"+root+path);
			fs.close();
			return text;
		}catch(Exception e){
			if (GLOBAL.loader_exception)
				System.out.println(
					GLOBAL.PRINT_RED+
					"~loader.FILE::getByName Exception: error reading file"+path+" \n\t"+
					GLOBAL.PRINT_NORMAL+e);
			throw new Exception("~loader.FILE::getByName get file! error reading file"+path);
		}
	}
	
	public void down(){
	}

	public void dump_state(){
		if (GLOBAL.protocol_debug>0)
			System.out.println("~loader.FILE::dump_state\n\t"+
							   "FILE protocol handler! \n\troot dir is \n\t"+
							   root);
	}
}
