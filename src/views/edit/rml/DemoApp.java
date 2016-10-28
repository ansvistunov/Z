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
import javax.swing.*;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;


/**
 * Stand-alone version of the demo.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DemoApp extends JFrame {


	public DemoApp() {
		setRootPane(new DemoRootPane());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("RSTA Language Support Demo Application");
		pack();
		LanguageSupportFactory.get().addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_RML, "views.edit.rml.RMLLanguageSupport");
		FoldParserManager.get().addFoldParserMapping(SyntaxConstants.SYNTAX_STYLE_RML, new CurlyFoldParser());
		//LanguageSupportFactory.get().addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_RML, "org.fife.rsta.ac.c.CLanguageSupport");
	}


	/**
	 * Called when we are made visible.  Here we request that the
	 * {@link RSyntaxTextArea} is given focus.
	 *
	 * @param visible Whether this frame should be visible.
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			((DemoRootPane)getRootPane()).focusTextArea();
		}
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.
											getSystemLookAndFeelClassName());
//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				new DemoApp().setVisible(true);
			}
		});
	}

	public static void createWindow(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.
											getSystemLookAndFeelClassName());
//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				new DemoApp().setVisible(true);
			}
		});
		
	} 
	

}