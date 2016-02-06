
/*
 * File: X.java
 *
 * Created: Tue Apr 27 09:06:02 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class X extends BaseExternFunction implements XFunction{
	static final String fun = "FUN X : "; 
	OP args;
	public Object call(ARGV _V) throws Exception{
		boolean flag = true;
		String label = null;
		if (GLOBAL.calc_debug>2)
			System.out.println("called X function with "+_V.hash());
		Object V = OP.aliases.get("V");
		OP.aliases.put("V",_V);
		if ( V != null )
			_V.setValueByName("V",V);
		Object result = new Double(0);
		try{
			while(flag){
				flag = false;
				try{
					if ( label!=null )
						expr.evalLabel(label);
					else
						expr.eval();
				}catch(ReturnException e){
					result = e.result;
				}catch(GotoException e){
					label = e.label;
					flag = true;
				}
			}
		}finally{
			if (V!=null) OP.aliases.put("V",V);
			else {
				OP.aliases.remove("V");
			}
		}
		return result;
	}
	public void getAliases(Hashtable h) throws Exception{
		super.getAliases(h);
		for(Enumeration e = h.keys(); e.hasMoreElements();){
			String s = (String)e.nextElement();
			if ( s.indexOf('.') == -1 ){
					h.remove((Object)s);
			}
		}
	}
	public Object eval() throws Exception{
		return call(new ARGV());
	}
}

