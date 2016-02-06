package views;

    public class FilterStruct implements java.io.Serializable{
        public int sortOrder=0;
        public boolean sort=false;
        public boolean filter=false;
        public Object minValue;
        public Object maxValue;
    }