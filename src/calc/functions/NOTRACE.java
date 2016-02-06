
/*
 * File: NOTRACE.java
 *
 * Created: Fri Apr 30 09:37:52 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;
import calc.*;
import loader.GLOBAL;

public class NOTRACE extends BaseExternFunction{
	static final String fun = "FUN notrace : "; 
	public Object eval() throws Exception{
		boolean trace = OP.trace;
		OP.trace = false;
		try{
			return expr.eval();
		}finally{
			OP.trace = trace;
		}
	}
}

