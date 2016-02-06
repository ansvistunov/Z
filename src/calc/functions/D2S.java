
package calc.functions;

import calc.*;

public class D2S extends BaseExternFunction {

    static String[] mnum = {"","один", "два", "три", "четыре", "пять",
    "шесть", "семь", "восемь","девять", "десять" , "одиннадцать", "двенадцать",
    "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать",
    "восемнадцать","девятнадцать"};
    static String[] fnum = {"","одна", "две"};
    static String[] decs = {"","","двадцать", "тридцать", "сорок", "пятьдесят",
    "шестьдесят","семьдесят","восемьдесят","девяносто"};
    static String[] sotni = {"","сто", "двести", "триста", "четыреста","пятьсот",
    "шестьсот","семьсот", "восемьсот", "девятьсот"};
    static String[] rs={"","тысяч", "миллион", "миллиард",
     "триллион", "квадриллион","10^18","10^21"};//разряды
    //таблица окончаний
    static String[] mokon={"ов","","а","а","а","ов","ов","ов","ов","ов"};
    static String[] fokon={"","а","и","и","и","","","","",""};
    static String[] rubs = {"ей","ь","я","я","я","ей","ей","ей","ей","ей"};
    static String[] kops = {"ек","йка","йки","йки","йки","ек","ек","ек","ек","ек"};
    static String rub="рубл";
    static String kop="копе";

    private String pr(int three,int r, boolean rubs) {//данная вспомогательная функция обрабатывает один разряд
                       //(3 цифры)
        String str="";
        if (three==0) return str;
        int num = three/100;
        if (num>0) str = sotni[num];
        num = three%100;
        if (num==0) {
                if (r==0) {
                str+=" " +rs[r];
            }else
            if (r==1) {
                str+=" " +rs[r]+fokon[0];
            }else {
                str+=" " +rs[r]+mokon[0];
            }
            return str.trim();
        }

        int num2=0;
        if (num<20) {
            if (num>9) num2=0; else num2=num;
            if (r!=1) {//разряды - не тысяч
                if (rubs || num>2) str+=" " + mnum[num];
                else str+=" " + fnum[num];
                //if (num<10) str+=
            }
            else {
                if (num<3) str+=" " + fnum[num];
                else str+=" " + mnum[num];
            }
        }else {
            str+=" " + decs[num/10];
            if (r==1)
                if ((num%10)<3) str+=" " +fnum[num%10];
                else str+=" " + mnum[num%10];
            else {
                if (!rubs && (num%10)<3) str +=" " +fnum[num%10];
                else str+=" " +mnum[num%10];
            }
            num2=num%10;
        }
        str=str.trim();
        if (r==0) {
            str+=" " +rs[r];
        }else
        if (r==1) {
            str+=" " +rs[r]+fokon[num2];
        }else {
            str+=" " +rs[r]+mokon[num2];
        }
        return str.trim();
    }
    private int getend(long l) {
        if ((l%100) < 10) return (int)(l%100);
        if (l%100>=10 && l%100<20) return 0;
        return (int)(l%10);
    }
    
    private String d2s(Double number) {
        if (number.doubleValue() > (double)Long.MAX_VALUE ||
        -number.doubleValue() > (double)Long.MAX_VALUE) return null;
        int r=0;
        String str="";
        String sign="";
        if (number.doubleValue()<0) {
            number = new Double(-number.doubleValue());
            sign="минус ";
        }
        //округление до целых долей копейки
            double tmp = number.doubleValue();
            number = new Double(((double)Math.round((tmp*100)))/100);
        //
        long l = number.longValue();
        long n = l;
        while(true) {
            n/=1000;
            if (n>0) r++;
            else break;
        }
        int curr;//текущая тройка цифр, соотв. разряда
        n=l;
        for (int i=0;i<=r;i++) {//цикл по разрядам
            //mul*=1000;
            curr = (int)(n%1000);
            n/=1000;
            str = pr(curr,i,true) + " " + str;
        }
        if (str.trim().length()>0) {
            StringBuffer temp = new StringBuffer(str.trim());
            temp.setCharAt(0,Character.toUpperCase(temp.charAt(0)));
            str =  temp + " " +rub+rubs[getend(l)];
        }
        else {//кол-во рублей=0
            str = "Ноль рублей"; 
        }
        
        int k = (int)Math.round((number.doubleValue() - l)*100);
        //if (k>0) {
            //System.out.println("k="+k);
        //    str+=" " + pr(k,0,false)+" "+kop+kops[getend(k)];
        //}else {
        //    str+=" 00 копеек";
        //}
        
        if (k>0) {           
            String zero = "";
			if (k<10) zero = "0";
			str+=" "+zero+k+" "+kop+kops[getend(k)];
        }else {
            str+=" 00 копеек";
        }

        return sign+str.trim();
    }



    public Object eval() throws Exception {
        Object o = OP.doHardOP(expr);
        if (!(o instanceof Double)) throw new RTException("CastException","D2S");
        return d2s((Double)o);
    }
}
