
/*
 * File: ALIAS.java
 *
 * Created: Mon May  3 14:36:57 1999
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

public class ALIAS extends BaseExternFunction {
	public Object eval() throws Exception{
		if ( GLOBAL.calc_debug > 2 )
			System.out.println("~calc.functions.ALIAS::eval");
		Object o = OP.doHardOP(expr);
		String reson =
			"reson: alias value is "+o+"\n\t"+
			"Object: "+toString()+"\n";

		if ( o instanceof String ){
			//System.out.println("~calc.functions.I::eval "+o);
			return new Alias(((String)o).trim().toUpperCase()).getValue();
		}else throw new ResonException(
			"~calc.functions.I::eval\n\ttype of args is't String \n\t"+
			reson);
	}
}
