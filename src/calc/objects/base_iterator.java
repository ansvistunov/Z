
/*
 * File: base_iterator.java
 *
 * Created: Wed May 12 09:18:43 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.objects;

import calc.*;
import java.util.*;

public abstract class base_iterator implements iterator{
	protected int cursor = -1;
	int maxcursor = -1;
	
	protected base_iterator(int maxcursor){this.maxcursor=maxcursor;}
	protected base_iterator(){this.maxcursor=-1;}
	protected void init(int maxcursor){this.maxcursor = maxcursor;}
	
	public Double next() throws Exception{
		if ( cursor == -1 ) cursor = 0;
		else ++cursor;
		if ( maxcursor >= cursor ){
			return new Double(1);
		}else{
			cursor = maxcursor;
			return new Double(0);
		}
		
	}
	public Double prev() throws Exception{
		if ( cursor == -1 ) cursor = maxcursor;
		else --cursor;
		if ( cursor >=0 ){
			return new Double(1);
		}else{
			cursor = 0;
			return new Double(0);
		}
	}
	public Double first() throws Exception{
		cursor = 0;
		if ( maxcursor >= cursor ){
			return new Double(1);
		}else{
			return new Double(0);
		}
	}
	public Double last() throws Exception{
		cursor = maxcursor;
		if ( cursor >= 0 ){
			return new Double(1);
		}else{
			return new Double(0);
		}
	}

	public Double size() throws Exception{
		return new Double(maxcursor+1);
	}

	public Double offset(Double d) throws Exception{
		return new Double(cursor);
	}
}
