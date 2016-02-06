
/*
 * File: HTTP.java
 *
 * Created: Thu Mar 25 10:20:35 1999
 *
 * Copyright(c) by Almanex Tec. 
 *
 * Author: Alexey Chen
 */

package loader;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.io.*;
import java.util.*;

import sun.io.*;


public class HTTP implements  Protocol{
	final static int BUFFER_LEN = 2048;
	class Buffer{
		int len = 0;
		byte[] b = null;
		Buffer next = null;
		public Buffer(){
			b = new byte[BUFFER_LEN];
		}
		public int sumlen(){
			return (next == null) ? len : len+next.sumlen();
		}
	}


	URL url;

	public void init(URL url) throws Exception{
		this.url = url;
		Socket sock=connect(url);
		sock.close();
	}

	public Object extendFunc(String s,Object o) throws Exception{
		throw new ProtoFunctionNotImplemented();
	}
	
	Socket connect(URL url) throws Exception{
		try{
			int port = url.getPort();
			return new Socket(url.getHost(),(port > 0)?port:80);
		}catch(UnknownHostException e){
			System.out.println("~loader.HTTP::init \n\t"+
							   GLOBAL.PRINT_RED+
							   e+
							   GLOBAL.PRINT_NORMAL);
			throw new Exception();
		}catch(IOException e){
			System.out.println("~loader.HTTP::init \n\t"+
							   GLOBAL.PRINT_RED+
							   e+
							   GLOBAL.PRINT_NORMAL);
			throw new Exception();
		}
	}
	
	public char[] getByName_chars(String path)
		throws Exception{
		return getByName_chars(path,false);
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
				
				if(GLOBAL.protocol_debug>1)
				System.out.println("~loader.HTTP::getByName_chars \n\t"+
							   "Document encoding "+encoding);
			
				//ByteToCharConverter c =	ByteToCharConverter.getConverter(encoding);
			
				CharsetDecoder c = Charset.forName(encoding).newDecoder(); //add
				
				//System.out.println(c.getCharacterEncoding());
				//c. convert(b,foo+1,b.length, text,0,text.length);
				
				ByteBuffer bb = ByteBuffer.wrap(b, foo+1, b.length -(foo+1));//add
				CharBuffer cb = CharBuffer.wrap(text);//add

				//c. convert(b,foo+1,b.length,text,0,text.length);
				
				c.decode(bb, cb, true);
				
				if(GLOBAL.protocol_debug>1)
					System.out.println(new String(text));
			}else{
				for (int i=0;i<text.length;++i){
					text[i] = (char)b[i];
				}
			}
			return text;
			
		}catch(Exception e){
			if (GLOBAL.loader_exception)
				System.out.println(
					GLOBAL.PRINT_GREEN+
					"~loader.HTTP::getByName_chars exception \n\t"+
					e+
					GLOBAL.PRINT_NORMAL);

			throw new Exception();
		}
	}

	
	public byte[] getByName_bytes(String path) throws Exception{
		Socket sock = null;
		String request = null;
		boolean flag = false;
		try{
			byte[] b;
			Buffer buf = new Buffer();
			Buffer endbuf = buf;

			sock = connect(url);
			DataInputStream ds = new DataInputStream(sock.getInputStream());
			PrintWriter ps = new PrintWriter(sock.getOutputStream());

			request = "GET "+url.getFile()+"/"+path+" HTTP/1.0";
			if(GLOBAL.protocol_debug>0)
				System.out.println(GLOBAL.PRINT_BLUE+
								   request+
								   GLOBAL.PRINT_NORMAL);
			ps.print(request+"\n\n");
			ps.flush();

			int q,len=0;

			while ((q=ds.read(endbuf.b))!=-1){
				endbuf.len = q;
				//System.out.print(new String(endbuf.b,0,endbuf.len));
				endbuf.next = new Buffer();
				endbuf=endbuf.next;
				len+=q;
			}

			Buffer bf = buf ;
			int hlen = 0;
			StringBuffer header = new StringBuffer("");
			boolean bflag=false;
			boolean endl=false;
		  loop1:
			while (bf!=null){
				for (int i=0;i<bf.len;++i,++hlen){
					if (bf.b[i] == '\n') {
						bflag = true;
						if (endl) break loop1;
						else endl=true;
					}
					else if (bf.b[i] == '\r'){
						continue;
					}
					else {
						if(!bflag) header.append((char)bf.b[i]);
						endl = false;
					}
				}
			}
			
			++hlen;
			bf = bf.next;
			
			String bfs = new String(header);

//			System.out.println("~loader.HTTP::getByName_bytes replay is\n\t"+
//				bfs);
			
			StringTokenizer st = new StringTokenizer(bfs);
			String token = st.nextToken();
			if (!token.startsWith("HTTP"))
				throw new Exception("Not HTTP server ..."+token+"...");
			token = st.nextToken();
			if (token.compareTo("200")!=0)
				throw new Exception(GLOBAL.PRINT_BLUE+
									"Error "+token+" "+
									st.nextToken("")+
									GLOBAL.PRINT_NORMAL);
			
			if(GLOBAL.protocol_debug>1)
				System.out.println("loading "+(len-hlen)+"b  header "+hlen+"b");

			b = new byte[len];

			int p = 0;
			while(buf!=null){
				if ( hlen < buf.len ){
					p=buf.len-hlen;
					System.arraycopy(buf.b,hlen,b,0,p);
					buf = buf.next;
					break;
				}else{
					hlen -= buf.len;
					buf = buf.next;
				}
			}
			while(buf!=null){
				System.arraycopy(buf.b,0,b,p,buf.len);
				p+=buf.len;
				buf = buf.next;
			}
			sock.close();

			return b;
		}catch(Exception e){
			if (GLOBAL.loader_exception)
				System.out.println(
					"\n"+
					GLOBAL.PRINT_RED+
					"~loader.HTTP::getByName_bytes exception\n\t"+
					GLOBAL.PRINT_BLUE+request+"\n\t"+
					GLOBAL.PRINT_NORMAL+e+"\n");
			//e.printStackTrace();
			flag = true;
			return null;
		}finally{
			if (sock!=null) sock.close();
			if (flag) throw new Exception();
		}
	}

	public void down(){
	}
	public void dump_state() throws Exception{
	}
	
	public void write(String file,String encoding,char[] text) throws Exception {
		 
	}
}


