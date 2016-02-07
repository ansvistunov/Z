
/*
 * File: IMPORTRML.java
 *
 * Created: Mon Jun 21 11:10:30 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;
import calc.*;
import java.awt.*;
import loader.GLOBAL;

public class IMPORTRML extends BaseExternFunction{
	static final String MSG_IMPORT_ERROR = "msg.importrml.error";
	static String path = null;
	public Object eval() throws Exception{
		try{
			Frame f = new Frame();
			FileDialog fd = new FileDialog(f,"",FileDialog.LOAD);
			if ( path != null ){
				fd.setDirectory(path);
			}
			String file = null;
			while(true){
				fd.show();
				path = fd.getDirectory();
				file = fd.getFile();
				if ( file == null) throw new RTException(
					"Cancel",
					"");
				System.out.println("@@@@:"+file);
				java.net.URL url = new java.net.URL("file:///"+path);
				loader.Protocol proto = new loader.FILE();
				proto.init(url);
				try{
					char[] text = proto.getByName_chars(file,true);
					rml.Proper p =  rml.Parser.createProper(text,null);
					p./*hash.*/put("###path###",path);
					p./*hash.*/put("###file###",file);
					return p;
				}catch(Exception e){
					GLOBAL.messag(
						((String)GLOBAL.pr(MSG_IMPORT_ERROR,"Error read file "))+
						path+file,
						true);
					continue;
				}
			}
		}finally{
		}
	}
}

