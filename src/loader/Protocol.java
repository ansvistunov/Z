/*
 * File: Protocol.java
 *
 * Created: Mon Mar 22 08:48:43 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;
import java.net.URL;

public interface Protocol {
	public void init(URL host) throws Exception;
	public char[] getByName_chars(String path) throws Exception;
	public char[] getByName_chars(String path,boolean encoding)
		throws Exception;
	public byte[] getByName_bytes(String path) throws Exception;
	public void down();
	public void dump_state() throws Exception;
	public Object extendFunc(String func,Object arg) throws Exception;
	public void write(String file,String encoding,char[] text) throws Exception;
}
