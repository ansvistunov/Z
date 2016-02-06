
/*
 * File: ArrayIterator.java
 *
 * Created: Wed May 12 09:16:44 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.objects;

import calc.*;

public class ArrayIterator extends base_iterator{
	Object[] arr = null;

	public ArrayIterator(Object[] arr){
		super(arr.length-1);
		this.arr = arr;
	}

	public Object value() throws Exception{
		if ( cursor != -1){
			return arr[cursor];
		}else throw new RTException("IteratorException",
									"iterator must be posited on any element");
	}

	public Object set_value(Object value) throws Exception{
		if ( cursor != -1){
			Object x = arr[cursor];
			arr[cursor] = value;
			return x;
		}else throw new RTException("IteratorException",
									"iterator must be posited on any element");

	}
}

