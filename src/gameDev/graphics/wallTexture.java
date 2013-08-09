package gameDev.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class wallTexture {
	
	private BufferedImage fullTexture;
	public Image segment[] = new Image [64];
	public int texDex;
	
	public wallTexture(String path){
		
		try {
			fullTexture = ImageIO.read(SpriteSheet.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(fullTexture != null){
			for(int i=0; i<64; i++){
				segment[i] = fullTexture.getSubimage(i,0,1,64);
			}
		}
	}
	
	
}
