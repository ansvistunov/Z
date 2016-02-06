
/*
 * File: __calc__.java
 *
 * Created: Sun Nov 14 21:12:31 1999
 *
 * Copyright (c) by Alexey Chen
 */

package calc;

import java.util.Hashtable;

public interface __calc__{
        public void initExpr(String expr) throws Exception;
        public Object eval(Hashtable aliases) throws Exception;
   public String[] getAliases() throws Exception; 
}
