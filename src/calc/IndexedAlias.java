
/*
 * File: IndexedAlias.java
 *
 * Created: Wed Apr 28 08:55:50 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import loader.GLOBAL;


public class IndexedAlias extends Alias implements Const{
	int index;
	Vector indexes = null;
	public IndexedAlias(String alias,int index){
		super(alias);
		this.index = index;
		indexes = null;
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.IndexedAlias::<init> alias is "+
							   alias+" index="+index);
	}
	public IndexedAlias(String alias,Vector index){
		super(alias);
		indexes = index;
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.IndexedAlias::<init> alias is "+
							   alias+" index="+index);
	}
	public IndexedAlias(IndexedAlias alias,int index){
		super(alias.name());
		indexes = alias.indexes;
		if ( indexes == null ){
			indexes = new Vector();
			indexes.addElement(new Double(alias.index));
		}
		indexes.addElement(new Double(index));
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.IndexedAlias::<init> alias is "+
							   alias+" index="+index);
	}
	public IndexedAlias(IndexedAlias alias,Vector index){
		super(alias.name());
		indexes = alias.indexes;
		if ( indexes == null ){
			indexes = new Vector();
			indexes.addElement(new Double(alias.index));
		}
		for (Enumeration e = index.elements();e.hasMoreElements();){
			indexes.addElement(e.nextElement());
		}
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.IndexedAlias::<init> alias is "+
							   alias+" index="+index);
	}
	public Object getValue() throws Exception{
		Object o = super.getValue();
		if ( o instanceof Object[] ){
			try{
				if ( indexes == null )
					return ((Object[])o)[index];
				else try {
					Object result = o;
					for (Enumeration e = indexes.elements();e.hasMoreElements();){
						result = ((Object[])result)[
							((Double)e.nextElement()).intValue()];
					}
					return result;
				}catch(ClassCastException e){
					throw new RTException("CastException",
						  "Element is not ARRAY or index is not NUMBER");
				}
			}catch(IndexOutOfBoundsException e){
				throw new RTException("IndexException",
									  "max index "+
									  (((Object[])o).length-1)+
									  " but index="+index);
			}
		}else throw new ResonException(
			"~calc.IndexedAlias::getValue type not Object[] \n\t"+
			"reson: alias value is "+o+"\n\t"+
			"Object: "+toString());
	}
	public Object setValue(Object a) throws
		Exception,NullPointerException,ClassCastException{
		Object o = super.getValue();
		if ( o instanceof Object[] ){
			try{
				if ( indexes == null )
					((Object[])o)[index] = a;
				else try {
					Object result = o;
					for (Enumeration e = indexes.elements();e.hasMoreElements();){
						if ( e.hasMoreElements() ){
							result = ((Object[])result)[
								((Double)e.nextElement()).intValue()];
						}else{
							((Object[])result)[
								((Double)e.nextElement()).intValue()]
								= a;
						}
					}
				}catch(ClassCastException e){
					throw new RTException("CastException",
						  "Element is not ARRAY or index is not NUMBER");
				}
			}catch(IndexOutOfBoundsException e){
				throw new ResonException(
					"~calc.IndexedAlias::setValue index exception \n\t"+
					"reson: array.length="+((Object[])o).length+
					"but index="+index+"\n\t"+
					"Object: "+toString());
			}
		}else throw new ResonException(
			"~calc.IndexedAlias::setValue type not Object[] \n\t"+
			"reson: alias value is "+o+"\n\t"+
			"Object: "+toString());
		return a;
	}
	public Object eval() throws Exception,NullPointerException,ClassCastException{
		return getValue();
	}
	public String toString(){
		return "IndexedAlias:"+index+"#"+left+((right!=null)?"."+right:"");
	}
	public String expr(){
		return toString();
	}
}
