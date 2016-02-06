
/*
 * File: WHILE.java
 *
 * Created: Wed Apr 28 12:27:12 1999
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

public class WHILE implements ExternFunction {
	OP expr = null;
	OP doing = null;

	public void getAliases(Hashtable h) throws Exception{
		expr.getAliases(h);
		if ( doing!= null ) doing.getAliases(h);
	}
	
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.WHILE::eval");

		while(true){
			Object result = expr.eval();
			boolean f = false;
			if ( result instanceof Double ){
				if (GLOBAL.calc_debug > 2)
					System.out.println("~clac.funcions.WHILE::eval double result "+
									   (Double)result);
				if ( ((Double)result).doubleValue() == 1 ){
					f = true;
				}else {
					break;
				}
			}else if ( result instanceof String ){
				if (GLOBAL.calc_debug > 2)
					System.out.println(
						"~clac.funcions.WHILE::eval string result "+result);
				if (((String)result).trim().toUpperCase().compareTo("TRUE")==0){
					f = true;
				}else {
					break;
				}
			}else throw new ResonException("~clac.funcions.WHILE::eval \n\t"+
				"result type must be logical");
			if (f){
				try{
					doing.eval();
				}catch(ContinueException e){
					continue;
				}catch(BreakException e){
					break;
				}
			}
		}
		return new Double(0);
	}

	public void init(String arg) throws Exception{
		Lexemator lex = new Lexemator(arg.toCharArray());
		lex.next();
			if (GLOBAL.calc_debug > 2)
				System.out.println("~clac.funcions.WHILE::init LEXPR");
		if (lex.type() == Lexemator.LEXPR){
			expr = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.WHILE::init LEXPR for doing");
		if (lex.type() == Lexemator.LEXPR){
			doing = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.WHILE::init end of parse");
	}
	public String toString(){
		return "WHILE";
	}
}
