package rml;

public class RmlRectangle {
@Override
	public String toString() {
		return "RmlRectangle [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + ", sx=" + sx + ", ex=" + ex + ", sy="
				+ sy + ", ey=" + ey + ", sw=" + sw + ", ew=" + ew + ", sh=" + sh + ", eh=" + eh + "]";
	}
public int x,y,w,h;
public int sx,ex,sy,ey,sw,ew,sh,eh;

public enum vertex {X,Y,W,H};

vertex getMin(){
	if (sx <=sy && sx<=sh && sx<=sw) return vertex.X;
	if (sy <=sx && sy<=sh && sy<=sw) return vertex.Y;
	if (sh <=sx && sh<=sy && sh<=sw) return vertex.H;
	if (sw <=sx && sw<=sy && sw<=sh) return vertex.W;
	
	return null;
}

	
}
