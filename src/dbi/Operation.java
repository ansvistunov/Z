
/*
 * File: Operation.java
 *
 * Created: Fri Apr  9 13:16:09 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */
package dbi;
import java.util.*;
//import lisp.*;
import calc.*;

public class Operation{
	public static final int Insert = 0;
	public static final int Update = 1;
	public static final int Delete = 2;
	String parent;
	String[][] depends;
	String[][] whe;
	int op_id;
	Hashtable aliases;
	public int getOpid(){return this.op_id;}
	public void setParameters(Hashtable aliases,String parent,String
							  where,String dep,int op_id){
		//System.out.println("dbi.Operation.setParameters:: parent="+parent+" where="+where+" dep="+dep);
		this.parent = parent;
		this.op_id = op_id;
		this.aliases = aliases;
		StringTokenizer st3 = null;
		StringTokenizer st = null;
		int countToken = 0;
		if (dep!=null) {
			st = new StringTokenizer(dep.toUpperCase(),";");
			countToken = st.countTokens();
			depends = new String[countToken][2];
		}
		if (where!=null) {st3 = new
							  StringTokenizer(where.toUpperCase(),";");}
		try{
			if(dep!=null){
				for (int i=0;i<countToken;i++) {
					String s = st.nextToken();
					//System.out.println("dbi.Operation.setParameters:: s="+s);
					StringTokenizer st2 = new StringTokenizer(s,"=");
					depends[i][0] = st2.nextToken().trim();
				 //System.out.println("dbi.Operation.setParameters::depends[i][0]="+depends[i][0]);
					depends[i][1] = st2.nextToken().trim();
				//System.out.println("dbi.Operation.setParameters::depends[i][1]="+depends[i][1]);
				}
			}
			if (where!=null){
				//System.out.println("dbi.Operation.setParameters:: in whe clause");
				countToken = st3.countTokens();
				whe = new String[countToken][2];
				for (int i=0;i<countToken;i++) {
					//String s = st.nextToken();
					//System.out.println("dbi.Operation.setParameters:: in whe clause s="+s);
					StringTokenizer st4 = new StringTokenizer(st3.nextToken(),"=");
					whe[i][0] = st4.nextToken().trim();
					whe[i][1] = st4.nextToken().trim();
					
				}
			}
		}catch (Exception e){
			System.out.println("dbi.Operation.setParameters:: bad Parameters!"+e);
			System.out.println("dbi.Operation.setParameters:: badParameters:dep="+dep+" count="+countToken);		

				}
	}
	public String getParent(){return parent;}
	public void doAction(DATASTORE db,int row){
		int i,j;
		switch(op_id){
		case Insert:{
			int newrow = db.newRow();
			System.out.println("dbi.Operation.doAction : INSERTED NEW ROW id="+newrow);
			/*for (i=0;i<depends.length;i++){
				Object o = Lisp.macro(depends[i][1],aliases);
				db.setValue(newrow,db.getColumn(depends[i][0]),o);
				}*/
			break;
		}
		case Update:{
			int[] rows;
			DATASTORE dbp = (DATASTORE)aliases.get(parent);
			dbp.setCurRow(db.getRowIndex(row));
			System.out.println("dbi.Operation.doAction : Setting Current Row:id="+row);
			//получили обьект в котором собственно и произошли изменения
			//System.out.println("dbi.Operation.doAction : "+
			//				   " whe[0][1] "+whe[0][1]);
			Object o=null;
			try{
				o = Calc.macro(whe[0][1],aliases);
			}catch(Exception e1){e1.printStackTrace();}

			// правая часть whe-выражения типа ~<alias>.field~ вот мы
			// и получили значение данного поля

			//System.out.println("dbi.Operation.doAction : o="+o+
			//				   " whe[0][1] "+whe[0][1]);
			rows=db.findElement(o,db.getColumn(whe[0][0]),0);
			System.out.println("rows="+rows+" rows length="+rows.length);
			//for(i=0;i<rows.length;i++) {System.out.println(rows[i]);}  ;
			//rows = findRow(rows,1,db);
			//НЕ забыть раскоментарить эту строчку!!!!
			for (i=0;i<rows.length;i++){
				for (j=0;j<depends.length;j++){
					Object val = null;
					try{
						val =  Calc.macro(depends[j][1],aliases);
					}catch(Exception e1){e1.printStackTrace();}	
					db.setValue(rows[i],db.getColumn(depends[j][0]),val);
					System.out.println("dbi.Operation.doAction : SETTING VALUE !!! "+val);
				}
			}
			break;
		}
		case Delete:{
			int[] rows;
			//System.out.println("in delete:whe[0][1]= "+whe);
			Object o = null;
			try{
				o = Calc.macro(whe[0][1],aliases);
			}catch(Exception e1){e1.printStackTrace();}	
			rows=db.findElement(o,db.getColumn(whe[0][0]),0);
			//rows = findRow(rows,1,db);
			//НЕ забыть раскоментарить эту строчку!!!!
			for (i=0;i<rows.length;i++){
				System.out.println("dbi.Operation.doAction : DELETING ROW!!! "+rows[i]);
				db.delRowForKey(db.getRowKey(rows[i]),rows[i]);
			}
		}
		break;

		}
	}
	
	public int[] findRow(int[] rows,int level,DATASTORE db){
		Vector v = new Vector();
		for (int i=0;i<rows.length;i++){
			Object o = null;
			try{
				o = Calc.macro(whe[i][1],aliases);
			}catch(Exception e1){e1.printStackTrace();}	
			if (db.getValue(rows[i],db.getColumn(whe[level][0])).equals(o)){
				v.addElement(new Integer(rows[i]));
			}
		}
		int[] ret = new int[v.size()];
		for (int k=0;k<v.size();k++){
			ret[k] = ((Integer)v.elementAt(k)).intValue();
		}
		if (level==whe.length) {return ret;}
		else {
			return findRow(ret,level+1,db);
		}
		
	}

}
