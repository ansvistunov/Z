
/*
 * File: QUOTE.java
 *
 * Created: Thu May 13 11:52:44 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class QUOTE extends BaseExternFunction {
	public Object eval() throws Exception{
		//System.out.println(expr.expr());
		return (new Quoted(expr));
	}
}
