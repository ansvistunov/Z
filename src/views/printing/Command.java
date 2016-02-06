
package views.printing;

import java.util.*;

public class Command {
    public int command;
    public String[] sargs;
    public int[] iargs;
    public String source;
    public String dest;
    public void setCommand(int com){
        this.command = com;
        }
    public void setSource(String sou){
        this.source = sou;
        }
    public void setDest(String dest){
        this.dest = dest;
        }
    public void setIArgs(int[] ia){
        this.iargs = ia;
        }
    public void setSArgs(String[] sa){
        String s;
        boolean wasslash=false;
        if (sa==null) return;
        sargs = new String[sa.length];
        for (int i=0;i<sa.length;i++){
            //System.out.println("sa.length="+sa.length);
            StringBuffer sb = new StringBuffer();
            s = sa[i];
            //System.out.println("sa["+i+"]="+s);
            for (int j=0;j<s.length();j++){
                //System.out.println("j="+j);
                if (s.charAt(j)=='\\' && !wasslash){
                    wasslash = true;
                    continue;
                }
                if (s.charAt(j)=='k' && wasslash) {
                    sb.append("\"");
                    wasslash=false;
                    continue;
                }
                if (s.charAt(j)=='\\' && wasslash) {
                    sb.append("\\");
                    wasslash=false;
                    continue;
                }
                sb.append(s.charAt(j));
            }
            sargs[i] = sb.toString();
        }
    }
    public Command(int com,String so,String des,int[] ia,String[] sa){
        this.command = com;
        this.source = so;
        this.dest = des;
        this.iargs = ia;
        //System.out.println("before setSArgs");
        setSArgs(sa);
        //System.out.println("after setSArgs");
        }
    public Command(String command1) throws Exception {
        //this.command = command;
        int c = command1.indexOf("\"");
        if (c==-1) c=command1.length();
        StringTokenizer st = new StringTokenizer(command1.substring(0,c),",");
        command = Integer.parseInt(st.nextToken());
        source = st.nextToken();
        dest = st.nextToken();
        int count = st.countTokens();
        iargs = new int[count];
        for (int i=0;i<count;i++){
            iargs[i] = Integer.parseInt(st.nextToken());
            }
        String tail = command1.substring(c,command1.length());
        StringTokenizer st2 = new StringTokenizer(tail,"\"");
        count = st2.countTokens();
        sargs = new String[count];
        String s;
        boolean wasslash=false;

        for (int i=0;i<count;i++){
            StringBuffer sb = new StringBuffer();
            s = st2.nextToken();
            for (int j=0;j<s.length();j++){
                if (s.charAt(j)=='\\' && !wasslash){
                    wasslash = true;
                    continue;
                }
                if (s.charAt(j)=='k' && wasslash) {
                    sb.append("\"");
                    wasslash=false;
                    continue;
                }
                if (s.charAt(j)=='\\' && wasslash) {
                    sb.append("\\");
                    wasslash=false;
                    continue;
                }
                sb.append(s.charAt(j));
            }
            sargs[i] = sb.toString();

            }
    }

    public String getString() throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append(command);

        sb.append(",");
        if (source!=null) sb.append(source);
        else throw new Exception("Bad data into Command");
        sb.append(",");
        if (dest!=null) sb.append(dest);
        else throw new Exception("Bad data into Command");
        sb.append(",");
        if (iargs==null) throw new Exception("Bad data into Command");
        System.out.println(iargs.length+"=length");
        for (int i=0;i<iargs.length;i++) {
            sb.append(String.valueOf(iargs[i]));
            sb.append(",");
        }
       if (sargs==null) throw new Exception("Bad data into Command");
       for (int i=0;i<sargs.length;i++) {
           if (sargs[i]==null) sb.append("");
           else {
               StringBuffer sbinner = new StringBuffer();
               for (int j=0;j<sargs[i].length();j++) {
                    if (sargs[i].charAt(j)=='"'){
                        sbinner.append("\\k");
                        continue;
                    }
                    if (sargs[i].charAt(j)=='\\'){
                        sbinner.append("\\\\");
                        continue;
                        }
                    sbinner.append(sargs[i].charAt(j));
               }
           sb.append("\"");
           sb.append(sbinner.toString());
           }
       }
       return sb.toString();
    }

    }