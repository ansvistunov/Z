
/*
 * File: TRACE.java
 *
 * Created: Fri Apr 30 09:34:37 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;
import calc.*;
import loader.GLOBAL;

public class TRACE extends BaseExternFunction{
	static final String fun = "FUN trace : "; 

	public Object eval() throws Exception{
		boolean trace = OP.trace;
		OP.trace = true;
		try{
			return expr.eval();
		}finally{
			OP.trace = trace;
		}

	}
}
