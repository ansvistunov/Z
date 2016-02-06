
/*
 * File: FALSE.java
 *
 * Created: Thu May 13 09:09:23 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class FALSE extends NullExternFunction {
	static final String fun = "FUN FALSE : "; 
	public Object eval() throws Exception{
		throw new ReturnException(new Double(0));
	}

}

