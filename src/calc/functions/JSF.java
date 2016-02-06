package calc.functions;

import java.util.Hashtable;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import calc.OP;
import calc.RTException;
import loader.GLOBAL;
import loader.Loader;

public class JSF extends BaseExternFunction {
	static ScriptEngine jsEngine;
	Loader loader;
	
	private Bindings h2b(Hashtable h){
		SimpleBindings b = new SimpleBindings((Hashtable<String, Object>)h);
		return b;
		
	}

	public Object eval() throws Exception {
		if (jsEngine==null) throw new Exception ("No script engine found for JavaScript");
        Object o = OP.doHardOP(expr);
        if (!(o instanceof String)) throw new RTException("CastException","JSF");
        //System.out.println("o="+o);
        
                
        char[] text = GLOBAL.loader.loadByName_chars(o.toString(),true);
        
        
        String debug=new String (text);
        //System.out.println("loaded="+debug);
        
        //Bindings b = expr.getAliases();
        Object r;
        
        try{
        	r =  jsEngine.eval(new String(text),h2b(expr.getAliases()));
        }catch(Exception e){
        	System.out.println("JavaScripFile :"+o+"\n Error is:"+e.getMessage());
        	throw e;
        }
        
        System.out.println("r="+r);
        
        return r;
        
        
    }
	
	static{
		
		ScriptEngineManager scriptEngineMgr = new ScriptEngineManager();

		jsEngine = scriptEngineMgr.getEngineByName("JavaScript");
		if (jsEngine == null) {
			System.out.println("No script engine found for JavaScript");
			
		}
		
		
		
		
	}
	
	
}
