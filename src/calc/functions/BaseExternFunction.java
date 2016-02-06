
/*
 * File: BaseExternFunction.java
 *
 * Created: Tue Jun  1 15:05:17 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public abstract class BaseExternFunction implements ExternFunction {
	OP expr;
	public void init(String arg) throws Exception{
		expr = Parser.parse1(arg.toCharArray());
	}
	public void getAliases(Hashtable h) throws Exception{
		expr.getAliases(h);
	}
}
