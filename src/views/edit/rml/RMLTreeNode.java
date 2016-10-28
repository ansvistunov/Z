package views.edit.rml;

import javax.swing.text.Position;

import org.fife.rsta.ac.SourceTreeNode;

import rml.Proper;

public class RMLTreeNode extends SourceTreeNode {

	private String mainAttr;
	private Position offset;
	private Position endOffset;
	private Proper prop;


	/**
	 * @return the prop
	 */
	public Proper getProper() {
		return prop;
	}


	/**
	 * @param prop the prop to set
	 */
	public void setProper(Proper prop) {
		this.prop = prop;
	}


	public RMLTreeNode(String name) {
		super(name);
	}


	public boolean containsOffset(int offs) {
		return offset!=null && endOffset!=null &&
				offs>=offset.getOffset() && offs<=endOffset.getOffset();
	}


	public String getElement() {
		return (String)getUserObject();
	}


	public int getEndOffset() {
		return endOffset!=null ? endOffset.getOffset() : Integer.MAX_VALUE;
	}


	public String getMainAttr() {
		return mainAttr;
	}


	public int getStartOffset() {
		return offset!=null ? offset.getOffset() : -1;
	}


	public void setEndOffset(Position pos) {
		this.endOffset = pos;
	}


	public void setMainAttribute(String attr) {
		this.mainAttr = attr;
	}


	public void setStartOffset(Position pos) {
		this.offset = pos;
	}


	/**
	 * Returns a string representation of this tree node.
	 *
	 * @return A string representation of this tree node.
	 */
	@Override
	public String toString() {
		String text = getElement();
		if (mainAttr!=null) {
			text += " " + mainAttr;
		}
		return text;
	}


}
