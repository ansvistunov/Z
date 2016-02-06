/*
 * File: Parser.java
 *
 * Created: Mon Mar 15 08:41:00 1999
 *
 * Copyright(c) by Alexey Chen
 */

package rml;

/**
 * Парсер для RML'я.
 * Реализован через построение иерархического списка своиств обьектов 
 * языка RML . И построение по списку реальной структуры обьекта. 
 */

import java.util.*;
import loader.*;


/*
  class ParserError{

	public static final int SYMANTEC = 0;
	public static final int SYNTAX = 1;
	public static final int LEXIC = 2;
	public static final int FSTR = 3;
	public static final int FSYM = 4;
	public static final int FPROP = 5;
	public static final int NFTAG = 6;
	public static final int BNUM = 7;
	public static final int BSTR = 8;
	public static final int BID = 9;



//	public static final int UNKNOW = 7;
	

	static final String[] errors = {
		"Symantec error",
		"Syntax error",
		"Lexcical error",
		"Found string , but must be identificator",
		"Found symbol '=' without proper name",
		"Found proper after body",
		"No found tag",
		"Bad number",
		"Bad string",
		"Bad identificator"
	};
		
	public static void error(int errno,int line) throws Exception{
		System.out.println(line+":"+errors[errno]);
		throw new Exception("Parser error");
	}
}

*/

/**
 * Класс Lexemator реализует лексический анализатор используемый
 * в синтаксическом анализаторе парсера RML'я.
 */
class Lexemator{
	/** идентификатор - алиас, тег, поле .... */
	public static final int LDEF = 0;
	/**  =  */
	public static final int LEQU = 1;
	/** целое число  */
	public static final int LINT = 2;
	/** строка  */
	public static final int LSTR = 3;
	/** деествительное число  */
	public static final int LFLT = 4;
	/** } */
	public static final int LCBK = 5;
	/** {  */
	public static final int LOBK = 6;
	/** конец потока лексем  */
	public static final int LEND = 7; 

	static final String lextypes[] ={
		"identificator",
		"EQU",
		"Integer",
		"String",
		"float",
		"'}'",
		"'{'",
		"LEND"
	};

	static final int SS = 0;
	static final int SAZ = 1;
	static final int SF = 2;
	static final int SSTR1 = 3;
	static final int SSTR2 = 4;
	static final int SINT = 5;
	static final int SFLT = 6;
	static final int SCOMENT = 7;
	
	char[] text;
	int counter;
	int mytype;
	StringBuffer mystring;
	int line=0;
	
	boolean is_DIL(char x)
		{return Character.isWhitespace(x);}
	boolean is_AZ(char x)
		{return (Character.isLetter(x)||(x == '_')
				 ||(x == '.')||(x == ',')
				 ||(x == '(')||(x == ')')||(x == '|')||(x == '$'));}
	boolean is_XDIL(char x)
		{return ( is_DIL(x) || is_EQU(x) || is_BK(x) || is_S(x) || (x=='/') );}
	boolean is_EQU(char x){return (x == '=');}
	boolean is_BK(char x){return (is_OBK(x)||is_CBK(x));}
	boolean is_OBK(char x){return (x == '{');}
	boolean is_CBK(char x){return (x == '}');}
	boolean is_S(char x){return (/*is_S1(x)||*/is_S2(x));}
//	boolean is_S1(char x){return /*(x == '\'')*/;}
	boolean is_S2(char x){return (x == '\"');}
	boolean is_NUM(char x){return Character.isDigit(x); }
	
	/**
	 * Конструктор инициализируется с параметром - разбираемым текстом
	 */
	public Lexemator(char[] text){
		this.text = text;
		counter = 0;
		line = 1;
	}

	/**
	 * Метод возвращает следующую лексему
	 */
	public int next() throws Exception{
		int state = SS; // начинаем со стартового состояния :)
		char ch = text[counter]; 
		mystring= new StringBuffer();
		//if (GLOBAL.parser_debug>2)
		//System.out.println("~rml.Lexemator::next : begin parsing");
		try{ // отлавливаем конец потока символов
			while(true){ // цикл по символам
				if ( ch == '\n' ) ++line;
				switch(state){ // витвление по состоянию автомата
				case SS: // стартовое состояние
					if ( is_DIL(ch) ){ // если разделитель
						state = SS; // опять на старт
						ch=text[++counter];
					}
					else if ( is_AZ(ch) )
						state = SAZ; // идентификатор
					//else if ( is_S1(ch) )
					//	state = SSTR1; // строка первого типа
					else if ( is_S2(ch) )
						state = SSTR2; // сторока второго типа
					else if ( is_EQU(ch) ) { // =
						mytype = LEQU; 
						state = SF; // конец разбора лексемы
						++counter;	
					}else if ( is_OBK(ch) ) { // {
						mytype = LOBK; 
						state = SF;	// финиш
						++counter;	
					}else if ( is_CBK(ch) ) { // }
						mytype = LCBK; 
						state = SF;	// финиш
					++counter;	
					}else if ( is_NUM(ch) ) state = SINT; // число
					else if ( ch == '/' ) {
						if ( text[counter+1] == '/' ){
							ch = text[counter+=2];
							try{
								while ( ch != '\n' ) ch = text[++counter];
								++line;
							}catch(Exception e){
								System.out.println(
									"~rml.Lexemator::next coments \n\t"+e);
							}
						}
					}
					else // что-то непонятное
						ch = text[++counter];
						/*throw new Exception("Lexemator: '"+ch+"' in SS");*/
					break;
				case SAZ: // идентификатор
					while(is_AZ(ch)||is_NUM(ch)){
						// пока символы алфавита
						mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( is_XDIL(ch) ){ // конец идентификатора
						mytype = LDEF;
						state = SF; // финиш
					}
					else throw new Exception("Lexemator: '"+ch+"' in SAZ line "+line);
					break;
				case SINT: // число
					while(is_NUM(ch)){
						// пока цифры
						mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( ch == '.' ) state = SFLT;
					else if ( is_XDIL(ch) ){ // конец числа
						mytype = LINT;
						state = SF; // финиш
					}
					else throw new Exception("Lexemator: '"+ch+"' in SINT line "+line);
					break;
				case SFLT: // действительное число
					mystring.append(".");
					ch = text[++counter];
					while(is_NUM(ch)){
						// пока цифры
						mystring.append(ch); // добавить в строку резалта
						ch = text[++counter];
					}
					if ( is_XDIL(ch) ){ // конец числа
						mytype = LFLT;
						state = SF; // финиш
					}
					else throw new Exception("Lexemator: '"+ch+"' in SFLT line "+line);
					break;
				case SSTR2: // разбор строки
					ch=text[++counter];
					while (!is_S2(ch)){ // пока не конец строки
						if ( ch == '\''){
							mystring.append(ch);
							ch = text[++counter];
							while ( ch != '\'' ){
								mystring.append(ch);
								if ( ch == '\\' ){
									ch = text[++counter];
									mystring.append(ch);
								}
								ch = text[++counter];
							}
						}else if ( ch == '\\') ch = text[++counter];
						mystring.append(ch); // записываем символы строки
						ch=text[++counter];
					}
					mytype = LSTR;
					state=SF; // финиш
					++counter;
					break;
/*				case SSTR1: // также как и предыдущий вариант
					ch=text[++counter];
					while (!is_S1(ch)){
						mystring.append(ch);
						ch=text[++counter];
					}
					mytype = LSTR;
					state=SF;
					++counter;
					break;
*/				case SF: // ФИНИШ
					if (GLOBAL.parser_debug>2)
						System.out.println("~rml.Lexemator::"+GLOBAL.PRINT_LIGHT+
										   lextypes[mytype]+
										   GLOBAL.PRINT_NORMAL);
					return counter;

				default:
					throw new Exception("Lexemator: !!!! R U normal !!!!");
				}	
			}

		}catch(ArrayIndexOutOfBoundsException e){
			mytype=LEND;
			return counter;
		}
	}

	/**
	 * дать тип лексемы
	 */
	public int type(){
		//if (GLOBAL.parser_debug>2)
		//	System.out.println("~rml.Lexemator::next lextype "+mytype);
		return mytype;
	}

	/**
	 * дать лексему как строку
	 */
	public String as_string(){
		String s;
		if ( mytype == LDEF ) s =  mystring.toString().toUpperCase();
		else s =  mystring.toString();
		if (GLOBAL.parser_debug>2)
			System.out.println("~rml.Lexemator::as_string ? "+s);
		return s;
	}

 	/**
	 * дать лексему как целое число
	 */
	public int as_int() throws Exception{
		String s = mystring.toString();
		if (GLOBAL.parser_debug>2)
			System.out.println("~rml.Lexemator::as_int str is "+s+"!");
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			if (GLOBAL.parser_debug>2)
				System.out.println("~rml.Lexemator::as_int "+e);
			throw new Exception();
		}
	}

	/**
	 * дать лексему как действительное число
	 */
	public double as_double() throws Exception{
		String s = mystring.toString();
		if (GLOBAL.parser_debug>2)
			System.out.println("~rml.Lexemator::as_double str is "+s+"!");
		try{
			Double d = Double.valueOf(s);
			return d.doubleValue();
		}catch(NumberFormatException e){
			if (GLOBAL.parser_debug>2)
				System.out.println("~rml.Lexemator::as_double "+e);
			throw new Exception();
		}
	}
}

/**
 *	Парсер
 */
public class Parser{
	static final int  SS = 0;
	static final int  STAG = 1;
	static final int  SP = 2;
	static final int  SPROP = 3;
	static final int  SVSD = 4;	
	static final int  SINT = 5;
	static final int  SFLT = 6;
	static final int  SSTR = 7;
	static final int  SDEF = 8;
	static final int  SBK = 9; 
	static final int  SOBK = 10;
	static final int  SF = 11;

	static final String sts[] = {
		"START",
		"TAG",
		"SP",
		"PROP",
		"SVSD",
		"INT",
		"FLOAT",
		"STRING",
		"IDENT",
		"SBK",
		"SOBK",
		"FINISH"
	};
	
	/**
	 * построить по потоку лексем дерево свойств
	 */

	static final String rp = GLOBAL.PRINT_BLUE+"~rml.Parser::paser "+GLOBAL.PRINT_NORMAL;
	
	public static Proper parser(Lexemator lex) throws Exception{
		return parser(lex,null);
		
	}
	
	static Proper parser(Lexemator lex, ParserObserver po) throws Exception{
		int cc=0;
		int state = SS;
		Proper prop = new Proper();
		String propName="none";
		if (GLOBAL.parser_debug>1)
			System.out.println(rp+"start parsing");
		while(true){// цикл по лексемам
			if (GLOBAL.parser_debug>1)
				System.out.println(rp+sts[state]);
			switch(state){
			case SS:
				cc = lex.next();
				if (lex.type() == Lexemator.LDEF){
					state = STAG;
				}else state = SBK;
				if (po!=null) po.SS(cc);
				
			break;
			case STAG:// обработка тега
				prop.tag = lex.as_string();
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+sts[SP]);
				if (po!=null) po.STAG(cc,prop.tag);
			// none BREAK;	!!!!
			case SP:
				cc = lex.next();
				if (lex.type() == Lexemator.LDEF){
					state = SPROP;
				}else state = SBK;
				if (po!=null) po.SP(cc);
			break;
			case SPROP:// свойства
				propName = lex.as_string();
				cc = lex.next();
				if (po!=null) po.SPROP(cc,propName);
				if ( lex.type() != Lexemator.LEQU ){ 
					prop.put(propName,"");
					if (lex.type() == Lexemator.LDEF){
						state = SPROP;
					}else{
						state = SBK;
					}
					
					break;
				}
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+sts[SVSD]);
			// none BREAK;	!!!!
			case SVSD:// установка значения свойства
				cc = lex.next();
				state=SP;
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+" lexema "+lex.type());
				
				if (po!=null) po.SVSD(cc, lex.as_string());
				switch(lex.type()){
				case Lexemator.LINT:
					prop.put(propName,new Integer(lex.as_int()));
				break;
				case Lexemator.LFLT:
					prop.put(propName,new Double(lex.as_double()));
				break;
				case Lexemator.LSTR:
				case Lexemator.LDEF:
					if (GLOBAL.parser_debug>1)
						System.out.println(rp+": = str");
					prop.put(propName,lex.as_string());
				break;	
				default: 
					throw new Exception(rp+"No reaction,(proper = ?????) line "+
						lex.line+"\n\tstring value = "+lex.as_string());
				}
			break;
			case SBK:// обработка вложений
				if (lex.type() == Lexemator.LOBK ) state = SOBK;
				else if (lex.type() == Lexemator.LCBK){
					state = SF;
					if (GLOBAL.parser_debug>1)
						System.out.println(rp+"------- } ");
					cc = lex.next();
				}else state= SF;
				if (po!=null) po.SBK(cc);
/*throw new Exception(rp+"\n\t"+
  GLOBAL.PRINT_LIGHT+
  "RML error in line "+lex.line+
  GLOBAL.PRINT_NORMAL);
*/
			break;
			case SOBK:// новое вложение
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+"------- { ");
				Proper p = parser(lex,po);
				Proper.add(prop,p);
				state = SBK;
				if (lex.type() == Lexemator.LEND) state = SF;
				if (po!=null) po.SOBK(cc);
			break;
			case SF:// конечное состояние
				if (po!=null) po.SF(cc);
				return prop;

			default:
				throw new Exception(rp+" Uncnown Error!!!!!!!");
			}
		}
	}

	/**
	 * построить дерево свойств по тексту описания документов
	 * с макроподстановкой значений из args
	 */
	public static Proper createProper(char[] text, Object[] args)
		throws Exception {
		return createProper(text,args,null);
	}
	
	public static Proper createProper(char[] text, Object[] args, ParserObserver po)
			throws Exception {
			GLOBAL.waitin();
			try{
				Lexemator lex = new Lexemator(text);
				Proper.clearDefault();
				Proper prop = parser(lex,po);
				if (GLOBAL.parser_debug>0) prop.dump();
				return prop;
			}finally{
				GLOBAL.waitout();
			}
		}
	

	/**
	 * рекурсивное построение документа по дереву свойств
	 */
	public static Object[] getContent(Proper prop,Hashtable aliases){
		Object[] res;	
		if (prop.content == null){
			return new Object[0];
		}
		int i = 0;
		prop = prop.content;
		for (Proper foo = prop; foo != null ; foo = foo.next ) ++i;
		res = new Object[i];
		Class cl;
		ParsedObject pobj;
		GLOBAL.waitin();
		try{
			for (i=0 ; prop!=null ; prop = prop.next, ++i) {
				try{
					String clName = "rml."+prop.tag;
					cl = GLOBAL.cl.loadClass(clName.toString());
					pobj = (ParsedObject)cl.newInstance();
					res[i] = pobj.doParsing(prop,aliases);
				}catch(Exception e){
					//System.out.println("~rml.Parser::getContent "+e);
					res[i] = null;
				}
			}
		}finally{
			GLOBAL.waitout();
		}
		return res;
	}
}


