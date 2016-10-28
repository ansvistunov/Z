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
	
	/*public static Proper parser(Lexemator lex) throws Exception{
		return parser(lex,null);
		
	}*/
	
	static Proper parser(Lexemator lex /*, ParserObserver po*/, int lexcount) throws Exception{
		int cc=0;
		int state = SS;
		Proper prop = new Proper(lexcount++);
		String propName="none";
		if (GLOBAL.parser_debug>1)
			System.out.println(rp+"start parsing");
		while(true){// цикл по лексемам
			if (GLOBAL.parser_debug>1)
				System.out.println(rp+sts[state]);
			switch(state){
			case SS:
				cc = lex.next();
				prop.s = cc;
				if (lex.type() == Lexemator.LDEF){
					state = STAG;
					
				}else state = SBK;
				//if (po!=null) po.SS(cc);
				
				
			break;
			case STAG:// обработка тега
				prop.tag = lex.as_string();
				//prop.s = cc;
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+sts[SP]);
				//if (po!=null) po.STAG(cc,prop.tag);
			// none BREAK;	!!!!
			case SP:
				cc = lex.next();
				if (lex.type() == Lexemator.LDEF){
					state = SPROP;
				}else state = SBK;
				//if (po!=null) po.SP(cc);
			break;
			case SPROP:// свойства
				propName = lex.as_string();
				cc = lex.next();
				//if (po!=null) po.SPROP(cc,propName);
				prop.put(propName,"",cc,-1);
				if ( lex.type() != Lexemator.LEQU ){ 
					prop.put(propName,"",cc,-1);
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
				
				//if (po!=null) po.SVSD(cc, lex.as_string());
				switch(lex.type()){
				case Lexemator.LINT:
					prop.put(propName,new Integer(lex.as_int()),-1,cc);
				break;
				case Lexemator.LFLT:
					prop.put(propName,new Double(lex.as_double()),-1,cc);
				break;
				case Lexemator.LSTR:
				case Lexemator.LDEF:
					if (GLOBAL.parser_debug>1)
						System.out.println(rp+": = str");
					prop.put(propName,lex.as_string(),-1,cc);
				break;	
				default: 
					throw new Exception(rp+"No reaction,(proper = ?????) line "+
						lex.line+"\n\tstring value = "+lex.as_string());
				}
			break;
			case SBK:// обработка вложений
				if (lex.type() == Lexemator.LOBK ) {
					state = SOBK;
				}
				else if (lex.type() == Lexemator.LCBK){
					
					state = SF;
					if (GLOBAL.parser_debug>1)
						System.out.println(rp+"------- } ");
					cc = lex.next();
				}else state= SF;
				//if (po!=null) po.SBK(cc);
/*throw new Exception(rp+"\n\t"+
  GLOBAL.PRINT_LIGHT+
  "RML error in line "+lex.line+
  GLOBAL.PRINT_NORMAL);
*/
			break;
			case SOBK:// новое вложение
				if (GLOBAL.parser_debug>1)
					System.out.println(rp+"------- { ");
				
				Proper p = parser(lex /*,po*/,lexcount++);
				Proper.add(prop,p);
				state = SBK;
				//prop.e = cc-1;
				if (lex.type() == Lexemator.LEND) state = SF;
				//if (po!=null) po.SOBK(cc);
			break;
			case SF:// конечное состояние
				//if (po!=null) po.SF(cc);
				prop.e = cc-1;
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
	/*public static Proper createProper(char[] text, Object[] args)
		throws Exception {
		return createProper(text,args);
	}*/
	
	public static Proper createProper(char[] text, Object[] args /*, ParserObserver po*/)
			throws Exception {
			GLOBAL.waitin();
			try{
				Lexemator lex = new Lexemator(text);
				Proper.clearDefault();
				Proper prop = parser(lex/*,po*/,0);
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


