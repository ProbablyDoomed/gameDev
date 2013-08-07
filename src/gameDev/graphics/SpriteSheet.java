package gameDev.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	public String path;
	public int width;
	public int height;
	public int tileSize;
	public BufferedImage sheetImage; 
	
	public SpriteSheet(String path, int tileSize){
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(SpriteSheet.class.getResource(path));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (image==null){
			return;
		}
		
		this.path = path;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.tileSize = tileSize;
		this.sheetImage = image;
		
	}
	
	public Image getSprite( int xTile, int yTile , boolean flipX, boolean flipY){
		Image sprite = sheetImage.getSubimage(xTile * tileSize, yTile * tileSize, tileSize, tileSize);
		//if (flipX) sprite.scale(-1,-1); 
		return sprite;
	}

}
