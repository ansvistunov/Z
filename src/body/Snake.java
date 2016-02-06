
/*
 * File: Snake.java
 *
 * Created: Thu Mar 18 16:35:45 1999
 *
 * Copyright(c) by Alexey Chen
 */

package body;

import java.awt.*;
import document.*;
import dbi.*;
import loader.*;
import java.util.*;




public class Snake implements LoadableClass , Runnable {

	void main(){
		try{
/*			Document d = new Document();
			d.loadDocument(GLOBAL.pr(GLOBAL.DOC_START,"doc.start"),
						   null,GLOBAL.loader);
			Frame f = new Frame(GLOBAL.c2b(
				GLOBAL.pr(GLOBAL.TITLE_MAINWINDOW,
						  "MainWindow"),GLOBAL.FTITLE));
			f.setSize(400,400);
			Dimension dm = f.getToolkit().getScreenSize();
			f.move((dm.width-400)/2,(dm.height-400)/2);
			f.setLayout(new GridLayout(1,1));
			f.add(d.getPanel());
			f.show();
*/
			//new Nafigator();
			new NavigatorController();
		}catch(Exception e){
			System.out.println(GLOBAL.PRINT_RED+
							   "~body.Snake: \n\t"+
							   GLOBAL.PRINT_NORMAL+e);
			e.printStackTrace();
			System.exit(0);
		}
	}

	public boolean connect(String user,String pass){
		if(GLOBAL.pr(GLOBAL.DBS_REALLYCONNECTED,"YES").toUpperCase().compareTo("NO")==0)
			return true;
                try{    String[] foo = {user,pass,GLOBAL.pr(GLOBAL.usrSchema)};
			DATASTORE.initConnect(
                                ""+GLOBAL.pr(GLOBAL.DBS_PROTO)+ GLOBAL.pr(GLOBAL.DBS_HOST),
                                user,pass);
		}catch(BadPasswordException e){
			e.printStackTrace();
			//System.out.println("msg.BadUserOrPassword");
		    GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_BADUSERORPASSWORD),true);
			return false;
		}catch(ConnectException e){
			e.printStackTrace();
			GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_CANTCONNECTTODBS),true);
			return false;
		}catch(Exception e){
			e.printStackTrace();
			GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_UNKNOWNERROR),true);
			return false;
		}
		return true;
	}

	public void run(){
		main();
	}
}


