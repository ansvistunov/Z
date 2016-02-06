
/*
 * File: FINALLY.java
 *
 * Created: Thu Apr 29 15:07:37 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;
import loader.GLOBAL;

public class FINALLY extends BaseExternFunction {
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.FINALLY::eval");
		Object result = expr.eval();
		if (GLOBAL.calc_debug > 2)
			System.out.println("~clac.funcions.FINALLY::eval string result "+result);
		Object o = OP.aliases.get("##return_exception##");
		if ( o !=  null ){
			CalcException exception = (CalcException)
								((GlobalValuesObject)o).getValue();
			throw exception;
		}else{
			GlobalValuesObject ob =
				((GlobalValuesObject)OP.aliases.get("##return_value##"));
			if ( ob == null ) return new Double(0);
			o = ob.getValue();
			return o;
		}
	}

	public String toString(){
		return "FINALLY";
	}
}
