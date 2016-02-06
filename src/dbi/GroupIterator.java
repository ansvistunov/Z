
/*
 * File: GroupIterator.java
 *
 * Created: Tue May 11 12:06:53 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */
package dbi;
import calc.objects.*;
import calc.*;

public class GroupIterator extends base_iterator{
	GroupReport ds;
	Group gr;
	Group[] grs;
	int count;
	public GroupIterator(GroupReport ds,Group gr){
		super();
		this.ds = ds;
		if(gr != null){
			grs = gr.getSubgroups();
			if (grs == null) count = 0;
								 else count = grs.length;
			super.init(count-1);
		}
	}
	
	public Object value() throws Exception{
		if (cursor == -1) throw new RTException("IteratorException",
												"DSIterator is not positioned!");
		if (grs == null) return new DsRow(gr.begrow+cursor,ds);
		else {
			grs[cursor].setReport(ds);
			return grs[cursor];
		}
	}
	public Object set_value(Object obj) throws Exception{
		return null;
		//throw new RTException("ReadOnlyException", "This object is Read Only!");
	  
	}
}
