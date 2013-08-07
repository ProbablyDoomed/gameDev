package gameDev.game;

import java.awt.Image;

public class projectile {
		
	public double x,y;
	public double xVel, yVel;
	public double colideRadius = 10;
	boolean hitscan; //instantly hitting bullet

	public Image sprite;
	
	public projectile(double x , double y, double dir, double speed, Image sprite){
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		
		this.xVel = (double)(speed * Math.cos(dir));
		this.yVel = (double)(speed * Math.sin(dir));
	}
	
	public void tickMovement(){
		
		x += xVel; y += yVel;

	}
	
}
