
package views.printing;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.text.AttributedCharacterIterator;

import views.UTIL;

public class RGraphics extends Graphics {
     DataOutputStream dos = null;
     protected Font font = null;
     protected Color color = null;
     RGraphics(DataOutputStream dos) {
         this.dos = dos;   
     }
     
     protected void writeCommand(String com) {
         //System.out.println("inside RGraphics.writeCommand");
         //System.out.println("command="+com);    
         if (dos!=null){
            try{
            dos.writeUTF(com);
            }catch(Exception e){};
         }
     }
     
     public void flush() {
         if (dos!=null) {
            try{dos.flush();}catch(Exception e){}
         }
     }
     
     protected void writeCommand(Command com) {         
         if (dos!=null){
            try{
            dos.writeUTF(com.getString());
            }catch(Exception e){};
         }
     }

    
     public  void clearRect(int x,
                                int y,
                                int width,
                                int height){
     }

     public  void clipRect(int x,
                                   int y,
                                   int width,
                                   int height){
     }

     public  void copyArea(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int dx,
                                   int dy){
     }

     public  Graphics create(){
         return this;   
     }

     public Graphics create(int x,
                            int y,
                            int width,
                            int height){
         return this;
     }

     public  void dispose(){
         //System.out.println("inside RGraphics.dispose()");
         writeCommand(c.END_PAGE+","+"1"+","+"1");   
         flush();
     }


     public void draw3DRect(int x,
                            int y,
                            int width,
                            int height,
                            boolean raised){
     }

     public  void drawArc(int x,
                                  int y,
                                  int width,
                                  int height,
                                  int startAngle,
                                  int arcAngle){
     }

     public void drawBytes(byte data[],
                           int offset,
                           int length,
                           int x,
                           int y){
     }

     public void drawChars(char data[],
                           int offset,
                           int length,
                           int x,
                           int y){}


     public  boolean drawImage(Image img,
                                       int x,
                                       int y,
                                       Color bgcolor,
                                       ImageObserver observer){
         return true;
     }


     public  boolean drawImage(Image img,
                                       int x,
                                       int y,
                                       ImageObserver observer){
         return true;
     }


     public  boolean drawImage(Image img,
                                       int x,
                                       int y,
                                       int width,
                                       int height,
                                       Color bgcolor,
                                       ImageObserver observer){
         return true;
     }


     public  boolean drawImage(Image img,
                                       int x,
                                       int y,
                                       int width,
                                       int height,
                                       ImageObserver observer){
         return true;
     }

     public  boolean drawImage(Image img,
                                       int dx1,
                                       int dy1,
                                       int dx2,
                                       int dy2,
                                       int sx1,
                                       int sy1,
                                       int sx2,
                                       int sy2,
                                       Color bgcolor,
                                       ImageObserver observer){
         return true;
     }

     public  boolean drawImage(Image img,
                                       int dx1,
                                       int dy1,
                                       int dx2,
                                       int dy2,
                                       int sx1,
                                       int sy1,
                                       int sx2,
                                       int sy2,
                                       ImageObserver observer){
         return true;
     }


     public  void drawLine(int x1,
                                   int y1,
                                   int x2,
                                   int y2){
         writeCommand(c.drawLine+","+"1"+","+"1"+","+
            x1+","+y1+","+x2+","+y2);
     }
     
     public  void drawLine(int x1,
                                   int y1,
                                   int x2,
                                   int y2,
                                   int w ){
         writeCommand(c.drawLine+","+"1"+","+"1"+","+
            x1+","+y1+","+x2+","+y2+","+w);
     }

     public  void drawOval(int x,
                                   int y,
                                   int width,
                                   int height){}

     public  void drawPolygon(int xPoints[],
                                      int yPoints[],
                                      int nPoints){}


     public void drawPolygon(Polygon p){}

     public  void drawPolyline(int xPoints[],
                                       int yPoints[],
                                       int nPoints){}

     public void drawRect(int x,
                          int y,
                          int width,
                          int height){
         writeCommand(c.drawRect+","+"1"+","+"1"+","+
                x+","+y+","+width+","+height);
     }


     public  void drawRoundRect(int x,
                                        int y,
                                        int width,
                                        int height,
                                        int arcWidth,
                                        int arcHeight){}

     public  void drawString(String str,
                                     int x,
                                     int y){
         writeCommand(c.drawString+","+"1"+","+"1"+","
         +x+","+y+",\""+UTIL.toCommandString(str));
         /*Command com = null;
         try {
            com = new Command(c.drawString,"1","1",new int[]{x,y},new String[]{str});
            writeCommand(com.getString());
         }catch(Exception e){}*/
         
     }

     public void fill3DRect(int x,
                            int y,
                            int width,
                            int height,
                            boolean raised){}


     public  void fillArc(int x,
                                  int y,
                                  int width,
                                  int height,
                                  int startAngle,
                                  int arcAngle){}

     public  void fillOval(int x,
                                   int y,
                                   int width,
                                   int height){}


     public  void fillPolygon(int xPoints[],
                                      int yPoints[],
                                      int nPoints){}


     public void fillPolygon(Polygon p){}

     public  void fillRect(int x,
                                   int y,
                                   int width,
                                   int height){
         writeCommand(c.fillRect+","+"1"+","+"1"+","+
                x+","+y+","+width+","+height);
     }


     public  void fillRoundRect(int x,
                                        int y,
                                        int width,
                                        int height,
                                        int arcWidth,
                                        int arcHeight){}


     public void finalize(){}


     public  Shape getClip(){
         return null;   
     }



     public  Rectangle getClipBounds(){
         return null;
     }


     public Rectangle getClipRect(){
         return null;    
     }


     public  Color getColor(){
         return null;
     }


     public  Font getFont(){
         return null;   
     }


     public FontMetrics getFontMetrics(){
         if (font!=null) return getFontMetrics(font);
         else return null;   
     }


     public  FontMetrics getFontMetrics(Font f){
         return Toolkit.getDefaultToolkit().getFontMetrics(f);
     }

     public  void setClip(int x,
                                  int y,
                                  int width,
                                  int height){
         writeCommand(c.setClip+","+"1"+","+"1"+","+x+","+y+","+width+","+height);
     }

     public  void setClip(Shape clip){
         Rectangle r = null;
         if (clip!=null) r = clip.getBounds();
         if (r!=null) setClip(r.x,r.y,r.width, r.height);      
     }


     public  void setColor(Color color){
         this.color = color;
         if (color!=null)
            writeCommand(c.setColor+","+"1"+","+"1"+","+color.getRGB());
     }



     public  void setFont(Font font){
        this.font = font;
        if (font==null) return;
        writeCommand(c.setFont+","+"1"+","+"1"+","+
        font.getStyle()+","+font.getSize()+",\""+font.getName());
        
     }


     public  void setPaintMode(){}


     public  void setXORMode(Color c1){}


     public String toString(){
         return "views.printing.RGraphics";
     }


     public  void translate(int x,int y){
         writeCommand(c.translate+","+"1"+","+"1"+","+x+","+y);
     }

	@Override
	public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
