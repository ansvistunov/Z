package dbi;
import java.util.*;
public class GrossTab extends DATASTORE {
    int rowCr;
    int colCr;
    int dataCr;
    int eval = 1;
    Vector rowData = new Vector();
    Vector colData = new Vector();
    Hashtable groupData = new Hashtable();
    public void build(){
        //System.out.println("we in build!!!!!"+super.getCountRows());
        for (int i=0;i<super.getCountRows();i++){
            System.out.println("i="+i);
            Object rd = super.getValue(i,rowCr).toString().trim();
            Object cd = super.getValue(i,colCr).toString().trim();
            System.out.println(rd+" "+cd);
            Object data = super.getValue(i,dataCr);
            if (data == null) data = new Double(0);
            if (!rowData.contains(rd)) rowData.addElement(rd);
            if (!colData.contains(cd)) colData.addElement(cd);
            String key = rd+"#"+cd;
            //System.out.println();
            //System.out.println("data="+data);
            Object mas = groupData.get(key);
            try{
            if (mas == null) {
                Vector array = new Vector();
                array.addElement(data);
                groupData.put(key,array);
            }else {
                ((Vector)mas).addElement(data);    
            }
            }catch (Exception e) {System.out.println(e+"$$$$$$$$$$");}
        }
        int compSize = 0;
			if (computeColumn!=null) compSize=computeColumn.size();
        countRows = rowData.size();
        int tpr = getType(rowCr);
        int tpc = getType(colCr);
        int tpd = getType(dataCr);
        int countColumns = colData.size()+1;
        types = new int[countColumns+compSize]; 
		colName = new String[countColumns+compSize]; 
		for (int i=0;i<types.length;i++) types[i] = java.sql.Types.DOUBLE; types[0] = tpr;
		colName[0] = "******";
		for (int j=0;j<colData.size();j++) colName[j+1] = colData.elementAt(j).toString();//colName[0] = "------";
		//for (int l = 0;l<colData.size();l++)System.out.println(colData.elementAt(l));
        
        
    }
    public void setParameters(Object rc,Object cc,Object dc,Object evl){
        //System.out.println("setParamere called:"+rc+"_"+cc+"_"+dc);
        try{
            rowCr = ((Integer)rc).intValue();
            colCr = ((Integer)cc).intValue();
            dataCr = ((Integer)dc).intValue();
            eval = ((Integer)evl).intValue();
        }catch(Exception e){System.out.println(e+" in GrossTab MUST be set parameters: \n rowcondition,columncondition,datacondition");}
        //System.out.println("row="+rowCr+" col="+colCr+" dat="+dataCr);
        }
    public int retrieve(){
        //System.out.println("retrieve in grosstab called");
        try{
            colData.removeAllElements();
            rowData.removeAllElements();
            groupData.clear();
            super.retrieve();
        }catch(Exception e){System.out.println("exception in grosstab.retireve:"+e);}
        //System.out.println("after retrieve");
        build();
        //System.out.println("after build");
        eval(eval);
        //System.out.println("after eval");
        return 1;
        }
    public void eval(int eval){
        switch (eval){
        case 1:do_summ();break;
        case 2:do_count();break;
        default:do_count();
        } 
    }
    public void do_summ(){
        data = new Vector();
        for (int row = 0; row<rowData.size(); row++){
            Vector row_vector = new Vector();
            row_vector.addElement(rowData.elementAt(row));
            for (int col = 0; col<colData.size(); col++){
                String key = rowData.elementAt(row).toString().trim()+"#"+colData.elementAt(col).toString().trim();
                //System.out.println("key="+key);
                Vector v = (Vector)groupData.get(key);
                double summ = 0;
                if (v != null) {
                        for (int i=0;i<v.size();i++){
                            summ = summ + ((Double)v.elementAt(i)).doubleValue();
                        }
                }
                row_vector.addElement(new Double(summ));
            }
            Object[] obj = new Object[row_vector.size()];
            row_vector.copyInto(obj);
            data.addElement(obj);
        }
       groupData = new Hashtable(); 
    }
    public void do_count(){
        data = new Vector();
        for (int row = 0; row<rowData.size(); row++){
            Vector row_vector = new Vector();
            row_vector.addElement(rowData.elementAt(row));
            for (int col = 0; col<colData.size(); col++){
                Vector v = (Vector)groupData.get(rowData.elementAt(row)+"#"+colData.elementAt(col));
                if (v!=null) row_vector.addElement(new Double(v.size()));
                else row_vector.addElement(new Double(0));
                
            }
            Object[] obj = new Object[row_vector.size()];
            row_vector.copyInto(obj);
            data.addElement(obj);
        }
        groupData = new Hashtable();
    }
    
}