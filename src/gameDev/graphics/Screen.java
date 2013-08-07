package gameDev.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Screen {
	
	public static final int MAP_WIDTH = 64;
	public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;
	
	//public int[] pixels;
	
	public int xOffset = 0;
	public int yOffset = 0;
	
	public int width;
	public int height;
	
	public SpriteSheet sheet;
	
	public Screen( int width, int height, SpriteSheet sheet){
		this.width = width;
		this.height = height;
		this.sheet = sheet;
		
		//pixels = new int[width * height];
		
	}
	
	public BufferedImage render(){
		BufferedImage renderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = renderImage.createGraphics();
		
		//g.drawImage(dude.sprite, dude.x, dude.y, 20, 20);
		
		return renderImage;
	}
}
