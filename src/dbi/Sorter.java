 
package dbi;
import dbi.*; 
import java.sql.*; 
import java.math.*;
import loader.*;
public class Sorter {  
    private int x,y,j,k,n; 
    public static final int ASC=1; 
    public static final int DESC=-1; 
    private int[] directions; 
    private int[] keys; 
    private VMatrix vm; 
    private int cols; 
    public Sorter(VMatrix vm) {
        //System.out.println("Sorter created!");
        if (vm!=null) this.vm = vm; else { 
            System.out.println("dbi.Sorter.Sorter: VMatrix is null!"); 
            return; 
        } 
         
		this.keys = vm.getKeys();
        if (keys==null) {
            System.out.println("dbi.Sorter.Sorter: keys is null!"); 
            return; 
        } 
		this.directions = vm.getDirections();
        if (directions==null){ 
            System.out.println("dbi.Sorter.Sorter: directions is null!"); 
            return; 
        } 
         
        n = keys.length; 
        cols = directions.length;
		//System.out.println("cols= "+cols); 
		
		 
		} 
 
    public int[] getSortedArray() {
		if(GLOBAL.dstore_debug>0)
				System.out.println("dbi.Sorter.getSortedArray  called");
        if (vm == null) return null; 
        else if (n == 0) return keys; 
        k=n; 
        for (j = n/2; j>=1; j--) { 
            x=j; 
            pros(); 
        } 
        for (k = n-1; k>=1; k--) { 
            swap(keys,1-1,k+1-1); 
            x=1; 
            pros(); 
        }		
        return keys; 
    } 
 
    private void pros() { 
        while(true) { 
            y=x+x; 
            switch(sign(y-k)+2) { 
                case 1: {if ( compareRows(y-1,y+1-1,directions)<0 ) y++;} 
                case 2: {if ( compareRows(x-1,y-1,directions)>=0) return; 
                         swap(keys,x-1,y-1);x=y;break;} 
                case 3: {return;} 
            } 
        } 
    } 
 
    private int sign(int x) { 
        if (x == 0) return 0; 
        return x<0?-1:1; 
    } 
 
    private void swap(int[] s, int x, int y) {         
		int t = s[x]; 
        s[x] = s[y]; 
        s[y] = t; 
    } 
 
    public int compareRows(int r1, int r2, int[] directions) {
		//System.out.println("dbi.Sorter.compareRows :: r1="+r1+" r2="+r2);
		try{
			for (int i = 0;i<cols;i++) { 
            int ret =
				compareTo(vm.get(r1,i),vm.get(r2,i),vm.getType(i));
            if ( (ret != 0) || (i == cols-1) ) return ret*directions[i]; 
			}
		}catch(Exception e){/*System.out.println(e+"from dbi.Sorter.compareRows");*/}
		

        return 0; 
 
    } 
    public int compareTo(Object o1, Object o2, int type) {
		//System.out.println("dbi.Sorter.compareTo :: o1="+o1+" o2="+o2);
		try{
        switch(type) { 
            case Types.NUMERIC: 
            case Types.INTEGER: 
            case Types.FLOAT: 
            case Types.REAL: 
			{//if(GLOBAL.dstore_debug>0) System.out.println("dbi.Sorter.compareTo :: DOUBLE");
				if (((Double)o1).doubleValue()==((Double)o2).doubleValue()) return 0;
			   if(((Double)o1).doubleValue()<((Double)o2).doubleValue()) {return -1;} else {return 1;}} 
            case Types.CHAR: 
		    case Types.VARCHAR:
		    case -8 : {
			//if(GLOBAL.dstore_debug>0) System.out.println("dbi.Sorter.compareTo :: String");
			return ((String)o1).compareTo((String)o2);
		} 
            case Types.DATE:
            case Types.TIMESTAMP:
              {java.util.Date d1 = (java.util.Date)o1; 
               java.util.Date d2 = (java.util.Date)o2; 
               if (d1.equals(d2)) return 0; 
               if (d1.before(d2)) return -1; else return 1; 
              }
			  //System.out.println("dbi.Sorter.compareTo :: return");
            default: { System.out.println("UNKNOWN TYPE!!!");return 0;} 
        } 
		}catch(NullPointerException e){
					//System.out.println("dbi.Sorter::compareTo "+e.getMessage());
					if(o1==null) {return -1;}else {return 1;} 
		}catch (Exception e1){
		    System.out.println("dbi.Sorter.CompareTo:"+e1);
		    return 0;
		    }
 
    } 
 
} 
 
 
