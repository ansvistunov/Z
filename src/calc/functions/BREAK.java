
/*
 * File: BREAK.java
 *
 * Created: Wed Apr 28 13:03:13 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;

public class BREAK extends NullExternFunction {
	public Object eval() throws Exception{
		throw new BreakException();
	}
}
