
/*
 * File: Iterator.java
 *
 * Created: Tue May 11 10:02:52 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */

package dbi;
public interface Iterator {
	public int next();
	public int prev();
	public int last();
	public int first();
	public int iteratorValue() throws Exception;

}
