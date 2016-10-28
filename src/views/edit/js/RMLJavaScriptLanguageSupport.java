package views.edit.js;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.js.JavaScriptCompletionProvider;
import org.fife.rsta.ac.js.JavaScriptDocUrlhandler;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptLinkGenerator;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.rsta.ac.js.JsErrorParser;
import org.fife.rsta.ac.js.PreProcessingScripts;
import org.fife.rsta.ac.js.SourceCompletionProvider;

import org.fife.rsta.ac.js.JavaScriptLanguageSupport.JavaScriptAutoCompletion;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport.Listener;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.type.ecma.v5.TypeDeclarationsECMAv5;
import org.fife.rsta.ac.js.engine.RhinoJavaScriptEngine;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.modes.JavaScriptTokenMaker;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;


public class RMLJavaScriptLanguageSupport extends JavaScriptLanguageSupport
{
    private static final String ENGINE = RhinoJavaScriptEngine.RHINO_ENGINE;
    PreProcessingScripts preProcessing;
    
//	

/**
	 * @return the preProcessing
	 */
	public PreProcessingScripts getPreProcessing() {
		return preProcessing;
	}

public RMLJavaScriptLanguageSupport()
{
    JavaScriptTokenMaker.setJavaScriptVersion("1.7");
    setECMAVersion(TypeDeclarationsECMAv5.class.getName(), getJarManager());
}

@Override
protected JavaScriptCompletionProvider createJavaScriptCompletionProvider()
{
	MySourceCompletionProvider mscp = new MySourceCompletionProvider();
	JavaScriptCompletionProvider jscp =  new JavaScriptCompletionProvider(mscp, getJarManager(), this);
    preProcessing = new PreProcessingScripts(mscp);
    //preProcessing.parseScript("var f = new java.io.File(); var s = java.lang.String();", null);
    mscp.setPreProcessingScripts(preProcessing);
    
    //preProcessing.parseScript("var f = new java.io.File(); var s = java.lang.String();", null);
    return jscp;
    
}
public void install(RSyntaxTextArea textArea)
{
    //remove javascript support and replace with Rhino support
    LanguageSupport support = (LanguageSupport)textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport");
    if (support!=null) {
        support.uninstall(textArea);
    }
 // We use a custom auto-completion.
 		// AutoCompletion ac = createAutoCompletion(p);
 		AutoCompletion ac = new JavaScriptAutoCompletion(provider, textArea);
 		ac.setListCellRenderer(getDefaultCompletionCellRenderer());
 		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
 		ac.setAutoActivationEnabled(isAutoActivationEnabled());
 		ac.setAutoActivationDelay(getAutoActivationDelay());
 		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
 		ac.setExternalURLHandler(new JavaScriptDocUrlhandler(this));
 		ac.setShowDescWindow(getShowDescWindow());
 		ac.install(textArea);
 		installImpl(textArea, ac);

 		Listener listener = new Listener(textArea);
 		textArea.putClientProperty(PROPERTY_LISTENER, listener);

 		parser = new RMLJavaScriptParser(this, textArea);
 		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
 		textArea.addParser(parser);
 		//textArea.setToolTipSupplier(provider);

 		Info info = new Info(provider, parser);
 		parserToInfoMap.put(parser, info);

 		installKeyboardShortcuts(textArea);
 		
 		// Set XML on JavascriptTokenMaker
 		JavaScriptTokenMaker.setE4xSupported(isXmlAvailable());

 		textArea.setLinkGenerator(new JavaScriptLinkGenerator(this));
    
 		
    
    
    
    
}
private class MySourceCompletionProvider extends SourceCompletionProvider
{
   public MySourceCompletionProvider()
    {
        super(ENGINE, false);
        
    }
   
   
   
   
}



}
