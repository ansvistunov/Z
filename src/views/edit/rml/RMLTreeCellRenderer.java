package views.edit.rml;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.tree.DefaultTreeCellRenderer;


import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;

public class RMLTreeCellRenderer extends DefaultTreeCellRenderer {

	private Icon elemIcon;
	private String elem;
	private String attr;
	private boolean selected;

	private static final RMLTreeCellUI UI = new RMLTreeCellUI();
	private static final Color ATTR_COLOR = new Color(0x808080);


	public RMLTreeCellRenderer() {
		URL url = getClass().getResource("tag.png");
		if (url!=null) { // Always true
			elemIcon = new ImageIcon(url);
		}
		setUI(UI);
	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean focused) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded,
											leaf, row, focused);
		this.selected = sel;
		if (value instanceof RMLTreeNode) {
			// Don't modify setText() since it determines width of tree node.
			RMLTreeNode node = (RMLTreeNode)value;
			elem = node.getElement();
			attr = node.getMainAttr();
		}
		else {
			elem = attr = null;
		}
		setIcon(elemIcon);
		return this;
	}


	@Override
	public void updateUI() {
		// We must call super.updateUI() since, as of Java 7, that's where
		// DefaultTreeCellRenderer caches its fonts, colors, etc.
		super.updateUI(); // Get "real" new defaults
		setUI(UI); // Doesn't update colors, border, etc., just paint method
	}


	/**
	 * Custom UI for our renderer.  This is basically a performance hack to
	 * avoid using HTML for our rendering.  Swing's HTML rendering engine is
	 * very slow, making tree views many thousands of nodes large using HTML
	 * very slow for expand operations (our expandInitialNodes() method).  This
	 * is caused by calls to get the preferred size of each HTML view.  A
	 * "plain text" renderer that can paint the different colors itself is
	 * much faster (~ 4x faster), but still doesn't eliminate the issue for
	 * huge trees.
	 */
	private static class RMLTreeCellUI extends BasicLabelUI {

		@Override
		protected void installDefaults(JLabel label) {
			// Do nothing
		}

		@Override
		protected void paintEnabledText(JLabel l, Graphics g, String s, 
				int textX, int textY) {
			RMLTreeCellRenderer r = (RMLTreeCellRenderer)l;
			Graphics2D g2d = (Graphics2D)g;
			Map<?,?> hints = RSyntaxUtilities.getDesktopAntiAliasHints();
			if (hints!=null) {
				g2d.addRenderingHints(hints);
			}
			g2d.setColor(l.getForeground());
			g2d.drawString(r.elem, textX, textY);
			if (r.attr!=null) {
				textX += g2d.getFontMetrics().stringWidth(r.elem + " ");
				if (!r.selected) {
					g2d.setColor(ATTR_COLOR);
				}
				g2d.drawString(r.attr, textX, textY);
			}
			g2d.dispose();
		}

		@Override
		protected void uninstallDefaults(JLabel label) {
			// Do nothing
		}

	}

   
}
