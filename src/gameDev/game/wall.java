package gameDev.game;

public class wall { //wall from (x1,y1) to (x2,y2)
	
	double x1,y1,x2,y2;
	int texture;
	
	double m,c; // y = m*x + c definition of wall line
	
	public wall(double x1,double y1,double x2,double y2, int texture){
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		this.m = (y2-y1)/(x2-y2);
		this.c = y1 - (m*x1);
		
		this.texture = texture;
	}
	
	public double[] getIntersection(){
		
		double result[] = new double[2];
		
		return result;
	}
	
	public boolean testIntersection(double x, double y){
		
		double lowX,lowY,highX,highY;
		
		if(x1>x2){
			lowX = x2; highX = x1;
		}
		else{
			lowX = x1; highX = x2;
		}
		
		if(y1>y2){
			lowY = y2; highY = y1;
		}
		else{
			lowY = y1; highY = y2;
		}
		
		if(x >= lowX && x <= highX && y >= lowY && y <= highY){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int getTextureColumn(double x, double y){
		
		double xDiff = x - x1;
		double yDiff = y - y1;
		
		int dist = (int)(Math.sqrt((xDiff*xDiff)+(yDiff*yDiff)));				
		return (dist % 64);
	}
	
}
