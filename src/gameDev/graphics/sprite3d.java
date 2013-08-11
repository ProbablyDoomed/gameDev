package gameDev.graphics;

import gameDev.game.Game;

import java.awt.Image;

public class sprite3d {//for wall segments and objects in the game world

	public double screenX;
	public int drawHeight,drawWidth;
	public double dist;
	double angleDiff;
	public boolean isWall,visible;
	
	public Image sprite;
	
	public sprite3d(int screenX, double dist,double cHead, boolean wall, Image sprite){
		//constructor taking screen pixel column and distance to camera/player;
		
		this.screenX = screenX;
		visible = true;
		this.dist = dist;
		this.isWall = wall;
		this.sprite = sprite;
		//this.cameraHeading = cHead;
		
		this.angleDiff = 0 - Game.FOV + (screenX * 2 * Game.FOV/Game.WIDTH);
		
		this.drawHeight = (int)(64*Game.WIDTH/(2*Math.tan(Game.FOV)*(dist * Math.cos(angleDiff))));
		
		if(wall){
			this.drawWidth = 1;
		}
		else{
			this.drawWidth = sprite.getWidth(null)*drawHeight/sprite.getHeight(null);
		}


		
	}
	
	public sprite3d(double xDiff, double yDiff, double cHead, boolean wall, Image sprite){
		//constructor taking x,y location of game object relative to camera/player
		
		this.isWall = wall;
		this.sprite = sprite;
		
		this.dist = Math.sqrt((yDiff*yDiff)+(xDiff*xDiff));
		//this.angleDiff = Math.atan(yDiff/xDiff) - cHead;
		
		if(xDiff == 0){
			if(yDiff >= 0){
				this.angleDiff = Math.PI/2  - cHead;
			}
			else{
				this.angleDiff = -Math.PI/2  - cHead;
			}
		}
		else{
			this.angleDiff = Math.atan(yDiff/xDiff) - cHead;
			if(xDiff < 0) this.angleDiff += (Math.PI);
			if (angleDiff >= Math.PI) angleDiff -= 2*Math.PI;
			if (angleDiff <= -Math.PI) angleDiff += 2*Math.PI;
		}
		
		this.drawHeight = (int)(64*Game.WIDTH/(2*Math.tan(Game.FOV)*(dist * Math.cos(angleDiff))));
		
		this.screenX = (int)((angleDiff+Game.FOV)*Game.WIDTH/(Game.FOV*2));
		
		
		if(wall){
			this.drawWidth = 1;
		}
		else{
			this.drawWidth = sprite.getWidth(null)*drawHeight/sprite.getHeight(null);
		}
	}
	
}
