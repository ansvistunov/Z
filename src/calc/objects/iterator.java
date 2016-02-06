
/*
 * File: iterator.java
 *
 * Created: Wed May 12 08:13:19 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.objects;

public interface iterator{
	public Double next() throws Exception;
	public Double prev() throws Exception;
	public Double first() throws Exception;
	public Double last() throws Exception;
	public Double size() throws Exception;
	public Object value() throws Exception;
	public Object set_value(Object value) throws Exception;
	public Double offset(Double x) throws Exception;
}
