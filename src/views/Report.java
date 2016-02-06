
package views;

import java.awt.*;
import java.util.*;
import rml.*;
import java.awt.event.*;
import loader.GLOBAL;
import views.printing.*;
import calc.objects.*;
import calc.*;

public class Report extends Panel implements Retrieveable,
	class_method,class_type,GlobalValuesObject {

    String query = null;
    //final int pageWidth = 600;
    //final int pageHeight = 800;
    final int th = 65;//высота toolbar'а
    final String rootGroupAlias = "ROOTGROUP";
    final String nextPageLabel = StringBundle.Report_Label_NextPage;//"—лед.стр.";
    final String prevPageLabel = StringBundle.Report_Label_PrevPage;//"ѕред.стр.";
    final String firstPageLabel = StringBundle.Report_Label_FirstPage;//"ѕерв.стр.";
    final String lastPageLabel = StringBundle.Report_Label_LastPage;//"ѕосл.стр.";
    final String printLabel = StringBundle.Report_Label_Print;
    //Object[] children = null;
    //собственно хранилище данных дл€ отчета
    Hashtable aliases = null;
    dbi.DATASTORE ds = null;
    ReportForm[] colon = new ReportForm[2];
    views.Group root = null;
    dbi.Group droot = null;
    int numRows=0;//число строк в Datastore;

    Toolbar tb = new Toolbar();
    ScrollPane sp = new ScrollPane();
    WorkCanvas workArea = new WorkCanvas();

    //пол€ дл€ печати и разбивки на страницы
    int numPages = 0;
    String orientation = "PORTRAIT";
    int currentPage=0;//текуща€ страница(инкрементируетс€ внутри processGroup)
    int displayPage=0;//страница отчета, отображаема€ на дисплее
    int printPage=-1;//страница, которую необходимо напечатать(=-1, если печатаем все)
    int offset=0;//смещение дл€ отрисовки на текущей странице
    boolean isPrint=false;//=true, если вывод на принте
    public Dimension pageSize = new Dimension(550,800);//magic numbers for A4(portrait)
    int mashtab = 100;//масштаб при отрисовке в контекте диспле€
    Image image = null;//здесь содержимое текущей страницы
    Graphics curGraphics = null;
    PrintJob pjob = null;
    boolean needDrawing = false;
    boolean printed = false;
    boolean needCreatePage = true;
    boolean wrepaint = true;
    PrintProgress pp = null;

    public Report() {
        //setSize(400,10000);
        setSize(600,500);
        setLayout(null);
        setBackground(Color.white);
        //doLayout();
        add(tb);
        sp.add(workArea);
        add(sp);
    }

    public void doLayout() {
        System.out.println("doLayout called");
        //super.doLayout();
        workArea.setBounds(0, 0, 50 + pageSize.width*mashtab/100, 50 + pageSize.height*mashtab/100);
        sp.setBounds(0, th, getSize().width, getSize().height-th+1);
        tb.setBounds(0,0,Report.this.getSize().width, th);
        sp.doLayout();
        repaint();
        //Graphics tmp = getGraphics();
        //paint(tmp);
    }


    public int getCountPages(){
        if (ds == null) return 0;
        if (numRows==0) return 1;
        needDrawing = false;
        currentPage = 0;
        offset = getC1Height();
        processGroup(droot,root);
        int ret = currentPage+1;
        currentPage = 0;
        offset = getC1Height();
        return ret;
    }
    public Dimension getPageSize(){return pageSize;}
    public void setPageSize(Dimension size) {
        pageSize = size;
    }

    //public void printAll() {}

    public void print(int npage) {
        Graphics pg = null;
        Properties prop = System.getProperties();
        String rp = GLOBAL.pr(GLOBAL.PRINTING_REMOTE,"NO");
        if (rp.toUpperCase().equals("YES")) {
            String host = GLOBAL.pr(GLOBAL.PRINTING_HOST,"");
            String pname = GLOBAL.pr(GLOBAL.PRINTING_PRINTER_NAME,"DEFAULT");
            int port = 0;
            int bsize = 0;
            try {
                port = Integer.parseInt(GLOBAL.pr(GLOBAL.PRINTING_PORT,"8001"));
                bsize = Integer.parseInt(GLOBAL.pr(GLOBAL.PRINTING_BUFFER_SIZE,"50000"));
            }
            catch(Exception e) {}
            try{pjob = new RPrintJob(host,port,pname,orientation,bsize);}
            catch(Exception e){}
            if (pjob==null) {
                System.out.println("Print Server unavaible!");
                return;
            }
        }else  {
		int res=0;
		try{
		if (orientation.toUpperCase().equals("LANDSCAPE")){
			res = loader.Boot.setLandscapeOrientation();
		}else
			res = loader.Boot.setPortraitOrientation();
		}catch(UnsatisfiedLinkError e){}
		//System.err.println("*** result= "+res);

        	pjob =
            	getToolkit().getPrintJob(new Frame(),
                	"Printing Test", prop);
	}
        //System.out.println("prop="+prop);
        if (pjob!=null) {
            System.out.println("page size = "+pjob.getPageDimension());
            System.out.println("page resolution is "+pjob.getPageResolution());
        }
        if (pjob != null) {
		pg = pjob.getGraphics();

	}
        if (pg == null) return;
        int savem=mashtab;
        mashtab = 100;//печатаем всегда в масштабе 100%
        if (root!=null) root.createFonts(mashtab);
        int numCopies = 1;
        try {
            numCopies = Integer.parseInt(tb.numCopies.getSelectedItem());
        }catch(NumberFormatException e) {}

        pp = new PrintProgress(new Frame(),"¬ывод на печать");
        String ppcp;
        if (npage==-1) ppcp = "1";
        else ppcp = String.valueOf(npage+1);
        pp.curpage.setText(ppcp);
        pp.countpages.setText(String.valueOf(numPages));
        pp.copy.setText("(1)");
        //System.out.println("before show dialog");
        pp.show();
        //System.out.println("after show dialog");

        for (int i=0;i<numCopies;i++) {
            if (i==0) curGraphics = pg;
            else curGraphics = pjob.getGraphics();
            pp.copy.setText("("+String.valueOf(i+1)+")");
            needDrawing = true;
            offset = getC1Height();
            currentPage = 0;
            printPage = npage;
            isPrint = true;
			if (isDrawPage()) {
				drawColon(colon[0]);//рисует верхний колонтитул
            	drawColon(colon[1]);//рисует нижний колонтитул
			}
            processGroup(droot, root);
            curGraphics.setClip(0,0,pageSize.width*mashtab/100, pageSize.height*mashtab/100);

            curGraphics.dispose();
        }
        mashtab=savem;
        pjob.end();
        //System.out.println("pjob.end called");
        isPrint = false;
        pp.dispose();
    }

    public void init(Proper prop, Hashtable aliases) {
        String sp;
        sp = (String)prop.get("BG_COLOR");
        if (sp!=null) setBackground(UTIL.getColor(sp));
        this.aliases = aliases;

        sp = (String)prop.get("ORIENTATION");
        if (sp!=null) {
            orientation = sp;
            if (orientation.toUpperCase().equals("LANDSCAPE")) {
                int t = pageSize.width;
                pageSize.width = pageSize.height;
                pageSize.height = t;
            }
        }
    }

    public void paint(Graphics g){
        //print(g,0);
        //System.out.println("----------paint in views.Report called---------");
		if (workArea.note) {
			Graphics gg = workArea.getGraphics();
			workArea.str1="";
			workArea.str2="";
			workArea.paint(gg);
		}
		workArea.note=false;
        if (ds!=null) {
            String res=null;
            //long t1 = System.currentTimeMillis();
            try {
                res = calc.Calc.macro(ds.getSql(),aliases);
            }catch(Exception e){/*System.out.println("exception in report.retrieve");*/
            }
            //long t2 = System.currentTimeMillis();
            //System.out.println("macro executing time="+(t2-t1));
            if ((res==null) || res.equals("")) {
				workArea.note=true;
				workArea.str1="ѕроверьте правильность";
				workArea.str2="задани€ параметров!";
				workArea.repaint();
				return;
			}
            //System.out.println("result sql="+res);
            //if (query==null) query = res;

            if (image==null)
                image = createImage(pageSize.width*mashtab/100,
                                pageSize.height*mashtab/100);

            if (!query.equals(res)) {
                //query=res;
                //if (curGraphics!=null)
                //    curGraphics.fillRect(0,0,pageSize.width*mashtab/100, pageSize.height*mashtab/100);


                workArea.clean = true;
                Graphics tg = workArea.getGraphics();
                workArea.update(tg);
                //tg.dispose();
				//try{
                retrieve();
				//}catch(Exception e) {
				if (ds.getCountRows()>0){
                	needCreatePage=true;
                	numPages=0;
                	displayPage=0;
                	wrepaint = true;
				}
            }
        }

		if (ds.getCountRows()==0){
			workArea.note = true;
			workArea.str1 = "ƒанных не получено!";
			workArea.str2 = "";
			workArea.repaint();
			return;
		}
        if (numPages==0) {
            numPages = getCountPages();
            tb.numPages.setText(String.valueOf(numPages));
        }

        if (needCreatePage) {
            needCreatePage = false;
            tb.curPage.setText(String.valueOf(displayPage+1));

            curGraphics = image.getGraphics();
            curGraphics.setColor(getBackground());
            curGraphics.fillRect(0,0,pageSize.width*mashtab/100, pageSize.height*mashtab/100);

            needDrawing = true;
            currentPage = 0;
            offset = getC1Height();
            //printPage = 0;
            processGroup(droot, root);
            curGraphics.setClip(0,0,pageSize.width*mashtab/100, pageSize.height*mashtab/100);
            drawColon(colon[0]);//рисует верхний колонтитул
            drawColon(colon[1]);//рисует нижний колонтитул
            curGraphics.setColor(Color.blue);
            curGraphics.setClip(0,0,pageSize.width*mashtab/100, pageSize.height*mashtab/100);
            curGraphics.drawRect(0,0,pageSize.width*mashtab/100-1, pageSize.height*mashtab/100-1);
        }
        if (wrepaint) {
            wrepaint = false;
            //System.out.println("wrepaint=true");
            //Graphics tg = workArea.getGraphics();
            //workArea.setSize(workArea.getSize().width, workArea.getSize().height);
            workArea.repaint();
        }
    }

    public void addChildren(Object[] objs){
        if (objs==null) return;
        if (objs.length == 0) return;
        REPORTHEADER rHeader = null;
        REPORTTRAILER rTrailer = null;
        ReportGrid rGrid = null;
        views.Group group = null;
        root = new views.Group();
        if (aliases!=null) {
            aliases.put(rootGroupAlias, root);
            root.aliases = aliases;
            root.alias = rootGroupAlias;
        }
        //children = new Object[objs.length];
        for (int i = 0;i<objs.length;i++) {
            if (objs[i] instanceof REPORTHEADER) {
                rHeader = (REPORTHEADER)objs[i];
            }else
            if (objs[i] instanceof REPORTTRAILER) {
                rTrailer = (REPORTTRAILER)objs[i];
            }else
            if (objs[i] instanceof COLONTITUL) {
                ReportForm col = ((COLONTITUL)objs[i]).getForm();
                if (col.getType().equals("TOP")) colon[0] = col;
                else
                if (col.getType().equals("BOTTOM")) colon[1] = col;
            }else
            if (objs[i] instanceof views.Group) {
                group = (views.Group)objs[i];
            }else
            if (objs[i] instanceof ReportGrid) {
                rGrid = (ReportGrid)objs[i];
                //rGrid.setParent(this);
            }else
            if (objs[i] instanceof dbi.GroupReport ||
                objs[i] instanceof dbi.DATASTORE) {
                    ds = (dbi.DATASTORE)objs[i];
            }
        }
        if (rGrid!=null && group!=null) rGrid = null;
        System.out.println("Header="+rHeader);
        root.addChildren(new Object[]{rHeader, group, rGrid, rTrailer});
        System.out.println("root.Header="+root.rHeader);
        root.setDatastore(ds);
        root.setParent(this);
        //System.out.println("after Report.addChildren");
    }

    void drawColon(ReportForm f) {
		if (f==null) return;
		fillColon(f);
        if (f.getType().equals("TOP")) {//значит, это верхний колонтитул
            f.paint(curGraphics, mashtab);
        }else {//а это нижний
            curGraphics.translate(0,(pageSize.height-getC2Height())*mashtab/100);
            f.paint(curGraphics, mashtab);
            curGraphics.translate(0,-(pageSize.height-getC2Height())*mashtab/100);
        }
    }
	void fillColon(ReportForm f) {
		if (f==null) return;
		f.setDatastore(ds);
        f.fillFields2();
	}

    public synchronized void processGroup(dbi.Group dgr, views.Group vgr) {

        if (dgr==null||vgr==null) return;
        dbi.Group[] subgr = dgr.getSubgroups();
        processHT(vgr.rHeader, dgr);

        if (subgr==null) {
            processGrid(vgr.rGrid, dgr);
        }
        else
        for (int i = 0; i < subgr.length; i++)
            processGroup(subgr[i], vgr.group);

        processHT(vgr.rTrailer, dgr);

    }

    public void processHT(ReportForm f, dbi.Group gr) {
        if (f==null) return;
        Rectangle r = f.getBounds();
        int hw = r.height + r.y;//высота header'а
        if (hw==0) return;
        int free = getPageFreeSpace();
        if (hw>free) {
            incPage();
        }
        f.isPrint = isPrint;
        if (isDrawPage()) {
            //System.out.println("offset="+offset);
            curGraphics.translate(0,offset*mashtab/100);
            curGraphics.setClip(0,0,pageSize.width*mashtab/100,pageSize.height*mashtab/100);
            f.currentGroup = gr;
            f.fillFields();//заполн€ет String-значени€ми Field'ы (Object-значени€ лежат либо в Datastore либо в dbi.Group)
            try {
                f.paint(curGraphics,mashtab);
            }catch(Exception e) {System.out.println("sd");}



            curGraphics.translate(0,-offset*mashtab/100);
        }
        offset+=hw;

    }

    public void processGrid(ReportGrid rg, dbi.Group gr) {
        //System.out.println("inside processGrid");
        //System.out.println("rg="+rg);
        //System.out.println("gr="+gr);
        //if (true) return;
        int delta = 0;
        if (rg==null || gr==null) return;
        int drawedRows = 0;
        if (drawedRows>=gr.endrow-gr.begrow+1) {
            System.out.println("alarm!!!");
        }
        offset+=rg.top;
        //if (isDrawPage()) rg.createFonts(mashtab);
        while (drawedRows<gr.endrow-gr.begrow+1) {
            if (isDrawPage()) rg.drawIt = true;
            else rg.drawIt = false;
            rg.beginRow = gr.begrow+drawedRows;
            rg.endRow = gr.endrow;

            if (offset == 0) delta = 1;else delta=0;
            offset+=delta;

            rg.offset = offset;
            rg.setFreeHeight(getPageFreeSpace()-rg.top-delta);
            int dr = rg.drawRows(curGraphics, mashtab);
            if (rg.endRow-rg.beginRow+1 > dr) incPage();
            else {
                offset += dr*rg.sizeRow;
                if (rg.drawGrid!=0) offset++;
            }
            drawedRows+=dr;
        }
    }

    public int getC1Height() {
        Rectangle r = null;
        if (colon[0]==null) return 0;
        r = colon[0].getBounds();
        return r.y + r.height;
    }

    public int getC2Height() {
        Rectangle r = null;
        if (colon[1]==null) return 0;
        r = colon[1].getBounds();
        return r.y + r.height;
    }

    //¬озвращает высоту страницы без учета места, занимаемого
    //колонтитулами
    int getPageFreeSpace() {
        if (pageSize==null) return 0;
        else
        return pageSize.height-offset-getC2Height();/*getC1Height()*/
    }

    void incPage() {
        offset = getC1Height();
        currentPage++;
        if (/*isDrawPage()*/printPage==-1&&isPrint) {
            curGraphics.dispose();
            //System.out.println("!curGraphics.dispose called!");
            //System.out.println("currentPage="+currentPage);
            if (pp!=null){
                pp.curpage.setText(String.valueOf(currentPage+1));
            }
            curGraphics = pjob.getGraphics();
            if (curGraphics==null){
                System.out.println("graphics context for next page is null!!!");
				return;
			}
        }
		if (isDrawPage()) {
			drawColon(colon[0]);
			drawColon(colon[1]);
		}
    }

    public void toDS(){}
    public void fromDS(){}
    public void update(){}
    public void retrieve(){
        try{
        long t1 = System.currentTimeMillis();
        if (ds!=null) {
            String res=null;
            try {
                res = calc.Calc.macro(ds.getSql(),aliases);
            }catch(Exception e){/*System.out.println("exception in report.retrieve");*/
                return;
            }
            query = res;
            if (res.equals("")) return;//throw new Error("Couldn't get data");
            //System.out.println("result SQL=empty");
            //System.out.println("before ds.retrieve");
            ds.retrieve();
            //System.out.println("after ds.retrieve");
            numRows = ds.getCountRows();
            if (numRows<=0) return;//throw new Error("");
            initDbiRoot();
            //System.out.println("datastore retrieve "+numRows+" rows");
            if (root!=null){
                ReportGrid rg = root.getGrid();
                if (rg!=null)
                if (rg.columns!=null){
                    Column[] columns = rg.columns;
                    for (int i = 0;i < columns.length;i++){
                        if (columns[i].target!=null)
                            columns[i].setType(ds.getType(columns[i].target));
                    }
                    for (int j = 0; j < numRows;j++) {
                        ds.setCurRow(j);
                        if (rg.calcArray!=null)
                            for (int i = 0;i < rg.calcArray.length;i++){
                                columns[rg.calcArray[i]].calc();
                        }
                    }
                    ds.setCurRow(0);
                }
            }
            //System.out.println("before createTree");
            createTree(droot, root);
            //System.out.println("after createTree");
            //System.out.println("before computeTree");
            computeTree(0,droot, root);
            root.setCurPos(-1);
			//fillColon(colon[0]);
			//fillColon(colon[1]);
            //System.out.println("after computeTree");

        }
        else throw new Error("views.Report have not DATASTORE!");
        long t2 = System.currentTimeMillis();
        System.out.println("******views.Report: retrieved "+ds.getCountRows()+" rows");
        System.out.println("******views.Report: retrieve time = "+(t2-t1));
        }catch(Exception e){e.printStackTrace();}
    }


    public void initDbiRoot() {
        if (ds instanceof dbi.GroupReport) {
            droot = ((dbi.GroupReport)ds).getRoot();

        }else
        if (ds instanceof dbi.DATASTORE) {
            droot =
                new dbi.Group(0,((dbi.DATASTORE)ds).getCountRows()-1);
        }
    }

    //данный метод используетс€ дл€ занесени€ в объекты dbi.Group
    //computed field'ов дл€ объектов ReportHeader и ReportTrailer
    public void createTree(dbi.Group dgr, views.Group vgr) {
        if (dgr==null||vgr==null) return;
        dbi.Group[] subgr = dgr.getSubgroups();
        createHT(vgr.rHeader, dgr);
        createHT(vgr.rTrailer, dgr);

        if (subgr!=null)
            for (int i = 0; i < subgr.length; i++)
                createTree(subgr[i], vgr.group);
    }

    public void createHT(ReportForm f, dbi.Group gr) {
        //System.out.println("inside createHT!!!!!!!!!!!!!!!!!!!!!!!!1");
        //System.out.println("f="+f);
        if (f == null) return;
        //System.out.println("before getFields-------------------");
        Field[] fields = f.getFields();
        if (fields==null) return;
        //System.out.println("after getFields--------------------");
        for(int i=0; i<fields.length;i++) {
            if (fields[i].getexp()!=null){//значит это computed field и его надо занести в dbi.Group
                gr.addField(fields[i].getalias());
            }
            //
            if (!fields[i].isComputed) {fields[i].setValue(ds.getValue(fields[i].gettarget()));}
            //
        }
    }
    //данный метод используетс€ дл€ вычислени€
    //computed field'ов дл€ объектов ReportHeader и ReportTrailer
    //созданных методом createTree
    public void computeTree(int j,dbi.Group dgr, views.Group vgr) {
        //System.out.println("inside computeTree!!!!!!!!!!!");
        if (dgr==null) return;
        dbi.Group[] subgr = dgr.getSubgroups();
        if (subgr!=null)
            for (int i = 0; i < subgr.length; i++)
                computeTree(i,subgr[i], vgr.group);
        computeHT(j,vgr.rHeader,vgr.rTrailer,dgr, vgr);

    }

    public void computeHT(int j,ReportForm header, ReportForm trailer, dbi.Group dgr, views.Group vgr) {
        //if (vgr==null) System.out.println("--------------vgr=null----------------------");
        if (vgr==null) return;
        if (dgr==null) return;
        if (header==null&&trailer==null) return;
        vgr.currentGroup = dgr;
        //vgr.curpos = j;
        vgr.curpos ++;
        if (header!=null) header.currentGroup = dgr;
        if (trailer!=null) trailer.currentGroup = dgr;
        //System.out.println("inside computeHT");
        //System.out.println("vgr.seq="+vgr.seq);
        if (vgr.seq==null) return;
        for (int i=0;i<vgr.seq.size();i++) {
            String alias = (String)vgr.seq.elementAt(i);
            if (alias==null) continue;
            Field f = null;
            if (header!=null) {
                f = header.getField(alias);
            }
            if (trailer!=null&&f==null) f = trailer.getField(alias);
            if (f!=null) {
                //System.out.println("before calling calc() for field with alias="+f.getalias());
                f.needSetString = false;
                f.calc();
                //System.out.println("after calling calc() for field with alias="+f.getalias());
            }
        }
    }

    void nextPageAction() {
        if (displayPage==numPages-1) return;
        displayPage++;
        needCreatePage = true;
        wrepaint = true;repaint();
    }
    void prevPageAction() {
        if (displayPage==0) return;
        displayPage--;
        needCreatePage = true;
        wrepaint = true;repaint();
    }

    void firstPageAction() {
        if (displayPage==0) return;
        displayPage = 0;
        needCreatePage = true;
        wrepaint = true;repaint();
    }

    void lastPageAction() {
        if (displayPage==numPages-1) return;
        displayPage = numPages-1;
        needCreatePage = true;
        wrepaint = true;repaint();
    }

    void printAction() {
        if (tb.whatPrint.getSelectedIndex()==0)
            print(-1);
        else print(displayPage);
    }

    boolean isDrawPage() {
        if (isPrint)
         return needDrawing && (currentPage==printPage || printPage==-1);
        else return needDrawing &&  (currentPage==displayPage);
    }

    void setMashtab(int m) {
        System.out.println("mashtab is "+m);
        if (mashtab==m) return;
        mashtab=m;
        if (root!=null) root.createFonts(mashtab);//ћасштабируем шрифты дл€ грида в нашем отчете
        image = null;
        //Runtime r  = Runtime.getRuntime();
        //System.out.println("free memory="+r.freeMemory());
        System.gc();
        needCreatePage = true;
        wrepaint = true;
        doLayout();
        repaint();
    }

    class Toolbar extends Panel implements ItemListener {
        Font coolFont = new Font("Monospaced",1,14);

        Button nextPage = new Button(nextPageLabel);
        Button prevPage = new Button(prevPageLabel);
        Button firstPage = new Button(firstPageLabel);
        Button lastPage = new Button(lastPageLabel);
        Button print = new Button(printLabel);

        Panel wp1 = new Panel();
        Panel wp11 = new Panel();
        Panel wp12 = new Panel();
        Panel wp2 = new Panel();
        java.awt.Label pages = new java.awt.Label(StringBundle.Report_Label_Pages);
        java.awt.Label iz = new java.awt.Label(StringBundle.Report_Label_Iz);
        java.awt.Label curPage = new java.awt.Label("");
        java.awt.Label numPages = new java.awt.Label("");
        java.awt.Label lnumCopies = new java.awt.Label(StringBundle.Report_Label_NumCopies);
        //TextField numCopies = new TextField();
        Choice numCopies = new Choice();
        Choice whatPrint = new Choice();
        java.awt.Label lwhatPrint = new java.awt.Label(StringBundle.Report_Label_WhatPrint);
        Choice mashtab = new Choice();
        java.awt.Label lmashtab = new java.awt.Label(StringBundle.Report_Label_Mashtab);
        public Toolbar() {
            //
            setBackground(Color.lightGray);
            setFont(new Font("Dialog",0,12));
            setLayout(new GridLayout(2,1));
            wp1.setLayout(new GridLayout(1,1));
            wp2.setLayout(null);
            wp12.setLayout(new GridLayout(1,5));
            wp11.setLayout(null);

            lmashtab.setBounds(10,10,50,15);
            mashtab.setBounds(60,7,55,15);
            mashtab.add("200%");
            mashtab.add("150%");
            mashtab.add("100%");
            mashtab.add("75%");
            mashtab.add("50%");
            mashtab.add("25%");
            mashtab.select(2);
            mashtab.addItemListener(this);

            pages.setBounds(120,10,30,15);
            curPage.setBounds(150,10,30,20);
            iz.setBounds(180,10,25,20);
            numPages.setBounds(205,10,30,20);
            lnumCopies.setBounds(235,10,40,15);
            numCopies.setBounds(275,7,50,15);
            initNumCopies();

            lwhatPrint.setBounds(10,10,55,15);
            whatPrint.setBounds(65,7,120,15);
            whatPrint.add(StringBundle.Report_Label_AllPages);
            whatPrint.add(StringBundle.Report_Label_OnePage);

            wp11.add(lmashtab);
            wp11.add(mashtab);
            wp11.add(pages);
            wp11.add(curPage);
            wp11.add(iz);
            wp11.add(numPages);
            wp11.add(lnumCopies);
            wp11.add(numCopies);
            //wp11.add(lwhatPrint);
            //wp11.add(whatPrint);

            nextPage.setFont(coolFont);
            prevPage.setFont(coolFont);
            firstPage.setFont(coolFont);
            lastPage.setFont(coolFont);
            wp12.add(firstPage);
            firstPage.addActionListener(new AL(this));
            wp12.add(prevPage);
            prevPage.addActionListener(new AL(this));
            wp12.add(nextPage);
            nextPage.addActionListener(new AL(this));
            wp12.add(lastPage);
            lastPage.addActionListener(new AL(this));
            wp12.add(print);
            print.addActionListener(new AL(this));

            wp1.add(wp11);
            //wp1.add(wp12);
            wp2.add(lwhatPrint);
            wp2.add(whatPrint);
            wp12.setLocation(200,7);
            //Dimension d = wp12.getLayout().preferredLayoutSize(wp2);
            wp12.setSize(250,20);
            wp2.add(wp12);

            add(wp1);
            add(wp2);
        }

        void initNumCopies() {
            for (int i=1;i<=10;i++) {
                numCopies.add(String.valueOf(i));
            }
            for (int i=20;i<=100;i+=10) {
                numCopies.add(String.valueOf(i));
            }
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getSource().equals(mashtab)) {
                //System.out.println("Item state changed");
                int m=100;
                try {
                    String str = mashtab.getSelectedItem();
                    str = str.substring(0,str.length()-1);
                    m=Integer.parseInt(str);
                }catch(Exception ex){}
                Report.this.setMashtab(m);
            }
        }
    }

	public Object method(String method,Object arg) throws Exception{
		if (method.equals("CURRENTPAGE")) {
			if (isPrint) return new Double(currentPage+1);
			else  return new Double(displayPage+1);
		}else
		if (method.equals("TOTALPAGES")) {
			return new Double(numPages);
		}
		return null;
	}
	public String type(){
		return "VIEWS_REPORT";
	}

	public void setValue(Object o){}
    public void setValueByName(String name, Object o) {}
    public Object getValue(){return this;}
    public Object getValueByName(String name) {return null;}

    class WorkCanvas extends Canvas {
        boolean clean = false;
		public boolean note=false;
		public String str1=null;
		public String str2=null;
        public synchronized void paint(Graphics g) {
			if (note) {
				g.setFont(new Font("Serif",2,40));
				g.setColor(new Color(0,100,0));
				g.drawString(str1,80,150);
				g.drawString(str2,80,200);
				return;
			}
            if (Report.this.image != null) {
                if (!clean) {
                    g.drawImage(Report.this.image,10,10,null);
                    //System.out.println("paint in draw mode called");
                }
                else {
                    clean = false;
                    //System.out.println("paint in clean mode called");
                    g.setColor(getBackground());
                    g.fillRect(0,0,getSize().width,getSize().height);
                }
            }
        }
        //public void update(Graphics g) {
        //    paint(g);
        //}
    }

    class AL implements ActionListener{
        Toolbar t = null;
        public AL(Toolbar t) {
            this.t = t;
        }
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(t.nextPage)) {
                Report.this.nextPageAction();
            }else
            if (e.getSource().equals(t.prevPage)) {
                Report.this.prevPageAction();
            }else
            if (e.getSource().equals(t.firstPage)) {
                Report.this.firstPageAction();
            }else
            if (e.getSource().equals(t.lastPage)) {
                Report.this.lastPageAction();
            }else
            if (e.getSource().equals(t.print)) {
                Report.this.printAction();
            }
        }
    }

}
