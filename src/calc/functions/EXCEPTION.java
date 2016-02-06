
/*
 * File: EXCEPTION.java
 *
 * Created: Thu Apr 29 15:31:16 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;

public class EXCEPTION extends NullExternFunction {
	public Object eval() throws Exception{
		Object o = OP.aliases.get("##exception##");
		if ( o !=  null )
			return ((Exception)((GlobalValuesObject)o).getValue()).getMessage();
		else return "No Exception";
	}
}
