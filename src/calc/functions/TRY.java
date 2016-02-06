
/*
 * File: TRY.java
 *
 * Created: Thu Apr 29 13:50:32 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;
import loader.GLOBAL;
import java.util.*;

public class TRY implements ExternFunction {
	OP expr = null;
	OP catcher = null;
	
	public void getAliases(Hashtable h) throws Exception{
		expr.getAliases(h);
		if ( catcher!= null ) catcher.getAliases(h);
	}

	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.TRY::eval");
		try{
			Object result = expr.eval();
			GlobalValuesObject gvo = new ARGV();
			OP.aliases.put("##return_value##",gvo);
			gvo.setValue(result);
		}catch(NullPointerException e){
			//e.printStackTrace();
			GlobalValuesObject gvo = new ARGV();
			OP.aliases.put("##exception##",gvo);
			gvo.setValue(new RTException("NULLEXCEPTION","element not initialized"));
			return OP.doOP(catcher);
		}catch(RTException e){
			GlobalValuesObject gvo = new ARGV();
			OP.aliases.put("##exception##",gvo);
			gvo.setValue(e);
			return OP.doOP(catcher);
		}catch(CalcException e){
			GlobalValuesObject gvo = new ARGV();
			OP.aliases.remove("##exception##");
			OP.aliases.put("##return_exception##",gvo);
			gvo.setValue(e);
			return OP.doOP(catcher);
		}
		OP.aliases.remove("##exception##");
		OP.aliases.remove("##return_exception##");
		return OP.doOP(catcher);
	}

	public void init(String arg) throws Exception{
		Lexemator lex = new Lexemator(arg.toCharArray());
		lex.next();
			if (GLOBAL.calc_debug > 2)
				System.out.println("~clac.funcions.TRY::init LEXPR");
		if (lex.type() == Lexemator.LEXPR){
			expr = Parser.parse1(lex.as_string().toCharArray());
		}else throw new Exception();
		lex.next();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.TRY::init LED/LTAG");
		switch (lex.type()){
			//case Lexemator.LEND:
			//break;
		case Lexemator.LTAG:
			catcher = new Func(lex.as_string(),lex.args());
			break;
		default: throw new RTException("SYNTAX","Must has catch and/or finally");
		}
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.TRY::init end of parse");
	}
	public String toString(){
		return "TRY";
	}
}
