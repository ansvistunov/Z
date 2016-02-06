
/*
 * File: CalcLan.java
 *
 * Created: Sun Nov 14 21:11:04 1999
 *
 * Copyright (c) by Alexey Chen
 */


package calc;

import java.util.*;
import loader.*;

/*
  ~~~~ calc.language.calc = xyz.chen.calc.CalcLan
 */


public class CalcLan implements __calc__{
        char[] text = null;
        OP expr = null;
        
        public CalcLan(){}
        public void initExpr(String s) throws Exception{
                text = s.toCharArray();
        }

        public OP parse() throws Exception{
                if (expr == null) {
                        try{
                                expr = Parser.parse(text);
                        }catch(Exception e){
                                e.printStackTrace();
                                String s = new String(text);
                                s = ((s.length()>80)?s.substring(0,80):s).replace('\n',' ');
                                System.out.println("!------\n"+s+"\n!------");
                                System.out.println("Calculator Paser Exception:\n\t"+e.getMessage());
                                throw new Exception("Parser exception");
                        }
                        return expr;
                }else return expr;
        }

        public String getExpression() {
                return new String(text);
        }
        
        public String[] getAliases() throws Exception{
                parse();
                Hashtable h = new Hashtable();
                //System.out.println("CalcLan getAliases before call to  expr.getaliases");
                expr.getAliases(h);
                //System.out.println("CalcLan getAliases h="+h);
                String[] s = new String[h.size()];
                int i = 0;
                for ( Enumeration e = h.keys();e.hasMoreElements();++i){
                        s[i] = (String)e.nextElement();
                        if ( s[i].startsWith("G.") ) s[i] = s[i].substring(s[i].indexOf('.')+1);
                        //System.out.println("Calclan getAliases s["+i+"]="+s[i]);
                }
                return s;
        }

        public Object eval(Hashtable aliases) throws Exception{
                parse();
                Object a = null;
                try{
                        try{
                                a = expr.eval(aliases);
                        }catch(ReturnException e){
                                throw new RTException("ReturnException",
                                                                          "return without X function");
                        }catch(BreakException e){
                                throw new RTException("BreakException",
                                                                          "break out from loop");
                        }catch(ContinueException e){
                                throw new RTException("CountinueException",
                                                                          "continue out from loop");
                        }catch(GotoException e){
                                throw new RTException("GotoException",
                                                                          "goto "+e.label+" out from X function");
                        }catch(CalcException e){
                                throw new RTException("CalcException",e.getMessage());
                        }catch(NullPointerException e){
                                e.printStackTrace();
                                throw new RTException("NullException",
                                                                          "may be not initializet any alement? ");
                        }
                }catch(RTException e){
                        String s = new String(text);
                        s = ((s.length()>80)?s.substring(0,80):s).replace('\n',' ');
                        System.out.println("!------\n"+s+"\n!------");
                        System.out.println(GLOBAL.PRINT_LIGHT+
                                                           "Calculator RunTime Exception:\n\t"+
                                                           GLOBAL.PRINT_NORMAL+
                                                           "TYPE: "+e.type+"\n\t"+
                                                           "RESON: "+e.getMessage()+
                                                           "\ntrap______________"+e.trap);
                        throw new Exception("~calc.Calc::eval error with eval expression");
                }
                return a;
        }
}
