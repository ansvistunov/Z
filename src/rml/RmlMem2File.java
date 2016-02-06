package rml;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import loader.GLOBAL;
import rml.RmlRectangle.vertex;

public class RmlMem2File {
	static ParserObserver observer = new ParserObserver();
	static char[] blanks={' ',' ',' ',' ',' ',' '};
	static int srcPos = 0;
	static int destPos = 0;
	static char[] text;
	static char[] result;
	
	public static Proper propFromFile(Hashtable aliases, String outfile) {
		String n = document.Document.getcurd().myname;
		String p = document.Document.getcurd().mypath;
		System.out.println("n="+n+" p="+p);
		srcPos = destPos = 0;
		observer.clear();
		
		try {
			text = GLOBAL.loader.loadByName_chars(p+"/"+n, true);
			System.out.println("text.length="+text.length);
			Proper prop =  rml.Parser.createProper(text, null, observer);
			
			Vector<String> keys = observer.getKeys();
			System.out.println(keys);
			Vector<RmlRectangle> recs = observer.getRecs();
			System.out.println(recs);
			
			
			int nac = needAddChar(keys,recs,aliases);
			
			System.out.println("nac="+nac);
			if (nac>0) 
				result = new char[text.length+nac];
			else result = text;
			
			//result = new char[text.length+nac]; //TODO эту строчку закомментировать!!!!
			
			
			
			String key;
			srcPos = 0;
			destPos = 0;
			
			for(int i=0;i<keys.size();i++){
				 key = keys.elementAt(i);
				 Object o = aliases.get(key);
				 if (o instanceof Component) {
					 Rectangle rec = ((Component)o).getBounds();
					 System.out.println(rec);
					 RmlRectangle rmlrec = recs.elementAt(i);
					 
					 RmlRectangle.vertex v = rmlrec.getMin();
					 partCopy(rec,rmlrec,v);
					 
					 v = rmlrec.getMin();
					 partCopy(rec,rmlrec,v);
					 
					 v = rmlrec.getMin();
					 partCopy(rec,rmlrec,v);
					 
					 v = rmlrec.getMin();
					 partCopy(rec,rmlrec,v);
					 
					 
				 }
			}
			
			System.arraycopy(text, srcPos, result, destPos, text.length - srcPos); //копируем хвост
			if (outfile == null) outfile=p+"/"+n;
			
			GLOBAL.loader.write(outfile,"CP1251" , result); //TODO что-то нужно делать с кодировкой
			
			
			return prop;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}; 
	
	
	static void partCopy(Rectangle rec, RmlRectangle rmlrec, vertex v) {
		int memlen;
		int flen;
		int diff; 
		int len;
		switch (v) {
		case X:
			len = rmlrec.sx - srcPos;
			System.arraycopy(text, srcPos, result, destPos, len ); // начало
			srcPos += len;
			destPos += len;
			memlen = getPropLength(rec.x);
			flen = getPropLength(rmlrec.x);
			diff = flen - memlen;
			
			int blen = (rmlrec.ex-rmlrec.sx);
			System.arraycopy(blanks, 0, result, destPos, blen);
			
			/*int blen = (rmlrec.ex-rmlrec.sx) - flen;
			if (blen>0) {System.arraycopy(blanks, 0, result, destPos, blen);
			destPos += blen;}*/
			
			
			System.arraycopy(String.valueOf(rec.x).toCharArray(), 0, result, destPos, memlen); // значение
			srcPos += flen;
			destPos += memlen;
			//
			
			if (diff > 0) {
				System.arraycopy(blanks, 0, result, destPos, diff); // забили
																	// пробелами,
																	// если
																	// длина
																	// значения
																	// уменьшилась
				destPos += diff;
			}
			rmlrec.sx = Integer.MAX_VALUE;
			break;
		case Y:
			len = rmlrec.sy - srcPos;
			System.arraycopy(text, srcPos, result, destPos, len); // начало
			srcPos += len;
			destPos += len;
			memlen = getPropLength(rec.y);
			flen = getPropLength(rmlrec.y);
			diff = flen - memlen;
			
			
			blen = (rmlrec.ey-rmlrec.sy);
			System.arraycopy(blanks, 0, result, destPos, blen);
			/*blen = (rmlrec.ey-rmlrec.sy) - flen;
			if (blen>0) {System.arraycopy(blanks, 0, result, destPos, blen);
			destPos += blen;}*/
			
			System.arraycopy(String.valueOf(rec.y).toCharArray(), 0, result, destPos, memlen); // значение
			srcPos += flen;
			destPos += memlen;
			//
			
			if (diff > 0) {
				System.arraycopy(blanks, 0, result, destPos, diff); // забили
																	// пробелами,
																	// если
																	// длина
																	// значения
																	// уменьшилась
				destPos += diff;
			}
			rmlrec.sy = Integer.MAX_VALUE;
			break;
		case H:
			len = rmlrec.sh - srcPos;
			System.arraycopy(text, srcPos, result, destPos, len); // начало
			srcPos += len;
			destPos += len;
			memlen = getPropLength(rec.height);
			flen = getPropLength(rmlrec.h);
			diff = flen - memlen;
			
			blen = (rmlrec.eh-rmlrec.sh);
			System.arraycopy(blanks, 0, result, destPos, blen);
			/*blen = (rmlrec.eh-rmlrec.sh) - flen;
			if (blen>0) {System.arraycopy(blanks, 0, result, destPos, blen);
			destPos += blen;}*/
			
			System.arraycopy(String.valueOf(rec.height).toCharArray(), 0, result, destPos, memlen); // значение
			srcPos += flen;
			destPos += memlen;
			//System.arraycopy(blanks, 0, result, destPos, rmlrec.eh-rmlrec.sh);
			
			if (diff > 0) {
				System.arraycopy(blanks, 0, result, destPos, diff); // забили
																	// пробелами,
																	// если
																	// длина
																	// значения
																	// уменьшилась
				destPos += diff;
			}
			rmlrec.sh = Integer.MAX_VALUE;
			break;
		case W:
			len = rmlrec.sw - srcPos;
			System.arraycopy(text, srcPos, result, destPos, len); // начало
			srcPos += len;
			destPos += len;
			memlen = getPropLength(rec.width);
			flen = getPropLength(rmlrec.w);
			diff = flen - memlen;
			
			
			blen = (rmlrec.ew-rmlrec.sw);
			System.arraycopy(blanks, 0, result, destPos, blen);
			/*blen = (rmlrec.ew-rmlrec.sw) - flen;
			if (blen>0) {System.arraycopy(blanks, 0, result, destPos, blen);
			
			destPos += blen;}*/
			
			System.arraycopy(String.valueOf(rec.width).toCharArray(), 0, result, destPos, memlen); // значение
			srcPos += flen;
			destPos += memlen;
			//System.arraycopy(blanks, 0, result, destPos, rmlrec.ew-rmlrec.sw);
			
			if (diff > 0) {
				System.arraycopy(blanks, 0, result, destPos, diff); // забили
																	// пробелами,
																	// если
																	// длина
																	// значения
																	// уменьшилась
				destPos += diff;
			}
			rmlrec.sw = Integer.MAX_VALUE;
			break;

		}

	}
	
	static int needAddChar(Vector<String> keys, Vector<RmlRectangle> recs, Hashtable aliases){
		
		String key;
		int meml,filel;
		int addchars = 0;
		int c;
		
		for (int i=0;i<keys.size();i++){
			 key = keys.elementAt(i);
			 Object o = aliases.get(key);
			 if (o instanceof Component) {
				 Rectangle rec = ((Component)o).getBounds();
				 RmlRectangle rmlrec = recs.elementAt(i);
				 
				 meml = getPropLength(rec.x);
				 filel = getPropLength(rmlrec.x);
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.y);
				 filel = getPropLength(rmlrec.y);
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.width);
				 filel = getPropLength(rmlrec.w);
				 c = meml - filel;
				 
				 if (c>0) addchars+=c;
				 
				 meml = getPropLength(rec.height);
				 filel = getPropLength(rmlrec.h);
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
