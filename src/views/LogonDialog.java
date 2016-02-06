
package views;

import java.awt.*;
import java.awt.event.*;


public class LogonDialog extends Dialog {

	static String userLabel="Имя пользователя";
	static String passLabel="Пароль";
	public int result = 2;//cancel;
	private TextField user = new TextField();		
	private TextField pass = new TextField();
	Button ok = new Button("OK");
	Button cancel = new Button("Отмена");
	public LogonDialog(Frame parent,String title) {
		super(parent, title, true);		
		setLayout(null);
		int th=25;		
		pass.setEchoCharacter('*');
		java.awt.Label userl = new java.awt.Label(userLabel,java.awt.Label.LEFT);
		java.awt.Label passl = new java.awt.Label(passLabel,java.awt.Label.LEFT);		
		this.addWindowListener(new WL());
		ok.addActionListener(new AL());		
		cancel.addActionListener(new AL());

		
		int lWidth=125;
		int lHeight=25;
		float c = 1.2f;
		int bpHeight = 35;
		int gap=10;

        Panel p1 = new Panel();p1.setLayout(null);
		Panel p2 = new Panel();p2.setLayout(new FlowLayout());
		int fWidth = (int)((float)lWidth*c);		
		int width = 2*gap + lWidth + fWidth;
		int height = 2*gap + 2*lHeight + bpHeight;
		
		user.setBounds(gap+lWidth, gap,fWidth,lHeight);
		pass.setBounds(gap+lWidth, 2*gap+lHeight,fWidth,lHeight);
		userl.setBounds(gap, gap,lWidth,lHeight);
		passl.setBounds(gap, 2*gap+lHeight,lWidth,lHeight);
		

		p1.setBounds(0,th,width, height-bpHeight);
		p2.setBounds(0,th+height-bpHeight,width,bpHeight);
		p1.add(userl);
		p1.add(user);
		p1.add(passl);
		p1.add(pass);
		p2.add(ok);
		p2.add(cancel);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setBounds((d.width-width)/2,(d.height-height-th)/2,width, height+th);			
		this.add(p1);
		this.add(p2);	
		//System.out.println("insets="+getInsets());
		
	}
	public String getUser(){
		return user.getText();
	}

	public String getPassword(){
		return pass.getText();
	}
	
	class AL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(ok)){
				result = 1;
				//dispose();
				hide();
			}else
			if (e.getSource().equals(cancel)){
				result = 2;
				//dispose();
				hide();
			}
		}
	}
	class WL extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			result = 2;
			//dispose();
			hide();
		}
	}
}
