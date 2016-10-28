package views.edit.js;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.io.CharArrayReader;
import java.io.IOException;

import javax.swing.*;

import org.fife.ui.rtextarea.*;

import views.edit.rml.RMLRootPane;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A simple example showing how to use RSyntaxTextArea to add Java syntax
 * highlighting to a Swing application.
 */
public class RMLJavaScriptEditor extends JPanel implements ActionListener{
							//extends JFrame {

	/*public class RMLHierarchyListener implements HierarchyListener {

		@Override
		public void hierarchyChanged(HierarchyEvent event) {
			

		}

	}*/

	public static final int JAVA_SCRIPT = 0;
	public static final int MACRO_SCRIPT = 1;
	public static final int EXTERNAL_SCRIPT = 2;
	
	public static final int ICON_SIZE = 25;
	
	public static final String SAVE_BUTTON="SAVE";
	public static final String SAVE_EXIT_BUTTON="SAVEEXIT";
	public static final String CANCEL_BUTTON="CANCEL";
	
	private int scriptType;
	private int startPosition;
	private int scriptLength;


private static final long serialVersionUID = 1L;
   RSyntaxTextArea textAreaEditor;
   RMLJavaScriptLanguageSupport support;
   JToolBar toolbar;
   RMLRootPane rmlPanel;
   
   JButton bSave;
   JButton bSaveExit;
   JButton bCancel;
   /**
 * @return the support
 */
public RMLJavaScriptLanguageSupport getSupport() {
	return support;
}


/**
 * @return the textAreaEditor
 */
public RSyntaxTextArea getTextAreaEditor() {
	return textAreaEditor;
}




JavaScriptOutlineTree tree;
   
   

   /**
 * @return the tree
 */
public JavaScriptOutlineTree getTree() {
	return tree;
}

protected static ImageIcon createImageIcon(String path) {
    /*java.net.URL imgURL = RSyntaxTextArea.class.getResource(path);
    
    
    if (imgURL != null) {
        return new ImageIcon(imgURL);
    } else {
        System.err.println("Couldn't find file: " + path);
        return null;
    }*/
	ImageIcon icon = new ImageIcon(path,"");
	Image img = icon.getImage() ;  
	Image newimg = img.getScaledInstance( ICON_SIZE, ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
	return new ImageIcon( newimg );
	
}


public RMLJavaScriptEditor(RMLRootPane rmlPanel) {

	
	  	   
      //JPanel cp = new JPanel(new BorderLayout());
	  super(new BorderLayout());
      createTextAreaEditor();
      this.rmlPanel = rmlPanel;
      //configureLanguageSupport(textAreaEditor); 
      
      
      //JPanel leftPanel = new JPanel(new BorderLayout());
      
      
      toolbar = new JToolBar();
      toolbar.setRollover(true);
      
      
      
      
      bSave = new JButton("", createImageIcon("images/save.gif"));
      bSave.setVerticalTextPosition(AbstractButton.CENTER);
      bSave.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
      //bSave.setMnemonic(KeyEvent.VK_D);
      bSave.setActionCommand(SAVE_BUTTON);
      bSave.addActionListener(this);
      
      bSaveExit = new JButton("", createImageIcon("images/saveexit.gif"));
      bSaveExit.setVerticalTextPosition(AbstractButton.CENTER);
      bSaveExit.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
      //bSave.setMnemonic(KeyEvent.VK_D);
      bSaveExit.setActionCommand(SAVE_EXIT_BUTTON);
      bSaveExit.addActionListener(this);
      
      bCancel = new JButton("", createImageIcon("images/cancel.gif"));
      bCancel.setVerticalTextPosition(AbstractButton.CENTER);
      bCancel.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
      //bSave.setMnemonic(KeyEvent.VK_D);
      bCancel.setActionCommand(CANCEL_BUTTON);
      bCancel.addActionListener(this);
      
      
      toolbar.add(bSave);
      toolbar.add(bSaveExit);
      toolbar.add(bCancel);
      
      add(toolbar, BorderLayout.NORTH);
      add(tree, BorderLayout.WEST);
      
      RTextScrollPane sp = new RTextScrollPane(textAreaEditor);
      add(sp, BorderLayout.CENTER);
      //add(leftPanel,BorderLayout.WEST);
      

      /*setContentPane(cp);
      setTitle("Text Editor Demo");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      pack();
      setLocationRelativeTo(null);*/
      
     
      //bSaveExit.setEnabled(false);
      //bSave.setEnabled(false);
      
      
      
      //tree.addHierarchyListener(new RMLHierarchyListener());
      try{
    	  rmlPanel.hidePropEditPanel();
      }catch(Exception e){
    	  //ignore
      }
   }
   
   
   public void setText(String s, int type, int startPosition, int scriptLength) {
	   try {
		this.scriptType = type;
		this.startPosition = startPosition;
		this.scriptLength = scriptLength;
		
		textAreaEditor.read(new CharArrayReader(s.toCharArray()), null);
		textAreaEditor.setCaretPosition(0);
		textAreaEditor.discardAllEdits();
		
		//textAreaEditor.repla
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
   }	
   private RSyntaxTextArea createTextAreaEditor()  {
	    textAreaEditor = new RSyntaxTextArea(20,60);
	    textAreaEditor.setCaretPosition(0);
	    textAreaEditor.requestFocusInWindow();
	    textAreaEditor.setMarkOccurrences(true);
	    textAreaEditor.setTabsEmulated(true);
	    textAreaEditor.setTabSize(4);
	    ToolTipManager.sharedInstance().registerComponent(textAreaEditor);

	    textAreaEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
	    
	    //textAreaEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
	    textAreaEditor.setCodeFoldingEnabled(true);
	    textAreaEditor.setAntiAliasingEnabled(true);

	    try {
			configureLanguageSupport(textAreaEditor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    tree = new JavaScriptOutlineTree();
	    tree.listenTo(textAreaEditor);
	    
	    return textAreaEditor;
	}

	private void configureLanguageSupport(RSyntaxTextArea textArea) throws IOException
	{

	    support = new RMLJavaScriptLanguageSupport();
	    JarManager jarManager = support.getJarManager();
	    jarManager.addCurrentJreClassFileSource();
	    //add additional libraries and classes
	    jarManager.addClassFileSource(new JarLibraryInfo("zeta.jar"));
	    support.install(textArea);
	    
	}
   
   
   

   /*public static void main(String[] args) {
      // Start all Swing applications on the EDT.
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            	new RMLJavaScriptEditor(null).setVisible(true);
         }
      });
   }*/

	void setScriptText(){
		String s = textAreaEditor.getText();
		switch(scriptType){
		case JAVA_SCRIPT: s="\""+s+"\""; break;
		case MACRO_SCRIPT:s="~"+s+"~";	break; 
		}
		
		rmlPanel.replaceRange(s, startPosition, startPosition+scriptLength);
		//rmlPanel.select(s, startPosition, startPosition+scriptLength);
		this.scriptLength = s.length();
	}
	
	
@Override
public void actionPerformed(ActionEvent e) {
	//buttons click
	System.out.println("clicked: "+e.getActionCommand());
	if (SAVE_BUTTON.equals(e.getActionCommand())) {
		setScriptText();
    } else if (SAVE_EXIT_BUTTON.equals(e.getActionCommand())) {
    	setScriptText();
    	rmlPanel.hidePropEditPanel();
    }else if (CANCEL_BUTTON.equals(e.getActionCommand())) {
        rmlPanel.hidePropEditPanel();
    }
	
}


public void setEditable(boolean b) {
	textAreaEditor.setEditable(b);
	
}

}
