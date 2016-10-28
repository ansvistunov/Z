
package views;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.math.*;
import java.util.*;
import rml.Proper;
import views.edit.EditMaketAdapter;
import dbi.*;
import document.*;
import document.Closeable;

import java.sql.Types;
import loader.GLOBAL;
import calc.*;
import calc.objects.*;
public class Grid extends Panel implements
    AdjustmentListener,Retrieveable,
    Handler,Closeable,NotifyInterface, dbi.Packer,
    class_method, class_type, GlobalValuesObject {
    boolean needAdjust = true;
    Scrollbar vscroll = new Scrollbar();
    Scrollbar hscroll = new Scrollbar(Scrollbar.HORIZONTAL);
    Panel numpanel = new Panel();
    Button[] numbut = null;
    Button emptyButton = new Button();
    MyCanvas canv = null;
    Dimension size;
    int numRows=0;
    int numColumns=0;
    int sizeRow=20;
    int vscrollWidth = 15;
    int hscrollHeight = 15;
    int sizeColumn=40;
    int[] colSizes = null;
    int headColSize = 20;
    int headRowSize = 35;
    int beginRow;
    int endRow;
    int currentRow=1;
    int currentColumn=0;
    int visibleRows;
    int maxVisibleRows;

    int beginColumn=0;//Столбцы(column) нумеруются с нуля
    int endColumn;
    int visibleColumns;
    int maxVisibleColumns;

    Color gridColor = Color.gray;
    
    String[][] items = null;//порядок индексов такой: [column,row]
    boolean redrawTitleBar = true;
    boolean needDrawGrid = true;
    boolean editMode = false;
    Vector selection = new Vector();
    Component field = null;
    PopupMenu popupMenu = null;
    views.Menu menu;
    ActionListener popupAL;
    Hashtable aliases = null;
    String alias = null;

    /**************************************************/
    boolean ctrlA = false;

    Color bbbg_color = Color.lightGray;
    Color tbbg_color = Color.lightGray;
    //содержит объекты-столбцы(как видимые так и невидимые)
    Column[] columns = null;
    //содержит номера ВИДИМЫХ столбцов в массиве columns
    int[] helpArray = null;

    public dbi.DATASTORE ds = null;//фильтр, наложенный на parentds(ds=parentds, если фильтр не наложен)
    dbi.DATASTORE parentds = null;//исходное Datastore
    private String bbfont_face = "Times";
    private int bbfont_size = 10;
    private int bbfont_family = 0;
    Font bbfont=null;
    Color bbfont_color = Color.black;
    Color currow_color = Color.blue;
    Color currow_bg_color = Color.lightGray;
    String editable = "NO";
    String multiselect = "NO";
    String ret = "NO";//умеет ли грид возвращать датасторе
                      //=yes для справочников
    String editAction=null;
    String addAction=null;
    String delAction=null;

    String editExp=null;
    String addExp=null;
    String delExp=null;

    String[] parseEdit=null;//1
    String[] parseAdd=null;//2
    String[] parseDel=null;//3

    FilterStruct[] filterData = null;
    boolean hasFilter=true;
    FilterDialog fd = null;
    FindDialog findd = null;
    double extrah=0;
    int[] calcArray;//данный массив используется для вычисления значений Computed Column'ов
    boolean dynamic = false;
    protected java.awt.datatransfer.Clipboard clipboard = null;
    Calc filter_change;
	ColumnTemplate[] colTemps = new ColumnTemplate[3];//0-Number;1-String;2-Data
    public void doLayout() {
        super.doLayout();
        //System.out.println("doLayout called");
        if (field!=null) {
                remove(field);
                field=null;
                editMode=false;
        }
        needDrawGrid = true;
        repaint(0);
        emptyButton.requestFocus();
    }
    public Grid() {
        super();        
        setLayout(null);
        vscroll.setValue(1);
        vscroll.addAdjustmentListener(this);
        hscroll.addAdjustmentListener(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        clipboard = getToolkit().getSystemClipboard();
        //addMouseListener(new ML());
        
       // addKeyListener(EditMaketAdapter.createEditMaketAdapter(aliases,this));
        
    }

    public void addChildren(Object[] objs) {
        //System.out.println("inside Grid.addChildren");
        int cc=0;
        int vc=0;
        try {
            for (int i=0;i<objs.length;i++) {
                if (objs[i]==null)
                 throw new Error("~views.Grid::addChildren : Object views.Grid cannot be created!");
                if (objs[i] instanceof dbi.DATASTORE) {
                    ds = (dbi.DATASTORE)objs[i];
                    parentds = ds;
                }
                if (objs[i] instanceof views.Column) {
                    cc++;
                    if (((Column)objs[i]).isVisible()) vc++;
                }
				if (objs[i] instanceof ColumnTemplate) {
					ColumnTemplate ct = (ColumnTemplate)objs[i];
					switch (ct.getType()) {
						case 0:colTemps[0] = ct;break;
						case 1:colTemps[1] = ct;break;
						case 2:colTemps[2] = ct;break;
						default:;//do noting
					}
				}
            }
            if (cc>0) {
                columns = new Column[cc];
                helpArray = new int[vc];
            }else dynamic = true;//если число Column'ов=0, значит, они будут добавлены после Retrieve'а

            //System.out.println("helpArray length ="+vc);
            cc=0;vc=0;
            for (int i = 0;i<objs.length;i++) {
                if (objs[i] instanceof views.Column) {
                    columns[cc] = (Column)objs[i];
                    columns[cc].setParent(this);
                    if (columns[cc].isVisible()) {
                        helpArray[vc] = cc;
                        vc++;
                    }
                    if (columns[cc].target==null) {
                        if (columns[cc].getType() == Integer.MIN_VALUE) {
                            System.out.println("views.Grid.addChildren says : type for computed column not defined!");
                            cc++;
                            continue;
                        }
                        columns[cc].target = ds.addColumn(columns[cc].getType());
                    }
                    cc++;
                }
                if (objs[i] instanceof views.Menu) {
                    popupMenu = new PopupMenu();
                    popupAL = new PopupAL();
                    menu = (views.Menu)objs[i];
                    //java.awt.Menu m = menu.getMenu();
                    if (menu==null) {
                        System.out.println("~views.Grid::addChildren:popmenu=null");
                        return;
                    }
                    int ic = menu.getItemCount();
                    for (int j=0;j<ic;j++) {
                        MenuItem mi = menu.getItem(0);
                        if (mi==null) continue;
                        mi.addActionListener(popupAL);
                        popupMenu.add(mi);
                    }
                    menu.setMenu(popupMenu);
                    add(popupMenu);
                }
            }
            createCalcSequence();
        }
        catch(Exception e) {
            System.out.println("~views.Grid::addChildren : "+e);
        }
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        Integer ip;
        this.aliases = aliases;

        sp = (String)prop.get("ALIAS");
        alias = sp;

        ip = (Integer)prop.get("BUTTONBAR_SIZE");
        if (ip!=null) headRowSize = ip.intValue();

        ip = (Integer)prop.get("TITLEBAR_SIZE");
        if (ip!=null) headColSize = ip.intValue();

        sp = (String)prop.get("BUTTONBAR_BG_COLOR");
        if (sp!=null) bbbg_color = UTIL.getColor(sp);

        sp = (String)prop.get("TITLEBAR_BG_COLOR");
        if (sp!=null) tbbg_color = UTIL.getColor(sp);
		
		sp = (String)prop.get("BG_COLOR");
		if (sp!=null) {
			setBackground(UTIL.getColor(sp));
		}else setBackground(Color.white);

        ip = (Integer)prop.get("VSCROLLSIZE");
        if (ip!=null) vscrollWidth = ip.intValue();

        ip = (Integer)prop.get("HSCROLLSIZE");
        if (ip!=null) hscrollHeight = ip.intValue();

        ip = (Integer)prop.get("ROWSIZE");
        if (ip!=null) sizeRow = ip.intValue();

        sp = (String)prop.get("BUTTONBAR_FONT_FACE");
        if (sp!=null) bbfont_face = sp;

        ip = (Integer)prop.get("BUTTONBAR_FONT_FAMILY");
        if (ip!=null) bbfont_family = ip.intValue();

        ip = (Integer)prop.get("BUTTONBAR_FONT_SIZE");
        if (ip!=null) bbfont_size = ip.intValue();

        sp = (String)prop.get("BUTTONBAR_FONT_COLOR");
        if (sp!=null) bbfont_color = UTIL.getColor(sp);

        bbfont = new Font(bbfont_face,bbfont_family,bbfont_size);

        sp=(String)prop.get("CURROW_COLOR");
        if (sp!=null) currow_color = UTIL.getColor(sp);
        sp=(String)prop.get("CURROW_BG_COLOR");
        if (sp!=null) currow_bg_color = UTIL.getColor(sp);

        sp = (String)prop.get("EDITABLE");
        if (sp!=null) editable = sp;
        sp = (String)prop.get("MULTISELECT");
        if (sp!=null) multiselect = sp;

        sp=(String)prop.get("EDIT");
        if (sp!=null) editAction = sp;
        sp=(String)prop.get("ADD");
        if (sp!=null) addAction = sp;
        sp=(String)prop.get("DEL");
        if (sp!=null) delAction = sp;

        sp=(String)prop.get("EDITEXP");
        if (sp!=null) {
            editExp = sp;
            try{
                parseEdit = UTIL.parseDep(editExp);
            }
            catch(Exception e) {
                System.out.println("~views.Grid:init() : "+e);
            }
        }
        sp=(String)prop.get("ADDEXP");
        if (sp!=null) {
            addExp = sp;
            try {
                parseAdd = UTIL.parseDep(addExp);
            }
            catch(Exception e) {
                System.out.println("~views.Grid:init() : "+e);
            }
        }
        sp=(String)prop.get("DELEXP");
        if (sp!=null) {
            delExp = sp;
            try {
                parseDel = UTIL.parseDep(delExp);
            }
            catch(Exception e) {
                System.out.println("~views.Grid:init() : "+e);
            }
        }

        sp = (String)prop.get("RETURN");
        if (sp!=null) ret = sp;

        sp = (String)prop.get("FILTER");
        if (sp!=null)
            if (sp.equals("NO")) hasFilter=false;

        ip = (Integer)prop.get("EXTRAH");
        if (ip!=null) extrah = ip.intValue();
        else
        extrah = Integer.parseInt(GLOBAL.pr(GLOBAL.DEFAULT_EXTRAH,"0"));
        //System.out.println("extrah="+extrah);

        sp = (String)prop.get("FCEXP");
        if (sp!=null) filter_change=new Calc(sp);

        emptyButton.setBounds(0,0,headRowSize,headColSize);
        //emptyButton.setEnabled(false);
        add(emptyButton);
        canv = new MyCanvas(this);
        add(canv);
        add(vscroll);
        add(hscroll);
        Document doc = (Document)aliases.get("###document###");
        doc.addHandler(this);
    }

    public void post_ret_init() {
        try {
            numColumns = getSourceColumns();
            numRows = getSourceRows();
            //System.out.println("numcolumns in grid "+alias+"="+numColumns);
            //if (numColumns<=0 || numRows<0) {
            //    throw new Error("Grid cannot be created because numColumns<=0 || numRows<0");

            //}
            if (numColumns==0) return;
            //System.out.println("numColumns in datastore="+numColumns);
            if (dynamic) {
                columns = new Column[numColumns];
                helpArray = new int[numColumns];
				Proper defp = ColumnTemplate.getDefaultProperties();
                Proper prop = null;
                String[] targets = ds.getNames();
                for (int i=0;i<numColumns;i++) {
                    columns[i] = new Column();
					int type = getJType(ds.getType(i));
					ColumnTemplate ct = null;
					if (type>=0) ct = colTemps[type];
					if (ct!=null) 
						prop = ct.getProperties();
					else
						prop = defp;
					
                    columns[i].init(prop,aliases);
                    columns[i].target = targets[i];
                    columns[i].title = targets[i];
                    helpArray[i] = i;
                }
            }

            for (int i = 0;i < columns.length;i++){
                if (columns[i].target!=null)
                    columns[i].setType(ds.getType(columns[i].target));
            }
            currentRow = 1;
            currentColumn = 0;
            beginColumn=0;
            beginRow=1;
            vscroll.setValue(1);
            hscroll.setValue(0);
            if (calcArray!=null) {
                for (int j = 0; j < numRows;j++) {
                    ds.setCurRow(j);
                        for (int i=0;i<calcArray.length;i++) {
                            columns[calcArray[i]].calc();
                        }
                }
            }
            ds.setCurRow(0);
            int sh = getToolkit().getScreenSize().height;
            //items = new String[sh/sizeRow][numColumns];

            /*---------------------------------------------*/
            if (numbut!=null)
                for (int i = 0; i < numbut.length;i++) {
                    remove(numbut[i]);
                }
            if (field!=null) {
                remove(field);
                field=null;
                editMode=false;
            }
            if (selection.size()>0) selection.removeAllElements();

            numbut = new Button[sh/sizeRow];
            for (int i=0;i<numbut.length;i++) {
                numbut[i] = new Button();
                //numbut[i].setBounds(0,headColSize + i*sizeRow,
                //                   headRowSize,sizeRow);
                numbut[i].setSize(headRowSize,sizeRow);
                numbut[i].setVisible(false);
                numbut[i].setBackground(bbbg_color);
                numbut[i].setFont(bbfont);
                add(numbut[i]);
            }
            ds.addHandler(this);
            ds.addPacker(this);
            canv.init();
        }
        catch (Exception e) {
            System.out.println("~views.Grid::post_ret_init : "+e);
        }
    }


    public void paint(Graphics g) {
        //System.out.println("paint in views.Grid called");
        if (columns==null) return;
        adjustViewPort();
        rebuildButtonBar();
        canv.repaint();
    }

    public void rebuildButtonBar() {
        if (numbut==null) return;
        for (int i=0;i<numbut.length;i++) {
            if (i<=endRow-beginRow){
                numbut[i].setLabel(new Integer(beginRow+i).toString());
                numbut[i].move(0,headColSize + i*sizeRow);
                numbut[i].setVisible(true);
            }
            else {
                numbut[i].setVisible(false);
                numbut[i].move(0,0);
            }
        }
    }

    public int getSourceRows() {
        if (ds!=null) return ds.getCountRows();
        else return 0;
        //return 0;
    }

    public int getSourceColumns(){
        if (dynamic&&ds!=null) return ds.getCountColumns();
        if (helpArray!=null) return helpArray.length;
        else return 0;
    }

     public void adjustViewPort() {
        size = getSize();
        vscroll.setBounds(size.width-vscrollWidth,0,vscrollWidth,size.height);
        hscroll.setBounds(0,size.height-hscrollHeight,size.width-vscrollWidth,hscrollHeight);
        canv.setBounds(headRowSize,0,size.width-vscrollWidth-headRowSize,size.height-hscrollHeight);
        adjustRC();
        vscroll.setMinimum(1);
        vscroll.setMaximum(getSourceRows()+1);
        vscroll.setVisibleAmount(endRow - beginRow+1);
        vscroll.setUnitIncrement(1);
        //fillItems(false,beginRow, endRow);
        hscroll.setMinimum(0);
        hscroll.setMaximum(numColumns);
        hscroll.setValue(beginColumn);
        hscroll.setVisibleAmount(endColumn - beginColumn+1);
        hscroll.setUnitIncrement(1);
        canv.hr.setFrame(beginColumn);
    }
    public void adjustRC(){
        /*Настройка вертикального Scrollbar*/
        maxVisibleRows = getViewPort().height / sizeRow;
        if (maxVisibleRows==0) maxVisibleRows = 1;
        visibleRows = maxVisibleRows;
        if (visibleRows>numRows) visibleRows = numRows;
        vscroll.setBlockIncrement(maxVisibleRows);
        beginRow=vscroll.getValue();
        endRow = beginRow + visibleRows-1;

        if (endRow>numRows) {
            int delta = endRow-numRows;
            endRow-=delta;
            beginRow-=delta;
        }

        /*Настройка горизонтального Scrollbar и TitleBar*/
        int size = 0;
        maxVisibleColumns=0;
        beginColumn=hscroll.getValue();
        for (int i = beginColumn;i<numColumns;i++) {
            size+=getVColumn(i).size;
            if (size>getViewPort().width) break;
            maxVisibleColumns++;
        }
        for (int i = beginColumn-1;i>=0;i--) {
            size+=getVColumn(i).size;
            if (size>getViewPort().width) break;
            maxVisibleColumns++;
        }

        if (maxVisibleColumns==0) maxVisibleColumns = 1;
        visibleColumns = maxVisibleColumns;
        if (visibleColumns>numColumns) visibleColumns = numColumns;
        hscroll.setBlockIncrement(maxVisibleColumns);
        endColumn = beginColumn + visibleColumns-1;

        if (endColumn>numColumns-1) {
            int delta = endColumn-numColumns+1;
            endColumn-=delta;
            beginColumn-=delta;
        }


    }

    public Dimension getViewPort() {
        Dimension dim = getSize();
        dim.width = dim.width - headRowSize - vscrollWidth;
        dim.height = dim.height - headColSize - hscrollHeight-1;
        return dim;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (field!=null) {
                remove(field);
                field=null;
                editMode = false;
        }
        if (e.getAdjustable().equals(vscroll)){
            beginRow = vscroll.getValue();
            endRow = beginRow+visibleRows-1;
            redrawTitleBar=false;
            repaint();
            //fillItems(true,beginRow,endRow);
        }
        if (e.getAdjustable().equals(hscroll)){
            canv.clean=true;
            repaint();
        }

    }

   /* public void fillItems(boolean rep, int begin, int end) {
        if (true) return;
        int r = end - begin +1;
        //В этом цикле i - номер строки, j - номер столбца
        try {
            for(int i = 0; i < r; i++) {
                int size = 0;
                for (int j = 0; j<numColumns; j++) {
                    size+=getVColumn(j).size;
                    items[i][j] = getSourceText(i+begin-1,j);
                }
            }

            if (rep) repaint();
        }
        catch (Exception e) {
            System.out.println("~views.Grid::fillItems : " + e);
        }
    }*/

    public String getSourceText(int r, int c) {
        Object value = ds.getValue(r,getVColumn(c).target);
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

    public int getColumnsSize() {
        int size = 0;
        if(columns==null) return 0;
        for (int i = 0; i<helpArray.length;i++){size+=getVColumn(i).size;}
        return size;
    }

    public int getVisibleColumnsSize() {
        int size = 0;
        if(columns==null) return 0;
        for (int i = beginColumn; i<=endColumn;i++){size+=getVColumn(i).size;}
        return size;
    }

    public void setCurrentRow(int pos, boolean needRepaint) {
        /*int delta = endRow-beginRow;
        currentRow = pos;
        if (currentRow<1) currentRow = 1;
        if (currentRow>numRows) currentRow = numRows;
        if (currentRow<beginRow || currentRow>endRow) {
            vscroll.setValue(currentRow);
        }
        ds.setCurRow(currentRow-1);*/
        if (pos<1) pos = 1;
        if (pos>numRows) pos = numRows;
        ds.setCurRow(pos-1);
        if (pos<beginRow || pos>endRow) {
            currentRow = pos;
            vscroll.setValue(currentRow);
            if (needRepaint) repaint();
        }else {
            if (needRepaint) canv.drawCell(false);
            currentRow = pos;
            if (needRepaint) canv.drawCell(true);
        }
    }

    public void setCurrentColumn(int pos, boolean needRepaint) {
        //int delta = endColumn-beginColumn;
        //currentColumn = pos;
        if (pos<0) pos = 0;
        if (pos>numColumns-1) pos=numColumns-1;
        canv.clean = false;
        if (pos<beginColumn || pos>endColumn) {
            currentColumn = pos;
            hscroll.setVisibleAmount(1);//т.к. VisibleAmount зависит от Value для hscrollbar
            hscroll.setValue(currentColumn);
            canv.clean = true;
            if (needRepaint) repaint();
        }else {
            if (needRepaint) canv.drawCell(false);
            currentColumn = pos;
            if (needRepaint) canv.drawCell(true);
        }
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.ACTION_EVENT) {
            if (e.target instanceof Button) {
                if (multiselect.equals("YES"))
                    buttonReaction(e);
                //System.out.println("Button with label "+((Button)e.target).getLabel()+" pressed");
            }
            return true;
        }
        return super.handleEvent(e);
    }
    /************************************/

    /************************************/


    public void retrieve() {
        //System.gc();
        try {
        if (ds!=null) {
            ds.retrieve();
            post_ret_init();
        }
        }catch(Exception e){}
        if (columns==null) {
            System.out.println("`views.Grid::retrieve: columns==null!");
            return;
        }

        for (int i=0;i<columns.length;i++) {
            columns[i].retrieve();
        }

        if (hasFilter) {
            filterData = loadFilter();
            if (filterData!=null) {
                if (filterData.length!=helpArray.length)
                    System.out.println("views.Grid::retrieve() : bad filter for grid "+alias);
                else {
                    System.out.println("loading filter in grid!!!!!!!!");
                    dbi.DATASTORE retds = getFilter(filterData);
                    ds = retds;
                    notifyHandler(null);
                }
            }

        }

        canv.repdelay=0;
        canv.clean=true;
        redrawTitleBar=true;
        numRows = getSourceRows();
        numColumns = getSourceColumns();
        if (isVisible()) repaint();
    }

    public void update() {
        //System.out.println("inside grid.update");
        //System.out.println("field="+field);
        try {
            if (field!=null) {
                toDS();
                remove(field);
                field=null;
                editMode = false;
            }
            if (ds!=null) ds.update();
        }
        catch (Exception e){
            System.out.println("~views.Grid::update() : "+e);
        }
    }

    public void fromDS(){
        //if (true) return;
        if (ds!=null) post_ret_init();
        else {
            throw new Error("Grid cannot be created.DS=null!");

        }
        if (columns==null) {
            System.out.println("`views.Grid::retrieve: columns==null!");
            return;
        }

        for (int i=0;i<columns.length;i++) {

            columns[i].retrieve();
        }

        canv.repdelay=0;
        canv.clean=true;
        redrawTitleBar=true;
        numRows = getSourceRows();
        numColumns = getSourceColumns();
        if (isVisible()) repaint();

    }

    public void toDS(){
        if (field==null) return;
        if (field instanceof views.EditField) {

            Column col  = getVColumn(currentColumn);
            Object val = null;
            try {
                val = col.validator.toObject(((views.EditField)field).getvalue());
            }catch(Exception e) {
                System.out.println("views.Grid::toDS : "+e);
                //throw new Error("Bad value from field!");
            }
            col.setValue(val);
            col.calcHandbookExp();
            //ds.setValue(currentRow-1,getVColumn(currentColumn).target, val);
            //items[currentRow-beginRow][currentColumn] =
            //    ds.getValue(currentRow-1,getVColumn(currentColumn).target).toString();
        }
        if (field instanceof Choice) {
            /*if (ds.getType(getVColumn(currentColumn).target)!=getVColumn(currentColumn).ds.getType(0)) {
                System.out.println("~views.Grid::toDS(): incompatible types of data in different datastores!");
                remove(field);field=null;editMode = false;
                return;
            }*/
            int row = ((Choice)field).getSelectedIndex();
            Column col = getVColumn(currentColumn);
            Object val = col.ds.getValue(row,0);
            col.setValue(val);
            col.ds.setCurRow(row);
            col.calcHandbookDep();
            //ds.setValue(currentRow-1,getVColumn(currentColumn).target, val);
            //items[currentRow-beginRow][currentColumn] =
            //    ds.getValue(currentRow-1,getVColumn(currentColumn).target).toString();
        }
    }

    public int getColumnX(int col) {
        int sum = 0;
        for (int i = 0; i < col ;i++) sum+=getVColumn(i).size;
        return sum;
    }
    public int getRowY(int row) {
        return headColSize+row*sizeRow;
    }

    void buttonReaction(Event e) {
        Integer row;
        try{
            row = new Integer(((Button)e.target).getLabel());
        }
        catch(Exception ex) {return;}
        if (selection.contains(row)) selection.removeElement(row);
        else selection.addElement(row);
        canv.repdelay=0;
        repaint();
    }

    public boolean keyDown(Event e,int key) {
        if (e.id == Event.KEY_PRESS) {
            //System.out.println("key press");
            if (e.key == Event.TAB) {
                processTab(e);
                return true;
            }
            if (e.key == Event.ENTER && ((e.modifiers&Event.CTRL_MASK)==0)) {
                if (field!=null) {
                    processEnter();
                    repaint();
                    return true;
                }
                if (numRows==0) return true;
                //System.out.println("insibe enter handler");
                if (editable.equals("NO")) {
                    if (editAction!=null)
                        doAction(editAction);
                    return true;
                }
                createField();
                return true;//false;
            }
            if (e.key == Event.ESCAPE) {
                if (field==null) return true;
                remove(field);
                field = null;
                editMode = false;
                emptyButton.requestFocus();
                return true;
            }
            if (field!=null) return field.handleEvent(e);
            if (e.key==Event.DELETE && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                //if (!this.editable.equals("YES")) return true;
                if (delAction!=null) {
                    doAction(delAction);
                  //alex patch возвращает фокус в grid
                    emptyButton.requestFocus();
                    
                    //deleteRow();
                    //canv.clean=true;
                    //canv.repdelay=0;
                    //repaint();
                  //alex
                    return true;
                }
                if (!this.editable.equals("YES")) return true;
                deleteRow();
                canv.clean=true;
                canv.repdelay=0;
                repaint();
                return true;
            }
            if ( (e.key==((int)'S'-64)) && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                return process_CTRL_S();
            }
            if (  e.key==6 &&                       // 6 = ((int)'F')-64
                (e.modifiers&Event.CTRL_MASK)!=0 && hasFilter){//вызываем окно фильтра
                return process_CTRL_F();
            }

            if ( (e.key==((int)'C'-64)) && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                return process_CTRL_C();
            }
            if ( (e.key==((int)'V'-64)) && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                return process_CTRL_V();
            }
            if ( (e.key==((int)'A'-64)) && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                return process_CTRL_A();
            }

            //else return true;
        }
        if (e.id==Event.KEY_ACTION) {
            if (field!=null) return  (field.handleEvent(e));
            if (e.key==Event.PGDN){
                if (currentRow==ds.getCountRows()) return true;
                setCurrentRow(currentRow+vscroll.getBlockIncrement(), true);
                //repaint();
                return true;
            }
            if (e.key==Event.PGUP){
                if (currentRow==1) return true;
                setCurrentRow(currentRow-vscroll.getBlockIncrement(), true);
                //repaint();
                return true;
            }
            if (e.key==Event.HOME) {
                if (currentRow==1) return true;
                canv.repdelay=0;
                setCurrentRow(1, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.END) {
                if (currentRow==ds.getCountRows()) return true;
                canv.repdelay=0;
                setCurrentRow(numRows, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.DOWN) {
                if (currentRow==ds.getCountRows()) return true;
                canv.repdelay=0;
                setCurrentRow(currentRow+1, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.UP) {
                if (currentRow==1) return true;
                canv.repdelay=0;
                setCurrentRow(currentRow-1, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.LEFT) {
                if (currentColumn==0) return true;
                canv.repdelay=0;
                setCurrentColumn(currentColumn-1, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.RIGHT) {
                if (currentColumn==helpArray.length-1) return true;
                canv.repdelay=0;
                setCurrentColumn(currentColumn+1, true);
                //canv.repdelay=0;
                //repaint();
                return true;
            }
            if (e.key==Event.INSERT && ((e.modifiers&Event.CTRL_MASK)!=0)) {
                if (addAction != null){
                    doAction(addAction);
                    //alex patch возвращает фокус в grid
                    emptyButton.requestFocus();
                    //alex
                    return true;
                }
                if (!this.editable.equals("YES")) return true;
                insertRow();
                canv.clean=true;
                canv.repdelay=0;
                repaint();
                return true;
            }
            return false;
        } return false;

    }
    public void processEnter() {
        toDS();
        if (field!=null){
            remove(field);
            field=null;
            editMode = false;
        }
        emptyButton.requestFocus();
    }

    public void processTab(Event e) {
        boolean em = editMode;
        if (em) processEnter();
        int max =  numColumns*numRows;
        int l = (currentRow-1)*numColumns+currentColumn;
        if ((e.modifiers&Event.SHIFT_MASK) != 0) l--;else l++;
        if (l<0) l=0;
        if (l>=max) l=max-1;
        canv.repdelay=0;
        setCurrentRow(l/numColumns + 1,true);
        setCurrentColumn(l % numColumns, true);
        if (em) createField();

        //repaint();

    }

    void createField() {
        if (!this.editable.equals("YES")) return;
        Column curcol = getVColumn(currentColumn);
        String editable = curcol.getEditable();
        double ec = 1;
        if (Integer.parseInt(GLOBAL.pr(GLOBAL.NEED_EXTRAH,"0"))!=0)
            ec=extrah/100+1;

        if (editable.equals("HAND")) {
            EditField f = new EditField(false);
            f.setFieldParent(this);
            //Proper prop = new Proper();
            f.settext(getSourceText(currentRow-1,currentColumn));
            f.setType(ds.getType(getVColumn(currentColumn).target));
            f.validator = getVColumn(currentColumn).validator;
            int x = headRowSize+getColumnX(currentColumn);

            adjustRC();

            int y = headColSize+(currentRow-beginRow)*sizeRow;
            f.setBounds(x-getColumnX(beginColumn),y,getVColumn(currentColumn).size,(int)(sizeRow*ec));
            add(f,0);
            if (field!=null) remove(field);
            field = f;
            f.getBaseField().requestFocus();
            f.getBaseField().selectAll();
            editMode = true;
        }
        if (editable.equals("HANDBOOK")) {
            EditField f = new EditField(false);
            f.setFieldParent(this);
            f.setaliases(aliases);
            //Proper prop = new Proper();
            f.settext(getSourceText(currentRow-1,currentColumn));
            f.setType(ds.getType(getVColumn(currentColumn).target));
            f.seteditable("HANDBOOK");
            if (getVColumn(currentColumn).edit!=null)
                f.setedit(getVColumn(currentColumn).edit);
            int x = headRowSize+getColumnX(currentColumn);

            adjustRC();

            int y = headColSize+(currentRow-beginRow)*sizeRow;
            f.setBounds(x-getColumnX(beginColumn),y,getVColumn(currentColumn).size,(int)(sizeRow*ec));
            add(f,0);
            if (field!=null) remove(field);
            field = f;
            f.getBaseField().requestFocus();
            f.getBaseField().selectAll();
            editMode = true;
        }
        if (editable.equals("LIST")) {
            Choice ch = new Choice();
            if (curcol.ds!=null){
                try{
                curcol.ds.retrieve();
                int count = curcol.ds.getCountRows();
                if (count==0) return;
              for (int i=0;i<count;i++) {
                Object val = curcol.ds.getValue(i,0);
                ch.add(val.toString());
              }
              }catch(Exception e){}
            }else return;
            //Proper prop = new Proper();
            ch.select(getSourceText(currentRow-1,currentColumn));
            int x = headRowSize+getColumnX(currentColumn);

            adjustRC();

            int y = headColSize+(currentRow-beginRow)*sizeRow;
            ch.setBounds(x-getColumnX(beginColumn),y,getVColumn(currentColumn).size,sizeRow);
            add(ch,0);
            if (field!=null) remove(field);
            field = ch;
            ch.requestFocus();
            editMode = true;
        }
    }

    public void deleteRow() {
        if (ds!=null) {
            if (numRows==0) return;
            ds.delRow(currentRow-1);
            numRows = getSourceRows();
            Enumeration elements = selection.elements();
            while(elements.hasMoreElements()) {
                Integer i = (Integer)elements.nextElement();
                if (i.intValue()>numRows)
                   selection.removeElement(i);
            }
            if (currentRow>numRows) setCurrentRow(numRows, false);

        }
    }

    public void insertRow() {
        try {
        if (ds!=null) ds.newRow();
        }
        catch(Exception e) {System.out.println("exception inside DATASTORE.newRow : "+e);}
        numRows = getSourceRows();
        vscroll.setMaximum(numRows+1);
        setCurrentRow(numRows, false);
    }

    public void showPopup(int x, int y) {
        if (popupMenu!=null)
            popupMenu.show(this,x,y);
    }

    public void notifyHandler(Object o) {
        numRows = getSourceRows();
        numColumns = getSourceColumns();
        if (numRows<currentRow) setCurrentRow(numRows,false);
        if (numColumns-1<currentColumn) setCurrentColumn(numColumns,false);
        if (currentRow-1!=ds.getCurRow()) {
            //currentRow = ds.getCurRow()+1;
            vscroll.setMaximum(numRows+1);
            setCurrentRow(ds.getCurRow()+1, false);
        }
        canv.clean=true;

        redrawTitleBar = true;

        canv.repdelay=0;
        repaint();
    }

    public void doAction(String action) {
        if (action!=null) {
            try {
                //System.out.println("before doaction");
		//System.err.println("*** doing Action "+action);	
                document.ACTION.doAction(action,aliases,this);
            }
            catch(Exception ex) {
                System.out.println("exception inside document.ACTION:doAction : "+ex);
            }
        }
    }

    public dbi.DATASTORE returnSelection() {
        if (ds==null || ds.getCountRows()==0) return null;
        if (!multiselect.equals("YES")) {
            dbi.DATASTORE tmp = ds.createFilter(new int[]{currentRow-1});
            return tmp;
        }
        if (selection == null) return null;
        if (selection.size() == 0) return null;

        int[] keys = new int[selection.size()];
        for (int i=0;i<keys.length;i++) {
            keys[i]=((Integer)selection.elementAt(i)).intValue()-1;
        }
        return ds.createFilter(keys);
    }
    //вызывается для грида-справочника, когда тот закрывается
    public void closeNotify() {
        dbi.DATASTORE ds2 = null;
        if (ret.equals("YES")) ds2 = returnSelection();

        selection.removeAllElements();
        if (ds2!=null) {
            aliases.put("RETURNSTORE", ds2);
            if (GLOBAL.views_debug>0)
            System.out.println("aliases in closeNotify = "+aliases);
            if (GLOBAL.views_debug>0)
            System.out.println("!row count in returned datastore is "+ds2.getCountRows());
        }
        else {
            //System.out.println("i remove dsal");
            //aliases.remove("");
            //System.out.println("i was put null");
        }
    }
    //Вызывается при завершении ACTION
    public void notifyIt() {
        //if (GLOBAL.views_debug>0)
        //System.out.println("notifyIt called");
        //если editMode=true, значит notifyIt вызван филдом, в который
        //занесли значение из справочника -> нужно пересчитать значение
        //зависимых столбцов
        //System.out.println("editMode="+editMode);
        if (editMode) {
            ds.removeHandler();
            processEnter();

            //установка значения в столбце из справочника
            if (getVColumn(currentColumn).editExp!=null) {
                try {
                    Column col = getVColumn(currentColumn);
                    Calc c = new Calc(col.editExp);
                    if (c!=null) c.eval(aliases);
                    col.calcHandbookDep();
                    //Object val = Calc.macro(getVColumn(currentColumn).editExp,aliases);
                    //System.out.println("after calling calcHandbookDep()");

                    //setDSValue(currentRow-1,getVColumn(currentColumn).target,val);
                }catch(Exception e) {
                    System.out.println("~views.Grid::notifyIt()(1) : "+e);
                }
            }
            //пересчет зависимых столбцов
            /*String[] deps = getVColumn(currentColumn).depends;
            if (deps!=null) {
                //System.out.println("deps[0]="+deps[0]);
                for (int i = 0; i < deps.length; i++) {
                    //System.out.println("column's alias="+deps[i]);
                    int n = getColumnNumByAlias(deps[i]);
                    //System.out.println("n="+n);
                    if (n<0) continue;
                    if (columns[n].editExp!=null) {
                        //System.out.println("val=" + val);
                        try {
                            Calc c = new Calc(columns[n].editExp);
                            if (c!=null) c.eval(aliases);
                            //Object val = Calc.macro(columns[n].editExp,aliases);
                            //setDSValue(currentRow-1,columns[n].target,val);
                        }catch(Exception e) {
                            System.out.println("~views.Grid::notifyIt()(2) : "+e);
                        }
                    }
                }
            }*/
            //Удаляем STORE, т.к. больше не нужно его хранит
            aliases.remove("STORE");

            ds.addHandler(this);
            notifyHandler(null);//выполняет repaint()
            return;
        }
        if (addExp!=null) {
            dbi.DATASTORE ds2 = (dbi.DATASTORE)aliases.get("STORE");
            if (ds2!=null) {
                //System.out.println("!!!!!row count in returned datastore is "+ds2.getCountRows());
                try{
                    processAddExp(ds2);
                    notifyHandler(null);
                }
                catch(Exception e) {
                    System.out.println("~views.Grid::notifyIt : "+e);
                }
                //Удаляем STORE, т.к. больше не нужно его хранит
                aliases.remove("STORE");
            }
        }
    }


    int getColumnNumByAlias(String al) {
        if (columns==null) return -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].alias!=null)
                if (columns[i].alias.equals(al)) return i;
        }
        return -1;
    }

    Column getColumnByAlias(String al) {
        if (columns==null) return null;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].alias!=null)
                if (columns[i].alias.equals(al)) return columns[i];
        }
        return null;
    }

    public void processAddExp(dbi.DATASTORE dstore) throws Exception{
        if (parseAdd==null) return;
        if (dstore==null) return;
        int currow = 0;//current row in dstore
        int crows=dstore.getCountRows();
        ds.removeHandler();

        //for (int k=0;k<parseAdd.length;k++){
        //    for (int m=0;m<2;m++)
        //    System.out.println("parseAdd["+k+"]"+"["+m+"]="+parseAdd[k][m]);
        //}

        for (int i=0;i<crows;i++) {
            dstore.setCurRow(i);
            int index = ds.newRow();
            ds.setCurRow(index);
            for (int j=0;j<parseAdd.length;j++) {
                //String col1 = parseAdd[j][0];//имя столбца в DATASTORE грида
                //String exp = parseAdd[j][1];
                //Object val = Calc.macro(exp,aliases);
                try {
                    if (parseAdd[j]!=null) {
                        Calc c = new Calc(parseAdd[j]);
                        if (c!=null) c.eval(aliases);
                    }
                }catch(Exception e) {
                    System.out.println("~views.Grid::processAddExp : "+e);
                    continue;
                }
                //System.out.println("val="+val);
                //setDSValue(index, col1, val);
            }
        }
        ds.addHandler(this);
    }

    void setDSValue(int row, String column, Object value)
        throws Exception {
        setDSValue(row,ds.getColumn(column),value);
    }
    void setDSValue(int row, int column, Object value)
        throws Exception {

        int type = ds.getType(column);
        switch (type) {
            case   Types.BIGINT :
            case   Types.DECIMAL:
            case   Types.DOUBLE :
            case   Types.FLOAT :
            case   Types.INTEGER :
            case   Types.NUMERIC:
            case   Types.REAL :
            case   Types.SMALLINT:
            case   Types.TINYINT:{
                if (GLOBAL.views_debug>0)
                System.out.println("number type");
                Double d;
                if (!(value instanceof Double)) {//смотрим, не строка л
                    if (value instanceof String){//пробуем конвертить в Double
                        d = new Double((String)value);
                        if (d!=null) ds.setValue(row,column,value);
                        else throw new Exception("incompatible types in different datastores!");
                    }
                    else throw new Exception("incompatible types in different datastores!");
                }
                else ds.setValue(row,column,value);
                break;
            }
            case   Types.CHAR :
            case   Types.VARCHAR:{
                System.out.println("string type");
                if (!(value instanceof String))
                throw new Exception("incompatible types in different datastores!");
                else ds.setValue(column,value);
                break;
            }
            case   Types.TIME :
            case   Types.TIMESTAMP:
            case   Types.DATE :{
                break;
            }
            case   Types.OTHER:{System.out.println("Unknown type!");return;}
		    default : {System.out.println("UNKNOWN TYPE!!!");}

        }
    }

    public Column getVColumn(int i) {
        try{
            return columns[helpArray[i]];
        }
        catch(Exception e) {
            System.out.println("views.Grid::getVColumn() : "+e);
            return null;
        }
    }
    class PopupAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            System.out.println("Action performed:"+command);
            try {
                document.ACTION.doAction(command,aliases,Grid.this);
            }
            catch(Exception ex) {
                System.out.println("exception inside document.ACTION:doAction : "+e+"\n"+ex);
            }
            if (e.getSource() instanceof views.Item) {
                Calc calc = ((views.Item)e.getSource()).getCalc();
                if (calc!=null) {
                    try {
                        calc.eval(aliases);
                    }catch(Exception ex) {
                        System.out.println("views.Grid$PopupAL::actionPerformed : "+ex);
                    }
                }
            }
        }
    }

    public static int getJType(int sqltype) {
        switch (sqltype) {
            case   Types.BIGINT :
            case   Types.DECIMAL:
            case   Types.DOUBLE :
            case   Types.FLOAT :
            case   Types.INTEGER :
            case   Types.NUMERIC:
            case   Types.REAL :
            case   Types.SMALLINT:
            case   Types.TINYINT:{return 0;}
            case   Types.CHAR :
            case   Types.VARCHAR:{return 1;}
            case   Types.TIME :
            case   Types.TIMESTAMP:
            case   Types.DATE :{return 2;}
            case   Types.OTHER:{return -1;}
		    default : {
		        System.out.println("views.Grid::getJType : unknown type<"+sqltype+">");
		        return -1;
		    }
		}
    }

    dbi.DATASTORE getFilter(FilterStruct[] fs) {
        if (fs==null) return null;
        if (parentds==null) return null;
        //Vector sortColumns = new Vector();
        int[] sortColumns = new int[fs.length];

        Vector minValues = new Vector();
        Vector maxValues = new Vector();
        Vector numColumns = new Vector();//здесь хранятся номера столбцов, которые нужно включить в фильт
        for (int i=0;i<fs.length;i++) {
            if (fs[i].sortOrder>0) sortColumns[fs[i].sortOrder-1] = i+1;
            if (fs[i].filter) System.out.println("filter=true");
            if (fs[i].filter && (fs[i].minValue!=null||fs[i].maxValue!=null)) {
                minValues.addElement(fs[i].minValue);
                maxValues.addElement(fs[i].maxValue);
                numColumns.addElement(new Integer(i));
            }
        }
        int[] cols = null;
        //Vector sCols = new Vector();
        int[] sortingCols = null;//этот массив будет передаваться в метод датасторе setSort
        int[] directions = null;
        int sortingSize=0;

        for (int i=0;i<sortColumns.length;i++) {
            if (sortColumns[i] > 0) sortingSize++;
            else break;
        }

        if (sortingSize>0) {
            sortingCols = new int[sortingSize];
            directions = new int[sortingSize];
            for (int i=0;i<sortingCols.length;i++) {
                if (sortColumns[i]==0) break;
                sortingCols[i] = parentds.getColumn(columns[helpArray[sortColumns[i]-1]].target);
                if (sortingCols[i]<0) {sortingCols=null;break;}
                directions[i] = 1;
            }
        }

        if (numColumns.size()>0) {
            System.out.println("numColumns.size()="+numColumns.size());
            cols = new int[numColumns.size()];
            for (int i=0;i<cols.length;i++) {
                cols[i] = parentds.getColumn( columns[helpArray[((Integer)numColumns.elementAt(i)).intValue()]].target );
                if (cols[i]<0) {cols = null;break;}
            }
        }
        dbi.DATASTORE retFilter = null;
        if (cols!=null) {
            Object[][] conditions = new Object[cols.length][2];
            for (int j=0; j<conditions.length;j++) {
                conditions[j][0] = minValues.elementAt(j);
                conditions[j][1] = maxValues.elementAt(j);
            }
            System.out.println("createFilter calling ...");
            retFilter = parentds.createFilter(cols, conditions);
        }
        if (sortingCols!=null) {
            if (retFilter!=null){//пробуем его отсортироват
                retFilter.setSort(sortingCols, directions);
            }else{
                System.out.println("setSort calling...");
                parentds.setSort(sortingCols, directions);
            }

        }else
            if (retFilter!=null) retFilter.resetSort();
            else parentds.resetSort();

        if (retFilter!=null) return retFilter;else return parentds;
    }

    public InputStream pack(Object data) {
        ByteArrayInputStream bis = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.close();
            bis = new ByteArrayInputStream(bos.toByteArray());
            System.out.println("Object's packed size = "+bos.size());
        }catch(Exception e) {System.out.println("Exception inside packFilter :"+e);}
        return bis;
    }

    public Object unpack(InputStream is) {
        FilterStruct[] ret=null;
        if (is==null) return null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            ret = (FilterStruct[])ois.readObject();
        }catch(Exception e) {System.out.println("views.Grid::unpack : "+e);}
        return ret;
    }

    public void saveFilter(FilterStruct[] data) throws Exception {
        //InputStream is = packFilter(data);
        //if (is==null) System.out.println("is=null!!!!!!!!!");
        try {
        //System.out.println("alias="+alias);

        parentds.saveObject(alias, data);
        //parentds.saveObject(alias, new Frame());

        }catch(Exception e){System.out.println("ffknjm "+ e);throw e;};
    }

    public FilterStruct[] loadFilter() {
        FilterStruct[] ret = null;
        try {
            ret = (FilterStruct[])parentds.readObject(alias);
        }catch(Exception e){System.out.println("views.Grid::loadFilter : "+e);}

        return ret;
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

    //Методы интерфейса GlobalValuesObject
    public void setValue(Object o){}
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}

    //Методы интерфейса class_type
    public String type(){
		return "VIEWS_GRID";
	}
    
    public dbi.DATASTORE getDatastore() {return ds;}
    

	//Методы интерфейса class_method
	public Object method(String method,Object arg) throws Exception{
	    //обработка вызова метода CurrentValue
	    if (method.equals("CURRENTVALUE") && (arg instanceof String)) {
            Column col = getColumnByAlias((String)arg);
            System.out.println("views.Grid::method(String ,Object) : inside processing CURRENTVALUE");
            if (col!=null) return col.getValue();
	    }else
		if(method.equals("GETVALUE") && (arg instanceof Vector)){
			try{
				String colal = (String)((Vector)arg).elementAt(1);
				int colnum = ((Double)((Vector)arg).elementAt(0)).intValue();
				String target = getColumnByAlias(colal).target;
				return ds.getValue(colnum,target);
				
			}catch(Exception e){
				throw new RTException("","Bad arguments in Grid.getValue");
			}
		}
		else
	    //обработка вызова метода CurrentValue
	    if(method.equals("SELECTIONVALUES") && (arg instanceof String) && selection!=null && selection.size()>0) {
            Column col = getColumnByAlias(((String)arg).toUpperCase());
            if (col==null) return null;
            int oldCurRow = ds.getCurRow();
            Object[] ret = new Object[selection.size()];
            System.out.println("views.Grid::method(String ,Object) : inside processing SELECTIONVALUES");
            for (int i=0;i<selection.size();i++) {
                int currow = ((Integer)selection.elementAt(i)).intValue()-1;
                ds.setCurRow(currow);
                ret[i] = col.getValue();
            }
            ds.setCurRow(oldCurRow);
            return ret;
	    }else
	    if (method.equals("RETRIEVE")) {
	        retrieve();
	    }else
	    if (method.equals("SETCURRENTROW") && (arg instanceof Double)) {
	        setCurrentRow(((Double)arg).intValue()+1, true);
	        return new Nil();
	    }else
	    if (method.equals("SETDATASTORE")) {
	        if ( arg instanceof dbi.DATASTORE ) {
	            ds.removeHandler();
	            parentds.removeHandler();

	            parentds = (dbi.DATASTORE)arg;
	            ds = parentds;
	            retrieve();
			}else throw new RTException("CASTEXCEPTION","(grid@SETDATASTORE String)");
		}else
		if (method.equals("GETDATASTORE")) {
		    return ds;
		}else
        if (method.equals("GETALLDATASTORE")) {
			return parentds;
        }else
		if (method.equals("SUM")) {
			if (ds==null || ds.getCountRows()==0) return new Double(0);
			Column col = getColumnByAlias((String)arg);
			if (col==null) return new Double(0);
			if (!(col.getValue() instanceof Double)) return new Double(0);
			int oldcur = ds.getCurRow();		    
			double s = 0;
			for (int i=0;i<ds.getCountRows();i++) {
				ds.setCurRow(i);
				s+=((Double)col.getValue()).doubleValue();
			}
			ds.setCurRow(oldcur);
			return new Double(s);
		}else
		if (method.equals("DUMPTOFILE")){
			FileDialog d = new FileDialog(new Frame(),"Сохранить в файле",FileDialog.SAVE);
			d.show();
			try{
				dumpToFile(d.getFile());
			}catch(Exception e){}

      }else if (method.equals("GETMENU")){
         return menu;
      }else if (method.equals("SETMENU")){
         try{
                popupMenu = new PopupMenu();
                if(popupAL==null) popupAL = new PopupAL();
                //java.awt.Menu m = ((views.Menu)arg).getMenu();
                views.Menu m = (views.Menu)arg;
                int ic = m.getItemCount();
                for (int j=0;j<ic;j++) {
                    MenuItem mi = m.getItem(0);                        
                    if (mi==null) continue;
                    mi.removeActionListener(popupAL);
                    mi.addActionListener(popupAL);
                    popupMenu.add(mi);
                }
                add(popupMenu);                    
                menu = m;
                menu.setMenu(popupMenu);
         }catch(Exception e){
            throw new RTException("RunTime"," Exception "+e.getMessage()+" in method setmenu (svr_grid )");
         
         }

         return new Double(0);




        }else if (method.equals("REPAINT")){
         repaint();
         return new Nil();
        }else if (method.equals("INVERTSELECTION")){
         Vector new_selection = new Vector();
         for (int i=0;i<getSourceRows();i++){
            Integer tmp = new Integer(i+1);
            if (!selection.contains(tmp)) new_selection.addElement(tmp);
         }
         selection = new_selection;
         repaint();

        }else if (method.equals("FASTSETSELECTION")){
         if (arg instanceof Double){
            Double d = (Double)arg;
            Integer i = new Integer(d.intValue());
            if (!selection.contains(i)) selection.addElement(i);
            
         }else throw new RTException("CASTEXCEPTION","grid@fastsetselection have one number parameter"); 
        }else if (method.equals("SETSELECTION")){
         if (arg instanceof Double){
            Double d = (Double)arg;
            Integer i = new Integer(d.intValue());
            if (!selection.contains(i)) selection.addElement(i);
            repaint();
         }else throw new RTException("CASTEXCEPTION","grid@setselection have one number parameter"); 
		}else
	    throw new RTException("HasNotMethod","method "+method+
							  " not defined in class views.Grid!");
		return new Nil();
	}

	boolean onSameScreen(int or, int oc, int nr, int nc) {
	    if (or>=beginRow && or<=endRow && oc>=beginColumn && oc <=endColumn &&
	    nr>=beginRow && nr<=endRow && nc>=beginColumn && nc <=endColumn) return true;
	    else return false;
	}

	protected boolean process_CTRL_S() {//вызвать окно поиска
	    String title =  GLOBAL.c2b(StringBundle.FindDialog_Caption + " ["+getVColumn(currentColumn).title+"]",GLOBAL.DTITLE);
        if (findd==null) {
            findd = new FindDialog(this,null,400,180,true);
            findd.setTitle(title);
            findd.show();
        }
        else {
            findd.setTitle(title);
            findd.show();
	    findd.validate();
        }
        if (findd.find_pressed) {
            findRow(findd.text,findd.go_down, findd.caze);
            return true;
        }
        return true;
	}
	protected boolean process_CTRL_F() {//вызвать окно фильтра
	    if (fd==null)
                fd = new FilterDialog(this, StringBundle.FilterDialog_Caption, 550, 400);
        fd.show();

        if (fd.result == FilterDialog.OK) {
            selection.removeAllElements();
            filterData = fd.getFilterStruct();
            if (filterData==null) {
                ds=parentds;
                if (filter_change!=null) {
                    try {
                        filter_change.eval(aliases);
                    }catch(Exception e) {
			System.out.println("views.Grid: exception in filter_change.eval(): "+e);
                        //e.printStackTrace();
                    }
                }
                notifyHandler(null);
                return true;
            }
            dbi.DATASTORE retds = getFilter(filterData);
            ds = retds;
            if (filter_change!=null) {
                    try {
                        filter_change.eval(aliases);
                    }catch(Exception e) {
			System.out.println("views.Grid: exception in filter_change.eval(): "+e);
                        //e.printStackTrace();
                    }
                }
            notifyHandler(null);
            return true;
        }
        if (fd.result == FilterDialog.RESET) {
            filterData=null;
            ds=parentds;
            ds.resetSort();
            if (filter_change!=null) {
                    try {
                        filter_change.eval(aliases);
                    }catch(Exception e) {
			System.out.println("views.Grid: exception in filter_change.eval(): "+e);
                        //e.printStackTrace();
                    }
                }
            notifyHandler(null);
            return true;
            //applyFilter(filterData);
        }
        return true;
	}

	protected boolean process_CTRL_C() {//копировать содержимое ячейки l
	    if (!editMode) {
            //System.out.println("press CTRL+C");
            if (clipboard!=null) {
                if(!ctrlA){
                String str = getSourceText(currentRow-1,currentColumn);
                clipboard.setContents(new java.awt.datatransfer.StringSelection(str), null);
                }else {
                        StringBuffer sb = new StringBuffer();
                        for(int i=0; i<ds.getCountRows(); i++){
                              for(int j=0;j<helpArray.length;j++){
                                        sb.append(getSourceText(i,j));
                                        if((j+1)<helpArray.length) sb.append('\t');
                              }
                              if((i+1)<ds.getCountRows()) sb.append('\n');
                        }
                        String str = sb.toString();
                        clipboard.setContents(new java.awt.datatransfer.StringSelection(str), null);
                        ctrlA = false;
                        repaint();

                }
            }
        }
        return true;
        }

        protected boolean process_CTRL_A() { // выделить таблицу
            if (!editMode) {
            //System.out.println("press CTRL+A");
            ctrlA = !ctrlA;
            repaint();
            }
        return true;
        }

        protected boolean process_CTRL_V() {//вставить содержимое ячеек
            if (!this.editable.equals("YES")) return true;
            //if (!editMode) {
            if (true) {
            if (clipboard!=null) {
                try{
                    Transferable tf = clipboard.getContents(null);
                    DataFlavor[] df = tf.getTransferDataFlavors();
                    String s = (String) tf.getTransferData(df[0]);
                    StringTokenizer st = new StringTokenizer(s,"\n");
                    int len = st.countTokens();
                    int cr = currentRow;
                    int cc = currentColumn;
                    for (int i=0; i<len; i++ ){
                        StringTokenizer st1 = new StringTokenizer(st.nextToken(),"\t",true);
                        int len1 = st1.countTokens();
                        //System.out.println("len1="+len1);
                        int r = ds.getCountRows();
                        if( (cr-1+i) >= r){
                                 //System.out.println("ins r="+r+"  "+(cr-1+i));
                                 if (addAction != null){
                                      doAction(addAction);
                                      //return true;
                                 }
                                 //if (!this.editable.equals("YES")) return true;
                                 insertRow();
                                 canv.clean=true;
                                 canv.repdelay=0;
                        }

                        int inc=0;
                        for(int j=0;j<len1;j++){
                                String text = st1.nextToken();
                                if(text.equals("\t")) {
                                        inc++;
                                        continue;
                                }
                                Column c = getVColumn(cc+inc);
                                Object o = c.validator.toObject(text);
                                ds.setValue(cr-1+i,c.target,o);
                        }
                    }
                    notifyHandler(null);
                    repaint();
                }catch(Exception e){
                     e.printStackTrace();
                     return true;
                }
            }
        }
        return true;
	}

	//Поиск строки по заданной маске
	void findRow(String mask, boolean down, boolean caze){
	    //если caze=true, значит поиск производим с учетом регистра
	    int count = getSourceRows();
	    if (count<2) return;
	    if (!caze) {
	        mask = mask.toUpperCase();
	    }
	    String curTarget = null;
	    Column curColumn = null;
	    try{
	        curColumn = getVColumn(currentColumn);
	        curTarget = curColumn.target;
	    }
	    catch (Exception e) {if (curColumn==null) return;}
	    MaskFilter mf = new MaskFilter(mask);
	    for (int i=0;i<count-1;i++) {
	        int currow ;
	        if (down) currow = (currentRow+i)%count + 1;
	        else currow = (count+currentRow-i-2)%count + 1;
	        Object value = ds.getValue(currow-1,curTarget);
	        String svalue=null;
	        try {
	            svalue = curColumn.validator.toString(value);
	        }catch(Exception e){}
	        if (svalue!=null && !caze) svalue = svalue.toUpperCase();
	        if (mf.accept(svalue)) {
	            setCurrentRow(currow,true);
	            return;
	        }
	    }

	}
	
	void dumpToFile(String fileName) throws Exception{		
		OutputStream os = null;
		if (helpArray == null || columns == null) return;
		os = new BufferedOutputStream(new FileOutputStream(fileName),2048);
		if (os==null) return;				
		int rows = getSourceRows();
		os.write((int)'-');
		os.write((int)'\n');
		for (int j=0;j<helpArray.length;j++) {
			String text = columns[helpArray[j]].getTitle();
			os.write((text+"\t").getBytes());
		}
		os.write((int)'\n');
		os.write((int)'-');
		os.write((int)'\n');
		for (int i=0;i<rows;i++){
			for (int j=0;j<helpArray.length;j++) {
				String text = 
				text = getSourceText(i,j);
				os.write((text+"\t").getBytes());
			}
			os.write((int)'\n');
		}
		os.close();
	}

}
