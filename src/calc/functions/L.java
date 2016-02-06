
/*
 * File: L.java
 *
 * Created: Thu May 13 09:19:44 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.functions;

import calc.*;

public class L extends BaseExternFunction {
	static final String fun = "FUN L : "; 
	public Object eval() throws Exception{
		boolean flag = true;
		String label = null;
		Object result = new Double(0);
		try{
			while(flag){
				flag = false;
				try{
					//System.out.println("1: flag "+flag+" label "+label);
					if ( label!=null ){
						if ( label.equals("BEGINLOOP") ) result = expr.eval();
						else result = expr.evalLabel(label);
					}else
						result = expr.eval();
				}catch(GotoException e){
					label = e.label;
					flag = true;
				}catch(ContinueException e){
					label = "BEGINLOOP";
					flag=true;
				}catch(BreakException e){
					break;
				}
				//System.out.println("2: flag "+flag+" label "+label);
			}
		}finally{
		}
		return result;
	}
}
