package dbi;

public interface DataTree{
        public int getGroupDimension(int[] rowid);
        public Object getGroupValue(int[] rowid);
        public Group getRoot();
        public void resolveAllGroups();

}