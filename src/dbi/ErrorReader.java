package dbi;
import java.sql.*;
import loader.*;
import oracle.jdbc.pool.OracleDataSource;

import java.awt.*;
public class ErrorReader extends Thread{
    public ErrorReader()throws BadPasswordException,ConnectException{
		circle = true;
        initConnect(DATASTORE.connStr, DATASTORE.user,DATASTORE.passwd);
        
            //fr = new MsgDebug("Debug Information");
            fr.setSize(300,300);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            fr.setLocation((d.width - 300) / 2, (d.height - 300) / 2);
            fr.hide();
            
        }
    public static Connection conn;
    
    static Statement stmt;
    static MsgDebug fr = new MsgDebug("Errors Information");
    boolean debug = true;
    static CallableStatement cs = null;
    static String pipe = "";
    static boolean success = false;
    static boolean fullreset = false;

    public boolean circle = true;
    public static volatile boolean initializing = false;

    public static void closePipe() throws SQLException{
        System.out.println("Closing pipe...");
		//Thread.currentThread().stop();
		//circle = false;
        //if (stmt!=null) executeQuery("begin deb_lib.close(); end;");
		//System.out.println("PIPE IS CLOSED");
        //if (stmt!=null) stmt.close();
        //if (conn!=null) conn.close();
        
        };
	
    public static void initPipe() {
        try{
            initializing = true;
            if (fullreset){
                addMessage("doing full reset....");
                stmt.close();
                conn.close();
                initConnect(DATASTORE.connStr,DATASTORE.user,DATASTORE.passwd);
                }

            try {
              cs = conn.prepareCall("begin select count(*) into :1 from v$db_pipes where name='ZETA$'; end;");
              cs.registerOutParameter(1,java.sql.Types.INTEGER);
              cs.executeUpdate();
              int n = cs.getInt(1);
              if (n > 0)
                Thread.sleep(15000);
              
              executeQuery("begin deb_lib.init('zeta$'); end;");
//              System.out.println("sending presence");
              executeQuery("begin deb_lib.write('zeta$presence', user, 5); end;");
              try {
                cs = conn.prepareCall("begin :1 := deb_lib.read('zeta$', 5); end;");
                cs.registerOutParameter(1,java.sql.Types.VARCHAR);
//                System.out.println("executing reading");
                cs.executeUpdate();
                String s = cs.getString(1).trim();
//                System.out.println("recieved " + s);
                if (s != null && s.equals("yes")) {
                   new DMS(GLOBAL.pr(GLOBAL.MSG_USERALREADYEXIST, "fuck off"), false,
                                 false, GLOBAL.nafigator);
                   executeQuery("begin deb_lib.close('zeta$'); end;");
                   System.exit(0);
                }
    //            executeQuery("begin deb_lib.close('zeta$'); end;");
              } catch (Exception e) {
                if (!(e instanceof SQLException))
                  System.out.println(e);
              }
              executeQuery("begin deb_lib.close('zeta$'); end;");
              
              cs = conn.prepareCall("begin select count(distinct username) " +
                          "into :1 from v$session " +
                          "where machine='jdbcclient' " +
                          "and username <> user; end;");
              cs.registerOutParameter(1,java.sql.Types.INTEGER);
              cs.executeUpdate();
              n = cs.getInt(1);
              int i = 0;
              try {
                 //i = xyz.chen.util.PROPERS.initLicMan("license");
            	  ;
              } catch (Throwable e) {
                new DMS(GLOBAL.pr(GLOBAL.MSG_BADLICENSE, "Bad license"), false,
                          false, GLOBAL.nafigator);
                System.exit(0);
              }
              if (n >= i) {
                 new DMS(GLOBAL.pr(GLOBAL.MSG_BADUSERSCOUNT1, "There is already") + 
                               " " + i + " " +
                               GLOBAL.pr(GLOBAL.MSG_BADUSERSCOUNT2, "users.\n   Fuck off."),
                               false, false, GLOBAL.nafigator);
                 System.exit(0);
              }

            } catch (Exception e) {
              if (!(e instanceof SQLException))
                System.out.println(e);
            }

            executeQuery("begin deb_lib.close(); end;");
            if(GLOBAL.dstore_debug>0) 
                System.out.println("pipe removed...");
            cs = conn.prepareCall("begin :1 := deb_lib.init(); end;" ) ;
            cs.registerOutParameter(1,java.sql.Types.VARCHAR);
            int res = cs.executeUpdate();
            pipe = cs.getString(1);
            if(GLOBAL.dstore_debug>0) 
                System.out.println("Pipe initialized PipeName="+pipe);
            cs = conn.prepareCall("begin :1 := deb_lib.read; end;" ) ;
            cs.registerOutParameter(1,java.sql.Types.VARCHAR);
            success = true;
        }catch (Exception e){
            System.out.println("Error initializing pipe:"+e);//МЕ ЯЛНЦКХ ХМХЖХЮКХГХПНБЮРЭ ЙЮМЮ ДКЪ НРКЮДЙХ
            success = false;
        } finally {
          initializing = false;
            }
        }
	public static void initConnect(String connstr,String user,String passwd) throws ConnectException,BadPasswordException{
		try {
		    
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		 }catch (SQLException e) {
			 System.out.println ("Could not register the driver");
			 e.printStackTrace ();
			 throw new ConnectException(e.getMessage());
 		 }
		 if(GLOBAL.dstore_debug>0) System.out.println ("Connecting to the remote database");
		 try{
			  //conn = DriverManager.getConnection("jdbc:oracle:thin:"+user+"/"+passwd+"@server:1521:"+sid);
			  
			  
			 OracleDataSource ods = new OracleDataSource();
			  ods.setURL(connstr);
			  ods.setUser(user);
			  ods.setPassword(passwd);
			  //java.util.Locale.setDefault(new java.util.Locale("rus","ru"));
			 
			 
			 //conn =  DriverManager.getConnection(connstr);
			  conn = ods.getConnection();
			  
			  stmt = conn.createStatement();
			  executeQuery(DATASTORE.initSQL);
			  //executeQuery();
			  
			  executeQuery(DATASTORE.initSQL3);
			  executeQuery(DATASTORE.initSQL2);
			  //executeQuery(initSQL4);
		 }
		 catch (SQLException e){
		 		System.out.println(e.getMessage());
			 //System.out.println ("Bad password or username");
			 if (e.getMessage().toUpperCase().indexOf("PASSW")>0){throw new BadPasswordException("Неверный пароль или имя пользователя");}
			 else {throw new ConnectException("Невозможно установить соединение");}
		}
		if(GLOBAL.dstore_debug>0) System.out.println("Connected!");
	}
	/**
	* интерфейс к базе данных для выполнения SQL запроса 
	*/
	public static ResultSet executeQuery(String query) throws SQLException{
		//System.out.println("from DATASTORE:"+conn.getCatalog());
		//try{
		    if (stmt == null){
		        System.out.println("ERROR!! Trying Restore connection...");
		        try{
		            initConnect(DATASTORE.connStr, DATASTORE.user, DATASTORE.passwd);
		        }catch(BadPasswordException e){
		            throw new SQLException(e.getMessage());
		        }catch(ConnectException e1){
		            throw new SQLException(e1.getMessage());
		        }
		    }
		    return stmt.executeQuery(query);
	}
		
	public static void addMessage( String msg ){
	    //System.out.println("add message called!!!");
	    fr.addMessage(msg);
	    fr.show();
	    }
	/**
	* интерфейс к базе данных для выполнения SQL запроса 
	*/
    public void run(){
    	
    	if (0<1) return; //TODO САПЮРЭ
    	
        //
		//if (true) return;
		//
		initPipe();
		//boolean circle  = true;
        while (circle){
            try{
				System.out.println("run in errorreader") ;
                int res = cs.executeUpdate();
                String message = cs.getString(1);
                if (message.trim().startsWith("zeta$")) {
//                  System.out.println("recieved command " + message.trim());
                  String command = message.trim().substring(5);
                  if (command.equals("presence"))
                    executeQuery("begin deb_lib.write('yes', 'zeta$', 5); end;");
                  continue;
                }
                if(GLOBAL.dstore_debug>1) System.out.println("Receved message:"+message);
                fr.addMessage(message+"\n");
                fr.show();
                //new Msg(new SQLException("Receved message:"+message),new Frame("from ErrorReader")).show();
            }catch (SQLException e){
                //fr.addMessage("ERROR CODE IS:::"+e.getErrorCode()+"\n");
                if (e.getErrorCode()!=20117){
                    try{
                        sleep(1000);
                    }catch(Exception e1){}
                    System.out.println("Error recive message"+e);
                    fr.addMessage("нЬХАЙЮ ВРЕМХЪ ЯННАЫЕМХЪ:\n"+e+"\n");
                    fr.show();
                    success = false;
                    //new Msg(e,new Frame("from ErrorReader"));
                    //int count = 0;
                    while(!success){
                        //count++;
                        fr.addMessage("Beginning inicializing pipe:"+"\n");
                        fr.show();
                        fullreset = true;
                        initPipe();
                        //if (count == 3) fullreset = true;
                        }
                        
                    //НЬХАЙЮ ЯБЪГХ
                    }
            }
                        //
                         catch(Exception extra){return;}
                        //                 
            }
			//if (stmt!=null) executeQuery("begin deb_lib.close(); end;");
			//System.out.println("PIPE IS CLOSED");
        	//if (stmt!=null) stmt.close();
        	//if (conn!=null) conn.close();
         
        }

     public void closeConnect() {
       try {
         circle = false;
         while (initializing) ;
         DATASTORE.updateQuery("begin deb_lib.write('zeta$empty'); end;");
         executeQuery("begin deb_lib.close; end;");
         conn.close();
         DATASTORE.conn.close();
       } catch (Exception e) {
         System.out.println(e);
       }
     }

    }