
package body;
import java.util.*;
import dbi.*;
import loader.GLOBAL;

public class NavigatorController {
    protected Vector workspaces = new Vector();
    public NavigatorController(){
        newWorkspace();
    }

    //public void addWorkspace(Nafigator ws) {
    //}

    public void newWorkspace() {
        Nafigator nw = null;
        try {
            nw = new Nafigator(this);
        }catch(Exception e) {
            System.out.println("body.NavigatorController.newWorkspace() : can't create new Workspace");
            e.printStackTrace();
            return;
        }
        workspaces.addElement(nw);
    }

    public void removeWorkspace(Nafigator ws){
        if (ws==null) return;
        //ws.hide();
        if (workspaces.size()==1) {
            exit();
        }
        else {
            workspaces.removeElement(ws);
            ws.dispose();
			//System.out.println("Free Memory="+Runtime.getRuntime().freeMemory()); 
            System.gc();
			//System.out.println("Free Memory="+Runtime.getRuntime().freeMemory());
        }
    }
	public void changeLogon(){
		String user=null;
		String pass=null;
		boolean connect = false;
		boolean connectDestroyed = false;
		views.LogonDialog ld = new views.LogonDialog(GLOBAL.nafigator,"Подключиться к Системе");
		while(!connect){
			ld.show();
			if ((ld.result!=1) && !connectDestroyed) {
				ld.dispose();
				return;
			}
			if ((ld.result!=1) && connectDestroyed) {
				continue;
			}
                        try{    String[] foo = {ld.getUser(),ld.getPassword(),GLOBAL.pr(GLOBAL.usrSchema)};
                                DATASTORE.er.closeConnect();
				DATASTORE.initConnect( ""+GLOBAL.pr(GLOBAL.DBS_PROTO)+GLOBAL.pr(GLOBAL.DBS_HOST),
                                        ld.getUser(),ld.getPassword());
				System.out.println("after init Connect");
				connect=true;
			}catch(BadPasswordException e){
		   		GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_BADUSERORPASSWORD),true);
				connectDestroyed = true;
			}catch(ConnectException e){
				GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_CANTCONNECTTODBS),true);
				connectDestroyed = true;
			}catch(Exception e){
				GLOBAL.messag(GLOBAL.pr(GLOBAL.MSG_UNKNOWNERROR),true);
				connectDestroyed = true;
			}
		}
		ld.dispose();
		ld =  null;		
		for (int i=0;i<workspaces.size();i++) {
			Nafigator f = (Nafigator)workspaces.elementAt(i);
			f.dispose();
		}
		workspaces.removeAllElements();
		newWorkspace();
	}

    public void workspaceActivated(Nafigator ws) {
        //System.out.println("Active workspace is "+ws);
        GLOBAL.nafigator = ws;
    }

    void exit(){
        if(!GLOBAL.sure(GLOBAL.pr(GLOBAL.MSG_RUSUREEXIT,
										  "Exit ?!, Are You Sure?"),false)){
		    return;
		}
        else {
          /*while (ErrorReader.initializing)
              GLOBAL.sure(GLOBAL.pr(GLOBAL.MSG_RUSUREEXIT,
                                       "Exit ?!, Are You Sure?"),false); */
          DATASTORE.er.closeConnect();
          System.exit(0);
        }
    }
}
