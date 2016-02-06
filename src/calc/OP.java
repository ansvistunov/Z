
/*
 * File: OP.java
 *
 * Created: Fri Apr 23 09:29:57 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.*;
import loader.GLOBAL;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
/**
 * Суть операция
 */

public abstract class OP extends Tree{
	int prior = 0;
	char sym =' ';
	char sym2 = ' ';

	public static boolean trace = false;
	
	public static Hashtable aliases;
	public static Hashtable functions;
	public static boolean soft = false;
	
	public static final int ALIAS_PRIOR = 12;
	public static final int U_PRIOR = 10;
	public static final int AM_PRIOR = 6;
	public static final int MD_PRIOR = 8;
	public static final int L_PRIOR = 4;
	public static final int Q_PRIOR = 2;
	public static final int SET_PRIOR = 1;

	public static String printArray(Object[] obj){
		//System.out.println("print Array "+obj);
		if ( obj == null ) return "[null]";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		int i = 0;
		int len = ((Object[])obj).length;
		if ( len == 0 ) return "[]";
		while (true){
			Object x = ((Object[])obj)[i];
			if ( x instanceof Object[] ){
				sb.append(printArray((Object[])x));
			}else{
				sb.append(x);
			}
			++i;
			if (i<len)
				sb.append(",");
			else break;
		}
		sb.append("]");
		return sb.toString();
	}

	public static Object doSoftOP(Object a) throws Exception{
		boolean x = soft;
		soft = true;
		try{
			return doOP(a);
		}finally{
			soft = x;
		}
	}

	public static Object doHardOP(Object a) throws Exception{
		boolean x = soft;
		soft = false;
		try{
			return doOP(a);
		}finally{
			soft = x;
		}
	}
	
	public static Object doOP(Object x) throws Exception{
		//System.out.println("~calc.OP::doOP:: !!!!!!!!!!!!!!!!!!! BEGIN DOOP");
		while ( x instanceof OP ){
			if (GLOBAL.calc_debug>2)
				System.out.println("~calc.OP::doOP eval "+((OP)x).expr());
			if ( soft && (x instanceof Alias) ) break;
			x = ((OP)x).eval();
		}
		//if (GLOBAL.calc_debug>2)
		//System.out.println("~calc.OP::doOP result "+x);
		//System.out.println("~calc.OP::doOP !!!!!!!!!!!!!!!!!!! END DOOP");
		return x;
	}
	public String toString(){
		return ""+sym+sym2;
	}
	String expare(Object x){
		if ( x == null ) return "#NULL#";
		if ( x instanceof OP ) return ((OP)x).expr();
		return x.toString();
	}
	public String expr(){
		return "("+expare(left)+" "+toString()+" "+expare(right)+")";
	}

	public static void setAliases(Hashtable aliases){
	    //System.out.println("set aliases "+aliases);
		OP.aliases = aliases;
		if ( aliases != null ){
			Hashtable o = (Hashtable)aliases.get("##functions##");
			if ( o == null ){
				o = new Hashtable();
				aliases.put("##functions##",o);
			}
			OP.functions = o;
		}else{
			OP.functions = null;
		}
	}

	public static Hashtable getAliases(){
		return aliases;
	}
	
	public void getAliases(Hashtable h) throws Exception{
		//System.out.println("getAliases in object "+this.expr());
		if ((left!=null)&&(left instanceof OP))
			((OP)left).getAliases(h);
		if ((right!=null)&&(right instanceof OP))
			((OP)right).getAliases(h);
	}
	
	public synchronized Object eval(Hashtable aliases) throws Exception{
		boolean xsoft = OP.soft;
		OP.soft = false;
		Hashtable foo = OP.aliases;
		setAliases(aliases);
		boolean xtrace = OP.trace;
		OP.trace = false;
		try{
			return eval();
		}catch(Exception e){
			System.out.println("~calc.OP::eval EVAL_EXCEPTION::");
			//e.printStackTrace();
			throw e;
		}finally{
			setAliases(foo);
			OP.soft = xsoft;
			OP.trace = xtrace;
		}
	}
	public abstract Object eval() throws Exception; 
	public Object evalLabel(String label) throws Exception{
		if ( GLOBAL.calc_debug > 2 )
			System.out.println("~calc.OP::evalLabel label "+label);
		throw new ResonException("label "+label+" Not found");
	}
	public static OP getOP(char sym){
		return getOP(sym,' ');
	}
   	public static OP getOP(char sym,char sym2){
		switch(sym2){
		case '=':
			switch(sym){
			case '=': return new EQU();
			case '>': return new EQUABOVE();
			case '<': return new EQUBELOW();
			case '!': return new NOTEQU();
			case '+': return new SETPLUS();
			case '-': return new SETMINUS();
			case '*': return new SETMUL();
			case '/': return new SETDIV();
			case '&': return new SETAND();
			case '|': return new SETOR();
			default: return null;
			}
/*		case '+':
			if ( sym == '+' ){
			}
		case '-':
			if ( sym == '-' ){
			}
*/		case ' ':
			switch(sym){
			case '=': return new SET();
			case '>': return new ABOVE();
			case '<': return new BELOW();
			case '+': return new PLUS();
			case '-': return new MINUS();
			case '*': return new MUL();
			case '/': return new DIV();
			case '&': return new AND();
			case '|': return new OR();
			case '!': return new NOT();
			case ';': return new COMA();
			case ',': return new COMA();
			default: return null;
			}
		default: return null;
		}
	}
	public OP setOP(OP op){
		if ( GLOBAL.calc_debug >2 )
			System.out.println("~calc.OP::setOP\n\top="+op.expr()+
							   "\n\tthis="+this.expr());
		if (prior < op.prior) {
			if ( GLOBAL.calc_debug >2 )
				System.out.println("~calc.OP::setOP\n\tRIGHT="+op.expr()+
							   "\n\tTOP="+this);
			// оптимизация остсточной рекурсии
			if ((right!=null)&&
				(right instanceof OP) &&
				!(right instanceof Const)){
				right = ((OP)right).setOP(op);
				return this;
			}else{
				op.left = right;
				right = op;
				return this;
			}
		}else{
			if ( GLOBAL.calc_debug >2 )
				System.out.println("~calc.OP::setOP\n\tTOP="+op+
							   "\n\tLEFT="+this.expr());
			op.left = this;
			return op;
		}
	}
	public void setOperand(Object foo) throws Exception{
		if ( right != null ){
			if ( (right instanceof Const) || !(right instanceof OP) )
				throw new Exception("try set operand in Not null point");
			else if (right instanceof OP)
				((OP)right).setOperand(foo);
		}else{
			right = foo;
		}
	}
}



abstract class Arifmetic extends OP{
	abstract double ops(double a,double b);
	public Object eval() throws NullPointerException,ClassCastException,Exception{
		//System.out.println("left="+left+",rihgt="+right);
		//System.out.println(aliases);
		//System.out.println(((ARGV)aliases.get("V")).hash());
		//System.out.println(""+(doOP(left))+
		//	sym+
		//	(doOP(right)));
		return new Double(ops(
			((Double)doHardOP(left)).doubleValue(),
			((Double)doHardOP(right)).doubleValue()
			));
	}
}

abstract class Logical extends OP{
	abstract boolean ops(boolean a,boolean b);
	public boolean instof(Object x)
		throws Exception,NullPointerException,ClassCastException {
		try{
			return ((Double)x).doubleValue()!=0;
		}catch(Exception e){
			return ((String)x).trim().toUpperCase().compareTo("TRUE")==0;
		}
	}
	public Object eval() throws Exception,NullPointerException,ClassCastException{
		//if ( GLOBAL.calc_debug > 2 )
		//	System.out.println(doOP(left)+toString()+doOP(right));
		int res;
		if ( ops(instof(doHardOP(left)),instof(doHardOP(right)))) res = 1;
		else res = 0;
		if ( GLOBAL.calc_debug > 20 )
			System.out.println(doHardOP(left)+toString()+doHardOP(right)+" = "+res);
		return new Double(res);
	}
}

class PLUS extends OP{
	public PLUS(){sym='+';left=new Double(0);prior=AM_PRIOR;}
	public Object eval() throws NullPointerException,ClassCastException,Exception{
		//System.out.println("left="+left+",rihgt="+right);
		//System.out.println(aliases);
		//System.out.println(((ARGV)aliases.get("V")).hash());
		//System.out.println(""+(doOP(left))+
		//	sym+
		//	(doOP(right)));
		Object a = doHardOP(left);
		Object b = doHardOP(right);
		if ( a instanceof Double ){
			return new Double(
			((Double)a).doubleValue()+
			((Double)b).doubleValue());
		}else if ( a instanceof String ){
			if ( b instanceof Object[] ) b = printArray((Object[])b);
			return (String)a + b.toString();
		}else throw new ResonException("~calc.PLUS::eval  PLUS may added only\n\t"+
									   "String+Double\n\t"+
									   "String+String\n\t"+
									   "Double+Double");
	}
}
class MINUS extends Arifmetic implements Unar{
	public MINUS(){sym='-';left=new Double(0);prior=AM_PRIOR;}
	double ops(double a, double b){return a-b;}
}
class MUL extends Arifmetic{
	public MUL(){sym='*';prior=MD_PRIOR;}
	double ops(double a, double b){return a*b;}
}
class DIV extends Arifmetic{
	public DIV(){sym='/';prior=MD_PRIOR;}
	double ops(double a, double b){return a/b;}
}


class EQU extends OP{
	public EQU(){sym='=';sym2='=';prior=Q_PRIOR;}
	public Object eval() throws Exception,NullPointerException,ClassCastException{
		int res;
		if ( doHardOP(left).equals(doHardOP(right)) ) res = 1;
		else res = 0;
		if ( GLOBAL.calc_debug > 20 )
			System.out.println("~calc.EQU::eval "+
							   doOP(left)+toString()+doOP(right)+" = "+res);
		return new Double(res);
	}
} 
class AND extends Logical{
	public AND(){sym='&';prior=L_PRIOR;}
	boolean ops(boolean a,boolean b){
		return a&&b;
	}
}
class OR extends Logical{
	public OR(){sym='|';prior=L_PRIOR;}
	boolean ops(boolean a,boolean b){
		return a||b;
	}
} 
class NOT extends Logical implements Unar{
	public NOT(){sym='!';left=new Double(0);prior=U_PRIOR;}
	boolean ops(boolean a,boolean b){
		return !b;
	}
	public String expr(){
		return "("+toString()+expare(right)+")";
	}
}

class ABOVE extends Arifmetic{
	public ABOVE(){sym='>';prior=L_PRIOR;}
	double ops(double a,double b){return  ( a>b )? 1:0;}
} 
class BELOW extends Arifmetic{
	public BELOW(){sym='<';prior=L_PRIOR;}
	double ops(double a,double b){return  ( a<b )? 1:0;}
} 
class EQUABOVE extends Arifmetic{
	public EQUABOVE(){sym2='=';prior=L_PRIOR;}
	double ops(double a,double b){return  ( a>=b )? 1:0;}
} 
class EQUBELOW extends Arifmetic{
	public EQUBELOW(){sym2='=';prior=L_PRIOR;}
	double ops(double a,double b){return  ( a<=b )? 1:0;}
}

class SET extends OP{
	public SET(){sym='=';prior=SET_PRIOR;}
	public static Object set(Object a,Object b)
		throws NullPointerException,ClassCastException,Exception{
		if (GLOBAL.calc_debug>20)
			System.out.println("~calc.SET::static#set a="+
							   ((a instanceof OP)?((OP)a).expr():a)+
							   " b="+
							   ((b instanceof OP)?((OP)b).expr():b));
		while(!(a instanceof Alias)){
			if (GLOBAL.calc_debug>20)
				System.out.println("~calc.SET::static#set a is "+a);
			if ( a instanceof OP )
				a = ((OP)a).eval();
			else throw new Exception("Can't set Constant to new value");
		}
		((Alias)a).setValue(doOP(b));	
		return ((Alias)a).getValue();
	}
	public Object eval() throws Exception{
		return set(left,right);
	}
}

class SETPLUS extends PLUS{
	public SETPLUS(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
}
class SETMINUS extends MINUS{
	public SETMINUS(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
}
class SETMUL extends MUL{
	public SETMUL(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
}
class SETDIV extends DIV{
	public SETDIV(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
}


class SETAND extends AND{
	public SETAND(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
} 
class SETOR extends OR{
	public SETOR(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		return SET.set(left,super.eval());
	}
} 


class NOTEQU extends EQU{
	public NOTEQU(){sym2='=';prior=SET_PRIOR;}
	public Object eval() throws Exception{
		if ( ((Double)super.eval()).doubleValue() == 0 )
			return new Double(1);
		else return new Double(0);
	}
} 

