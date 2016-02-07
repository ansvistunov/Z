package rml;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import calc.RTException;
import loader.GLOBAL;
import rml.Proper.PropHash.HashRow;


public class RmlMem2File {
	//static ParserObserver observer = new ParserObserver();
	static char[] blanks={' ',' ',' ',' ',' ',' '};
	static String[] filerProps = {"LEFT","TOP","WIDTH","HEIGHT"};
	static String[] tags = {"FIELD","LABEL","BUTTON"};
	static int srcPos = 0;
	static int destPos = 0;
	static char[] text;
	static char[] result;
	
	public static Proper propFromFile(Hashtable aliases, boolean samefile) {
		String n = document.Document.getcurd().myname;
		String p = document.Document.getcurd().mypath;
		System.out.println("n="+n+" p="+p);
		srcPos = destPos = 0;
		//observer.clear();
		Vector<String> tmp;
		
		//Proper proper = (Proper) aliases.get("###propers###");
		
		tmp = new Vector<String> (Arrays.asList(tags));
		
		
		try {
			text = GLOBAL.loader.loadByName_chars(p+"/"+n, true);
			System.out.println("text.length="+text.length);
			Proper proper =  rml.Parser.createProper(text, null);
			
			/*Vector<RmlElement> elements = observer.getElements();*/
			// оставл€ем только интересующие нас тэги ,те в которых нет алиасов - тоже убираем
			
			tmp = new Vector<String> (Arrays.asList(tags));
			
			/*for (int i=0; i<elements.size();){
				RmlElement c = elements.elementAt(i);
				//System.out.println("tag="+c.tag);
				if (!tmp.contains(c.tag) || c.getAlias() == null) {
					elements.removeElementAt(i);
					//System.out.println("tag="+c.tag+" removed");
				}else{
					i++;
				}
			}*/
			
			Iterator<Proper> itp = proper.filerTags(tmp);
			
			//System.out.println(elements);
			
			int nac = needAddChar(itp,aliases);
			
			System.out.println("nac="+nac);
			if (nac>0) 
				result = new char[text.length+nac];
			else result = text;
			
			
			//Vector<String> f = new Vector(); f.add("LEFT");f.add("TOP");
			
			srcPos = 0;
			destPos = 0;
			
			itp = proper.filerTags(tmp);
			
			//System.out.println(proper.toText());
			
			tmp = new Vector<String> (Arrays.asList(filerProps));
			
			while(itp.hasNext()) {
				Proper pp = itp.next();
				Iterator<HashRow> iii =  pp.filerProps(tmp);
				Object alias = pp.get("ALIAS");
				System.out.println("alias for "+pp+" "+alias);
				if (alias == null) continue;
				
				Object o = aliases.get(alias); //вз€ли компонент, который редактируем
				Rectangle rec = null;
				if (o instanceof Component) {
					rec = ((Component)o).getBounds();
				}
				while(iii.hasNext()) {
					HashRow hr = iii.next();
					partCopy(rec,hr);
					System.out.println(hr);
				}
					
			}
			
			
			
			/*for (int i=0;i<elements.size();i++) { //цикл по тэгам (элементам)
				RmlElement rmlelem = elements.get(i);
				Iterator<RmlElementProp> it = rmlelem.filerElements(tmp);
				Object o = aliases.get(rmlelem.getAlias()); //вз€ли компонент, который редактируем
				Rectangle rec = null;
				if (o instanceof Component) {
					rec = ((Component)o).getBounds();
				}
				while (it.hasNext()) {	//цикл по свойствам
					RmlElementProp ep = it.next();
					partCopy(rec,ep);
					System.out.println(ep);
					
				}
			}*/
			
			
			
			
			
			System.arraycopy(text, srcPos, result, destPos, text.length - srcPos); //копируем хвост
			String outfile;
			if (samefile) {
				outfile=p+"/"+n;
			}else{
				try{
					Frame f = new Frame();
					FileDialog fd = new FileDialog(f,"",FileDialog.SAVE);
					
					String file = null;
					String path;
						fd.show();
						path = fd.getDirectory();
						file = fd.getFile();
						if ( file == null) throw new RTException("Cancel","");
						outfile = file;
						System.out.println("outfile:"+outfile);
				}finally{
				}
				
			}
			
			GLOBAL.loader.write(outfile,"CP1251" , result); //TODO что-то нужно делать с кодировкой
			
			
			return proper;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}; 
	
	
	static void partCopy(Rectangle rec, HashRow rmlprop) {
		int memlen;
		int flen;
		int diff; 
		int len;
		
		int foo = -1;
		
		switch(rmlprop.name.toString()){
		case "LEFT": foo = rec.x; break;
		case "TOP": foo = rec.y; break;
		case "WIDTH": foo = rec.width;break;
		case "HEIGHT": foo = rec.height; break;
		}
		
			len = rmlprop.s - srcPos;
			System.arraycopy(text, srcPos, result, destPos, len ); // начало
			srcPos += len;
			destPos += len;
			
			memlen = getPropLength(foo);
			flen = getPropLength(getIntProp(rmlprop.value));
			diff = flen - memlen;
			
			int blen = (rmlprop.e-rmlprop.s);
			System.arraycopy(blanks, 0, result, destPos, blen); //забили пробелами место под значение свойства
			
			if (blen > memlen && blen > flen) {
				srcPos += (blen-flen);
				destPos +=(blen-flen);
			}
			
			/*int blen = (rmlrec.ex-rmlrec.sx) - flen;
			if (blen>0) {System.arraycopy(blanks, 0, result, destPos, blen);
			destPos += blen;}*/
			
			
			System.arraycopy(String.valueOf(foo).toCharArray(), 0, result, destPos, memlen); // значение
			srcPos += flen;
			destPos += memlen;
			//
			
			if (diff > 0) {
				System.arraycopy(blanks, 0, result, destPos, diff); // забили
																	// пробелами,
																	// если
																	// длина
																	// значени€
																	// уменьшилась
				destPos += diff;
			}
			
			

	}
	
	static int getIntProp(Object value) {
		int i = Integer.parseInt(value.toString());
		return i;
	}
	
	static int needAddChar(Iterator<Proper> itp, Hashtable aliases){
		
		String key;
		int meml,filel;
		int addchars = 0;
		int c;
		
		while(itp.hasNext()){
			Proper re = itp.next();
			Object o = aliases.get(re.tag);
			 if (o instanceof Component) {
				 Rectangle rec = ((Component)o).getBounds();
				 
				 //RmlRectangle rmlrec = recs.elementAt(i);
				 
				 meml = getPropLength(rec.x);
				 filel = getPropLength(getIntProp(re.get("LEFT")));
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.y);
				 filel = getPropLength(getIntProp(re.get("TOP")));
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.width);
				 filel = getPropLength(getIntProp(re.get("WIDTH")));
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.height);
				 filel = getPropLength(getIntProp(re.get("HEIGHT")));
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
			 }
		}
		
		return addchars;
		
	};
	
	static int getPropLength(int value) {
		if (value>=0 && value <10) return 1;
		if (value >=10 && value<100) return 2;
		if (value >=100 && value<1000) return 3;
		if (value>=100 && value<10000) return 4;
		return 0;
		
	}

}
