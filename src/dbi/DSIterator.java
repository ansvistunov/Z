
/*
 * File: DSIterator.java
 *
 * Created: Tue May 11 09:45:34 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */

package dbi;
import calc.objects.*;
import calc.*;

public class DSIterator extends base_iterator{
	DATASTORE ds;

	public DSIterator(DATASTORE ds){
		super(ds.getCountRows()-1);
		this.ds = ds;
	}

	public Object value() throws Exception{
		if (cursor == -1) throw new RTException("IteratorException",
												"DSIterator is not positioned!");
		return new DsRow(cursor,ds);
	}
	public Object set_value(Object obj) throws RTException{
		//throw new RTException("ReadOnlyException", "DSIterator is not positioned!");
		return null;
	}
	
}

