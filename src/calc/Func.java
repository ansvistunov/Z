
/*
 * File: Func.java
 *
 * Created: Fri Apr 23 10:24:20 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import calc.objects.*;
import loader.GLOBAL;
import java.util.*;

public class Func extends OP implements Const{
	//Tree expr;
	public static int level = 0;
	public static String stab = "";
	public Func(String tag, String args) throws Exception{
		prior = 15;
		tag = tag.trim().toUpperCase();
		if (GLOBAL.calc_debug > 2) 
			System.out.println("~calc.Func::<init> tag ="+tag );
		int x = tag.lastIndexOf('@');
		if ( x != -1 ){
			String a = tag.substring(0,x);
			String b = tag.substring(x+1);
			if (GLOBAL.calc_debug > 2) 
				System.out.println("~calc.Func::<init> func a="+a+" b="+b);
//			Alias c = new Alias(a);
			OP c = Parser.parse1(a.toCharArray());
			left = new Tree(c,b);
			right =  Parser.parse1(args.toCharArray());
		}else{
			try{
				//		Class c = GLOBAL.cl.loadClass("calc.functions."+tag);
				Class c = Class.forName("calc.functions."+tag);
				ExternFunction ef = (ExternFunction)c.newInstance();
				ef.init(args);
				left = ef;
				right= args;
			}catch(ClassNotFoundException e){
				left = tag;
				right = Parser.parse1(args.toCharArray());
			}
		}
	}

	public void getAliases(Hashtable h) throws Exception{
		Tree expr;
		OP args;
		try{
			if (left instanceof String){
				if ( (expr=(Tree)OP.functions.get(left)) != null ){
					args = (OP)right;
				}else throw new RTException("FunctionNotFound",
										 "could'n found function "+left); 
				args.getAliases(h);
			}else if ( left instanceof Tree){
				String method = (String)((Tree)left).right;
				Alias alias;
				//Object ali = OP.doSoftOP((OP)((Tree)left).left);
				((OP)right).getAliases(h);
			}else ((ExternFunction)left).getAliases(h);
		}catch(RTException e){
			String s = expr();
			s = ( s.length() > 50 )?s.substring(0,50):s;
			throw new RTException(e.type,e.getMessage(),e.trap+
								  "\n\t"+GLOBAL.PRINT_LIGHT+
								  "TRAP: Func "+
								  GLOBAL.PRINT_NORMAL+s);
		}
	}
	
	public Object eval() throws Exception{
		boolean msoft = OP.soft;
		Tree expr;
		OP args;
		++level;
		String mstab=stab;
		if (OP.trace){
			stab=stab+"   ";
			Object fn;
			if ( left instanceof String ){
				fn = left;
			}else if( left instanceof Tree ){
				String s = ((Tree)left).left.toString();
				fn = ""+((s.trim().equals(""))?"<?>":s)+"@"+((Tree)left).right;
			}else fn = left;
			System.out.println(stab+level+":IN FUNCTION '"+fn+"'");
		}
		if (GLOBAL.calc_debug > 2) 
			System.out.println("~calc.Func::eval func "+left+" args "+
							   ((right instanceof OP)?((OP)right).expr():right));
		try{
			if (left instanceof String){
				if ( (expr=(Tree)OP.functions.get(left)) != null ){
					args = (OP)right;
				}else throw new RTException("FunctionNotFound",
										 "could'n found function "+left); 
				ARGV V = new ARGV();
				if (GLOBAL.calc_debug > 2) 
					System.out.println("~calc.Func::eval expression "+args.expr());
				Object o = args.eval();
				if ( o instanceof Vector ){
					if (OP.trace){
						System.out.println("\t with args "+o);
					}
					Tree t = (Tree)expr.right;
					try{
						for(Enumeration e=((Vector)o).elements();e.hasMoreElements();){
							Object bar = e.nextElement();
							if (GLOBAL.calc_debug > 2) 
								System.out.println("set arg "+(String)t.left+" to "+bar);
							V.setValueByName((String)t.left,bar);
							t=(Tree)t.right;
						}
						if (t!=null) throw new Exception("????? Bad tree of arguments");
					}catch(Exception e){
						throw new RTException("CastException",
											  "func "+expr.left+" few arguments");
					}
				}else{
					if ( ((Tree)expr.right).right != null )
						throw new RTException("CastException",
											  "func "+expr.left+" few arguments");
					V.setValueByName((String)((Tree)expr.right).left,o);
				}
				if (GLOBAL.calc_debug > 2) 
					System.out.println(((OP)expr.left).expr());
				return ((Func)expr.left).call(V);
			}else if ( left instanceof Tree){
				String method = ((String)((Tree)left).right).trim().toUpperCase();
//				Alias alias = (Alias)((Tree)left).left;
				Alias alias;
				if ( GLOBAL.calc_debug > 2)
					System.out.println("~calc.Func::eval expr is "+
									   ((OP)((Tree)left).left).expr());
				Object ali = OP.doSoftOP((OP)((Tree)left).left);
				if ( GLOBAL.calc_debug > 2)
					System.out.println("~calc.Func::eval ali is "+ali);
				if ( ali instanceof Alias ) alias = (Alias)ali;
				else if (( ali instanceof String ) && ((String)ali).equals("")) 
				    alias = new Alias((String)ali);
				else{
					if ( OP.aliases.get("##argv##") == null )
						OP.aliases.put("##argv##",new ARGV());
					((ARGV)(OP.aliases.get("##argv##"))).setValueByName("##ali##",ali);
					alias = new Alias("##argv##.##ali##");
				}
				if ( GLOBAL.calc_debug > 2)
					System.out.println("~calc.Func::eval alias is "+alias);
				if ( trace &&
					 !(((Tree)left).left instanceof Alias) &&
					 !alias.name().equals(""))
					System.out.println(stab+"\t REALY : '"+alias.name()+"@"+method+"'");
				if ( alias.name().equals("") ){
				if ( trace )
					System.out.println(stab+"\t REALY : CREATE '"+method+"'");

					if ( method.equals("ARRAY")){
						Object o = ((OP)right).eval();
						if ( o instanceof Vector ){
							Object[] a = new Object[((Vector)o).size()];
							((Vector)o).copyInto(a);
							return a;
						}else if ( o instanceof Double ){
							return new Object[((Double)o).intValue()];
						}else throw new RTException("CASTEXCEPTION",
							"type of args must be list of values or one number");
					}else if ( method.equals("HASH") ){
						return new Hashtable();
					}else if ( method.equals("NUMBER") ){
						Object o = OP.doHardOP((OP)right);
						if ( !( o instanceof String ) )
							throw new RTException("CastException",
							  "constructor of number must have STRING argument");
						try{
							return Double.valueOf((String)o);
						}catch(NumberFormatException e){
							throw new RTException("NUMBEREXCEPTION",
												  "bad nuber format "+o);
						}
					}else try{
						//Class c = GLOBAL.cl.loadClass("calc.objects."+method);
						Class c = Class.forName("calc.objects."+method);
						class_constructor ef = (class_constructor)c.newInstance();
						return ef.constructor(OP.doHardOP((OP)right));
					}catch(ClassNotFoundException e){
						throw new RTException("UNKNOWNCLASS","Unknown class "+method);
					}catch(ClassCastException e){
						throw new RTException("UNKNOWNCLASS","Unknown class "+method);
					}
				}
				Object obj = alias.getValue();
				if ( GLOBAL.calc_debug > 2)
					System.out.println("~calc.Func::eval object is  "+obj);
				if ( method.equals("SIZE") &&
							(obj instanceof  class_size))
						   return ((class_size)obj).size();  
				else if ( method.equals("ITERATOR") &&
							(obj instanceof  class_iterator))
						   return ((class_iterator)obj).iterator();  
				else if (obj instanceof String ){
					if (method.equals("LENGTH")||method.equals("SIZE")){
						return new Double(((String)obj).length());
					}else if (method.equals("SUBSTR")){
						Object x = ((OP)right).eval();
						if ( (x instanceof Vector) &&
							 (((Vector)x).size() == 2) &&
							 ( ((Vector)x).elementAt(0) instanceof Double ) &&
							 ( ((Vector)x).elementAt(1) instanceof Double ) 
							){
							return ((String)obj).substring(
								((Double)((Vector)x).elementAt(0)).intValue(),
								((Double)((Vector)x).elementAt(1)).intValue());
						}else if ( x instanceof Double ){
							return ((String)obj).substring(((Double)x).intValue());
						}else throw new RTException("CASTEXCEPTION",
												  "Must be ($String@substr num[,num])");
					}else if (method.equals("INDEXOF")){
						Object x = ((OP)right).eval();
						if ( x instanceof String ){
							return new
								Double(((String)obj).indexOf(
									((String)x).charAt(0)));
						}else throw new
								  RTException("CASTEXCEPTION",
											  "Argument of indexof must be String"+
											  " but it "+
											  "is "+x);
					}else if (method.equals("LASTINDEXOF")){
						Object x = ((OP)right).eval();
						if ( x instanceof String ){
							return new
								Double(((String)obj).lastIndexOf(
									((String)x).charAt(0)));
						}else throw new
								  RTException("CASTEXCEPTION",
											  "Argument of lastIndexOf must be String"+
											  " but it "+
											  "is "+x);
					}else if (method.equals("CHARAT") ||
							  method.equals("")){
						Object x = ((OP)right).eval();
						if ( x instanceof Double ){
							return ""+((String)obj).charAt(((Double)x).intValue());
						}else throw new RTException("CASTEXCEPTION",
												  "Must be ($String@charAt num)");
					}else if (method.equals("TRIM")){
						return ((String)obj).trim();
					}else if (method.equals("TOUPPERCASE")){
						return ((String)obj).toUpperCase();
					}else if (method.equals("TOLOWERCASE")){
						return ((String)obj).toLowerCase();
					}else if ( method.equals("TYPE")) {
						return "STRING";
					}else if ( method.equals("ITERATOR")) {
						return new StringIterator((String)obj);
					}else if ( method.equals("NORMALIZE")) {
						return ((String)obj).replace('\n',' ');
					}else if ( method.equals("CONTAINS") ){
						Object x = ((OP)right).eval();
						if (x instanceof Vector){
							for ( Enumeration en =((Vector)x).elements();
								  en.hasMoreElements();){
								Object o = en.nextElement();
								if (o instanceof String){
									if (((String)obj).indexOf((String)o)!= -1)
										return new Double(1);
								}else throw new RTException("CASTEXCEPTION",
								   "Must be ($String@contains string ....)");
							}
							return new Double(0);
						}else if (x instanceof String){
							if (((String)obj).indexOf((String)x)!=-1)
								return new Double(1);
							else return new Double(0);
						}else throw new RTException("CASTEXCEPTION",
									  "Must be ($String@contains string ....)");
					}else throw new RTException("HASNOTMETHOD",
												"String has not method "+method);
				}else if (obj instanceof Object[]){
					if (method.equals("I") ||
						method.equals("")){
						Object x = ((OP)right).eval();
						Object y = OP.doSoftOP(((Tree)left).left);
						if ( x instanceof Double ){
							if ( y instanceof IndexedAlias ){
								return new IndexedAlias(
									(IndexedAlias)y,
									((Double)x).intValue());
							}else 
							return new
								IndexedAlias(((Alias)y).name(),
											 ((Double)x).intValue());
						}else if (x instanceof Vector){
							if ( y instanceof IndexedAlias ){
								return new IndexedAlias(
									(IndexedAlias)y,
									(Vector)x);
							}else 
							return new IndexedAlias(
								((Alias)y).name(),
								(Vector)x);
						}else throw new
								  RTException("CASTEXCEPTION",
											  "Index of array must be number but it "+
											  "is "+x);
					}else if ( method.equals("LENGTH") ||
							   method.equals("SIZE") ){
						return new Double(((Object[])obj).length);
					}else if ( method.equals("TYPE")) {
						return "ARRAY";
					}else if ( method.equals("PRINT")) {
						return printArray((Object[])obj);
					}else if ( method.equals("ITERATOR")) {
						return new ArrayIterator((Object[])obj);
					}else throw new RTException("HASNOTMETHOD",
												"Array has not method "+method);
				}else if (obj instanceof Hashtable){
					if ( method.equals("PUT") ){
						Object x = ((OP)right).eval();
						if ( (x instanceof Vector) && ((Vector)x).size()==2){
							((Hashtable)obj).put(
								((Vector)x).elementAt(0),
								((Vector)x).elementAt(1));
							return obj;
						}else throw new RTException("CASTEXCEPTION",
												  "Must be ($Hash@put any,any)");
					}else if ( method.equals("GET") ||
							   method.equals("")){
						Object x = ((OP)right).eval();
						return ((Hashtable)obj).get(x);
						/*				}else if ( method.equals("KEYS") ){
						 */
					}else if ( method.equals("ISKEY") ){
						Object x = ((OP)right).eval();
						return ((Hashtable)obj).containsKey(x)?
							new Double(1):new Double(0);
					}else if ( method.equals("ISVAL") ){
						Object x = ((OP)right).eval();
						return ((Hashtable)obj).contains(x)?
							new Double(1):new Double(0);
					}else if ( method.equals("TYPE")) {
						return "HASH";
					}else if ( method.equals("ITERATOR")) {
						return new HashIterator((Hashtable)obj);
					}else throw new RTException("HASNOTMETHOD",
												"Hash has not method "+method);
				}else if (obj instanceof Double ){
					if ( method.equals("TYPE")) {
						return "NUMBER";
					}else if ( method.equals("INT")) {
						return ""+((Double)obj).intValue();
					}else if ( method.equals("INTEGER")) {
						return new Integer(((Double)obj).intValue());
					}else throw new RTException("HASNOTMETHOD",
												"NUMBER has not method "+method);
				}else if (obj instanceof Integer ){
					if ( method.equals("TYPE")) {
						return "INTEGER";
					}else if ( method.equals("DOUBLE")) {
						return new Double(((Integer)obj).intValue());
					}else throw new RTException("HASNOTMETHOD",
												"NUMBER has not method "+method);
				}else if ( obj instanceof iterator ){
					if ( method.equals("NEXT") ){
						return ((iterator)obj).next();
					}else if ( method.equals("PREV") ){
						return ((iterator)obj).prev();
					}else if ( method.equals("FIRST") ){
						return ((iterator)obj).first();
					}else if ( method.equals("LAST") ){
						return ((iterator)obj).last();
					}else if ( method.equals("SIZE") ){
						return ((iterator)obj).size();
					}else if ( method.equals("VALUE") ){
						return ((iterator)obj).value();
					}else if ( method.equals("SETVALUE") ){
						return ((iterator)obj).set_value(OP.doHardOP((OP)right));
					}else throw new RTException("HASNOTMETHOD",
												"NUMBER has not method "+method);
				}else if ( method.equals("TYPE") ){
					if (obj instanceof  class_type) 
						return ((class_type)obj).type();
					else return("OBJECT");
				}else if ( obj instanceof class_method ){

					Object x;
					if (!(right instanceof EMPTYOP)) 
						x= OP.doHardOP((OP)right);
					else
						x=null;
					return ((class_method)obj).method(method,x);
				}else{
					throw new RTException("HASNOTMETHOD","Object "+obj+
										  " has not any method");
				}
			}else return ((ExternFunction)left).eval();
		}catch(RTException e){
			String s = expr();
			s = ( s.length() > 50 )?s.substring(0,50):s;
			throw new RTException(e.type,e.getMessage(),e.trap+
								  "\n\t"+GLOBAL.PRINT_LIGHT+
								  "TRAP: Func "+
								  GLOBAL.PRINT_NORMAL+s);
		}finally{
			OP.soft = msoft;
			if (trace) {
				//System.out.println(stab+level+":out from level");
				stab = mstab;
			}
			--level;
		}
	}
	Object call(ARGV V) throws Exception{
		return ((XFunction)left).call(V);
	}
	public String expr(){
		if ( left instanceof String ){
			return "($"+left+" "+((OP)right).expr()+")";
		}else if( left instanceof Tree ){
			return "($"+((Tree)left).left+"@"+
				((Tree)left).right+" "+((OP)right).expr()+")";
		}
		return "("+left+" "+right+")";
	}
/*	public toString(){
		return "($"+left+" "+right+")";
	}
*/
}


