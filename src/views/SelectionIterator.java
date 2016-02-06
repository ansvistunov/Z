
package views;
import calc.objects.*;

public class SelectionIterator extends base_iterator{
	Object[] data;
	public SelectionIterator(Object[] data) {
	    super();
	    if (data==null) return;
	    this.data = data;
	    init(data.length-1);	    
	}

	public Object value() throws Exception {
	    return data[cursor];
	}
	public Object set_value(Object value) {return null;} 
}
