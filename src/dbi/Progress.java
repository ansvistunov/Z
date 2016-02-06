package dbi;
import java.awt.*;
public class Progress extends Frame{
    Label lb;
    public Progress(){
	    super("");
	    setResizable(false);
		setSize(200,100);
   		setLayout(new BorderLayout());
		Panel p = new Panel();
		p.setLayout(new BorderLayout());
		lb = new Label("");
		p.add("North",lb);

		add(p);
		hide();
	}
    public void setprogress(int rows){
        String text = rows+":";
        lb.setText(text);
        }

    }