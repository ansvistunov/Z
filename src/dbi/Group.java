
/*
 * File: Group.java
 *
 * Created: Wed Apr  7 10:10:01 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Swistunov */
package dbi;
import calc.objects.*;
import calc.*;
import java.util.*;

public class Group implements class_type,class_method,GlobalValuesObject{
	public int begrow;
	public int endrow;
	Group[] subgroups;
        //GroupReport rep;
        DATASTORE rep;
	Hashtable hash;
	public Group[] getSubgroups(){
		return subgroups;
	}
	public Group(int beg,int end){
		this.begrow = beg;
		this.endrow = end;
	}
        //public void setReport(GroupReport rep){
        public void setReport(DATASTORE rep){
		this.rep = rep;
	}
	public void addChild(Group child){
	    if (subgroups==null) {
	        subgroups = new Group[1];
	        subgroups[0] = child;
	    }else{
	        int size = subgroups.length;
	        Group[] grs = new Group[size+1];
	        for (int i=0;i<size;i++){
	            grs[i] = subgroups[i];
	            }
	        grs[size] = child;
	        subgroups = grs;
	    }
	    }
	public void setSubgroups(Group[] groups){
		this.subgroups = groups;
		//System.out.println("dbi.Group.setSubgroups called!!! for"+this);
	}
	Double sum(String col) throws Exception{
		double d = 0;
		try{
			if (hash.containsKey(col)) return (Double)(hash.get(col)); // �����
			                                                          // �� ��������
			if (rep.getColumn(col) != -1){ //���� ��� ����� �� ���� �������
				for(int i=begrow;i<=endrow;i++)
					d+=((Double)rep.getValue(i,col)).doubleValue();
				return new Double(d);
			}
			else {
				for (int i=0;i<subgroups.length;i++){
					d = d+subgroups[i].sum(col).doubleValue();
				}
				return new Double(d);
			}
		}catch(ClassCastException e) {
			throw new RTException("CastException","method Sum must can perform only numeric fields!");
		}
	}
   
	public void addField(String field){
		if (hash == null) hash = new Hashtable();
		hash.put(field,(Object)(new int[1]));
	}

	//////////////////////////GlobalValueObject functions ////////////////
	/**
	* ���������� ���������� GlobalValueObject
	*/
	public void setValue(Object obj){};
	/**
	* ���������� ���������� GlobalValueObject
	*/
    public Object getValue(){return this;};
	/**
	* ���������� ���������� GlobalValueObject
	*/
    public void setValueByName(String name, Object obj){
		if (hash.containsKey(name)) hash.put(name,obj);
	};
	/**
	* ���������� ���������� GlobalValueObject
	*/
    public Object getValueByName(String name){
		Object o = hash.get(name);
		if ((o == null) || (o instanceof int[])) return null;
		else return o;
	};
	
	/////////////////////////////////////////////////////////////////////
	public String type(){return "GROUP";}
	public Object method(String method,Object arg) throws Exception{
		if (method.equals("ITERATOR")){
                        return new GroupIterator((GroupReport)rep,this) ;
		} else if(method.equals("SUM")){
			//���������� ����� �� ������� ����
			try{
				return sum((String)arg);
			}catch(ClassCastException e){
					throw new RTException("CastException","method Sum must have one parameter"
										  +"compateable with String type");
			}
		}else if (method.equals("SIZE")){
			return new Double(endrow-begrow+1);
		}

		throw new RTException("HasNotMethod","method "+method+" not defined in class Group!");
	}
}
