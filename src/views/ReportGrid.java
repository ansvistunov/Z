
package views;
import java.awt.*;
import java.util.*;
import rml.Proper;

public class ReportGrid extends Component {

    int sizeRow = 20;
    int left = 1;//смещение грида по оси X(<1 нельзя!)
    int top = 0;//смещение грида по оси Y
    dbi.DATASTORE ds;
    Column[] columns;
    int[] helpArray;
    int[] calcArray;
    int offset=0;//смещение в пикселах для текущей страницы,
                //по которому будет идти отрисовка строк ReportGrid
    //int numRows = 0;
    public int beginRow = 0;//начальная строка в Datastore - источнике данных
    public int endRow = 0;//конечная строка в Datastore - источнике данных

    //Group group=null;//группа, к которой принадлежит даннный ReportGrid
    //Dimension freePageSize = null;

    int freeHeight = 0;
    //Размер свободной части страницы
    //(часть пространства может быть занята, например, колонтитулами),
    //в которой производится отрисовка
    boolean drawIt = false;
    boolean isPrint = false;

    //определяет, каким образом будет нарисована сетка в гриде
    //0 бит (1) - обрамляющий Rectangle
    //1 бит (2) - вертикальные лин
    //2 бит (4) - горизонтальные лин
    int drawGrid = 0;
    Report parent = null;

    Font[] fonts = null;
    Hashtable aliases = null;

    public void init(Proper prop, Hashtable aliases) {
        Integer ip = null;
        this.aliases = aliases;
        ip = (Integer)prop.get("ROWSIZE");
        if (ip!=null) sizeRow = ip.intValue();

        ip = (Integer)prop.get("DRAWGRID");
        if (ip!=null) drawGrid = ip.intValue();

        ip = (Integer)prop.get("LEFT");
        if (ip!=null) left = ip.intValue();

        ip = (Integer)prop.get("TOP");
        if (ip!=null) top = ip.intValue();
    }

    public void setParent(Report parent) {
        this.parent = parent;
    }

    public void addChildren(Object[] objs) {
        int cc=0;
        int vc=0;
        try {
            for (int i=0;i<objs.length;i++) {
                if (objs[i]==null)
                 throw new Error("~views.ReportGrid::addChildren : Object views.Grid cannot be created!");
                //if (objs[i] instanceof dbi.GroupReport) {
                //    ds = (dbi.GroupReport)objs[i];
                //}
                if (objs[i] instanceof views.Column) {
                    cc++;
                    if (((Column)objs[i]).isVisible()) vc++;
                }
            }
            columns = new Column[cc];
            helpArray = new int[vc];
            cc=0;vc=0;
            for (int i = 0;i<objs.length;i++) {
                if (objs[i] instanceof views.Column) {
                    columns[cc] = (Column)objs[i];
                    columns[cc].setParent(this);
                    if (columns[cc].isVisible()) {
                        helpArray[vc] = cc;
                        vc++;
                    }
                    cc++;
                }
            }

            createCalcSequence();
        }
        catch(Exception e) {
            System.out.println("~views.Grid::addChildren : "+e);
        }
        fonts = new Font[helpArray.length];
        createFonts(100);
    }

    public void createFonts(int a){
        for (int i=0;i<helpArray.length;i++) {
            Font tmp = getVColumn(i).font;
            fonts[i] = new Font(tmp.getName(), tmp.getStyle(), tmp.getSize()*a/100);
        }
    }

    public Column getVColumn(int i) {
        try{
            return columns[helpArray[i]];
        }
        catch(Exception e) {
            return null;
        }
    }

    public int[] getOutPoint(Column col, int x, int y, String str) {
        int xp,yp;
        int width = col.size;
        int height = sizeRow;
        if (col==null) return null;
        //FontMetrics fm = new FontMetrics(col.font);
        int sw = col.fm.stringWidth(str);
        int sh = col.fm.getHeight()-col.fm.getDescent();
        int desc = 0;//col.fm.getDescent();
        int wwidth = width - 2*col.dw;
        int wheight = height - 2*col.dh;
        if (col.halignment.equals("LEFT")) {
            xp = x+col.dw;
        }else
        if (col.halignment.equals("RIGHT")) {
            xp = x+ col.dw + wwidth - sw;
        }else
        if (col.halignment.equals("CENTER")) {
            xp = x + col.dw + (wwidth - sw)/2;
        }
        else xp = x+col.dw;

        if (col.valignment.equals("BOTTOM")) {
            yp = y+col.dh+wheight-desc;
        }else
        if (col.valignment.equals("TOP")) {
            yp = y + col.dh + sh - desc;
        }else
        if (col.valignment.equals("CENTER")) {
            yp = y + col.dh+sh+(wheight-sh)/2 - desc;
        }
        else yp = y+col.dh+wheight-desc;

        int[] ret=new int[2];
        ret[0] = xp-1;
        ret[1] = yp;
        return ret;
    }

    public int getNumRows(){return endRow - beginRow +1;}

    public void setDatastore(dbi.DATASTORE ds) {
        this.ds = ds;
        for (int i =0; i<columns.length;i++) {
            if (columns[i].target==null) {
                if (columns[i].getType() == Integer.MIN_VALUE) {
                    System.out.println("views.Grid.addChildren says : type for computed column not defined!");
                    continue;
                }
                columns[i].target = ds.addColumn(columns[i].getType());
            }
        }
    }

    public dbi.DATASTORE getDATASTORE(){return ds;}

    public void setFreeHeight(int height) {
        this.freeHeight = height;
    }

    public int getFreeHeight(){return freeHeight;}

    //рисует строки ReportGrid'а в данном графическом контексте,
    //отображенном на текущую страницу.
    //Возвращает количество строк, которые уместились на стран

    public int drawRows(Graphics g, int a) {
        int drawedRows = 0;
        //System.out.println("inside drawRows");
        //System.out.println("drawIt="+drawIt);
        //System.out.println("freeHeight="+freeHeight);
        //System.out.println("offset="+offset);
        if (drawGrid!=0) freeHeight--;
        //freeHeight-=top;
        drawedRows = Math.min(freeHeight / sizeRow, endRow-beginRow+1);
        if (!drawIt || (a<=0) || drawedRows==0) {
            //freeHeight-=drawedRows*sizeRow;
            return drawedRows;
        }
        int colw = getColumnsSize();
        SmartLine line = new SmartLine(g);
        if (parent.isPrint) {
            line.isPrint = true;
        }
        else {
            line.isPrint = false;
        }
        //createFonts(a);
        if ((drawGrid&2)!=0)
            for (int i=0;i<helpArray.length;i++) {
                int x = getColumnX(i);
                //g.setClip(x+left,offset,2,drawedRows*sizeRow);
                g.setColor(Color.black);
                line.setType(1);
                line.draw(x+left,offset,drawedRows*sizeRow/*+1*/, a);//(!отрисовка с масштабированием!)
            }
        if ((drawGrid&1)!=0) {
            g.setColor(Color.black);
            line.setType(0);
            line.draw(left,offset,getColumnsSize(), a);//(!отрисовка с масштабированием!)
            line.draw(left,offset+drawedRows*sizeRow,getColumnsSize(), a);
            line.setType(1);
            line.draw(left,offset, drawedRows*sizeRow, a);//(!отрисовка с масштабированием!)
            line.draw(left+getColumnsSize(),offset,drawedRows*sizeRow+1, a);//(!отрисовка с масштабированием!)
            //g.drawRect(left, offset, getColumnsSize(), drawedRows*sizeRow);
        }

        for (int i = beginRow; i <= endRow; i++) {
            int y = getRowY(i-beginRow);
            if ((drawGrid&4) !=0 ) {
                g.setColor(Color.black);
                //g.setClip(0+left,offset+y,colw+1,2);
                line.setType(0);
                line.draw(0+left,offset+y,colw, a);//(!отрисовка с масштабированием!)
            }
            if (sizeRow>freeHeight) return (i-beginRow);
            freeHeight-=sizeRow;
            for (int j=0;j<helpArray.length;j++) {
                g.setFont(fonts[j]);
                int x = getColumnX(j);
                //x-=delta;
                int height = sizeRow;
                int width = getVColumn(j).size;
                String str = getSourceText(i,j);
                Column col = getVColumn(j);
                int cdw = col.dw;
                if (col.halignment.equals("RIGHT")) cdw=cdw*5/2;
                int[] xy = UTIL.getOutPoint(col.size,sizeRow,col.fm,
                col.halignment,col.valignment,cdw,col.dh,x+left,offset+y,str);

                g.setClip((left+x+1)*a/100, (offset+y+1)*a/100, (width-1)*a/100, (height-1)*a/100);
                g.setColor(Color.black);
                
                if (col.multiline) {//нужно распарсить строки и сделать выравнивание
                	    String svalue1;
                        if (col.wordwrap) {
                            svalue1 = UTIL.makeWrap(str," ",width-cdw-3 ,col.fm);
                        }else svalue1 = str;
                        StringTokenizer st = new StringTokenizer(svalue1, "\n", true);
                        int cnt = st.countTokens();//кол-во строк
                        String[] tok = new String[cnt];
                        boolean ptisnl=false;
                        int curind = 0;
                        for (int ii=0;ii<cnt;ii++) {
                            String next = st.nextToken();
                            if (!next.equals("\n")&&ptisnl) {
                                ptisnl = false;                        
                                tok[curind-1] = next;
                                continue;
                            }else {
                                if (next.equals("\n")) ptisnl = true;
                                tok[curind] = next;
                                curind++;
                            }
                        }
                        cnt = curind;
                        int y1 =xy[1];
                                        
                        for (int ii=0;ii<curind;ii++) {                    
                            String next = tok[ii];
                            if (next.equals("\n")) next = "";
                            int[] xxyy = UTIL.getOutPoint(width,height,col.fm,
                            		"TOP","LEFT",cdw,col.dh,x+left,offset+y,next);
                            //g.setClip(0,0,width,height);
                            g.drawString(next,xxyy[0]*a/100,(y1+ii*col.fm.getHeight())*a/100);                        
                        }
                }
                else g.drawString(str,xy[0]*a/100,xy[1]*a/100);
                //System.out.println("str="+str+" x="+xy[0]+" y="+xy[1]);
            }
        }
        //g.setClip(null);
        //g.setClip(0,0,30000,30000);
        return getNumRows();
    }

    public int getColumnX(int col) {
        int sum = 0;
        for (int i = 0; i < col ;i++) sum+=getVColumn(i).size;
        return sum;
    }

    int getColumnNumByAlias(String al) {
        if (columns==null) return -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].alias!=null)
                if (columns[i].alias.equals(al)) return i;
        }
        return -1;
    }

    public int getColumnsSize() {
        int sum=0;
        for (int i=0;i<helpArray.length;i++)
            sum+=getVColumn(i).size;
        return sum;
    }

    public int getRowY(int row) {
        return row*sizeRow;
    }

    public String getSourceText(int r, int c) {
        Object value = ds.getValue(r,getVColumn(c).target);
        //System.out.println("value="+value);
        try {
            if (value!=null) {
                //System.out.println("validator type = "+columns[c].validator.type);
                return getVColumn(c).validator.toString(value);
            }
            else return "";
        }catch(Exception e) {
            System.out.println("~views.Grid::getSourceText() : "+e);
            return "";
        }

    }

    void createCalcSequence() {//создает последовательность для вычисления Computed Column'ов
        if (columns==null) return;
        Vector names = new Vector();
        Vector Bn = new Vector();
        for (int i=0;i<columns.length;i++) {
            String alias = columns[i].alias;
            if (alias!=null&&columns[i].exp!=null) {//кладем его в вектор names
                names.addElement(alias);
                Vector bi = new Vector();
                calc.Calc cc = columns[i].calc;
                String[] als = null;
                try{
                    if (cc!=null) {
                        als = cc.getAliases();
                    }
                }catch(Exception e) {}
                if (als!=null)
                    for (int j=0;j<als.length;j++) {
                        if (als[j]!=null&& (!als[j].equals(columns[i].alias))) bi.addElement(als[j]);
                    }
                Bn.addElement(bi);

            }
        }//end of for
        Vector ret = null;
        if (names.size()>0) {
            try {
                ret = UTIL.createSequence(names, Bn);
            }catch(Exception e) {
                System.out.println("views.ReportGrid::createCalcSequence() : "+e);
                throw new Error(e.getMessage());
            }
        }
        if (ret!=null) {
            if (ret.size()>0) {
                calcArray = new int[ret.size()];
                //for (int i=0;i<calcArray.length;i++) calcArray[i] = i;
            }
            else return;

            for (int i=0;i<ret.size();i++) {
                //int indvec = 0;
                //if (columns[i].alias==null) indvec = -1;
                //else indvec = ret.indexOf(columns[i].alias);
                String name = (String)ret.elementAt(i);
                int index = 0;
                index = getColumnNumByAlias(name);
                if (index!=-1)
                    calcArray[i] = index;
            }
        }
    }//end of create sequence

}
