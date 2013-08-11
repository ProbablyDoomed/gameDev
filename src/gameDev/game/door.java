package gameDev.game;

public class door extends wall{ //wall that can rotate about (x1,y1) between start orientation and specified angle
	
	public boolean moving = false; //opening/closing
	public boolean state = false; //open/closed
	public int position = 0;
	//public double rotateAngle;
	public static final int rotateFrames = 5;
	
	public double[] x2Frames = new double[rotateFrames];
	public double[] y2Frames = new double[rotateFrames];
	
	public door(double x1, double y1, double x2, double y2, double angle, int texture) {
		super(x1, y1, x2, y2, texture);
		
		double rotateAngle = angle;
		double length = Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
		double startAngle;
		
		if (vertical){
			if(y2>y1){
				startAngle = Math.PI/2;
			}
			else{
				startAngle = -Math.PI/2;
			}
		}
			
		else startAngle = Math.atan(m);
		
		
		
		x2Frames[0] = x2;
		y2Frames[0] = y2;
		for (int i=1; i<rotateFrames; i++){
			
			double frameAngle = startAngle +  (i*rotateAngle/rotateFrames);
			
			x2Frames[i] = x1 + length * Math.cos(frameAngle);
			y2Frames[i] = y1 + length * Math.sin(frameAngle);
			
		}
		
	}
	
	public void tickMovement(){
		//double xNew = x2 + 	
	}

}
