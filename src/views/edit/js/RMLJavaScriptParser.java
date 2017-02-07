package views.edit.js;
import java.io.CharArrayReader;
import java.util.Iterator;


import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import views.edit.rml.RMLLanguageSupport;

import org.fife.ui.rsyntaxtextarea.Token;

public class RMLJavaScriptParser extends JavaScriptParser{

	RMLLanguageSupport support;
	
	
	
	public RMLJavaScriptParser(JavaScriptLanguageSupport langSupport, RSyntaxTextArea textArea) {
		super(langSupport, textArea);
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		support = (RMLLanguageSupport)lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_RML);
		
	}
	
	
	
	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {
		System.out.println("RMLJavaScriptParser called");
		
		//if (true) 
			return super.parse(doc, style);
		
		
		
		/*Element root = doc.getDefaultRootElement();
		
		RSyntaxDocument temp = null;
		
		
		try {
			
			String  s = doc.getText(0, doc.getLength());
			
			
			String code  = "";
			if (support!=null) 
				code = support.getAliasesAsVariableDecl();
			//code = "var file = new java.io.File(); var BN_10 = new views.mButton();  ";
			//code="";
			System.out.println("code="+code);
			System.out.println("before insert text="+s);
			//s.insert(0, "var file = new java.io.File();");
			
			
			temp = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			
			temp.insertString(0, code , null);
			System.out.println("insert code");
			temp.insertString(code.length(), s, null);
			System.out.println("insert rest of document ");
			System.out.println("temp document="+temp.getText(0, temp.getLength()));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		Iterator<Token> it = temp.iterator();
		for (;it.hasNext();) {
			Token t = it.next();
			System.out.println(t);
			
		};
		ParseResult results =  super.parse(temp, style);
		
		System.out.println(results);
		return results;*/
		
		
	}

}
