package views.edit.rml;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class RMLOutlineTree extends AbstractSourceTree {

	private RMLParser parser;
	private RMLEditorListener listener;
	private DefaultTreeModel model;
	private RMLTreeCellRenderer rmlTreeCellRenderer;


	/**
	 * Constructor.  The tree created will not have its elements sorted
	 * alphabetically.
	 */
	public RMLOutlineTree() {
		this(false);
	}


	/**
	 * Constructor.
	 *
	 * @param sorted Whether the tree should sort its elements alphabetically.
	 *        Note that outline trees will likely group nodes by type before
	 *        sorting (i.e. methods will be sorted in one group, fields in
	 *        another group, etc.).
	 */
	public RMLOutlineTree(boolean sorted) {
		setSorted(sorted);
		setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
		setRootVisible(false);
		rmlTreeCellRenderer = new RMLTreeCellRenderer();
		setCellRenderer(rmlTreeCellRenderer);
		model = new DefaultTreeModel(new RMLTreeNode("Nothing"));
		setModel(model);
		listener = new RMLEditorListener();
		addTreeSelectionListener(listener);
	}


	/**
	 * Refreshes listeners on the text area when its syntax style changes.
	 */
	private void checkForRmlParsing() {

		System.out.println("checkForRmlParsing");
		
		// Remove possible listener on old Java parser (in case they're just
		// changing syntax style AWAY from Java)
		if (parser!=null) {
			parser.removePropertyChangeListener(RMLParser.PROPERTY_AST, listener);
			parser = null;
		}

		// Get the Java language support (shared by all RSTA instances editing
		// Java that were registered with the LanguageSupportFactory).
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SyntaxConstants.
													SYNTAX_STYLE_RML);
		RMLLanguageSupport xls = (RMLLanguageSupport)support;

		// Listen for re-parsing of the editor, and update the tree accordingly
		parser = xls.getParser(textArea);
		if (parser!=null) { // Should always be true
			parser.addPropertyChangeListener(RMLParser.PROPERTY_AST, listener);
			// Populate with any already-existing AST.
			RMLTreeNode root = parser.getAst();
			update(root);
		}
		else {
			update((RMLTreeNode)null); // Clear the tree
			
		}

	}


//static int expandCount;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void expandInitialNodes() {

		//long start = System.currentTimeMillis();
		//expandCount = 0;
		fastExpandAll(new TreePath(getModel().getRoot()), true);
		//long end2 = System.currentTimeMillis();
		//System.out.println("Expand all all: " + ((end2-start)/1000f) + " seconds (" + expandCount + ")");
		//System.out.println("--- " + getRowCount());

	}


	RMLTreeNode gotoElementAtPath(TreePath path) {
		Object node = path.getLastPathComponent();
		if (node instanceof RMLTreeNode) {
			RMLTreeNode xtn = (RMLTreeNode)node;
			DocumentRange range = new DocumentRange(xtn.getStartOffset(),
					xtn.getEndOffset());
			RSyntaxUtilities.selectAndPossiblyCenter(textArea, range, true);
			return xtn;
		}
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean gotoSelectedElement() {
		TreePath path = getLeadSelectionPath();//e.getNewLeadSelectionPath();
		if (path != null) {
			gotoElementAtPath(path);
			return true;
		}
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void listenTo(RSyntaxTextArea textArea) {

		if (this.textArea!=null) {
			uninstall();
		}

		// Nothing new to listen to
		if (textArea==null) {
			return;
		}

		// Listen for future language changes in the text editor
		this.textArea = textArea;
		textArea.addPropertyChangeListener(
							RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);

		checkForRmlParsing();

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall() {

		if (parser!=null) {
			parser.removePropertyChangeListener(RMLParser.PROPERTY_AST, listener);
			parser = null;
		}

		if (textArea!=null) {
			textArea.removePropertyChangeListener(
					RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);
			textArea = null;
		}

	}


	private void update(RMLTreeNode root) {
		if (root!=null) {
			root = (RMLTreeNode)root.cloneWithChildren();
		}
		model.setRoot(root);
		
		if (root!=null) {
			root.setSorted(isSorted());
		}
		refresh();
	}


	/**
	 * Overridden to update the cell renderer
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		rmlTreeCellRenderer = new RMLTreeCellRenderer();
		setCellRenderer(rmlTreeCellRenderer); // So it picks up new LAF's properties
	}


	/**
	 * Listens for events this tree is interested in (events in the associated
	 * editor, for example), as well as events in this tree.
	 */
	private class RMLEditorListener implements PropertyChangeListener,
							TreeSelectionListener {
		
		String[] scriptProps = {"ACTION","DEP","QUERY"};

		/**
		 * Called whenever the text area's syntax style changes, as well as
		 * when it is re-parsed.
		 */
		@Override
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			// If the text area is changing the syntax style it is editing
			if (RSyntaxTextArea.SYNTAX_STYLE_PROPERTY.equals(name)) {
				checkForRmlParsing();
			}

			else if (RMLParser.PROPERTY_AST.equals(name)) {
				RMLTreeNode root = (RMLTreeNode)e.getNewValue();
				update(root);
			}

		}

		/**
		 * Selects the corresponding element in the text editor when a user
		 * clicks on a node in this tree.
		 */
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (getGotoSelectedElementOnClick()) {
				//gotoSelectedElement();
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath!=null) {
					gotoElementAtPath(newPath);
				}
			}
		}

		

	}


	/**
	 * Whenever the caret moves in the listened-to RSyntaxTextArea, this
	 * class ensures that the XML element containing the caret position is
	 * focused in the tree view after a small delay.
	 */
/*
 * TODO: Make me work for any LanguageSupport (don't synchronize if waiting on
 * a pending parse) and pull me out and make me available for all languages.
	private class Synchronizer implements CaretListener, ActionListener {

		private Timer timer;
		private int dot;

		public Synchronizer() {
			timer = new Timer(650, this);
			timer.setRepeats(false);
		}

		public void actionPerformed(ActionEvent e) {
			recursivelyCheck(root);
			//System.out.println("Here");
		}

		public void caretUpdate(CaretEvent e) {
			this.dot = e.getDot();
			timer.restart();
		}

		private boolean recursivelyCheck(XmlTreeNode node) {
			if (node.containsOffset(dot)) {
				for (int i=0; i<node.getChildCount(); i++) {
					XmlTreeNode child = (XmlTreeNode)node.getChildAt(i);
					if (recursivelyCheck(child)) {
						return true;
					}
				}
				// None of the children contain the offset, must this guy
				node.selectInTree();
				return true;
			}
			return false;
		}

	}
*/

}
