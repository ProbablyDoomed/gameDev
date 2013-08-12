package gameDev.game;

import java.awt.Image;



public class prop {
	
	public Image sprite;
	public double x,y;
	
	public prop(double x, double y, Image sprite){
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}
}
