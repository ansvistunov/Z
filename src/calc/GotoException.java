
/*
 * File: GotoException.java
 *
 * Created: Thu Apr 29 08:56:51 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

public class GotoException extends CalcException{
	public String label;
	public GotoException(String label){
		this.label = label;
	}
}
