
/*
 * File: LoadableClass.java
 *
 * Created: Mon Mar 22 13:30:26 1999
 *
 * Copyright(c) by Alexey Chen
 */

package loader;
import java.util.*;

public interface LoadableClass {
//	public void param(Properties pr,Loader loader,ClassLoader classloader);
	public boolean connect(String user,String pass);
}
