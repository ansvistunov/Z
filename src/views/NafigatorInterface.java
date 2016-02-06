
/*
 * File: NafigatorInterface.java
 *
 * Created: Thu Jun 17 11:34:07 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package views;

import dbi.Handler;

public interface NafigatorInterface{
	public void setCurPath(String path) throws Exception;
	public String loadDate(String path) throws Exception;
	public void retrieve();
	public void addHandler(Handler h);
}
