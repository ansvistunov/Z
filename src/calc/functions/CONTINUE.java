
/*
 * File: CONTINUE.java
 *
 * Created: Wed Apr 28 13:03:21 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;

public class CONTINUE extends NullExternFunction {
	public Object eval() throws Exception{
		throw new ContinueException();
	}
}
