package gameDev.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	public String path;
	public int width,width_t;
	public int height,height_t;
	public int tileSize;
	public BufferedImage[][] sprite; 
	
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
		this.tileSize = tileSize;
		
		this.width = image.getWidth();
		this.width_t = width / tileSize;
		this.height = image.getHeight();
		this.height_t = height/ tileSize;

		//this.sheetImage = image;
		sprite = new BufferedImage[width_t][height_t];
		
		for (int x=0; x < width_t; x++){
			for (int y=0; y < height_t; y++){
				sprite[x][y] = image.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
			}
		}
		
	}


}
