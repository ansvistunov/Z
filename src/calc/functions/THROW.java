
/*
 * File: THROW.java
 *
 * Created: Tue Apr 27 09:24:37 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class THROW extends BaseExternFunction {
	static final String fun = "FUN THROW : "; 
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println(fun+" "+expr.expr()+"  OP.soft="+OP.soft);
		Object a = OP.doOP(expr);
		if ( (a instanceof Vector) && (((Vector)a).size() == 2) ){
			try{
				throw new RTException(
					(String)((Vector)a).elementAt(0),
					(String)((Vector)a).elementAt(1));
			}catch(ClassCastException e){
				throw new ResonException(
					"~calc.functions.THROW::eval must be String,String");
			}
		}else throw new ResonException("~calc.functions.THROW::eval must be 2 args");
	}
}
