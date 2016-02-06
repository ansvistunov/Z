
/*
 * File: PROPER.java
 *
 * Created: Fri Jun 18 17:51:25 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.objects;

import calc.*;
import rml.*;
import java.util.*;

public class PROPER implements class_constructor{
	public Object constructor(Object arg) throws Exception{
		try{
			Vector v = (Vector)arg;
			Object o = v.elementAt(1);
			return new Proper((String)v.elementAt(0),
							  (o instanceof Nil)?(Proper)null:(Proper)o);
		}catch(Exception e){
			//e.printStackTrace();
			throw new RTException("CastException","Constructor of Proper must have 2 arguments : tag and default propers");
		}
	}
}
