
package views;
import java.awt.*;

public class PrintProgress extends Dialog {
    java.awt.Label stran = new java.awt.Label("Страница");
    public java.awt.Label curpage = new java.awt.Label("");
    java.awt.Label iz = new java.awt.Label("из");
    public java.awt.Label countpages = new java.awt.Label("");
    public java.awt.Label copy = new java.awt.Label("()");
    Button ok = new Button("OK");
    public PrintProgress(Frame frame, String title){
        super(frame, title);
        setModal(false);
        int w = 300;
        int h = 100;
        //FontMetrics fm = stran.getFontMetrics(stran.getFont());
        int fh = 20;//fm.getHeight();
        Dimension ss = getToolkit().getScreenSize();
        setBounds((ss.width-w)/2,(ss.height-h)/2,w,h);
        setLayout(null);
        setBackground(Color.lightGray);
        stran.setBounds(20,(h-fh)/2,60,20);
        curpage.setBounds(80,(h-fh)/2,30,20);
        iz.setBounds(110,(h-fh)/2,20,20);
        countpages.setBounds(130,(h-fh)/2,30,20);
        copy.setBounds(160,(h-fh)/2,40,20);
        ok.setBounds(75,150,50,30);
        add(stran);
        add(curpage);
        add(iz);
        add(countpages);
        add(copy);        
        add(ok);
    }
}
