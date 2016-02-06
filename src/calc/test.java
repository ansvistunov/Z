
/*
 * File: test.java
 *
 * Created: Mon Apr 26 07:58:42 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.io.*;
import loader.GLOBAL;
import java.util.*;


public class test{
	public static void main(String s[]){
		GLOBAL.calc_debug=0 ;
		try{
			Hashtable h = new Hashtable();
			File f = new File(s[0]);
			FileReader fis = new FileReader(f);
			char[] text = new char[(int)f.length()];
			fis.read(text,0,text.length);
			Calc c = new Calc(new String(text));
			String ali[] = c.getAliases();
			if ( ali != null )
				if ( ali.length!=0 )
					System.out.println(OP.printArray(ali));
			else
				System.out.println("Expression has not any aliases");
			StringBuffer ss = new StringBuffer();
			Object[] r = c.eval(h);
			for (int i =0; i < r.length; ++i ){
				if ( r[i] instanceof Object[] )
					ss.append(" ").append(OP.printArray((Object[])r[i]));
				else ss.append(" ").append(r[i]);
			}
			System.out.println("result:  "+ ss);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}




