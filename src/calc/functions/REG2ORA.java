
package calc.functions;

import calc.*;

public class REG2ORA extends BaseExternFunction {
    public Object eval() throws Exception {
        Object o = OP.doHardOP(expr);
        if (!(o instanceof String)) throw new RTException("CastException","REG2ORA");
        return reg2ora((String)o);
    }

    String reg2ora(String str) {
        if (str==null) return null;
        if (str.equals("")) return "";
        StringBuffer ret = new StringBuffer();
        boolean was_slash = false;
        for (int i=0;i<str.length();i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\\':if (was_slash) {
                               ret.append("\\");
                               break;
                           }else {
                               was_slash=true;
                               continue;
                           }
                case '*' :if (was_slash){
                              ret.append("*");
                          }else {
                            ret.append("%");
                          }
                          break;
                case '?' :if (was_slash) {
                              ret.append("?");
                          }else {
                              ret.append("_");
                          }
                          break;
                case '%' :ret.append("^%");
                          break;
                case '_' :ret.append("^_");
                          break;
                case '^' :ret.append("^^");
                          break;
                default  :ret.append(ch);
            }
            was_slash = false;
        }
        return ret.toString();
    }
}
