
package calc.functions;

import calc.*;

public class ROUND extends BaseExternFunction {
    public Object eval() throws Exception {
        Object o = OP.doHardOP(expr);
        if (!(o instanceof Double)) throw new RTException("CastException","ROUND");
        return new Double(Math.round(((Double)o).doubleValue()));
    }
}
