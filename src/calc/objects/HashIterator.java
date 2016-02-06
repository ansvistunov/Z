
/*
 * File: HashIterator.java
 *
 * Created: Wed May 12 08:58:30 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.objects;

import calc.*;
import java.util.*;

public class HashIterator extends base_iterator{
	Object[] keys = null;
	Hashtable hash = null;

	public HashIterator(Hashtable hash){
		super(hash.size()-1);
		this.hash = hash;
		keys = new Object[hash.size()];
		Enumeration e = hash.keys();
		for(int i=0;e.hasMoreElements();++i){
			keys[i] = e.nextElement();
		}
	}

	public Double size() throws Exception{
		return new Double(keys.length);
	}
	public Object value() throws Exception{
		if ( cursor != -1){
			return keys[cursor];
		}else throw new RTException("IteratorException",
									"iterator must be posited on any element");
	}
	public Object set_value(Object value) throws Exception{
		throw new RTException("ReadOnlyException","Can't modify key in hashtable");
	}
}
