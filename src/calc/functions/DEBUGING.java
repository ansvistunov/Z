
/*
 * File: DEBUGING.java
 *
 * Created: Mon May  3 14:08:39 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */



package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class DEBUGING extends BaseExternFunction {
	static final String fun = "FUN DEBUG : "; 
	static boolean debuging = true;
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println(fun+" "+expr.expr()+"  OP.soft="+OP.soft);
		Object o;
		o = OP.doHardOP(expr);
		if ( debuging)
			if ( o instanceof Object[] ) o = OP.printArray((Object[])o);
			System.out.println(
				GLOBAL.PRINT_LIGHT+"#"+o+GLOBAL.PRINT_NORMAL);
		return o;
	}
}
