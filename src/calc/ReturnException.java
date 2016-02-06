
/*
 * File: ReturnException.java
 *
 * Created: Tue Apr 27 09:19:40 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

public class ReturnException extends CalcException{
	public Object result;
	public ReturnException(Object r){
		super();
		result = r;
	}
}
