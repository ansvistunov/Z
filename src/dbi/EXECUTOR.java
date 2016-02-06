
/*
 * File: EXECUTOR.java
 *
 * Created: Fri Apr 23 12:59:06 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */


package dbi;
import java.sql.*;
import java.util.*;
import loader.*;


public class EXECUTOR {

	public static void execute(Object[] args, Hashtable aliases)
		throws UpdateException {
		    //System.out.println("______________EXECUTOR CALLED!!!             ");
		if (args == null) return;
		String query="begin ";
		for (int i=0; i<args.length;i++) {
		    query = query+(String)args[i]+";";
		    if (GLOBAL.dstore_debug>2) System.out.println(query);
		    }
		query = query + " end;";
		
		/*
		for (int i=1;i<args.length;i++){
			if (i!=args.length)
				query = query +args[i]+",";
			else query = query + args[i]+"); end;";
					}
		*/
		ResultSet rset = null;
		try{
			rset = DATASTORE.executeQuery(query);
                        DATASTORE.conn.commit();
                        DSCollection.repeatLocks();
		}catch (SQLException e){
		    
		    throw new UpdateException(e.getMessage(),0);
		    }
	}
}







