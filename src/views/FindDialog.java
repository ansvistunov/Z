
package views;

import java.awt.*;
import java.awt.event.*;
import loader.*;

public class FindDialog extends Dialog {    
    Checkbox casebox;
    Checkbox down;
    Checkbox up;
    CheckboxGroup direction;
    TextField pattern;
    Button find;
    Button close;
    Grid parent;
    
    private int counter;
    
    private String title = null;
    public boolean find_pressed;
    public boolean go_down = true;
    public boolean caze = false;
    public String text;
    public FindDialog(Grid parent, String title, int width, int height, boolean modal) {        
        super(new Frame(),title,modal);        
        this.parent = parent;       
        Panel p1,p2,p3,p21,p22,p211,p212,p213,p221,p222,p223;
        setSize(width, height);
        addWindowListener(new WL());
        setResizable(false);
        direction = new CheckboxGroup();
        // setLayout(new GridLayout(1,1));
        setLayout(new BorderLayout());
        p1 =new Panel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        p1.add(new java.awt.Label(StringBundle.FindDialog_Pattern));
        pattern = new TextField(30);
        p1.add(pattern);
        
        p2 = new Panel();
        p2.setLayout(new GridLayout(1,2));
        p21 = new Panel();
        p21.setLayout(new GridLayout(3,1));
        //p21.add(new Label(FindDialog_Direction));
        p211 = new Panel();
        p211.setLayout(new FlowLayout(FlowLayout.LEFT));
        p211.add(new java.awt.Label(StringBundle.FindDialog_Direction));
        p212 = new Panel();
        p212.setLayout(new FlowLayout(FlowLayout.LEFT));
        down = new Checkbox(StringBundle.FindDialog_Direction_Down,direction,true);
        down.addItemListener(new IL());
        p212.add(down);
        p213 = new Panel();
        p213.setLayout(new FlowLayout(FlowLayout.LEFT));
        up = new Checkbox(StringBundle.FindDialog_Direction_Up,direction,false);
        up.addItemListener(new IL());
        p213.add(up);
        p21.add(p211);        
        p21.add(p212);
        p21.add(p213);        
        p22 = new Panel();
        p22.setLayout(new GridLayout(3,1));
        p221 = new Panel();
        p222 = new Panel();
        p223 = new Panel();
        p223.setLayout(new FlowLayout(FlowLayout.LEFT));
        //casebox = new Checkbox(StringBundle.FindDialog_Case,true);
        casebox = new Checkbox(StringBundle.FindDialog_Case,caze);
        casebox.addItemListener(new IL());
        p223.add(casebox);
        p22.add(p221);
        p22.add(p222);
        p22.add(p223);
        p2.add(p21);
        p2.add(p22);
        
        p3 = new Panel();
        find = new Button(StringBundle.FindDialog_Button_Find);
        find.addActionListener(new BL());        
        close = new Button(StringBundle.FindDialog_Button_Close);
        close.addActionListener(new BL());
        p3.add(find);
        p3.add(close);
        
        
        add(p1,"North");
        add(p2,"Center");
        add(p3,"South");
    }
    
    public void show() {
        //Данный поток устанавливает заголовок окна
        new Thread(new TitleSetter()).start();
        super.show();
	pattern.requestFocus();
    }
    
    class WL extends WindowAdapter {
         public void windowClosing(WindowEvent e) {
            find_pressed = false;
            //FindDialog.this.hide();
	    dispose();
            if (parent!=null) {
                parent.redrawTitleBar=true;
                parent.emptyButton.requestFocus();
            }
         }    
    }
    
    class BL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(find)) {
                find_pressed = true;
                text = GLOBAL.b2c(pattern.getText(),GLOBAL.FIELD);
                //hide();
		dispose();
                if (parent!=null) {
                    parent.redrawTitleBar=true;
                    parent.emptyButton.requestFocus();
                }
                return;
            }
            if (e.getSource().equals(close)) {
                find_pressed = false;
                //hide();
		dispose();
                if (parent!=null) {
                    parent.redrawTitleBar=true;
                    parent.emptyButton.requestFocus();
                }
                return;
            }
        }
    }
    
    class IL implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getSource().equals(down)) {
                if  (((Checkbox)e.getSource()).getState()) go_down=true;
                else go_down=false;
                return;
            }
            if (e.getSource().equals(up)) {
                if  (((Checkbox)e.getSource()).getState()) go_down=false;
                else go_down=true;
                return;
            }
            if (e.getSource().equals(casebox)) {
                if  (((Checkbox)e.getSource()).getState()) caze=true;
                else caze=false;
                return;
            }
        }
    }
    
    class TitleSetter implements Runnable {
        public void run() {            
            for (int i=0;i<1;i++) {                
                FindDialog.this.setTitle(FindDialog.this.getTitle());
            }
        }
    }
    
    /*public static void main(String[] args) {
        FindDialog fd = new FindDialog("Поиск",400,180,true);
        fd.show();
    }*/
}
