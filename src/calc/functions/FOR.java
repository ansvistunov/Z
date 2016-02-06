
/*
 * File: FOR.java
 *
 * Created: Fri Apr 23 13:09:50 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;
import loader.GLOBAL;
import java.util.*;

public class FOR implements ExternFunction {
	static final String mesg = "~clac.funcions.FOR::";

	OP init_expr = null;
	OP do_expr = null;
	OP expr = null;
	OP doing = null;
	
	public void getAliases(Hashtable h) throws Exception{
		if ( init_expr!= null ) init_expr.getAliases(h);
		if ( do_expr!= null ) do_expr.getAliases(h);
		if ( expr!= null ) expr.getAliases(h);
		if ( doing!= null ) doing.getAliases(h);
	}

	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.IF::eval");
		if ( init_expr != null ){
			init_expr.eval();
		}

		while(true) {
			boolean f = true;
			if ( expr != null ) {
				Object result = expr.eval();
				if ( result instanceof Double ){
					if ( GLOBAL.calc_debug > 2 )
						System.out.println(mesg+"eval double result "+
										   (Double)result);
					f = (((Double)result).doubleValue() == 1 );

				}else if ( result instanceof String ){
					if (GLOBAL.calc_debug > 2)
						System.out.println(mesg+"eval string result "+result);
					f = ( ((String)result).trim().toUpperCase().compareTo("TRUE")==0 );
				}else throw new Exception();
			}
			if ( f ) doing.eval(); else break;
			if ( do_expr!=null ) do_expr.eval();
		}
		return new Double(0);
	}

	public void init(String arg) throws Exception{
		Lexemator lex = new Lexemator(arg.toCharArray());
		lex.next();
			if (GLOBAL.calc_debug > 2)
				System.out.println(mesg+"init LEXPR");
		if (lex.type() == Lexemator.LEXPR){
			do_expr = Parser.parse1(lex.as_string().toCharArray());
			if ( (do_expr!=null) && (do_expr instanceof COMA) ){
				expr = (OP)do_expr.left;
				do_expr = (OP)do_expr.right;
				if ( (expr!=null) && (expr instanceof COMA)){
					init_expr = (OP)expr.left;
					expr = (OP)expr.right;
				}else throw new Exception(mesg+" Expression Syntax error");
			}else throw new Exception(mesg+" Expression Syntax error");
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println(mesg+"init LEXPR for doing");
		if (lex.type() == Lexemator.LEXPR){
			doing = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		if (GLOBAL.calc_debug > 2)
			System.out.println(mesg+"init end of parse");
	}
	public String toString(){
		return "FOR";
	}
}


