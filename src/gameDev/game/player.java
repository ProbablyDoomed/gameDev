package gameDev.game;


public class player {
	
	public double x,y;
	public double xVel=0 , yVel=0;
	public static final double xVelLimit = 0.5, yVelLimit = xVelLimit;
	public static final int moveRes = 1;
	public static final double acceleration = 5;
	public static final double friction = 0.3;
	
	public double heading;
	public static final double turnRate = 0.03;
	
	public double collideRadius = 40;
	
	public player(int startX, int startY, double startFacing){
		this.x = startX; 
		this.y = startY;
		this.heading = startFacing;	
	}
	
	public void accel( double dir ){
		xVel += acceleration * Math.cos(dir);
		yVel += acceleration * Math.sin(dir);
	}
	
	public void applyFriction() {
			xVel *= friction;
			yVel *= friction;
	}
	
	public void turn(double scale){
		heading += scale*turnRate;
		
		if (heading >= Math.PI) heading -= 2*Math.PI;
		if (heading <= -Math.PI) heading += 2*Math.PI;
	}
	
	public void tickMovement( level world ){
		for (int i=0; i<moveRes; i++){
			double xNew = x + xVel/moveRes;
			double yNew = y + yVel/moveRes;
			
			boolean xCollide = false;
			boolean yCollide = false;
			
			for(int w = 0; w < world.walls.size(); w++){ //for each wall in level
				if( world.walls.get(w).testIntersection(xNew, y, collideRadius)){
					xCollide = true;
				}
				if( world.walls.get(w).testIntersection(x, yNew, collideRadius)){
					yCollide = true;
				}
			}
			if(xCollide) {
				//xVel = 0;
	/*			if (x > xNew){
					x = xNew + collideRadius;
				}
				else{
					x = xNew - collideRadius;
				}*/
			}
			else x = xNew;
			
			if(yCollide) {
				//yVel = 0; 
	/*			if (y > yNew){
					y = yNew + collideRadius;
				}
				else{
					y = yNew - collideRadius;
				}*/
			}
			else y = yNew;
		}
		
	}
	

	
}
