package dbi;
import java.util.*;
import java.sql.*;
import loader.*;

public class GroupReport extends DATASTORE implements DataTree{
int[][] groupCriteria;
int[][] directions;
int[] sort_columns;
int[] dir_columns;
String sortOrder;
Group root;
	int levelColumn,parentColumn,idColumn,nameColumn;
	boolean tree = false;
	String treeParam;
	String[] groups;
	String[] direction;
	int row;
	boolean putLast = false;
/**
* вызывается из rml формат:"column1,column2;column3,column4"
* "1,1;1,0;"
*/
public void setParameters(String grouping,String dir,String treeParam ) {
	//String[] groups;
	//String[] direction;
	if(GLOBAL.dstore_debug>1)
		System.out.println("dbi.GroupReport.setParameters :called "+treeParam);
	if (treeParam!=null){
		this.treeParam = treeParam.toUpperCase();
	}
	if (grouping == null && dir == null){
      //Обработка ситуации когда не заданы параметры группировк
	}else{
		StringTokenizer st1 = new StringTokenizer(grouping.toUpperCase(),";");
		StringTokenizer st2 = new StringTokenizer(dir,";");
		int countTokens = st1.countTokens();
		groups = new String[countTokens];
		direction = new String[countTokens];
		for (int i=0;i<countTokens;i++){
			groups[i] = st1.nextToken();
			direction[i] = st2.nextToken();
		}
	}
	//setGrouping(groups,direction);
}
/**
*Группировка задается в виде массива вида {"column1,column2","column3,column4"...}
*{"0,1","1,1"...}-параметры сортировк
*/
public void setGrouping(String[] groups,String[] direction){
	//super.tables();
	//super.retrieve();
	if(GLOBAL.dstore_debug>1)
				System.out.println("dbi.GroupReport.setGrouping : called");
	groupCriteria = new int[groups.length][];
	directions = new int[groups.length][];
	int i,k;
	int countToken;
	if (treeParam!=null){
		
		StringTokenizer st3 = new
			StringTokenizer(treeParam,";");
		
		if (st3.countTokens()!=4) {
			if(GLOBAL.dstore_debug>1)
				System.out.println("dbi.GroupReport.setParameters : bad TreeParam");
		}
		
		levelColumn = getColumn(st3.nextToken());
		
		parentColumn = getColumn(st3.nextToken());
		
		idColumn = getColumn(st3.nextToken());

		nameColumn = getColumn(st3.nextToken());
		//System.out.println("THIS is TREE!");
		tree = true;
		
		
	}
	
	for (i=0;i<groups.length;i++){
		StringTokenizer st = new StringTokenizer(groups[i],",");
		countToken = st.countTokens();
		groupCriteria[i] = new int[countToken];
		for (k=0;k<countToken;k++) {
			groupCriteria[i][k] = getColumn(st.nextToken());
			}
	}
	for (i=0;i<groups.length;i++){
		StringTokenizer st = new StringTokenizer(direction[i],",");
		countToken = st.countTokens();
		directions[i] = new int[countToken];
		for (k=0;k<countToken;k++) {
		directions[i][k] = new Integer(st.nextToken()).intValue();
		}
	}
	
}
public boolean eq(Object o1,Object o2,int type){
	switch (type) {
	case Types.NUMERIC:{if (((Double)o1).doubleValue()==((Double)o2).doubleValue()) {return true;} else {return false;}}
	case Types.CHAR:
	case Types.VARCHAR:{if (((String)o1).compareTo((String)o2)==0){return true;} else {return false;}}
	default :{System.out.println("dbi.GroupReport.eq: UNKNOWN TYPE FOR OPERATIONS!!!");}
	}
	return false;

}



	public void resolveTree( int lev){
	    //System.out.println("Calling Resolve Tree");
	    Vector st = new Vector();
	    root = new Group(0,0);
	    st.addElement(root);
	    int count = getCountRows();
	    //System.out.println("count rows = "+count);
	    for(int i=0;i<count;i++){
	        //System.out.println("performing row:"+i);
	        Group gr = new Group(i,i);
	        int level = ((Double)getValue(i,levelColumn)).intValue();
	        //System.out.println("level="+level+" size="+st.size());
	        if (st.size()==level) {
	            st.addElement(gr);
	            //System.out.println("added new level:"+level);
	            }
	        else {
	            st.insertElementAt(gr,level);
	            //System.out.println("added new group: level="+level);
	            }
	        ((Group)st.elementAt(level-1)).addChild(gr);
	    }
	   
		    
	}
	public Group[] resolveTree2(int level){
		int new_level = level;
		int old_level = level;
		Group gr = null;
		//row = begrow;
		boolean flag = true;
		boolean put = false;
		String msg = "rrr";
		//System.out.println("dbi.GroupReport.resolveTree called level="+level);
		Vector v = new Vector();
		try{
			while ((new_level!=old_level-1)&&(row<countRows-1)&&flag){
				//System.out.println("in while! "+level);
				gr = new Group(row,row);
				try{new_level =
						((Double)getValue(row+1,levelColumn)).intValue();
				}catch (ArrayIndexOutOfBoundsException e){
					gr=new Group(row+1,row+1);
					v.addElement(gr);
					System.out.println("333333333333333333333333333333333333333");
					throw e;
					
				}
			
			//int parent =
			//((Double)getValue(row,parentColumn)).intValue();
			//int id = ((Double)getValue(row,idColumn)).intValue();
			//gr = new Group(row,row);
			//System.out.println("level="+new_level+" parent="+parent+" id="+id +"row="+row);
				if(new_level==old_level+1){
					row++;
					//System.out.println("calling child this level="+new_level+" row="+row);
					gr.setSubgroups(resolveTree2(level+1));
					//v.addElement(gr);
					//new_level = old_level;
					put = true;
					try{new_level =
							((Double)getValue(row+1,levelColumn)).intValue();
					}catch (ArrayIndexOutOfBoundsException e) {
						throw new Exception("@@@@@@@@");
						//System.out.println("333333333333333333333333333333333333333");
						//v.addElement(new Group(row,row));
					}
					if (new_level<old_level) {
						v.addElement(gr);
						//	if (gr.begrow==69)
						//System.out.println("1I called! "+level+" begrow="+gr.begrow+"endrow="+gr.endrow);
						System.out.println("throw Exception!");
						throw new Exception("&&&&&&");
					}
					
				}
				
				if(new_level==old_level||put){
					v.addElement(gr);
					//if (gr.begrow==69)
					//System.out.println("2I called! "+level+" begrow="+gr.begrow+"endrow="+gr.endrow);
					//System.out.println("added new group; level="+new_level+"row="+row);
					row++;
					put = false;
				}
				else {
					flag = false;
					//System.out.println("added new group; level="+new_level+"row="+row);
					v.addElement(gr);
					put = false;
					//row++;
				};
			//row++;
			
			}
		}catch(Exception e) {
			//msg = e.getMessage();
			System.out.println(e+" "+level);
		}
		if (row == countRows-1) {
			//System.out.println("3I called! "+level+" begrow="+gr.begrow+"endrow="+gr.endrow+"countRows ="+countRows+" row ="+row);
			int size = v.size();
			if (((Group)v.elementAt(size-1)).begrow!=gr.begrow)
				{v.addElement(gr);}
			else {v.addElement(new Group(row,row));}
			putLast = true;
		}
		Group[] grps = new Group[v.size()];
		v.copyInto(grps);
		//System.out.println("dbi.Group.Report returning from resolve tree... v.size="+v.size());
		return grps;
	}
public Group[] resolveOneGroup(int level,Group group) {
	//строки ОТСОРТИРОВАНЫ
	if (level==groupCriteria.length) return null;
	int[] columns = new int[groupCriteria[level].length];
	columns = groupCriteria[level];
	Object[] n =  new Object[groupCriteria[level].length];
	int begrow = group.begrow;
	int endrow = group.endrow;
	Vector v = new Vector();
	int beg = begrow;
	int end = endrow;
	boolean newgroup = false;
	int i;
	//System.out.println("dbi.GroupReport.resolveOneGroup called; parent="+group+"begrow="+beg+"end row="+end);
	//int column = begcolumn;
	
	//Object o = getValue(begrow,begcolumn);
	for( i=0;i<columns.length;i++)  n[i] = getValue(begrow,columns[i]);
	for( i=begrow+1;i<=endrow;i++)
		for(int k=0;k<columns.length;k++){
			//Object n = getValue(i,columns[k]);
		if (!eq(n[k],getValue(i,columns[k]),getType(columns[k]))) {
			end = i-1;
			//System.out.println("dbi.GroupReport.resolveOneGroup new group added; parent="+group+"begrow="+beg+"end row="+end);
			Group gr = new Group(beg,end);
			gr.setReport(this);
			gr.setSubgroups(resolveOneGroup(level+1,gr));
			v.addElement(gr);
			beg = i;
			n[k] = getValue(i,columns[k]);
		}else continue;
		}
	Group gr = new Group(beg,endrow);
	gr.setReport(this);
	gr.setSubgroups(resolveOneGroup(level+1,gr));
	v.addElement(gr);
	//if (v.size() == 0) v.addElement(new Group(begrow,endrow));
	Group[] sub = new Group[v.size()];
	v.copyInto(sub);
	return sub;
}
public void resolveAllGroups(){
	//СНАЧАЛА СОРТИРОВКА!!!
	if(GLOBAL.dstore_debug>0)
		System.out.println("dbi.GroupReport.resolveAllGroups  calling");
	//retrieve();
	//if(GLOBAL.dstore_debug>0)
		//System.out.println("dbi.GroupReport.resolveAllGroups  calling 2");
	if (groups != null && direction != null){
		setGrouping(groups,direction);
		Vector v = new Vector();
		Vector v1 = new Vector();
		int i,k;
		for (i=0;i<groupCriteria.length;i++)
			for (k=0;k<groupCriteria[i].length;k++){ 
				v.addElement(new Integer(groupCriteria[i][k]));
				v1.addElement(new Integer(directions[i][k]));
			}
		int[] sort = new int[v.size()];
		int[] dim = new int[v1.size()];
		for (i=0;i<v.size();i++){
			sort[i] = ((Integer)v.elementAt(i)).intValue();
			dim[i] = ((Integer)v1.elementAt(i)).intValue();
		}
		if(GLOBAL.dstore_debug>0)
			System.out.println("dbi.GroupReport.setSort  keys="+keys+" skeys="+skeys);
		//if (!tree) setSort(sort,dim);
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if(!tree){
		
		    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    setSortOrder_real(sortOrder);
		    if((sort_columns!=null) && (dir_columns!=null)) {
		    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    int[] c = new int[sort.length+sort_columns.length];
		    int[] d = new int[sort.length+sort_columns.length];
		    //System.out.println("sort.length="+sort.length);
		    //System.out.println("sort_columns.length="+sort_columns.length);
		    System.arraycopy(sort,0,c,0,sort.length);
		    System.arraycopy(sort_columns,0,c,sort.length,sort_columns.length);
		
		    System.arraycopy(dim,0,d,0,dim.length);
		    System.arraycopy(dir_columns,0,d,dim.length,dir_columns.length);
		    //System.out.println("d.length="+d.length);
		    //System.out.println("c.length="+c.length);
		    
		    /*for (int o = 0;o<sort.length;o++){
		        System.out.println("sort["+o+"]="+sort[o]);
		        //System.out.println("d["+o+"]="+d[0]);
		        }*/
		    //for (int o = 0;o<c.length;o++){
		        //System.out.println("c["+o+"]="+c[o]);
		        //System.out.println("d["+o+"]="+d[0]);
		        //}
		    //for (int o = 0;o<c.length;o++){
		        //System.out.println("c["+o+"]="+c[0]);
		        //System.out.println("d["+o+"]="+d[o]);
		        //}
		        //try{Thread.currentThread().sleep(10000);}catch(Exception e){}
		    setSort(c,d);
		    
		}else setSort(sort,dim);
		}
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		try{
		    if(GLOBAL.dstore_debug>0)
			    System.out.println("dbi.GroupReport.setSort  calling");
		
		if (tree) {
			//root = new Group(0,0);
			row = 0;
			resolveTree(1);
		}else{
		    root = new Group(0,getCountRows()-1);
		    root.setReport(this);
			//System.out.println("root is"+root);
			root.setSubgroups(resolveOneGroup(0,root));
		}
		if(GLOBAL.dstore_debug>0)
			System.out.println("dbi.GroupReport.resolveAllGroups:: all groups maked!!! ");
		}catch(Exception e){System.out.println(e+" from group Report");}
	}else {
		root = new Group(0,getCountRows()-1);
		Group[] grs = new Group[this.getCountRows()];
		for (int l=0;l<this.getCountRows();l++){
			grs[l] = new Group(l,l);
		}
		root.setSubgroups(grs);
	}
	//System.out.println("---------------------------------------"+root.getSubgroups().length);
}
   public int retrieve() throws Exception{
		//if(GLOBAL.dstore_debug>0)
			//System.out.println("dbi.GroupReport.retrieve:: called ");
		int res = super.retrieve();
		resolveAllGroups();
		putLast = false;
		if(GLOBAL.dstore_debug>1) printAllGroups();
		return res;		
	}
/**
* rowid представляет из себя сложную систему навигации по группа
* ПРИМЕР- массив вида {1,0,0} щзначает дай нулевую строку нулевой подгруппы первой группы
*/
	public Object getGroupValue(int[] rowid,int column){
		Group[] sub = root.getSubgroups();
		for(int i=0;i<rowid.length-1;i++){
			sub = sub[rowid[i]].getSubgroups();
		}
		return getValue(sub[rowid[rowid.length-2]].begrow+rowid[rowid.length-1],column);
	}


	public Object getGroupValue(int[] rowid){
		Group[] sub = root.getSubgroups();		
		for(int i=0;i<rowid.length-1;i++){
			sub = sub[rowid[i]].getSubgroups();
		}
		if(!tree){
					return getValue(sub[rowid[rowid.length-1]].begrow,groupCriteria[rowid.length-1][0]);
				}else{
					return getValue(sub[rowid[rowid.length-1]].begrow,nameColumn);
				}
		//return getValue(sub[rowid[rowid.length-2]].begrow+rowid[rowid.length-1],column);
	}
	public int getGroupDimension(int[] gr_id){
		Group[] sub = root.getSubgroups();
		for(int i=0;i<gr_id.length;i++){
			sub = sub[gr_id[i]].getSubgroups();
		}
		return sub.length;
	}
	public Group getRoot(){return root;}

	public void printAllGroups(){
		printGroups(0,root);
	}
	public String getNode(String n_id){
	    System.out.println("We called :"+n_id);
		StringTokenizer st = new StringTokenizer(n_id,"/");
		int[] rowid = new int[st.countTokens()];
		//System.out.println("count="+st.countTokens());
		int count = st.countTokens();
		for(int i=0;i<count;i++){
			try{
				String s = st.nextToken();
				int pos = s.indexOf('#');
				if (pos!=-1) s = s.substring(0,pos);
				rowid[i] = Integer.parseInt(s);
				//System.out.println("rowid[i]="+rowid[i]);
			}catch (Exception e){System.out.println("dbi.GroupReport.getNode "+e);}
		}
		//System.out.println(rowid.length +"=length");
		try{
		Group[] sub = root.getSubgroups();
		for(int i=1;i<rowid.length;i++){
			sub=sub[rowid[i]].getSubgroups();
		}
		String ret="";
		for (int i=0;i<sub.length;i++){
			if (sub[i].getSubgroups()!=null){
				if(!tree){
					ret =
						ret+"N/"+getValue(sub[i].begrow,groupCriteria[rowid.length-1][0])+"/"+i+"#"+sub[i].begrow+"\n";
				}else{
					ret = ret+"N/"+getValue(sub[i].begrow,nameColumn)+"/"+i+"#"+sub[i].begrow+"\n";
				}
			}else{
				if(!tree){
					ret =
						ret+"P/"+getValue(sub[i].begrow,groupCriteria[rowid.length-1][0])+"/"+sub[i].begrow+"\n";
				}else{
					ret = ret+"P/"+getValue(sub[i].begrow,nameColumn)+"/"+sub[i].begrow+"\n";
				}
			}
		}
		//System.out.println("ret="+ret);
		return ret;
		}catch (Exception e) {return "P/"+"null"+"/"+"-1";}
	}
	public void printGroups(int level,Group gr){
		Group[] grps;
		String s = new String("");
		grps = gr.getSubgroups();
		for (int l=0;l<level;l++) s=s+" ";
		if (grps==null){
			for(int i=gr.begrow;i<=gr.endrow;i++){
				for(int j=0;j<getCountColumns();j++){
					System.out.print(s+" |"+getValue(i,j));
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------"+level);
		}else{
			for(int i=gr.begrow;i<=gr.endrow;i++){
				for(int j=0;j<getCountColumns();j++){
					System.out.print(s+" |"+getValue(i,j));
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------"+level);
			for (int k=0;k<grps.length;k++) printGroups(level+1,grps[k]);
		}
		//System.out.println();
	}
	public void setSortOrder(String SortOrder){
	    this.sortOrder = SortOrder;
	    }
	public void setSortOrder_real(String SortOrder){
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
	    this.sort_columns = sort_fields;
	    this.dir_columns = sort_dim;
	    }catch(Exception e){System.out.println(e+"in set SortOrder");}
	    };
	
}




