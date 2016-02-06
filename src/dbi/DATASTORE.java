package dbi;
import java.util.*;
import java.sql.*;
//import lisp.*;
import loader.*;
import oracle.jdbc.pool.OracleDataSource;
import calc.*;
import java.text.*;
import calc.objects.*;
import java.io.*;
import document.*;

public class DATASTORE implements GlobalValuesObject,class_method,class_type{

    Progress progress = new Progress();
	/**
	* Обьекты, в кот
	орых хранится текущий коннект
	*/
	static Connection conn;
	static Statement stmt;

	public static final String compute = "@@COMPUTE_";
	Vector  computeColumn = null;
	int[] typeComputeColumn = null;

	/**
	*признак обновляемости данного обьекта
	*/
	boolean readOnly = true;
	/**
	*исходный обьект
	*/
	DATASTORE parent = null;
	/**
	*исходный, полученный запро
	*/
	String sql = null;
	/**
	*преобразованный запрос, возможно со вставленными аргумента
	*/
	String rsql = null;
	/**
	*имена полей полученного из базы набора(я их получаю при разборе выражения!)
	*/
	String[] colName = null;
	/**
	*имена полей полученного из базы набора
	*/

	String[] colLabels = null;
	/**
	*типы полученных поле
	*/
	int[] types = null;
	/**
	* массив ключей таблицы при сортировках и фильтрах он изменяется
	*/
	int[] keys = null;
	/**
	* массив ключей таблицы при сортировках  он изменяется, при фильтрации остается неизменны
	*/
	int[] skeys = null;
	/**
	*текущая строка
	*/
	int crow;
	/**
	*хэш, хранит спецификации фильтро
	*/
	Vector filters = new Vector();
	/**
	*массивы, определяющий сортировку поле
	*/

	int[] sortcolumn  = null;
	int[] direction = null;

	/**
	*таблицы, к которым относятся столбцы
	*/
	Hashtable tables = new Hashtable();
	/**
	*индексы полей, которые можно изменят
	*/
	int[] updColumns = null;
	/**
	*журнал операций 0-ничего
	*					1-UPDATE
	*					2-DELETE
	*					3-INERT (new row)
	*координаты абсолютные, т.е это ключи а не индексы
	*/
	Hashtable operation = new Hashtable();
	Hashtable old_operation = new Hashtable();
	/**
	*хэш, содержит первичные ключи таблиц
	*/
	Hashtable pkColumns = new Hashtable();
	/**
	*массив в котором лежат дакные, полученные из базы
	*/
	Vector data;
	/**
	*кол-во строк в буфере
	*/
	int countRows = 0;
	/**
	* Параметры текущей фильтрац
	*/
	int[] fColumns = null;
	Object[][] condition = null;
	int[] fkeys = null;
	/**
	* Массив описании actions
	*/
	String[] actions;
	/**
	* связи с родительскими обьекта
	*/
	Hashtable links;
	String strLinks;

	boolean head = false;
	Hashtable aliases;
	String[] defaults;


	Operation[] opers = null;
	String[][] deps = null;
    DATASTORE[] subStores;

/**
 *алиас данного обьекта
 */

	String alias;


    Packer packer;
	Handler handler = null;

	Object[] defrow;

	String selAction = null;


	/**
	*устанавливает соединение с БД
	*/
	public static ErrorReader er;
	static String connStr = "";
	static String user = "";
	static String passwd = "";
	
	
	static final String initSQL = "alter session set nls_language='Russian' ";
	static final String initSQL2 = "alter session set NLS_DATE_FORMAT='dd-mm-yyyy'";
	static final String initSQL3 = "alter session set NLS_TERRITORY=Russia";
	//static final String initSQL4 = "alter session set NLS_CHARACTERSET='UCS2'";

	//static final String upSQL = "alter session set nls_language='AMERICAN'";
	static PreparedStatement ps = null;
	//static PreparedStatement psup = null;
	public static void resetStmt() throws SQLException{
	          stmt = conn.createStatement();
			  executeQuery(initSQL);
			  //executeQuery();

			  executeQuery(initSQL3);
			  executeQuery(initSQL2);
	    }
	public static void closeConnect(){
	    try{
			er.circle = false;
			//ErrorReader.conn.close();
			if (er!=null) er.closePipe();
			//System.out.println("Pipe closed.");
			//er.stop();
	        if (conn!=null) conn.close();

	        //Comment by And

			//if (er!=null) er.closePipe();

			//
	        
			//System.out.println("Connection close.");
	    }catch (Exception e){System.out.println("Error closing connect.."+e);}
	    }
	public static void initConnect(String connstr, String user, String passwd) throws ConnectException,BadPasswordException{
        closeConnect();
		//System.out.println("+++ after closeConnect");
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
			  connStr = connstr;
			  DATASTORE.user = user;
			  DATASTORE.passwd = passwd;
			 
			  if(GLOBAL.dstore_debug>0) System.out.println ("Connecting string: "+connStr);
			  
			  
			  /*
			    String connString="jdbc:oracle:thin:@prodHost:1521:ORCL";

				OracleDataSource ods = new OracleDataSource();
				ods.setURL(connString);
				ods.setUser("scott");
				ods.setPassword("tiger");
				Connection conn = ods.getConnection();
			   * 
			   */
			  OracleDataSource ods = new OracleDataSource();
			  ods.setURL(connStr);
			  ods.setUser(user);
			  ods.setPassword(passwd);
			  //java.util.Locale.setDefault(new java.util.Locale("rus","ru"));
			  java.util.Locale.setDefault(new java.util.Locale("en","us"));
			  
			  //conn =  DriverManager.getConnection(connstr);
			  
			  conn = ods.getConnection();
			  System.out.println("Connected");
			  
			//pavel
              conn.setAutoCommit(false);
              //end pavel
			  
			  stmt = conn.createStatement();
			  
			  executeQuery(initSQL);
			  //executeQuery();
			  
			  executeQuery(initSQL3);
			  
			  executeQuery(initSQL2);
			  
			  //executeQuery(initSQL4);

	          //хМХЖХЮКХГЮЖХЪ ЙСПЯНПНБ, МЕНАУНДХЛШУ ДКЪ ВРЕМХЪ.ГЮОХЯХ НАЭЕЙРНБ
	          ps = conn.prepareStatement("insert into obj_table(id_object,object) values (:1,:2)" ) ;
              //psup = conn.prepareStatement("update obj_table set object=:1 where id_object = '"+pk+"'" ) ;


		 }
		 catch (SQLException e){
		 		System.out.println(e.getMessage());
		 		e.printStackTrace();
			 //System.out.println ("Bad password or username");
			 if (e.getMessage().toUpperCase().indexOf("PASSW")>0){throw new BadPasswordException("мЕБЕПМШИ ОЮПНКЭ ХКХ ХЛЪ ОНКЭГНБЮРЕКЪ");}
			 else {throw new ConnectException("мЕБНГЛНФМН СЯРЮМНБХРЭ ЯНЕДХМЕМХЕ");}
		}
		if(GLOBAL.dstore_debug>0) System.out.println("Connected!");
		if(GLOBAL.dstore_debug>0) System.out.println("starting thread...");
		er = new ErrorReader();
		er.start();
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
		            initConnect(connStr,user,passwd);
		        }catch(BadPasswordException e){
		            throw new SQLException(e.getMessage());
		        }catch(ConnectException e1){
		            throw new SQLException(e1.getMessage());
		        }
		    }
		    return stmt.executeQuery(query);
	}

	public void addPacker(Packer p){
	    this.packer = p;
	    }

	    public static boolean checkRegInfo(String key,String ident){
	        try{

	            ResultSet rs = executeQuery("select count(1) from secure where key='"+key+"' and info='"+ident+"'");
	            int count = 0;
	            if (rs.next()){
	                count = rs.getInt(1);
	                }
	            rs.close();
	            updateQuery("insert into logging values('"+ident+"')");
	            if (count==0) return false;
	            else return true;
	            }catch(SQLException e){System.out.println(e);return false;}



	        }
	 public static String createRegInfo(String ident,String info) throws Exception{
	    try{
	        ResultSet rs = executeQuery("select log_sq.nextval from dual");
	            int key = 0;
	            if (rs.next()){
	                key = rs.getInt(1);
	                }
	            rs.close();
	            if (key==0) throw new Exception("Error generate secure key!");
	            updateQuery("insert into secure values('"+key+"','"+ident+"','"+info+"')");
	            updateQuery("insert into logging values('"+ident+"')");
	            return key+"";
	        }catch(SQLException e){System.out.println(e); throw e;}

	    }

	/**
	* интерфейс к базе данных для выполнения SQL запроса

	*/
	public Object readObject(String alias) throws Exception{
	    if(packer==null) return null;
	    //if (true) return null;
	    //System.out.println("in read object");
	    //if (Document.curd == null) System.out.println("curd is nULL!!!!");
	    //String pk = Document.curd.mypath+alias;
	    Document d = (Document)aliases.get("###document###");
	    String pk = connStr+"/"+GLOBAL.pr(GLOBALPROP.DOC_SERVER)+d.mypath+"/"+d.myname+"/"+alias;
	      //PreparedStatement ps = conn.prepareStatement("select object from obj_table where id_object = :2" ) ;
          //  ps.setString(1,pk);
          String query = "select object from obj_table where id_object = '"+pk+"'";
          //System.out.println("query="+query);
          Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            InputStream is = null;
            if( rs.next() ) {
                   is = rs.getBinaryStream(1);
                   Object o = packer.unpack(is);
                   is.close();
                   //resetStmt();
                   rs.close();
                   stmt.close();
                   return o;
                   //initConnect(connStr);
                   //System.out.println("read 1 stroka");
                   }
            rs.close();
            stmt.close();
            //initConnect(connStr);
	        //return is;
	        return null;
	    }

	public void saveObject(String alias,Object o) throws SQLException,IOException{
	    //String pk = Document.curd.mypath+alias;
	    if(packer==null) return;
	    //System.out.println(" in saveObject");
	    Document d = (Document)aliases.get("###document###");
	    String pk = connStr+"/"+GLOBAL.pr(GLOBALPROP.DOC_SERVER)+d.mypath+"/"+d.myname+"/"+alias;
	    String query = "select id_object from obj_table where id_object = '"+pk+"'";

	    //PreparedStatement ps = null;
	    int count = updateQuery(query);
	    //System.out.println("query="+query+" count="+count);

	    if (count == 0){
	        //System.out.println("count="+count);
	        InputStream is = packer.pack(o);
	        //ps = conn.prepareStatement("insert into obj_table(id_object,object) values (:1,:2)" ) ;
            ps.setString(1,pk);
            ps.setBinaryStream(2,is,is.available());

            //ps.setString(2,connStr);
            int res = ps.executeUpdate();
            //ps.close();
        }else{
            PreparedStatement psup = conn.prepareStatement("update obj_table set object=:1 where id_object = '"+pk+"'" ) ;
            InputStream is = packer.pack(o);
            //psup.setString(1,pk);
            psup.setBinaryStream(1,is,is.available());

            //System.out.println("pk set..pk="+pk);
            //ps.setString(2,pk);
            int res = psup.executeUpdate();
            psup.close();
            }
	    }

	public static Object readObject(String alias,loader.Callback packer) throws Exception{
	    if(packer==null) return null;
	    //if (true) return null;
	    //System.out.println("in read object");
	    //if (Document.curd == null) System.out.println("curd is nULL!!!!");
	    //String pk = Document.curd.mypath+alias;
	    //Document d = (Document)aliases.get("###document###");
	    String pk = alias;
	    //PreparedStatement ps = conn.prepareStatement("select object from obj_table where id_object = :2" ) ;
          //  ps.setString(1,pk);
          String query = "select object from obj_table where id_object = '"+pk+"'";
          //System.out.println("query="+query);
          Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            InputStream is = null;
            if( rs.next() ) {
                   is = rs.getBinaryStream(1);
                   Object o = packer.callback(is);
                   is.close();
                   rs.close();
                   stmt.close();
                   //resetStmt();
                   return o;
                   //initConnect(connStr);
                   //System.out.println("read 1 stroka");
                   }
            rs.close();
            stmt.close();
            //initConnect(connStr);
	        //return is;
	        throw new Exception("OBJECT WITH THIS KEY NOT FOUND!!!");
	        //return null;
	    }
	public static String key(String docKey){
	    return connStr+"/"+GLOBAL.pr(GLOBALPROP.DOC_SERVER)+docKey;
	    }
	public static void saveObject(String alias,loader.Callback packer) throws Exception{
	    //String pk = Document.curd.mypath+alias;
	    if(packer==null) return;
	    //System.out.println(" in saveObject");
	    //Document d = (Document)aliases.get("###document###");
	    String pk = alias;
	    String query = "select id_object from obj_table where id_object = '"+pk+"'";
	    //PreparedStatement ps = null;
	    int count = updateQuery(query);
	    //System.out.println("query="+query+" count="+count);

	    if (count == 0){
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        //System.out.println("count="+count);
	        Object o = packer.callback(os);
	        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

	        //ps = conn.prepareStatement("insert into obj_table(id_object,object) values (:1,:2)" ) ;
            ps.setString(1,pk);
            ps.setBinaryStream(2,is,is.available());
            System.out.println("Inserting;;;;;;");
            //ps.setString(2,connStr);
            int res = ps.executeUpdate();

            //ps.close();
        }else{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Object o = packer.callback(os);
            PreparedStatement psup = conn.prepareStatement("update obj_table set object=:1 where id_object = '"+pk+"'" ) ;
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            psup.setBinaryStream(1,is,is.available());
            System.out.println("Updating;;;;;;");
            //psup.setString(2,pk);

            int res = psup.executeUpdate();
            psup.close();
            }
            conn.commit();
	    }



	public static int updateQuery(String query) throws SQLException{
		return stmt.executeUpdate(query);
	}
	public void initActions(String act){
		//this.actions = actions;
		if (act==null) return;
		StringTokenizer st = new StringTokenizer(act,";");
		int countToken = st.countTokens();
		actions = new String[countToken];
		for(int i=0;i<countToken;i++) actions[i] = st.nextToken().trim();
	}
	public ResultSet doAction(int id) throws ConnectException,Exception{
		try{
			return executeQuery(Calc.macro(actions[id],aliases));
			}catch(SQLException e){
			 System.out.println(e);
			 throw new ConnectException(e.getMessage());
			}
	}
	public void addSubStores(Object[] objs){
	    if (objs==null) return;
		Vector v = new Vector();

		for(int i=0;i<objs.length;i++)
			if(objs[i] instanceof DATASTORE) v.addElement(objs[i]);
		subStores = new DATASTORE[v.size()];
		v.copyInto(subStores);
	    //subStores = dss;
	    }
	public void setSortOrder(String SortOrder){
	    //field -1|1; field -1|1
	    //System.out.println("WE CALLED "+SortOrder);
	    try{
	        if (colName==null) tables();
	    StringTokenizer st = new StringTokenizer(SortOrder,";");
	    int count = st.countTokens();
	    //System.out.println("count="+count);
	    int[] sort_fields = new int[count];
	    int[] sort_dim = new int[count];
	    for(int i=0;i<count;i++){
	        String param = st.nextToken();
	        //System.out.println("param="+param);
	        StringTokenizer st1 = new StringTokenizer(param);
	        //System.out.println("count2="+st1.countTokens());
	        //System.out.println("colName="+colName);
	        sort_fields[i] = getColumn(st1.nextToken().toUpperCase());
	        //System.out.println(sort_fields[i]+"8888888888888888888888");
	        sort_dim[i] = (new Integer(st1.nextToken())).intValue();
	        }
	    this.sortcolumn = sort_fields;
	    this.direction = sort_dim;
	    }catch(Exception e){System.out.println(e+"in set SortOrder");}
	    };
	public void addOperation(Object[] objs){
		if (objs==null) return;
		Vector v = new Vector();

		for(int i=0;i<objs.length;i++)
			if(objs[i] instanceof Operation) v.addElement((Operation)objs[i]);
		opers = new Operation[v.size()];
		v.copyInto(opers);
	}
	public void notify(String alias,int op_id,int row){
		//Здесь row это КЛЮЧ а не индек
		//System.out.println("dbi.DATASTORE.notify  called
		//op_id="+op_id);
		DATASTORE db = (DATASTORE)aliases.get(alias);
		for (int i=0;i<opers.length;i++){
			if ((opers[i].getParent().compareTo(alias)==0)&&(opers[i].getOpid()==op_id))
				opers[i].doAction(this,row);
		}
		if (handler!=null) handler.notifyHandler(new Integer(getRowIndex(row)));

	}
	public void setDefRow(String dr){
		StringTokenizer st = new StringTokenizer(dr,",");
		int countToken = st.countTokens();
		defrow = new Object[countToken];
		for(int i=0;i<countToken;i++){
			if (types[i] == Types.NUMERIC) {
				defrow[i] = new	Double(st.nextToken());
			}else {defrow[i] = st.nextToken();}
		}
	}
	public void setDeps(String in,String up,String del){
		deps = new String[3][];
		int i;
		//System.out.println("dbi.DATASTORE.setDeps in="+in+" up="+up+" del="+del);
		StringTokenizer st;
		int countToken=0;
		if (in!=null){
			try{
			st = new StringTokenizer(in,",");
			countToken = st.countTokens();
			deps[0] = new String[countToken];
			for (i=0;i<countToken;i++) deps[0][i] =
										   st.nextToken().trim();
			}catch (Exception e){System.out.println(e);}
		}
		if (up!=null){
			st = new StringTokenizer(up,",");
			countToken = st.countTokens();
			deps[1] = new String[countToken];
			try{

				//System.out.println("dbi.DATASTORE.setDeps countToken= "+countToken+" deps[1]="+deps[1][0]);
			String s = null;

				s =  st.nextToken().trim();

				//System.out.println("s="+s);
			for (i=0;i<countToken;i++) deps[1][i] = s;
			}catch (Exception e){System.out.println(e+" from setDeps");}
			//System.out.println("dbi.DATASTORE.setDeps finished ");
		}
		if (del!=null){
			//System.out.println("dbi.DATASTORE.setDeps in del clause");
			st = new StringTokenizer(del,",");
			countToken = st.countTokens();
			deps[2] = new String[countToken];
			//countToken = st.countTokens();
			for (i=0;i<countToken;i++) deps[2][i] =
										   st.nextToken().trim();
		}
	}

	public void notifyChildren(int op_id,int row){
		//System.out.println("dbi.DATASTORE.notifyChildren : called op_id="+op_id);
		//if (handler!=null) handler.notifyHandler(null);
		if (deps==null) return;
		if (deps[op_id]==null) return;
		//System.out.println("dbi.DATASTORE.notifyChildren ");
		for(int i=0;i<deps[op_id].length;i++){
			//System.out.println("dbi.DATASTORE.notifyChildren ");
			DATASTORE child = (DATASTORE)aliases.get(deps[op_id][i]);
			//System.out.println("dbi.DATASTORE.notifyChildren : called child:"+child);
			child.notify(alias,op_id,row);
			//System.out.println("dbi.DATASTORE.notifyChildren : finished");
		}
	}
	public void addHandler(Handler hd){
		this.handler = hd;
	};
	public void removeHandler(){
		this.handler = null;
	}
	public void setSelAction(String str){this.selAction=str;}
	/**
	*устанавливает,доступен ли обьект только для чтения
	*/
	public void setReadOnly(boolean readOnly){
		this.readOnly = readOnly;
	};
	public void setAliases(String alias,Hashtable aliases){
		if (aliases==null){System.out.println("dbi.DATASTORE.setAliases aliases is null!");}
		this.aliases = aliases;
		this.alias = alias;
	};
	/**
	* filed = ~alias~,
	*/
	public void setDefaults(String def){
		String str = new String();
		String expr = def.toUpperCase();
		String f1,f2;
		tables();
		if (expr == null) return;
		StringTokenizer st = new StringTokenizer(expr,",");
		int countToken = st.countTokens();
		defaults = new String[colName.length];
		for(int i=0;i<countToken;i++){
			str = st.nextToken();
			if(GLOBAL.dstore_debug>2) System.out.println("dbi.DATASTORE.setDefaults def="+str);
			int index = str.indexOf("=");
			f1 = str.substring(0,index).trim();
			f2 = str.substring(index+1,str.length()).trim();
			if(GLOBAL.dstore_debug>2) System.out.println("dbi.DATASTORE.setDefaults f1="+f1+" f2="+f2);
			try{int curind = getColumn(f1);
				defaults[curind] = f2;
				}catch (Exception e){System.out.println(e);}
			//if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.setDefaults defaults[curind]"+defaults[curind]);

		}
	}
	public void clear(){
		if (data!=null) data.removeAllElements();
		keys = new int[0];
		skeys = new int[0];
		countRows = 0;
		if (data!=null) operation.clear();

	}
	public boolean isHead(){
		if(GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.isHead head="+head);
		return head;
	}
	public void setHead(boolean head){this.head = head;}

	public void setUpdateable(String data){};
	/**
	 *ищет первое вхождение о в поле column, начиная с  start_pos
	 *возвращает ИНДЕКС строки если ничего не нашел возвращает -1
	 */
	public int[] findElement(Object o,int column,int start_pos){
		Vector v = new Vector();
		for (int i=start_pos;i<getCountRows();i++){
			Object val=null;
			try{
				val = getValue(i,column);

				if (o instanceof String){
				//System.out.println("dbi.DATASTORE.findElement IT IS STRING!!!");
					if ((((String)val).trim()).equals(((String)o).trim())) {
						if (GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.findElement FOUND!!! ");
						v.addElement(new Integer(i));
					}
					//System.out.println("dbi.DATASTORE.findElement val="+val+" o="+o);
				}
				if (o instanceof Double){
					if(((Double)val).doubleValue()==((Double)o).doubleValue()){
						v.addElement(new Integer(i));
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("dbi.DATASTORE.findElement skeys.length ="+ skeys.length);
			}
		}
		int[] ret = new int[v.size()];
		for (int k=0;k<v.size();k++){
			ret[k] = ((Integer)v.elementAt(k)).intValue();
		}
		return ret;
	}

	/**
	* устанавливает список первичных ключей для кождой updateble таблицы
	* если для какой то из таблиц список не задан, ее изменение невозможно
	* формат ('table','column1,column2, ...')
	*/
	public void setUnique(String data){
		String table,tabledata,tabledata2;
		StringTokenizer st;
		String keys[];
		while (true){
		int b = data.indexOf("(");
		//System.out.println("b="+b);
		if (b == -1) return;
		int e = data.indexOf(")");
		//System.out.println("e="+e);
		if (e == -1) return;
		tabledata2 = data.substring(e+1,data.length());
		tabledata = data.substring(b+1,e);

		//System.out.println(tabledata);
		data = data.substring(e,data.length());
			int b1 = tabledata.indexOf("'");
			tabledata = tabledata.substring(b1+1,tabledata.length());
			int e1 = tabledata.indexOf("'");
			table = tabledata.substring(0,e1).toUpperCase();
			//System.out.println(table);
			tabledata = tabledata.substring(e1+1,tabledata.length());
		b = tabledata.indexOf("'");
		tabledata = tabledata.substring(b+1,tabledata.length());
		e = tabledata.indexOf("'");
		tabledata = tabledata.substring(0,e);
		st = new StringTokenizer(tabledata,",");
		keys = new String[st.countTokens()];
		int i=0;
		//System.out.println("TABLE++++"+table);
		while(st.hasMoreElements()){
			keys[i] = st.nextToken().toUpperCase();
			//System.out.println("COLUMN++++"+keys[i]);
			i++;
		}
		setPkColumns(keys,table);
		data = tabledata2;
		}
	};
	/**
	* инициализирует links "field=field,....."
	*/
	public void setLinks(String links){
		this.strLinks = links;
	};
	public void resolveLinks(String expr,DATASTORE rem){
		if(expr == null) return;
		String str = new String();
		expr = expr.toUpperCase();
		String f1,f2;
		if (expr == null) return;
		StringTokenizer st = new StringTokenizer(expr,",");
		int countToken = st.countTokens();
		links = new Hashtable(countToken);
		for(int i=0;i<countToken;i++){
			str = st.nextToken();
			int index = str.indexOf("=");
			f1 = str.substring(0,index).trim();
			f2 = str.substring(index+1,str.length()).trim();
			Integer curind = new Integer(getColumn(f1));
			Integer remind = new Integer(rem.getColumn(f2));
			links.put(remind,curind);
		}

	}
	/**
	*По имени таблицы, возвращает ранее установленную уникальную комбинацию
	*/
	public Hashtable getPkColumns(){
		return pkColumns;
	}
	public Hashtable getTables(){
		return tables;
	}
 	/**
	*возвращает,доступен ли обьект только для чтения
	*/
 	public boolean isReadOnly(){return readOnly;};
 /**
 *Устанавливает столбцы(поля), подлежащие дальнейшей записи в баз
 */
	public void setUpdColumns(String[] columns){
		for (int i=0; i<columns.length;i++){
			updColumns[getColumn(columns[i])] = 1;
			}
		};
	/**
	*Возвращает столбцы(поля), подлежащие дальнейшей записи в баз
	*/
	public String[] getUpdColumns() {
		String[] ret = null;
		Vector v = new Vector();
		for(int i=0; i<colName.length;i++){
			if(updColumns[i] ==1){v.addElement(colName[i]);}
		}
		v.copyInto(ret);
		return ret;
	};
/**
*Устанавливает столбцы(поля), подлежащие дальнейшей записи в баз
*/
	public void setUpdColumns(int[] columns){
		updColumns = new int[columns.length];
		System.arraycopy(columns,0,updColumns,0,columns.length);
		};
/**
*Возвращает столбцы(поля), подлежащие дальнейшей записи в баз
*/
	public int[] getIUpdColumns() {return updColumns;};
	/**
	*Устанавливает поля(столбцы) таблицы, считающиеся уникальной коибинацие
	* DEPRECATED!
	*/
	public void setPkColumns(String[] columns, String table){
		pkColumns.put(table,columns);
	};
	/**
	*По имени таблицы, возвращает ранее установленную уникальную комбинацию
	*DEPRECATED!
	*/
	public String[] getPkColumns(String table){
		return (String[])pkColumns.get(table);
	};
	/**
	*По имени таблицы, возвращает ранее установленную уникальную комбинацию
	*DEPRECATED!
	*/
	public int[] getIPkColumns(String table){
		String[] ret;
		ret = (String[])pkColumns.get(table);
		int[] iret = new int[colName.length];
		for (int i=0; i<ret.length;i++){
			for(int j=0;j<colName.length;j++){
				if (ret[i].compareTo(colName[j]) == 0) {iret[j] = 1;}
			}
		}
		return iret;
	};
	/**
	*Возвращает список таблиц для которых установлена уникальная комбинация
	*DEPRECATED!
	*/
	public String[] getUpTables(){
		String[] ret = new String[pkColumns.size()];
		int i = 0;
		for  (Enumeration e = pkColumns.keys();e.hasMoreElements();){
			ret[i] = (String)e.nextElement();
			i++;
		}
		return ret;
	};
	/**
	*Устанавливает поля(столбцы) таблицы, считающиеся уникальной коибинацие
	* DEPRECATED!
	*/
	public void setPkColumns(int[] columns, String table){
		Vector v = new Vector();
		for(int i=0;i<columns.length;i++){
			if (columns[i] == 1) {v.addElement(colName[i]);};
		}
		String[] ret = new String[v.size()];
		v.copyInto(ret);
		pkColumns.put(table,ret);
	};
	/**
	*удаляет строку с ИНДЕКСОМ index
	*/
	public void delRow(int index){
		delRowForKey(getRowKey(index),index);
	}
	/**
	*удаляет строку с ИНДЕКСОМ index и КЛЮЧОМ row
	*/
	void delRowForKey(int row,int index){
		//System.out.println("skeys="+skeys+" parent="+parent);
		int pos = getPosition(row);
		//int row1 = index;
		if (parent == null){
			int[] op = new int[colName.length+2];
			Integer I = new Integer(row);
			if( !operation.containsKey(I) ){operation.put(I,op);}
			else {
					int[] op1 = (int[])operation.get(I);
					if(op1.length == colName.length+1){
						operation.remove(new Integer(row));
						}else operation.put(I,op);
				 }
			//System.out.println("dbi.DATASTORE::delRowForKey removing row. id="+row+"index="+index);
			//System.out.println("before");
			//int i1;
			//for (i1=0;i1<keys.length;i1++){System.out.println("keys["+i1+"]="+keys[i1]);}
			//for (i1=0;i1<skeys.length;i1++){System.out.println("skeys["+i1+"]="+skeys[i1]);}
			int oldcrow = crow;
			crow = index;
			if (GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE::delRowForKey calling notify children row="+row+" index="+index);
			notifyChildren(Operation.Delete,row);
			crow = oldcrow;
			int[] keys1 = new int[keys.length-1];
			int[] skeys1 = new int[skeys.length-1];
			System.arraycopy(keys,0,keys1,0,pos);
			System.arraycopy(keys,pos+1,keys1,pos,keys1.length-pos);
			//int i = row1;//getRowIndex(row);
			System.arraycopy(skeys,0,skeys1,0,index);
			System.arraycopy(skeys,index+1,skeys1,index,skeys1.length-index);

			keys = keys1; skeys = skeys1;
			countRows  = keys.length;
			//System.out.println("dbi.DATASTORE::delRowForKey in parent:Coutrows = "+countRows);
			//System.out.println("after");
			//for (i1=0;i1<keys.length;i1++){System.out.println("keys["+i1+"]="+keys[i1]);}
			//for (i1=0;i1<skeys.length;i1++){System.out.println("skeys["+i1+"]="+skeys[i1]);}
			children();

		}else {
			//System.out.println("parent called from delrow!");
			parent.delRowForKey(row,index);
			//parent.children();
			}
	};
	/**
	* создает новую строку и возвращает ее ИНДЕКС Вновь созданная строка СТАНОВИТСЯ ТЕКУЩЕ
	*/
	public int newRow(){
		if(parent == null){
			if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.newRow colName="+colName);
			Object[] o = new Object[colName.length];
			int[] op = new int[colName.length+1];
			if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.newRow defaults="+defaults);

			if (defaults!=null){
				for(int k=0;k<defaults.length;k++){
					if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.newRow defaults[i]="+defaults[k]);
					if(defaults[k]!=null) {
						try{
						    int l = defaults[k].indexOf("~");
						    int r = defaults[k].lastIndexOf("~");
						    String str = defaults[k].substring(l+1,r);
						    //System.out.println("string into calc is "+str);
						    Calc c = new Calc(str);
							o[k]=c.eval(aliases)[0];
						}catch(Exception e){e.printStackTrace();}
						op[k]=1;///////////////////////
						if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.newRow insert default value:"+o[k]);
					}
				}
			}
			//for(int i=0;i<o.length;i++)
			//if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.newRow insert array:"+o[i]);
			data.addElement(o);
			countRows ++;
			operation.put(new Integer(data.size()-1),op);
			//System.out.println("new row inserted. id="+(data.size()-1)+"operation="+operation);

			int[] keys1 = new int[keys.length+1];
			int[] skeys1 = new int[skeys.length+1];
			System.arraycopy(keys,0,keys1,0,keys.length);
			keys1[countRows-1] = data.size()-1;

			System.arraycopy(skeys,0,skeys1,0,skeys.length);
			skeys1[countRows-1] = data.size()-1;

			keys = keys1; skeys = skeys1;
			children();
			//System.out.println("Dstore.newRow data.size()="+data.size());
			//crow = countRows-1;
			setCurRow(countRows-1);
			try{
				notifyChildren(Operation.Insert,countRows-1);
			}catch (Exception e){e.printStackTrace();}

			return countRows-1;

		}else return parent.newRow();
	};
	//////////////////////////GlobalValueObject functions ////////////////
	/**
	* реализация интерфейса GlobalValueObject
	*/
	public void setValue(Object obj){};
	/**
	* реализация интерфейса GlobalValueObject
	*/
    public Object getValue(){return this;};
	/**
	* реализация интерфейса GlobalValueObject
	*/
    public void setValueByName(String name, Object obj){
		setValue(crow,name,obj);
	};
	/**
	* реализация интерфейса GlobalValueObject
	*/
    public Object getValueByName(String name) throws Exception{
		if (crow==-1) {return defrow[getColumn(name)];}
		//System.out.println("called:"+name);
		try{
		    //System.out.println("returning....from datastore:"+getValue(crow,name));
		    //System.out.println("current row is:"+crow);
		    return getValue(crow,name);
		}catch (Exception e) {throw new RTException("NullException","Column is not inicialized");}
	};

	/////////////////////////////////////////////////////////////////////
	/**
	*Возвращает значение столбца выборки из текущей строк
	*/
	public Object getValue(String column) {
	    //
	    //System.out.println("current row in datastore.getValue is "+crow);
	    //
		if(parent == null){
			return ((Object[])data.elementAt(skeys[crow]))[getColumn(column)];
		}else return parent.getKValue(skeys[crow],column);
	};
	/**
	*Возвращает значение столбца выборки из текущей строк
	*/
	public Object getValue(int column){
		if(parent == null){
			return ((Object[])data.elementAt(skeys[crow]))[column];
		}else return parent.getKValue(skeys[crow],column);
	}
	/**
	*Возвращает значение столбца выборки из  строки с ИНДЕКСОМ row
	*/
	public Object getValue(int row, int column){
		if(parent == null){

			return ((Object[])data.elementAt(skeys[row]))[column];

		}else return parent.getKValue(skeys[row],column);
	};
	/**
	*Возвращает значение столбца выборки из  строки с ИНДЕКСОМ row
	*/
	public Object getValue(int row, String column ){
		//System.out.println("in get value");
		if(parent == null){
		    return ((Object[])data.elementAt(skeys[row]))[getColumn(column)];
		}else return parent.getKValue(skeys[row],column);
	};

	/**
	*Возвращает значение столбца выборки из  строки с КЛЮЧЕМ row
	*/
	public Object getKValue(int row, int column){
		if(parent == null){
		    //System.out.println ("called:"+row+" "+column);
			return ((Object[])data.elementAt(row))[column];
		}else return parent.getKValue(row,column);
	};
	/**
	*Возвращает значение столбца выборки из  строки с КЛЮЧЕМ row
	*/
	public Object getKValue(int row, String column ){
		//System.out.println("in get value");
		if(parent == null){
		    //System.out.println ("called:"+row+" "+column);
		    return ((Object[])data.elementAt(row))[getColumn(column)];
		}else return parent.getKValue(row,column);
	};


	//////////////////////////////////////////////////////////////////////////
	/**
	*Устанавливает значение столбца выборки для текущей строк
	*/
	public void setValue(String column, Object value){
		if (parent == null){
			//System.out.println(column+" "+value+"ins");
			Object[] o = (Object[])data.elementAt(skeys[crow]);
                        try{
                        if (o[getColumn(column)].equals(value)) return;
                        }catch(Exception e){}
			o[getColumn(column)] = value;
			data.setElementAt(o,skeys[crow]);
			Integer I = new Integer(skeys[crow]);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,crow);
				//if (handler!=null) handler.notifyHandler(null);
			}else {
				int[] op = new int[colName.length];
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,crow);
				//if (handler!=null) handler.notifyHandler(null);
			}
		}else parent.setValue(getRowKey(crow),column,value);
	};
	/**
	*Устанавливает значение столбца выборки для текущей строк
	*/
	public void setValue(int column, Object value){
		if (parent == null){
			//System.out.println(column+" "+value+"ins");
			Object[] o = (Object[])data.elementAt(skeys[crow]);
                        try{
                        if (o[column].equals(value)) return;
                        }catch(Exception e){}
			o[column] = value;
			data.setElementAt(o,skeys[crow]);
			Integer I = new Integer(skeys[crow]);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,crow);
			}else {
				int[] op = new int[colName.length];
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,crow);
			}
		}else parent.setKValue(getRowKey(crow),column,value);
	};
	/**
	*Устанавливает значение столбца выборки для строки с ИНДЕКСОМ row
	*/
	public void setValue(int row, int column, Object value ){
		//System.out.println(parent+" we called!");
		if (parent == null){
			//System.out.println("row="+row+" column="+column+" value="+value);
			Object[] o = (Object[])data.elementAt(skeys[row]);
                        try{
                        if (o[column].equals(value)) return;
                        }catch(Exception e){}
			o[column] = value;
			data.setElementAt(o,skeys[row]);
			Integer I = new Integer(skeys[row]);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,skeys[row]);
			}else {
				int[] op = new int[colName.length];
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,skeys[row]);
			}
		}else parent.setKValue(getRowKey(row),column,value);
	};
	/**
	*Устанавливает значение столбца выборки для строки с ИНДЕКСОМ row
	*/
	public void setValue(int row, String column, Object value){
		if (parent == null){
			//System.out.println("SetValue called with value:"+value);
			//if (value instanceof String) System.out.println("it is STRING____________________");
			//if (value instanceof java.util.Date) System.out.println("it is Date____________________");

			Object[] o = (Object[])data.elementAt(skeys[row]);
                        try{
                        if (o[getColumn(column)].equals(value)) return;
                        }catch(Exception e){}
			o[getColumn(column)] = value;
			data.setElementAt(o,skeys[row]);
			Integer I = new Integer(skeys[row]);
			//System.out.println("I="+I+"operations="+operation);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,skeys[row]);
			}else {
				int[] op = new int[colName.length];
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,skeys[row]);
			}
		}else parent.setKValue(getRowKey(row),column,value);
	};
	/////////////////////////////////////////////////////////////////

	/**
	*Устанавливает значение столбца выборки для строки с КЛЮЧЕМ key
	*/
	public void setKValue(int key, String column, Object value){
		if (parent == null){
			//System.out.println(column+" "+value+"ins");
			Object[] o = (Object[])data.elementAt(key);
                        try{
                        if (o[getColumn(column)].equals(value)) return;
                        }catch(Exception e){}
			o[getColumn(column)] = value;
			data.setElementAt(o,key);
			Integer I = new Integer(key);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,key);
			}else {
				int[] op = new int[colName.length];
				op[getColumn(column)] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,key);
			}
		}else parent.setKValue(key,column,value);
	};
	/**
	*Устанавливает значение столбца выборки для строки с КЛЮЧЕМ key
	*/
	public void setKValue(int key,int column, Object value){
		if (parent == null){
			//System.out.println(column+" "+value+"ins");
			Object[] o = (Object[])data.elementAt(key);
                        try{
                           if (o[column].equals(value)) return;
                        }catch(Exception e){}
			o[column] = value;
			data.setElementAt(o,key);
			Integer I = new Integer(key);
			if(operation.containsKey(I)){
				int[] op = (int[])operation.get(I);
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,key);
			}else {
				int[] op = new int[colName.length];
				op[column] = 1;
				operation.put(I,op);
				notifyChildren(Operation.Update,key);
			}
		}else parent.setKValue(key,column,value);
	};

	/////////////////////////////////////////////////////////////////
	class Compute{
		String name;
		int type;
		public Compute(String name,int type){
			this.name=name;
			this.type=type;
		}
		public String getName(){return name;}
		public int getType(){return type;}
	}
	public String addColumn(int typeCol){
		if (computeColumn==null) computeColumn = new Vector();
		String name = compute+computeColumn.size();
		computeColumn.addElement(new Compute(name,typeCol));
		return name;
	}

	public boolean isSqlWord(String word){
		String[] array = {"+","-","*","/","(","||"};
		for (int i=0;i<array.length;i++){
			if (word.indexOf(array[i])!=-1) {return true;}
		}
		return false;
	}


	/**
	*разбирает SQL и формирует полные названия столбцов выборк
	*/
	protected void tables(){
	    String sql1 = sql.toUpperCase();
		tables = new Hashtable();
		if(GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.tables ");

		int k = sql1.indexOf("SELECT");
		String query = sql1.substring(k+6,sql1.indexOf("FROM"));
		if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.tables query= "+query);

		StringTokenizer st1 = new StringTokenizer(query,",");
		Vector str = new Vector();
		String table = "";
		Integer I;
		String colNam = new String("");
		int count = st1.countTokens();

		try{
			if (colName == null) {
				colName = new String[count];
				System.out.println("dbi.DATASTORE.tables:: GREATET COLNAME!!! ATTENSION!!! ");
			}else{
			    count = colName.length;
			    }
		for(int j=0;j<count;j++){
			try{
				//if(GLOBAL.dstore_debug>2) System.out.println("-----------------dbi.DATASTORE.tables EXCEPTION!! st1= ");
			String foo = st1.nextToken();
			StringTokenizer st=new StringTokenizer(foo,".");
			table = st.nextToken().trim();

			str = (Vector)tables.get(table);
			I = new Integer(j);
			if (str == null) {
				colNam = table+"."+st.nextToken().trim();
				str = new Vector();str.addElement(colNam);tables.put(table,str);
			}else {
				colNam = table+"."+st.nextToken().trim();
				str.addElement(colNam);tables.put(table,str);
			}
			}catch(Exception e){
								if(GLOBAL.dstore_debug>0)
									System.out.println("dbi.DATASTORE.tables  "+e+" j="+j);
								//try{
								if(colLabels!=null) {
									colNam = colLabels[j];
									if(GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.tables colLabels= "+colLabels[j]);
								}else {
									if(GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.tables table= "+table);
									colNam = table;
								}
								//}catch(Exception e1){System.out.println(e1);}
								}
			//System.out.println("co"+j);
			if(GLOBAL.dstore_debug>2) System.out.println("dbi.DATASTORE.tables colnam= "+colNam);
			colName[j] = colNam;
			//System.out.println("colName[i]"+colName[i]);


		}
		}catch(Exception e){System.out.println(e+"dbi.DATASTORE.tables");}

	}
	/**
	* синхронизирует содержимое внутреннего буфера с БД
	*/
	public void update() throws UpdateException,SQLException{
		//System.out.println("update called");
		try{
		if (readOnly) return;
		int[] op = null;
		Integer I;
		String sql = "";
		String table;
		//System.out.println("colname="+colName);
		int colCount = colName.length;
		//System.out.println("parent = "+parent);
		if(parent!=null){if(GLOBAL.dstore_debug>1){System.out.println("calling parent update...");}
		parent.update();
		return;}
		//Инициализация классов преобразования даты
		StringBuffer datBuff = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.UK);
		FieldPosition fp = new FieldPosition(0);
		//System.out.println("in parent update operations size = "+operation);
		//try{
		for(Enumeration  e = operation.keys();e.hasMoreElements();){
		    //executeQuery(up);
			I = (Integer)e.nextElement();
			//System.out.println(I+"dbi.DATASTORED::update in update");
			op = (int[])operation.get(I);
			if(op.length == colCount+1){
						//insert
						if(GLOBAL.dstore_debug>0) System.out.println("insert executing");
						for(Enumeration tbl = tables.keys();tbl.hasMoreElements();){

							table = (String)tbl.nextElement();
							String[] pkColumns = getPkColumns(table);
							if(pkColumns == null){continue;}
							sql = "insert into "+table +" ( ";
							Vector v = (Vector)tables.get(table);
							String[] Columns = new String[v.size()];
							v.copyInto(Columns);
							boolean vs = false;
							for (int i=0;i<Columns.length;i++){
								if(op[getColumn(Columns[i])] == 1){
									//System.out.println("Columns="+Columns[i]);
									if(i!=Columns.length-1)
									{sql = sql+Columns[i]+" , ";}else {sql = sql+Columns[i]+") values (";vs=true;}
								}//if
							}//for
							if(!vs) {sql = sql.substring(0,sql.length()-2); sql = sql+") values (";}
							vs = false;
							//System.out.println("\n\n**********NOW INQUISING DATASTORE**********");
							for (int i=0;i<Columns.length;i++){
								if(op[getColumn(Columns[i])] == 1){
									int typeCol = types[getColumn(Columns[i])];
									Object myobj = ((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];
									//ДЮММШИ ТКЮЦ НОПЕДЕКЪЕР, МСФМН КХ ГМЮВЕМХЕ ЯРНКАЖЮ НАПЮЛКЪРЭ ЮОНЯРПНТЮЛХ;
									boolean apInsFlag =
									    ((typeCol ==  Types.TIMESTAMP)||
									    (typeCol == Types.VARCHAR)||
									    (typeCol == Types.CHAR)||
									    (typeCol == Types.DATE)||typeCol==-8)&&
									( (myobj!=null) && (myobj instanceof String ? !((String)myobj).equals("") : true));
									//int ml=0;
									//if ((myobj!=null) && (myobj instanceof String)) {ml=((String)myobj).length();}
									//else ml=0;
			                        //System.out.println("Column name="+Columns[i]+"; Column type="+typeCol+"; Column value="+myobj+"; Column StringValue.length="+ml);
									if(i!=Columns.length-1){
										//if( ((typeCol ==
										//	  Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8) && (((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])]!=null)){sql=sql+"'";}
										if (apInsFlag) {sql=sql+"'";}
											  Object o1 = ((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];
										if ( (o1==null) || ((o1 instanceof String ) && (o1.equals("") ) ) ) {sql = sql+"NULL";}
										else {
											Object o = ((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];
											if (typeCol==Types.DATE||typeCol==Types.TIMESTAMP){

												//try{
												sdf.format((java.util.Date)o,datBuff,fp);
													//}catch (Exception
													//er){System.out.println(o+" "+er);}
												sql=sql+new String(datBuff);
												datBuff = new StringBuffer();
											}else sql = sql+o;

										}
										sql = sql.trim();
										//if( ((typeCol ==
										//	  Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8) && (((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])]!=null)){sql=sql+"'";}
										if (apInsFlag) {sql=sql+"'";}
										sql = sql+" , ";

									}else {
											//if( ((typeCol ==
											//	  Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8) && (((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])]!=null)){sql=sql+"'";}
											if (apInsFlag) {sql=sql+"'";}
											Object o1 = ((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];
											if ((o1==null) || ((o1 instanceof String ) && (o1.equals("") ) ) ) {sql = sql+"NULL";}
											else {
												Object o
													=((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];
												if (typeCol==Types.DATE||typeCol==Types.TIMESTAMP){
													//try{
													sdf.format((java.util.Date)o,datBuff,fp);
														//}catch(Exception
														// er){System.out.println(o+" "+er);}
													sql=sql+new String(datBuff);
													datBuff = new StringBuffer();
												}else sql = sql+o;
												sql = sql.trim();
											}
											vs = true;
											//if( ((typeCol ==
											//	  Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8) && (((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])]!=null)){sql=sql+"'";}
											if (apInsFlag) {sql=sql+"'";}
											sql = sql+")";
											}
								}//if
							}//for
							//System.out.println("\n**********END OF INQUISING DATASTORE**********\n\n");
							if(!vs){sql = sql.substring(0,sql.length()-2);sql = sql+")";}

							try{
							    if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.update: insert executing. Query="+sql);
							    ///System.out.println("before sql=2222");
							    executeQuery(sql);
							    //System.out.println("Query performed success");
							}
							catch (Exception ex){
							    if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.update(insert)");					//operation.remove(I);t
							                        //throw new Error("BAD ERROR");
							                        throw new UpdateException((SQLException)ex,I.intValue());
												  }
						}
										  }else
			if (op.length == colCount+2){
				//delete
				Stack stk = new Stack();
				if(GLOBAL.dstore_debug>0) System.out.println("delete executing");
				for(Enumeration tbl = tables.keys();tbl.hasMoreElements();){
					stk.push(tbl.nextElement());
				}
				while(!stk.empty()){
					table = (String)stk.pop();
					sql = "delete from "+table +" where ";
					String[] pkColumns = getPkColumns(table);
					if (pkColumns == null){continue;}
					for(int i=0;i<pkColumns.length;i++){
						sql = sql+pkColumns[i]+" = ";
						int typeCol = types[getColumn(pkColumns[i])];
						if( (typeCol == Types.TIMESTAMP)||(typeCol ==
														   Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}
						Object o =((Object[])data.elementAt(I.intValue()))[getColumn(pkColumns[i])];
						if (typeCol==Types.DATE||typeCol==Types.TIMESTAMP){
													sdf.format((java.util.Date)o,datBuff,fp);
													sql=sql+new String(datBuff);
													datBuff = new StringBuffer();
												}else sql = sql+o;
								sql = sql.trim();
								if( (typeCol == Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}
						if (i!=pkColumns.length-1){sql=sql+" and ";}// else {sql=sql+";";};
					}
					try{if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.update:delete executing. Query="+sql);
						DATASTORE.executeQuery(sql);
					}catch (Exception ex){if(GLOBAL.dstore_debug>0) System.out.println(ex);
								//operation.remove(I);
								throw new UpdateException((SQLException)ex,I.intValue());}
				}
			}else if (op.length==colCount){

						//update
						if(GLOBAL.dstore_debug>0) System.out.println("update executing");
						//System.out.println("in update clause!");
						for(Enumeration tbl = tables.keys();tbl.hasMoreElements();){

							boolean updateble = false;
							boolean vs = false;
							table = (String)tbl.nextElement();
							//System.out.println("dbi.DATASTORE::update updating table: "+table);
							sql = "update  "+table +" set ";
							Vector v = (Vector)tables.get(table);
							String[] Columns = new String[v.size()];
							v.copyInto(Columns);
							//System.out.println("sql="+sql);
							for (int i=0;i<Columns.length;i++){
								if(op[getColumn(Columns[i])] == 1){

									vs = false;
									int typeCol = types[getColumn(Columns[i])];
									updateble = true;
									sql = sql+Columns[i]+" = ";
									Object o=null;

									    o =((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];

									if (o!=null && (o instanceof String ? !((String)o).equals("") : true)){
									if( (typeCol ==
										 Types.TIMESTAMP)||(typeCol ==
															Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}
									//Object o=null;

									//    o =((Object[])data.elementAt(I.intValue()))[getColumn(Columns[i])];

									if ((o!=null)&&(typeCol==Types.DATE||typeCol==Types.TIMESTAMP)){
													    sdf.format((java.util.Date)o,datBuff,fp);
													    sql=sql+new String(datBuff);
													    datBuff = new StringBuffer();

												}else if (o!=null) sql = sql+o;
									sql = sql.trim();
									 if( (typeCol == Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}
									}else sql = sql+"NULL";

									if (i!=Columns.length-1){sql = sql+",";vs = true;};

								}

							}


							if (vs) { sql = sql.substring(0,sql.length()-1);}

							if (updateble){
							String[] pkColumns = getPkColumns(table);
							if(pkColumns == null){continue;}
							sql = sql+" where ";
							for(int i=0;i<pkColumns.length;i++){
								int typeCol = types[getColumn(pkColumns[i])];
								sql = sql+pkColumns[i]+" = ";
															if(
									(typeCol ==
									 Types.TIMESTAMP)||(typeCol ==
														Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}
								Object o = ((Object[])data.elementAt(I.intValue()))[getColumn(pkColumns[i])];
								if ((o!=null)&&(typeCol==Types.DATE||typeCol==Types.TIMESTAMP)){
													sdf.format((java.util.Date)o,datBuff,fp);
													sql=sql+new String(datBuff);
													datBuff = new StringBuffer();
												}else sql = sql+o;
								sql = sql.trim();

								if( (typeCol == Types.TIMESTAMP)||(typeCol == Types.VARCHAR)||(typeCol == Types.CHAR)||(typeCol == Types.DATE)||typeCol==-8){sql=sql+"'";}

								if (i!=pkColumns.length-1){sql=sql+" and ";}
							}

							try{
							    if(GLOBAL.dstore_debug>0)
							    System.out.println("dbi.DATASTORE.update:Update executing. Query="+sql);
							    DATASTORE.executeQuery(sql);
							    //System.out.println("Update Success!");
							}catch (Exception ex){
							    if(GLOBAL.dstore_debug>0) System.out.println("dbi.Datastore.update()::update  "+ex);
							//operation.remove(I);
										throw new UpdateException((SQLException)ex,I.intValue());}
									}
						}

						}else {if(GLOBAL.dstore_debug>0) System.out.println("dbi.DATASTORE.update UNKNOWN OPERATION!!!");}
				}

	    if(GLOBAL.dstore_debug>0) System.out.println("dbi.Datastore.update:before clear operatiuon");
	    if (operation.size()!=0) old_operation = (Hashtable)operation.clone();

		operation.clear();

		}catch (SQLException ee3) {
		    System.out.println("Unknown Exception in Datastore.update:"+ee3);
		    throw ee3;
		    }
	};
	public void rollback(){
	    if (old_operation.size()!=0){
	        if(GLOBAL.dstore_debug>0) System.out.println("db..DATASTORE:: rollbacking...");
	        operation = old_operation;
	    }
	}
	/**
	*Заполняет обьект данными из базы.Данный метод ДОЛЖЕН вызываться перед ЛЮБЫМИ метода
	*обьекта, оперирующего с данны
	*/
        public int retrieve() throws Exception{
	    //System.out.println("retrieve in dbi.DATASTORE called");
	    if(GLOBAL.dstore_debug>1) System.out.println("dbi.DATASTORE.retrieve rsql="+rsql);
		try{
			rsql = Calc.macro(sql,aliases);
			}catch (Exception
					e){if(GLOBAL.dstore_debug>1)System.out.println("dbi.DATASTORE.retrieve:: ERROR IN SQL EXPRESSION:"+e);}
		if(GLOBAL.dstore_debug>1)System.out.println("dbi.DATASTORE.retrieve rsql="+rsql);
		GLOBAL.waitwin_text("");
		//System.out.println("we are here!");

		if(parent == null){
		ResultSet rset = null;
		ResultSetMetaData rmd = null;
		if(GLOBAL.dstore_debug>0)System.out.println("dbi.DATASTORE::retrieve rsql="+rsql);
		data = new Vector();
		operation = new Hashtable();
		int row = 0;
		try {
		    //GLOBAL.addmessage("бШОНКМЪЕРЯЪ ГЮОПНЯ...");
		    rset = DATASTORE.executeQuery(rsql);
		    rmd = rset.getMetaData();
		    
			int columnCount = rmd.getColumnCount();
			int compSize = 0;
			if (computeColumn!=null) compSize=computeColumn.size();
			//System.out.println("Count columns:"+columnCount);
			types = new int[columnCount+compSize];
			colName = new String[columnCount+compSize];
			colLabels = new String[columnCount];
			//sort = new Boolean[columnCount];
			//tables = new String[columnCount];
			//Сначала заполняем значениями из базы...
			for(int i=1;i<=columnCount;i++){
				//System.out.println("in for i="+i);
				types[i-1] = rmd.getColumnType(i);
				colLabels[i-1] = rmd.getColumnLabel(i);
			//System.out.println("typeName="+rmd.getColumnTypeName(i));
			//System.out.println("typeCode="+rmd.getColumnType(i));
			//System.out.println("colname="+rmd.getColumnName(i));
			//System.out.println("collavel="+rmd.getColumnLabel(i));
			//colName[i-1] = rmd.getColumnName(i);
			//tables[i-1] = "DIMENSION";//rmd.getTableName(i);
			}
			//А теперь заполняем COMPUTED FIELDS...
			for (int l=0;l<compSize;l++){
				Compute cm = (Compute)computeColumn.elementAt(l);
				types[columnCount+l] = cm.getType();
				colName[columnCount+l] = cm.getName();
			}
		//System.out.println("retrieving data....");

		    //progress.show();
			while (rset.next()){
				Object[] dat = new Object[columnCount+compSize];
				for(int i=1;i<=columnCount;i++){
					//System.out.println("retrieving: i="+i);
					if(types[i-1]==Types.NUMERIC){dat[i-1] = rset.getObject(i)==null?null:new Double(rset.getDouble(i));}
					else{dat[i-1] = rset.getObject(i);}
				//if (dat[i-1] instanceof java.sql.Date)
				//System.out.println(dat[i-1]+"IT IS DATE!!!");
				}
			//System.out.println();
				data.addElement(dat);
				row++;
				if (row%100 == 0) GLOBAL.addmessage((new Integer(row)).toString());//progress.setprogress(row);
			}
		GLOBAL.waitout();
		//progress.hide();
		//System.out.println("end retrieving!");

		/*for (int i=0;i<v.size();i++)
			for(int j=0;j<columnCount;j++) System.out.println(data[i][j]+" row:"+i);*/
			rset.close();
        } catch (SQLException e ) {
			System.out.println(e+" \nError Code:"+e.getErrorCode()+
			"\n SqlState:"+e.getSQLState()+" from DataStore");
			er.addMessage(e.getMessage());
			GLOBAL.waitout();
			//progress.hide();
		};
		crow = 0;
		countRows = data.size();
		keys = new int[countRows];
		skeys = new int[countRows];
		for (int i=0;i<keys.length;i++) {keys[i] = i;skeys[i] = i;};
		}else {
				System.out.println("dbi.DATASTORE.retrieve call parent...");
				parent.retrieve();
				parent.children();
			   }
		tables();
		if (subStores!=null) for(int i=0;i<subStores.length;i++) subStores[i].retrieve();
		if((sortcolumn!=null) && (direction!=null)){
		    //System.out.println("begin sorting...");
		    setSort();
		    //VMatrix v = new VMatrix(this,sortcolumn,direction);
		    //Sorter s = new Sorter(v);
		    //skeys = s.getSortedArray();
		}
		if (handler!=null) handler.notifyHandler(null);
		//System.out.println("after retrieve");
		return 0;
	};
 	public DATASTORE(){
		data = new Vector();
		operation = new Hashtable();
		//tables();
	};
	/**
	* конструкто
	*/
 	public DATASTORE(int[] keys,DATASTORE parent){
		if(GLOBAL.dstore_debug>0) System.out.println("begin constructor....");

		this.parent = parent;
		this.keys = keys;
		this.skeys = keys;
		this.fkeys = new int[keys.length];
		this.crow = 0;
		this.types = parent.getTypes();
		this.colName = parent.getNames();
		this.sql = parent.getSql();
		this.rsql = sql;
		countRows = keys.length;
		this.colLabels = parent.colLabels;
		this.tables = parent.getTables();
		if(GLOBAL.dstore_debug>0) System.out.println("end constructor....");
	};
	/**
	* установка значений ключей для дочернего обьекта
	*/
	public void setkeys(int[] keys1){
		keys = new int[keys1.length];
		skeys = new int[keys1.length];
		System.arraycopy(keys1,0,keys,0,keys1.length);
		System.arraycopy(keys1,0,skeys,0,keys1.length);
		setSort(sortcolumn,direction);
		//System.out.println("Setting sort order.....");
		//for(int i=0;i<skeys.length;i++) System.out.println("skeys= "+skeys[i]);

		countRows = keys.length;
		//System.out.println("dbi.DATASTORE.setkeys called");
		//System.out.println("dbi.DATASTORE.setkeys in child countRows =  "+keys1.length);
		//this.skeys = keys1;
	};
	/**
	* возвращает свой массив ключе
	*/
	public int[] getkeys(){
		return keys;
	};
	/**
	*Установка запроса, описывающего данные подлежащие выборке
	*/
	public void setSql(String sql) {
		if(parent==null){
	    	this.sql = sql;
	    	this.rsql = sql;
	    	countRows = 0;
			}else parent.setSql(sql);
	    };
 /**
 *Возвращает запро
 */
	public String getSql(){return sql;};

	/**
	*Устанавливает и выполняет сортировк
	*/
	public void setSort(){
	    VMatrix vm = new VMatrix(this,sortcolumn,direction);
		Sorter s = new Sorter(vm);
		//System.out.println("Sorting begin");
		//t1 = System.currentTimeMillis();
		//for(int i=0;i<skeys.length;i++) System.out.println("skeys=
		//"+skeys[i]);
		//if(GLOBAL.dstore_debug>0)
		//System.out.println("dbi.DATASTOREt.setSort  keys="+keys+" skeys="+skeys);
        skeys = s.getSortedArray();
		//if(GLOBAL.dstore_debug>0)
		//System.out.println("dbi.DATASTOREt.setSort  keys="+keys+" skeys="+skeys);
		//t2 = System.currentTimeMillis();
		//System.out.println("Sorting end");
		//System.out.println("Time elapsed : " + (t2-t1));
	    }
	public void setSort(int[] column, int[] direction) {
		long t1,t2;
		this.sortcolumn = column;
		this.direction = direction;
		//System.out.println(column+" "+direction+" "+this);
		VMatrix vm = new VMatrix(this,column,direction);
		Sorter s = new Sorter(vm);
		//System.out.println("Sorting begin");
		//t1 = System.currentTimeMillis();
		//for(int i=0;i<skeys.length;i++) System.out.println("skeys=
		//"+skeys[i]);
		//if(GLOBAL.dstore_debug>0)
		//System.out.println("dbi.DATASTOREt.setSort  keys="+keys+" skeys="+skeys);
        skeys = s.getSortedArray();
		//if(GLOBAL.dstore_debug>0)
		//System.out.println("dbi.DATASTOREt.setSort  keys="+keys+" skeys="+skeys);
		//t2 = System.currentTimeMillis();
		//System.out.println("Sorting end");
		//System.out.println("Time elapsed : " + (t2-t1));

	};
	/**
	*Сбрасывает сортировк
	*/
	public void resetSort(){System.arraycopy(keys,0,skeys,0,keys.length);};

	/**
	*Возвращает текущую строку Нумерация строк начинается с 0
	*/
	public int getCurRow(){return crow;};
	/**
	*Устанавливает текущую строку Нумерация строк начинается с 0
	*/
	public void setCurRow(int i){
	    int old = crow;
		crow = i;
		try{
			if ((selAction!=null) && ( old!=crow )){
			    if(GLOBAL.dstore_debug>0) System.out.println("Setcurrrow called!!!");
				document.ACTION.doAction(selAction,aliases,null);
			}
			if (parent!=null) parent.setCurRow(skeys[i]);
		}catch(Exception e){e.printStackTrace();}
	};
	/**
	*Переходит к следующей по порядку строке
	*/
	public void nextRow() {
		crow++;
		try{
			if (selAction!=null)
				document.ACTION.doAction(selAction,aliases,null);
			if(parent!=null) parent.setCurRow(skeys[crow]);
		}catch(Exception e){e.printStackTrace();}
	};
	/**
	*Возвращает типы столбцов результирующего набора
	*/
	public int[] getTypes(){return types;};
	/**
	*Возвращает имена столбцов результирующего набора
	*/
	public String[] getNames(){return colName;};
	/**
	*Возвращает количество столбцов результирующего набора
	*/
	public int getCountColumns(){return colName.length;};
	/**
	*Возвращает количество строк результирующего набора
	*/
	public int getCountRows(){return countRows;};
	/**
	*Возвращает тип столбца
	*/
	public int getType(String column){ return types[getColumn(column)];};
	/**
	*Возвращает тип столбца
	*/
	public int getType(int column){return types[column];};
	/**
	* осуществляет перестроение потомко
	*/
	public void children(){
		DATASTORE rep = null;
		int[] fkeys1 = null;
		//System.out.println("in "+this);
		for(int k=0;k<filters.size();k++){
			rep = (DATASTORE)filters.elementAt(k);
			//System.out.println("dbi.DATASTORE::children child "+rep);
			fkeys1 = rep.getRowFilter();
			int[] fColumns1 = null;
			Object[][] condition1 = null;
			fColumns1 = rep.getColumnFilterColumns();
			condition1 = rep.getColumnFilterCondition();
			//System.out.println(fkeys1+" "+fColumns1+" "+condition1);
			if(fkeys1 != null){
				rep.setkeys(fkeys1);
				//System.out.println("dbi.DATASTORE::children call children "+rep +" of parent "+this+" setting freys="+fkeys1);
				rep.children();
				}
			if(fColumns1 != null && condition1 != null) {
				fkeys1 = filter(fColumns1,condition1);
				//System.out.println("dbi.DATASTORE::children fkeys.length="+fkeys1.length);
				rep.setkeys(fkeys1);
				//System.out.println("dbi.DATASTORE::children call children"+rep +" of parent"+this);
				rep.children();
				}
		}

	}
	/**
	* устанавливает для фильтра параметры его создания
	*/
	public void setCondition(int[] keys1){
		System.arraycopy(keys1,0,fkeys,0,keys1.length);
	};
	/**
	* устанавливает для фильтра параметры его создания
	*/
	public void setCondition(int[] fColumns,Object[][] condition){
		this.fColumns = fColumns;
		this.condition = condition;
		//System.out.println("dbi.DATASTORE.setCondition fColumns = "+this.fColumns+" condition="+this.condition+" this="+this);
	};
	/**
	* возвращает для фильтра параметры его создания
	*/
	public int[] getRowFilter(){
		return fkeys;
	};
	/**
	* возвращает для фильтра параметры его создания
	*/
	public int[] getColumnFilterColumns(){
		//System.out.println("dbi.DATASTORE.getColumnFilter fColumns = "+fColumns+" condition="+condition+" this="+this);
		return fColumns;
	};
	/**
	* возвращает для фильтра параметры его создания
	*/
	public Object[][] getColumnFilterCondition(){
		//System.out.println("dbi.DATASTORE.getColumnFilter fColumns = "+fColumns+" condition="+condition+" this="+this);
		return condition;
	};
	/**
	*Порождает фильтр из датасторе fColumns-столбцы на которые накладываются условия
	*								contition -условия в виде массива структур вида (мин,мах)
	*/
	public DATASTORE createFilter_with_abs_keys(int[] keys1){
		DATASTORE ds;
		if(GLOBAL.dstore_debug>0) System.out.println("in Create Filter.....");
		if(parent == null){
			ds = new DATASTORE(keys1,this);
		}else{
		    //щРН ТХКЭРП МЕ ОЕПБНЦН СПНБМЪ!!!
		    //МН ЩРНР ЛЕРНД БШГШБЮЕРЯЪ рнкэйн ХГ дюрюярнпе Х ОПХМХЛЮЕР ятнплхпнбюммши ЛЮЯЯХБ ЙКЧВЕИ
			ds = new DATASTORE(keys1,parent);
		}
		filters.addElement(ds);
		ds.setCondition(keys1);
		return ds;
		};
		public DATASTORE createFilter(int[] keys1){
		    //System.out.println("Create Filter Called!!!");
		DATASTORE ds;
		if(GLOBAL.dstore_debug>0) System.out.println("in Create Filter.....");
		//if(parent == null){
		//	ds = new DATASTORE(keys1,this);
		//}else{
		    //щРН ТХКЭРП МЕ ОЕПБНЦН СПНБМЪ!!!
		    //щРНР ЛЕРНД ОПХМХЛЮЕР люяяхб яяшкнй!!!
		    int[] filter_keys = new int[keys1.length];
		    for (int i=0;i<keys1.length;i++){
		        filter_keys[i] = skeys[keys1[i]];
		        //System.out.println("keys1[i]="+keys1[i]);
		        //System.out.println("filter_keys[i]="+filter_keys[i]);
		        }
		    if (parent == null) {ds = new DATASTORE(filter_keys,this);}
		    else {ds = new DATASTORE(filter_keys,parent);}
		///}
		filters.addElement(ds);
		//ds.setCondition(keys1);
		ds.setCondition(keys1);

		return ds;
		};
	/**
	* осуществляет реальную фильтрацию данных
	*/
	public int[] filter(int[] fColumns,Object[][] condition){
		int count = fColumns.length;
		int[] fkeys = new int[keys.length];
		//System.out.println("dbi.DATASTORE.filter fkeys.length = "+fkeys.length);
		System.arraycopy(keys,0,fkeys,0,keys.length);
		Vector v = new Vector();
		DATASTORE ds = null;
		if(parent == null) {ds=this;} else {ds = parent;};
		for (int i = 0;i<count;i++){
			//for(int l=0; l<fkeys.length;l++) System.out.print("fkeys["+l+"]="+fkeys[l]);
			//System.out.println();
			v.removeAllElements();
			switch (types[fColumns[i]]){
			case java.sql.Types.DECIMAL:
			case java.sql.Types.INTEGER:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:
			case java.sql.Types.NUMERIC:{
										 Double min = (Double)condition[i][0];
										 if(GLOBAL.dstore_debug>0) System.out.println("Decimal");
										 Double max = (Double)condition[i][1];
										 //System.out.println(max.doubleValue()+" =max");
										 for(int j=0;j<fkeys.length;j++){
										 		//System.out.println("in for:"+j);
												Double a = null;
												//try{
													a = ((Double)ds.getKValue(fkeys[j],fColumns[i]));
													//}catch (NullPointerException e){
													//v.addElement(new Integer(fkeys[j]));
													//continue;
													//};
												//System.out.println(a.doubleValue()+" in for...");
												//System.out.println(a.compareTo(min)+" COMPARE");
												try{
												if ((((min == null || a.doubleValue()>=min.doubleValue())) && ((max == null || a.doubleValue()<=max.doubleValue())))){
													v.addElement(new Integer(fkeys[j]));
													//System.out.println(a+"Inserted!");
													}
													}catch (NullPointerException e){
														v.addElement(new Integer(fkeys[j]));
														continue;
														};
												}
										//System.out.println("end Double");
										fkeys = new int[v.size()];
										for(int k = 0;k<v.size();k++){fkeys[k] = ((Integer)v.elementAt(k)).intValue();}
										//System.out.println("end "+i+"filtering");
										break;
										}
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
			case -8:            {String min = (String)condition[i][0];
										 if(GLOBAL.dstore_debug>0) System.out.println("Char");
										 String max = (String)condition[i][1];
										 //System.out.println(max+" =max "+min+" =min");
										 for(int j=0;j<fkeys.length;j++){
										 		//System.out.println("in for:"+j);
												String a = ((String)ds.getKValue(fkeys[j],fColumns[i]));
												//System.out.println(a.doubleValue()+" in for...");
												//System.out.println(a.compareTo(min)+" COMPARE CHAR");
												//System.out.println(a.compareTo(max)+" COMPARE CHAR");
												try{
													if (((min==null || a.compareTo(min)>=0) && (max==null || a.compareTo(max)<=0))){
													v.addElement(new Integer(fkeys[j]));
													//System.out.println(a+"Inserted Char");
													}
													}catch (NullPointerException e){
														v.addElement(new Integer(fkeys[j]));
														continue;
														};
												}
										//System.out.println(v.size()+" vector size");
										fkeys = new int[v.size()];
										for(int k = 0;k<v.size();k++){fkeys[k] = ((Integer)v.elementAt(k)).intValue();}
										//System.out.println("end "+i+"filtering");
										break;
										}
			case java.sql.Types.DATE:
			case java.sql.Types.TIMESTAMP:{java.util.Date min = (java.util.Date)condition[i][0];
										 if(GLOBAL.dstore_debug>0) System.out.println("Date");
										 java.util.Date max = (java.util.Date)condition[i][1];
										 //System.out.println(max+" =max "+min+" =min");
										 for(int j=0;j<fkeys.length;j++){
										 		//System.out.println("in for:"+j);
												java.util.Date a = ((java.util.Date)ds.getKValue(fkeys[j],fColumns[i]));
												//System.out.println(a.doubleValue()+" in for...");
												//System.out.println(a.compareTo(min)+" COMPARE CHAR");
												//System.out.println(a.compareTo(max)+" COMPARE CHAR");
												try{
													if (((min==null || a.after(min) || a.equals(min) ) && (max==null || a.before(max)  || a.equals(max) ))){
													v.addElement(new Integer(fkeys[j]));
													//System.out.println(a+"Inserted Char");
													}
													}catch (NullPointerException e){
														v.addElement(new Integer(fkeys[j]));
														continue;
														};
												}
										//System.out.println(v.size()+" vector size");
										fkeys = new int[v.size()];
										for(int k = 0;k<v.size();k++){fkeys[k] = ((Integer)v.elementAt(k)).intValue();}
										//System.out.println("end "+i+"filtering");
										break;
										}
			case java.sql.Types.OTHER: {System.out.println("dbi.DATASTORE.filter: Unknown type");}
			}

		}
		return fkeys;
	}
	/**
	*Порождает фильтр из датасторе fColumns-столбцы на которые накладываются условия
	*							contition -условия в виде массива структур вида (мин,мах)
	*/




	public DATASTORE createFilter(int[] fColumns,Object[][] condition){
		DATASTORE ds = null;
		if(parent == null) {ds=this;} else {ds = parent;};
		int[] fkeys = filter(fColumns,condition);
		//System.out.println(fkeys+" "+ds);
		DATASTORE rep = new DATASTORE(fkeys,ds);
		rep.setCondition(fColumns,condition);
		//System.out.println("dbi.DATASTORE.createFilter fColumns = "+fColumns+" condition = "+condition);
		filters.addElement(rep);
		return rep;
	};
	/**
	* склеивает много DATASTORE в одну и возвращает ее
	*/
	public DATASTORE dsConcat(DATASTORE[] ds,boolean scr){
		DATASTORE ret ;
		DATASTORE par;
		if (parent==null) par = this;
		else par = parent;
		if (scr) ret = new ScriptDs();
		else ret = new DATASTORE();
		ret.setSql(par.getSql());
		ret.tables = par.getTables();
		ret.types = par.getTypes();
		ret.colName = par.getNames();
		ret.colLabels = par.colLabels;
		ret.keys = new int[0];
		ret.skeys = new int[0];
		try{
			for(int i=0;i<ds.length;i++)
				for(int j=0;j<ds[i].getCountRows();j++){
					int nr = ret.newRow();
					for(int k=0;k<ds[i].getCountColumns();k++){
						ret.setValue(nr,k,ds[i].getValue(j,k));
					}
				}

		}catch(Exception e){System.out.println(e+"dbi.DATASTORE.dsConcat");}
		return ret;
	}
	/**
	* заполняет данное DATASTORE данными из другого DATASTORE
	* <br><b>может вызываться перед retrieve()</b>
	*/
	public void fromDs(DATASTORE source){
		Integer sourceColumn;
		Integer column;
		keys = new int[0];
		skeys = new int[0];
		tables();
		types = new int[colName.length];
		resolveLinks(strLinks,source);

		for(int i=0;i<source.getCountRows();i++){
			int nr = newRow();
			for(int j=0;j<source.getCountColumns();j++){
				sourceColumn = new Integer(j);
				column = (Integer)links.get(sourceColumn);
				setValue(nr,column.intValue(),source.getValue(i,sourceColumn.intValue()));
				types[column.intValue()] = source.getType(sourceColumn.intValue());
				}
			}
	}

	/**
	*делает текущей нулевую строк
	*/
	public void setFirst() {crow = 0;};
	/**
	* по индексу строки возвращает ее идентификато
	*/
	public int getRowKey(int index){return skeys[index];};
	/**
	*по идентификатору возвращает индекс строк
	*/
	public int getRowIndex(int key){
		for(int i=0;i<skeys.length;i++){
			if(skeys[i]==key) {return i;};
		};
		if (GLOBAL.dstore_debug>0)
			System.out.println("dbi.DATASTORE.getRowIndex Column with key"+key+" not found!!!");
		return -1;
		};
	/**
	*возвращает массив идентификаторо
	*/
	public int[] getKeyMap(){
		//int[] k = new int[skeys.length];
		//System.arraycopy(skeys,0,k,0,skeys.length);
		//System.out.println("k===="+k);
		return skeys;
		};
	/**
	* по имени столбца возвращает его номе
	*/
	public int getColumn(String colnam){
		for(int i = 0;i<colName.length;i++){
			if(colName[i].compareTo(colnam)==0) {return i;}
		}
		if (GLOBAL.dstore_debug>2)
			System.out.println("dbi.DATASTORE.getColumn Column "+colnam+" not found!!! try using colnam as number");
		try{	
			int i = Integer.parseInt(colnam);
			return i;
		}catch (Exception e){
			if (GLOBAL.dstore_debug>0)
				System.out.println("dbi.DATASTORE.getColumn Column "+colnam+" is not number");
		}
		
		return -1;
	}
	/**
	* по ключу  возвращает его позицию в массиве keys
	*/
	public int getPosition(int key){
		for(int i=0;i<keys.length;i++){
			if(keys[i]==key) {return i;};
		};
		if (GLOBAL.dstore_debug>0)
			System.out.println("dbi.DATASTORE.getPosition key"+key+" not found!!!");
		return -1;
	};
	boolean isTrue(Object obj){
		if (obj instanceof Double){
			if (((Double)obj).intValue()!=0) return true;
			else return false;
		}
		if (obj instanceof String){
			if (((String)obj).equals("TRUE")) return true;
			else return false;
		}
		return false;
	}

	//
	// Implementation for Calculator Methods
	//
	public Object method(String method,Object arg) throws Exception{
	    if (method.equals("ITERATOR") ){
			return new DSIterator(this);
		}else
		if (method.equals("SETVALUE")){
		    try{
		        Vector v = (Vector)arg;
		        setValue((String)v.elementAt(0),v.elementAt(1));
		        return new Double(0);
		        }catch (ClassCastException e){
		            throw new RTException("CastException","method SETVALUE must have"+
										  "two parameter, first compateable with String ");


		            }

		    }else

		if (method.equals("GETVALUE")){

		        if (arg instanceof String){
		            return getValue((String)arg);
		            }else
		        if (arg instanceof Double){
		            return getValue(((Double)arg).intValue());
		            }else throw new RTException("CastException","method GETVALUE must have"+
										  "one parameter,compateable with String or Numeric type");

		}else
		if (method.equals("RETRIEVE")){
			retrieve();
			return new Double(getCountRows());

		}else
		if (method.equals("SORT")){
		    try{
		        Vector v = (Vector)arg;
		        Object[] sort = (Object[])v.elementAt(0);
		        Object[] dim = (Object[])v.elementAt(1);
		        if (sort.length!=dim.length)
		        throw new RTException("Index Exception","method Sort must have"+
										  "two parameters compateable with Array type and with equals length");

		        int[] isort = new int[sort.length];
		        int[] idim = new int[dim.length];

		        for(int i=0;i<idim.length;i++){
		            idim[i] = ((Double)dim[i]).intValue();
		            isort[i] = ((Double)sort[i]).intValue();
		            }
		        setSort(isort,idim);
		        }catch (ClassCastException e){
		            throw new RTException("CastException","method Sort must have"+
										  "two parameters compateable with Array type and with equals length");

		        }catch (ArrayIndexOutOfBoundsException e1){
		            throw new RTException("Index Exception","method Sort must have"+
										  "two parameters compateable with Array type and with equals length");

		        }

			setSort();
			return new Double(0);

		}else
		if (method.equals("CLEARSORT")){
			resetSort();
			return new Double(0);

		}else
		if (method.equals("FILTER")){
				if (arg instanceof Quoted){
					Vector v1 = new Vector();
					int currow = getCurRow();
					for (int i=0;i<getCountRows();i++){
						setCurRow(i);
						if (isTrue(((OP)((Quoted)arg).getOP()).eval())) {
							v1.addElement(new Integer(i));
						}
					}
					int[] ret = new int[v1.size()];
					for (int i=0;i<v1.size();i++) ret[i] =
													 ((Integer)v1.elementAt(i)).intValue();
					setCurRow(currow);
					return this.createFilter_with_abs_keys(ret);
				}
			}else
			if (method.equals("SUM")){
					try{
						int col=-1000;
						double d=0;
						if (arg instanceof Double) col =
													   ((Double)arg).intValue();
						if (arg instanceof String) col =
													   getColumn((String)arg);
						if (col == -1000)
							throw new RTException("CastException","method Sum must have"+
										  "one parameter compateable with String or Number type");
						for (int i=0;i<getCountRows();i++){
							d+=((Double)getValue(i,col)).doubleValue();
						}
						return new Double(d);
					}catch(ClassCastException e){
						throw new RTException("CastException",
											  "method Sum must have one parameter ");
					}
					}else
					if (method.equals("SIZE")){
					    //System.out.println("size called:::size="+getCountRows());
						return new Double(getCountRows());
					}
					else
					if (method.equals("UPDATE")){
					    update();
					    return new Double(0);
					}else

					if (method.equals("NEWROW")){
					    return new Double(newRow());
					}else
					if (method.equals("CLEAR")){
					    clear();
					    return new Double(0);
					}else
					if (method.equals("SETSQL")){
					    if (arg instanceof String) {
					        setSql((String)arg);
					        return new Double(0);
					        }
					    else throw new RTException("CastException","method SetSql must have"+
										  "one parameter compateable with String type");

					}else
					if (method.equals("DELROW")){
					    if (arg instanceof Double) {
					        delRow(((Double)arg).intValue());
					        return new Double(0);
					        }
					    else throw new RTException("CastException","method DelRow must have"+
										  "one parameter compateable with Number type");

					}else
					if (method.equals("GETSQL")){
					    return new String(getSql());
					}else
					if (method.equals("GETCURROW")){
					    return new Double(getCurRow());
					}else
					if (method.equals("SETCURROW")){
					    if (arg instanceof Double) {
					        setCurRow(((Double)arg).intValue());
					        return new Double(0);
					        }
					    else throw new RTException("CastException","method SetCurRow must have"+
										  "one parameter compateable with Number type");

					}else
					if (method.equals("FASTSETCURROW")){
					    if (arg instanceof Double) {
					        crow = ((Double)arg).intValue();
					        return new Double(0);
					        }
					    else throw new RTException("CastException","method FastSetCurRow must have"+
										  "one parameter compateable with Number type");

					}else
					if (method.equals("NOTIFYVIEWS")){
					    if (handler!=null) handler.notifyHandler(null);
					    return new Double(0);
					    }else
					if (method.equals("CONCAT")||method.equals("CLONE")){
					    DATASTORE[] dss ;
					    if (arg == null) {
					        dss = new DATASTORE[1];
					        dss[0] = this;
					        return dsConcat(dss,true);
					        }
					    if ((arg instanceof Vector)){
					        dss = new DATASTORE[((Vector)arg).size()];
					        ((Vector)arg).copyInto(dss);
					        return dsConcat(dss,true);
					        }
					    else if (arg instanceof DATASTORE){
					        dss = new DATASTORE[1];
					        dss[0] = (DATASTORE)arg;
					        return dsConcat(dss,true);
					        }
					    }
					else
					if (method.equals("ADDCOLUMN")){
					    if (arg ==null) return addColumn(java.sql.Types.CHAR);
					    try{
					        String s = ((String)arg).toUpperCase();
					        if (s.equals("NUMERIC")) {return addColumn(java.sql.Types.DOUBLE);}
					        else if (s.equals("DATE")) {return addColumn(java.sql.Types.TIMESTAMP);}
					        else if (s.equals("STRING")) {return addColumn(java.sql.Types.CHAR);}
					        else {throw new RTException("Syntax","method ADDCOLUMN must have"+
										  "one parameter equals NUMERIC, DATE or STRING");}
					        }catch (ClassCastException e) {throw new RTException("CastException","method ADDCOLUMN must have"+
										  "one parameter compatible with String type");}
                                        }else if(method.equals("READOBJECT")){
                                              try{
                                                 //System.out.println("READOBJECT");
                                                 PackerRead pr = new PackerRead();
                                                 Object o = readObject((String)arg,pr);
                                                 return (String)o;
                                              }catch(Exception e){throw new RTException("RunTime",e.getMessage());}
                                        }else if(method.equals("SAVEOBJECT")){
                                              try{
                                                  Vector v = (Vector)arg;
                                                  PackerSave ps = new PackerSave(v.elementAt(1));
                                                  saveObject((String)v.elementAt(0),ps);
                                                  return new Double(0);
                                              }catch(Exception e){throw new RTException("RunTime",e.getMessage());}
					}else

		throw new RTException("HasNotMethod","method "+method+
										" not defined in class DATASTORE!");
		return new Double(0);
	};
	public String type(){
		return "DATASTORE";
	}

	protected void finalize() {
		System.out.println("+++ Datastore says: I gc'ed!;alias="+alias);
		System.out.println("Total memory:"+Runtime.getRuntime().totalMemory());
		System.out.println("Free memory:"+Runtime.getRuntime().freeMemory());
	}

}

