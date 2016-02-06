
/*
 * File: LFIS.java
 *
 * Created: Tue Mar 23 16:00:18 1999
 *
 * Copyright(c) by Almanex Tec. 
 *
 * Author: Alexey Chen
 */

package loader;

import java.io.*;
import java.nio.charset.Charset;


public class LFIS extends InputStream{
	//FileReader fr;
	InputStreamReader isr;
	static final Charset cs = Charset.forName("cp1251");
	public LFIS(String fname) throws IOException{
		super();
		FileInputStream fr = new FileInputStream(fname);
		isr = new InputStreamReader(fr,cs);
	}
	public int read() throws IOException{
		return isr.read();
	}
}
