package body;
import java.awt.*;
import java.awt.event.*;

public class AboutDialog extends Dialog {
	static int width = 350;
	static int height = 315;
	Button ok = new Button("OK");
        Label l = new Label("© Almanex Technology 1998-2001");
	public AboutDialog(String title,Frame parent) {
		super(parent,title,true);		
		Toolkit t = Toolkit.getDefaultToolkit();
//                Image i = t.getImage("./about.jpg");
                Image i = t.getImage("./zeta.gif");
		canvas can = new canvas(i);
		int sw = t.getScreenSize().width;
		int sh = t.getScreenSize().height;
		setBounds((sw-width)/2,(sh-height)/2,width,height);
		setLayout(new BorderLayout());
		ok.addActionListener(new AL());
		Panel p0 = new Panel();
		p0.setLayout(new BorderLayout());
		p0.add("South",l);
		p0.add("Center",can);
		Panel p = new Panel();
		p.add(ok);
		add("South",p);
		add("Center",p0);
		addWindowListener(new WL());		
	}
	
	class canvas extends Canvas {
		Image i=null;
		public canvas(Image i) {
			this.i=i;
		}
		public void paint(Graphics g){
                        g.drawImage(i,0,0,getSize().width, getSize().height,
                                        this);
			System.out.println("paint called");
		}
		public boolean imageUpdate(Image img,int flags,int x,int y,int w,int h){
			boolean loading = (flags & (ALLBITS|ABORT)) == 0;
			if (!loading) repaint();
			return loading;
		}
	}
	
	class AL implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(ok)) {
				dispose();
			}
		}
	}
	
	class WL extends WindowAdapter {
		public void windowClosing(WindowEvent e){
			dispose();
		}
	}
}
