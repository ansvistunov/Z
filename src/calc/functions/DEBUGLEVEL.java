
/*
 * File: DEBUGLEVEL.java
 *
 * Created: Mon Jul  5 13:34:15 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class DEBUGLEVEL extends BaseExternFunction {
	public Object eval() throws Exception{
		try{
			Vector v = (Vector)expr.eval();
			String s = (String)v.elementAt(0);
			Double d = (Double)v.elementAt(1);
			s = s.trim().toUpperCase();
			if ( s.equals("DSTORE") ){
				GLOBAL.dstore_debug = d.intValue();
			}else if ( s.equals("PARSER") ){
				GLOBAL.parser_debug = d.intValue();
			}else if ( s.equals("CALC") ){
				GLOBAL.calc_debug = d.intValue();
			}else if ( s.equals("LOADER") ){
				GLOBAL.loader_debug = d.intValue();
			}else if ( s.equals("PROTOCOL") ){
				GLOBAL.protocol_debug = d.intValue();
			}else throw new Exception();
		}catch(Exception e){
			//e.printStackTrace();
			//throw new RTException("CASTEXCEPTION",
			//				  "DEBUGLEVEL (string)WHAT (numeric)LEVEL");
		}
		System.out.println("$$$$$$$$$ LIST OF DEBUG LEVELS\n"+
						   "$$\tDSTORE\t"+GLOBAL.dstore_debug+
						   "\n$$\tPARSER\t"+GLOBAL.parser_debug+
						   "\n$$\tCALC\t"+GLOBAL.calc_debug+
						   "\n$$\tLOADER\t"+GLOBAL.loader_debug+
						   "\n$$\tPROTOCOL\t"+GLOBAL.protocol_debug+
						   "\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		return "";
	}
}



