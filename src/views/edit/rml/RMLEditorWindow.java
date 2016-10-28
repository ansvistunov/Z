/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package views.edit.rml;

import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.Stack;

import javax.swing.*;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;

import document.Document;
import document.Editor;
import loader.GLOBAL;
import loader.Loader;

/**
 * Stand-alone version of the demo.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class RMLEditorWindow extends JFrame implements Editor {

	Stack<Object[]> stackArgs;
	Stack<String> stackDocnames;
	RMLRootPane rootPanel;
	/** флаг, устанавливается в момент начала перезагрузки документа и сбрасывается в конце
	 * служит для того, чтобы в процессе перезагрузки документа не реагировать на события его закрытия и имзменения текста*/
	boolean processReload = false;
	
	/** флаг ,показывает, привела ли к ошибке последняя загрузка документа. Если привела, то документ не загружен и значит 
	 * выгружать его (ACT_CANCEL) НЕ нужно*/
	boolean lastError = false;
	

	public RMLEditorWindow(/* Document document */) {
		LanguageSupportFactory.get().addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_RML,
				"views.edit.rml.RMLLanguageSupport");
		FoldParserManager.get().addFoldParserMapping(SyntaxConstants.SYNTAX_STYLE_RML, new CurlyFoldParser());
		rootPanel = new RMLRootPane(this);
		setRootPane(rootPanel);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("RML Editor");
		stackArgs = new Stack<Object[]>();
		stackDocnames = new Stack<String>();
		Document.setEditor(this);
		pack();
		// LanguageSupportFactory.get().addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_RML,
		// "org.fife.rsta.ac.c.CLanguageSupport");
	}

	/**
	 * Called when we are made visible. Here we request that the
	 * {@link RSyntaxTextArea} is given focus.
	 *
	 * @param visible
	 *            Whether this frame should be visible.
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			((RMLRootPane) getRootPane()).focusTextArea();
		}
	}

	public static void createWindow(/* Document document */) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				new RMLEditorWindow(/* document */).setVisible(true);
			}
		});

	}

	@Override
	public void loadDocument(String docname, char[] text, Object[] args) {
		rootPanel.setLastFileName(null);
		if (!processReload) {
			rootPanel.setText(text);
			stackArgs.push(args);
			stackDocnames.push(docname);
		}

		System.out.println("Editor: loadDocument add to stack docname=" + docname);
	}

	@Override
	public void closeNotify() {
		if (!processReload) {

			try {
				stackArgs.pop();
			} catch (Exception e) {
				// ignore
			}

			try {
				stackDocnames.pop();
			} catch (Exception e) {
				// ignore
			}
			rootPanel.setText(null);
		}
	}

	public synchronized void reloadCurrentDocument() {
		try {
			processReload = true;
			String docname = null;
			Object[] args = null;
			char[] text = null;

			Hashtable aliases = null;
			if (Document.getcurd() != null) { //мы работаем внутри основной программы
				aliases = Document.getcurd().aliases;
				try {
					docname = stackDocnames.peek();
					args = stackArgs.peek();
					text = rootPanel.getText();
				} catch (Exception e) {
					docname = Document.getcurd().mypath + "/" + Document.getcurd().myname;
					args = null;
					text = rootPanel.getText();
				}

				if (!lastError) Document.getcurd().processAction(Document.ACT_CANCEL);
				// if (!stackArgs.empty() && !stackDocnames.empty()){
				// System.out.println("begin :"+memLoader);
				// Document.l
			} else { // мы просто загрузили файл через функцию Open
				docname = rootPanel.getLastFileName();
			}
			try {
				Document.callDocumentSomeWindowFromMemory(docname, text, args, aliases, null);
				lastError=false;
			} catch (Exception e) {
				rootPanel.setText(text);
				rootPanel.setLastFileName(docname);
				lastError = true;
			}

			
		} finally {
			processReload = false;
		}

	}

	public void setEditable(boolean b) {
		rootPanel.setEditable(b);

	}

	public void setText(char[] text) {
		rootPanel.setText(text);
	}

	public char[] getText() {
		return rootPanel.getText();
	}

}