package gameDev.game;


public class player {
	
	public double x,y;
	public double xVel=0 , yVel=0;
	public static final double xVelLimit = 0.5, yVelLimit = xVelLimit;
	public static final double acceleration = 6;
	public static final double friction = 0.6;
	
	public double heading;
	public static final double turnRate = 0.03;
	
	public double colideRadius = 10;
	
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
	}
	
	public void tickMovement(){
		
		x += xVel; y += yVel;
		
	}
	
	public void accel2d( double xRate, double yRate ){
		
		double xVelNew =  xVel + xRate;
		double yVelNew =  yVel + yRate;
		
		if(xVelNew < -xVelLimit){
			xVel = -xVelLimit;
		}
		else if( xVelNew > xVelLimit){
			xVel = xVelLimit;
		}
		else{
			xVel = xVelNew;
		}
		
		if(yVelNew < -yVelLimit){
			yVel = -yVelLimit;
		}
		else if(yVelNew > yVelLimit){
			yVel = yVelLimit;
		}
		else{
			yVel = yVelNew;
		}
	}
	


	public void applyFriction2d( double xRate, double yRate ) {
		//boolean xDir, yDir;
			xVel *= xRate;
			yVel *= yRate;
	}
	
}
