package views.edit.rml;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.ts.TypeScriptCompletionProvider;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import document.Document;

public class RMLLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing C.
	 */
	private RMLCompletionProvider provider;
	//private document.Document currentDocument;


	/**
	 * @return the currentDocument
	 *//*
	public document.Document getCurrentDocument() {
		return currentDocument;
	}


	*//**
	 * @param currentDocument the currentDocument to set
	 *//*
	public void setCurrentDocument(document.Document currentDocument) {
		this.currentDocument = currentDocument;
	}
*/
	public static String getAliasesAsVariableDecl(){
		//if (currentDocument == null) return "";
		
		Hashtable hash = Document.getcurd().aliases;
		Enumeration e = hash.keys();
		StringBuffer code = new StringBuffer();
		int l = 0;
		//if (true) return code.toString();
		while(e.hasMoreElements()) {
			String name = e.nextElement().toString();
			if (name.startsWith("#")) continue;
			if (name.contains(".")) continue;
			
			Object o = hash.get(name);
			//System.out.println("name="+name+" hash="+hash);
			//System.out.println("name="+name);
			code.append("var "+name+" = new "+o.getClass().getName()+"();\n");
			////////////
			
			////////////
		}
		
		
		return code.toString();
		
	}

	/**
	 * Constructor.
	 */
	public RMLLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
		setAutoActivationEnabled(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new RMLCellRenderer();
	}


	private RMLCompletionProvider getProvider() {
		if (provider==null) {
			provider = new RMLCompletionProvider();
		}
		return provider;
	}

	
	public RMLParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof RMLParser) {
			return (RMLParser)parser;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {

		System.out.println("RML support added");
		
		RMLCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		//ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		//ac.setAutoActivationEnabled(isAutoActivationEnabled());
		ac.setListCellRenderer(getDefaultCompletionCellRenderer());
 		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setShowDescWindow(getShowDescWindow());
		ac.setAutoActivationDelay(getAutoActivationDelay());
		installImpl(textArea, ac);

		//textArea.setToolTipSupplier(provider);
		
		RMLParser parser = new RMLParser(this);
		
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}


}
