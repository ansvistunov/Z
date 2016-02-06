
/*
 * File: Lexemator.java
 *
 * Created: Fri Apr 23 09:33:30 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc;

import java.util.*;
import loader.GLOBAL;


/**
 * Класс реализует лексический анализатор
 */
public class Lexemator{
	/** идентификатор - алиас, тег, поле .... */
	public static final int LDEF = 0;
	/**  =  */
	public static final int LEQU = 1;
	/** строка  */
	public static final int LSTR = 3;
	/** деествительное число  */
	public static final int LNUM = 4;
	/** ) */
	public static final int LCBK = 5;
	/** (  */
	public static final int LOBK = 6;
	/** конец потока лексем  */
	public static final int LEND = 7; 
	/**   */
	public static final int LOP = 8;
	/**  */
	public static final int LEXPR = 9;
	/** */
	public static final int LTAG = 10;

	
	
	public static final String lextypes[] ={
		"@@_identificator_@@",
		"@@_EQU_@@",
		"",
		"@@_String_@@",
		"@@_NUMBER_@@",
		"@@_')'_@@",
		"@@_'('_@@",
		"@@_LEND_@@",
		"@@_OPERATION_@@",
		"@@_Expression_@@",
		"@@_TAG_@@"
	};

	static final String[] states = {
		"__START__",
		"__IDENTIFER__",
		"__FINISH__",
		"__STRING__",
		"__UNKNOWN__",
		"__NUMBER__",
		"__UNKNOWN__",
		"__COMENT__",
		"__OPERATION__",
		"__TAG__"
	};
	
	static final int SS = 0;
	static final int SAZ = 1;
	static final int SF = 2;
	static final int SSTR = 3;
	static final int SNUM = 5;
	static final int SCOMENT = 7;
	static final int SOP = 8;
	static final int STAG = 9;
	
	char[] text;
	int counter;
	int mytype;
	StringBuffer mystring;
	OP op;
	String args;
	
	boolean is_DIL(char x)
		{return Character.isWhitespace(x);}
	boolean is_AZ(char x)
		{return (x!=':')&&
			 (Character.isLetter(x)||(x == '_')||(x == '.')
			  ||(x == '@')|| ( x == '$'));
		}
	boolean is_XDIL(char x)
		{return ( is_DIL(x) || is_EQU(x) || is_BK(x) || is_S(x) ||
				  (x=='/') || (is_op(x)));}
	boolean is_EQU(char x){return (x == '=');}
	boolean is_BK(char x){return (is_OBK(x)||is_CBK(x));}
	boolean is_OBK(char x){return (x == '(');}
	boolean is_CBK(char x){return (x == ')');}
	boolean is_S(char x){return (x == '\'');}
	boolean is_NUM(char x){return Character.isDigit(x); }
	boolean is_op(char x){
		//System.out.println("operation "+x);
		switch(x){
		case '!':		//System.out.println("test as "+'!');
		case '=':		//System.out.println("test as "+'=');
		case '>':		//System.out.println("test as "+'>');
		case '<':		//System.out.println("test as "+'<');
		case '&':		//System.out.println("test as "+'&');
		case '|':		//System.out.println("test as "+'|');
		case '+':		//System.out.println("test as "+'+');
		case '-':		//System.out.println("test as "+'-');
		case '*':		//System.out.println("test as "+'*');
		case '/':		//System.out.println("test as "+'/');
		case ',':		//System.out.println("test as "+'/');
		case ';':		//System.out.println("test as "+'/');
			return true;
		default: return false;
		}
	}
	/**
	 * Конструктор инициализируется с параметром - разбираемым текстом
	 */
	public Lexemator(char[] text){
		this.text = new char[text.length+1];
		//System.arraycopy(text,0,this.text,0,text.length);
		this.text[text.length]=' ';
		counter = 0;
		op = null;
		for(int i=0;i<text.length;){
			if ( text[i] == '\'' ) {
				this.text[i]=text[i++];
				try{
					while (text[i]!='\''){ // пока не конец строки 
						if ( text[i] == '\\' ){
							this.text[i] = text[i++];
						}
						this.text[i]=text[i++];
					}
					this.text[i]=text[i++];
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if ( text[i] == '#' ) {
				//this.text[i] = ' ';
				try{
					while ( text[i] != '\n' ){
						this.text[i]=' ';
						++i;
					}
				}catch(Exception e){
				}
			}else{
				this.text[i]=text[i];
				++i;
			}
		}
	}

	/**
	 * Метод возвращает следующую лексему
	 */
	public void next() throws Exception{
		int state = SS; // начинаем со стартового состояния :)
		mystring= new StringBuffer();
		if (GLOBAL.calc_debug>26)
			System.out.println("~clac.Lexemator::next : begin parsing");
		try{ // отлавливаем конец потока символов
			char ch = text[counter]; 
			while(true){ // цикл по символам
				if ( ch == '$' ) {
					ch = text[++counter];
					state = STAG;
				}
				if (GLOBAL.calc_debug>26)
					System.out.println("~clac.Lexemator::next : state="+states[state]+":"+counter+":"+ch);
				switch(state){ // витвление по состоянию автомата
			// start ---------------------------------------------------
				case SS: // стартовое состояние
					// крутимся пока не начнется лексема
					if ( is_DIL(ch) ){ // если разделитель
						state = SS; // опять на старт
						ch=text[++counter];
					}
					// началась лексема
					// проверим на операцию
					else if ( is_op(ch) ) state = SOP; // операция
					else if ( is_AZ(ch) ) state = SAZ; // идентификатор
					// проверим на начало строки
					else if ( ch == '\'' ) state = SSTR; // строка
					// проверим на начало выражения
					else if ( is_OBK(ch) ) {/* ( */ 
						++counter;
						mystring = readExpr();
						ch=text[counter];
						state = SF;
						mytype = LEXPR;
					}
					// проверим на число
					else if ( is_NUM(ch) ) state = SNUM; // число
					else {// что-то непонятное
						ch = text[counter++];
						throw new ResonException("Lexemator: '"+ch+"'");
					}
					break;
		   // Разбор идентификатора или метки --------------------------------------
				case SAZ: // идентификатор
					while(is_AZ(ch)||is_NUM(ch)){
						// пока символы алфавита
						mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( ch == ':' ){
						op = new LABEL(mystring.toString());
						mytype = LOP;
						state = SF;
						ch = text[++counter];
					}
					else if ( is_XDIL(ch) ){ // конец идентификатора
						mytype = LDEF;
						state = SF; // финиш
					}
					else throw new ResonException("Lexemator: '"+ch+"'");
					break;
		   // Разбор тега   -------------------------------------------
				case STAG: // тег
					while(is_AZ(ch)||is_NUM(ch)||
						  (ch == '(') ){
						// пока символы алфавита
						if ( ch == '(' ){
							++counter;
							mystring.append("("+readExpr()+")");
							--counter;
						}
						else mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( is_XDIL(ch) ){ // конец ТЕГА
						mytype = LTAG;
						state = SF; // финиш
					}
					else throw new ResonException("Lexemator: '"+ch+"'");
					args = new
						String(text,counter,text.length-counter);
					if ( GLOBAL.calc_debug >2 )
						System.out.println("~clac.Lexemator::next : TAG "+mystring+
							"args "+args);
					counter = text.length;
					break;
		  // Разбор числа ----------------------------------------------------
				case SNUM: // число
					while(is_NUM(ch)){
						// пока цифры
						mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( is_XDIL(ch) ){
							mytype = LNUM;
							state = SF; // финиш
							break;
					}else if ( ch == '.' ){
						mystring.append(".");
						ch = text[++counter];
						while(is_NUM(ch)){ // пока цифры
							mystring.append(ch); // добавить в строку резалта
							ch = text[++counter];
						}
						if ( is_XDIL(ch) ){ // конец числа
							mytype = LNUM;
							state = SF; // финиш
							break;
						}
					}
					throw new ResonException("Lexemator: '"+ch+"'");
		  // Разбор операции  ----------------------------------------------------
				case SOP:
					op = OP.getOP(ch,text[counter+1]);
					if (op != null){
						counter+=2;
						ch = text[counter];
					}else if ( (op = OP.getOP(ch)) != null ){
						ch=text[++counter];
					}else throw new
							  ResonException("Lexemator: Bad Operation '"+
										ch+
										"'");
					mytype = LOP;
					state =SF;
					mystring = new StringBuffer(op.toString());
					break;
					
		   // Разбор строки  ----------------------------------------------------
				case SSTR: // разбор строки
					ch=text[++counter];
					while (ch!='\''){ // пока не конец строки 
						if ( ch == '\\' ){
							ch = text[++counter];
							switch(ch){
							case 't':case 'T':ch = '\t';break;
							case 'n':case 'N':ch = '\n';break;
							case 'b':case 'B':ch = '\b';break;
							case 'e':case 'E':ch = '\033';break;
							}
						}
						mystring.append(ch); // записываем символы строки
						ch=text[++counter];
					}
					mytype = LSTR;
					state=SF; // финиш
					++counter;
					break;
		  // stop -----------------------------------------------------------
				case SF: // ФИНИШ
					if (GLOBAL.calc_debug>26)
						System.out.println("~clac.Lexemator::"+GLOBAL.PRINT_LIGHT+
										   lextypes[mytype]+
										   ":"+
										   as_string()+
										   GLOBAL.PRINT_NORMAL+"\n");
					return;


		  // error ??  -----------------------------------------------------------
				default:
					throw new Exception("Lexemator: !!!! UNCKNOWN STATE ????");
				}	
			}

		}catch(ArrayIndexOutOfBoundsException e){
			//e.printStackTrace();
			mytype=LEND;
			return;
		}
	}


	/**
	 *
	 */
	StringBuffer readExpr() throws Exception{
//		if (GLOBAL.calc_debug>26)
//			System.out.println("~clac.Lexemator::redExpr");

		int xc = 1;
		try{
			char ch = text[counter];
			StringBuffer sb = new StringBuffer();
			while(true){
				ch = text[counter];
				if ( ch == ')' ) --xc;
				else if ( ch == '(' ) ++xc;
				if ( xc == 0 ) break;
				//System.out.print(ch);
				sb.append(ch);
				++counter;
			}
			++counter;
//		if (GLOBAL.calc_debug>26)
//			System.out.println("\n~clac.Lexemator::redExpr expr="+sb.toString());
			return sb;
		}catch(IndexOutOfBoundsException e){
			throw new ResonException("~~clac.Lexemator::redExpr may be '"+
									 ((xc>0)?')':'(')+"' ?");
		}
	}
	
	
	/**
	 * дать тип лексемы
	 */
	public int type(){
		//if (GLOBAL.calc_debug>26)
		//	System.out.println("~clac.Lexemator::next lextype "+mytype);
		return mytype;
	}

	/**
	 * дать лексему как строку
	 */
	public String as_string(){
		String s;
		if ( mytype == LDEF ) s =  mystring.toString().toUpperCase();
		else s =  mystring.toString();
		if (GLOBAL.calc_debug>26)
			System.out.println("~clac.Lexemator::as_string ? "+s);
		return s;
	}

 	/**
	 * дать лексему как целое число
	 */
	public int as_int() throws Exception{
		String s = mystring.toString();
		if (GLOBAL.calc_debug>26)
			System.out.println("~clac.Lexemator::as_int str is "+s+"!");
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			if (GLOBAL.calc_debug>26)
				System.out.println("~clac.Lexemator::as_int "+e);
			throw new Exception();
		}
	}

	/**
	 * дать лексему как действительное число
	 */
	public double as_double() throws Exception{
		String s = mystring.toString();
		if (GLOBAL.calc_debug>26)
			System.out.println("~clac.Lexemator::as_double str is "+s+"!");
		try{
			Double d = Double.valueOf(s);
			return d.doubleValue();
		}catch(NumberFormatException e){
			if (GLOBAL.calc_debug>26)
				System.out.println("~clac.Lexemator::as_double "+e);
			throw new Exception();
		}
	}
	public OP as_op() throws Exception{
		return op;
	}
	public String args(){
		return args;
	}
}







