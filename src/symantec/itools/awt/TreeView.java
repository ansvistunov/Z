package symantec.itools.awt;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Scrollbar;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.ItemSelectable;
import java.awt.AWTEventMulticaster;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;


//	01/15/97	RKM	Changed drawTree to make certain g1 has a font, before calling getFontMetrics on it
//	01/15/07	RKM	Added invalidate to setTreeStructure
//	01/29/97	TWB	Integrated changes from Windows and RKM's changes
// 	01/29/97	TWB	Integrated changes from Macintosh
//  02/05/97    MSH Changed so that draws from first visible node
//  02/27/97    MSH Merged change to add SEL_CHANGED
//  04/02/97    TNM Draw all vertical lines
//  04/14/97    RKM Changed bogus invalidates to repaint
//				RKM	Changed hard coded sbVWidth to use preferredSize.width
//				RKM Changed getTreeStructure so it actually returned a representation of what was in the treeview
//				RKM	Rearranged a lot of stuff to get this to work
//				RKM Changed g1.drawRect in drawTree to not overlap the scrollbar
//				RKM Changed parseTreeStructure to not force a root node
//  05/02/97    RKM Add arg to addSibling so caller could control whether the sible was added as the last sibling or not
//				RKM	Changed insert to call addSibling with false when handling NEXT
//				RKM	Kept addSibling with two params for compatibility
//	05/31/97	RKM	Updated to support Java 1.1
//					Made properties bound & constrained
//					Removed get/setBackground & get/setForeground overrides, getter did nothing but call the super
//					and setters were calling repaint, no one else does this
//					Deprecated foreground and background hilite colors, used system colors instead
//					Deprecated SEL_CHANGED, replaced by ItemSelectable interface
//					Hid scrollbar on creation, to avoid ugly redraw problems
//					Changed to triggered redraw
//					NOTE: SystemColor seems to be broken on Mac
//  06/01/97	RKM	Changed symantec.beans references to java.beans
//  05/13/97    TNM Added horizontal scrollbar
//  05/15/97    TNM Check for vendor to corect scrollbar problem
//  06/09/97    CAR Modified check for vendor to include java.version 1.1.x
//  06/19/97    TNM Merging changes
//  07/25/97    CAR marked fields transient as needed will have to re-test after event handling code is changed
//  08/14/97    RKM Changed hiliteColor to be consistent when on Mac
//  08/20/97    LAB Changed protection of triggerRedraw to public from protected to allow the node
//					to trigger redraws.  Addresses Mac Bug #4372.  Changed privates to protecteds.
//					Reorganized the code in correspondence to the GoodBean Spec.  Deleted null
//					methods that had been deprecated.  Made some package level functions public.
//					Separated the internals of event hadling into protected handle<event> methods
//					to facilitate overriding.  Updated the InvalidTreeNodeException class to take
//					a message string for more detailed exceptions.  Fixed findLastPreSpace to handle
//					being passed null strings, or strings with no lenght (Addresses Mac Bug #4005).
//					Added clear method (Addresses Mac Bug #7369).  Fixed so selection is only set
//					if a single or double click occurs on a node, or if selected node was made
//					invisible by node collapse.  deprecated preferredSize and minimumSize in favor
//					of getPreferredSize and getMiniumumSize.  Made getPreferredSize calculate
//					the size TreeView should be based on the parts of the TreeView that are visible.
//					Added newTreeNode function to allow interception of the creation of nodes (Addresses
//					Win Bug #4174).  Took out isDesignTime code in paint that caused looping paints
//					(i.e. flickering) at design time.  Added paintTree(TreeNode, boolean) that
//					allows tree to be output with indenting (Win Bug #13095).  Fixed append to
//					make sure the node to append didn't already exist in the tree (Win Bug #13050).
//					Updated javadoc on append, insert, remove, removeSelected, etc. (Win Bug #12663).
//  08/22/97    LAB	Call resetVector() before sending Action events so internal state is reset
//					(Addresses Win Bug #12666).  redraw now only calls resetVector if needed.
//  08/26/97    CAR added null and zero length parameter checks to setTreeStructure
//  08/28/97    CAR fixed bug re: horizontal scrolling not working
//  08/28/97    RKM Added isFocusTraversable override (Yep, you can tab to this one)
//  08/29/97    CAR modified getPreferredSize and getMinimumSize
//  09/24/97    RKM Properly set isSun1_1 for Apple MRJ 2.0
//					Set isSun1_1 same as MutliList did (changed to static)
//  10/04/97    LAB	Added ItemEvent firing when a node is expaned or collapsed.  Added
//					NODE_TOGGLED, NODE_EXPANDED, and NODE_COLLAPSED constants to support this.
//  12/09/97    DS  Added check for a null itemListener in handleMousePressed
//  12/19/97    DS  Added more checks for a null itemListener in handleMousePressed
//  02/09/98    DS  Added support for hiding/showing nodes (TreeNode.setHidden(boolean)
//                  Added support for deslecting a node (setSelectedNode(Tree Node)


/**
 * Creates an "outline view" of text headings and, optionally, images.
 * The headings are arranged in a hierarchical fashion, and can be
 * expanded to show their sub-headings or collapsed, hiding their
 * sub-headings.
 * <p>
 * A TreeView is typically used to display information that is organized in a
 * hierarchical fashion like an index or table of contents.
 * <p>
 * A TreeNode object is used for each heading.
 * @see TreeNode
 */
public class TreeView extends Panel implements ItemSelectable
{
	// constants for insertion
	/**
	 * Constant indicating that the new node is to be a child
	 * of the existing node.
	 * @see #insert
	 */
	public static final int CHILD   = 0;
	/**
	 * Constant indicating that the new node is to be the next
	 * sibling of the existing node.
	 * @see #insert
	 */
	public static final int NEXT    = CHILD + 1;
	/**
	 * Constant indicating that the new node is to be the last
	 * sibling of the existing node.
	 * @see #insert
	 */
	public static final int LAST    = CHILD + 2;
	/**
	 * Constand used to describe ItemEvents sent when a node is toggled.
	 * @see #NODE_EXPANDED
	 * @see #NODE_COLLAPSED
	 */
	public static final int NODE_TOGGLED = 2001;
	/**
	 * Constand used to detail that the node referenced in a NODE_TOGGLED ItemEvent has been expanded.
	 * @see #NODE_TOGGLED
	 * @see #NODE_COLLAPSED
	 */
	public static final int NODE_EXPANDED = 2002;
	/**
	 * Constand used to detail that the node referenced in a NODE_TOGGLED ItemEvent has been collapsed.
	 * @see #NODE_TOGGLED
	 * @see #NODE_EXPANDED
	 */
	public static final int NODE_COLLAPSED = 2003;
    /**
     * @deprecated As of JDK version 1.1,
     * replaced by ItemSelectable interface.
     * @see java.awt.ItemSelectable
     */
	public static final int SEL_CHANGED = 1006; //selection changed event

	//
	// Constructors
	//

	/**
	 * Constructs an empty TreeView.
	 */
	public TreeView()
	{
		super.setLayout(null);

	    verticalScrollBar = new Scrollbar(Scrollbar.VERTICAL);
        verticalScrollBar.hide();
        add(verticalScrollBar);

        horizontalScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
        horizontalScrollBar.hide();
        add(horizontalScrollBar);

		needResetVector = true;
	}

	/**
	 * Constructs a TreeView with the given node.
	 *
	 * @param head the root node of the constructed tree
	 */
	public TreeView(TreeNode head)
	{
	    this();
	    selectedNode = rootNode = head;
	    count = 1;
	}

	//
	// Properties
	//

    /**
     * Initializes the TreeView from a string array.
     * There is one string for each node in the array. That string
     * contains the text of the node indented with same number of
     * leading spaces as the depth of that node in the tree.
     * @param s the string array used for initialization.
     * If null, the tree will be cleared.
     * @see #getTreeStructure
     */
	public void setTreeStructure(String s[])
	{
	    if (s == null || s.length == 0)
	    {
	        clear();
	        return;
	    }

	    rootNode = selectedNode = null;
		try
		{
		    parseTreeStructure(s);
		}
		catch(InvalidTreeNodeException e)
		{
		    System.err.println(e);
		}

		triggerRedraw();

	    invalidate();
	}

    /**
     * Gets a string array that reflects the current TreeView's contents.
     * There is one string for each node in the array. That string
     * contains the text of the node indented with same number of
     * leading spaces as the depth of that node in the tree.
     * @return the string array that reflects the TreeView's contents
     * @see #setTreeStructure
     */
	public String[] getTreeStructure()
	{
		//Create a vector representing current tree structure
		if (rootNode==null) return null;
		Vector nodesVector = new Vector(count);
		rootNode.depth = 0;
		vectorize(rootNode, false, false, nodesVector);

		//Convert this to a String[]
		int numNodes = nodesVector.size();
		String[] treeStructure = new String[numNodes];
		for (int i = 0;i < numNodes;i++)
		{
			TreeNode thisNode = (TreeNode)nodesVector.elementAt(i);

			//Add appropriate number of blanks
			String treeString = "";
			for (int numBlanks = 0;numBlanks < thisNode.depth;numBlanks++)
				treeString += ' ';

			//Add tree
			treeString += thisNode.text;

			//Put string into array
			treeStructure[i] = treeString;
		}

	    return treeStructure;
	}

	//
	// Deprecated Properties
	//

	/**
     * @deprecated As of JDK version 1.1,
     * replaced by use of SystemColors.textHighlightText.
	 */
	public Color getFgHilite()
	{
	    return SystemColor.textHighlightText;
	}

	/**
     * @deprecated As of JDK version 1.1,
     * replaced by use of SystemColors.textHighlight.
	 */
	public Color getBgHilite()
	{
	    return SystemColor.textHighlight;
	}

	//
	// ItemSelectable interface
	//

    /**
     * Returns the selected items or null if no items are selected.
     * <p>
     * This is a standard method of the ItemSelectable interface.
     */
    public Object[] getSelectedObjects()
    {
    	if (selectedNode == null)
    		return null;

    	TreeNode[] selectedObjects = new TreeNode[1];
    	selectedObjects[0] = selectedNode;

    	return selectedObjects;
    }

	//
	// Methods
	//

	// Insert a new node relative to a node in the tree.
	// position = CHILD inserts the new node as a child of the node
	// position = NEXT inserts the new node as the next sibling
	// position = LAST inserts the new node as the last sibling
	/**
	 * Inserts a new node relative to an existing node in the tree.
	 * @param newNode the new node to insert into the tree
	 * @param relativeNode the existing node used for a position reference
	 * @param position where to insert the new node relative to relativeNode.
	 * Legal values are CHILD, NEXT and LAST.
	 * @see #CHILD
	 * @see #NEXT
	 * @see #LAST
	 * @see #append
	*/
	public void insert(TreeNode newNode, TreeNode relativeNode, int position)
	{
	    if (newNode == null || relativeNode == null)
	        return;

	    if (exists(relativeNode)==false)
	        return;

	    switch (position)
	    {
	        case CHILD:
	            addChild(newNode, relativeNode);
	            break;

	        case NEXT:
	            addSibling(newNode, relativeNode, false);
	            break;

	        case LAST:
	            addSibling(newNode, relativeNode, true);
	            break;

	        default:
	            // invalid position
	            return;
	    }
	}

	/**
	 * Clears the tree structure and redraws.
	 */
	public void clear()
	{
		rootNode = selectedNode = null;
	    count = 0;
	    v = new Vector();
	    e = new Vector();
		triggerRedraw();

		invalidate();
	}

    /**
     * Returns the "root" node.
     * The root node is the first top-level node in the tree hierarchy.
     * All other nodes are either children or siblings of that one.
     * @return the root tree node
     */
	public TreeNode getRootNode()
	{
	    return rootNode;
	}

	/**
	 * Returns the total number of nodes in the tree.
	 */
	public int getCount()
	{
	    return count;
	}

	/**
	 * Returns the total number of viewable nodes in the tree.
     * A node is viewable if all of its parents are expanded.
     */
	public int getViewCount()
	{
	    return viewCount;
	}

	/**
	 * Determines if the given node is viewable.
     * A node is viewable if all of its parents are expanded.
     * @param node the node to check
     * @return true if the node is visible, false if it is not
     * @see #viewable(java.lang.String)
	 */
	boolean viewable(TreeNode node)
	{
	    for (int i=0; i<viewCount; i++)
	    {
	        if (node == v.elementAt(i))
	        {
	            return true;
	        }
	    }

	    return false;
	}

	/**
	 * Determines if the node with the given text is viewable.
     * A node is viewable if all of its parents are expanded.
     * @param s the node text to find
     * @return true if the node is visible, false if it is not
     * @see #viewable(TreeNode)
	 */
	boolean viewable(String s)
	{
	    if (s==null)
	    {
	        return false;
	    }

	    for (int i=0; i<viewCount; i++)
	    {
	        TreeNode tn = (TreeNode)v.elementAt(i);

	        if (tn.text != null)
	        {
	            if (s.equals(tn.text))
	            {
	                return true;
	            }
	        }
	    }

	    return false;
	}

	/**
	 * Determines if the given node is in the TreeView.
     * @param node the node to check
     * @return true if the node is in the TreeView, false if it is not
     * @see #exists(java.lang.String)
	 */
	public boolean exists(TreeNode node)
	{
	    recount();

	    for (int i=0; i<count; i++)
	    {
	        if (node == e.elementAt(i))
	        {
	            return true;
	        }
	    }

	    return false;
	}

	/**
	 * Determines if the node with the given text is in the TreeView.
	 * @param s the node text to find
     * @return true if the node is in the TreeView, false if it is not
     * @see #exists(symantec.itools.awt.TreeNode)
	 */
	public boolean exists(String s)
	{
	    recount();

	    if (s==null)
	    {
	        return false;
	    }

	    for (int i=0; i<count; i++)
	    {
	        TreeNode tn = (TreeNode)e.elementAt(i);

	        if (tn.text != null)
	        {
	            if (s.equals(tn.text))
	            {
	                return true;
	            }
	        }
	    }

	    return false;
	}

	/**
	 * Adds a new node at root level. If there is no root node, the given
	 * node is made the root node. If there is a root node, the given node
	 * is made a sibling of the root node.
	 * @param newNode the new node to add
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
	 * @see #insert
	 */
	public void append(TreeNode newNode)
	{
	    if (rootNode == null)
	    {
	        rootNode = newNode;
	        selectedNode = rootNode;
	        count = 1;
	        redrawTriggered = true;
	    }
	    else
	    {
	    	recount();
	    	if (e.contains(newNode))
	    		System.err.println(new InvalidTreeNodeException("append: " + errors.getString("NodeAlreadyExists")));
	    	else
		        addSibling(newNode, rootNode, true);
	    }
	}

	/**
	 * Adds the specified child node to the specified parent node
	 * @param newNode the node to add as a child
	 * @param relativeNode the node to add the child to.
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
	 */
	public void addChild(TreeNode newNode, TreeNode relativeNode)
	{
	    if (relativeNode.child == null)
	    {
	        relativeNode.child = newNode;
	        newNode.parent = relativeNode;
	        count++;
	        redrawTriggered = true;
	    }
	    else
	    {
	        addSibling(newNode, relativeNode.child, true);
	    }

	    relativeNode.numberOfChildren++;
	}

	/**
	 * Adds the specified node as a sibling to the specified node
	 * @param newNode the node to add as a sibling
	 * @param siblingNode a sibling node to the new node.
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
	 * @see #addSibling(TreeNode newNode, TreeNode siblingNode, boolean asLastSibling)
	 */
	public void addSibling(TreeNode newNode, TreeNode siblingNode)
	{
		addSibling(newNode,siblingNode,true);
	}

	/**
	 * Adds the specified node as a sibling to the specified node
	 * @param newNode the node to add as a sibling
	 * @param siblingNode a sibling node to the new node.
	 * @param asLastSibling if true, then add the new node as the last (bottommost)
	 * sibling node to the specified sibling node.
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
	 * @see #addSibling(TreeNode newNode, TreeNode siblingNode)
	 */
	public void addSibling(TreeNode newNode, TreeNode siblingNode, boolean asLastSibling)
	{
		if (asLastSibling)
		{
			//Find last sibling
			TreeNode tempNode = siblingNode;
			while (tempNode.sibling != null)
				tempNode = tempNode.sibling;

			tempNode.sibling = newNode;
		}
		else
		{
			//Insert the newNode below the siblingNode
			newNode.sibling = siblingNode.sibling;

			siblingNode.sibling = newNode;
		}

		//Set the parent of the new node to the parent of the sibling
		newNode.parent = siblingNode.parent;

		count++;
		redrawTriggered = true;
	}

	/**
	 * Removes the node with the given text from the TreeView.
	 * @param s the node text to find
     * @return the TreeNode removed from this TreeView or null if not found
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
     * @see #remove(symantec.itools.awt.TreeNode)
     * @see #removeSelected
	 */
	public TreeNode remove(String s)
	{
		recount();

		for (int i=0; i<count; i++)
		{
			TreeNode tn = (TreeNode)e.elementAt(i);

			if (tn.text != null)
			{
			    if (s.equals(tn.text))
			    {
			        remove(tn);
			        redrawTriggered = true;
			        return tn;
			    }
			}
		}

		return null;
	}

	/**
	 * Removes the currently selected node from the TreeView.
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
     * @see #remove(symantec.itools.awt.TreeNode)
     * @see #remove(java.lang.String)
	 */
	public void removeSelected()
	{
	    if (selectedNode != null)
	    {
	        remove(selectedNode);
	    }
	}

	/**
	 * Removes the given node from the TreeView.
	 * @param node the node to remove
     * @return the TreeNode removed from this TreeView or null if not found
	 * Does not redraw the component.  Allows you to call repeatedly without
	 * causing the component to flicker while nodes are manipulated.  After all
	 * manipulation is done, repaint the tree.
     * @see #remove(java.lang.String)
     * @see #removeSelected
	 */
	public void remove(TreeNode node)
	{
	    if (!exists(node))
	    {
	        return;
	    }

	    if (node == selectedNode)
	    {
	        int index = v.indexOf(selectedNode);

	        if (index == -1)
	        {    //not viewable
	            index = e.indexOf(selectedNode);
	        }

	        if (index > viewCount-1)
	        {
	            index = viewCount-1;
	        }

	        if (index>0)
	        {
	            setSelectedNode((TreeNode)v.elementAt(index-1));
	        }
	        else if (viewCount>1)
	        {
	            setSelectedNode((TreeNode)v.elementAt(1));
	        }
	    }

	    // remove node and its decendents
	    if (node.parent != null)
	    {
	        if (node.parent.child == node)
	        {
	            if (node.sibling != null)
	            {
	                node.parent.child = node.sibling;
	            }
	            else
	            {
	                node.parent.child = null;
	                node.parent.collapse();
	            }
	        }
	        else
	        {
	            TreeNode tn=node.parent.child;

	            while (tn.sibling != node)
	            {
	                tn = tn.sibling;
	            }

	            if (node.sibling != null)
	            {
	                tn.sibling = node.sibling;
	            }
	            else
	            {
	                tn.sibling = null;
	            }
	        }
	    }
	    else
	    {
	        if (node == rootNode)
	        {
	            if (node.sibling == null)
	            {
	                rootNode=null;
	            }
	            else
	            {
	                rootNode=node.sibling;
	            }
	        }
	        else
	        {
	            TreeNode tn = rootNode;

	            while (tn.sibling != node)
	            {
	                tn = tn.sibling;
	            }

	            if (node.sibling != null)
	            {
	                tn.sibling = node.sibling;
	            }
	            else
	            {
	                tn.sibling = null;
	            }
	        }
	    }

	    recount();
	    redrawTriggered = true;
	}

	/**
	 * Print out the text of each node in the TreeView beginning with
	 * the given node.
	 * The nodes are printed out one per line with no indenting.
	 * @param node the first node to print
	 */
	public void printTree(TreeNode node)
	{
		printTree(node, false);
	}

	/**
	 * Print out the text of each node in the TreeView beginning with
	 * the given node.
	 * The nodes are printed out one per line with no indenting.
	 * @param node the first node to print
	 * @param isIndented, if true, nodes will have hierarchical indenting,
	 * if false, all nodes will pe printed at the same level.
	 */
	public void printTree(TreeNode node, boolean isIndented)
	{
	    if (node == null)
	    {
	        return;
	    }
	    String padding = new String();
		if (isIndented)
		{
			for (int i = 0; i < node.depth; i++)
				padding = "  " + padding;
		}

	    System.out.println(padding + node.text);
	    printTree(node.child, isIndented);
	    printTree(node.sibling, isIndented);
	}

	/**
	 * Gets the currently selected node.
	 * @return the currently selected node, or null if none selected
	 */
	public TreeNode getSelectedNode()
	{
	    return selectedNode;
	}

	/**
	 * Gets the text of the currently selected node.
	 * @return the text of the currently selected node or null if no node
	 * is selected
	 */
	public String getSelectedText()
	{
	    if (selectedNode == null)
	        return null;

	    return selectedNode.getText();
	}

	// -----------------------------------------
	// --------- graphics related methods ------
	// -----------------------------------------
    /**
     * Handles redrawing of this component on the screen.
     * This is a standard Java AWT method which gets called by the Java
     * AWT (repaint()) to handle repainting this component on the screen.
     * The graphics context clipping region is set to the bounding rectangle
     * of this component and its [0,0] coordinate is this component's
     * top-left corner.
     * Typically this method paints the background color to clear the
     * component's drawing space, sets graphics context to be the foreground
     * color, and then calls paint() to draw the component.
     *
     * It is overridden here to reduce flicker by eliminating the uneeded
     * clearing of the background.
     *
     * @param g the graphics context
     * @see java.awt.Component#repaint
     * @see #paint
     */
	public void update (Graphics g)
	{
	    //(eliminates background draw to reduce flicker)
	    paint(g);
	}

	/**
	 * Paints this component using the given graphics context.
     * This is a standard Java AWT method which typically gets called
     * by the AWT to handle painting this component. It paints this component
     * using the given graphics context. The graphics context clipping region
     * is set to the bounding rectangle of this component and its [0,0]
     * coordinate is this component's top-left corner.
     *
     * @param g the graphics context used for painting
     * @see java.awt.Component#repaint
     * @see #update
	 */
	public void paint (Graphics g)
	{
		Dimension d = size();

		if (redrawTriggered || (d.width != viewWidth) || (d.height != viewHeight))
		{
			// redraw needed, or size has changed
			redraw(g);
		}

		g.translate(-sbHPosition, 0);
        g.clearRect(sbHPosition,0,d.width-sbVWidth,d.height-sbHHeight);
        if (sbVShow && sbHShow)
        {
            g.setColor(Color.lightGray);
            g.fillRect(sbHPosition+d.width-sbVWidth, d.height-sbHHeight, sbVWidth, sbHHeight);
        }
        g.clipRect(sbHPosition,0,d.width-sbVWidth,d.height-sbHHeight);
		g.drawImage(im1, 0, 0, this);
		g.setColor(Color.black);
        g.drawRect(sbHPosition,0, d.width-sbVWidth-1, d.height-sbHHeight-1);

	}

    /**
     * Lays out the vertical scrollbar as needed, then draws the TreeView into
     * an offscreen image. This is used for cleaner refresh.
     */
    public void redraw()
    {
        //For backward compatibality. Do not allow to call only redraw() without recalculation.
        triggerRedraw();
    }
    
    public void repaint(boolean f)
    {
        if(f)
        {
            needResetVector = true;
        }
        
        triggerRedraw();
    }

    /**
     * Lays out the vertical scrollbar as needed, then draws the TreeView into
     * an offscreen image. This is used for cleaner refresh.
     * @param g the graphics object use for drawing
     */
	public void redraw(Graphics g)
	{
        Dimension d = size();

		redrawTriggered = false;

		if(needResetVector)
			resetVector();
		else
			needResetVector = true;

        newWidth = compWidth(g);

        int inRectCount = ((d.height - sbHHeight) / cellSize);
        
	    if (viewCount > inRectCount)
	    {
  	        // need the vertical scrollbar
	        sbVShow  = true;
	        sbVWidth = verticalScrollBar.preferredSize().width;
	    }
	    else
	    {
  	        sbVShow     = false;
	        sbVWidth    = 0;
	        sbVPosition = 0;
	    }

	    if (newWidth > (d.width - sbVWidth))
	    {
	        // need the horizontal scrollbar
	        sbHShow = true;
			sbHHeight = horizontalScrollBar.preferredSize().height;
	    }
	    else
	    {
	        sbHShow     = false;
            sbHHeight   = 0;
            sbHPosition = 0;
	    }

        drawTree();

	    if (sbVShow)
	    {
	        verticalScrollBar.reshape(d.width-sbVWidth,0,sbVWidth,d.height-sbHHeight);
	        verticalScrollBar.setValues(sbVPosition, inRectCount, 0, viewCount-(isSun1_1?0:inRectCount));
			verticalScrollBar.setPageIncrement(inRectCount-1);
	        verticalScrollBar.show();
	    }
	    else
	    {
	        verticalScrollBar.hide();
	    }

        if (sbHShow)
	    {
	        horizontalScrollBar.reshape(0,d.height-sbHHeight,d.width-sbVWidth,sbHHeight);
            horizontalScrollBar.setValues(sbHPosition, d.width-sbVWidth, 0, sbHSize-(isSun1_1?0:(d.width-sbVWidth)));
            horizontalScrollBar.setPageIncrement(d.width-sbVWidth);
            horizontalScrollBar.setLineIncrement(sbHLineIncrement);
            horizontalScrollBar.show();
	    }
	    else
	    {
	        horizontalScrollBar.hide();
	    }
	}

    /**
     * Draws the TreeView into an offscreen image. This is used for cleaner refresh.
     */
	public void drawTree()
	{
		Dimension d = size();

		if(needResetVector)
			resetVector();

		if ((d.width != viewWidth) || (d.height != viewHeight) || (g1 == null) || (sbHSize != newWidth))
		{
	        // size has changed, must resize image
			im1 = createImage(Math.max(sbHSize=newWidth, d.width), d.height);
			
			if (g1 != null)
			{
				g1.dispose();
			}
	        
	        g1         = im1.getGraphics();
	        viewWidth  = d.width;
	        viewHeight = d.height;
		}

	    Font f = getFont();  //unix version might not provide a default font

		//Make certain there is a font
	    if (f == null)
	    {
	        f = new Font("Serif", Font.PLAIN, 13);
	        g1.setFont(f);
	        setFont(f);
	    }

	    //Make certain the graphics object has a font (Mac doesn't seem to)
		if (g1.getFont() == null)
			g1.setFont(f);

	    fm = g1.getFontMetrics();
	    g1.setColor(getBackground());
	    g1.fillRect(0, 0, im1.getWidth(this), d.height);// clear image

	    //do drawing for each visible node
	    int lastOne = sbVPosition + viewHeight / cellSize + 1;

	    if (lastOne > viewCount)
	    {
	        lastOne = viewCount;
	    }

	    TreeNode outerNode = null;
	    
	    if (!v.isEmpty())
	        outerNode = (TreeNode)v.elementAt(sbVPosition);
	        
	    for (int i = sbVPosition; i < lastOne; i++)
	    {
	        TreeNode node = (TreeNode)v.elementAt(i);
	        int x         = cellSize*(node.depth + 1);
	        int y         = (i - sbVPosition) * cellSize;

	        // draw lines
	        g1.setColor(getForeground());

	        // draw vertical sibling line
	        if (node.sibling != null && node.isASiblingVisible())
	        {
	            int k = v.indexOf(node.sibling) - i;

	            if (k > lastOne)
	            {
	                k = lastOne;
	            }

	            drawDotLine(x - cellSize/2, y + cellSize/2,
	                        x - cellSize/2, y + cellSize/2 +  k*cellSize);

	        }
	        
	        // if sibling is above page, draw up to top of page for this level
	        for (int m = 0; m < i; m++)
	        {
	            TreeNode sib = (TreeNode)v.elementAt(m);

	            if ((sib.sibling == node) && (m < sbVPosition))
	            {
	                drawDotLine (x - cellSize / 2, 0,
	                             x - cellSize / 2, y + cellSize / 2);
	            }
	        }

	        // draw vertical child lines
	        if (node.isExpanded() && node.isAChildVisible())
	        {
	            drawDotLine(x + cellSize / 2, y + cellSize - 2 ,
	                        x + cellSize / 2, y + cellSize + cellSize / 2);
	        }
	        
	        // draw node horizontal line
	        g1.setColor(getForeground());
	        drawDotLine(x - cellSize / 2, y + cellSize / 2,
	                    x + cellSize / 2, y + cellSize / 2);

	        // draw toggle box
	        drawNodeToggle(node, x, y);

	        // draw node image
	        Image nodeImage = node.getImage();

	        if (nodeImage != null)
	        {
				//---written by And
				while (!g1.drawImage(nodeImage, x + imageInset, y, this)){
					try{
						Thread.sleep(200);
					}catch(Exception e){}
				}
				//---
				
	            //g1.drawImage(nodeImage, x + imageInset, y, this);				
	        }

	        // draw node text
	        if (node.text != null)
	        {
	            drawNodeText(node, y, node == selectedNode, false);
	        }

	        if(outerNode.depth > node.depth)
	            outerNode = node;
	    }

	    // draw outer vertical lines
	    if (outerNode != null)
	    {
    	    while((outerNode = outerNode.parent) != null)
	        {
	            if (outerNode.sibling != null && outerNode.isASiblingVisible())
	                drawDotLine (cellSize * (outerNode.depth + 1) - cellSize / 2, 0,
	                             cellSize * (outerNode.depth + 1) - cellSize / 2, d.height);
    	    }
        }
        
		needResetVector = true;
	}

	/**
	 * Used to draw the toggle box of an expandable node.
	 * Override to change the look of the toggle box.
	 */
	protected void drawNodeToggle(TreeNode node, int x, int y)
	{
	    if(node.isExpandable() && node.isAChildVisible())
	    {
	        g1.setColor(getBackground());
	        g1.fillRect(cellSize * (node.depth) + cellSize / 4, y + clickSize / 2, clickSize, clickSize);
	        g1.setColor(getForeground());
	        g1.drawRect(cellSize * (node.depth) + cellSize / 4, y + clickSize / 2, clickSize, clickSize);
	        
	        // cross hair
	        g1.drawLine(cellSize * (node.depth) + cellSize / 4 + 2,             y + cellSize / 2,
	                    cellSize * (node.depth) + cellSize / 4 + clickSize - 2, y + cellSize / 2);

	        if(!(node.isExpanded()))
	        {
	            g1.drawLine(cellSize * (node.depth) + cellSize / 2, y + clickSize / 2 + 2,
	                        cellSize * (node.depth) + cellSize / 2, y + clickSize / 2 + clickSize - 2);
	        }
	    }
	}

    /**
	 * Returns the recommended dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the recommended size of this component.
     *
     * @see #getMinimumSize
	 */
	public synchronized Dimension getPreferredSize()
	{
    	Dimension p = size();
    	Dimension m = getMinimumSize();
    	return new Dimension(Math.max(p.width, m.width), Math.max(p.height, m.height));
	}

    /**
	 * @deprecated
     * @see #getPreferredSize
	 */
	public synchronized Dimension preferredSize()
	{
	    return getPreferredSize();
	}

    /**
	 * Returns the minimum dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the minimum size of this component.
     *
     * @see #getPreferredSize
	 */
	public synchronized Dimension getMinimumSize()
	{
	    return new Dimension(20, 40);
	}

    /**
	 * @deprecated
     * @see #getMinimumSize
	 */
	public synchronized Dimension minimumSize()
	{
	    return getMinimumSize();
	}

	/**
	 * Takes no action.
	 * This is a standard Java AWT method which gets called to specify
	 * which layout manager should be used to layout the components in
	 * standard containers.
	 *
	 * Since layout managers CANNOT BE USED with this container the standard
	 * setLayout has been OVERRIDDEN for this container and does nothing.
	 *
	 * @param lm the layout manager to use to layout this container's components
	 * (IGNORED)
	 * @see java.awt.Container#getLayout
	 **/
	public void setLayout(LayoutManager lm)
	{
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

	/**
	 * Tells this component that it has been added to a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is added to a container. Typically, it is used to
	 * create this component's peer.
	 *
	 * It has been overridden here to hook-up event listeners.
	 *
	 * @see #removeNotify
	 */
	public synchronized void addNotify()
	{
		super.addNotify();
        errors = ResourceBundle.getBundle("symantec.itools.resources.ErrorsBundle");

		//Hook up listeners
		if (mouse == null)
		{
			mouse = new Mouse();
			addMouseListener(mouse);
		}
		if (key == null)
		{
			key = new Key();
			addKeyListener(key);
		}
		if (adjustment == null)
		{
			adjustment = new Adjustment();
			verticalScrollBar.addAdjustmentListener(adjustment);
			horizontalScrollBar.addAdjustmentListener(adjustment);
		}
		if (focus == null)
		{
			focus = new Focus();
			addFocusListener(focus);
		}

	}

	/**
	 * Tells this component that it is being removed from a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is removed from a container. Typically, it is used to
	 * destroy the peers of this component and all its subcomponents.
	 *
	 * It has been overridden here to unhook event listeners.
	 *
	 * @see #addNotify
	 */
	public synchronized void removeNotify()
	{
		//Unhook listeners
		if (mouse != null)
		{
			removeMouseListener(mouse);
			mouse = null;
		}
		if (key != null)
		{
			removeKeyListener(key);
			key = null;
		}
		if (adjustment != null)
		{
			verticalScrollBar.removeAdjustmentListener(adjustment);
			horizontalScrollBar.removeAdjustmentListener(adjustment);
			adjustment = null;
		}
        if (focus != null)
		{
			removeFocusListener(focus);
			focus = null;
		}
		super.removeNotify();
	}

	/**
	 * Triggers redrawing the entire image, even if the size of the component
	 * has not changed.
	 */
	public void triggerRedraw()
	{
		redrawTriggered = true;
		repaint();
	}

	// -----------------------------------------
	// --------- event related methods ---------
	// -----------------------------------------

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */
	public synchronized void addActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
     * @param l the action listener
     */
	public synchronized void removeActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

    /**
     * Add a listener to recieve item events when the state of
     * an item changes.
     * <p>
     * This is a standard method of the ItemSelectable interface.
     * @param l the listener to recieve events
     * @see ItemEvent
     */
    public synchronized void addItemListener(ItemListener l)
    {
        itemListener = AWTEventMulticaster.add(itemListener, l);
    }

    /**
     * Removes an item listener.
     * <p>
     * This is a standard method of the ItemSelectable interface.
     * @param l the listener being removed
     * @see ItemEvent
     */
    public synchronized void removeItemListener(ItemListener l)
    {
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

	class Adjustment implements AdjustmentListener, java.io.Serializable
	{
		public void adjustmentValueChanged(AdjustmentEvent event)
		{
			handleAdjustmentEvent(event);
		}
	}

	class Mouse extends MouseAdapter implements java.io.Serializable
	{
	    /**
	     * Processes MOUSE_DOWN events.
	     * This is a standard Java AWT method which gets called by the AWT
	     * method handleEvent() in response to receiving a MOUSE_DOWN
	     * event. These events occur when the mouse button is pressed while
	     * inside this component.
	     *
	     * @param event the event
	     * @param x the component-relative horizontal coordinate of the mouse
	     * @param y the component-relative vertical coordinate of the mouse
	     *
	     * @return true if the event was handled
	     *
	     * @see java.awt.Component#mouseUp
	     */
		public void mousePressed(MouseEvent event)
		{
			handleMousePressed(event);
		}

		public void mouseReleased(MouseEvent event)
		{
			handleMouseReleased(event);
		}
	}

	class Key extends KeyAdapter implements java.io.Serializable
	{
	    /**
	     * Processes KEY_PRESS and KEY_ACTION events.
	     * This is a standard Java AWT method which gets called by the AWT
	     * method handleEvent() in response to receiving a KEY_PRESS or
	     * KEY_ACTION event. These events occur when this component has the focus
	     * and the user presses a "normal" or an "action" (F1, page up, etc) key.
	     *
	     * @param event the Event
	     * @param key the key that was pressed
	     * @return true if the event was handled
	     * @see java.awt.Component#keyUp
	     * @see #handleEvent
	     */
		public void keyPressed(KeyEvent event)
		{
			handleKeyPressed(event);
		}
	}

	class Focus extends FocusAdapter implements java.io.Serializable
	{
        public void focusGained(FocusEvent event)
        {
			handleFocusGained(event);
        }

        public void focusLost(FocusEvent event)
        {
			handleFocusLost(event);
        }
	}

	/**
	 * Handles mouse pressed events.
	 * This function will get called when the component recives a mouse pressed event.
	 * Override to change the way mouse pressed is handled.
	 * @param event the MouseEvent
	 * @see #handleMouseReleased
	 */
	protected void handleMousePressed(MouseEvent event)
	{
		requestFocus();

		int x = event.getX();
		int y = event.getY();

		int index = (y / cellSize) + sbVPosition;

		//If clicked below the last node
		if (index > viewCount-1)
			return;

		TreeNode oldNode = selectedNode;

		TreeNode newNode = (TreeNode)v.elementAt(index);

		int newDepth = newNode.getDepth();

		// check for toggle box click
		// todo: make it a bit bigger
		Rectangle toggleBox = new Rectangle(cellSize*newDepth + cellSize/4,
						                    (index-sbVPosition)*cellSize + clickSize/2,
						                    clickSize, clickSize);

		if (toggleBox.inside(x,y))
		{
			newNode.toggle();
			resetVector();
			if(!newNode.isExpanded())
			{
				if (!v.contains(selectedNode))
					setSelectedNode(newNode);
			}
			triggerRedraw();
			invalidate();
			sendActionEvent();

			if(itemListener != null)
			{
			    itemListener.itemStateChanged(new ItemEvent(this, NODE_TOGGLED, newNode, newNode.isExpanded ? NODE_EXPANDED : NODE_COLLAPSED));
			}
		}
		else
		{
			setSelectedNode(newNode);

			// check for double click
			long currentTime = event.getWhen();

			if ((newNode==oldNode) && ((event.getWhen() - timeMouseDown)<doubleClickResolution))
			{
				newNode.toggle();
				resetVector();
				triggerRedraw();
				invalidate();
				sendActionEvent();

				if(itemListener != null)
				{
				    itemListener.itemStateChanged(new ItemEvent(this, NODE_TOGGLED, newNode, newNode.isExpanded ? NODE_EXPANDED : NODE_COLLAPSED));
				}

				return;
			}
			else
			{
				//single click action could be added here
				timeMouseDown = event.getWhen();
			}

		}
	}

	/**
	 * Handles mouse released events.
	 * This function will get called when the component recives a mouse released event.
	 * Override to change the way mouse released is handled.
	 * @param event the MouseEvent
	 * @see #handleMousePressed
	 */
	protected void handleMouseReleased(MouseEvent event)
	{
	}

	/**
	 * Handles adjustment events.
	 * This function will get called when the component recives a adjustment event.
	 * Override to change the way adjustment is handled.
	 * @param event the AdjustmentEvent
	 */
	protected void handleAdjustmentEvent(AdjustmentEvent event)
	{
		if (event.getAdjustable() == verticalScrollBar)
		{
			if (sbVPosition != verticalScrollBar.getValue())
			{
				sbVPosition = verticalScrollBar.getValue();
				triggerRedraw();
			}
		}
		else
		if (event.getAdjustable() == horizontalScrollBar)
		{
			if (sbHPosition != horizontalScrollBar.getValue())
			{
				sbHPosition = horizontalScrollBar.getValue();
				//repaint();
				triggerRedraw();
			}
		}
	}

	/**
	 * Handles key pressed events.
	 * This function will get called when the component recives a key pressed event.
	 * Override to change the way key pressed is handled.
	 * @param event the KeyEvent
	 */
	protected void handleKeyPressed(KeyEvent event)
	{
		int index = v.indexOf(selectedNode);

		switch (event.getKeyCode())
		{
		    case KeyEvent.VK_ENTER:    //enter key
		    case 13:
		        sendActionEvent();
		        requestFocus();
		        break;
		    case KeyEvent.VK_LEFT:    //left arrow
		        if(event.isControlDown())
                {
	                if(sbHPosition > 0)
                    {
                        horizontalScrollBar.setValue(Math.max(sbHPosition-=sbHLineIncrement,0));
                        repaint();
                    }
                    break;
	            }
	            else
		        if (selectedNode.isExpanded())
		        {
		            selectedNode.toggle();

		            if(itemListener != null)
		            {
					    itemListener.itemStateChanged(new ItemEvent(this, NODE_TOGGLED, selectedNode, selectedNode.isExpanded ? NODE_EXPANDED : NODE_COLLAPSED));
    		            triggerRedraw();
					}

		            break;
		        }

		        // else drop through to "UP" with no "break;"
		    case KeyEvent.VK_UP:
		        if (index > 0)
		        {
		            index--;
		            setSelectedNode((TreeNode)v.elementAt(index));
		            requestFocus();
		        }
		        break;
		    case KeyEvent.VK_RIGHT:
		        if(event.isControlDown())
    	        {
	                int max = horizontalScrollBar.getMaximum()-(isSun1_1?size().width-sbVWidth:0);
	                if(sbHShow && sbHPosition < max)
                    {
                        horizontalScrollBar.setValue(Math.min(sbHPosition+=sbHLineIncrement, max));
                        repaint();
                    }
                    break;
	            }
	            else
		        if (selectedNode.isExpandable() && (!selectedNode.isExpanded()))
		        {
		            selectedNode.toggle();
		            sendActionEvent();

		            if(itemListener != null)
		            {
    					itemListener.itemStateChanged(new ItemEvent(this, NODE_TOGGLED, selectedNode, selectedNode.isExpanded ? NODE_EXPANDED : NODE_COLLAPSED));
	    	            triggerRedraw();
	    	        }

		            break;
		        }

		        if (!selectedNode.isExpandable())
		        {
		            break;
		        }
		        // else drop thru' to DOWN
		    case KeyEvent.VK_DOWN:
		        if (index < viewCount-1)
		        {
		            index++;
		            setSelectedNode((TreeNode)v.elementAt(index));
		            requestFocus();
		        }
		        break;
		}
	}

	/**
	 * Handles focus gained events.
	 * This function will get called when the component recives a focus gained event.
	 * Override to change the way focus gained is handled.
	 * @param event the FocusEvent
	 * @see #handleFocusLost
	 */
	protected void handleFocusGained(FocusEvent event)
	{
	    hasFocus = true;
	    if (selectedNode != null && v != null)
	        drawNodeText(selectedNode, (v.indexOf(selectedNode) - sbVPosition)*cellSize, true, false);
	}

	/**
	 * Handles focus lost events.
	 * This function will get called when the component recives a focus lost event.
	 * Override to change the way focus lost is handled.
	 * @param event the FocusEvent
	 * @see #handleFocusGained
	 */
	protected void handleFocusLost(FocusEvent event)
	{
	    hasFocus = false;
	    if (selectedNode != null && v != null)
	        drawNodeText(selectedNode, (v.indexOf(selectedNode) - sbVPosition)*cellSize, true, false);
	}

	protected void drawNodeText(TreeNode node, int yPosition, boolean eraseBackground, boolean eraseLines)
	{
	    if (node == null)
	        return;

	    Color fg, bg;
	    int depth=node.depth;
	    Image nodeImage = node.getImage();
	    int textOffset = ((depth + 1) * (cellSize)) + cellSize + textInset - (nodeImage==null ? 12:0);

	    if (node == selectedNode && hasFocus)
	    {
	    	//??RKM?? Temp until these return some good values
			if (symantec.itools.lang.OS.isMacintosh())
				fg = Color.white;
			else
				fg = SystemColor.textHighlightText;

			if (symantec.itools.lang.OS.isMacintosh())
				bg = new Color(0,0,128);
			else
				bg = SystemColor.textHighlight;
	    }
	    else
	    {
	        fg = getForeground();
	        bg = getBackground();
	    }

	    if (eraseBackground)
	    {
	        g1.setColor(bg);
	        g1.fillRect(textOffset-1, yPosition+1, fm.stringWidth(node.text)+4, cellSize-1);
	    }

        if (node == selectedNode)
        {
            g1.setColor(getForeground());
            g1.drawRect(textOffset-1, yPosition+1, fm.stringWidth(node.text)+3, cellSize-2);
            repaint(Math.max(0,textOffset-1-sbHPosition), yPosition+1, fm.stringWidth(node.text)+4, cellSize-1);
        }
        
        if (eraseLines)
        {
            g1.setColor(getBackground());
            g1.drawRect(textOffset-1, yPosition+1, fm.stringWidth(node.text)+3, cellSize-2);
            repaint(Math.max(0,textOffset-1-sbHPosition), yPosition+1, fm.stringWidth(node.text)+4, cellSize-1);
        }
        
	    g1.setColor(fg);
	    g1.drawString(node.text, textOffset, yPosition + cellSize - textBaseLine);
	}

	/**
	 * Sends an action performed event to any action listeners, as needed.
	 */
	protected void sendActionEvent()
	{
		if (actionListener != null)
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, new String(selectedNode.getText())));
	}

    protected int compWidth(Graphics gg)
    {
        int size = 0;
        int textOffset;
        TreeNode node;

   	    Font f = getFont();  //unix version might not provide a default font
		//Make certain there is a font
	    if (f == null)
	    {
	        f = new Font("Serif", Font.PLAIN, 13);
	        if(gg != null)
	       		gg.setFont(f);
	        setFont(f);
	    }

	    if(gg == null)
	    	fm = null;
	    else
	    	fm = gg.getFontMetrics();

	    if(fm == null)
	    	fm = getFontMetrics(f);

		if(fm == null || v == null)
			size = 100;
		else
		{
	        for (int i=0; i < v.size(); i++)
	        {
	            node = (TreeNode)v.elementAt(i);
	       	    textOffset = ((node.depth + 1) * (cellSize)) + cellSize + textInset - (node.getImage()==null ? 12:0);
	       	    if (size < (textOffset+fm.stringWidth(node.text)+6))
	    	        size = textOffset+fm.stringWidth(node.text)+6;
	        }
		}

        return size;
    }

	protected void drawDotLine(int x0, int y0, int x1, int y1)
	{
	   if (y0==y1)
	   {
	        for (int i = x0; i<x1; i+=2)
	        {
	           g1.drawLine(i,y0, i, y1);
	        }
	    }
	    else
	    {
	        for (int i = y0; i<y1; i+=2)
	        {
	            g1.drawLine(x0, i, x1, i);
	        }
	    }
	}

	/**
	 * Handles selecting the given node.
	 * @param node the node to select
	 */
	protected void changeSelection(TreeNode node)
    {
        setSelectedNode(node);
    }
    
	/**
	 * Handles selecting the given node.
	 * @param node the node to select
	 */
	public void setSelectedNode(TreeNode node)
	{
        if(node == null)
        {
            if(selectedNode != null)
            {
	            drawNodeText(selectedNode, (v.indexOf(selectedNode) - sbVPosition) * cellSize, true, true);
	        
    	        if(itemListener != null)
	            {
		    	    itemListener.itemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectedNode, ItemEvent.DESELECTED));
			    }
			
                selectedNode = null;
            }
            
            return;
        }
        
	    if (node == selectedNode)
	        return;

	    TreeNode oldNode = selectedNode;
	    selectedNode = node;
	    	    
	    if(oldNode != null)
	    {
	        drawNodeText(oldNode, (v.indexOf(oldNode) - sbVPosition)*cellSize, true, false);
	    }
	    
	    drawNodeText(node, (v.indexOf(node) - sbVPosition)*cellSize, true, false);
	    
	    // send select event

	    int index = v.indexOf(selectedNode);

		if (itemListener != null)
		{
		    if(oldNode != null)
		    {   
			    itemListener.itemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, oldNode, ItemEvent.DESELECTED));
			}
			
			itemListener.itemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectedNode, ItemEvent.SELECTED));
		}

	    if (index < sbVPosition)
	    { //scroll up
	        sbVPosition--;
	        verticalScrollBar.setValue(sbVPosition);
	        triggerRedraw();
	        return;
	    }

	    if (index >= sbVPosition + (viewHeight-cellSize/2)/cellSize)
	    {
	        sbVPosition++;
	        verticalScrollBar.setValue(sbVPosition);
	        triggerRedraw();
	        return;
	    }

	    repaint();
	}

	/**
	 * Generates a new tree node.
	 * Override if you want to return your own instantiation of a TreeNode subclas.
	 * @param text the node text
	 * @param treeView a reference to the parent TreeView (usually "this").
	 * @return a new TreeNode
	 */
	protected TreeNode newTreeNode(String text, TreeView treeView)
	{
		return new TreeNode(text, treeView);
	}

	protected void parseTreeStructure(String tempStructure[]) throws InvalidTreeNodeException
	{
		for(int i = 0; i < tempStructure.length; i++)
		{
			String entry = tempStructure[i];
			int indentLevel = findLastPreSpace(entry)/*+1*/;

			if (indentLevel == -1)
				throw new InvalidTreeNodeException("parseTreeStructure: " + errors.getString("EmptyStrings"));

			TreeNode node = newTreeNode(entry.trim(), this);
			node.setDepth(indentLevel);

			if (rootNode == null)
			{
				if (indentLevel != 0)
					throw new InvalidTreeNodeException("parseTreeStructure: " + errors.getString("NoRootLevelNode"));

				append(node);
			}
			else
			{
				TreeNode currentNode = rootNode;
                while(currentNode.sibling != null)
    				currentNode = currentNode.sibling;

				for(int j = 1; j < indentLevel; j++)
				{
					int numberOfChildren = currentNode.numberOfChildren;
					TreeNode tempNode = null;

					if (numberOfChildren > 0)
					{
						tempNode = currentNode.child;

						while(tempNode.sibling != null)
							tempNode = tempNode.sibling;
					}

					if (tempNode != null)
						currentNode = tempNode;
					else
						break;
				}

				int diff = indentLevel - currentNode.getDepth();

				if (diff > 1)
					throw new InvalidTreeNodeException("parseTreeStructure: " + errors.getString("NoParent") + entry.trim());

				if (diff == 1)
					insert(node, currentNode, CHILD);
				else
					insert(node, currentNode, NEXT);
			}
		}
	}

	protected void recount()
	{
	    count = 0;
	    e = new Vector();

	    if (rootNode != null)
	    {
	        rootNode.depth=0;
	        traverse(rootNode);
	    }
	}

	protected void traverse(TreeNode node)
	{
	    count++;
	    
	    if(!node.isHidden())
	    {
	        e.addElement(node);
	        
    	    if (node.child != null)
	        {
	            node.child.depth = node.depth+1;
	            traverse(node.child);
	        }	    
        }
        
	    if (node.sibling != null)
	    {
	        node.sibling.depth = node.depth;
	        traverse(node.sibling);
	    }
	}

	protected void resetVector()
	{
		// Traverses tree to put nodes into vector v
		// for internal processing. Depths of nodes are set,
		// and viewCount and viewWidest is set.
		v = new Vector(count);
		viewWidest = 30;

		if (count < 1)
		{
			viewCount = 0;
			return;
		}

		rootNode.depth = 0;
		vectorize(rootNode, true, true, v);
		viewCount = v.size();

		needResetVector = false;
	}

	protected void vectorize(TreeNode node, boolean respectExpanded, boolean repectHidden, Vector nodeVector)
	{
	    if(node == null)
	    {
	        return;
        }
        
        if(!repectHidden || !node.isHidden())
        {            
    	    nodeVector.addElement(node);

	        if((!respectExpanded && node.child != null) || node.isExpanded())
	        {
    	        node.child.depth = node.depth + 1;
	            vectorize(node.child, respectExpanded, repectHidden, nodeVector);
	        }
        }
        
	    if (node.sibling != null)
	    {
	        node.sibling.depth = node.depth;
	        vectorize(node.sibling, respectExpanded, repectHidden, nodeVector);
	    }
	}

	protected void debugVector()
	{
	    int vSize = v.size();

	    for (int i=0; i<count; i++)
	    {
	        TreeNode node = (TreeNode) v.elementAt(i);
	        System.out.println(node.text);
	    }
	}

	protected int findLastPreSpace(String s)
	{
		if(s != null && s.length() > 0)
		{
		    int length;

		    length = s.length();

		    if(s.charAt(0) != ' ' && s.charAt(0) != '\t')
		    {
		        return 0;
		    }

		    for(int i = 1; i < length; i++)
		    {
		        if(s.charAt(i) != ' ' && s.charAt(i) != '\t')
		        {
		            return i;
		        }
		    }
		}
	    return -1;
	}


	int		sbVPosition				= 0;	// hold value of vertical scrollbar
	int		sbVWidth;    			   		// width of vertical scrollbar
	int		sbHPosition				= 0;	// hold value of horizontal scrollbar
	int		sbHHeight				= 0;	// height of horizontal scrollbar
	long	sbVTimer				= -1;	// time of last vert scrollbar event
	int		cellSize   				= 16;   // size of node image
	int		clickSize   			= 8;    // size of mouse toggle (plus or minus)
	int		imageInset				= 3;    // left margin of node image
	int		textInset   			= 6;    // left margin for text
	int		textBaseLine			= 3;    // position of font baseline from bottom of cell
	int		doubleClickResolution	= 333;	// double-click speed in milliseconds

	/**
	 * root node of tree
	 */
	protected TreeNode rootNode;
	/**
	 * highlighted node
	 */
	protected TreeNode selectedNode;
	/**
	 * first node in window
	 */
	protected TreeNode topVisibleNode;
	/**
	 * The vertical scrollbar.
	 */
	protected Scrollbar	verticalScrollBar;
	/**
	 * show or hide vertical scrollbar
	 */
	protected boolean sbVShow = false;
	/**
	 * Number of nodes in the tree.
	 */
	protected int count = 0;
	/**
	 * Number of viewable nodes in the tree.
	 * A node is viewable if all of its parents are expanded.
	 */
	protected int	viewCount = 0;
	/**
	 * The horizontal scrollbar
	 */
    protected Scrollbar horizontalScrollBar;
	/**
	 * size of horizontal scrollbar
	 */
    protected int sbHSize;
	/**
	 * for horizontal scrollbar
	 */
    protected int newWidth = 0;
	/**
	 * show or hide horizontal scrollbar
	 */
   	protected boolean sbHShow = false;
   	/**
   	 * Keeps track of if drawTree needs to call resetVector or not.
   	 */
   	protected boolean needResetVector;

	protected int sbHLineIncrement = 4;
   	/**
   	 * pixel size of tree display
   	 */
	protected int viewHeight = 300;
   	/**
   	 * pixel size of tree display
   	 */
	protected int viewWidth  = 300;
   	/**
   	 * widest item displayable (for horz scroll)
   	 */
	protected int viewWidest = 0 ;
    /**
     * The this component's key event listener.
     */
	protected Key key = null;
    /**
     * The this component's mouse event listener.
     */
	protected Mouse mouse = null;
    /**
     * The this component's adjustment event listener.
     */
	protected Adjustment adjustment = null;
    /**
     * The action listener to keep track of listeners for our action event.
     */
	protected ActionListener actionListener = null;
    /**
     * The action listener to keep track of listeners for our item event.
     */
    protected ItemListener itemListener = null;
    /**
     * The this component's focus event listener.
     */
    protected Focus focus = null;
	/**
	 * v is vector of viewable nodes
	 */
	protected Vector v;
	/**
	 * e is vector of existing nodes
	 */
	protected Vector e;
    /**
     * Flag indicating repaint() shoud redraw the entire image even
     * if the component size has not changed.
     */
	transient protected boolean redrawTriggered = false;
	transient boolean hasFocus = false;
   	/**
   	 * current font metrics
   	 */
	transient protected FontMetrics fm;
   	/**
   	 * save time of last mouse down (for double click)
   	 */
	transient long timeMouseDown;
	/**
	 * Offscreen Image used for buffering the painting process.
	 */
	transient protected Image im1;
	/**
	 * Offscreen graphics context used for buffering the painting process.
	 */
	transient protected Graphics g1 = null;
	/**
	 * Checks for scrollbars that have different max value.
	 */
    static protected boolean isSun1_1;

    transient protected ResourceBundle errors;

	static
	{
		//Calc it once
		String vendor = System.getProperty("java.vendor");
		String version = System.getProperty("java.version");

		/*isSun1_1 = ((vendor.startsWith("Sun Microsystems Inc.") ||
		            (vendor.startsWith("Apple"))                ||
		            (vendor.startsWith("Symantec Corporation")) ||
		            (vendor.startsWith("Netscape"))) &&
  				   ((version.startsWith("11")) ||
				    (version.startsWith("1.1"))));*/
		isSun1_1 = true;
	}
}

class InvalidTreeNodeException extends Exception
{
	public InvalidTreeNodeException()
	{
		super();
	}

	public InvalidTreeNodeException(String message)
	{
		super(message);
	}
}
