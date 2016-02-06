
/*
 * File: EMPTYOP.java
 *
 * Created: Mon May  3 12:14:29 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */


package calc;

public class EMPTYOP extends OP{
	public Object eval() throws NullPointerException,ClassCastException,Exception{
		return "";
	}
}
