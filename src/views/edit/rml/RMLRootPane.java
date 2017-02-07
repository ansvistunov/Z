package views.edit.rml;

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


import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.PreProcessingScripts;

import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.rsta.ac.js.tree.JavaScriptTreeNode;
import org.fife.rsta.ac.js.util.RhinoUtil;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.modes.XMLTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import document.Document;
import loader.GLOBAL;
import rml.Lexemator;
import rml.Proper;
import rml.Proper.PropHash.HashRow;
import rml.RmlMem2File;
import views.edit.js.RMLJavaScriptEditor;
import views.edit.js.RMLJavaScriptLanguageSupport;




/**
* The root pane used by the demos.  This allows both the applet and the
* stand-alone application to share the same UI. 
*
* @author Robert Futrell
* @version 1.0
*/
public class RMLRootPane extends JRootPane implements HyperlinkListener,
							SyntaxConstants, Actions {

	private JScrollPane treeSP; //панель - скроллер для дерева
	private RMLOutlineTree tree; // дерево
	/**
	 * @return the tree
	 */
	public RMLOutlineTree getTree() {
		return tree;
	}

	private RTextScrollPane scrollPane; // панель-скроллер для редактора RML
	//private RTextScrollPane scrollPropPane; // панель-скроллер для редактора RML
	private RSyntaxTextArea textArea; // редактор RML
	private RMLJavaScriptEditor propArea; // редактор RML
	/**
	 * @return the propArea
	 */
	public RMLJavaScriptEditor getPropArea() {
		return propArea;
	}

	//private Document rmlDocument;
	private final JSplitPane sp; //сплиттер для дерева| правой части
	private final JSplitPane sp2; //сплиттер для редактора RML| редактора свойств
	private JPanel mainPanel ;//основная панель
	private JPanel rightPanel ;//Панель для правой части (редактора rml и редактора свойств)
	private ErrorStrip errorStrip; //для вывода ошибок
	RMLLanguageSupport languageSupport;
	AstRoot preProcessingRoot;
	private RMLEditorWindow editorWindow;
	private String lastFileName;
	

	/**
	 * @param lastFileName the lastFileName to set
	 */
	public void setLastFileName(String lastFileName) {
		this.lastFileName = lastFileName;
	}

	/**
	 * @return the lastFileName
	 */
	public String getLastFileName() {
		return lastFileName;
	}

	public void replaceRange(String str, int start, int end){
		
		textArea.replaceRange(str, start, end);
	};
	
	public void select (String str, int start, int end){
		textArea.select(start, end);
		
	}
	
	/**
	 * @return the rmlDocument
	 */
	/*public Document getRmlDocument() {
		return rmlDocument;
	}
*/

	public RMLRootPane(RMLEditorWindow editorWindow) {
		//this.rmlDocument = rmlDocument;
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SYNTAX_STYLE_RML);
		RMLLanguageSupport languageSupport = null;
		if (support instanceof RMLLanguageSupport){
			languageSupport = (RMLLanguageSupport)support;
			//languageSupport.setCurrentDocument(rmlDocument);
		}
		this.editorWindow = editorWindow;
		
		// TODO: This API will change!  It will be easier to do per-editor
		// changes to the build path.
		
		
		// Dummy tree keeps JViewport's "background" looking right initially
		JTree dummy = new JTree((TreeNode)null);
		treeSP = new JScrollPane(dummy);

		textArea = createRMLTextArea();
		setText(null);
		
		tree = new RMLOutlineTree();
		
		tree.listenTo(textArea);
		treeSP.setViewportView(tree);
		
		treeSP.revalidate();
		
		
		scrollPane = new RTextScrollPane(textArea, true);
		scrollPane.setIconRowHeaderEnabled(true);
		scrollPane.getGutter().setBookmarkingEnabled(true);
		
		
		propArea = new RMLJavaScriptEditor(this); // окно должно быть другое!!!!
		
		//Это листенер, который мы положим в дерево JavaScript
		RMLTreeModelListener rmlListener = new RMLTreeModelListener(this);
		//Этот листенер отслеживает изменения структуры дерева
		//он нужен для того ,чтобы добавлять в дерево JavaScript переменные сформированные из алиасов документа
		
		propArea.getTree().getModel().addTreeModelListener(rmlListener);
		
		//нам нужно заменить стандартный SelectionListener в дереве JavaScript, чтобы в нем корректно работали добавленные нами элементы
		TreeSelectionListener[] listeners = propArea.getTree().getTreeSelectionListeners();
		for (int i=0;i<listeners.length;i++){
			System.out.println("remove listener:"+listeners[i]);
			propArea.getTree().removeTreeSelectionListener(listeners[i]);
		}
		//удалили стандартные SelectionListener и поставили вместо него наш
		propArea.getTree().addTreeSelectionListener(rmlListener);
		
		//для дерева RML тоже нужно поставить нестандартный SelectionListener листенер 
		//этот листенер при выборе узла со скриптом будет открывать панель для его редактирования
		listeners = tree.getTreeSelectionListeners();
		for (int i=0;i<listeners.length;i++){
			System.out.println("remove listener:"+listeners[i]);
			tree.removeTreeSelectionListener(listeners[i]);
		}
		tree.addTreeSelectionListener(new RMLTreeSelectionListener(tree,propArea,this));
		
		
		
		//scrollPropPane = new RTextScrollPane(propArea, true);
		//scrollPropPane.setIconRowHeaderEnabled(true);
		//scrollPropPane.getGutter().setBookmarkingEnabled(true);

		rightPanel = new JPanel(new BorderLayout());
		sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scrollPane, propArea);
		
		rightPanel.add(sp2);
		
		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
										treeSP, rightPanel);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				sp.setDividerLocation(0.25);
			}
		});
		sp.setContinuousLayout(true);
//		setContentPane(sp);

		setJMenuBar(createMenuBar());

		errorStrip = new ErrorStrip(textArea);
//errorStrip.setBackground(java.awt.Color.blue);
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(sp);
		mainPanel.add(errorStrip, BorderLayout.LINE_END);
		setContentPane(mainPanel);
		
		
		processDocumentVars();
		
	}

	
	public void processDocumentVars(){
		PreProcessingScripts pps = propArea.getSupport().getPreProcessing();
		
		TypeDeclarationOptions tdo = new TypeDeclarationOptions("testScript", true, true);
		
		
		String script = RMLLanguageSupport.getAliasesAsVariableDecl();
		//System.out.println("Preprocess script parse...script="+script+" pps="+pps);
		preProcessingRoot = pps.parseScript(script, tdo);
		
		
	}
	
	
	

	/*private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}*/


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new OpenAction(this)));
		menu.add(new JMenuItem(new SaveAction(this)));
		menu.add(new JMenuItem(new SaveAsAction(this)));
		menu.add(new JMenuItem(new Action1(this)));
		//menu.add(new JMenuItem(new Action2(this)));
		menu.add(new JMenuItem(new Action3(this)));
		menu.addSeparator();
		menu.add(new JMenuItem(new ExitAction()));
		
		mb.add(menu);

	/*	menu = new JMenu("Language");
		ButtonGroup bg = new ButtonGroup();
		addItem(new StyleAction(this, "C",          "CExample.txt",      SYNTAX_STYLE_C), bg, menu);
		addItem(new StyleAction(this, "CSS",        "CssExample.txt",    SYNTAX_STYLE_CSS), bg, menu);
		addItem(new StyleAction(this, "Groovy",     "GroovyExample.txt", SYNTAX_STYLE_GROOVY), bg, menu);
		addItem(new StyleAction(this, "Java",       "JavaExample.txt",   SYNTAX_STYLE_JAVA), bg, menu);
		addItem(new StyleAction(this, "JavaScript", "JSExample.txt",     SYNTAX_STYLE_JAVASCRIPT), bg, menu);
		addItem(new StyleAction(this, "JSP",        "JspExample.txt",    SYNTAX_STYLE_JSP), bg, menu);
		addItem(new StyleAction(this, "Less",       "LessExample.txt",   SYNTAX_STYLE_LESS), bg, menu);
		addItem(new StyleAction(this, "Perl",       "PerlExample.txt",   SYNTAX_STYLE_PERL), bg, menu);
		addItem(new StyleAction(this, "HTML",       "HtmlExample.txt",   SYNTAX_STYLE_HTML), bg, menu);
		addItem(new StyleAction(this, "PHP",        "PhpExample.txt",    SYNTAX_STYLE_PHP), bg, menu);
		addItem(new StyleAction(this, "sh",         "ShellExample.txt",  SYNTAX_STYLE_UNIX_SHELL), bg, menu);
		addItem(new StyleAction(this, "TypeScript", "TypeScriptExample.txt",  SYNTAX_STYLE_TYPESCRIPT), bg, menu);
		addItem(new StyleAction(this, "XML",        "XmlExample.txt",    SYNTAX_STYLE_XML), bg, menu);
		addItem(new StyleAction(this, "RML",        "RMLExample.txt",    SYNTAX_STYLE_RML), bg, menu);
		menu.getItem(0).setSelected(true);
		mb.add(menu);

		menu = new JMenu("LookAndFeel");
		bg = new ButtonGroup();
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for (int i=0; i<infos.length; i++) {
			addItem(new LookAndFeelAction(this, infos[i]), bg, menu);
		}
		mb.add(menu);

		menu = new JMenu("View");
		menu.add(new JCheckBoxMenuItem(new ToggleLayeredHighlightsAction(this)));
		mb.add(menu);
		
		menu = new JMenu("Help");
		menu.add(new JMenuItem(new AboutAction(this)));
		mb.add(menu);
*/
		return mb;
		

	}


	/**
	 * Creates the text area for this application.
	 *
	 * @return The text area.
	 */
	private RSyntaxTextArea createRMLTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(25, 80);
		LanguageSupportFactory.get().register(textArea);
		textArea.setCaretPosition(0);
		textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(3);
		textArea.setSyntaxEditingStyle(SYNTAX_STYLE_RML);
		//textArea.setBackground(new java.awt.Color(224, 255, 224));
		//	textArea.setUseSelectedTextColor(true);
		//textArea.setLineWrap(true);
		ToolTipManager.sharedInstance().registerComponent(textArea);
		return textArea;
	}
	
	
	


	/**
	 * Focuses the text area.
	 */
	void focusTextArea() {
		textArea.requestFocusInWindow();
	}


	RSyntaxTextArea getTextArea() {
		return textArea;
	}


	/**
	 * Called when a hyperlink is clicked in the text area.
	 *
	 * @param e The event.
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			if (url==null) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
			else {
				JOptionPane.showMessageDialog(this,
									"URL clicked:\n" + url.toString());
			}
		}
	}


	/**
	 * Opens a file in the editor (as opposed to one of the pre-defined
	 * code examples).
	 *
	 * @param file The file to open.
	 */
	public void openFile(File file) {
		try {
			//rmlDocument = null;
			lastFileName = file.getAbsolutePath();
			BufferedReader r = new BufferedReader(new FileReader(file));
			textArea.read(r, null);
			textArea.setCaretPosition(0);
			r.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			UIManager.getLookAndFeel().provideErrorFeedback(this);
			return;
		}
	}


	/**
	 * Displays a tree view of the current source code, if available for the
	 * current programming language.
	 */
	/*private void refreshSourceTree() {

		if (tree!=null) {
			tree.uninstall();
		}

		String language = textArea.getSyntaxEditingStyle();
		
			tree = new RMLOutlineTree();
		

		if (tree!=null) {
			tree.listenTo(textArea);
			treeSP.setViewportView(tree);
		}
		else {
			JTree dummy = new JTree((TreeNode)null);
			treeSP.setViewportView(dummy);
		}
		treeSP.revalidate();

	}*/

	public void showPropEditPanel(){
		propArea.setVisible(true);
		sp2.setDividerLocation(0.5);
	}
	
	public void hidePropEditPanel(){
		propArea.setVisible(false);
		sp2.setDividerLocation(1);
	}
	
	
	public char[] getText(){
		return textArea.getText().toCharArray();
		
	}
	
	/**
	 * Sets the content in the text area to that in the specified resource.
	 *
	 * @param resource The resource to load.
	 * @param style The syntax style to use when highlighting the text.
	 */
	void setText(char[] text) {
		
		//System.out.println("resource="+resource+" style="+style);
		
		//textArea.setSyntaxEditingStyle(style);

		//ClassLoader cl = getClass().getClassLoader();
		
		try{
			hidePropEditPanel();
		}catch(Exception e) {
			//ignore
		}
		
		try {
			
			//System.out.println(resource);
			
			
			//System.out.println(resource+" "+ cl.getSystemResourceAsStream("examples/" + resource));
			
			//BufferedReader r = new BufferedReader();
			
			if (text == null){
			
				String name = Document.getcurd().mypath+"/"+Document.getcurd().myname;
				System.out.println("document name="+name);
			
				text = GLOBAL.loader.loadByName_chars(name, true);
			
			}
			//String s = new String(rmltext);
			CharArrayReader textReader = new CharArrayReader(text);
			
			
			//textArea.setText(s);
			
			textArea.read(textReader, null);
			
			textReader.close();

			
			/*try{
				RSyntaxDocument document = (RSyntaxDocument)textArea.getDocument();
				Segment text = new Segment();
				document.getText(0, document.getLength(), text);
				
				
				System.out.println("begin.............");
				int line = 0;
				Token t;
				while(true){
					try{
						t = document.getTokenListForLine(line);
					}catch(Exception e){
						break;
					}
					if (t == null) break;
					line++;
					
					
					while(t!=null){
						System.out.println(t);
						t = t.getNextToken();
						
					}
				}
				
								
			}catch(Exception e){
				e.printStackTrace();
				
			}
					*/
			
			
			
			
			
			textArea.setCaretPosition(0);
			textArea.discardAllEdits();

			//refreshSourceTree();
			
			//processDocumentVars();

		} catch (RuntimeException re) {
			throw re; // FindBugs
		} catch (Exception e) {
			textArea.setText("Type here to see syntax highlighting");
		}

	}

	static class OpenAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private RMLRootPane demo;
		private JFileChooser chooser;

		public OpenAction(RMLRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Open...");
			putValue(MNEMONIC_KEY, new Integer('O'));
			int mods = demo.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_O, mods);
			putValue(ACCELERATOR_KEY, ks);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chooser == null) {
				chooser = new JFileChooser();
				StringBuffer beginpath = new StringBuffer(GLOBAL.loader.getRoot().getPath());
				beginpath.delete(0, 1);
				String bp = beginpath.toString().replace("/", "\\")+"\\";
				chooser.setCurrentDirectory(new File(bp));
				chooser.setFileFilter(
						new ExtensionFileFilter("RML Source Files", "rml"));
			}
			int rc = chooser.showOpenDialog(demo);
			if (rc == JFileChooser.APPROVE_OPTION) {
				demo.openFile(chooser.getSelectedFile());
			}
		}

	}
	
	static class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private RMLRootPane demo;
		

		public SaveAction(RMLRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Save");
			putValue(MNEMONIC_KEY, new Integer('S'));
			int mods = demo.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_S, mods);
			putValue(ACCELERATOR_KEY, ks);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//if (demo.getRmlDocument()!=null){
			String docname = "";
			if (demo.lastFileName!=null) { //загружен из файла, или ошибка при загрузке
				docname = demo.lastFileName;
			}else				
			try{
				docname = document.Document.getcurd().mypath+"/"+document.Document.getcurd().myname;
			}catch(Exception ex){
				docname = demo.lastFileName;
			}
				
				String text = demo.textArea.getText();
				try{
					RmlMem2File.saveRMLSameFile(docname, text.toCharArray());
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			//}
		}

	}
	
	static class SaveAsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private RMLRootPane demo;
		

		public SaveAsAction(RMLRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Save As");
			putValue(MNEMONIC_KEY, new Integer('V'));
			int mods = demo.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_V, mods);
			putValue(ACCELERATOR_KEY, ks);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String n = "";
			String p = "";
			
			//if (demo.getRmlDocument()!=null){
				n = document.Document.getcurd().myname;
				p = document.Document.getcurd().mypath;
			//}
				String text = demo.textArea.getText();
				try{
					RmlMem2File.saveRMLasNewFile(p, n, text.toCharArray());
					
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			
		}

	}
	
	
	
	
	static class ExitAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ExitAction() {
			putValue(NAME, "Exit");
			putValue(MNEMONIC_KEY, new Integer('x'));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

	}
	
	static class Action1 extends AbstractAction {

		private static final long serialVersionUID = 1L;
		RMLRootPane panel;
		public Action1(RMLRootPane panel) {
			this.panel = panel;
			putValue(NAME, "Синхронизировать документ");
			putValue(MNEMONIC_KEY, new Integer('R'));
			int mods = panel.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_R, mods);
			putValue(ACCELERATOR_KEY, ks);
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//panel.rightPanel.remove(panel.sp2);
			//panel.rightPanel.add(panel.scrollPane);
			panel.editorWindow.reloadCurrentDocument();
			
		}

	}
	
	static class Action2 extends AbstractAction {

		private static final long serialVersionUID = 1L;
		RMLRootPane panel;
		public Action2(RMLRootPane panel) {
			this.panel = panel;
			putValue(NAME, "Показать редактор свойств");
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//panel.rightPanel.remove(panel.scrollPane);
			//panel.rightPanel.add(panel.sp2);
			panel.propArea.setVisible(true);
			panel.sp2.setDividerLocation(0.5);
			
		}

	}
	
	static class Action3 extends AbstractAction {

		private static final long serialVersionUID = 1L;
		RMLRootPane panel;
		public Action3(RMLRootPane panel) {
			this.panel = panel;
			putValue(NAME, "Test Lexemator");
			
		}

		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//panel.rightPanel.remove(panel.scrollPane);
			//panel.rightPanel.add(panel.sp2);
			
			
			RSyntaxDocument document = (RSyntaxDocument)panel.getTextArea().getDocument();
			Segment text = new Segment();
			try {
				document.getText(0, document.getLength(), text);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//System.out.println("document="+document);
			//System.out.println("text="+text.toString());
			System.out.println("CAT HERE begin............._________________________________________________________________________________________________________________");
			int line = 0;
			Token t;
			while(true){
				try{
					t = document.getTokenListForLine(line);
				}catch(Exception ex){
					break;
				}
				if (t == null) break;
				line++;
				
				
				while(t!=null){
					System.out.println(t);
					t = t.getNextToken();
					
				}
				//System.out.println("NEW LINE");
			}
			System.out.println("CAT HERE end............._________________________________________________________________________________________________________________");
			
			/*Lexemator lex = new Lexemator(text);
			try{
				while(lex.next(true) <= text.length){
					if (lex.type() == Lexemator.LEND) break;
					System.out.printf("type=%d, value=|%s| \t", lex.type(), lex.as_string(false));
				
				}
			}catch(Exception ex) {ex.printStackTrace();};*/
			
			
			}
			
			
		}

	public void setEditable(boolean b) {
		textArea.setEditable(b);
		propArea.setEditable(b);
		
	}

	}




class RMLTreeSelectionListener implements TreeSelectionListener {

	String[] scriptProps = { "ACTION", "EXP", "QUERY","POSTLOADSCRIPT","PRELOADSCRIPT","SELACTION" };
	
	RMLOutlineTree tree;
	RMLJavaScriptEditor rightPanel;
	RMLRootPane mainPanel;
	
	public RMLTreeSelectionListener(RMLOutlineTree tree,RMLJavaScriptEditor rightPanel, RMLRootPane mainPanel){
		this.tree = tree;
		this.rightPanel = rightPanel;
		this.mainPanel = mainPanel;
	}	

	/**
	 * Selects the corresponding element in the text editor when a user clicks
	 * on a node in this tree.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		if (tree.getGotoSelectedElementOnClick()) {
			// gotoSelectedElement();
			TreePath newPath = event.getNewLeadSelectionPath();
			if (newPath != null) {
				RMLTreeNode node = tree.gotoElementAtPath(newPath);
				if (node != null) {
					if (containsScript(node)) {
						String script = getScript(node);
						
						Proper p = node.getProper();
						HashRow hr = p.hash.getHashRow(node.getUserObject().toString());
						int start = hr.s;
						int end = hr.e;
						
						//пытаемся учесть пробелы перед значением свойства
						
						int lenRml = end-start;
						int lenVal = script.length() + 2;//+2 кавычки
						
						if (lenRml>lenVal) start = start+(lenRml - lenVal);
						
						
						int s = script.indexOf('~');
						if (s>=0) { //macro
							int e = script.indexOf('~',s+1);
							if (e>=0) {
								System.out.println("s="+script);
								System.out.println("s="+s+" e="+e);
								rightPanel.setText(script.substring(s+1, e), RMLJavaScriptEditor.MACRO_SCRIPT, start+s+1, (e-s+1));
							}
							
						}else rightPanel.setText(script, RMLJavaScriptEditor.JAVA_SCRIPT,start,(end-start));
						mainPanel.showPropEditPanel();
					}else mainPanel.hidePropEditPanel();

				}

			}
		}
	}

	private String getScript(RMLTreeNode node) {
		// TODO Auto-generated method stub
		Proper p = node.getProper();
		String value = p.get(node.getUserObject().toString()).toString();
		return value;

	}

	private boolean containsScript(RMLTreeNode node) {
		String s = node.getUserObject().toString();
		for (int i=0; i<scriptProps.length;i++){
			if (scriptProps[i].equals(s)) return true;
		}
		return false;
	}

}

	
	class RMLTreeModelListener implements TreeModelListener,  NodeVisitor, TreeSelectionListener{

		RMLRootPane panel;
		JavaScriptTreeNode treeRoot;
		AstRoot astRoot;
		JavaScriptOutlineTree tree;
		
		
		int i;
		void fillAllNodeOffset(JavaScriptTreeNode foo){
			if(foo!=null){
				javax.swing.text.Document document =  panel.getPropArea().getTextAreaEditor().getDocument();
				try {
					foo.setOffset(document.createPosition(i));
					
					try{
						i+=foo.getLength();
					}catch(Exception e) {
						i++;
					}
					System.out.println("set offset="+i+" for "+foo.getText(true));
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Enumeration<TreeNode> e = foo.children();
				while(e.hasMoreElements()){
					TreeNode node = e.nextElement();
					if (node!=null) fillAllNodeOffset ( (JavaScriptTreeNode)node);
				}
			}
			
		}
		
		public RMLTreeModelListener(RMLRootPane panel) {
			this.panel = panel;
			astRoot = panel.preProcessingRoot;
			RMLJavaScriptEditor editor = panel.getPropArea();
			if (editor != null) tree = editor.getTree();
		}
		
		
		
		void doIt(TreeModelEvent e) {
			//System.out.println("tree changed "+e);
			//if (true) return;
		// 
					TreeModel model = tree.getModel();
					if (model != null) {
						treeRoot = (JavaScriptTreeNode) model.getRoot();
						//root.
						
						//System.out.println(panel.preProcessingRoot.debugPrint());
						
						if (panel.preProcessingRoot!=null) 
							panel.preProcessingRoot.visit(this);
						
						
						i = 0;
						//fillAllNodeOffset(root);
						
					}
				
			

		}
		
		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			
			// TODO Auto-generated method stub
			System.out.println("tree treeNodesChanged "+e);
			doIt(e);
			
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			// TODO Auto-generated method stub
			System.out.println("tree treeNodesInserted "+e);
			doIt(e);
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			// TODO Auto-generated method stub
			System.out.println("tree treeNodesRemoved "+e);
			doIt(e);
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			// TODO Auto-generated method stub
			System.out.println("tree treeStructureChanged "+e);
			doIt(e);
		}

		@Override
		public boolean visit(AstNode node) {
			//System.out.println("visit +"+node);
			if (node==null) {
				return false;
			}

			int nodeType = node.getType();
			switch (nodeType) {

				case org.mozilla.javascript.Token.SCRIPT: // AstRoot
					//curScopeTreeNode = root;
					return true;

				case org.mozilla.javascript.Token.FUNCTION:
					FunctionNode fn = (FunctionNode)node;
					//System.out.println("function:"+fn);
					
					return false;

				case org.mozilla.javascript.Token.VAR:
					VariableDeclaration varDec = (VariableDeclaration)node;
					//System.out.println("variable:"+varDec);
					return visitVariableDeclaration(varDec);

				case org.mozilla.javascript.Token.BLOCK:
					return true;

				case org.mozilla.javascript.Token.EXPR_RESULT:
					ExpressionStatement exprStmt = (ExpressionStatement)node;
					//System.out.println("EXPR_RESULT:"+exprStmt);
					return false;

			}

			return false; // Unhandled node type
		}
		private final JavaScriptTreeNode createTreeNode(AstNode node) {
			JavaScriptTreeNode tn = new JavaScriptTreeNode(node);
//			try {
//				int offs = node.getAbsolutePosition();
//				tn.setOffset(textArea.getDocument().createPosition(offs));
//			} catch (BadLocationException ble) { // Never happens
//				ble.printStackTrace();
//			}
			return tn;
		}
		private boolean visitVariableDeclaration(VariableDeclaration varDec) {

			List<VariableInitializer> vars = varDec.getVariables();
			for (VariableInitializer var : vars) {

				Name varNameNode = null;
				String varName = null;
				AstNode target = var.getTarget();
				switch (target.getType()) {
					case org.mozilla.javascript.Token.NAME:
						varNameNode = (Name)target;
						//System.out.println("... Variable: " + name.getIdentifier());
						varName = varNameNode.getIdentifier();
						break;
					default:
						System.out.println("... Unknown var target type: " + target.getClass());
						varName = "?";
						break;
				}

				boolean isFunction = var.getInitializer() instanceof FunctionNode;
				JavaScriptTreeNode tn = createTreeNode(varNameNode);
				if (isFunction) {

					FunctionNode func = (FunctionNode)var.getInitializer();
					tn.setText(varName + RhinoUtil.getFunctionArgsString(func));
					tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
					//tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
					treeRoot.add(tn);

					treeRoot = tn;
					func.getBody().visit(this);
					treeRoot = (JavaScriptTreeNode)treeRoot.getParent();

				}
				else {
					tn.setText(varName);
					tn.setIcon(IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
					//tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_VARIABLE);
					treeRoot.add(tn);
				}

			}

			return false;

		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (tree.getGotoSelectedElementOnClick()) {
				//gotoSelectedElement();
				try{
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath!=null) {
					tree.gotoElementAtPath(newPath);
				}
				}catch(Exception ex){
					//ignore this
				}
			}
			
		}
		
		
	}


