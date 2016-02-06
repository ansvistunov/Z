

package dbi;

import java.awt.*;
import java.sql.*;

public class Msg extends Dialog{
	
		Panel p = new Panel();
		Button ButOk = new Button("Ok");
		Button ButCancel = new Button("Cancel");
		boolean ok=false;
		boolean upError = false;
		boolean conError=false;
		UpdateException upEx = null;
		ConnectException conEx = null;
	

	public Msg(Exception e,Frame fr){
	    super(fr,"",true);
	    if (e instanceof UpdateException) {
	        upError = true;
	        upEx = (UpdateException)e;
	    }
	    if (e instanceof ConnectException) {
	        conError = true;
	        conEx = (ConnectException)e;
	        }
	    
		
		setResizable(false);
		setSize(400,300);
   		setLayout(new BorderLayout());
		Panel p = new Panel();
		add(p);
		Label lb = null;
		if (upError) {
		    lb = new Label("Ошибка в строке "+upEx.getBadKey());
		    }
		if (conError){
		    lb = new Label("Ошибка соединения ");
		    }
		    //if(lb!=null) System.out.println("label!=null1");
		p.setLayout(new BorderLayout());
		p.add("North",lb);
		TextArea ta = new TextArea();
		String message;
		if (upError) {
		    try{
		        ResultSet rs = DATASTORE.executeQuery("Select msg from msg where id="+upEx.getErrorCode());
		        rs.next();
		        message = rs.getString("msg");
		        rs.close();
		        //rs = DATASTORE.executeQuery("begin  msg_lib.print_msg; end");
		        //rs.next();
		        //message = message+rs.getString(0);
		        //rs.close();
		    }catch(SQLException e2){
		        System.out.println(e2+" error code:"+e2.getErrorCode());
		        message = upEx.getMessage();
		        }
		    //ta.setText(upEx.getMessage()+"\n"+"error code:"+upEx.getErrorCode()+"\n"+upEx.getSQLState());
		    ta.setText(message);
		    
		    }
		else ta.setText(e.getMessage());
		ta.setEditable(false);
		
		p.add("Center",ta);
		Panel bp = new Panel();
		p.add("South",bp);
		bp.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		bp.add(ButOk);
		bp.add(ButCancel);

		try{
			show();
		}catch(Exception e1){
			e1.printStackTrace();
		}
		
		if (ok) dispose();
		
	}
	public boolean handleEvent(Event ev){
		if (ev.id == Event.ACTION_EVENT){
			if (ev.target == ButOk) {
				ok = true;
			}else if (ev.target == ButCancel) {
				ok = false;
			}else return super.handleEvent(ev);
			hide();
			return true;
		}else return super.handleEvent(ev);
	}
}


