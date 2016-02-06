package document;
import java.awt.*;

public class StatusPanel extends Panel{
	java.awt.Label info;	
	static Color background = Color.lightGray;
	static int height = 12;
	String text = "";
	public StatusPanel(){
		super();
		setLayout(null);
		setBackground(background);		
		setFont(new Font("Sans Serif",0,11));		
	}
	
	/*public void addNotify() {
		super.addNotify();
		Dimension d = getSize();
		info.setBounds(1,1,d.width-2,d.height-2);
	}*/
	
	public void setText(String text) {
		this.text = text;
		paint(getGraphics());
	}
	
	public void paint(Graphics g) {
		Dimension d = getSize();
		g.setColor(Color.lightGray);
		g.fillRect(0,0,d.width,d.height);
		g.setColor(Color.gray);
		g.drawRect(0,0,d.width-1,d.height-1);
		g.setFont(getFont());
		g.setColor(Color.black);
		g.drawString(text,2,height-2);
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(250,height);
	}
}

