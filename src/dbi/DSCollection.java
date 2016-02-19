package dbi;
import java.sql.*;
import java.util.*;
//import lisp.*;
import calc.*;
import loader.*;
/**
* обьект - коллекция DATASTORE позволяет совершать их синхронизацию с базой
* в пределах одной транзакции
*/
public class DSCollection extends DATASTORE {
	DATASTORE[] dss;
	String query,seqQuery;
	String[] aliases;
	String[] names_sq;
	Hashtable names;
	Hashtable fromDBnames;
	Hashtable seqNames = new Hashtable();
	DATASTORE head = null;
	String initAction = null;
	public DSCollection() {
		//System.out.println("Empty DSCollection created");
	}

	public void setNames(Hashtable names){this.names = names;}

	public void setDatastores(DATASTORE[] dss){
		//System.out.println("DSCollection created");
		this.dss = dss;
		for (int i=0;i<dss.length;i++){if(dss[i].isHead()) head=dss[i];}
}
    public void removeDs(String alias){
        Vector v = new Vector();
        for (int i=0;i<dss.length;i++){
            if (!dss[i].alias.equals(alias)) v.addElement(dss[i]);
            }
        DATASTORE ds[] = new DATASTORE[v.size()];
        v.copyInto(ds);
        dss = ds;
        }
    public void addDs(DATASTORE ds){
        DATASTORE[] dsa = new DATASTORE[dss.length+1];
        System.arraycopy(dss,0,dsa,0,dss.length);
        dsa[dss.length] = ds;
        dss = dsa;
       }
	public void setSeqQuery(String seqQuery){
		if (seqQuery==null) return;
		this.seqQuery = seqQuery;
	}
	public void setSeqNames(String seqNames){
		if (seqNames==null) return;
		seqNames = seqNames.toUpperCase();
		StringTokenizer st = new StringTokenizer(seqNames,",");
		int countToken = st.countTokens();
		names_sq = new String[countToken];
		for(int i=0;i<countToken;i++) names_sq[i] = st.nextToken();
	}
	public void nextVal() throws ConnectException{
		System.out.println("dbi.DSCollection.nextVal called");
		System.out.println("dbi.DSCollection.nextVal seqQuery="+seqQuery+" seqNames="+names_sq);
		ResultSet rset = null; 
		ResultSetMetaData rmd = null;
		//System.out.println("dbi.DSCollection.reset called");
		try {
			//System.out.println("dbi.DSCollection.reset before execute query="+query);
		    try{
                                rset = DATASTORE.executeQuery(seqQuery);
			}catch(Exception e2){e2.printStackTrace();}
                    rmd = rset.getMetaData();
			//System.out.println("dbi.DSCollection.reset rset="+rset);
			int columnCount = rmd.getColumnCount(); 
			int types;
			Object dat;
			rset.next();
			for(int i=1;i<=columnCount;i++){ 
				types = rmd.getColumnType(i);
				if(types==Types.NUMERIC){dat = new Double(rset.getDouble(i));}
				else{dat = rset.getObject(i);}
				seqNames.put(names_sq[i-1],dat);
			} 
		}catch(SQLException e){
			System.out.println("dbi.DSCollection.nextVal "+e);
			throw new ConnectException("Потеря соединения с сервером");
		}
	}
	public int retrieve(){
	    if (dss == null) return 0;
		for(int i=0;i<dss.length;i++){
			try{
				dss[i].retrieve();
			}catch(Exception e){e.printStackTrace();}
		}
		return 0;
	}
/**
* метод, синхронизирующий  коллекцию DATASTORE с базой 
*/
	public void update() throws UpdateException,ConnectException,SQLException{
		int i=0;
		if(GLOBAL.dstore_debug>0)
			System.out.println("dbi.DSCollection.update called");
		if (dss == null ) return;
                try{//pavel DATASTORE.conn.setAutoCommit(false);
		    try{
			    if (head!=null){
				    if(GLOBAL.dstore_debug>0) System.out.println("dbi.DSCollection.update UPDATING HEAD:::"+head);
				    //DATASTORE.executeQuery(" savepoint ");
				    head.update();
				    }
			    for (i=0;i<dss.length;i++) {dss[i].update();System.out.println("dbi.DSCOlection.update "+dss[i]);};
		    }catch (UpdateException e){
			    if(GLOBAL.dstore_debug>0) System.out.println(e);
			    for (i=0;i<dss.length;i++) {dss[i].rollback();System.out.println("rollback in "+dss[i]);};
			    DATASTORE.conn.rollback();
			    if(GLOBAL.dstore_debug>0) System.out.println("dbi.DSCollection: Rollback PERFORMED");
		        System.out.println("dbi.DSCollection.updateException "+e);
			    throw new UpdateException((SQLException)e,e.getBadKey(),i);
		    }catch (Exception e1){
			    DATASTORE.conn.rollback();
			    if (e1.getMessage().toUpperCase().indexOf("PROTOCOL")!=-1){
		                DATASTORE.stmt.executeQuery("rollback");
		                //System.out.println("Protocol violation from remote host\n"+
		                //            "trying restore connection... ");
		            //DATASTORE.initConnect(DATASTORE.connStr);
		            }
		       
			    System.out.println("dbi.DSCollection.UnknownException "+e1);
			    throw new UpdateException((SQLException)e1,0,i);
		     }
		
		DATASTORE.conn.commit();
                //pavel
                repeatLocks();
                //end pavel
		if(GLOBAL.dstore_debug>0) System.out.println("dbi.DSCollection: COMMIT PERFORMED");
                //pavel
                //DATASTORE.conn.setAutoCommit(true);
                //end pavel
		
		}catch(SQLException e){
		    if(e instanceof UpdateException) throw e;
		    else{
		    System.out.println("dbi.DSCollection.update: ConnectException performed");
		    System.out.println("throws Exception:"+e);
			throw new ConnectException(e.getMessage());
			}
		}
		
	}
	    
	public void reset() throws ConnectException{
		ResultSet rset = null; 
		ResultSetMetaData rmd = null;
		//System.out.println("dbi.DSCollection.reset called");
		try {
			System.out.println("dbi.DSCollection.reset before execute query="+query);
		    try{
                                rset = DATASTORE.executeQuery(query);
			}catch(Exception e2){e2.printStackTrace();}
                    rmd = rset.getMetaData();
			//System.out.println("dbi.DSCollection.reset rset="+rset);
			int columnCount = rmd.getColumnCount(); 
			int types;
			Object dat;
			if(GLOBAL.dstore_debug>0)System.out.println("dbi.DSCollection.reset rset="+rset);
			rset.next();
			fromDBnames = new Hashtable(columnCount);
			for(int i=1;i<=columnCount;i++){ 
				types = rmd.getColumnType(i);
				if(types==Types.NUMERIC){dat = new Double(rset.getDouble(i));}
				else{dat = rset.getObject(i);}
				
				fromDBnames.put(aliases[i-1],dat);
				if(GLOBAL.dstore_debug>0)System.out.println("dbi.DSCollection.reset aliases="+aliases[i-1]+" dat="+dat);
			} 
		}catch(SQLException e){
			System.out.println("dbi.DSCollection.reset "+e);
			throw new ConnectException("Потеря соединения с сервером");
		}
		if(GLOBAL.dstore_debug>0) System.out.println("dbi.DSCollection.reset dss.length="+dss.length);
                try{
		if (head!=null){
			head.retrieve();
			head.clear();
			head.newRow();
		}
		for(int i=0;i<dss.length;i++){
			if(!dss[i].isHead()) {dss[i].retrieve();dss[i].clear();}
			if (dss[i].handler!=null) dss[i].handler.notifyHandler(null);
		}
                } catch(Exception e){
                        e.printStackTrace();
                }
		if (initAction!=null) {
		    
		    try{
		        Calc c = new Calc(initAction);
		        c.eval(names);
		    }catch (Exception e){System.out.println("dbi.DSCollection.reaet:"); e.printStackTrace();}
		    
		    }
}

	public void setInitQuery(String query){
		if (query==null) return;
		this.query = query;
		//System.out.println("dbi.DSCollection setInitQuery query="+query);
	}
/**
* alias1,alias2,....,aliasn
*/
	public void setInitAction(String initAction){
	    this.initAction = initAction;
	    }
	public void setAliases(String alias){
		if (alias==null) return;
		try{
			alias = alias.toUpperCase();
			StringTokenizer st = new StringTokenizer(alias,",");
			int count = st.countTokens();
			aliases = new String[count];
			for(int i = 0;i<count;i++){
				aliases[i] = st.nextToken().trim();
			}
		}catch(Exception e){System.out.println(e + "from dbi.DSCollection");}
	}
	
	public void setValue(Object obj){};
    public Object getValue(){ return this;};
    public void setValueByName(String name, Object obj){};
    
    public Object GETVALUEBYNAME(String name){return getValueByName(name);}
    
    public Object getValueByName(String name){
		//System.out.println("dbi.DSCollection.getValuebyName called");
		Object o = null;
		try{
			o = fromDBnames.get(name);
		}catch(Exception e){System.out.println("from DSCollection"+e);}
		if (o==null) {
			try{
				//System.out.println("before get seqNames="+seqNames);
				o = seqNames.get(name);
				//if (o==null) return null;
				//System.out.println("before nextVal");
				nextVal();
			}catch(Exception e){e.printStackTrace(); return null;}
			//System.out.println(" seqquery Return from Database: "+seqNames.get(name));
			return seqNames.get(name);
		}else return o;
	};
	public Object method(String method,Object arg) throws Exception{
	    if (method.equals("DELSTORE") ){
			try{
			    removeDs((String)arg);
			    return new Double(0);
			}catch (ClassCastException e) {
			                              throw new RTException("CastException","method DELSTORE must have"+
			                              " one parameter compateable with String type");
										  }
		}else if (method.equals("ADDSTORE")){
		    try{
			    addDs((DATASTORE)arg);
			    return new Double(0);
			}catch (ClassCastException e) {
			                              throw new RTException("CastException","method ADDSTORE must have"+
										  "one parameter compateable with DATASTORE type");
										  }
                }else if (method.equals("LOCKROW")){
                  try {
                    Vector v = (Vector)arg;
                    String table = (String)v.elementAt(0);
                    String key = (String)v.elementAt(1);
                    Object val = v.elementAt(2);
                    lockRow(table, key, val);
                  } catch (ClassCastException e) {
                    throw new RTException("CastException", "Wrong arguments of LockRow <Table>, <Key>, <Value>");
                  } catch (IndexOutOfBoundsException e) {
                    throw new RTException("CastException", "Wrong arguments of LockRow <Table>, <Key>, <Value>");
                  }
		}else
		throw new RTException("HasNotMethod","method "+method+
										" not defined in class DSCOLLECTION!");
                return new Double(0); 
                
        }

        private static Vector locks = new Vector();
        private Vector thisLocks = new Vector();

		
        public static synchronized void repeatLocks() throws SQLException {
          for (int i = 0; i < locks.size(); i++)
            ((CallableStatement)locks.elementAt(i)).executeUpdate();
        }

        public void removeLocks() {
          synchronized (getClass()) {
            for (int i = 0; i < thisLocks.size(); i++)
              locks.removeElement(thisLocks.elementAt(i));
            try {
              DATASTORE.conn.rollback();
              repeatLocks();
            } catch (SQLException e) {
              System.out.println(e);
            }
          }
        }
        
        
        public synchronized void lockRow(String table, String key, Object val)
                     throws Exception {
          synchronized (getClass()) {
            String sql = "begin select " + key + " into :1 from " + table +
                           " where " + key + "=";
            if (val instanceof String)
              sql += "'" + val + "'";
            else if (val instanceof Double)
              sql += val;
            else
              throw new RTException("CastException", "<Value> can be only String or Number");

            sql += "for update nowait; end;";
            System.out.println(sql);
            CallableStatement cs = DATASTORE.conn.prepareCall(sql);
            if (val instanceof String)
              cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            else if (val instanceof Double)
              cs.registerOutParameter(1, java.sql.Types.NUMERIC);

            try {
              cs.executeUpdate();
            } catch (SQLException e) {
              if (e.getMessage().indexOf("NOWAIT") > -1)
                throw new SQLException(GLOBAL.pr(GLOBAL.MSG_BLOCKED, "нАЗЕЙР СФЕ ГЮАКНЙХПНБЮМ ДПСЦХЛ ОНКЭГНБЮРЕКЕЛ"));
              else
                throw e;
	}
		
            locks.addElement(cs);
            thisLocks.addElement(cs);
          }
        }
		
	public String type(){
		return "DSCOLLECTION";
	}
	
}
