
/*
 * File: RET.java
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

public class RET extends BaseExternFunction {
	static final String fun = "FUN RET : "; 
	public Object eval() throws Exception{
		if (GLOBAL.calc_debug > 2)
			System.out.println(fun+" "+expr.expr()+"  OP.soft="+OP.soft);
		throw new ReturnException(OP.doOP(expr));
	}
}
