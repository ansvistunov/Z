
/*
 * File: ELSE.java
 *
 * Created: Mon Apr 26 13:58:07 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */


package calc.functions;

import calc.*;
import loader.GLOBAL;
import java.util.*;

public class ELSE implements ExternFunction {
	OP doing = null;
	
	public void getAliases(Hashtable h) throws Exception{
		doing.getAliases(h);
	}
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.ELSE::eval");
		return doing.eval();

	}
	public void init(String arg) throws Exception{
		Lexemator lex = new Lexemator(arg.toCharArray());
		lex.next();
			if (GLOBAL.calc_debug > 2)
				System.out.println("~clac.funcions.ELSE::init LEXPR");
		if (lex.type() == Lexemator.LEXPR){
			doing = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.ELSE::init end of parse");
	}
	public String toString(){
		return "ELSE";
	}
}








