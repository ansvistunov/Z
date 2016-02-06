
/*
 * File: Calc.java
 *
 * Created: Mon Apr 19 13:56:58 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

import java.util.*;
import loader.*;
//import xyz.chen.util.*;

/**
 * Класс реализующий калькулятор
 */

/*
  ~~~~ calc.language=zscript
 */

public class Calc {
   String code = null;
   __calc__ cl = null;
   String lang = null;
	
	public Calc(String s){
      code = s;
      lang = (GLOBAL.pr(GLOBAL.calcLanguage,"calc")).trim().toLowerCase();
	}

   public Calc(String s,String lang){
      code = s;
      this.lang = lang; 
   }
   
   public String[] getAliases() throws Exception{
      //System.out.println("cl = "+cl);
      if ( cl == null ){
			try{
            Class cls = Class.forName(
             GLOBAL.pr(GLOBAL.calcLanguage+"."+lang,"XXXX").trim());
            cl = (__calc__)cls.newInstance();
         }catch(Exception _){
            throw new Exception ("cant load language :\n\t"+
               _.getMessage());
         }
         cl.initExpr(code);
         //.parse();
      }
      //System.out.println("cl = "+cl);
      return cl.getAliases();
   }
   public void parse(Hashtable aliases) throws Exception{
      //write asw 
      if (aliases!=null) lang = ((String)aliases.get("CALC.LANGUAGE"));
      if (lang == null) lang = "calc";
      lang = lang.trim().toLowerCase();
      int l=code.indexOf("calc.language=");
      if(l>=0) {
        int l2 = code.indexOf("*/",l);
        int l1 = code.indexOf("\n",l);
        if(l2>=0) l1=l2;
        lang = code.substring(l+"calc.language=".length(), l1);
        lang = lang.trim().toLowerCase();
      }
      //end write asw
      if ( cl == null ){
         try{
            Class cls = Class.forName(
             GLOBAL.pr(GLOBAL.calcLanguage+"."+lang,"XXXX").trim());
            cl = (__calc__)cls.newInstance();
         }catch(Exception _){
            throw new Exception ("cant load language :\n\t"+
               _.getMessage());
         }
         cl.initExpr(code);
         //.parse();
			}
	}

	public String getExpression() {
      return code;
	}
	
/* public String[] getAliases() throws Exception{
		parse();
		Hashtable h = new Hashtable();
		expr.getAliases(h);
		String[] s = new String[h.size()];
		int i = 0;
		for ( Enumeration e = h.keys();e.hasMoreElements();++i){
			s[i] = (String)e.nextElement();
			if ( s[i].startsWith("G.") ) s[i] = s[i].substring(s[i].indexOf('.')+1);
		}
		return s;
      }*/

	public Object[] eval(Hashtable aliases) throws Exception{
      synchronized (document.Document.class) {
        parse(aliases);
        Object o = cl.eval(aliases);
        //System.out.println("!!!!!!!!!!!!!!!!!ret="+o);
        Object[] m = new Object[1];
        m[0]=o;
        return m;
		}
	}

   public static String macro(String str,Hashtable aliases)
      throws Exception{
      String lang = "calc";
      if (aliases!=null) lang = ((String)aliases.get("CALC.LANGUAGE"));
      if (lang == null) lang = "calc";
      lang = lang.trim().toLowerCase();
      return macro(str,aliases,lang);
      
   }
   public static String macro(String str,Hashtable aliases,String lang)
      throws Exception{
		try{
		StringTokenizer st = new StringTokenizer("_"+str,"~");
		boolean flag = false;
		String result ="";
		while(st.hasMoreTokens()){
			String s = st.nextToken();
			if ( flag ){
               Calc c = new Calc(s,lang);
				Object[] o = c.eval(aliases);
				for (int i=0;true;++i){
                                        //path 1 by Alex Svistunov
                                        //old code: result+=o[i].toString();
                                        if ((o[i] instanceof String) ||
                                            (o[i] instanceof Double) ||
                                            (o[i] instanceof java.util.Date)) result+=o[i].toString();//<-new Code
                                        //end of path 1
					if ( i<o.length-1 ) result+=",";
					else break;
				}
			}else{
				result+=s;
			}
			flag = !flag;
		}
		result = result.substring(1);
		return result;
		}catch(Exception e){
         e.printStackTrace();
			return "";
		}
	}
   public static int getCalcLanguage(String a,String b){
      return 0; // Calc
   }

   public String toString(){
      return "Calc <"+lang+"> expr \n"+code;
   }
}





