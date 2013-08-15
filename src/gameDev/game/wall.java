package gameDev.game;

public class wall { //wall from (x1,y1) to (x2,y2)
	
	double x1,y1,x2,y2;	
	double m,c; // y = m*x + c definition of wall line
	boolean vertical;
	
	int texture;
	
	public wall(double x1,double y1,double x2,double y2, int texture){
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		setOrientation();
		
		this.texture = texture;
		
	}
	
	public void setOrientation() {
		if(x1==x2){
			this.vertical = true;
		}
		else{
			this.m = (y2-y1)/(x2-x1);
			this.c = y1 - (m*x1);
			this.vertical = false;
		}	
	}

	public double[] getIntersectPoint(double m2, double c2/*, double angle*/){
		
		double result[] = new double[2];
		
		//double m2 = Math.atan(angle); //y = m*x + c for ray 
		//double c2 = y - (m2*x);
		if(vertical){
			result[0] = x1;
		}
		else{
			result[0] = (c - c2)/(m2 - m); //x
		}
		result[1] = (m2 * result[0]) + c2;
		
		
		return result;
	}
	
	public boolean testIntersection(double x, double y, double margin){
		
		double lowX,highX;
		double lowY,highY;
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
		
		
		if( x >= lowX - margin && x <= highX + margin && y >= lowY - margin && y <= highY + margin){
			if(vertical){		

				if(x >= x1 - margin && x <= x1 + margin  ){
					return true;
				}
				else
				{
					return false;
				}
				
			}
			
			else if(m == 0){
					
				if(y >= y1 - margin && y <= y1 + margin){
					return true;
				}
				else
				{
					return false;
				}
			}	
			
			else{
				if(y >= m*x + c - margin && y <= m*x + c + margin){
					return true;
				}
				else
				{
					return false;
				}
			}
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
	
	public void toggle(){
		
	}

	public void tickMovement() {
		// TODO Auto-generated method stub
		
	}
	
}
