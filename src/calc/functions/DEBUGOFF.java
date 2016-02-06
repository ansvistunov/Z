
/*
 * File: DEBUGOFF.java
 *
 * Created: Mon May  3 14:12:17 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */



package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;

public class DEBUGOFF extends DEBUGING implements ExternFunction {
	public String toString(){
		return "DEBUGOFF";
	}
	public Object eval() throws Exception{
		boolean dbg = debuging;
		debuging = false;
		try{
			return OP.doOP(expr);
		}finally{
			debuging = dbg;
		}
	}
}
