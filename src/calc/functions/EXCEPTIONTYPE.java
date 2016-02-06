
/*
 * File: EXCEPTIONTYPE.java
 *
 * Created: Thu Apr 29 16:41:48 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;

public class EXCEPTIONTYPE extends NullExternFunction {
	public Object eval() throws Exception{
		Object o = OP.aliases.get("##exception##");
		if ( o !=  null )
			return ((RTException)((GlobalValuesObject)o).getValue()).type;
		else return "No Exception";
	}
}
