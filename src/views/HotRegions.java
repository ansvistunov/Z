
package views;
public class HotRegions {
    private int[] regsAll; 
    private int[] regs;
    int sizeReg = 2;
    int beg = 0;
    //int end;
    int offset = 0;
    public HotRegions(int[] regs) {
        this.regsAll = regs;
        this.regs = this.regsAll;
        adjustRegs();
        
        this.regs = new int[regsAll.length];
        System.arraycopy(this.regsAll,0,this.regs,0,this.regsAll.length);        
        
    }
    /*public HotRegions(int[] regs, int sizeReg) {
        this.regs = regs;
        this.sizeReg = sizeReg;
        adjustRegs();
    }*/
    
    /*Перемещает области с номером >=reg на delta пикселов*/
    public void moveReg(int reg, int delta) {
        try {
            int newdelta = delta;
            if (reg>0) {
                if(regs[reg]+delta-regs[reg-1]-1<(2*sizeReg)){
                    newdelta = -regs[reg]+regs[reg-1]+1+(2*sizeReg);
                }
            }
            if (reg==0) {
                if ((regs[0]-offset)+delta-1<2*sizeReg) 
                newdelta = 2*sizeReg+1-(regs[0]-offset);
            }
            for (int i=reg;i<regs.length;i++) {
                regs[i] += newdelta;
                
            }
        }
        catch(Exception e) {
            System.out.println("~views.HotRegions::moveReg : "+e);
        }
    }
    
    /*Производит выравнивание областей*/
    public void adjustRegs() {
        for (int i = 0;i<regs.length;i++) moveReg(i,0);
    }
    
    /*Возвращает номер области(начиная с 0), к которой принадлежит
    данная точка.Если точка не принадлежит ни одной из областей, 
    возвращается -1*/
    public int getPointReg(int point) {
        point+=offset;
        for (int i = 0;i<regs.length;i++) {
            if ((point>=regs[i]-sizeReg) && (point<=regs[i]+sizeReg)){
                return i;
            }
        }
        return -1;
    }
    public int getXReg(int i) {
        if ((i>=0) && (i<regs.length)) return regs[i]-offset;
        else return -100;
    }
    
    public void setFrame (int beg) {
        //System.out.println("inside setframe");
        try {
            System.arraycopy(regs,0,regsAll,this.beg,regs.length);
            if (beg<0) beg = 0; 
            regs = new int[regsAll.length-beg];
            System.arraycopy(regsAll,beg,regs,0,regs.length);
            this.beg=beg;            
            if (beg>0) offset=/*regs[beg]-*/regsAll[beg-1];
            else offset=0;
        }
        catch(Exception e) {System.out.println("~views.HotRegions::setFrame : "+e);}
    }       
}
