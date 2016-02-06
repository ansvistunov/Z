
/*
 * File: NullExternFunction.java
 *
 * Created: Tue Jun  1 16:52:35 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */



package calc.functions;

import calc.*;
import java.util.*;

public abstract class NullExternFunction implements ExternFunction {
	public void getAliases(Hashtable h) throws Exception{
	}
	public void init(String arg) throws Exception{
	}
}
