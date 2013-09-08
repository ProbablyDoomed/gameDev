package gameDev.game;

public class slidedoor extends wall {
	
	public boolean state = false; //opening/closing
	public int position = 0;
	public static final int motionFrames = 16;
	
	public double x1Origin;
	public double y1Origin;
	public double x2Origin;
	public double y2Origin;
	
	public double xShift,yShift;

	public slidedoor(double x1, double y1, double x2, double y2, double distance, int texture) {
		super(x1, y1, x2, y2, texture);
		
		this.x1Origin = x1;
		this.x2Origin = x2;
		this.y1Origin = y1;
		this.y2Origin = y2;
		
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
		
		xShift = Math.cos(startAngle) * distance/motionFrames;
		yShift = Math.sin(startAngle) * distance/motionFrames;
	
	}
	
	public void tickMovement(){
		if(state && position < motionFrames){
			position++;
			//setOrientation();
		}
		else if(!state && position > 0){
			position--;
			//setOrientation();
		}
		x1 = x1Origin + xShift*position;
		y1 = y1Origin + yShift*position;
		x2 = x2Origin + xShift*position;
		y2 = y2Origin + yShift*position;
	}
	
	public void toggle(){
		if(state) state = false;
		else state = true;
	}
	
}
