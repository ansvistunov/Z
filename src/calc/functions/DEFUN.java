
/*
 * File: DEFUN.java
 *
 * Created: Tue Apr 27 10:15:53 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;
import java.util.*;

public class DEFUN extends NullExternFunction{
	static final String fun = "FUN DEFUN : "; 
	Tree func = null;
	String name = null;

	public Object eval() throws Exception{
		OP.functions.put(name,func);
		return new Double(1);
	}
	public void init(String arg) throws Exception{
		int p1 = arg.indexOf('<');
		int p2 = arg.indexOf('>');
		name = arg.substring(0,p1).trim().toUpperCase();
		String s = arg.substring(p1+1);
		StringTokenizer st = new
		   StringTokenizer(
			   s.substring(0,s.indexOf('>')),
			   ",");
		Tree a = new Tree();
		Tree foo = a;
		try{
			foo.left = st.nextToken().trim().toUpperCase();
			while(st.hasMoreTokens()){
				foo.right = new Tree();
				foo = (Tree)foo.right;
				foo.left = st.nextToken().trim().toUpperCase();
			}
		}catch(Exception e){
		}
		
		func = new Tree(
			Parser.parse1(arg.substring(p2+1).toCharArray()),
			a);
	}
}

