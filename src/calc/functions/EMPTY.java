
/*
 * File: EMPTY.java
 *
 * Created: Fri Apr 23 11:52:35 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc.functions;

import calc.*;

public class EMPTY extends BaseExternFunction {
	public Object eval() throws Exception{
		expr.eval();
		return "";
	}
}
