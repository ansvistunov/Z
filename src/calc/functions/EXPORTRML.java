
/*
 * File: EXPORTRML.java
 *
 * Created: Mon Jun 21 12:32:40 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;
import calc.*;
import java.awt.*;
import loader.GLOBAL;
import rml.Proper;
import java.util.*;
public class EXPORTRML extends BaseExternFunction{
	static final String MSG_EXPORT_ERROR = "msg.importrml.error";
	static String path = null;
	public Object eval() throws Exception{
		try{
			Object o = expr.eval();
			String file = null;
			Proper p = null;
			try{
				if (o instanceof Vector){
					p = (Proper)((Vector)o).elementAt(0);
					file = (String)((Vector)o).elementAt(1);
				}else 
					p = (Proper)o;
			}catch(Exception e){
				throw new RTException("CastException",
			    	  "EXPORTRML must have 2 args (Proper,String) or only Proper");
			}
			Frame f = new Frame();
			FileDialog fd = new FileDialog(f,"",FileDialog.SAVE);
			if ( path != null ){
				fd.setDirectory(path);
			}
			if ( file != null ){
				int i = path.lastIndexOf('/');
				if (i >= 0 ){
					fd.setDirectory(file.substring(0,i+1));
					fd.setFile(file);
				}else {
					fd.setDirectory(path);
					fd.setFile(path+file);
				}
			}else{
				try{
					String foo1 = (String)p./*hash.*/get("###path###");
					if (foo1 != null) fd.setDirectory(foo1);
					String foo = (String)p./*hash.*/get("###file###");
					if (foo != null) fd.setFile(foo1+foo);
				}catch(Exception e){
				}
			}
			while(true){
				fd.show();
				path = fd.getDirectory();
				file = fd.getFile();
				if (file == null )
					throw new RTException(
						"Cancel",
						"");
					
				//System.out.println("path is "+path);
				//System.out.println("file is "+file);
				java.net.URL url = new java.net.URL("file:///"+path);
				loader.FILE fl = new loader.FILE();
				fl.init(url);
				String encoding = (String)p./*hash.*/get("ENCODING");
				if (encoding == null) {
					encoding = "KOI8_R";
					p./*hash.*/put("ENCODING",encoding);
				}
				try{
					fl.write(file,encoding,p.toText());
				}catch(Exception e){
					GLOBAL.messag(
						((String)GLOBAL.pr(MSG_EXPORT_ERROR,"Error write file "))+path+file,
						true);
					continue;
				}
				p./*hash.*/put("###path###",path);
				p./*hash.*/put("###file###",file);
				return new Double(1);
			}
		}finally{
		}
	}
}

