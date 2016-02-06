
/*
 * File: CREATE_TVLIST.java
 *
 * Created: Thu Aug 12 13:30:11 1999
 * Author: Alexey Chen */


package calc.functions;

import calc.*;
import java.util.*;
import loader.GLOBAL;
import dbi.*;

public class CREATE_TVLIST extends BaseExternFunction {
	public Object eval() throws Exception{
	    Vector v;
		try{
			v = (Vector)OP.doHardOP(expr);
			System.out.println(v);
			DATASTORE ds = (DATASTORE)v.elementAt(0);
			int l = ds.getCountRows();
			int c1 = ((Double)v.elementAt(1)).intValue();
			int c2 = ((Double)v.elementAt(2)).intValue();
			String node = (String)v.elementAt(3);
			StringBuffer result = new StringBuffer(4096);
			result.append("");
			for ( int i = 0; i < l; ++i ){
				result.append(node).
					append("/").
					append(ds.getValue(i,c1)).
					append("/").
					append(ds.getValue(i,c2)).
					append("\n");
			}
			return result.toString();
		}catch(Exception e){
			e.printStackTrace();
			throw new RTException("CAST EXCEPTION",
								  "CREATE_TVLIST must have 4 arguments "+
						"DATASTORE dstore,NUMBER column1,NUMBER column2,STRING Node");
		}
	}
}
