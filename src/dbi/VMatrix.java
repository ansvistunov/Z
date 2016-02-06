package dbi;
public class VMatrix {
	DATASTORE ds;
	int[] columns;
	int[] directions;
	public VMatrix(DATASTORE ds,int[] columns,int[] directions){
		this.ds = ds;
		this.columns = columns;
		this.directions = directions;
		/*for(int i=0;i<columns.length;i++){
			System.out.println("dbi.VMatrix column[i]="+columns[i]);
			System.out.println("dbi.VMatrix directions[i]="+directions[i]);
			}*/
	}
    public Object get(int rows, int cols){
		return ds.getValue(rows,columns[cols]);
	}
	public int getType(int col) { return ds.getType(columns[col]);}
	public int[] getKeys() {return ds.getKeyMap();}
	public int[] getDirections(){return directions;}
}
