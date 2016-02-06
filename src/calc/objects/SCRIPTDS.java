
/*
 * File: SCRIPTDS.java
 *
 * Created: Fri Jun 25 10:32:36 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.objects;

import calc.*;
import dbi.*;

public class SCRIPTDS implements class_constructor{
	public Object constructor(Object arg) throws Exception{
		dbi.ScriptDs ds = new dbi.ScriptDs();
		return (Object)ds;
	}
}
