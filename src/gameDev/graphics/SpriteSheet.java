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
	
	//public int[] pixels;
	
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
		
		/*pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
		for (int i=0; i < pixels.length; i++){
			pixels[i] = (pixels[i] & 0xff)/64;
		}*/
	}
	
	public Image getSprite( int xTile, int yTile ){
		return sheetImage.getSubimage(xTile * tileSize, yTile * tileSize, tileSize, tileSize);
	}

}
