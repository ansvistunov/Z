
package views;
import rml.*;
import java.util.*;
import calc.objects.*;
import calc.*;

public class Group implements class_method, class_type, GlobalValuesObject{
    //Object[] children;
    ReportForm rHeader = null;
    ReportForm rTrailer = null;
    ReportGrid rGrid = null;
    Group group = null;
    Hashtable aliases = null;
    String alias = null;
    
    public dbi.Group currentGroup = null;//используется при вычислении групповых ф-ций
    public int curpos = -1; 
    
    Vector seq = null;//здесь будет хранится последовательность алиасов филдов 
                      //ReportHeader&ReportTrailer для вычисления значений этих филдов                      
    //dbi.DARTASTORE ds = null;
    
    public void setValue(Object o){}
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}
    
    public void setDatastore(dbi.DATASTORE ds) {
        if (rGrid!=null) rGrid.setDatastore(ds);
        if (rHeader!=null) rHeader.setDatastore(ds);
        if (rTrailer!=null) rTrailer.setDatastore(ds);
        if (group!=null) group.setDatastore(ds);
    }    
    
    
    public void setParent(Report parent) {
        if (rGrid!=null) rGrid.setParent(parent);
        if (group!=null) group.setParent(parent);
    }
    
    public void setCurPos(int pos) {
        curpos = pos;
        if (group!=null) group.setCurPos(pos);
    }
    
    public void createFonts(int a) {
        if (rGrid!=null) rGrid.createFonts(a);
        else if (group!=null) group.createFonts(a);
    }
    
    public void init(Proper prop, Hashtable aliases) {
        this.aliases = aliases;
        String sp;
        sp = (String)prop.get("ALIAS");
        alias = sp;
    }
    
    public void addChildren(Object[] objs){
        if (objs==null) return;
        if (objs.length == 0) return;
        int cindex = 0;
        //System.out.println("inside views.Group.addchildren");
        //children = new Object[objs.length];
        for (int i = 0;i<objs.length;i++) {            
            if (objs[i] instanceof REPORTHEADER) {
                rHeader = ((REPORTHEADER)objs[i]).getForm();
            }else
            if (objs[i] instanceof REPORTTRAILER) {
                rTrailer = ((REPORTTRAILER)objs[i]).getForm();
            }else
            if (objs[i] instanceof Group) {
                group = (Group)objs[i];
            }else
            if (objs[i] instanceof ReportGrid) {
                rGrid = (ReportGrid)objs[i];
            }            
        }
        if (rGrid!=null && group!=null) rGrid=null;
        //в нижеследующем коде получаем "магическую последовательность" 
        //для вычисления значений Computed Field'ов
        //if (true) return;
        if ((rHeader==null)&&(rTrailer==null)) return;
        Field[] hfs=null;//филды header'а
        Field[] tfs=null;//филды trailer'а
        //System.out.println("rHeader="+rHeader);
        if (rHeader!=null) hfs = rHeader.getFields();
        if (rTrailer!=null) tfs = rTrailer.getFields();
        if (tfs==null&&hfs==null) return;
        
        Vector names = new Vector();
        Vector Bn = new Vector();
        if (hfs!=null) 
            for (int i=0;i<hfs.length;i++) 
                if ((hfs[i]!=null)&&hfs[i].isComputed) {
                    names.addElement(hfs[i].getalias());
                    String[] exps = null;
                    try{
                        if (hfs[i].calc!=null) exps = hfs[i].calc.getAliases();                        
                    }catch(Exception e) {}
                    Vector bi = new Vector();
                    if (exps!=null) for (int j=0;j<exps.length;j++) {
                        //System.out.println("exps[j]="+exps[j]);
                        //System.out.println("hfs[i].getalias()="+hfs[i].getalias());
                        if (!exps[j].equals(hfs[i].getalias())) bi.addElement(exps[j]);
                    }
                    Bn.addElement(bi);
                }
        if (tfs!=null)
            for (int i=0;i<tfs.length;i++)
                if ((tfs[i]!=null)&&tfs[i].isComputed) {
                    names.addElement(tfs[i].getalias());
                    String[] exps = null;
                    try {
                        if (tfs[i].calc!=null) exps = tfs[i].calc.getAliases();
                    }catch(Exception e){}
                    Vector bi = new Vector();
                    if (exps!=null) for (int j=0;j<exps.length;j++) {
                        if (!exps[j].equals(tfs[i].getalias())) bi.addElement(exps[j]);
                    }
                    Bn.addElement(bi);
                }        
        if (names.size()==0) return;
        try {
            seq = UTIL.createSequence(names, Bn);//получаем "магическую последовательность" 
        }catch(Exception e) {
            System.out.println("~views.Group::addChildren : "+e);
        }
        //System.out.println("sequence="+seq);
    }
    
    public ReportGrid getGrid() {
        if (rGrid!=null) return rGrid;
        if (group!=null) return group.getGrid();
        else return null;
    }
    
    
    
    public Object method(String method,Object arg) throws Exception{	
		class help{
		    double summa = 0;
		    void obhod(dbi.Group dgr, views.Group vgr, int worklevel,int level, calc.Calc cc) {
        	    if (dgr == null) return;
        	    if (vgr == null) return;
        	    dbi.Group[] subgr = dgr.getSubgroups();
        	    if (worklevel==level) {//добрались до нужного уровня, суммируем значения
        	        if (vgr.rHeader!=null) vgr.rHeader.currentGroup = dgr;
        	        if (vgr.rTrailer!=null) vgr.rTrailer.currentGroup = dgr;	        
        	        try {        	            
                        Object[] objs = cc.eval(aliases);
                        if (objs!=null) {
                            Double d = (Double)objs[0];
                            summa+=d.doubleValue();                            
                        }
                            
        	        }catch(Exception e) {}        	        
        	        return;
        	    }
        	    if (subgr!=null) {
        	        for (int i=0;i<subgr.length;i++) {
        	            obhod(subgr[i], vgr.group, worklevel+1, level, cc);
        	        }
        	    }
        	}

		}//end of class help
		double summa = 0;
		help h = new help();
		
		if (method.equals("SUM")){
			if (arg instanceof String){
				Calc cc = new Calc((String)arg);				
				String[] names = null;
				try {
				    names = cc.getAliases();
				}catch(Exception e) {}
				if (names==null) {
				    //System.out.println("aliases in function SUM not found");
				    throw new RTException("Syntax","aliases in function SUM not found");
				}
				
			    int result = 0;
			    try {
			        result = find(this,names,0);
			    }catch(RTException e) {throw e;}			    
			    if (result==0){//все алиасы в текущей группе
			        Object[] objs = null;
			        try {
			            objs = cc.eval(aliases);
			        }catch(Exception e){throw new RTException("Any",e.getMessage());}
			        if (objs!=null) {
			            Double d = null;
			            try {
			                d = (Double)objs[0];
			            }catch(ClassCastException e) {throw new RTException("CastException",e.getMessage());}
			            return d;
			        }else return null;
			        
			    }
			    if (result==-1){//возможно, алиасы либо в гриде, либо в датасторе
			        if (currentGroup!=null) {
			            ReportGrid rg;
		                if ( (rg = getGrid()) != null) 
		                    if (rg.ds!=null){
		                        for (int i=currentGroup.begrow;i<=currentGroup.endrow;i++){
		                            rg.ds.setCurRow(i);		                            
		                            Object[] objs = cc.eval(aliases);
		                            //System.out.println("objs[0]="+objs[0]);
		                            if (objs!=null) {
		                                try{
		                                    Double d = (Double)objs[0];
		                                    summa+=d.doubleValue();
		                                }catch(ClassCastException e) {throw new RTException("",e.getMessage());}
		                            }
		                        }
		                        //System.out.println("summa="+summa);
		                        return new Double(summa);		                        
		                    }				                				                
			        }
			    }			
			    
			    if (result>0){//значиит, все names в одной группе views.Group;
			                  //организуем обход дерева для суммирования значений
			        //System.out.println("before calling obhod, result="+result);
			        //System.out.println("arguments = "+(String)arg);
			        h.obhod(currentGroup, this, 0, result, cc);
                    //System.out.println("after calling obhod");
			        //System.out.println("summa after obhod = " + h.summa);
			        return new Double(h.summa);			        
			    }			    
		    }		
	    }
	    if (method.equals("CURRENTROW")) {		    
		    return new Double(curpos);
		}
		throw new RTException("HasNotMethod","method "+method+
										" not defined in class views.Group!");
	    //return null;
	    //return new Double(0);
	}
	
	int find(views.Group vgr, String[] names, int level) throws RTException{
	    if (names==null) return level;
	    boolean inhead = true;
	    boolean intrail = true;
	    boolean res = true; 
	    for (int i=0;i<names.length;i++) {
	        if (vgr.rHeader!=null&&vgr.rHeader.fields!=null&&vgr.rHeader.fields.get(names[i])!=null)
	            inhead=true;
	        else inhead=false;
	        
	        if (vgr.rTrailer!=null&&vgr.rTrailer.fields!=null&&vgr.rTrailer.fields.get(names[i])!=null) 
	            intrail = true;
	        else intrail=false;
	        
	        if (i==0) res = inhead||intrail;
	        else if (inhead||intrail!=res) throw new RTException("Syntax","Aliases must be in same level!");
	    }
	    if (res){//значит, все алиасы функции SUM принадлежат группе vgr
	        return level;
	    }else{
	        if (vgr.group!=null) return find(vgr.group, names,level+1);
	        else return -1;//говорит о том ,что ни в одной из подгрупп данной группы
	                       //филдов с алиасами names не обнаружено	            
	    }

 
	}
	
	
	
	public String type(){
		return "VIEWS_GROUP";
	}
}
