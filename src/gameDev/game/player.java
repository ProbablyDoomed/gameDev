package gameDev.game;

import java.awt.Image;

public class player {
	
	public double x,y;
	public double xVel=0 , yVel=0;
	public static final double xVelLimit = 6, yVelLimit = xVelLimit;
	
	public double colideRadius = 10;
	
	public Image sprite;

	public player(int startX, int startY, Image sprite){
		this.x = startX; 
		this.y = startY;
		this.sprite = sprite;
	}
	
	public void tickMovement(){
		
		x += xVel; y += yVel;
		
	}
	
	public void accel( double xRate, double yRate ){
		
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

	public void applyFriction( double xRate, double yRate ) {
		//boolean xDir, yDir;
			xVel *= xRate;
			yVel *= yRate;
	}
	
}
