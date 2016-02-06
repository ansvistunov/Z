
/*
 * File: CATCH.java
 *
 * Created: Thu Apr 29 13:50:38 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;
import loader.GLOBAL;
import java.util.*;

public class CATCH implements ExternFunction {
	OP expr = null;
	OP doing = null;
	OP catcher = null;
	
	public void getAliases(Hashtable h) throws Exception{
		expr.getAliases(h);
		doing.getAliases(h);
		if ( catcher!= null ) catcher.getAliases(h);
	}
	public Object eval() throws Exception{
		Object rets = new Double(0);
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.CATCH::eval");
		Object result = expr.eval();
		if ( result instanceof String ){
			if (GLOBAL.calc_debug > 2)
				System.out.println("~clac.funcions.CATCH::eval string result "+result);
			Object o = OP.aliases.get("##exception##");
			if ( o !=  null ){
				String exception = ((RTException)
									((GlobalValuesObject)o).getValue()).type;
				String rst = ((String)result).trim().toUpperCase();
				if ( OP.trace ){
					System.out.println(Func.stab+Func.level+
									   ":CATCH '"+rst+"'/ check exception "+exception);
				}
				if ( rst.equals(exception) || rst.equals("ANY")){
					try{
						if (GLOBAL.calc_debug > 2)
							System.out.println(
								"~clac.funcions.CATCH::eval doing\n\t"+doing.expr());
						rets = doing.eval();
					}catch(CalcException e){
						if (GLOBAL.calc_debug > 2)
							System.out.println(
								"~clac.funcions.CATCH::eval doing CalcException\n\t"+e);
						GlobalValuesObject gvo = new ARGV();
						OP.aliases.put("##return_exception##",gvo);
						gvo.setValue(e);
					}catch(Exception e){
						if (GLOBAL.calc_debug > 2)
							System.out.println(
								"~clac.funcions.CATCH::eval doing Exception\n\t"+e);
						throw e;
					}
					OP.aliases.remove("##exception##");
				}
				if (catcher!=null)
					return OP.doOP(catcher);
				else{
					o = OP.aliases.get("##exception##");
					if ( o != null ) throw (Exception)((GlobalValuesObject)o).getValue();
					o = OP.aliases.get("##return_exception##");
					if ( o != null ) throw (Exception)((GlobalValuesObject)o).getValue();
				}
			}else if (catcher!=null) return OP.doOP(catcher);
			else {
				o = OP.aliases.get("##return_exception##");
				if ( o != null )
					throw (Exception)((GlobalValuesObject)o).getValue();
				else{
					o = ((GlobalValuesObject)
						 OP.aliases.get("##return_value##")).getValue();
					return o;
				}
			}
		}else throw new Exception();
		return rets;
	}

	public void init(String arg) throws Exception{
		Lexemator lex = new Lexemator(arg.toCharArray());
		lex.next();
			if (GLOBAL.calc_debug > 2)
				System.out.println("~clac.funcions.CATCH::init LEXPR");
		if (lex.type() == Lexemator.LEXPR){
			expr = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.CATCH::init LEXPR for doing");
		if (lex.type() == Lexemator.LEXPR){
			doing = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.CATCH::init LED/LTAG");
		switch (lex.type()){
		case Lexemator.LEND:
			break;
		case Lexemator.LTAG:
			catcher = new Func(lex.as_string(),lex.args());
			break;
		default: throw new Exception();
		}
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.CATCH::init end of parse");
	}
	public String toString(){
		return "CATCH";
	}
}
