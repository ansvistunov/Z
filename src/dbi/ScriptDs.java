package dbi; 
import java.util.*; 
import java.sql.*; 
import loader.*;
import calc.*;
import java.text.*;
import calc.objects.*;
import java.io.*;
public class ScriptDs extends DATASTORE{
    String retScript;
    String upScript;
    String newScript;
    public void setScripts(String ret,String update,String newSc){
        retScript = ret;
        upScript = update;
        newScript = newSc;
    }
	public ScriptDs(){
		data = new Vector();
		operation = new Hashtable();
		int row = 0;
		types = new int[0]; 
		colName = new String[0]; 
		colLabels = new String[0];
		crow = 0;
		countRows = data.size();
		keys = new int[countRows];
		skeys = new int[countRows];
		for (int i=0;i<keys.length;i++) {keys[i] = i;skeys[i] = i;}; 

	}
    public int retrieve(){
		if((sortcolumn!=null) && (direction!=null)){
		    setSort();
		}
		if (handler!=null) handler.notifyHandler(null);
        if (retScript==null) return 0;
        try{
            Calc c = new Calc(retScript);
	        //System.out.println(aliases);
		    c.eval(aliases);
		    //System.out.println(aliases);
	}catch(Exception e){e.printStackTrace();}
	return 0;
	}
    public void update(){
        if (upScript==null) return;
        try{
            Calc c = new Calc(upScript);
		    c.eval(aliases);
		}catch(Exception e){e.printStackTrace();}
        }
    public int newRow(){
        int ret = super.newRow();
        if (newScript==null) return ret;
        try{
            Calc c = new Calc(newScript);
		    c.eval(aliases);
		}catch(Exception e){e.printStackTrace();}
		return ret;
        }
    
    }








