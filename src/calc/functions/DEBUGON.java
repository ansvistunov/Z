
/*
 * File: DEBUGON.java
 *
 * Created: Mon May  3 14:08:03 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */



package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class DEBUGON extends DEBUGING implements ExternFunction {
	public String toString(){
		return "DEBUGON";
	}
	public Object eval() throws Exception{
		boolean dbg = debuging;
		debuging = true;
		try{
			return OP.doOP(expr);
		}finally{
			debuging = dbg;
		}
	}
}
