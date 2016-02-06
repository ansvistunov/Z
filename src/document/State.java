
/*
 * File: State.java
 *
 * Created: Fri Jul  2 13:28:40 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package document;

import java.util.*;

public class State extends Hashtable{
	public State(){
		super();
	}
	public void init(Hashtable h){
		if (h == null) return;
		System.out.println(h);
		for (Enumeration en = h.keys();en.hasMoreElements();){
			try{
				String key = (String)en.nextElement();
				put(key,h.get(key));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

