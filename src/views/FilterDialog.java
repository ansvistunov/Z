
package views;
import java.awt.*;
import java.awt.event.*;
import loader.*;

//Данный класс нужен для задания параметров фильтрации
//и сортировки столбцов Grid'a с помощью диалогового окна
public class FilterDialog extends Dialog {
    public static int OK=0;
    public static int RESET=1;
    public static int CANCEL=2;
    public int result=-1;
    Button ok = new Button(StringBundle.FilterDialog_Button_Ok);
    Button cancel = new Button(StringBundle.FilterDialog_Button_Cancel);
    Button reset = new Button(StringBundle.FilterDialog_Button_Reset);
    Button save = new Button(StringBundle.FilterDialog_Button_Save);
    Panel p = new Panel();//сюда будут встроены header, spane, buttonPanel
    Panel header = new Panel();
    Panel buttonPanel = new Panel();//в эту панель будут встраиваться кнопки 
    ScrollPane spane = new ScrollPane();//в эту ScrollPane встроится p1
    Panel p1 = new Panel();//в эту панель будут встраиваться субпанели
    Panel[] subpanels=null;//в эту панель будут встраиваться эл-ты управления
    int rowSize = 30;
    int bpHeight = 35;
    int left = 30;//отступы при расположении элементов
    int top = 15;//
    Grid parent = null;
    FilterStruct[] data = null;
    java.awt.Label[] titles = null;
    SmartCheckbox[][] boxes = null;
    java.awt.Label[] sortOrder = null;
    SmartTextField[][] values = null;
    //Validator[] validators = null;
    int numRows = 0;
    public FilterDialog(Grid parent, String title, int width, int height) {
        super(new Frame(), title, true);
        setLayout(new GridLayout(1,1));
        setSize(width, height);
        this.parent = parent;
        addWindowListener(new WL());
        if (parent==null) return;
        if (parent.helpArray!=null) numRows = parent.helpArray.length;
        if (numRows>0) {
            int hsortWidth = 50;
            int hfilterWidth = 50;
            int hnaimWidth = 120;
            int hbegWidth = 120;
            int hendWidth = 120;
            titles = new java.awt.Label[numRows];
            //validators = new Validator[numRows];
            sortOrder = new java.awt.Label[numRows];
            boxes = new SmartCheckbox[numRows][2];
            values = new SmartTextField[numRows][2];
            ok.addActionListener(new AL());
            cancel.addActionListener(new AL());
            reset.addActionListener(new AL());
            save.addActionListener(new AL());
            for (int i = 0;i < titles.length;i++) {
                titles[i] = new java.awt.Label(parent.columns[parent.helpArray[i]].getTitle());
                titles[i].setBounds(left+hsortWidth+hfilterWidth,0,
                    hnaimWidth,rowSize);
                //validators[i] = parent.columns[parent.helpArray[i]].validator;
                boxes[i][0] = new SmartCheckbox(boxes,sortOrder,true);
                boxes[i][1] = new SmartCheckbox(null,null,false);
                Validator valid = parent.columns[parent.helpArray[i]].validator;
                values[i][0] = new SmartTextField(valid);
                values[i][1] = new SmartTextField(valid);
                sortOrder[i] = new java.awt.Label("");
            }
            if (parent.filterData!=null) {
                if (parent.filterData.length!=numRows) throw new Error("views.FilterDialog::<init> : Number of visible columns and filterData.length must be equals !");
                data = parent.filterData;
            }else {
                data = new FilterStruct[numRows];
                for (int i=0;i<data.length;i++) data[i] = new FilterStruct();
            }
            subpanels = new Panel[numRows];
            for (int i=0;i<data.length;i++) {                
                subpanels[i] = new Panel();
                subpanels[i].setBounds(0, rowSize*i+top, getSize().width, rowSize);
                subpanels[i].setLayout(null);
            }
        
            //System.out.println("before adding controls into dialog");
            //Добавляем эл-ты управления в диалоговое окно
            p.setSize(getSize().width,getSize().height);
            p.setLayout(new BorderLayout());
            
            header.setSize(getSize().width, rowSize);
            header.setLayout(null);
            java.awt.Label hsort = new java.awt.Label(StringBundle.FilterDialog_Label_Sort);
            hsort.setBounds(left+0,0, 
                hsortWidth, rowSize);            
            java.awt.Label hfilter = new java.awt.Label(StringBundle.FilterDialog_Label_Filter);
            hfilter.setBounds(left+hsortWidth,0,
                hfilterWidth,rowSize);
            java.awt.Label hnaim = new java.awt.Label(StringBundle.FilterDialog_Label_Name);
            hnaim.setBounds(left+hsortWidth+hfilterWidth,0,
                hnaimWidth,rowSize);
            java.awt.Label hbeg = new java.awt.Label(StringBundle.FilterDialog_Label_BValue);
            hbeg.setBounds(left+hsortWidth+hfilterWidth+hnaimWidth,0,
                hbegWidth,rowSize); 
            java.awt.Label hend = new java.awt.Label(StringBundle.FilterDialog_Label_EValue);
            hend.setBounds(left+hsortWidth+hfilterWidth+hnaimWidth+hbegWidth,0,
                hendWidth,rowSize);
            header.add(hsort);
            header.add(hfilter);
            header.add(hnaim);
            header.add(hbeg);
            header.add(hend);            
            
            buttonPanel.setSize(getSize().width, bpHeight);
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(ok);
            buttonPanel.add(reset);
            buttonPanel.add(save);
            buttonPanel.add(cancel);
            
            p1.setSize(getSize().width, numRows*rowSize);
            //p1.setLayout(new GridLayout(numRows,1));
            p1.setLayout(null);
            
            for (int i=0;i<data.length;i++) {
                if (data[i].sortOrder!=0) {
                    sortOrder[i].setText("(" + (data[i].sortOrder) + ")");
                }
                boxes[i][0].setState(data[i].sort,0);
                boxes[i][0].sort = data[i].sortOrder;
                boxes[i][0].setBounds(left+0,0, 
                    hsortWidth, rowSize);            
                boxes[i][1].setState(data[i].filter,0);
                boxes[i][1].setBounds(left+hsortWidth,0,
                    hfilterWidth,rowSize);                
                sortOrder[i].setBounds(left-25,0,25,rowSize);
                values[i][0].setValue(data[i].minValue);                
                values[i][0].setBounds(left+hsortWidth+hfilterWidth+hnaimWidth,0,
                    hbegWidth,rowSize);
                values[i][1].setValue(data[i].maxValue);
                values[i][1].setBounds(left+hsortWidth+hfilterWidth+hnaimWidth+hbegWidth,0,
                    hendWidth,rowSize);                
                subpanels[i].add(sortOrder[i]);
                subpanels[i].add(boxes[i][0]);
                subpanels[i].add(boxes[i][1]);
                subpanels[i].add(titles[i]);
                subpanels[i].add(values[i][0]);
                subpanels[i].add(values[i][1]);
                p1.add(subpanels[i]);
            }
            spane.add(p1);
            
            p.add(header,"North");
            p.add(spane,"Center");
            p.add(buttonPanel,"South");
            
            add(p);//добавляем "всеобъемлющую" панель в диалоговое окно
        }
    }
    
    public FilterStruct[] getFilterStruct(){
        boolean result=false;
        for (int i = 0;i<numRows;i++) {
            data[i].sortOrder = boxes[i][0].sort;
            data[i].sort = boxes[i][0].getState();result|=boxes[i][0].getState();
            data[i].filter = boxes[i][1].getState();result|=boxes[i][1].getState();
            data[i].minValue = values[i][0].getValue();result|=(data[i].minValue!=null);
            data[i].maxValue = values[i][1].getValue();result|=(data[i].maxValue!=null);            
        }
        if (!result) return null;else return data;
    }
    
    
    
    class SmartCheckbox extends java.awt.Checkbox {
        SmartCheckbox[][] siblings=null;
        java.awt.Label[] labels=null;
        boolean isSort = false;
        int sort = 0;//приоритет  в sort order'e
        public SmartCheckbox(SmartCheckbox[][] siblings, java.awt.Label[] labels, boolean isSort) {
            super();
            this.siblings = siblings;
            this.labels = labels;
            this.isSort = isSort;
        }
        
        public int getIndex() {//возвращает порядковый номер эл-та в массиве siblings
            if (siblings==null) return -1;
            for (int i=0;i<siblings.length;i++) {
                if (siblings[i][0].equals(this)) return i;
            }
            return -1;
        }
        
        public int getMaxPrior() {
            if (siblings==null) return 0;
            int current=0;
            for (int i=0;i<siblings.length;i++) {
                if (siblings[i][0].sort > current) {                    
                    current=siblings[i][0].sort;                    
                }
            }
            return current;
        }
        
        public void setState(boolean b) {//в этом методе пересчитываем sortOrder
            super.setState(b);
            if (!isSort) return;
            if (labels==null||siblings==null) return;
            //System.out.println("item state changed!");
            if (b) {
                int ind = getIndex();
                if (ind<0) return;
                if (labels[ind].getText().equals("")) {
                    int sp = getMaxPrior();
                    sort = sp+1;
                    labels[ind].setText(new String("("+(sort)+")"));
                }
            }else {
                if (sort>0){
                    labels[getIndex()].setText("");
                    for (int i=0;i<siblings.length;i++) {
                        if (siblings[i][0].sort>sort) {
                            siblings[i][0].sort--;
                            labels[siblings[i][0].getIndex()].setText(
                                new String("(" + siblings[i][0].sort + ")"));
                        }                            
                    }
                    sort = 0;
                }
            }
        }
        
        public void setState(boolean b, int f) {
            super.setState(b);
        }
        
    }
    
    class SmartTextField extends TextField implements FocusListener{
        Validator validator;
        Object value = null;
        public SmartTextField(Validator v) {
            super();
            validator = v;            
            addFocusListener(this);
        }
        
        public void setValue(Object value) {
            this.value = value;
            try {                
                String str = validator.toString(value);
                setText(GLOBAL.c2b(str,GLOBAL.FIELD));                
            }catch(Exception ex){setText("");}
        }
        
        public Object getValue(){return value;}
        
        public void focusLost(FocusEvent e){
            //System.out.println("Focus lost");            
            if (validator!=null) {
                String str = GLOBAL.b2c(getText(),GLOBAL.FIELD);
                if (str.equals("")) {setValue(null);return;};
                try {
                    value = validator.toObject(str);
                    str = validator.toString(value);
                    setText(GLOBAL.c2b(str,GLOBAL.FIELD));
                }catch(Exception ex){setText("");}
            }
        }
        
        public void focusGained(FocusEvent e){
        }
    }
    
    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(ok)) {
                //System.out.println("button OK pressed");
                result = OK;
                FilterDialog.this.dispose();
                //FilterDialog.this.hide();
                try {
                    FilterDialog.this.parent.emptyButton.requestFocus();
                }catch(Exception ex) {}
                return;
            }
            if (e.getSource().equals(cancel)) {
                //System.out.println("button CANCEL pressed");
                result=CANCEL;
                FilterDialog.this.dispose();
                //FilterDialog.this.hide();
                try {
                    FilterDialog.this.parent.emptyButton.requestFocus();
                }catch(Exception ex) {}
                return;
            }
            if (e.getSource().equals(reset)) {
                //System.out.println("button RESET pressed");
                result=RESET;
                FilterDialog.this.dispose();
                //FilterDialog.this.hide();
                try {
                    FilterDialog.this.parent.emptyButton.requestFocus();
                }catch(Exception ex) {}
                return;
            }
            if (e.getSource().equals(save)) {
                //System.out.println("button SAVE pressed");
                try {
                    //data = getFilterStruct();
                    parent.saveFilter(getFilterStruct());
                }catch(Exception ex){System.out.println("views.Filterdialog$AL::actionPerformed : "+ex);}
                
            }
            
        }
    }    
    class WL extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            FilterDialog.this.dispose();
            //FilterDialog.this.hide();	    
            try {
                FilterDialog.this.parent.emptyButton.requestFocus();
            }catch(Exception ex) {}
        }
    }
    
    
}
