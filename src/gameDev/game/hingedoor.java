package gameDev.game;

public class hingedoor extends wall{ //wall that can rotate about (x1,y1) between start orientation and specified angle
	
	public boolean state = false; //opening/closing
	public int position = 0;
	public static final int rotateFrames = 16;
	
	public double[] x2Frames = new double[rotateFrames];
	public double[] y2Frames = new double[rotateFrames];
	
	public hingedoor(double x1, double y1, double x2, double y2, double angle, int texture) {
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
			
			double frameAngle = startAngle +  (double)(rotateAngle*i/(rotateFrames-1));
			
			x2Frames[i] = x1 + length * Math.cos(frameAngle);
			y2Frames[i] = y1 + length * Math.sin(frameAngle);
			
		}
		
	}
	
	public void tickMovement(){
		if(state && position < rotateFrames - 1){
			position++;
			x2 = x2Frames[position];
			y2 = y2Frames[position];
			setOrientation();
		}
		else if(!state && position > 0){
			position--;
			x2 = x2Frames[position];
			y2 = y2Frames[position];
			setOrientation();
		}
	}
	
	public void toggle(){
		if(state) state = false;
		else state = true;
	}

}
