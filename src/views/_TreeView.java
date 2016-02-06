package views;

import java.awt.*;
import java.awt.event.*;
import symantec.itools.awt.*;

public class _TreeView extends symantec.itools.awt.TreeView{
	TreeView2 parent;
	public _TreeView(TreeView2 tv){
		super();
		parent = tv;
		addItemListener(new IL());
		isSun1_1 = true;
	}
	
	public _TreeView(TreeNode node){
		super(node);
		addItemListener(new IL());
		isSun1_1 = true;		
	}
	
	int getCurrentLevel(){		
		TreeNode node = getSelectedNode();
		if (node==null) return 0;
		int l=0;
		while ((node = node.getParent())!=null) ++l;
		return l;
	}
	
	//возвращает true если аргумент-лист дерева
	boolean isList(TreeNode node){
		if (node.getChild() == null) return true;
		else return false;
	}
	
	public void expandAll(){
		_expandNode(getRootNode());
	}
	
	protected void _expandNode(TreeNode n){
		if (n==null) return;
		n.expand();
		_expandNode(n.getChild());
		_expandNode(n.getSibling());
	}
	
	public void handleMousePressed(MouseEvent event){
		if ((event.getModifiers()&MouseEvent.BUTTON3_MASK)!=0) {
			parent.rightClickReaction(event);
			return;
		}
		super.handleMousePressed(event);
		//Обработка щелчка мышью с CTRL на УЗЛЕ дерева
		TreeNode node = getSelectedNode();
		if (node==null) return;
		if (isList(node)){
			if (event.getClickCount()==2) {
				parent.listAction2();
				return;
			}
		}else
		if ((event.getModifiers()&event.CTRL_MASK)!=0){
			parent.nodeAction();
		}
	}
	
	class IL implements ItemListener {
		public void itemStateChanged(ItemEvent e){
			TreeNode n = (TreeNode)e.getItem();
			dbi.Group g = (dbi.Group)n.getDataObject();
			if (g!=null) parent.setCurrentRow(g.begrow);
			if ((e.getStateChange() == ItemEvent.SELECTED)
				&& isList(n)){
				parent.listAction();
			}
		}
	}
}
