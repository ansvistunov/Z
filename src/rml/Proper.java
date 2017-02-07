/**
	Copyrigth(c) 1999, by Gama
	author Alexey Chen (xx.2.99)
*/

package rml;

/**
*/

import java.util.*;
import calc.*;
import calc.objects.*;
import loader.Callback;
import rml.Proper.PropHash.HashRow;
/**
	Узел дерева свойств
*/
public class Proper implements GlobalValuesObject,class_type,class_field,class_method{
	
	@Override
	public String toString() {
		return "Proper [tag=" + tag + " id="+id+"]";
	}

	public class PropHash extends Hashtable{
		Vector<HashRow> elements;
		HashRow tmp;
		
		@Override
		public synchronized Enumeration<HashRow> elements() {
			
			return elements.elements();
		}
		@Override
		public synchronized Enumeration<HashRow> keys() {
			
			return elements.elements();
		}
		
		@Override
		public synchronized Object get(Object key) {
			//System.out.println("get: key="+key);
			//if (true) return super.get(key);
					
			
			HashRow hr;
			
			tmp.name = key;
			int index = elements.indexOf(tmp);
			
			//System.out.println("get: key="+key+" index="+index);
			if (index == -1) return null;
			else {
				hr = elements.get(index);
				//System.out.println("get: key="+key+" return value="+hr.value);
				return hr.value;
			}
			
		}
		@Override
		public synchronized Object put(Object key, Object value) {
			return put (key,value,-1,-1);
			
		}
		
		public synchronized Object put(Object key, Object value, int s, int e) {
			
			//System.out.println("put: key="+key+" value="+value);
			//if (true) return super.put(key,value);
			
			HashRow hr;
			tmp.name = key;
			int index = elements.indexOf(tmp);
			
			if (index == -1) {
				hr = new HashRow(value,s,e,key);
				elements.add(hr);
				//System.out.println("add "+hr);
				return value;
			}else {
				hr = elements.get(index);
				hr.name = key;
				hr.value = value;
				if (e!=-1) hr.e = e;
				if (s!=-1) hr.s = s;
				elements.set(index, hr);
				//System.out.println("set "+hr+" at index "+index);
				return value;
			}
			
			
			
			
		}
		
		public HashRow getHashRow(Object key) {
			HashRow hr;
			
			tmp.name = key;
			int index = elements.indexOf(tmp);
			
			//System.out.println("get: key="+key+" index="+index);
			if (index == -1) return null;
			else {
				hr = elements.get(index);
				//System.out.println("get: key="+key+" return value="+hr.value);
				return hr;
			}
			
		}
		
		public Iterator<HashRow> filerProps(Vector<String> propers){
			Iterator<HashRow> it = new Iterator<HashRow>() {
				
				private int currentIndex = 0;
				
				
				@Override
				public boolean hasNext() {
					while(currentIndex<elements.size()) {
						if (propers.contains(elements.elementAt(currentIndex).name)) return true;
						else currentIndex++;
					}
					
					return false;
				}

				@Override
				public HashRow next() {
					
					return elements.elementAt(currentIndex++);
				}
				
			};
			
			
			return it;
		}
		
		
		
		
		
		public class HashRow{
			@Override
			public String toString() {
				return "HashRow [value=" + value + ", name=" + name + ", s=" + s + ", e=" + e + "]";
			}
			@Override
			public boolean equals(Object obj) {
				
				//System.out.println("equals called");
				
				if (obj == this) return true;
				if (obj instanceof HashRow) {
					return ((HashRow)obj).name.toString().equals(this.name.toString());
				} else if (obj instanceof String) {
					//System.out.println("this.name="+this.name+" obj="+obj +" equals="+((String)obj).equals(this.name));
					return ((String)obj).equals(this.name.toString());
				} else return false; 
			}
			public HashRow(Object value, int s, int e, Object name) {
				//super();
				this.value = value;
				this.s = s;
				this.e = e;
				this.name = name;
			}
			public Object value;
			public Object name;
			public int s;
			public int e;
			
			
		}
		
		
		public PropHash(){
			super(0);
			elements = new Vector<>();
			tmp = new HashRow(null,-1,-1,null);
		}
		
	}
	
	
	
	public String tag = "UNKNOWN";
	public Proper content = null;
	public Proper next = null;;
	public PropHash hash;
	Hashtable dhash = null;
	static Hashtable sdhash = null;
	int id = -1;
	
	public int s;//start position in source file
	public int e;//end position in source file
	
	
	public String getAliasOrId(){
		Object ret = get("ALIAS");
		if (ret == null) ret = tag+"_"+id;
		return ret.toString();
		
	}
	
	public Proper(int id){
		hash = new PropHash();
		if ( dhash == null ) {
			if (sdhash == null) {
				sdhash = hash;
			}
			dhash = sdhash;
		}
		this.id = id;
	}

	public Proper( String tag, Proper defu){
		hash = new PropHash();
		if ( defu == null ) dhash = null;
		else dhash = defu.hash;
		this.tag = tag;
	}

	public static void clearDefault(){
		sdhash = null;
	}
	
	public Iterator<HashRow> filerProps(Vector<String> propers){
		return hash.filerProps(propers);
	}
	
	public Iterator<Proper> filerTags(Vector<String> tags){
		Iterator<Proper> it  = new Iterator<Proper>(){
			
			Proper current = null;
			Proper next = Proper.this;
			Stack<Proper> stack = new Stack<>();
			Callback finder = new Callback(){

				@Override
				public Object callback(Object arg) throws Exception {
					Object[] arr = (Object[])arg;
					int rec = (int)arr[1];
					Proper foo = (Proper)arr[0];
					
					if (tags.contains(foo.tag)) {
						current = foo;
						return null;
						
					}
					else return rec;
				}
				
			};

			@Override
			public boolean hasNext() {
				try{
					current = null;
					next = traversal2(stack,next,0,finder);
					if (current!=null) return true;
					else return false;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}

			@Override
			public Proper next() {
				return current;
			}
			
		}; 
		return it;
	}
	
	public Object get(String alias){
		Object res;
		res = hash.get(alias);
		if ( res == null ){
			if ( dhash != null ){
				res = dhash.get(alias);
			}
		}
		return res;
	}
	
	public Object get(Object alias){
		
		return get(alias.toString());
	}

	public Object get(String alias,Object o){
		Object res;
		res = hash.get(alias);
		if ( res == null ){
			if ( dhash != null ){
				res = dhash.get(alias);
			}
		}
		if ( res == null ) res = o;
		return res;
	}

	public void put(String alias,Object obj){
		//System.out.println("~rml.Proper::put alias="+alias+", obj="+obj);
		hash.put(alias,obj);
	}
	
	public void put(Object alias,Object obj){
		//System.out.println("~rml.Proper::put alias="+alias+", obj="+obj);
		put(alias.toString(),obj);
	}
	public void put(String propName, Object value, int s, int e) {
		// TODO Auto-generated method stub
		hash.put(propName, value, s, e);
	}

	public static void add(Proper prop,Proper p){
		//System.out.println("~rml.Proper:add called");
		if (prop.content == null) prop.content = p;
		else {
			Proper foo = prop.content;
			while (foo.next!=null){
				foo = foo.next;
			}
			foo.next = p;
		}
	}

	
	
	/*void recur_dump(Proper prop, int rec,StringBuffer sbb){
		StringBuffer sb = new StringBuffer();
		for ( int i=0; i<rec ; ++i) sb.append("    ");
		String s = sb.toString();
		Proper foo = prop;
		while ( foo != null ){
			if ( rec != 0 ) sbb.append(s+"{"); 
			sbb.append(foo.tag+"\n");			
			Enumeration e = foo.hash.keys();
			while(e.hasMoreElements()){
				Object o = e.nextElement();
				if ( ((String)o).startsWith("##") ) continue; 
				HashRow oo = (HashRow)(foo.hash.get(o));
			    sbb.append(s+"    "+
						   o+
						   " = "+
						   ((oo.value instanceof String)?"\"":"")+
						   oo.value+
						   ((oo.value instanceof String)?"\"":"")+
						   "\n");
			}
			recur_dump(foo.content,rec+1,sbb);
			if ( rec != 0 ) sbb.append(s+"}"); 
			sbb.append("\n");
			foo = foo.next;
		}
	}*/
	
	
	public Vector propNames(){
		Vector v = new Vector();
		Enumeration<HashRow> e = hash.elements();
		while(e.hasMoreElements()){
			HashRow hr = e.nextElement();
			v.add(hr.name);
		}
		return v;
	}
	
	
	public Proper traversal2(Stack<Proper> stack, Proper foo, int rec, Callback cb) throws Exception {

		//System.out.println("traversal2");
		Proper prev;
		
		while (stack.size() > 0 || foo != null) {
			if (foo != null) {
				// printf("visited %d\n", root->data);
								
				prev=foo;
				
				if (foo.next != null) {
					stack.push(foo.next);
				}
				foo = foo.content;
				
				Object ret = cb.callback(new Object[] { prev, rec });
				if (ret == null) return foo;
				
				
			} else {
				foo = stack.pop();
			}
		}
		return null;
	}

		
		
			
	
	

	public void dump(){
		System.out.println(toText());
	}

	public String toText(){
		/*StringBuffer sbb = new StringBuffer("");
		sbb.append("//\n//Document generated by Zeta RML Browser\n//\n");
		recur_dump(this,0,sbb);
		return sbb.toString();*/
		
		
		
		
		Callback t = new Callback() {
			
			StringBuffer sb = new StringBuffer();
			
			
			@Override
			public String toString() {
				return sb.toString();
			}
			@Override
			public Object callback(Object arg) throws Exception {
				Object[] arr = (Object[])arg;
				int rec = (int)arr[1];
				Proper foo = (Proper)arr[0];
				for ( int i=0; i<rec ; ++i) sb.append("    ");
				//String s = sb.toString();
				//if ( rec != 0 ) sb.append("{"); 
				sb.append(foo.tag+"\n");			
				Enumeration<HashRow> e = foo.hash.elements();
				while(e.hasMoreElements()){
					HashRow o = e.nextElement();
					if ( ((String)o.name).startsWith("##") ) continue; 
					
				    sb.append("    "+
							   o.name+
							   " = "+
							   ((o.value instanceof String)?"\"":"")+
							   o.value+
							   ((o.value instanceof String)?"\"":"")+
							   "\n");
				}
				//if ( rec != 0 ) sb.append("}"); 
				sb.append("\n");
				return rec;
			}
			
			
			
		};
		
		
		
		try {
			traversal2(new Stack<Proper> (), this,0,t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t.toString();
		
	}

/*	public String toString(){

		StringBuffer sbb = new StringBuffer("");
		sbb.append("\nBEGIN Dump of Proper tree\n");
		recur_dump(this,0,sbb);
		sbb.append("END Dump of Proper tree\n");
		return sbb.toString();
	}
*/	
// implementaion of GlobalValuesObject

	public void setValue(Object obj) throws Exception{}
    public Object getValue() throws Exception{return this;}
    public void setValueByName(String name, Object obj) throws Exception {}
    public Object getValueByName(String name) throws Exception {return null;}

//implementaion of class_method
	
	public Object method(String method,Object arg) throws Exception{
		throw new RTException("HasMethodException",
									"object Proper has not method "+method);
	}
	public String type() throws Exception{
		return "PROPER";
	}

//implementaion of class_field

	public Object field(String field) throws Exception{
		if ( field.equals("NEXT") ){
			return (next!=null)?(Object)next:new Nil();
		}else if ( field.equals("HASH") ){
			return (hash!=null)?(Object)hash:new Nil();
		}else if ( field.equals("DEFAULT") ){
			return (dhash!=null)?(Object)dhash:new Nil();
		}else if ( field.equals("TAG") ){
			return tag;
		}else if ( field.equals("CONTENT") ){
			return (content!=null)?(Object)content:new Nil();		
		}else throw new RTException("HasFieldException",
									"object Proper has not field "+field);
	}

	public Object set_field(String field,Object value) throws Exception{
		try{
			if ( field.equals("NEXT") ){
				if (value instanceof Nil) {
					next = null;
				}else next = (Proper)value;
				return value;
			}else if ( field.equals("CONTENT") ){
				if (value instanceof Nil) {
					content = null;
				}else content = (Proper)value;
				return value;
			}else if ( field.equals("HASH") ){
				if (value instanceof Nil) {
					hash = null;
				}else hash = (PropHash)value;
				return value;
			}else if ( field.equals("DEFAULT") ){
				if (value instanceof Nil) {
					dhash = null;
				}else dhash = (Hashtable)value;
				return value;
			}else if ( field.equals("TAG") ){
				if (value instanceof Nil) {
					tag = null;
				}else tag = (String)value;
				return value;
			}else throw new RTException("HasFieldException",
										"object Proper has not field "+field);
		}catch(ClassCastException e){
			throw new RTException("CastException",
										"Proper@"+field);
		}
	}

	
	
}
