
package views;

import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class FButton extends Component {
	String label;                      // The Button's text  
	boolean enabled;  
	boolean pressed = false;

	public FButton() {
		this(null);
	}

	public FButton(String label) {      
		this.label = label;      
		setFont(new Font("Times",0,12));

	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
		invalidate();
		repaint();
	}	
	public void update(Graphics g) {
		paint(g);
	}



	public void paint(Graphics g) {
		//System.out.println("inside button's paint "+this);
		int width = getSize().width - 1;
		int height = getSize().height - 1;
		Rectangle clip = g.getClipBounds();		
		Color interior;
		Color highlight1;
		Color highlight2;

		interior = getBackground();

		// ***** determine what colors to use
		if(pressed) {
			highlight1 = interior.darker();
			highlight2 = interior.brighter();
		} else {
			highlight1 = interior.brighter();
			highlight2 = interior.darker();
		}

		// ***** paint the interior of the button
		g.setColor(interior);

		g.fillRect(0, 0, width, height);

		// ***** highlight the perimeter of the button
		// draw upper and lower highlight lines
		g.setColor(highlight1);
		g.drawLine(0, 0, width, 0);
		g.drawLine(0,0,0,height);
		g.setColor(highlight2);
		g.drawLine(0, height, width, height);
		g.drawLine(width,0,width,height);

		// ***** draw the label centered in the button
		Font f = getFont();
		if(f != null) {			g.setClip(new Rectangle(1,1,width,height));
			FontMetrics fm = getFontMetrics(getFont());
			g.setColor(getForeground());
			g.setFont(f);
			g.drawString(label,
						 width/2 - fm.stringWidth(label)/2,
						 height/2 + fm.getHeight()/2 - fm.getMaxDescent()
						 );			g.setClip(clip);
		}		
	}


	public Dimension getPreferredSize() {
		Font f = getFont();
		if(f != null) {
			FontMetrics fm = getFontMetrics(getFont());
			return new Dimension(fm.stringWidth(label) + 10,
								 fm.getHeight() + 10);
		} else {
			return new Dimension(100, 50);
		}
	}


	public Dimension getMinimumSize() {
		return new Dimension(100, 50);
	}

}
